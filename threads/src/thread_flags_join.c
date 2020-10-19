#include "thread.h"
#include <stdio.h>
#include <stdlib.h>
#include <valgrind/valgrind.h>
#define NOT_BLOCKED 0
#define BLOCKED 1

/*   structure thread_s   */

typedef struct thread_s {
  int status;
  int yield_status;
  ucontext_t uc;
  void *retval;
  int valgrind_stackid;
  void *(*func)(void *);
  void *funcarg;
  struct thread_s *thread_joined;
  SIMPLEQ_ENTRY(thread_s) next;
} thread_s;

/* ***************************************** */
SIMPLEQ_HEAD(, thread_s) head;

struct thread_s *active_thread = NULL;

static ucontext_t finale;           // context de fin
static int stack_finale[64 * 1024]; // pile du context de fin
int finale_valgrind_stackid;

/* permet de free le thread de fin si le main a été retiré avant */
void finale_func(int useless) {
  VALGRIND_STACK_DEREGISTER(active_thread->valgrind_stackid);
  free(active_thread->uc.uc_stack.ss_sp);
  free(active_thread);
  VALGRIND_STACK_DEREGISTER(finale_valgrind_stackid);
}

/* allocation statique pour le thread main */
int main_created = NOT_CREATED;

struct thread_s main_th = {};

#define MAIN_THREAD &main_th

void init_main_thread() {
  // fprintf(stderr, "[lib] init\n");
  if (main_created == NOT_CREATED) {
    SIMPLEQ_INIT(&head);
    main_created = CREATED;
    main_th.status = NOT_FINISHED;
    main_th.yield_status = NOT_BLOCKED;
    getcontext(&main_th.uc);
    main_th.uc.uc_link = NULL;
    main_th.thread_joined = NULL;
    SIMPLEQ_INSERT_HEAD(&head, &main_th, next);
    active_thread = &main_th;

    getcontext(&finale);
    finale.uc_stack.ss_size = 64 * 1024;
    finale_valgrind_stackid = VALGRIND_STACK_REGISTER(
        finale.uc_stack.ss_sp, finale.uc_stack.ss_sp + finale.uc_stack.ss_size);
    finale.uc_stack.ss_sp = stack_finale;
    makecontext(&finale, (void (*)(void))finale_func, 1);
  }
}

/* ************************************** */

void thread_runner(thread_s *thread) {
  active_thread = thread;
  thread_exit(thread->func(thread->funcarg));
}

extern thread_t thread_self() {
  // fprintf(stderr, "[lib] self\n");
  if (SIMPLEQ_EMPTY(&head)) {
    init_main_thread();
  }
  return (thread_t)active_thread;
}

extern int thread_yield() {
  // fprintf(stderr, "[lib] yield\n");
  if (SIMPLEQ_EMPTY(&head) ||
      SIMPLEQ_NEXT(SIMPLEQ_FIRST(&head), next) == NULL) {
    // pas de threads ou thread seul
    return 0;
  }
  // on récupère le prochain thread à qui donner la main
  struct thread_s *next_thread = SIMPLEQ_NEXT(active_thread, next);
  if (next_thread == NULL) {
    next_thread = SIMPLEQ_FIRST(&head);
  }
  while (next_thread->yield_status == BLOCKED) {
    next_thread = SIMPLEQ_NEXT(next_thread, next);
    if (next_thread == NULL) {
      // on est en fin de liste
      next_thread = SIMPLEQ_FIRST(&head);
    }
  }
  // on change le contexte en sauvegardant l'actuel dans celui du thread actif
  thread_s *tmp = active_thread;
  active_thread = next_thread;
  // fprintf(stderr, "[lib] thread %i\n", next_thread->id);
  swapcontext(&tmp->uc, &next_thread->uc);
  // fprintf(stderr, "[lib] fin yield\n");
  return 0;
}

extern int thread_create(thread_t *newthread, void *(*func)(void *),
                         void *funcarg) {
  // fprintf(stderr, "[lib] create\n");
  /* Si la SIMPLEQ est vide, l'initialise */
  if (SIMPLEQ_EMPTY(&head)) {
    init_main_thread();
  }
  struct thread_s *new_th = malloc(sizeof(struct thread_s));
  new_th->status = NOT_FINISHED;
  new_th->yield_status = NOT_BLOCKED;
  new_th->thread_joined = NULL;

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

  // On insere new_th dans la SIMPLEQ en fin de liste
  SIMPLEQ_INSERT_TAIL(&head, new_th, next);

  *newthread = new_th;
  // fprintf(stderr, "[lib] fin create\n");
  thread_yield(); // à commenter si besoin
  return 0;
}

extern int thread_join(thread_t thread, void **retval) {
  thread_s *elm = (thread_s *)thread;
  thread_s *current = active_thread;
  // fprintf(stderr, "[lib] join : %i\n", elm->id);
  active_thread->yield_status = BLOCKED;
  elm->thread_joined = active_thread;

  if (elm->yield_status == BLOCKED) {
    thread_yield();
  }
  /* on donne la main au thread jusqu'à ce qu'il termine */
  while (elm->status == NOT_FINISHED) {
    // fprintf(stderr, "[lib] join pas fini\n");
    active_thread = elm;
    swapcontext(&current->uc, &elm->uc);
  }

  active_thread = current;
  active_thread->yield_status = NOT_BLOCKED;
  if (retval != NULL) {
    *retval = elm->retval;
  }

  if (elm != MAIN_THREAD) {
    VALGRIND_STACK_DEREGISTER(elm->valgrind_stackid);
    free(elm->uc.uc_stack.ss_sp);
    free(thread);
  }
  // fprintf(stderr, "[lib] fin join\n");
  return 0;
}

extern void thread_exit(void *retval) {
  // fprintf(stderr, "[lib] exit %i\n", active_thread->id);
  active_thread->retval = retval;
  active_thread->status = FINISHED;
  // récupération du thread suivant
  thread_s *next_thread = active_thread->thread_joined;
  if (next_thread == NULL) {
    next_thread = SIMPLEQ_NEXT(active_thread, next);
    if (next_thread == NULL) {
      // on est en fin de liste
      next_thread = SIMPLEQ_FIRST(&head);
    }
    while (next_thread->yield_status == BLOCKED) {
      if (next_thread == NULL) {
        // on est en fin de liste
        next_thread = SIMPLEQ_FIRST(&head);
      }
      next_thread = SIMPLEQ_NEXT(next_thread, next);
    }
  }
  // fprintf(stderr, "[lib] remove %i\n", active_thread->id);
  // fprintf(stderr, "[lib] next %i\n", next_thread->id);
  SIMPLEQ_REMOVE(&head, active_thread, thread_s, next);
  if (next_thread == active_thread) {
    // on exit le dernier thread, donc il faut s'arrêter
    // et ne pas oublier de free le thread actif !
    // fprintf(stderr, "[lib] fin\n");

    setcontext(&finale);
  } else {
    active_thread = next_thread;
    setcontext(&next_thread->uc);
  }
} //__attribute__ ((__noreturn__)){};
