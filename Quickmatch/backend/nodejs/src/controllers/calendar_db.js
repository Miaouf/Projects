"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true,
});

var _db = require("../db");

var _db2 = _interopRequireDefault(_db);

function _interopRequireDefault(obj) {
  return obj && obj.__esModule ? obj : { default: obj };
}

function _asyncToGenerator(fn) {
  return function () {
    var gen = fn.apply(this, arguments);
    return new Promise(function (resolve, reject) {
      function step(key, arg) {
        try {
          var info = gen[key](arg);
          var value = info.value;
        } catch (error) {
          reject(error);
          return;
        }
        if (info.done) {
          resolve(value);
        } else {
          return Promise.resolve(value).then(
            function (value) {
              step("next", value);
            },
            function (err) {
              step("throw", err);
            }
          );
        }
      }
      return step("next");
    });
  };
}

var Calendar_db = {
  getByPlayer: (function () {
    var _ref = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee(req, res) {
        var text, _ref2, rows, rowCount;

        return regeneratorRuntime.wrap(
          function _callee$(_context) {
            while (1) {
              switch ((_context.prev = _context.next)) {
                case 0:
                  text =
                    "select m.id as meet, i.player as player, i.id as invitation, m.played, status, ms.won, m.precise_date as precise_date, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, played from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id left outer join meet_sheet ms on i.meet = ms.meet where i.player=$1 order by precise_date desc";
                  _context.prev = 1;
                  _context.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  _ref2 = _context.sent;
                  rows = _ref2.rows;
                  rowCount = _ref2.rowCount;
                  return _context.abrupt(
                    "return",
                    res.status(200).send({ rows: rows, rowCount: rowCount })
                  );

                case 10:
                  _context.prev = 10;
                  _context.t0 = _context["catch"](1);
                  return _context.abrupt(
                    "return",
                    res.status(400).send(_context.t0)
                  );

                case 13:
                case "end":
                  return _context.stop();
              }
            }
          },
          _callee,
          this,
          [[1, 10]]
        );
      })
    );

    function getByPlayer(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return getByPlayer;
  })(),

  getNotDecided: (function () {
    var _ref = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee(req, res) {
        var text, _ref2, rows, rowCount;

        return regeneratorRuntime.wrap(
          function _callee$(_context) {
            while (1) {
              switch ((_context.prev = _context.next)) {
                case 0:
                  text =
                    "select m.id as meet, i.player as player, status, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, precise_date, played from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id where i.player=$1 and status IS NULL order by repeat_day";
                  _context.prev = 1;
                  _context.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  _ref2 = _context.sent;
                  rows = _ref2.rows;
                  rowCount = _ref2.rowCount;
                  return _context.abrupt(
                    "return",
                    res.status(200).send({ rows: rows, rowCount: rowCount })
                  );

                case 10:
                  _context.prev = 10;
                  _context.t0 = _context["catch"](1);
                  return _context.abrupt(
                    "return",
                    res.status(400).send(_context.t0)
                  );

                case 13:
                case "end":
                  return _context.stop();
              }
            }
          },
          _callee,
          this,
          [[1, 10]]
        );
      })
    );

    function getNotDecided(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return getNotDecided;
  })(),

  /* Get all Meets of a player (without rows) */
  getByPlayerRows: (function () {
    var _ref = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee(req, res) {
        var text, a;

        return regeneratorRuntime.wrap(
          function _callee$(_context) {
            while (1) {
              switch ((_context.prev = _context.next)) {
                case 0:
                  text =
                    "select m.id as meet, i.player as player, i.id as invitation, m.played, status, ms.won, m.precise_date as precise_date, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, played from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id left outer join meet_sheet ms on i.meet = ms.meet where i.player=$1 order by precise_date desc";
                  _context.prev = 1;
                  _context.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  a = _context.sent;
                  return _context.abrupt(
                    "return",
                    res.status(200).send(a.rows)
                  );

                case 8:
                  _context.prev = 8;
                  _context.t0 = _context["catch"](1);
                  return _context.abrupt(
                    "return",
                    res.status(400).send(_context.t0)
                  );

                case 11:
                case "end":
                  return _context.stop();
              }
            }
          },
          _callee,
          this,
          [[1, 8]]
        );
      })
    );

    function getByPlayerRows(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return getByPlayerRows;
  })(),

  getByMeet: (function () {
    var _ref3 = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee2(req, res) {
        var text, _ref4, rows, rowCount;

        return regeneratorRuntime.wrap(
          function _callee2$(_context2) {
            while (1) {
              switch ((_context2.prev = _context2.next)) {
                case 0:
                  text =
                    "select m.id as meet, i.player as player, status, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, precise_date from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id where meet=$1 ";
                  _context2.prev = 1;
                  _context2.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  _ref4 = _context2.sent;
                  rows = _ref4.rows;
                  rowCount = _ref4.rowCount;
                  return _context2.abrupt(
                    "return",
                    res.status(200).send({ rows: rows, rowCount: rowCount })
                  );

                case 10:
                  _context2.prev = 10;
                  _context2.t0 = _context2["catch"](1);
                  return _context2.abrupt(
                    "return",
                    res.status(400).send(_context2.t0)
                  );

                case 13:
                case "end":
                  return _context2.stop();
              }
            }
          },
          _callee2,
          this,
          [[1, 10]]
        );
      })
    );

    function getByMeet(_x3, _x4) {
      return _ref3.apply(this, arguments);
    }

    return getByMeet;
  })(),

  getByMeetRows: (function () {
    var _ref3 = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee2(req, res) {
        var text, a;

        return regeneratorRuntime.wrap(
          function _callee2$(_context2) {
            while (1) {
              switch ((_context2.prev = _context2.next)) {
                case 0:
                  text =
                    "select m.id as meet, i.player as player, i.id as invitation, m.played, status, ms.won, m.precise_date as precise_date, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, precise_date from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id left outer join meet_sheet ms on i.meet = ms.meet where i.meet=$1 ";
                  _context2.prev = 1;
                  _context2.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  a = _context2.sent;
                  return _context2.abrupt(
                    "return",
                    res.status(200).send(a.rows)
                  );

                case 8:
                  _context2.prev = 8;
                  _context2.t0 = _context2["catch"](1);
                  return _context2.abrupt(
                    "return",
                    res.status(400).send(_context2.t0)
                  );

                case 11:
                case "end":
                  return _context2.stop();
              }
            }
          },
          _callee2,
          this,
          [[1, 8]]
        );
      })
    );

    function getByMeetRows(_x3, _x4) {
      return _ref3.apply(this, arguments);
    }

    return getByMeetRows;
  })(),

  getAccepted: (function () {
    var _ref5 = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee3(req, res) {
        var text, _ref6, rows, rowCount;

        return regeneratorRuntime.wrap(
          function _callee3$(_context3) {
            while (1) {
              switch ((_context3.prev = _context3.next)) {
                case 0:
                  text =
                    "select pseudo from player, (select m.id as meet, i.player as player, status, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, precise_date from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id where meet = $1 and status = TRUE) t where t.player = id";
                  _context3.prev = 1;
                  _context3.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  _ref6 = _context3.sent;
                  rows = _ref6.rows;
                  rowCount = _ref6.rowCount;
                  return _context3.abrupt(
                    "return",
                    res.status(200).send({ rows: rows, rowCount: rowCount })
                  );

                case 10:
                  _context3.prev = 10;
                  _context3.t0 = _context3["catch"](1);
                  return _context3.abrupt(
                    "return",
                    res.status(400).send(_context3.t0)
                  );

                case 13:
                case "end":
                  return _context3.stop();
              }
            }
          },
          _callee3,
          this,
          [[1, 10]]
        );
      })
    );

    function getAccepted(_x5, _x6) {
      return _ref5.apply(this, arguments);
    }

    return getAccepted;
  })(),
  getDeclined: (function () {
    var _ref7 = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee4(req, res) {
        var text, _ref8, rows, rowCount;

        return regeneratorRuntime.wrap(
          function _callee4$(_context4) {
            while (1) {
              switch ((_context4.prev = _context4.next)) {
                case 0:
                  text =
                    "select pseudo from player, (select m.id as meet, i.player as player, status, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, precise_date from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id where meet = $1 and status = FALSE) t where t.player = id";
                  _context4.prev = 1;
                  _context4.next = 4;
                  return _db2.default.query(text, [req.params.id]);

                case 4:
                  _ref8 = _context4.sent;
                  rows = _ref8.rows;
                  rowCount = _ref8.rowCount;
                  return _context4.abrupt(
                    "return",
                    res.status(200).send({ rows: rows, rowCount: rowCount })
                  );

                case 10:
                  _context4.prev = 10;
                  _context4.t0 = _context4["catch"](1);
                  return _context4.abrupt(
                    "return",
                    res.status(400).send(_context4.t0)
                  );

                case 13:
                case "end":
                  return _context4.stop();
              }
            }
          },
          _callee4,
          this,
          [[1, 10]]
        );
      })
    );

    function getDeclined(_x7, _x8) {
      return _ref7.apply(this, arguments);
    }

    return getDeclined;
  })(),
  getInv: (function () {
    var _ref7 = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee4(req, res) {
        var text, _ref8, rows, rowCount;

        return regeneratorRuntime.wrap(
          function _callee4$(_context4) {
            while (1) {
              switch ((_context4.prev = _context4.next)) {
                case 0:
                  text =
                    "select distinct id from invitation a, (select m.id as meet, i.player as player, status, start_hour, end_hour, repeat_day, location, minimal_team_size, maximal_team_size, precise_date from invitation i left outer join slot s on i.slot = s.id left outer join meet m on i.meet = m.id where meet = $1) t where a.player = $2 and a.meet = $3";
                  _context4.prev = 1;
                  _context4.next = 4;
                  return _db2.default.query(text, [
                    req.params.mid,
                    req.params.pid,
                    req.params.mid,
                  ]);

                case 4:
                  _ref8 = _context4.sent;
                  rows = _ref8.rows;
                  rowCount = _ref8.rowCount;
                  return _context4.abrupt(
                    "return",
                    res.status(200).send({ rows: rows, rowCount: rowCount })
                  );

                case 10:
                  _context4.prev = 10;
                  _context4.t0 = _context4["catch"](1);
                  return _context4.abrupt(
                    "return",
                    res.status(400).send(_context4.t0)
                  );

                case 13:
                case "end":
                  return _context4.stop();
              }
            }
          },
          _callee4,
          this,
          [[1, 10]]
        );
      })
    );

    function getInv(_x7, _x8) {
      return _ref7.apply(this, arguments);
    }

    return getInv;
  })(),
};

exports.default = Calendar_db;
