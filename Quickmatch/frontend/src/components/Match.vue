<template>
  <div>
    <div v-if="isSignedIn">
      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

      <div>
        <div class="my_card">
          <div class="title">Vos Matchs de cette semaine</div>
          <br />


          <div v-if="matchsInToShow.length != 0">
            <div class="tab_head_v_matchs">Jour</div>
            <div class="tab_head_v_matchs">Heure</div>
            <div class="tab_head_v_matchs">Localisation</div>
            <div class="tab_head_v_matchs">Joueurs</div>
            <div class="tab_head_v_matchs">Votre Décision</div>
            <div class="tab_head_v_matchs">Décider</div>

            <!-- POPUP -->
            <v-dialog v-model="dialog" persistent max-width="600px">
              <v-card>
                <v-card-text>
                  <v-container>
                    <v-col>
                      <span v-html="dialogDetails"></span>
                    </v-col>
                  </v-container>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="dialog = false">Fermer</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            <!-- /POPUP -->

            <div v-for="row in matchsInToShowPage" :key="row.id">
              <hr />
              <div class="tab_v_matchs">{{ row.day }}</div>
              <div class="tab_v_matchs">{{ row.hour }}</div>
              <div class="tab_v_matchs">{{ row.location }}</div>

              <div class="btn_v_matchs">
                <v-btn
                  dark
                  small
                  rounded
                  :color="row.color"
                  @click="showDialog(row.details)"
                >{{row.players}}</v-btn>
              </div>
              <div class="tab_v_matchs">{{ row.decision }}</div>

              <div v-if="row.decision == 'En Attente'" class="btn_v_matchs">
                  <v-btn
                    dark
                    small
                    rounded
                    color="green"
                    @click="AcceptInvit(row.meetid)"
                  >Accepter</v-btn>
                  <v-btn
                    dark
                    small
                    rounded
                    color="red"
                  @click="DeclineInvit(row.meetid)"
                  >Refuser</v-btn>
              </div>
              <div v-else class="btn_v_matchs">
                                    <v-btn
                    dark
                    small
                    rounded
                    color="grey"
                  @click="ModifyInvit(row.meetid)"
                  >Modifier</v-btn>
              </div>

            </div>
            <br />

            <div class="pages">
              <div class="numeric_icon" v-on:click="page_left_vc()">
                <v-icon color="black">mdi-chevron-left</v-icon>
              </div>
              <div
                style="display: inline-block;"
                v-for="index in this.matchsInPageMax"
                :key="index"
              >
                <div
                  v-bind:id="'m'+index"
                  class="numeric_icon"
                  v-on:click="page_vc(index)"
                >{{ index }}</div>
              </div>
              <div class="numeric_icon" v-on:click="page_right_vc()">
                <v-icon color="black">mdi-chevron-right</v-icon>
              </div>
            </div>
          </div>

          <div v-else>
            <hr />
            <div class="tab_nothing">Vous n'avez pas de match</div>
          </div>

          <br />
          <div style="text-align: right; margin-right: 5px;">
            <v-btn dark rounded align="left" color="#666" to="CreateMatch">Créer un match</v-btn>
          </div>
          <br />
        </div>

        <div class="my_card">
          <div class="title">Historique des matchs</div>
          <br />

          <div v-if="statsInNbRow != 0">
            <div class="tab_head_v_matchs">Date</div>
            <div class="tab_head_v_matchs">Heure</div>
            <div class="tab_head_v_matchs">Localisation</div>
            <div class="tab_head_v_matchs">Résultat</div>
            <div class="tab_head_v_matchs">Buts marqués</div>
            <div class="tab_head_v_matchs">Buts encaissés</div>

            <div v-for="row in statsInToShowPage" :key="row.id">
              <hr />
              <div class="tab_v_matchs">{{ new Date(row.precise_date).toLocaleDateString('fr-FR') }}</div>
              <div class="tab_v_matchs">{{ new Date(row.precise_date).toLocaleTimeString('fr-FR')}}</div>

              <div class="tab_v_matchs">{{ row.location }}</div>
              <div class="tab_v_matchs" v-if="row.won">Victoire</div>
              <div class="tab_v_matchs" v-else>Défaite</div>
              <div class="tab_v_matchs">{{ row.scored_goals }}</div>
              <div class="tab_v_matchs">{{ row.conceded_goals }}</div>
            </div>
            <br />

            <div class="pages">
              <div class="numeric_icon" v-on:click="page_left_st()">
                <v-icon color="black">mdi-chevron-left</v-icon>
              </div>
              <!-- <div class="numeric_icon" v-if="this.clubsInPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
              <div style="display: inline-block;" v-for="index in this.statsInPageMax" :key="index">
                <div
                  v-bind:id="'s'+index"
                  class="numeric_icon"
                  v-on:click="page_st(index)"
                >{{ index }}</div>
              </div>
              <div class="numeric_icon" v-on:click="page_right_st()">
                <v-icon color="black">mdi-chevron-right</v-icon>
              </div>
            </div>
          </div>

          <div v-else>
            <hr />
            <div class="tab_nothing">Vous n'avez joué aucun match</div>
          </div>

          <br />
          <br />
        </div>
      </div>

      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->
    </div>
    <Not-connected v-else></Not-connected>
  </div>
