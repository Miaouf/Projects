import Vue from "vue";
import Vuex from "vuex";
import axios from "axios";

import router from "../router";
import store from ".";

Vue.use(Vuex);

/* Cookies administration */

function setCookie(name, value, expire, path, domain, security) {
  document.cookie =
    name +
    " = " +
    escape(value) +
    "  " +
    (expire == undefined ? "" : "; expires = " + expire.toUTCString()) +
    (path == undefined ? "" : "; path = " + path) +
    (domain == undefined ? "" : "; domain = " + domain) +
    (security == true ? "; secure" : "");
}

function getCookie(name) {
  if (document.cookie.length == 0) return null;

  var regSepCookie = new RegExp("(; )", "g");
  var cookies = document.cookie.split(regSepCookie);

  for (var i = 0; i < cookies.length; i++) {
    var regInfo = new RegExp("=", "g");
    var infos = cookies[i].split(regInfo);
    if (infos[0] == name) {
      return unescape(infos[1]);
    }
  }
  return null;
}

function forcedDisconnection() {
  store.dispatch("logout");
  router.push("/");
}

/* ********************* */

export default new Vuex.Store({
  state: {
    id: 0,
    givenName: "",
    name: "", // nom complet
    surname: "",
    email: "none", // can't be empty
    nickname: "",
    imageUrl: "",
    isSignedIn: false,
    isValid: false,
    connectionDate: Object,
    expirationDate: Object,
    expirationTimer: 0,
    googleUser: Object,
    profile: Object,
    hasAccount: false,
    cookieCheck: false, // to avoid multiple checkings (infinite loop)
  },
  mutations: {
    setGoogleUser(state, googleUser) {
      state.cookieCheck = true;
      state.googleUser = googleUser;
      state.profile = googleUser.getBasicProfile();
      state.givenName = state.profile.getGivenName();
      state.name = state.profile.getName();
      state.surname = state.profile.getFamilyName();
      state.email = state.profile.getEmail();
      state.imageUrl = state.profile.getImageUrl();
      state.isSignedIn = true;
      state.expirationTimer = state.googleUser.getAuthResponse().expires_in;
      /*** Setting expiration timer ***/
      state.connectionDate = new Date();
      state.expirationDate = new Date();
      state.expirationDate.setSeconds(
        state.connectionDate.getSeconds() + state.expirationTimer
      );
    },
    isSignedIn(state) {
      if (state.cookieCheck == false) {
        state.cookieCheck = true;
        var cookieExpiration = getCookie("quickmatchExpiration");
        var cookieId = getCookie("quickmatchId");
        var cookieValid = getCookie("quickmatchValid");
        if (
          cookieId != null &&
          cookieExpiration != null &&
          cookieValid != null
        ) {
          state.expirationDate = new Date(cookieExpiration);
          state.id = cookieId;
          state.hasAccount = true;
          state.isValid = cookieValid;
        }
      }
      if (state.hasAccount == true) {
        var now = new Date();
        state.isSignedIn = now < state.expirationDate;
        if (state.isSignedIn == false) {
          state.hasAccount = false;
          alert("Votre session a expiré.");
          document.location.reload(true);
        }
      } else {
        state.isSignedIn = false;
      }
    },
    logout(state) {
      state.hasAccount = false;
      state.isSignedIn = false;
      var end = new Date();
      end.setTime(end.getTime() - 1);
      /* cookie deletion */
      setCookie(
        "quickmatchExpiration",
        state.expirationDate,
        end,
        "/",
        "quickmatch.fr",
        false
      );
      setCookie("quickmatchId", state.id, end, "/", "quickmatch.fr", false);
      setCookie(
        "quickmatchValid",
        state.isValid,
        end,
        "/",
        "quickmatch.fr",
        false
      );
    },
    hasAccount(state) {
      state.hasAccount = true;
      var cookieExp = getCookie("quickmatchExpiration");
      if (cookieExp == null) {
        setCookie(
          "quickmatchExpiration",
          state.expirationDate,
          state.expirationDate,
          "/",
          "quickmatch.fr",
          false
        );
      }
    },
    async setID(state) {
      var cookieId = getCookie("quickmatchId");
      if (cookieId == null) {
        const player = await axios.get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/ma" +
            state.email,
          { ResponseType: "json" }
        );
        state.id = player.data.id;
        setCookie(
          "quickmatchId",
          state.id,
          state.expirationDate,
          "/",
          "quickmatch.fr",
          false
        );
      } else {
        state.id = cookieId;
      }
    },
    setEmail(state, mail) {
      state.email = mail;
      state.connectionDate = new Date();
      state.expirationDate = new Date();
      state.expirationDate.setSeconds(state.connectionDate.getSeconds() + 3600);
    },
    async setIsValid(state, bool) {
      state.isValid = bool;
      var cookieValid = getCookie("quickmatchValid");
      if (cookieValid == null) {
        setCookie(
          "quickmatchValid",
          state.isValid,
          state.expirationDate,
          "/",
          "quickmatch.fr",
          false
        );
      }
    },
    async setIsValidGoogle(state) {
      state.isValid = true;
      var cookieValid = getCookie("quickmatchValid");
      if (cookieValid == null) {
        setCookie(
          "quickmatchValid",
          state.isValid,
          state.expirationDate,
          "/",
          "quickmatch.fr",
          false
        );
      }
    },
    async sendAgain(state) {
      state.isValid = false;
      if (state.email == "none") {
        alert(
          "Une erreur est survenue, vous allez être déconnecté(e) et redirigé(e) vers la page d'accueil. Si le problème persiste, merci de contacter le support. \n\n ERR: VERIFY_ACCOUNT_MAIL_NONE"
        );
        forcedDisconnection();
        return 1;
      }
      if (state.id == 0) {
        let getId = await axios.get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/ma" +
            state.email,
          { ResponseType: "json" }
        );
        state.id = getId.data.id;
      }
      var cookieCodeVerif = getCookie("quickmatchCodeVerif");
      if (cookieCodeVerif != null) {
        alert("Merci de patienter quelques instants avant un nouvel envoi.");
      } else {
        var now = new Date();
        var end = new Date();
        end.setSeconds(now.getSeconds() + 120);
        setCookie(
          "quickmatchCodeVerif",
          state.isValid,
          end,
          "/",
          "quickmatch.fr",
          false
        );
        let apiRep = await axios.post(
          "https://dbcontrol.quickmatch.fr//dbcontrol/api/v1/sendMail",
          {
            to: state.email, // list of receivers
            subject: "Votre code de vérification Quickmatch", // Subject line
            text:
              "[HTML NOT SUPPORTED] Bonjour, votre code est le suivant:" +
              state.id +
              ". A bientôt sur Quickmatch.fr !",
            html:
              "<p>Bonjour,<br><br> Votre code est le suivant : " +
              state.id +
              '.<br><br> Merci de nous faire confiance, <br> À bientôt sur Quickmatch.fr ! <br><br></p><font size="-5"><i> Ce mail a été envoyé automatiquement, merci de ne pas y répondre.</i></front>', // plain text body
          }
        );
        alert("Le code vient d'être envoyé à l'adresse " + state.email + " !");
      }
    },
  },
  actions: {
    setGoogleUser(commit, googleUser) {
      this.commit("setGoogleUser", googleUser);
    },
    isSignedIn(commit) {
      this.commit("isSignedIn");
    },
    hasAccount(commit) {
      this.commit("hasAccount");
    },
    logout(commit) {
      this.commit("logout");
    },
    setID(commit) {
      this.commit("setID");
    },
    setEmail(commit, mail) {
      this.commit("setEmail", mail);
    },
    setIsValid(commit, bool) {
      this.commit("setIsValid", bool);
    },
    setIsValidGoogle(commit) {
      this.commit("setIsValidGoogle");
    },
    sendAgain(commit) {
      this.commit("sendAgain");
    },
  },
  getters: {
    givenName: function(state) {
      return state.givenName;
    },
    name: function(state) {
      return state.name;
    },
    surname: function(state) {
      return state.surname;
    },
    email: function(state) {
      return state.email;
    },
    isSignedIn: function(state) {
      return state.isSignedIn;
    },
    isValid: function(state) {
      return state.isValid;
    },
    connectionDate: function(state) {
      return state.connectionDate;
    },
    expirationDate: function(state) {
      return state.expirationDate;
    },
    id: function(state) {
      return state.id;
    },
    imageUrl: function(state) {
      return state.imageUrl;
    },
  },
});
