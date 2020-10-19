#define _GNU_SOURCE
#include "thread.h"
#include <assert.h>
#include <sched.h>
#include <semaphore.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/syscall.h>
#include <sys/time.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>
#include <valgrind/valgrind.h>
#define gettid() syscall(SYS_gettid)

/*   structure thread_s   */
typedef struct thread_s {
  int status;
  ucontext_t uc;
  void *retval;
  int valgrind_stackid;
  void *(*func)(void *);
  void *funcarg;
  LIST_ENTRY(thread_s) next;
} thread_s;
/* ***************************************** */
#define STACK_SIZE (1024 * 1024)

LIST_HEAD(, thread_s) head;

static ucontext_t finale;            // context de fin
static int stack_finale[STACK_SIZE]; // pile du context de fin
int finale_valgrind_stackid;

/* **** Mutlicore section *********************/
#define CLONE_NUMBER 4
#define READY 0
#define FINISHED 1
#define RUNNING 2
#define LOCKED_BY_YIELD 3
#define LOCKED_BY_JOIN 4
#define LOCKED_BY_JOIN_ELSE 5
#define LOCKED_BY_EXIT 6
#define LOCKED_BY_MAIN 7

struct clone_id {
  int tid;
  int id;
  int change;
};

struct clone_id global_id[CLONE_NUMBER];
sem_t sem_list;
sem_t sem_creation;

static struct thread_s *active_thread[CLONE_NUMBER] = {NULL, NULL, NULL, NULL};
static struct thread_s *before_thread[CLONE_NUMBER] = {NULL, NULL, NULL, NULL};
static ucontext_t clone_uc[CLONE_NUMBER];

char stackuc1[STACK_SIZE];
char stackuc2[STACK_SIZE];
char stackuc3[STACK_SIZE];
char stackuc4[STACK_SIZE];

static char *clone_uc_stack[CLONE_NUMBER] = {stackuc1, stackuc2, stackuc3,
                                             stackuc4};

/* stacks allocation */
char stack1[STACK_SIZE];
char stack2[STACK_SIZE];
char stack3[STACK_SIZE];
static char *stack[CLONE_NUMBER] = {NULL, stack1, stack2, stack3};

/* Retourne l'id d'un clone */
static int clone_self() {
  int i;
  int tid = (int)gettid();
  for (i = 0; i < CLONE_NUMBER; i++) {
    if (global_id[i].tid == tid) {
      return global_id[i].id;
    }
  }
  /* On ne devrait pas arriver dans ce cas */
  fprintf(stderr, "global clone id error for tid %d\n", tid);
  assert(i != CLONE_NUMBER);
  return -1;
}

/* Indique aux threads noyau si un changement a été effectué sur la file */
static void queue_change() {
  for (int i = 0; i < CLONE_NUMBER; i++) {
    global_id[i].change = 1;
  }
}

/* On regarde si on avait lock un thread auparavant, si oui on
 * l'unlock */
static void check_before_thread() {
  int clone_id = clone_self();
  if (before_thread[clone_id] != NULL) {
    assert(before_thread[clone_id]->status == LOCKED_BY_JOIN);
    before_thread[clone_id]->status = READY;
  }
}
/* ************************************************************* */

/* Permet de free le thread de fin si le main a été retiré avant */
void finale_func(int useless) {
  int clone_id = clone_self();
  assert(active_thread[clone_id] != NULL);
  VALGRIND_STACK_DEREGISTER(active_thread[clone_id]->valgrind_stackid);
  free(active_thread[clone_id]->uc.uc_stack.ss_sp);
  free(active_thread[clone_id]);
  VALGRIND_STACK_DEREGISTER(finale_valgrind_stackid);
}

/* allocation statique pour le thread main */
static int main_created = NOT_CREATED;

struct thread_s main_th = {};

#define MAIN_THREAD &main_th

