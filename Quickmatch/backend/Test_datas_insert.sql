TRUNCATE TABLE Player, Club, Slot, Invitation, Meet, Player_Belong_Club, Meet_Sheet, Invitation_For_Meet CASCADE;

INSERT INTO Player (surname, first_name, pseudo, mdp, mail_address, phone_number, scored_goals, conceded_goals, matches_played, victories)
	VALUES ('Chataigner', 'Johan', 'cj', 'mdpcj', 'Chataigner@gmail.com', '0600000000', 1, 0, 1, 1),
				 ('Genty', 'Laurent', 'gl', 'mdpgl', 'Genty@gmail.com', '0600000001', 2, 1, 1, 0),
         ('Issaoui', 'Ali', 'ia', 'mdpia', 'Issaoui@gmail.com', '0600000002', 2, 2, 1, 0),
         ('Abderrahmane', 'Faiz', 'af', 'mdpaf', 'Abderrahmane@gmail.com', '0600000003', 0, 0, 1, 1),
         ('Rosay', 'Maxime', 'rm', 'mdprm', 'Rosay@gmail.com', '0600000004', 2, 2, 1, 0),
         ('Hala', 'Mehdi', 'hm', 'mdphm', 'Hala@gmail.com', '0600000005', 2, 1, 1, 0),
         ('Faverge', 'Mathieu', 'fm', 'mdpfm', 'Faverge@gmail.com', '0600000006', 100, 0, 1, 1),
         ('Delarue', 'Tony', 'dt', 'mdpdt', 'Delarue@gmail.com', '0600000007', 101, 0, 1, 1),
         ('Meline', 'Raphael', 'mr', 'mdpmr', 'Meline@gmail.com', '0600000008', 1, 2, 1, 0),
         ('Danquigny', 'Antoine', 'da', 'mdpda', 'Danquigny@gmail.com', '0600000009', 1000, 0, 1, 1);

INSERT INTO Club (club_name, creation_date, private_club)
	VALUES ('Inria', '2019-11-16 17:00:00', FALSE),
				 ('Enseirb', '2019-11-16 17:00:00', FALSE),
         ('Gars', '2019-11-16 17:00:00', FALSE),
         ('Filles', '2019-11-16 17:00:00', FALSE);

INSERT INTO Slot (start_hour, end_hour, repeat_day)
	VALUES ('14:00:00', '16:00:00', 'Monday'),
				 ('14:30:00', '16:30:00', 'Wednesday'),
         ('13:00:00', '15:00:00', 'Saturday'),
         ('15:00:00', '17:00:00', 'Sunday');

INSERT INTO Invitation (slot, player, event_type)
	VALUES (1, 1, 'Match'),
				 (3, 7, 'Tournoi'),
         (1, 2, 'Match'),
         (1, 3, 'Match'),
         (3, 8, 'Tournoi');

INSERT INTO Meet (location, precise_date, minimal_team_size, maximal_team_size, deletion_date)
	VALUES ('François Bordes', '2019-12-10 14:00:00', 2, 22, NULL),
				 ('Cosec', '2019-12-10 14:00:00', 2, 22, NULL),
         ('François Bordes', '2019-12-11 14:30:00', 2, 22, NULL),
         ('Cosec', '2019-12-11 14:30:00', 2, 22, NULL),
         ('François Bordes', '2019-12-14 13:00:00', 2, 22, NULL);

INSERT INTO Player_Belong_Club (player, club, is_admin)
	VALUES (1, 2, TRUE),
				 (2, 2, FALSE),
         (3, 2, FALSE),
         (4, 2, FALSE),
         (5, 2, FALSE),
         (6, 2, FALSE),
         (7, 1, TRUE),
         (8, 1, FALSE),
         (9, 2, FALSE),
         (10, 2, FALSE);

INSERT INTO Meet_Sheet (player, meet, won)
	VALUES (1, 1, TRUE),
				 (2, 1, FALSE),
         (3, 2, FALSE),
         (4, 2, TRUE),
         (5, 2, FALSE),
         (6, 3, FALSE),
         (7, 3, TRUE),
         (8, 4, TRUE),
         (9, 4, FALSE),
         (10, 1, TRUE);

INSERT INTO Invitation_For_Meet (invitation, meet)
	VALUES (1, 2),
				 (2, 1),
         (3, 5),
         (4, 4),
         (5, 3);
