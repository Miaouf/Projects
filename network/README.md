# Projet de Réseau - RE203
Application d’échange de Fichiers en Pair à Pair

Ce projet comporte deux parties : le tracker et le peer, chacun dans le dossier respectif dans "src".

## Tracker

Pour compiler le tracker Il suffit d'exécuter `make` Dans le dossier tracker.

Pour exécuter le tracker: `./tracker -v -p 8090`

Où:

-v active les logs du tracker

-p renseigne la porte où le tracker execute


## Peer

Pour compiler le tracker Il suffit d'exécuter `make` Dans le dossier peer.

Ensuite, toute la configuration pour faire fonctionner le peer est dans le fichier `config.ini`. La configuration par défaut prévoit que le tracker fonctionne sur `127.0.0.1:8090`.

Pour tester, vous pouvez lancer `make run1` ou `make run2` pour lancer deux paires différentes dans ces ports, avec des fichiers et des ips différents. Vous pouvez ainsi interagir directement avec le shell des paires.