/* Start function for cloned child */
static int cloneFunc(void *arg) {

  /* On attend que le thread noyau initial termine de lancer les clones */
  if (arg != NULL) {
    assert(sem_wait(&sem_creation) == 0);
  }
  /* Une fois la création terminée, on peut s'éxcuter */
  int clone_id = clone_self();

  getcontext(&clone_uc[clone_id]);
  // tant qu'il n'y a pas de thread à executer on attend

  while (LIST_EMPTY(&head)) {
    ;
  }

  /* On va cherche un thread user à faire progresser */
  struct thread_s *next_thread = LIST_FIRST(&head);
  struct thread_s *thread_after;
  while (next_thread != NULL) {
    /* On commence à attendre la semaphore */
    assert(sem_wait(&sem_list) == 0);

    /* On teste si la file a été modifiée entre temps pour repartir au début */
    if (global_id[clone_id].change) {
      global_id[clone_id].change = 0;
      next_thread = LIST_FIRST(&head);
      assert(sem_post(&sem_list) == 0);

      continue;
    }

    assert(active_thread[clone_id] == NULL);
    assert(next_thread != NULL);

    /* on regarde si le thread utilisateur next_thread est disponible */
    if (next_thread->status == READY) {
      active_thread[clone_id] = next_thread;

      active_thread[clone_id]->status = RUNNING;

      /* on récupère son suivant pour notre retour*/
      thread_after = LIST_NEXT(next_thread, next);
      assert(sem_post(&sem_list) == 0);

      swapcontext(&clone_uc[clone_id], &next_thread->uc);

      /* On a terminé d'avancer ce thread */
      active_thread[clone_id] = NULL;

      /* On regarde si on avait lock un thread user auparavant qui peut
       * enchainer sur d'autres  */
      while (before_thread[clone_id] != NULL) {
        assert((before_thread[clone_id]->status == LOCKED_BY_JOIN) ||
               (before_thread[clone_id]->status == LOCKED_BY_JOIN_ELSE) ||
               (before_thread[clone_id]->status == LOCKED_BY_EXIT) ||
               (before_thread[clone_id]->status == LOCKED_BY_YIELD));
        /* Trois cas de blocage */
        /* Si bloqué par yield ou le else du join on le passe en READY
         */
        if ((before_thread[clone_id]->status == LOCKED_BY_YIELD) ||
            (before_thread[clone_id]->status == LOCKED_BY_JOIN_ELSE)) {
          before_thread[clone_id]->status = READY;
          before_thread[clone_id] = NULL;
        } else if (before_thread[clone_id]->status == LOCKED_BY_EXIT) {
          /* Si bloqué par exit on le place en status FINISHED */
          before_thread[clone_id]->status = FINISHED;
          before_thread[clone_id] = NULL;
        } else {
          /* Sinon on l'exécute */
          before_thread[clone_id]->status = RUNNING;
          active_thread[clone_id] = before_thread[clone_id];
          before_thread[clone_id] = NULL;
          swapcontext(&clone_uc[clone_id], &active_thread[clone_id]->uc);
        }
        active_thread[clone_id] = NULL;
      }

      /* On reprend notre recherche */
      next_thread = thread_after;
      /* Si son suivant était NULL alors on repart au début */
      if (next_thread == NULL) {
        next_thread = LIST_FIRST(&head);
      }
      continue;
    } else {
      next_thread = LIST_NEXT(next_thread, next);
      /* On repart au début si le suivant est NULL */
      if (next_thread == NULL) {
        next_thread = LIST_FIRST(&head);
        /* Si la liste ne contient qu'au maximum un élément on break*/
        if (LIST_EMPTY(&head)) {
          assert(sem_post(&sem_list) == 0);
          break;
        }
      }
      assert(sem_post(&sem_list) == 0);
    }
  }
  // en sortie de while pour ne pas terminer le clone
  cloneFunc(NULL);
  return 0;
}

