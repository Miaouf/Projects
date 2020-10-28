# Projet base de données / application web réalisé avec Tanguy PEMEJA et Théo MATRICON en 2019

### Déploiement

  1) Configuration MySQL

    Vous avez besoin d'avoir MySQL qui tourne en local sur le port par défaut.
  Lien : https://dev.mysql.com/downloads/mysql/
  Pour information nous utilisons la version 8.0.1.

  Maintenant, il faut configurer vos accès MySQL dans le backend.
  Pour cela, ouvrez ./backend/src/app.js.
  Et modifiez cet objet selon vos paramètres :
  const connection = mysql.createConnection({
  	host: 'localhost',
  	user: 'root',
  	password: 'password',
  	database: 'canard'
  });

  2) Configuration Node.js

    Vous avez besoin de Node.js qui se trouve sur https://nodejs.org/en/.
  Nous utilisons la version 12.13.1 LTS.
  Vous avez maintenant besoin d'installer les modules nécessaires pour déployer les serveurs.

  Pour cela, cd dans le dossier 'backend' puis lancer la commande 'npm install'.
  Faites de même dans le frontend dans le dossier 'frontend'.

  3) Déploiement

  Vous pouvez maintenant lancer les applications. Néanmoins, il faut avoir au prélalable avoir initialiser la base à laquelle on se connecte. Voir ci-après "Gestion de la base de données".

  Pour lancer le backend, une fois dans le dossier 'backend' vous pouvez lancer 'npm run serve'.

  Pour lancer le frontend une fois dans le dossier 'frontend' vous pouvez lancer 'npm run serve'.

### Gestion de la base de données 

  Nous avons scindés la gestion de la base de données en plusieurs scripts :
    - suppression.sql :
      Supprime tout ce qui est crée par les autres scripts.
    - base.sql :
      Crée les tables nécessaires.
    - remplissage.sql : (nécessite base.sql)
      Remplit la base avec des données, il est à noté que le script datant d'avant la période où nous avons réalisé ces dernières, nous ne garantissons pas que le remplissage s'effectue correctement si les triggers sont crées au préalable.
    - procedures.sql : (nécessite base.sql)
      Crée les procedures utilisées dans la gestion de la base.
    - triggers.sql : (nécessite procedures.sql)
      Crée les triggers permettant de garder la cohérence des données dans la base.

  L'utilisation typique dans un shell mysql est :

  create database canard;       #canard est le nom spécifié au début de la base
  use canard;
  source suppression.sql;
  source base.sql;
  source remplissage.sql;
  source procedures.sql;
  source triggers.sql;
