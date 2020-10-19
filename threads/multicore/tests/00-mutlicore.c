#include "thread.h"
#include <assert.h>
#include <stdio.h>

/* test du join, avec ou sans thread_exit.
 *
 * le programme doit retourner correctement.
 * valgrind doit être content.
 *
 * support nécessaire:
 * - thread_create()
 * - thread_exit()
 * - retour sans thread_exit()
 * - thread_join() avec récupération valeur de retour, avec et sans
 * thread_exit()
 */

static void *thfunc(void *name) {
  while (1) {
    ;
  }
  for (int i = 0; i < 30; i++) {
    fprintf(stdout, "%s\n", (char *)name);
  }
  thread_exit((void *)0xdeadbeef);
  return NULL; /* unreachable, shut up the compiler */
}

int main() {
  thread_t th, th2, th3;
  int err;
  void *res = NULL;

  err = thread_create(&th, thfunc, "\t thread 1");
  assert(!err);
  err = thread_create(&th2, thfunc, "\t \t thread 2");
  assert(!err);
  err = thread_create(&th3, thfunc, "\t \t \t thread 3");
  assert(!err);

  fprintf(stdout, "tous les threads ont été créé\n");

  err = thread_join(th, &res);
  assert(!err);
  assert(res == (void *)0xdeadbeef);

  err = thread_join(th2, &res);
  assert(!err);
  assert(res == (void *)0xdeadbeef);

  err = thread_join(th3, &res);
  assert(!err);
  assert(res == (void *)0xdeadbeef);
  return 0;
}
