DROP DATABASE IF EXISTS Quick_Match;
CREATE DATABASE Quick_Match WITH ENCODING 'UTF8';
\c quick_match;


CREATE TABLE IF NOT EXISTS Player (
id SMALLSERIAL PRIMARY KEY,
surname VARCHAR(20) NOT NULL,
first_name VARCHAR(20) NOT NULL,
pseudo VARCHAR(20) NOT NULL,
mdp VARCHAR(20) NOT NULL,
private_profil BOOLEAN NOT NULL DEFAULT FALSE,
mail_address VARCHAR(50),
phone_number VARCHAR(10),						/*A voir pour le type selon utilisation*/
scored_goals SMALLINT DEFAULT 0 NOT NULL CHECK (scored_goals >= 0),
conceded_goals SMALLINT DEFAULT 0 NOT NULL CHECK (conceded_goals >= 0),
matches_played SMALLINT DEFAULT 0 NOT NULL CHECK (matches_played >= 0),
victories SMALLINT DEFAULT 0 NOT NULL CHECK (victories >= 0),
UNIQUE(pseudo),
UNIQUE(mail_address),
UNIQUE(phone_number)
);

CREATE TABLE IF NOT EXISTS Club (				/* Table groupe*/
id SMALLSERIAL PRIMARY KEY,
club_name VARCHAR(40) NOT NULL,
creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
private_club BOOLEAN,
nb_match_played SMALLINT DEFAULT 0 NOT NULL CHECK (nb_match_played >= 0)
UNIQUE(club_name)
);

/*Pas de table feuille stat car (1,1)-(1,1)*/

CREATE TABLE IF NOT EXISTS Slot (
id SMALLSERIAL PRIMARY KEY,
start_hour TIME NOT NULL,
end_hour TIME NOT NULL,
repeat_day VARCHAR(9),							/*Champ a preciser*/
UNIQUE(start_hour, end_hour, repeat_day)
);

CREATE TABLE IF NOT EXISTS Invitation (
id SMALLSERIAL PRIMARY KEY,
slot SMALLINT CHECK (slot >= 0),
player SMALLINT CHECK (player >= 0),
meet SMALLINT CHECK (player >= 0),
status BOOLEAN,
FOREIGN KEY (slot)   REFERENCES Slot(id),
FOREIGN KEY (player) REFERENCES Player(id),
FOREIGN KEY (meet) REFERENCES Meet(id),
event_type VARCHAR(20)  						/*type discutable si liste finie d'event*/
);

CREATE TABLE IF NOT EXISTS Invitation_Club (
id SMALLSERIAL PRIMARY KEY,
player SMALLINT CHECK (player >= 0),
club SMALLINT CHECK (club >= 0),
FOREIGN KEY (club)   REFERENCES Club(id),
FOREIGN KEY (player) REFERENCES Player(id),
UNIQUE(player, club)
);

CREATE TABLE IF NOT EXISTS Meet (				/*Nom a revoir peut etre*/
id SMALLSERIAL PRIMARY KEY,
location TEXT,
precise_date TIMESTAMP,
minimal_team_size SMALLINT,
maximal_team_size SMALLINT,
deletion_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Player_Belong_Club (
player SMALLINT CHECK (player >= 0),
club SMALLINT CHECK (club >= 0),
inscription_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
is_admin BOOLEAN,
scored_goals_club SMALLINT DEFAULT 0 NOT NULL CHECK (scored_goals_club >= 0),
conceded_goals_club SMALLINT DEFAULT 0 NOT NULL CHECK (conceded_goals_club >= 0),
matches_played_club SMALLINT DEFAULT 0 NOT NULL CHECK (matches_played_club >= 0),
victories_club SMALLINT DEFAULT 0 NOT NULL CHECK (victories_club >= 0),
FOREIGN KEY (player) REFERENCES Player(id),
FOREIGN KEY (club)   REFERENCES Club(id),
PRIMARY KEY (player, club)
);

CREATE TABLE IF NOT EXISTS Meet_Sheet (
player SMALLINT CHECK (player >= 0),
meet SMALLINT CHECK (meet >= 0),
scored_goals SMALLINT CHECK (scored_goals >= 0) DEFAULT 0 NOT NULL,
conceded_goals SMALLINT CHECK (conceded_goals >= 0) DEFAULT 0 NOT NULL,
won BOOLEAN,
FOREIGN KEY (player) REFERENCES Player(id),
FOREIGN KEY (meet)   REFERENCES Meet(id),
PRIMARY KEY (player, meet)
);

CREATE TABLE IF NOT EXISTS Invitation_For_Meet (
invitation SMALLINT CHECK (invitation >= 0),
meet SMALLINT CHECK (meet >= 0),
FOREIGN KEY (invitation) REFERENCES Invitation(id),
FOREIGN KEY (meet)       REFERENCES Meet(id),
PRIMARY KEY (invitation, meet)
);
