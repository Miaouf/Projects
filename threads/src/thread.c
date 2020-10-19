#include "thread.h"
#include <assert.h>
#include <errno.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <time.h>
#include <inttypes.h>
#include <valgrind/valgrind.h>

#define AVERAGE_WAITING_TIME 2e7

/* ***************************************** */
/*                 VARIABLES                 */
/* ***************************************** */

LIST_HEAD(, thread_s) head;
static struct thread_s *active_thread = NULL;

/* Variables pour la gestion du dernier context */

static ucontext_t finale;           // context de fin
static int stack_finale[64 * 1024]; // pile du context de fin
int finale_valgrind_stackid;

/* Variables de préemption */

timer_t timer;
sigset_t block;

/* ***************************************** */
/*               FIN VARIABLES               */
/* ***************************************** */

/* ***************************************** */
/*                 MUTEX                 */
/* ***************************************** */

/* Initialise d'un mutex */
int thread_mutex_init(thread_mutex_t *mutex) {
  mutex->lock_isinit = 1;
  mutex->lock_istaken = 0;
  mutex->lock_isdestroyed = 0;
  mutex->lock_owner = NULL;
  SIMPLEQ_INIT(&mutex->lock_pendings);
  return SUCCESS;
}

/* Détruit d'un mutex */
int thread_mutex_destroy(thread_mutex_t *mutex) {
  if (!mutex->lock_isinit || mutex->lock_isdestroyed || mutex->lock_istaken)
    return FAILURE;

  /* Libère tous les threads bloqués par le mutex à détruire */
  while(!SIMPLEQ_EMPTY(&mutex->lock_pendings))
    SIMPLEQ_REMOVE_HEAD(&mutex->lock_pendings, next);

  mutex->lock_isdestroyed = 1;
  mutex->lock_owner = NULL;
  return SUCCESS;
}

/* Crée un élément d'une liste de thread */
thread_queue* create_pending_thread(thread_s *active_thread) {
  thread_queue* pt = malloc(sizeof(*pt));
  pt->thread = active_thread;
  return pt;
}

/* Verrouille un mutex pour le thread courant */
int thread_mutex_lock(thread_mutex_t *mutex) {
  /* Protection contre les signaux de préemption */
  sigprocmask(SIG_BLOCK, &block, NULL);

  if (!mutex->lock_isinit || mutex->lock_isdestroyed)
    return FAILURE;

  /* Boucle d'attente semi-active */
  while (mutex->lock_istaken) {
    if (mutex->lock_owner == NULL)
      return FAILURE;

    thread_s *current = active_thread;
    thread_queue* head_pt = SIMPLEQ_FIRST(&mutex->lock_pendings);
    /* Si le thread n'est pas déjà bloqué, alors on le bloque */
    if (head_pt != NULL && head_pt->thread == current) {
      SIMPLEQ_INSERT_HEAD(&mutex->lock_pendings,
        create_pending_thread(active_thread), next);
    }
    active_thread = mutex->lock_owner;
    /* On redonne la main au thread bloquant */
    swapcontext(&current->uc, &active_thread->uc);
  }
  mutex->lock_owner = active_thread;
  mutex->lock_istaken = 1;

  /* Réactivation des signaux de préemption */
  sigprocmask(SIG_UNBLOCK, &block, NULL);
  return SUCCESS;
}

int thread_mutex_unlock(thread_mutex_t *mutex) {
  /* Protection contre les signaux de préemption */
  sigprocmask(SIG_BLOCK, &block, NULL);

  if (!mutex->lock_isinit || mutex->lock_isdestroyed)
    return FAILURE;

  /* On donne la main au premier thread en attente */
  if(!SIMPLEQ_EMPTY(&mutex->lock_pendings)){
    thread_queue* awaken = SIMPLEQ_FIRST(&mutex->lock_pendings);
    thread_s *current = active_thread;
    active_thread = awaken->thread;
    swapcontext(&current->uc, &active_thread->uc);

    /* Le thread n'est plus bloqué */
    SIMPLEQ_REMOVE_HEAD(&mutex->lock_pendings, next);
  }

  mutex->lock_istaken = 0;

  /* Réactivation des signaux de préemption */
  sigprocmask(SIG_UNBLOCK, &block, NULL);
  return SUCCESS;
}

