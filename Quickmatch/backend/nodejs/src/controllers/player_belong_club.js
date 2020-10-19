"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _moment = require("moment");

var _moment2 = _interopRequireDefault(_moment);

var _db = require("../db");

var _db2 = _interopRequireDefault(_db);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _asyncToGenerator(fn) { return function () { var gen = fn.apply(this, arguments); return new Promise(function (resolve, reject) { function step(key, arg) { try { var info = gen[key](arg); var value = info.value; } catch (error) { reject(error); return; } if (info.done) { resolve(value); } else { return Promise.resolve(value).then(function (value) { step("next", value); }, function (err) { step("throw", err); }); } } return step("next"); }); }; }
//import uuidv4 from 'uuid/v4';


var player_belong_club = {
  /**
   * Get all the clubs where the player is an admin
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & club object
   */
  getPlayerAdminClubs: function () {
    var _ref = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee(req, res) {
      var text, _ref2, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              text = "SELECT * FROM player_belong_club P INNER JOIN club C on P.club = C.id WHERE player = $1 AND is_admin = TRUE";
              _context.prev = 1;
              _context.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref2 = _context.sent;
              rows = _ref2.rows;
              rowCount = _ref2.rowCount;
              return _context.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context.prev = 10;
              _context.t0 = _context["catch"](1);
              return _context.abrupt("return", res.status(400).send(_context.t0));

            case 13:
            case "end":
              return _context.stop();
          }
        }
      }, _callee, this, [[1, 10]]);
    }));

    function getPlayerAdminClubs(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return getPlayerAdminClubs;
  }(),


  /**
   * Get all the club where the $1 player is
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & club object
   */
  getPlayerClubsByPlayerID: function () {
    var _ref3 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee2(req, res) {
      var text, _ref4, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee2$(_context2) {
        while (1) {
          switch (_context2.prev = _context2.next) {
            case 0:
              text = "SELECT * FROM\n         (\n           SELECT C.id, COUNT(PBC.player) as nb_player FROM\n             club C\n             JOIN\n             player_belong_club PBC\n             ON C.id = PBC.club\n           GROUP BY C.id\n         ) counter_club\n       JOIN\n         (\n        \t SELECT * FROM\n             club C\n             JOIN\n             player_belong_club PBC\n             ON C.id = PBC.club\n           WHERE PBC.player = $1\n         ) player_clubs\n       ON counter_club.id = player_clubs.id\n       ORDER BY is_admin DESC";
              _context2.prev = 1;
              _context2.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref4 = _context2.sent;
              rows = _ref4.rows;
              rowCount = _ref4.rowCount;
              return _context2.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context2.prev = 10;
              _context2.t0 = _context2["catch"](1);
              return _context2.abrupt("return", res.status(400).send(_context2.t0));

            case 13:
            case "end":
              return _context2.stop();
          }
        }
      }, _callee2, this, [[1, 10]]);
    }));

    function getPlayerClubsByPlayerID(_x3, _x4) {
      return _ref3.apply(this, arguments);
    }

    return getPlayerClubsByPlayerID;
  }(),


  /**
  * Get all the club where the $1 player is
  * @param {object} req
  * @param {object} res
  * @returns {object} player_belong_club & club object
  */
  getPlayerClubsByPlayerIDRows: function () {
    var _ref5 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee3(req, res) {
      var text, _ref6, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee3$(_context3) {
        while (1) {
          switch (_context3.prev = _context3.next) {
            case 0:
              text = "SELECT * FROM\n    (\n      SELECT C.id, COUNT(PBC.player) as nb_player FROM\n      club C\n      JOIN\n      player_belong_club PBC\n      ON C.id = PBC.club\n      GROUP BY C.id\n    ) counter_club\n    JOIN\n    (\n      SELECT * FROM\n      club C\n      JOIN\n      player_belong_club PBC\n      ON C.id = PBC.club\n      WHERE PBC.player = $1\n    ) player_clubs\n    ON counter_club.id = player_clubs.id\n    ORDER BY club_name, creation_date";
              _context3.prev = 1;
              _context3.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref6 = _context3.sent;
              rows = _ref6.rows;
              rowCount = _ref6.rowCount;
              return _context3.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context3.prev = 10;
              _context3.t0 = _context3["catch"](1);
              return _context3.abrupt("return", res.status(400).send(_context3.t0));

            case 13:
            case "end":
              return _context3.stop();
          }
        }
      }, _callee3, this, [[1, 10]]);
    }));

    function getPlayerClubsByPlayerIDRows(_x5, _x6) {
      return _ref5.apply(this, arguments);
    }

    return getPlayerClubsByPlayerIDRows;
  }(),


  /**
   * Get all the club where the $1 player is not and which are public club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & club object
   */
  NgetPlayerClubsByPlayerID: function () {
    var _ref7 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee4(req, res) {
      var text, _ref8, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee4$(_context4) {
        while (1) {
          switch (_context4.prev = _context4.next) {
            case 0:
              text = "SELECT * FROM\n          (\n            SELECT C.id, COUNT(PBC.player) as nb_player FROM\n              club C\n              JOIN\n              player_belong_club PBC\n              ON C.id = PBC.club\n            GROUP BY C.id\n          ) counter_club\n       JOIN\n          (\n\t          SELECT * FROM club C\n              WHERE C.id NOT IN\n              (\n                SELECT club FROM player_belong_club\n                WHERE player = $1\n              )\n\t            AND C.private_club = FALSE\n          ) player_not_clubs\n       ON counter_club.id = player_not_clubs.id\n       ORDER BY club_name, creation_date";
              _context4.prev = 1;
              _context4.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref8 = _context4.sent;
              rows = _ref8.rows;
              rowCount = _ref8.rowCount;
              return _context4.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context4.prev = 10;
              _context4.t0 = _context4["catch"](1);
              return _context4.abrupt("return", res.status(400).send(_context4.t0));

            case 13:
            case "end":
              return _context4.stop();
          }
        }
      }, _callee4, this, [[1, 10]]);
    }));

    function NgetPlayerClubsByPlayerID(_x7, _x8) {
      return _ref7.apply(this, arguments);
    }

    return NgetPlayerClubsByPlayerID;
  }(),


  /**
  * Get all the club where the $1 player is not and which are public club
  * @param {object} req
  * @param {object} res
  * @returns {object} player_belong_club & club object
  */
  NgetPlayerClubsByPlayerIDRows: function () {
    var _ref9 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee5(req, res) {
      var text, _ref10, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee5$(_context5) {
        while (1) {
          switch (_context5.prev = _context5.next) {
            case 0:
              text = "SELECT * FROM\n    (\n      SELECT C.id, COUNT(PBC.player) as nb_player FROM\n      club C\n      JOIN\n      player_belong_club PBC\n      ON C.id = PBC.club\n      GROUP BY C.id\n    ) counter_club\n    JOIN\n    (\n      SELECT * FROM club C\n      WHERE C.id NOT IN\n      (\n        SELECT club FROM player_belong_club\n        WHERE player = $1\n      )\n      AND C.private_club = FALSE\n    ) player_not_clubs\n    ON counter_club.id = player_not_clubs.id\n    ORDER BY club_name, creation_date";
              _context5.prev = 1;
              _context5.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref10 = _context5.sent;
              rows = _ref10.rows;
              rowCount = _ref10.rowCount;
              return _context5.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context5.prev = 10;
              _context5.t0 = _context5["catch"](1);
              return _context5.abrupt("return", res.status(400).send(_context5.t0));

            case 13:
            case "end":
              return _context5.stop();
          }
        }
      }, _callee5, this, [[1, 10]]);
    }));

    function NgetPlayerClubsByPlayerIDRows(_x9, _x10) {
      return _ref9.apply(this, arguments);
    }

    return NgetPlayerClubsByPlayerIDRows;
  }(),


  /**
   * Get all the player who belong to the $1 club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & player object
   */
  getPlayerClubsByClubID: function () {
    var _ref11 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee6(req, res) {
      var text, _ref12, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee6$(_context6) {
        while (1) {
          switch (_context6.prev = _context6.next) {
            case 0:
              text = "SELECT * FROM\n       player_belong_club PBC INNER JOIN player P ON P.id = PBC.player\n       WHERE PBC.club = $1\n       ORDER BY\n          PBC.is_admin DESC,\n          PBC.inscription_date ASC,\n          P.surname ASC,\n          P.first_name ASC";
              _context6.prev = 1;
              _context6.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref12 = _context6.sent;
              rows = _ref12.rows;
              rowCount = _ref12.rowCount;
              return _context6.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context6.prev = 10;
              _context6.t0 = _context6["catch"](1);
              return _context6.abrupt("return", res.status(400).send(_context6.t0));

            case 13:
            case "end":
              return _context6.stop();
          }
        }
      }, _callee6, this, [[1, 10]]);
    }));

    function getPlayerClubsByClubID(_x11, _x12) {
      return _ref11.apply(this, arguments);
    }

    return getPlayerClubsByClubID;
  }(),


  /**
   * Get all the player who belong to the $1 club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club & player object
   */
  getPlayerClubsByClubIDRows: function () {
    var _ref13 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee7(req, res) {
      var text, _ref14, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee7$(_context7) {
        while (1) {
          switch (_context7.prev = _context7.next) {
            case 0:
              text = "SELECT * FROM\n       player_belong_club PBC INNER JOIN player P ON P.id = PBC.player\n       WHERE PBC.club = $1\n       ORDER BY\n          PBC.is_admin DESC,\n          PBC.inscription_date ASC,\n          P.surname ASC,\n          P.first_name ASC";
              _context7.prev = 1;
              _context7.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref14 = _context7.sent;
              rows = _ref14.rows;
              rowCount = _ref14.rowCount;
              return _context7.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context7.prev = 10;
              _context7.t0 = _context7["catch"](1);
              return _context7.abrupt("return", res.status(400).send(_context7.t0));

            case 13:
            case "end":
              return _context7.stop();
          }
        }
      }, _callee7, this, [[1, 10]]);
    }));

    function getPlayerClubsByClubIDRows(_x13, _x14) {
      return _ref13.apply(this, arguments);
    }

    return getPlayerClubsByClubIDRows;
  }(),


  /**
  * Get all the player who do not belong to the $1 club, have a public profil, and do not have an invitation for the $1 club
  * @param {object} req
  * @param {object} res
  * @returns {object} player_belong_club & player object
  */
  NgetPlayerClubsByClubID: function () {
    var _ref15 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee8(req, res) {
      var text, _ref16, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee8$(_context8) {
        while (1) {
          switch (_context8.prev = _context8.next) {
            case 0:
              text = "SELECT * FROM player P WHERE\n     P.id NOT IN (SELECT PBC.player FROM player_belong_club PBC WHERE PBC.club = $1)\n     AND\n     P.private_profil = FALSE\n     AND\n     P.id NOT IN (SELECT player FROM invitation_club WHERE club = $1)\n     ORDER BY\n        P.surname ASC,\n        P.first_name ASC";
              _context8.prev = 1;
              _context8.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref16 = _context8.sent;
              rows = _ref16.rows;
              rowCount = _ref16.rowCount;
              return _context8.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context8.prev = 10;
              _context8.t0 = _context8["catch"](1);
              return _context8.abrupt("return", res.status(400).send(_context8.t0));

            case 13:
            case "end":
              return _context8.stop();
          }
        }
      }, _callee8, this, [[1, 10]]);
    }));

    function NgetPlayerClubsByClubID(_x15, _x16) {
      return _ref15.apply(this, arguments);
    }

    return NgetPlayerClubsByClubID;
  }(),


  /**
   * Add a player to a club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  addPlayerToClub: function () {
    var _ref17 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee9(req, res) {
      var text, values, _ref18, rows;

      return regeneratorRuntime.wrap(function _callee9$(_context9) {
        while (1) {
          switch (_context9.prev = _context9.next) {
            case 0:
              text = "INSERT INTO player_belong_club (player, club, is_admin) VALUES($1, $2, $3) RETURNING *";
              values = [req.body.player, req.body.club, req.body.is_admin];
              _context9.prev = 2;
              _context9.next = 5;
              return _db2.default.query(text, values);

            case 5:
              _ref18 = _context9.sent;
              rows = _ref18.rows;
              return _context9.abrupt("return", res.status(201).send(rows[0]));

            case 10:
              _context9.prev = 10;
              _context9.t0 = _context9["catch"](2);
              return _context9.abrupt("return", res.status(400).send(_context9.t0));

            case 13:
            case "end":
              return _context9.stop();
          }
        }
      }, _callee9, this, [[2, 10]]);
    }));

    function addPlayerToClub(_x17, _x18) {
      return _ref17.apply(this, arguments);
    }

    return addPlayerToClub;
  }(),


  /**
   * Add a player to a club given his pseudo
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  addPlayerToClubByPseudo: function () {
    var _ref19 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee10(req, res) {
      var text, values, _ref20, rows_id, _ref21, rows;

      return regeneratorRuntime.wrap(function _callee10$(_context10) {
        while (1) {
          switch (_context10.prev = _context10.next) {
            case 0:
              text = "SELECT id FROM player WHERE pseudo = '$1'";
              values = [req.body.player_pseudo];
              _context10.prev = 2;
              _context10.next = 5;
              return _db2.default.query(text, values);

            case 5:
              _ref20 = _context10.sent;
              rows_id = _ref20.rows_id;

              if (!(rows_id.length == 0)) {
                _context10.next = 9;
                break;
              }

              return _context10.abrupt("return", res.status(201).send(rows_id));

            case 9:
              player = rows_id.id;
              _context10.next = 15;
              break;

            case 12:
              _context10.prev = 12;
              _context10.t0 = _context10["catch"](2);
              return _context10.abrupt("return", res.status(400).send(_context10.t0));

            case 15:
              //
              text2 = "INSERT INTO player_belong_club (player, club, is_admin) VALUES($1, $2, $3) RETURNING *";
              values2 = [player, req.body.club, req.body.is_admin];
              _context10.prev = 17;
              _context10.next = 20;
              return _db2.default.query(text2, values2);

            case 20:
              _ref21 = _context10.sent;
              rows = _ref21.rows;
              return _context10.abrupt("return", res.status(201).send(rows));

            case 25:
              _context10.prev = 25;
              _context10.t1 = _context10["catch"](17);
              return _context10.abrupt("return", res.status(400).send(_context10.t1));

            case 28:
            case "end":
              return _context10.stop();
          }
        }
      }, _callee10, this, [[2, 12], [17, 25]]);
    }));

    function addPlayerToClubByPseudo(_x19, _x20) {
      return _ref19.apply(this, arguments);
    }

    return addPlayerToClubByPseudo;
  }(),


  /**
   * Delete a player from a club
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  deletePlayerFromClub: function () {
    var _ref22 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee11(req, res) {
      var deleteQuery, values, _ref23, rows;

      return regeneratorRuntime.wrap(function _callee11$(_context11) {
        while (1) {
          switch (_context11.prev = _context11.next) {
            case 0:
              deleteQuery = "DELETE FROM player_belong_club WHERE club = $1 AND player = $2 RETURNING *";
              values = [req.params.cid, req.params.pid];
              _context11.prev = 2;
              _context11.next = 5;
              return _db2.default.query(deleteQuery, values);

            case 5:
              _ref23 = _context11.sent;
              rows = _ref23.rows;

              if (rows[0]) {
                _context11.next = 9;
                break;
              }

              return _context11.abrupt("return", res.status(404).send({ message: "belonging of the player to the club not found" }));

            case 9:
              return _context11.abrupt("return", res.status(204).send({ message: "deleted" }));

            case 12:
              _context11.prev = 12;
              _context11.t0 = _context11["catch"](2);
              return _context11.abrupt("return", res.status(400).send(_context11.t0));

            case 15:
            case "end":
              return _context11.stop();
          }
        }
      }, _callee11, this, [[2, 12]]);
    }));

    function deletePlayerFromClub(_x21, _x22) {
      return _ref22.apply(this, arguments);
    }

    return deletePlayerFromClub;
  }(),


  /**
   * Get all the belonging of players to clubs
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  getAll: function () {
    var _ref24 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee12(req, res) {
      var findAllQuery, _ref25, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee12$(_context12) {
        while (1) {
          switch (_context12.prev = _context12.next) {
            case 0:
              findAllQuery = "SELECT * FROM player_belong_club";
              _context12.prev = 1;
              _context12.next = 4;
              return _db2.default.query(findAllQuery);

            case 4:
              _ref25 = _context12.sent;
              rows = _ref25.rows;
              rowCount = _ref25.rowCount;
              return _context12.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context12.prev = 10;
              _context12.t0 = _context12["catch"](1);
              return _context12.abrupt("return", res.status(400).send(_context12.t0));

            case 13:
            case "end":
              return _context12.stop();
          }
        }
      }, _callee12, this, [[1, 10]]);
    }));

    function getAll(_x23, _x24) {
      return _ref24.apply(this, arguments);
    }

    return getAll;
  }(),


  /**
   * Get a couple player/couple
   * @param {object} req
   * @param {object} res
   * @returns {object} player_belong_club object
   */
  getOne: function () {
    var _ref26 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee13(req, res) {
      var findAllQuery, values, _ref27, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee13$(_context13) {
        while (1) {
          switch (_context13.prev = _context13.next) {
            case 0:
              findAllQuery = "SELECT * FROM\n                            player P\n                            JOIN\n                            player_belong_club PBC\n                            ON\n                            P.id = PBC.player\n                            JOIN\n                            club C\n                            ON\n                            PBC.club = C.id\n                          WHERE PBC.player = $1 AND PBC.club = $2";
              values = [req.params.pid, req.params.cid];
              _context13.prev = 2;
              _context13.next = 5;
              return _db2.default.query(findAllQuery, values);

            case 5:
              _ref27 = _context13.sent;
              rows = _ref27.rows;
              rowCount = _ref27.rowCount;
              return _context13.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 11:
              _context13.prev = 11;
              _context13.t0 = _context13["catch"](2);
              return _context13.abrupt("return", res.status(400).send(_context13.t0));

            case 14:
            case "end":
              return _context13.stop();
          }
        }
      }, _callee13, this, [[2, 11]]);
    }));

    function getOne(_x25, _x26) {
      return _ref26.apply(this, arguments);
    }

    return getOne;
  }(),


  /**
   * Get the number of admin of the $1 club
   * @param {object} req
   * @param {object} res
   * @returns {object} int
   */
  getCountAdmin: function () {
    var _ref28 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee14(req, res) {
      var text, _ref29, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee14$(_context14) {
        while (1) {
          switch (_context14.prev = _context14.next) {
            case 0:
              text = "SELECT COUNT(*) AS nb FROM player_belong_club WHERE club = $1 AND is_admin = TRUE GROUP BY club";
              _context14.prev = 1;
              _context14.next = 4;
              return _db2.default.query(text, [req.params.cid]);

            case 4:
              _ref29 = _context14.sent;
              rows = _ref29.rows;
              rowCount = _ref29.rowCount;
              return _context14.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 10:
              _context14.prev = 10;
              _context14.t0 = _context14["catch"](1);
              return _context14.abrupt("return", res.status(400).send(_context14.t0));

            case 13:
            case "end":
              return _context14.stop();
          }
        }
      }, _callee14, this, [[1, 10]]);
    }));

    function getCountAdmin(_x27, _x28) {
      return _ref28.apply(this, arguments);
    }

    return getCountAdmin;
  }(),


  /**
   * Promote a player to admin of the given club
   * @param {object} req
   * @param {object} res
   * @returns {object} updated player_belong_club object
   */
  promoteToAdmin: function () {
    var _ref30 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee15(req, res) {
      var text, values, _ref31, rows;

      return regeneratorRuntime.wrap(function _callee15$(_context15) {
        while (1) {
          switch (_context15.prev = _context15.next) {
            case 0:
              text = "UPDATE player_belong_club SET is_admin = TRUE WHERE player = $1 AND club = $2 RETURNING *";
              values = [req.params.pid, req.params.cid];
              _context15.prev = 2;
              _context15.next = 5;
              return _db2.default.query(text, values);

            case 5:
              _ref31 = _context15.sent;
              rows = _ref31.rows;
              return _context15.abrupt("return", res.status(200).send(rows[0]));

            case 10:
              _context15.prev = 10;
              _context15.t0 = _context15["catch"](2);
              return _context15.abrupt("return", res.status(400).send(_context15.t0));

            case 13:
            case "end":
              return _context15.stop();
          }
        }
      }, _callee15, this, [[2, 10]]);
    }));

    function promoteToAdmin(_x29, _x30) {
      return _ref30.apply(this, arguments);
    }

    return promoteToAdmin;
  }()
};

exports.default = player_belong_club;