</template>

<style>
.pages {
  text-align: center;
}
.numeric_icon {
  margin: 2px;
  display: inline-block;
  background-color: white;
  color: black;
  border-radius: 5px;
  width: 30px;
  height: 30px;
}
.my_card {
  margin-top: 1%;
  margin-left: 2%;
  margin-right: 2%;
  margin-bottom: 1%;
  background-color: black;
  border-radius: 5px;
  box-shadow: 0px 4px 8px #aaa;
}
.title {
  color: white;
  font-size: 200%;
  margin: 5px;
}
.tab_nothing {
  text-align: center;
  font-weight: bold;
  color: white;
}
.tab_head_v_matchs,
.tab_v_matchs,
.btn_v_matchs {
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 5px;
  margin-left: 5px;
}
.tab_head_v_matchs {
  font-weight: bold;
  color: white;
}
.tab_v_matchs {
  color: #ccc;
  font-size: 120%;
}
.tab_head_v_matchs,
.tab_v_matchs,
.btn_v_matchs {
  width: 16%;
}
</style>

<script>
import store from "../store";
import axios from "axios";
import router from "../router";
import NotConnected from "./NotConnected";
export default {
  components: {
    NotConnected
  },
  data() {
    return {
      matchs: [],
      matchsToShow: {},
      matchsInToShow: {},
      matchsInPage: 1,
      matchsInPageMax: 1,
      matchsInNbRowPerPage: 5,
      matchsInNbRow: 0,
      matchsInToShowPage: [],

      /////////////////////////////////////////////////////////////////////////

      statsIn: [],
      statsToShow: {},
      statsInPage: 1,
      statsInPageMax: 1,
      statsInNbRowPerPage: 5,
      statsInNbRow: 0,
      statsInToShowPage: [],

      playerToShow: {},
      dialog: false,
      dialogDetails: {},

      pseudoRules: [
        v => !!v || "Pseudo requis",
        v => v.length >= 2 || "Pseudo trop court",
        v =>
          /^[a-zA-Z0-9 _\-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
          'Pseudo invalide (lettres, nombres, espace, "_" et "-" seulement)'
      ]
    };
  },
  async created() {
    var matchsIn = await axios
      .get(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBPlayer/" +
          this.id,
        { responseType: "json" }
      )
      .catch(e => {
        if (this.isSignedIn()) {
          alert(
            "Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page"
          );
          this.$router.go();
        }
      });

    this.matchsInToShow = matchsIn.data.rows;

    const statsIn = await axios
      .get(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/players/stat" +
          this.id,
        {
          responseType: "json"
        }
      )
      .catch(e => {
        if (this.isSignedIn()) {
          alert(
            "Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page. \n\n ERR: CANNOT_GET_PLAYER_STAT"
          );
          this.$router.go();
        }
      });

    this.statsInToShow = statsIn.data.rows;
    this.statsInPage = 1;
    this.statsInNbRow = statsIn.data.rowCount;
    this.statsInPageMax = Math.floor((statsIn.data.rowCount - 1) / this.statsInNbRowPerPage) + 1;
    this.statsInToShowPage = [];
    for (let i = 0; i < Math.min(this.statsInNbRowPerPage, this.statsInNbRow); i++) {
      this.statsInToShowPage.push(this.statsInToShow[i]);
    }

    this.byPlayerTable = matchsIn.data;
    this.matchInfo().then(response => {
      this.page_vc(Math.min(this.matchsInPageMax, this.matchsInPage));
      this.page_st(Math.min(this.statsInPageMax, this.statsInPage));
    });

    const pseudodata = await axios.get(
      "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Players/id" + this.id,
      {
        responseType: "json"
      }
    );
    this.pseudo = pseudodata.data.pseudo;
  },
  computed: {
    isSignedIn() {
      store.dispatch("isSignedIn");
      return store.getters.isSignedIn;
    },
    id() {
      store.dispatch("setID");
      return store.getters.id;
    }
  },
  methods: {
    async refresh_match() {
      var matchsIn = await axios
        .get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBPlayer/" +
            this.id,
          { responseType: "json" }
        )
        .catch(e => {
          if (this.isSignedIn()) {
            alert(
              "Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page"
            );
            this.$router.go();
          }
        });

      this.matchsInToShow = matchsIn.data.rows;

      this.byPlayerTable = matchsIn.data;

      this.matchs = [];

      await this.matchInfo();
    },

    async matchInfo() {
      let byPlayerTable = [];
      let tableMeet = [];
      let acceptedTable = [];
      let declinedTable = [];
      let meets = [];
      for (let i = 0; i < this.byPlayerTable.rowCount; i++) {
        meets.push(this.byPlayerTable.rows[i].meet);
      }
      let by_meet_table = {};
      let accepted = {};
      let declined = {};
      for (let i = 0; i < meets.length; i++) {
        by_meet_table = await axios.get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBMeet/" +
            meets[i],
          { responseType: "json" }
        );
        tableMeet.push(by_meet_table.data);

        accepted = await axios.get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMAccept/" +
            meets[i],
          { responseType: "json" }
        );
        acceptedTable.push(accepted.data);

        declined = await axios.get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMDecline/" +
            meets[i],
          { responseType: "json" }
        );
        declinedTable.push(declined.data);
      }

      for (let i = 0; i < this.byPlayerTable.rowCount; i++) {
        let color = "red";
        let state = "en manque de joueurs";
        let waiting = 0;
        let playing = 0;
        let refused = 0;
        for (let j = 0; j < tableMeet[i].rowCount; j++) {
          if (tableMeet[i].rows[j].status == true) playing++;
          else if (tableMeet[i].rows[j].status == false) refused++;
          else waiting++;
        }
        if (playing >= this.byPlayerTable.rows[i].minimal_team_size * 2) {
          color = "green";
          state = "accepté";
        } else if (
          playing + waiting >=
          this.byPlayerTable.rows[i].minimal_team_size * 2
        ) {
          color = "orange";
          state = "en attente";
        }

        // Les noms des joueurs
        let dts = "";
        dts = "<h1>Le match est "+state+"!</h1><br>"
        dts = dts + "<h3> Joueurs ayant accepté:</h3> <ul>";
        for (let j = 0; j < acceptedTable[i].rowCount; j++) {
          dts = dts + "<li>" + acceptedTable[i].rows[j].pseudo + "</li>";
        }
        dts = dts + "</ul>";
        if (declinedTable[i].rowCount > 0) {
          dts = dts + "<br> <h3> Joueurs ayant refusé:</h3> <ul>";
          for (let j = 0; j < declinedTable[i].rowCount; j++) {
            dts = dts + "<li>" + declinedTable[i].rows[j].pseudo + "</li>";
          }
          dts = dts + "</ul>";
        }
        if (waiting > 0) {
          if (waiting == 1) {
            dts += "<br> <p> Encore 1 joueur n'a pas encore répondu au match";
          } else {
            dts =
              dts +
              "<br> <p> Encore " +
              waiting +
              " joueurs n'ont pas encore répondu au match";
          }
        }
        //\\
        if (this.checkDecision(acceptedTable[i])) {
          this.matchs.push({
            club: "club" + i + "!",
            day: this.DayInFr(this.byPlayerTable.rows[i].repeat_day),
            location: this.byPlayerTable.rows[i].location,
            players:
              playing + "/" + this.byPlayerTable.rows[i].minimal_team_size * 2,
            state: state,
            color: color,
            hour: this.byPlayerTable.rows[i].start_hour,
            details: dts,
            meetid: this.byPlayerTable.rows[i].meet,
            decision: "Accepté"

          });
        }

        else if (this.checkDecision(declinedTable[i])) {
          this.matchs.push({
            club: "club" + i + "!",
            day: this.DayInFr(this.byPlayerTable.rows[i].repeat_day),
            location: this.byPlayerTable.rows[i].location,
            players:
              playing + "/" + this.byPlayerTable.rows[i].minimal_team_size * 2,
            state: state,
            color: color,
            hour: this.byPlayerTable.rows[i].start_hour,
            details: dts,
            meetid: this.byPlayerTable.rows[i].meet,
            decision: "Refusé"
          });
        }

        else {
          this.matchs.push({
            club: "club" + i + "!",
            day: this.DayInFr(this.byPlayerTable.rows[i].repeat_day),
            location: this.byPlayerTable.rows[i].location,
            players:
              playing + "/" + this.byPlayerTable.rows[i].minimal_team_size * 2,
            state: state,
            color: color,
            hour: this.byPlayerTable.rows[i].start_hour,
            details: dts,
            meetid: this.byPlayerTable.rows[i].meet,
            decision: "En Attente"
          });

        }

        this.matchsInNbRow = this.matchs.length;
        this.matchsInPageMax = Math.floor((this.matchs.length - 1) / this.matchsInNbRowPerPage) + 1;
        this.matchsInToShowPage = [];
        for (let i = 0; i < Math.min(this.matchsInNbRowPerPage, this.matchsInNbRow); i++) {
          this.matchsInToShowPage.push(this.matchs[i]);
        }
      }
    },

    //////////////////////////////////////////////////////////////////////
    //////////////////// Pages pour les matches /////////////////////

    page_left_vc() {
      if (this.matchsInPage != 1) {
        this.page_vc(this.matchsInPage - 1);
      }
    },
    page_right_vc() {
      if (this.matchsInPage != this.matchsInPageMax) {
        this.page_vc(this.matchsInPage + 1);
      }
    },
    page_vc(n) {
      document.getElementById("m" + this.matchsInPage).style.backgroundColor = "white";
      this.matchsInPage = n;
      document.getElementById("m" + this.matchsInPage).style.backgroundColor = "orange";
      let len = this.matchsInToShowPage.length;
      for (let i = 0; i < len; i++) {
        this.matchsInToShowPage.pop();
      }
      for (
        let i = (this.matchsInPage - 1) * this.matchsInNbRowPerPage;
        i < Math.min(this.matchsInNbRow, this.matchsInPage * this.matchsInNbRowPerPage);
        i++
      ) {
        this.matchsInToShowPage.push(this.matchs[i]);
      }
    },

    //////////////////////////////////////////////////////////////////////
    //////////////////// Pages pour les statistiques /////////////////////

    page_left_st() {
      if (this.statsInPage != 1) {
        this.page_st(this.statsInPage - 1);
      }
    },
    page_right_st() {
      if (this.statsInPage != this.statsInPageMax) {
        this.page_st(this.statsInPage + 1);
      }
    },
    page_st(n) {
      document.getElementById("s" + this.statsInPage).style.backgroundColor = "white";
      this.statsInPage = n;
      document.getElementById("s" + this.statsInPage).style.backgroundColor = "orange";
      let len = this.statsInToShowPage.length;
      for (let i = 0; i < len; i++) {
        this.statsInToShowPage.pop();
      }
      for (
        let i = (this.statsInPage - 1) * this.statsInNbRowPerPage;
        i <
        Math.min(this.statsInNbRow, this.statsInPage * this.statsInNbRowPerPage);
        i++
      ) {
        this.statsInToShowPage.push(this.statsInToShow[i]);
      }
    },

    //////////////////////////////////////////////////////////////////////

    showDialog(data) {
      (this.dialog = true), (this.dialogDetails = data);
    },

    checkDecision: function(items) {
      for (let i = 0; i < items.rowCount; i++) {
        if (this.pseudo == items.rows[i].pseudo)
          return true;
      }
      return false;
    },

    async ModifyInvit(meetid) {
      const inv_id = await axios.get(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMInv/" +
          this.id +
          "/" +
          meetid,
        {
          responseType: "json"
        }
      );
      let invid = inv_id.data.rows[0].id;
      await axios.put(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/invitations/" + invid,
        {
          status: null
        }
      );
      alert(
        "Vous serez rediriger vers vos invitations pour modifier votre choix !"
      );
      this.$router.push("/invitation");
    },
    async AcceptInvit(meetid) {
      const inv_id = await axios.get(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMInv/" +
          this.id +
          "/" +
          meetid,
        {
          responseType: "json"
        }
      );
      let invid = inv_id.data.rows[0].id;
      await axios.put(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/invitations/" + invid,
        {
          status: true
        }
      )
      .then(response => {
        alert("Invitation acceptée !");
        document.getElementById("m" + this.matchsInPage).style.backgroundColor = "white";
        this.refresh_match().then(response => {
          this.page_vc(this.matchsInPage);
        });
      });
    },
    async DeclineInvit(meetid) {
      const inv_id = await axios.get(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMInv/" +
          this.id +
          "/" +
          meetid,
        {
          responseType: "json"
        }
      );
      let invid = inv_id.data.rows[0].id;
      await axios.put(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/invitations/" + invid,
        {
          status: false
        }
      )
      .then(response => {
        alert("Invitation refusée !");
        document.getElementById("m" + this.matchsInPage).style.backgroundColor = "white";
        this.refresh_match().then(response => {
          this.page_vc(this.matchsInPage);
        });
      });
    },

    DayInFr(s) {
      switch (s) {
        case "Monday":
          return "Lundi";
        case "Tuesday":
          return "Mardi";
        case "Wednesday":
          return "Mercredi";
        case "Thursday":
          return "Jeudi";
        case "Friday":
          return "Vendredi";
        case "Saturday":
          return "Samedi";
        case "Sunday":
          return "Dimanche";
      }
    }
  }
};
</script>
