Projet de GO réalisé par Tanguy PEMEJA et Maxime ROSAY
--------------------------------------------------------------------------------

=============================== Heuristique ====================================
Réseau de neurones qui évalue, suivant le plateau, le pourcentage de victoire du
joueur noir (cf TP).
L'avantage d'une telle heuristique est que nous n'avons pas à connaître les
règles de jeu.
Il nous suffit d'avoir uniquement des données qui respectent les règles afin
d'évaluer le plateau de jeu.
De plus, nous utilisons les 8 symétries du plateau afin d'améliorer notre
prédiction.

=================================== Joueur =====================================
Composition:
  - myPlayer.py
  - myplayer_model.h5
------------------------
Points forts:
  - joue localement en fonction de son dernier coup et du dernier coup adverse
  - joue en fonction du temps (il cherche à ne pas dépasser 300 secondes de
    réflexion)
  - joue relativement rapidement chaque coup grâce à une profondeur maximale de
    2 et un réseau limité en terme de taille (2,798,673 paramètres)
  - premier coup scripté (observation de GnuGo)
  - joue PASS si l'ennemi l'a fait et qu'on a un meilleur score
  - joue PASS lorsqu'il ne reste que deux cases vides et que son prochain coup
    aurait laissé une seule case vide

Points faibles:
  - profondeur de recherche faible
  - prédictions potentiellement fausses


========================== Essais non-concluants ===============================

- création de deux models associés respectivement au joueur noir et joueur blanc
- création d'un model renvoyant le coup précis à jouer
- création d'un player utilisant un algorithme MINMAX avec un élagage en largeur
- génération de nouvelles données JSON en utilisant GnuGo
