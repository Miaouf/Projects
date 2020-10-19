import moment from "moment";
//import uuidv4 from 'uuid/v4';
import db from "../db";

const Club = {
  /**
   * Create A Club
   * @param {object} req
   * @param {object} res
   * @returns {object} Club object
   */
  async create(req, res) {
    const text = `INSERT INTO Club (club_name, private_club)
	    VALUES($1, $2)
        RETURNING *`;
    const values = [req.body.club_name, req.body.private_club];

    try {
      const { rows } = await db.query(text, values);
      return res.status(201).send(rows[0]);
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get All clubs
   * @param {object} req
   * @param {object} res
   * @returns {object} clubs array
   */
  async getAll(req, res) {
    const findAllQuery = "SELECT * FROM club";
    try {
      const { rows, rowCount } = await db.query(findAllQuery);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Get All clubs rows
   * @param {object} req
   * @param {object} res
   * @returns {object} players array
   */
  async getAllRows(req, res) {
    const findAllQuery = "SELECT * FROM club";
    try {
      const a = await db.query(findAllQuery);
      return res.status(200).send(a.rows);
    } catch (error) {
      return res.status(200).send(error);
    }
  },

  /**
   * Get A Club
   * @param {object} req
   * @param {object} res
   * @returns {object} club object
   */
  async getOne(req, res) {
    const text = `SELECT * FROM
                    (
                      SELECT C.id, C.club_name, C.creation_date, C.private_club, C.nb_match_played, COUNT(PBC.player) as nb_player FROM
                        club C
                        JOIN
                        player_belong_club PBC
                        ON C.id = PBC.club
                      GROUP BY C.id, C.club_name, C.creation_date, C.private_club, C.nb_match_played
                    ) one_club
                  WHERE one_club.id = $1`;
    try {
      const { rows } = await db.query(text, [req.params.id]);
      if (!rows[0]) {
        return res.status(404).send({ message: "club not found" });
      }
      return res.status(200).send(rows[0]);
    } catch (error) {
      return res.status(400).send(error);
    }
  },

  /**
   * Update A Club
   * @param {object} req
   * @param {object} res
   * @returns {object} updated Club
   */
  async update(req, res) {
    const updateOneQuery = `UPDATE club
                            SET club_name = $1, private_club = $2
                            WHERE id = $3 RETURNING *`;
    try {
      const values = [
        req.params.name,
        req.params.private,
        req.params.cid
      ];
      const response = await db.query(updateOneQuery, values);
      return res.status(200).send(response.rows[0]);
    } catch (err) {
      return res.status(400).send(err);
    }
  },

  /**
   * Delete A Club
   * @param {object} req
   * @param {object} res
   * @returns {void} return statuc code 204
   */
  async delete(req, res) {
    const deleteQuery1 = "DELETE FROM player_belong_club WHERE club=$1 RETURNING *";
    const deleteQuery2 = "DELETE FROM invitation_club WHERE club=$1 RETURNING *";
      const deleteQuery3 = "DELETE FROM club WHERE id=$1 RETURNING *";
    try {
      await db.query(deleteQuery1, [req.params.id]);
      await db.query(deleteQuery2, [req.params.id])
      await db.query(deleteQuery3, [req.params.id])
      return res.status(204).send({ message: "deleted" });
    } catch (error) {
      return res.status(400).send(error);
    }
  }
};

export default Club;
