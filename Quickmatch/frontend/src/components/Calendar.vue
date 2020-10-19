<template>
  <div v-if="isSignedIn">
    <v-row class="fill-height">
      <v-col>
        <v-sheet height="64">
          <v-toolbar flat color="white">
            <v-btn outlined class="mr-4" @click="setToday">Aujourd'hui</v-btn>
            <v-btn outlined class="mr-4" to="CreateMatch">Créer match</v-btn>
            <v-spacer></v-spacer>
            <v-btn fab text small @click="prev">
              <v-icon small>mdi-chevron-left</v-icon>
            </v-btn>
            <v-toolbar-title>{{ title }}</v-toolbar-title>
            <v-btn fab text small @click="next">
              <v-icon small>mdi-chevron-right</v-icon>
            </v-btn>
            <v-spacer></v-spacer>
            <v-menu bottom right>
              <template v-slot:activator="{ on }">
                <v-btn outlined v-on="on">
                  <span>{{ typeToLabel[type] }}</span>
                  <v-icon right>mdi-menu-down</v-icon>
                </v-btn>
              </template>
              <v-list>
                <v-list-item @click="type = 'day'">
                  <v-list-item-title>Jour</v-list-item-title>
                </v-list-item>
                <v-list-item @click="type = 'week'">
                  <v-list-item-title>Semaine</v-list-item-title>
                </v-list-item>
                <v-list-item @click="type = 'month'">
                  <v-list-item-title>Mois</v-list-item-title>
                </v-list-item>
                <v-list-item @click="type = '4day'">
                  <v-list-item-title>4 jours</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>
          </v-toolbar>
        </v-sheet>
        <v-sheet height="600">
          <v-calendar
            ref="calendar"
            v-model="focus"
            color="primary"
            :events="events"
            :event-color="getEventColor"
            :event-margin-bottom="3"
            :type="type"
            @click:event="showEvent"
            @click:more="viewDay"
            @click:date="viewDay"
            @change="updateRange"
          ></v-calendar>
          <v-menu
            v-model="selectedOpen"
            :close-on-content-click="false"
            :activator="selectedElement"
            full-width
            offset-x
          >
            <v-card color="grey lighten-4" min-width="350px" flat>
              <v-toolbar :color="selectedEvent.color" dark>
                <!-- <v-btn icon>
                  <v-icon>mdi-pencil</v-icon>
                </v-btn>-->
                <v-toolbar-title v-html="selectedEvent.name"></v-toolbar-title>
                <v-spacer></v-spacer>
                <!-- <v-btn icon>
                  <v-icon>mdi-dots-vertical</v-icon>
                </v-btn>-->
              </v-toolbar>
              <v-card-text>
                <span v-html="selectedEvent.details"></span>
              </v-card-text>
              <v-card-actions>
                <v-btn text color="primary" @click="selectedOpen = false">OK</v-btn>
                <div v-if="!selectedEvent.played">
                  <v-btn
                    v-if="selectedEvent.inv"
                    text
                    color="success"
                    @click="AcceptInvit(selectedEvent.meetid)"
                  >Accepter</v-btn>
                  <v-btn
                    v-if="selectedEvent.inv"
                    text
                    color="error"
                    @click="DeclineInvit(selectedEvent.meetid)"
                  >Refuser</v-btn>
                  <v-btn
                    v-else
                    text
                    color="secondary"
                    @click="ModifyInvit(selectedEvent.meetid)"
                  >Modifier</v-btn>
                </div>
              </v-card-actions>
            </v-card>
          </v-menu>
        </v-sheet>
      </v-col>
    </v-row>
  </div>
  <NotConnected v-else></NotConnected>
</template>