static void mainClone(void *arg) {
  /* Pour le thread noyau MAIN à son premier lancement */
  if (active_thread[0]->status == LOCKED_BY_MAIN) {
    assert(sem_wait(&sem_list) == 0);
    active_thread[0]->status = READY;
    assert(sem_post(&sem_list) == 0);
    active_thread[0] = NULL;
  }
  cloneFunc(NULL);
}
/* Fonction d'initialisation du thread user main */
void init_main_thread() {
  if (main_created == NOT_CREATED) {
    LIST_INIT(&head);
    main_created = CREATED;
    main_th.status = RUNNING;
    getcontext(&main_th.uc);
    main_th.uc.uc_link = NULL;
    assert(sem_wait(&sem_list) == 0);
    LIST_INSERT_HEAD(&head, MAIN_THREAD, next);
    assert(sem_post(&sem_list) == 0);

    getcontext(&finale);
    finale.uc_stack.ss_size = STACK_SIZE;
    finale_valgrind_stackid = VALGRIND_STACK_REGISTER(
        finale.uc_stack.ss_sp, finale.uc_stack.ss_sp + finale.uc_stack.ss_size);
    finale.uc_stack.ss_sp = stack_finale;
    makecontext(&finale, (void (*)(void))finale_func, 1);

    getcontext(&clone_uc[0]);
    makecontext(&clone_uc[0], (void (*)(void))mainClone, 0);
  }
}

/* ************************************** */

/**** initialisation *****/
void __attribute__((constructor)) init(void) {
  global_id[0].tid = gettid();
  global_id[0].id = 0;
  global_id[0].change = 0;
  // lancement des clones
  sem_init(&sem_list, 0, 1);
  sem_init(&sem_creation, 0, 0);

  /* Distribution de stack pour les contextes */
  for (int i = 0; i < CLONE_NUMBER; i++) {
    clone_uc[i].uc_stack.ss_size = STACK_SIZE;
    clone_uc[i].uc_stack.ss_sp = clone_uc_stack[i];
    VALGRIND_STACK_REGISTER(clone_uc[i].uc_stack.ss_sp,
                            clone_uc[i].uc_stack.ss_sp +
                                clone_uc[i].uc_stack.ss_size);
  }

  const int clone_flags = (CLONE_VM | CLONE_SIGHAND | CLONE_THREAD | SIGCHLD |
                           CLONE_SYSVSEM | CLONE_FILES | CLONE_FS | 0);
  /* Lancement des clones */
  int tid;
  for (int i = 1; i < CLONE_NUMBER; i++) {
    tid = clone(cloneFunc, stack[i] + STACK_SIZE, clone_flags, (void *)1);
    assert(tid != -1);
    global_id[i].tid = tid;
    global_id[i].id = i;
    global_id[i].change = 0;
  }

  /* Déblocage des clones */
  for (int i = 1; i < CLONE_NUMBER; i++)
    assert(sem_post(&sem_creation) == 0);

  init_main_thread();
  /* Gestion du thread noyau MAIN d'id 0 */
  active_thread[0] = MAIN_THREAD;
  active_thread[0]->status = LOCKED_BY_MAIN;
  swapcontext(&active_thread[0]->uc, &clone_uc[0]);
}
/*****************************************/
/* Fonction initiale de tous les threads user */
void thread_runner(thread_s *thread) {
  active_thread[clone_self()] = thread;
  thread_exit(thread->func(thread->funcarg));
}

extern thread_t thread_self() {
  int clone_id = clone_self();
  assert(active_thread[clone_id] != NULL);
  return (thread_t)active_thread[clone_id];
}

extern int thread_yield() {
  /* S'il n'y a pas + d'un thread user dans la file, on retourne */
  if (LIST_EMPTY(&head) || LIST_NEXT(LIST_FIRST(&head), next) == NULL) {
    return 0;
  }

  int clone_id = clone_self();

  assert(active_thread[clone_id] != NULL);
  assert(active_thread[clone_id]->status == RUNNING);

  /* le thread noyau main ne peut changer de contexte */
  assert(sem_wait(&sem_list) == 0);
  check_before_thread();
  active_thread[clone_id]->status = LOCKED_BY_YIELD;
  before_thread[clone_id] = active_thread[clone_id];
  assert(sem_post(&sem_list) == 0);

  swapcontext(&active_thread[clone_id]->uc, &clone_uc[clone_id]);
  return 0;
}

