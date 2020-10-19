#include "thread.h"
#include <assert.h>
#include <stdio.h>

/* test du thread_self et yield du main seul.
 *
 * le programme doit retourner correctement.
 * valgrind doit être content.
 *
 * support nécessaire:
 * - thread_yield() depuis et vers le main
 * - thread_self() depuis le main
 */

int main() {
  int err, i;

  for (i = 0; i < 1; i++) {
    fprintf(stdout, "le main yield tout seul\n");
    err = thread_yield();
    assert(!err);
  }

  fprintf(stdout,"le main est %p\n", (void *)thread_self());

  return 0;
}
