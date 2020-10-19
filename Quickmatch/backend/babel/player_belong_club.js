import moment from "moment";
//import uuidv4 from 'uuid/v4';
import db from "../db";

const player_belong_club = {
  /**
   * Get all the clubs where the player is an admin
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & club object
   */
  async getPlayerAdminClubs(req, res) {
    const text =
      "SELECT * FROM player_belong_club P INNER JOIN club C on P.club = C.id WHERE player = $1 AND is_admin = TRUE";
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get all the club where the $1 player is
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & club object
   */
  async getPlayerClubsByPlayerID(req, res) {
    const text =
      `SELECT * FROM
         (
           SELECT C.id, COUNT(PBC.player) as nb_player FROM
             club C
             JOIN
             player_belong_club PBC
             ON C.id = PBC.club
           GROUP BY C.id
         ) counter_club
       JOIN
         (
        	 SELECT * FROM
             club C
             JOIN
             player_belong_club PBC
             ON C.id = PBC.club
           WHERE PBC.player = $1
         ) player_clubs
       ON counter_club.id = player_clubs.id
       ORDER BY is_admin DESC`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
  * Get all the club where the $1 player is
  * @param {object} req
  * @param {object} res
  * @returns {object} player_belong_club & club object
  */
  async getPlayerClubsByPlayerIDRows(req, res) {
    const text =
    `SELECT * FROM
    (
      SELECT C.id, COUNT(PBC.player) as nb_player FROM
      club C
      JOIN
      player_belong_club PBC
      ON C.id = PBC.club
      GROUP BY C.id
    ) counter_club
    JOIN
    (
      SELECT * FROM
      club C
      JOIN
      player_belong_club PBC
      ON C.id = PBC.club
      WHERE PBC.player = $1
    ) player_clubs
    ON counter_club.id = player_clubs.id
    ORDER BY club_name, creation_date`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get all the club where the $1 player is not and which are public club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & club object
   */
  async NgetPlayerClubsByPlayerID(req, res) {
    const text =
      `SELECT * FROM
          (
            SELECT C.id, COUNT(PBC.player) as nb_player FROM
              club C
              JOIN
              player_belong_club PBC
              ON C.id = PBC.club
            GROUP BY C.id
          ) counter_club
       JOIN
          (
	          SELECT * FROM club C
              WHERE C.id NOT IN
              (
                SELECT club FROM player_belong_club
                WHERE player = $1
              )
	            AND C.private_club = FALSE
          ) player_not_clubs
       ON counter_club.id = player_not_clubs.id
       ORDER BY club_name, creation_date`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
  * Get all the club where the $1 player is not and which are public club
  * @param {object} req
  * @param {object} res
  * @returns {object} player_belong_club & club object
  */
  async NgetPlayerClubsByPlayerIDRows(req, res) {
    const text =
    `SELECT * FROM
    (
      SELECT C.id, COUNT(PBC.player) as nb_player FROM
      club C
      JOIN
      player_belong_club PBC
      ON C.id = PBC.club
      GROUP BY C.id
    ) counter_club
    JOIN
    (
      SELECT * FROM club C
      WHERE C.id NOT IN
      (
        SELECT club FROM player_belong_club
        WHERE player = $1
      )
      AND C.private_club = FALSE
    ) player_not_clubs
    ON counter_club.id = player_not_clubs.id
    ORDER BY club_name, creation_date`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get all the player who belong to the $1 club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & player object
   */
  async getPlayerClubsByClubID(req, res) {
    const text =
      `SELECT * FROM
       player_belong_club PBC INNER JOIN player P ON P.id = PBC.player
       WHERE PBC.club = $1
       ORDER BY
          PBC.is_admin DESC,
          PBC.inscription_date ASC,
          P.surname ASC,
          P.first_name ASC`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get all the player who belong to the $1 club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & player object
   */
  async getPlayerClubsByClubIDRows(req, res) {
    const text =
      `SELECT * FROM
       player_belong_club PBC INNER JOIN player P ON P.id = PBC.player
       WHERE PBC.club = $1
       ORDER BY
          PBC.is_admin DESC,
          PBC.inscription_date ASC,
          P.surname ASC,
          P.first_name ASC`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
  * Get all the player who do not belong to the $1 club, have a public profil, and do not have an invitation for the $1 club
  * @param {object} req
  * @param {object} res
  * @returns {object} player_belong_club & player object
  */
  async NgetPlayerClubsByClubID(req, res) {
    const text =
    `SELECT * FROM player P WHERE
     P.id NOT IN (SELECT PBC.player FROM player_belong_club PBC WHERE PBC.club = $1)
     AND
     P.private_profil = FALSE
     AND
     P.id NOT IN (SELECT player FROM invitation_club WHERE club = $1)
     ORDER BY
        P.surname ASC,
        P.first_name ASC`;
    try {
      const { rows, rowCount } = await db.query(text, [req.params.id]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Add a player to a club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  async addPlayerToClub(req, res) {
    const text = "INSERT INTO player_belong_club (player, club, is_admin) VALUES($1, $2, $3) RETURNING *";
    const values = [req.body.player, req.body.club, req.body.is_admin];
    try {
      const { rows } = await db.query(text, values);
      return res.status(201).send(rows[0]);
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Add a player to a club given his pseudo
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  async addPlayerToClubByPseudo(req, res) {
    const text = "SELECT id FROM player WHERE pseudo = '$1'";
    const values = [req.body.player_pseudo];

    try {
      const { rows_id } = await db.query(text, values);
      if (rows_id.length == 0){
        return res.status(201).send(rows_id);
      }
      player = rows_id.id;
    } catch (error) {
      return res.status(400).send(error);
    }
    //
    text2 = "INSERT INTO player_belong_club (player, club, is_admin) VALUES($1, $2, $3) RETURNING *";
    values2 = [player, req.body.club, req.body.is_admin];
    try {
      const { rows } = await db.query(text2, values2);
      return res.status(201).send(rows);
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Delete a player from a club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  async deletePlayerFromClub(req, res) {
    const deleteQuery =
      "DELETE FROM player_belong_club WHERE club = $1 AND player = $2 RETURNING *";
    const values = [
      req.params.cid,
      req.params.pid
    ];
    try {
      const { rows } = await db.query(deleteQuery, values);
      if (!rows[0]) {
        return res.status(404).send({ message: "belonging of the player to the club not found" });
      }
      return res.status(204).send({ message: "deleted" });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get all the belonging of players to clubs
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  async getAll(req, res) {
    const findAllQuery = "SELECT * FROM player_belong_club";
    try {
      const { rows, rowCount } = await db.query(findAllQuery);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get a couple player/couple
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  async getOne(req, res) {
    const findAllQuery = `SELECT * FROM
                            player P
                            JOIN
                            player_belong_club PBC
                            ON
                            P.id = PBC.player
                            JOIN
                            club C
                            ON
                            PBC.club = C.id
                          WHERE PBC.player = $1 AND PBC.club = $2`;
    const values = [
      req.params.pid,
      req.params.cid
    ];
    try {
      const { rows, rowCount } = await db.query(findAllQuery, values);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get the number of admin of the $1 club
   * @param {object} req
   * @param {object} res
   * @returns {object} int
   */
  async getCountAdmin(req, res) {
    const text =
      "SELECT COUNT(*) AS nb FROM player_belong_club WHERE club = $1 AND is_admin = TRUE GROUP BY club";
    try {
      const { rows, rowCount } = await db.query(text, [req.params.cid]);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Promote a player to admin of the given club
   * @param {object} req
   * @param {object} res
   * @returns {object} updated player_belong_club object
   */
  async promoteToAdmin(req, res) {
    const text =
      "UPDATE player_belong_club SET is_admin = TRUE WHERE player = $1 AND club = $2 RETURNING *";
      const values = [
        req.params.pid,
        req.params.cid
      ];
    try {
      const { rows } = await db.query(text, values);
      return res.status(200).send(rows[0]);
    } catch (error) {
      return res.status(400).send(error);
    }
  }
};

export default player_belong_club;