extern int thread_create(thread_t *newthread, void *(*func)(void *),
                         void *funcarg) {
  assert(!LIST_EMPTY(&head));

  struct thread_s *new_th = malloc(sizeof(struct thread_s));
  assert(new_th != NULL);
  new_th->status = READY;

  getcontext(&new_th->uc);
  new_th->uc.uc_stack.ss_size = 64 * 1024;
  new_th->uc.uc_stack.ss_sp = malloc(new_th->uc.uc_stack.ss_size);
  assert(new_th->uc.uc_stack.ss_sp != NULL);
  int valgrind_stackid = VALGRIND_STACK_REGISTER(
      new_th->uc.uc_stack.ss_sp,
      new_th->uc.uc_stack.ss_sp + new_th->uc.uc_stack.ss_size);
  new_th->valgrind_stackid = valgrind_stackid;
  new_th->uc.uc_link = NULL;
  new_th->retval = NULL;
  new_th->func = func;
  new_th->funcarg = funcarg;
  makecontext(&new_th->uc, (void *)thread_runner, 1, new_th, NULL);

  assert(sem_wait(&sem_list) == 0);
  LIST_INSERT_HEAD(&head, new_th, next);
  /* On averti les clones d'un changement dans la file */
  queue_change();
  assert(sem_post(&sem_list) == 0);

  *newthread = new_th;
  // thread_yield(); // à commenter si besoin
  return 0;
}

extern int thread_join(thread_t thread, void **retval) {
  int clone_id = clone_self();

  thread_s *current = active_thread[clone_id];
  assert(current != NULL);
  assert(current->status == RUNNING);

  thread_s *joined = (thread_s *)thread;

  /* on attend la terminaison du thread joined */
  while (joined->status != FINISHED) {

    assert(sem_wait(&sem_list) == 0);

    /* Si on peut on fait progresser le thread joined */
    if (joined->status == READY) {
      check_before_thread();

      active_thread[clone_id] = joined;
      before_thread[clone_id] = current;
      current->status = LOCKED_BY_JOIN;
      joined->status = RUNNING;
      assert(sem_post(&sem_list) == 0);

      swapcontext(&current->uc, &joined->uc);
      /* On set la bonne valeur du clone_id ! */
      clone_id = clone_self();

      assert(active_thread[clone_id] == current);
      assert(current->status == RUNNING);
    }
    /* On fait progresser un autre thread à la place */
    else {
      current->status = LOCKED_BY_JOIN_ELSE;
      check_before_thread();

      before_thread[clone_id] = current;

      assert(sem_post(&sem_list) == 0);
      swapcontext(&current->uc, &clone_uc[clone_id]);
      /* On set la bonne valeur du clone_id ! */
      clone_id = clone_self();

      assert(active_thread[clone_id] == current);
      assert(current->status == RUNNING);
    }
  }
  assert(joined->status == FINISHED);
  assert(active_thread[clone_id] == current);

  if (retval != NULL) {
    *retval = joined->retval;
  }
  if (joined != MAIN_THREAD) {
    VALGRIND_STACK_DEREGISTER(joined->valgrind_stackid);
    free(joined->uc.uc_stack.ss_sp);
    free(joined);
  }

  return 0;
}

extern void thread_exit(void *retval) {
  int clone_id = clone_self();

  assert(active_thread[clone_id] != NULL);
  assert(active_thread[clone_id]->status == RUNNING);

  assert(sem_wait(&sem_list) == 0);
  active_thread[clone_id]->retval = retval;
  /* Récupération du thread suivant */
  thread_s *next_thread = LIST_NEXT(active_thread[clone_id], next);
  if (next_thread == NULL) {
    next_thread = LIST_FIRST(&head);
  }
  LIST_REMOVE(active_thread[clone_id], next);
  queue_change();
  if (next_thread == active_thread[clone_id]) {
    active_thread[clone_id]->status = FINISHED;
    assert(sem_post(&sem_list) == 0);
    // on exit le dernier thread, donc il faut s'arrêter
    // et ne pas oublier de free le thread actif !
    setcontext(&finale);
  } else {
    check_before_thread();

    active_thread[clone_id]->status = LOCKED_BY_EXIT;
    before_thread[clone_id] = active_thread[clone_id];
    assert(sem_post(&sem_list) == 0);

    setcontext(&clone_uc[clone_id]);
  }
}
