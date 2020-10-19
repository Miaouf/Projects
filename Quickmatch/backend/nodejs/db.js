"use strict";

var _require = require("pg"),
  Pool = _require.Pool;

var dotenv = require("dotenv");

dotenv.config();

var pool = new Pool({
  connectionString: process.env.DATABASE_URL
});

pool.on("connect", function() {
  console.log("connected to the db");
});

/**
 * Create Tables
 */
var createTables = function createTables() {
  var queryText =
    "CREATE TABLE IF NOT EXISTS Player (\n    id SMALLSERIAL PRIMARY KEY,\n    surname VARCHAR(20) NOT NULL,\n    first_name VARCHAR(20) NOT NULL,\n    pseudo VARCHAR(20) NOT NULL,\n    mdp VARCHAR(20) NOT NULL,\n    private_profil BOOLEAN NOT NULL DEFAULT FALSE,\n    mail_address VARCHAR(50),\n    phone_number VARCHAR(10),\t\t\t\t\t\t/*A voir pour le type selon utilisation*/\n    scored_goals SMALLINT DEFAULT 0 NOT NULL CHECK (scored_goals >= 0),\n    conceded_goals SMALLINT DEFAULT 0 NOT NULL CHECK (conceded_goals >= 0),\n    matches_played SMALLINT DEFAULT 0 NOT NULL CHECK (matches_played >= 0),\n    victories SMALLINT DEFAULT 0 NOT NULL CHECK (victories >= 0),\n    UNIQUE(pseudo),\n    UNIQUE(mail_address),\n    UNIQUE(phone_number)\n    );\n    \n    CREATE TABLE IF NOT EXISTS Club (\t\t\t\t/* Table groupe*/\n    id SMALLSERIAL PRIMARY KEY,\n    club_name VARCHAR(40) NOT NULL,\n    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    private_club BOOLEAN,\n    UNIQUE(club_name)\n    );\n    \n    /*Pas de table feuille stat car (1,1)-(1,1)*/\n    \n    CREATE TABLE IF NOT EXISTS Slot (\n    id SMALLSERIAL PRIMARY KEY,\n    start_hour TIME NOT NULL,\n    end_hour TIME NOT NULL,\n    repeat_day VARCHAR(9),\t\t\t\t\t\t\t/*Champ a preciser*/\n    UNIQUE(start_hour, end_hour, repeat_day)\n    );\n    CREATE TABLE IF NOT EXISTS Invitation (\n    id SMALLSERIAL PRIMARY KEY,\n    slot SMALLINT CHECK (slot >= 0),\n    player SMALLINT CHECK (player >= 0),\n    FOREIGN KEY (slot)   REFERENCES Slot(id),\n    FOREIGN KEY (player) REFERENCES Player(id),\n    \n    event_type VARCHAR(20)  \t\t\t\t\t\t/*type discutable si liste finie d'event*/\n    );\n   \n   CREATE TABLE IF NOT EXISTS Invitation_Club (\n    id SMALLSERIAL PRIMARY KEY,\n    player SMALLINT CHECK (player >= 0),\n    club SMALLINT CHECK (club >= 0),\n    FOREIGN KEY (club)   REFERENCES Club(id),\n    FOREIGN KEY (player) REFERENCES Player(id),\n    UNIQUE(player, club)\n    );\n    \n    CREATE TABLE IF NOT EXISTS Meet (\t\t\t\t/*Nom a revoir peut etre*/\n    id SMALLSERIAL PRIMARY KEY,\n    location TEXT,\n    precise_date TIMESTAMP,\n    minimal_team_size SMALLINT,\n    maximal_team_size SMALLINT,\n    deletion_date TIMESTAMP\n    );\n    \n    CREATE TABLE IF NOT EXISTS Player_Belong_Club (\n    player SMALLINT CHECK (player >= 0),\n    club SMALLINT CHECK (club >= 0),\n    inscription_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n    is_admin BOOLEAN,\n    scored_goals_club SMALLINT DEFAULT 0 NOT NULL CHECK (scored_goals_club >= 0),\n    conceded_goals_club SMALLINT DEFAULT 0 NOT NULL CHECK (conceded_goals_club >= 0),\n    matches_played_club SMALLINT DEFAULT 0 NOT NULL CHECK (matches_played_club >= 0),\n    victories_club SMALLINT DEFAULT 0 NOT NULL CHECK (victories_club >= 0),\n    FOREIGN KEY (player) REFERENCES Player(id),\n    FOREIGN KEY (club)   REFERENCES Club(id),\n    PRIMARY KEY (player, club)\n    );\n    \n    CREATE TABLE IF NOT EXISTS Meet_Sheet (\n    player SMALLINT CHECK (player >= 0),\n    meet SMALLINT CHECK (meet >= 0),\n    scored_goals SMALLINT CHECK (scored_goals >= 0) DEFAULT 0 NOT NULL,\n    conceded_goals SMALLINT CHECK (conceded_goals >= 0) DEFAULT 0 NOT NULL,\n    won BOOLEAN,\n    FOREIGN KEY (player) REFERENCES Player(id),\n    FOREIGN KEY (meet)   REFERENCES Meet(id),\n    PRIMARY KEY (player, meet)\n    );\n    \n    CREATE TABLE IF NOT EXISTS Invitation_For_Meet (\n    invitation SMALLINT CHECK (invitation >= 0),\n    meet SMALLINT CHECK (meet >= 0),\n    FOREIGN KEY (invitation) REFERENCES Invitation(id),\n    FOREIGN KEY (meet)       REFERENCES Meet(id),\n    PRIMARY KEY (invitation, meet)\n    );\n    ";

  pool
    .query(queryText)
    .then(function(res) {
      console.log(res);
      pool.end();
    })
    .catch(function(err) {
      console.log(err);
      pool.end();
    });
};

/**
 * Drop Tables
 */
var dropTables = function dropTables() {
  var queryText = "DROP TABLE IF EXISTS reflections";
  pool
    .query(queryText)
    .then(function(res) {
      console.log(res);
      pool.end();
    })
    .catch(function(err) {
      console.log(err);
      pool.end();
    });
};

pool.on("remove", function() {
  console.log("client removed");
  process.exit(0);
});

module.exports = {
  createTables: createTables,
  dropTables: dropTables
};

require("make-runnable");
