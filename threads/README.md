**Projet de système d'exploitation du groupe 1-4
(Maxime ROSAY, Julien MIENS, Hind BOUCHERIT, Antoine BOULANGE)**

*Pour compiler le projet :*
  $ make

*Pour nettoyer le projet :*
  $ make clean

*Pour lancer les tests sur notre librairie:*
  $ make check

*Pour tester valgrind sur notre librairie:*
  $ make valgrind

*Pour tester la préemption:*
  $ make check-preemption

*Pour lancer les tests avec pthreads:*
  $ make check-pthreads

*Pour lancer un graph de comparaison entre notre librairie et pthreads:*
  $ make graphs
Pensez à effectuer un make clean après tout make graphs.

Pour changer l'intervalle de test, se rendre dans le Makefile à la racine et
modifier les valeurs suivantes:
*N = 8
N_MAX = 20
NBR_YD = 10
NBR_TH = 20
NBR_TH_MAX = 200*

**VERSION MULTICOEUR**
$ cd ./multicore

Pour tester que cette version utilise bien 4 coeurs CPU:
  $ htop *sur un terminal*
  $ LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD/install/lib ./install/bin/00-multicore
    *sur un autre*

Terminaison de ce test ci-dessus via ^C.

Les commandes ci-dessus sont toutes valables sauf la préemption.

Les tests mutex ne sont pas implémentés dans cette version.

#Nous savons qu'il y a une erreur sur le test Fibonacci lié à malloc.
#Nous savons aussi qu'utiliser *printf* au lieu de *fprintf(stderr,..)* est
#source d'erreur malloc.