<script>
import axios from "axios";
import moment from "moment";
import NotConnected from "./NotConnected";
import store from "../store";
export default {
  components: {
    NotConnected
  },
  data: () => ({
    today: new Date(),
    focus: new Date(),
    type: "month",
    typeToLabel: {
      month: "Mois",
      week: "Semaine",
      day: "Jour",
      "4day": "4 jours"
    },
    daysOfWeek: [
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday",
      "Sunday"
    ],
    slotsTable: {},
    invitationsTable: {},
    playersTable: {},
    byPlayerTable: {},
    byMeetTable: {},
    pseudo: null,
    start: null,
    end: null,
    selectedEvent: {},
    selectedElement: null,
    selectedOpen: false,
    events: [
      {
        name: "RDV PFA",
        details: "Go to INRIA",
        start: "2019-12-20 11:00:00",
        end: "2019-12-20 12:00:00",
        color: "blue"
      },
      {
        name: "New Year",
        details: "Eat chocolate until you pass out",
        start: "2020-01-01",
        end: "2020-01-01",
        color: "green"
      }
    ]
  }),
  computed: {
    isSignedIn() {
      store.dispatch("isSignedIn");
      return store.getters.isSignedIn;
    },
    id() {
      store.dispatch("setID");
      return store.getters.id;
    },
    title() {
      const { start, end } = this;
      if (!start || !end) {
        return "";
      }
      const startMonth = this.monthFormatter(start);
      const endMonth = this.monthFormatter(end);
      const suffixMonth = startMonth === endMonth ? "" : endMonth;
      const startYear = start.year;
      const endYear = end.year;
      const suffixYear = startYear === endYear ? "" : endYear;
      const startDay = start.day + this.nth(start.day);
      const endDay = end.day + this.nth(end.day);
      switch (this.type) {
        case "month":
          return `${startMonth} ${startYear}`;
        case "week":
        case "4day":
          return `${startMonth} ${startDay} ${startYear} - ${suffixMonth} ${endDay} ${suffixYear}`;
        case "day":
          return `${startMonth} ${startDay} ${startYear}`;
      }
      return "";
    },
    monthFormatter() {
      return this.$refs.calendar.getFormatter({
        timeZone: "UTC",
        month: "long"
      });
    }
  },
  mounted() {
    this.$refs.calendar.checkChange();
  },
  methods: {
    checkPseudoInZabi: function(Json9elwa) {
      for (let i = 0; i < Json9elwa.rowCount; i++) {
        if (this.pseudo == Json9elwa.rows[i].pseudo) return true;
      }
      return false;
    },
    setSlotsTable: function(response) {
      this.slotsTable = response.data;
    },
    setInvitationsTable: function(response) {
      this.invitationsTable = response.data;
    },
    setPlayersTable: function(response) {
      this.playersTable = response.data;
    },
    setByPlayerTable: function(response) {
      this.byPlayerTable = response.data;
    },
    setByMeetTable: function(response) {
      this.byMeetTable = response.data;
    },
    showSlots: async function() {
      let byPlayerTable = [];
      let tableMeet = [];
      let acceptedTable = [];
      let declinedTable = [];
      let meets = [];
      for (let i = 0; i < this.byPlayerTable.rowCount; i++) {
        meets.push(this.byPlayerTable.rows[i].meet);
      }
      let by_meet_table;
      let accepted;
      let declined;
      for (let i = 0; i < meets.length; i++) {
        by_meet_table = await axios.get(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBMeet/" +
            meets[i],
          {
            responseType: "json"
          }
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

      // Retrieve Players of a
      // Shows the slots
      for (let i = 0; i < this.byPlayerTable.rowCount; i++) {
        let gameDate = this.byPlayerTable.rows[i].precise_date;
        let color = "red";
        let waiting = 0;
        let playing = 0;
        for (let j = 0; j < tableMeet[i].rowCount; j++) {
          if (tableMeet[i].rows[j].status == true) playing++;
          else if (tableMeet[i].rows[j].status == false);
          else waiting++;
        }
        if (playing >= this.byPlayerTable.rows[i].minimal_team_size * 2) {
          color = "green";
        } else if (
          playing + waiting >=
          this.byPlayerTable.rows[i].minimal_team_size * 2
        ) {
          color = "orange";
        }
        let dts = "";
        dts = "<h3> Joueurs ayant accepté:</h3> <ul>";
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
        let cmpdate = new Date(gameDate);
        if (cmpdate < this.today) {
          if (this.byPlayerTable.rows[i].played == true) {
            if (this.byPlayerTable.rows[i].won == true) {
              this.events.push({
                name: "Match remporté à " + this.byPlayerTable.rows[i].location,
                details:
                  "Score du matche sera affiché ici (ex. Team Faverge 9 - 0 Matmika)",
                start:
                  gameDate.substring(0, 11) +
                  " " +
                  this.byPlayerTable.rows[i].start_hour,
                end:
                  gameDate.substring(0, 11) +
                  " " +
                  this.byPlayerTable.rows[i].end_hour,
                color: "black",
                meetid: this.byPlayerTable.rows[i].meet,
                played: true
              });
            } else {
              this.events.push({
                name: "Match perdu à " + this.byPlayerTable.rows[i].location,
                details:
                  "<a href=https://youtu.be/dQw4w9WgXcQ>Score du matche sera affiché ici. Don't give up</a>",
                start:
                  gameDate.substring(0, 10) +
                  " " +
                  this.byPlayerTable.rows[i].start_hour,
                end:
                  gameDate.substring(0, 10) +
                  " " +
                  this.byPlayerTable.rows[i].end_hour,
                color: "black",
                meetid: this.byPlayerTable.rows[i].meet,
                played: true
              });
            }
          }
        } else {
          if (this.checkPseudoInZabi(acceptedTable[i])) {
            this.events.push({
              name:
                "Match à " +
                this.byPlayerTable.rows[i].location +
                " ~~~ " +
                playing +
                "/" +
                this.byPlayerTable.rows[i].minimal_team_size * 2,
              details: dts,
              start:
                gameDate.substring(0, 11) +
                " " +
                this.byPlayerTable.rows[i].start_hour,
              end:
                gameDate.substring(0, 11) +
                " " +
                this.byPlayerTable.rows[i].end_hour,
              color: color,
              meetid: this.byPlayerTable.rows[i].meet,
              inv: false,
              played: false
            });
          } else if (this.checkPseudoInZabi(declinedTable[i])) {
            this.events.push({
              name:
                "Match à " +
                this.byPlayerTable.rows[i].location +
                " ~~~ " +
                playing +
                "/" +
                this.byPlayerTable.rows[i].minimal_team_size * 2,
              details: dts,
              start:
                gameDate.substring(0, 11) +
                " " +
                this.byPlayerTable.rows[i].start_hour,
              end:
                gameDate.substring(0, 11) +
                " " +
                this.byPlayerTable.rows[i].end_hour,
              color: "grey",
              meetid: this.byPlayerTable.rows[i].meet,
              inv: false,
              played: false
            });
          } else {
            this.events.push({
              name:
                "Invitation à " +
                this.byPlayerTable.rows[i].location +
                " ~~~ " +
                playing +
                "/" +
                this.byPlayerTable.rows[i].minimal_team_size * 2,
              details: dts,
              start:
                gameDate.substring(0, 11) +
                " " +
                this.byPlayerTable.rows[i].start_hour,
              end:
                gameDate.substring(0, 11) +
                " " +
                this.byPlayerTable.rows[i].end_hour,
              color: "blue",
              meetid: this.byPlayerTable.rows[i].meet,
              inv: true,
              played: false
            });
          }
        }
      }
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
      );
      alert("Invitation acceptée !");
      this.$router.go();
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
      );
      alert("Invitation refusée !");
      this.$router.go();
    },
    viewDay({ date }) {
      this.focus = date;
      this.type = "day";
    },
    getEventColor(event) {
      return event.color;
    },
    setToday() {
      this.focus = this.today;
    },
    prev() {
      this.$refs.calendar.prev();
    },
    next() {
      this.$refs.calendar.next();
    },
    showEvent({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event;
        this.selectedElement = nativeEvent.target;
        setTimeout(() => (this.selectedOpen = true), 10);
      };
      if (this.selectedOpen) {
        this.selectedOpen = false;
        setTimeout(open, 10);
      } else {
        open();
      }
      nativeEvent.stopPropagation();
    },
    updateRange({ start, end }) {
      this.start = start;
      this.end = end;
    },
    nth(d) {
      return d > 3 && d < 21
        ? "th"
        : ["th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"][d % 10];
    },
    surname() {
      return store.getters.surname();
    }
  },
  async created() {
    const by_player_table = await axios.get(
      "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBPlayer/" +
        this.id,
      {
        responseType: "json"
      }
    );
    const pseudodata = await axios.get(
      "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Players/id" + this.id,
      {
        responseType: "json"
      }
    );
    this.pseudo = pseudodata.data.pseudo;
    this.byPlayerTable = by_player_table.data;
    this.showSlots();
  }
};
</script>