/* ***************************************** */

/* free le thread de fin si le main a été retiré avant + free le timer */

void finale_func() {
  VALGRIND_STACK_DEREGISTER(active_thread->valgrind_stackid);
  free(active_thread->uc.uc_stack.ss_sp);
  free(active_thread);
  VALGRIND_STACK_DEREGISTER(finale_valgrind_stackid);
}

/* allocation statique pour le thread main */
static int main_created = NOT_CREATED;

struct thread_s main_th = {};

#define MAIN_THREAD &main_th

void init_main_thread() {
  if (main_created == NOT_CREATED) {
    LIST_INIT(&head);
    main_created = CREATED;
    main_th.status = NOT_FINISHED;
    getcontext(&main_th.uc);
    main_th.uc.uc_link = NULL;
    LIST_INSERT_HEAD(&head, &main_th, next);
    active_thread = &main_th;
  }
}

/* *********************************** */
/*               TIMER                 */
/* *********************************** */

void start_timer() {
  struct itimerspec timer_spec;
  int res;
  timer_spec.it_interval.tv_sec = 0;
  timer_spec.it_interval.tv_nsec = AVERAGE_WAITING_TIME;
  timer_spec.it_value.tv_sec = 0;
  timer_spec.it_value.tv_nsec = 2 * AVERAGE_WAITING_TIME;
  res = timer_settime(timer, 0, &timer_spec, NULL);
  assert(res != -1);
}

struct thread_s * old = NULL;

void timer_handler() {
  struct thread_s * new = thread_self();
  if (new == old){
    //printf("I am the thread [%ld] and I took my share of time ! \n", (intptr_t)(void*)new->funcarg);
    thread_yield();
  }
  else {
    old = new;
  }
}

void free_timer(){
  timer_delete(timer);
}

/* ************************************** */
/*          CONSTRUCTEUR                  */
/* ************************************** */

void __attribute__((constructor)) init(void) {

  /* initialisation main */

  init_main_thread();

  atexit(&free_timer);
  /* initialisation timer et gestionnaire de signal */

  struct sigaction action;
  int res;

  memset(&action, 0, sizeof(action));
  res = sigemptyset(&action.sa_mask);
  assert(res != -1);

  action.sa_handler = timer_handler;
  action.sa_flags = 0;

  sigemptyset(&block);
  sigaddset(&block, SIGALRM);

  res = sigaction(SIGALRM, &action, NULL);
  assert(res != -1);

  res = timer_create(CLOCK_REALTIME, NULL, &timer);
  assert(res != -1);

  /* initialisation contexte de libération */

  getcontext(&finale);
  finale.uc_stack.ss_size = 64 * 1024;
  finale_valgrind_stackid = VALGRIND_STACK_REGISTER(
      finale.uc_stack.ss_sp, finale.uc_stack.ss_sp + finale.uc_stack.ss_size);
  finale.uc_stack.ss_sp = stack_finale;
  makecontext(&finale, (void (*)(void))finale_func, 1);

  /* Déclenchement du timer que si nous activons la préemption (make check-preemption) */

  #if defined(PREEMPTION)
    start_timer();
  #endif

}

/*****************************************/
/*            IMPLEMENTATION             */
/*****************************************/

void thread_runner(thread_s *thread) {
  sigprocmask(SIG_UNBLOCK, &block, NULL);
  active_thread = thread;
  thread_exit(thread->func(thread->funcarg));
}

