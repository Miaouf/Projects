"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});

var _nodemailer = require("nodemailer");

var _nodemailer2 = _interopRequireDefault(_nodemailer);

function _interopRequireDefault(obj) {
  return obj && obj.__esModule ? obj : { default: obj };
}

function _asyncToGenerator(fn) {
  return function() {
    var gen = fn.apply(this, arguments);
    return new Promise(function(resolve, reject) {
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
            function(value) {
              step("next", value);
            },
            function(err) {
              step("throw", err);
            }
          );
        }
      }
      return step("next");
    });
  };
}

var validAccount = {
  sendMail: (function() {
    var _ref = _asyncToGenerator(
      /*#__PURE__*/ regeneratorRuntime.mark(function _callee(req, res) {
        var transporter, mailOptions;
        return regeneratorRuntime.wrap(
          function _callee$(_context) {
            while (1) {
              switch ((_context.prev = _context.next)) {
                case 0:
                  _context.next = 2;
                  return _nodemailer2.default.createTransport({
                    service: "gmail",
                    auth: {
                      user: "no.reply.quickmatch@gmail.com",
                      pass: "v1v3Qu1ckm4tch"
                    }
                  });

                case 2:
                  transporter = _context.sent;

                  // setuping mail options from req
                  mailOptions = {
                    from: "no.reply.quickmatch@gmail.com", // sender address
                    to: req.body.to, // list of receivers
                    subject: req.body.subject, // Subject line
                    text: req.body.text,
                    html: req.body.html // plain text body
                  };
                  // sending email...

                  transporter.sendMail(mailOptions, function(err, info) {
                    if (err) {
                      console.log(err);
                      return res.json({ error: "API Error" }); // error
                    }
                    /*else{
                            console.log("The message is sent: " + response.message);
                            return res.json({response: "sent"})
                        }*/
                  });

                case 5:
                case "end":
                  return _context.stop();
              }
            }
          },
          _callee,
          this
        );
      })
    );

    function sendMail(_x, _x2) {
      return _ref.apply(this, arguments);
    }

    return sendMail;
  })()
};

exports.default = validAccount;