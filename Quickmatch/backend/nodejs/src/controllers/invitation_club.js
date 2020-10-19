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


var invitation_club = {
  /**
   * Create an invitation into a club
   * @param {object} req
   * @param {object} res
   * @returns {object} Invitation_Club object
   */
  create: function () {
    var _ref = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee(req, res) {
      var text1, text2, values, _ref2, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              text1 = "SELECT * FROM Invitation_Club WHERE player = $1 AND club = $2";
              text2 = "INSERT INTO Invitation_Club (player, club) VALUES ($1, $2) RETURNING *";
              values = [req.params.pid, req.params.cid];
              _context.prev = 3;
              _context.next = 6;
              return _db2.default.query(text1, values);

            case 6:
              _ref2 = _context.sent;
              rows = _ref2.rows;
              rowCount = _ref2.rowCount;

              if (!(rowCount != 0)) {
                _context.next = 11;
                break;
              }

              return _context.abrupt("return", res.status(200).send({ message: "already exist" }));

            case 11:
              _context.next = 13;
              return _db2.default.query(text2, values);

            case 13:
              return _context.abrupt("return", res.status(200).send({ message: "invitation send" }));

            case 16:
              _context.prev = 16;
              _context.t0 = _context["catch"](3);
              return _context.abrupt("return", res.status(200).send(_context.t0));

            case 19:
            case "end":
              return _context.stop();
          }
        }
      }, _callee, this, [[3, 16]]);
    }));

    function create(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return create;
  }(),


  /**
   * Get the $1 player inviations into a club
   * @param {object} req
   * @param {object} res
   * @returns {object} inviations array
   */
  getPlayerInvitations: function () {
    var _ref3 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee2(req, res) {
      var text, values, _ref4, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee2$(_context2) {
        while (1) {
          switch (_context2.prev = _context2.next) {
            case 0:
              text = "SELECT I.id, I.player, I.club, C.club_name, C.creation_date, C.nb_match_played, counter_clubs.nb_player FROM\n                    Invitation_Club I\n                    JOIN\n                    Club C\n                    ON\n                    I.club = C.id\n                    JOIN\n                    (\n                      SELECT C.id, COUNT(PBC.player) as nb_player FROM\n                        club C\n                        JOIN\n                        player_belong_club PBC\n                        ON C.id = PBC.club\n                      GROUP BY C.id\n                    ) counter_clubs\n                    ON counter_clubs.id = C.id\n                  WHERE I.player = $1";
              values = [req.params.pid];
              _context2.prev = 2;
              _context2.next = 5;
              return _db2.default.query(text, values);

            case 5:
              _ref4 = _context2.sent;
              rows = _ref4.rows;
              rowCount = _ref4.rowCount;
              return _context2.abrupt("return", res.status(200).send({ rows: rows, rowCount: rowCount }));

            case 11:
              _context2.prev = 11;
              _context2.t0 = _context2["catch"](2);
              return _context2.abrupt("return", res.status(200).send(_context2.t0));

            case 14:
            case "end":
              return _context2.stop();
          }
        }
      }, _callee2, this, [[2, 11]]);
    }));

    function getPlayerInvitations(_x3, _x4) {
      return _ref3.apply(this, arguments);
    }

    return getPlayerInvitations;
  }(),


  /**
   * Delete a player invitation into a club
   * @param {object} req
   * @param {object} res
   * @returns {void} return status code 204
   */
  delete: function () {
    var _ref5 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee3(req, res) {
      var deleteQuery, values;
      return regeneratorRuntime.wrap(function _callee3$(_context3) {
        while (1) {
          switch (_context3.prev = _context3.next) {
            case 0:
              deleteQuery = "DELETE FROM Invitation_Club WHERE player = $1 AND club = $2 RETURNING *";
              values = [req.params.pid, req.params.cid];
              _context3.prev = 2;
              _context3.next = 5;
              return _db2.default.query(deleteQuery, values);

            case 5:
              return _context3.abrupt("return", res.status(204).send({ message: "deleted" }));

            case 8:
              _context3.prev = 8;
              _context3.t0 = _context3["catch"](2);
              return _context3.abrupt("return", res.status(400).send(_context3.t0));

            case 11:
            case "end":
              return _context3.stop();
          }
        }
      }, _callee3, this, [[2, 8]]);
    }));

    function _delete(_x5, _x6) {
      return _ref5.apply(this, arguments);
    }

    return _delete;
  }()
};

exports.default = invitation_club;