extern thread_t thread_self() {
  assert(active_thread != NULL);
  return (thread_t)active_thread;
}

extern int thread_yield() {
  sigprocmask(SIG_BLOCK, &block, NULL);
  if (LIST_EMPTY(&head) || LIST_NEXT(LIST_FIRST(&head), next) == NULL) {
    // pas de threads ou thread seul
    sigprocmask(SIG_UNBLOCK, &block, NULL);
    return 0;
  }
  // on récupère le prochain thread à qui donner la main
  struct thread_s *next_thread = LIST_NEXT(active_thread, next);
  if (next_thread == NULL) {
    // on est en fin de liste
    next_thread = LIST_FIRST(&head);
  }

  // on change le contexte en sauvegardant l'actuel dans celui du thread actif
  thread_s *tmp = active_thread;
  active_thread = next_thread;
  swapcontext(&tmp->uc, &next_thread->uc);
  sigprocmask(SIG_UNBLOCK, &block, NULL);
  return 0;
}

extern int thread_create(thread_t *newthread, void *(*func)(void *),
                         void *funcarg) {

  sigprocmask(SIG_BLOCK, &block, NULL);
  assert(!LIST_EMPTY(&head));
  struct thread_s *new_th = malloc(sizeof(struct thread_s));
  new_th->status = NOT_FINISHED;

  getcontext(&new_th->uc);
  new_th->uc.uc_stack.ss_size = 64 * 1024;
  new_th->uc.uc_stack.ss_sp = malloc(new_th->uc.uc_stack.ss_size);
  int valgrind_stackid = VALGRIND_STACK_REGISTER(
      new_th->uc.uc_stack.ss_sp,
      new_th->uc.uc_stack.ss_sp + new_th->uc.uc_stack.ss_size);
  new_th->valgrind_stackid = valgrind_stackid;
  new_th->uc.uc_link = NULL;
  new_th->retval = NULL;
  new_th->func = func;
  new_th->funcarg = funcarg;
  makecontext(&new_th->uc, (void *)thread_runner, 1, new_th, NULL);

  LIST_INSERT_HEAD(&head, new_th, next);

  *newthread = new_th;
  thread_yield(); // à commenter si besoin
  sigprocmask(SIG_UNBLOCK, &block, NULL);
  return 0;
}

extern int thread_join(thread_t thread, void **retval) {
  sigprocmask(SIG_BLOCK, &block, NULL);
  thread_s *elm = (thread_s *)thread;
  thread_s *current = active_thread;
  /* on donne la main au thread jusqu'à ce qu'il termine */
  while (elm->status == NOT_FINISHED) {
    active_thread = elm;
    swapcontext(&current->uc, &elm->uc);
  }
  active_thread = current;
  if (retval != NULL) {
    *retval = elm->retval;
  }

  if (elm != MAIN_THREAD) {
    VALGRIND_STACK_DEREGISTER(elm->valgrind_stackid);
    free(elm->uc.uc_stack.ss_sp);
    free(thread);
  }
  sigprocmask(SIG_UNBLOCK, &block, NULL);
  return 0;
}

extern void thread_exit(void *retval) {
  sigprocmask(SIG_BLOCK, &block, NULL);
  active_thread->retval = retval;
  active_thread->status = FINISHED;
  // récupération du thread suivant
  thread_s *next_thread = LIST_NEXT(active_thread, next);
  if (next_thread == NULL) {
    next_thread = LIST_FIRST(&head);
  }
  LIST_REMOVE(active_thread, next);
  if (next_thread == active_thread) {
    // on exit le dernier thread, donc il faut s'arrêter
    // et ne pas oublier de free le thread actif !
    setcontext(&finale);
  } else {
    active_thread = next_thread;
    setcontext(&next_thread->uc);
  }
} //__attribute__ ((__noreturn__)){};

/* ***************************************** */
/*               FIN IMPLEMENTATION          */
/* ***************************************** */
