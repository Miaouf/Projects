#ifndef __THREAD_H__
#define __THREAD_H__

#ifndef USE_PTHREAD

#include <sys/queue.h>
#include <ucontext.h>

#define NOT_FINISHED 0
#define FINISHED 1
#define NOT_BLOCKED 0
#define BLOCKED 1
#define NOT_CREATED 0
#define CREATED 1
#define SUCCESS 0
#define FAILURE 1

typedef void *thread_t;
/* Structure d'un thread */
typedef struct thread_s {
  /* État du thread */
  int status;
  /* Context associé au thread */
  ucontext_t uc;
  /* Valeur de retour */
  void *retval;
  /* Identifiant de la pile pour valgrind */
  int valgrind_stackid;
  /* Fonction du thread */
  void *(*func)(void *);
  /* Argument de la fonction */
  void *funcarg;
  /* Thread suivant dans la liste */
  LIST_ENTRY(thread_s) next;
} thread_s;

/* recuperer l'identifiant du thread courant.
 */
extern thread_t thread_self(void);

/* creer un nouveau thread qui va exécuter la fonction func avec l'argument
 * funcarg. renvoie 0 en cas de succès, -1 en cas d'erreur.
 */
extern int thread_create(thread_t *newthread, void *(*func)(void *),
                         void *funcarg);

/* passer la main à un autre thread.
 */
extern int thread_yield(void);

/* attendre la fin d'exécution d'un thread.
 * la valeur renvoyée par le thread est placée dans *retval.
 * si retval est NULL, la valeur de retour est ignorée.
 */
extern int thread_join(thread_t th, void **retval);

/* terminer le thread courant en renvoyant la valeur de retour retval.
 * cette fonction ne retourne jamais.
 *
 * L'attribut noreturn aide le compilateur à optimiser le code de
 * l'application (élimination de code mort). Attention à ne pas mettre
 * cet attribut dans votre interface tant que votre thread_exit()
 * n'est pas correctement implémenté (il ne doit jamais retourner).
 */
extern void thread_exit(void *retval); // __attribute__ ((__noreturn__));

/* Interface possible pour les mutex */
/* File de threads */
typedef struct thread_queue {
  thread_s* thread;
  SIMPLEQ_ENTRY(thread_queue) next;
} thread_queue;

/* Structure d'un mutex */
typedef struct thread_mutex {
  /* Le verrou est-il initialisé ? */
  unsigned short lock_isinit;
  /* Le verrou est-il détruit ? */
  unsigned short lock_isdestroyed;
  /* Le verrou est-il pris ? */
  unsigned short lock_istaken;
  /* A qui apprtient le verrou ? */
  thread_s* lock_owner;
  /* File de threads en attente */
  SIMPLEQ_HEAD(head_pt, thread_queue) lock_pendings;
} thread_mutex_t;

int thread_mutex_init(thread_mutex_t *mutex);
int thread_mutex_destroy(thread_mutex_t *mutex);
int thread_mutex_lock(thread_mutex_t *mutex);
int thread_mutex_unlock(thread_mutex_t *mutex);

#else /* USE_PTHREAD */

/* Si on compile avec -DUSE_PTHREAD, ce sont les pthreads qui sont utilisés */
#include <pthread.h>
#include <sched.h>
#define thread_t pthread_t
#define thread_self pthread_self
#define thread_create(th, func, arg) pthread_create(th, NULL, func, arg)
#define thread_yield sched_yield
#define thread_join pthread_join
#define thread_exit pthread_exit

/* Interface possible pour les mutex */
#define thread_mutex_t pthread_mutex_t
#define thread_mutex_init(_mutex) pthread_mutex_init(_mutex, NULL)
#define thread_mutex_destroy pthread_mutex_destroy
#define thread_mutex_lock pthread_mutex_lock
#define thread_mutex_unlock pthread_mutex_unlock

#endif /* USE_PTHREAD */

#endif /* __THREAD_H__ */
