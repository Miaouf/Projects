import moment from "moment";
//import uuidv4 from 'uuid/v4';
import db from "../db";

const invitation_club = {
  /**
   * Create an invitation into a club
   * @param {object} req
   * @param {object} res
   * @returns {object} Invitation_Club object
   */
  async create(req, res) {
    const text1 = "SELECT * FROM Invitation_Club WHERE player = $1 AND club = $2";
    const text2 = "INSERT INTO Invitation_Club (player, club) VALUES ($1, $2) RETURNING *";
    const values = [req.params.pid, req.params.cid];

    try {
      const {rows, rowCount} = await db.query(text1, values);
      if (rowCount != 0){
        return res.status(200).send({ message: "already exist" });
      }
      await db.query(text2, values);
      return res.status(200).send({ message: "invitation send" });
    } catch (error) {
      return res.status(200).send(error);
    }
  },

  /**
   * Get the $1 player inviations into a club
   * @param {object} req
   * @param {object} res
   * @returns {object} inviations array
   */
  async getPlayerInvitations(req, res) {
    const text = `SELECT I.id, I.player, I.club, C.club_name, C.creation_date, C.nb_match_played, counter_clubs.nb_player FROM
                    Invitation_Club I
                    JOIN
                    Club C
                    ON
                    I.club = C.id
                    JOIN
                    (
                      SELECT C.id, COUNT(PBC.player) as nb_player FROM
                        club C
                        JOIN
                        player_belong_club PBC
                        ON C.id = PBC.club
                      GROUP BY C.id
                    ) counter_clubs
                    ON counter_clubs.id = C.id
                  WHERE I.player = $1`;
    const values = [req.params.pid];

    try {
      const { rows, rowCount } = await db.query(text, values);
      return res.status(200).send({ rows, rowCount });
    } catch (error) {
      return res.status(200).send(error);
    }
  },

  /**
   * Delete a player invitation into a club
   * @param {object} req
   * @param {object} res
   * @returns {void} return status code 204
   */
  async delete(req, res) {
    const deleteQuery = "DELETE FROM Invitation_Club WHERE player = $1 AND club = $2 RETURNING *";
    const values = [req.params.pid, req.params.cid];

    try {
      await db.query(deleteQuery, values);
      return res.status(204).send({ message: "deleted" });
    } catch (error) {
      return res.status(400).send(error);
    }
  }
};

export default invitation_club;
