TRUNCATE TABLE Player, Club, Slot, Invitation, Meet, Player_Belong_Club, Meet_Sheet, Invitation_For_Meet CASCADE;

-- Function that creates a new player
DELIMITER//
CREATE OR REPLACE FUNCTION CreatePlayer(surname VARCHAR(40), first_name VARCHAR(40))
RETURNS INT
BEGIN
    INSERT INTO Player (surname, first_name, scored_goals, conceded_goals, matches_played, victories)
	VALUES (surname, first_name, 0, 0, 0, 0);
    RETURN 1;
END //
DELIMITER;

-- Function that adds scored goals of a player to the table
DELIMITER//
CREATE OR REPLACE FUNCTION PlayerScoredGoals(sn VARCHAR(40), fn VARCHAR(40), scored_goals_game SMALLINT)
RETURNS INT
BEGIN
    UPDATE Player
    SET scored_goals = scored_goals + scored_goals_game
    WHERE surname = sn
    AND first_name = fn;
END //
DELIMITER;

-- Function that updates conceded goals after a game
DELIMITER//
CREATE OR REPLACE FUNCTION PlayerconcededGoals(meet_id SMALLINT, winner_score SMALLINT, loser_score)
RETURNS INT
BEGIN
    UPDATE Player P INNER JOIN Player_Played_Meet M on P.id = M.player
    SET P.conceded_goals = P.conceded_goals + winner_score
    WHERE won = 0;
    UPDATE Player P INNER JOIN Player_Played_Meet M on P.id = M.player
    SET P.conceded_goals = P.conceded_goals + loser_score
    WHERE won = 1;
END //
DELIMITER;

-- Function that creates a club
DELIMITER//
CREATE OR REPLACE FUNCTION CreateClub(club_name VARCHAR(40))
RETURNS INT
BEGIN
    INSERT INTO Club (title, creation_date)
	    VALUES (club_name, DATETIME);
END //
DELIMITER;
