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


var Club = {
  /**
   * Create A Club
   * @param {object} req
   * @param {object} res
   * @returns {object} Club object
   */
  create: function () {
    var _ref = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee(req, res) {
      var text, values, _ref2, rows;

      return regeneratorRuntime.wrap(function _callee$(_context) {
        while (1) {
          switch (_context.prev = _context.next) {
            case 0:
              text = "INSERT INTO Club (club_name, private_club)\n\t    VALUES($1, $2)\n        RETURNING *";
              values = [req.body.club_name, req.body.private_club];
              _context.prev = 2;
              _context.next = 5;
              return _db2.default.query(text, values);

            case 5:
              _ref2 = _context.sent;
              rows = _ref2.rows;
              return _context.abrupt("return", res.status(201).send(rows[0]));

            case 10:
              _context.prev = 10;
              _context.t0 = _context["catch"](2);
              return _context.abrupt("return", res.status(400).send(_context.t0));

            case 13:
            case "end":
              return _context.stop();
          }
        }
      }, _callee, this, [[2, 10]]);
    }));

    function create(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return create;
  }(),


  /**
   * Get All clubs
   * @param {object} req
   * @param {object} res
   * @returns {object} clubs array
   */
  getAll: function () {
    var _ref3 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee2(req, res) {
      var findAllQuery, _ref4, rows, rowCount;

      return regeneratorRuntime.wrap(function _callee2$(_context2) {
        while (1) {
          switch (_context2.prev = _context2.next) {
            case 0:
              findAllQuery = "SELECT * FROM club";
              _context2.prev = 1;
              _context2.next = 4;
              return _db2.default.query(findAllQuery);

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

    function getAll(_x3, _x4) {
      return _ref3.apply(this, arguments);
    }

    return getAll;
  }(),


  /**
   * Get All clubs rows
   * @param {object} req
   * @param {object} res
   * @returns {object} players array
   */
  getAllRows: function () {
    var _ref5 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee3(req, res) {
      var findAllQuery, a;
      return regeneratorRuntime.wrap(function _callee3$(_context3) {
        while (1) {
          switch (_context3.prev = _context3.next) {
            case 0:
              findAllQuery = "SELECT * FROM club";
              _context3.prev = 1;
              _context3.next = 4;
              return _db2.default.query(findAllQuery);

            case 4:
              a = _context3.sent;
              return _context3.abrupt("return", res.status(200).send(a.rows));

            case 8:
              _context3.prev = 8;
              _context3.t0 = _context3["catch"](1);
              return _context3.abrupt("return", res.status(200).send(_context3.t0));

            case 11:
            case "end":
              return _context3.stop();
          }
        }
      }, _callee3, this, [[1, 8]]);
    }));

    function getAllRows(_x5, _x6) {
      return _ref5.apply(this, arguments);
    }

    return getAllRows;
  }(),


  /**
   * Get A Club
   * @param {object} req
   * @param {object} res
   * @returns {object} club object
   */
  getOne: function () {
    var _ref6 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee4(req, res) {
      var text, _ref7, rows;

      return regeneratorRuntime.wrap(function _callee4$(_context4) {
        while (1) {
          switch (_context4.prev = _context4.next) {
            case 0:
              text = "SELECT * FROM\n                    (\n                      SELECT C.id, C.club_name, C.creation_date, C.private_club, C.nb_match_played, COUNT(PBC.player) as nb_player FROM\n                        club C\n                        JOIN\n                        player_belong_club PBC\n                        ON C.id = PBC.club\n                      GROUP BY C.id, C.club_name, C.creation_date, C.private_club, C.nb_match_played\n                    ) one_club\n                  WHERE one_club.id = $1";
              _context4.prev = 1;
              _context4.next = 4;
              return _db2.default.query(text, [req.params.id]);

            case 4:
              _ref7 = _context4.sent;
              rows = _ref7.rows;

              if (rows[0]) {
                _context4.next = 8;
                break;
              }

              return _context4.abrupt("return", res.status(404).send({ message: "club not found" }));

            case 8:
              return _context4.abrupt("return", res.status(200).send(rows[0]));

            case 11:
              _context4.prev = 11;
              _context4.t0 = _context4["catch"](1);
              return _context4.abrupt("return", res.status(400).send(_context4.t0));

            case 14:
            case "end":
              return _context4.stop();
          }
        }
      }, _callee4, this, [[1, 11]]);
    }));

    function getOne(_x7, _x8) {
      return _ref6.apply(this, arguments);
    }

    return getOne;
  }(),


  /**
   * Update A Club
   * @param {object} req
   * @param {object} res
   * @returns {object} updated Club
   */
  update: function () {
    var _ref8 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee5(req, res) {
      var updateOneQuery, values, response;
      return regeneratorRuntime.wrap(function _callee5$(_context5) {
        while (1) {
          switch (_context5.prev = _context5.next) {
            case 0:
              updateOneQuery = "UPDATE club\n                            SET club_name = $1, private_club = $2\n                            WHERE id = $3 RETURNING *";
              _context5.prev = 1;
              values = [req.params.name, req.params.private, req.params.cid];
              _context5.next = 5;
              return _db2.default.query(updateOneQuery, values);

            case 5:
              response = _context5.sent;
              return _context5.abrupt("return", res.status(200).send(response.rows[0]));

            case 9:
              _context5.prev = 9;
              _context5.t0 = _context5["catch"](1);
              return _context5.abrupt("return", res.status(400).send(_context5.t0));

            case 12:
            case "end":
              return _context5.stop();
          }
        }
      }, _callee5, this, [[1, 9]]);
    }));

    function update(_x9, _x10) {
      return _ref8.apply(this, arguments);
    }

    return update;
  }(),


  /**
   * Delete A Club
   * @param {object} req
   * @param {object} res
   * @returns {void} return statuc code 204
   */
  delete: function () {
    var _ref9 = _asyncToGenerator( /*#__PURE__*/regeneratorRuntime.mark(function _callee6(req, res) {
      var deleteQuery1, deleteQuery2, deleteQuery3;
      return regeneratorRuntime.wrap(function _callee6$(_context6) {
        while (1) {
          switch (_context6.prev = _context6.next) {
            case 0:
              deleteQuery1 = "DELETE FROM player_belong_club WHERE club=$1 RETURNING *";
              deleteQuery2 = "DELETE FROM invitation_club WHERE club=$1 RETURNING *";
              deleteQuery3 = "DELETE FROM club WHERE id=$1 RETURNING *";
              _context6.prev = 3;
              _context6.next = 6;
              return _db2.default.query(deleteQuery1, [req.params.id]);

            case 6:
              _context6.next = 8;
              return _db2.default.query(deleteQuery2, [req.params.id]);

            case 8:
              _context6.next = 10;
              return _db2.default.query(deleteQuery3, [req.params.id]);

            case 10:
              return _context6.abrupt("return", res.status(204).send({ message: "deleted" }));

            case 13:
              _context6.prev = 13;
              _context6.t0 = _context6["catch"](3);
              return _context6.abrupt("return", res.status(400).send(_context6.t0));

            case 16:
            case "end":
              return _context6.stop();
          }
        }
      }, _callee6, this, [[3, 13]]);
    }));

    function _delete(_x11, _x12) {
      return _ref9.apply(this, arguments);
    }

    return _delete;
  }()
};

exports.default = Club;