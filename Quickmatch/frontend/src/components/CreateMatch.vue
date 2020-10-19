<template>
  <div>
    <br />
    <h1 style="text-align:center;">Création de match</h1>
    <br />
    <br />
    <v-card class="mx-auto" max-width="1000" tile>
      <br />
      <span
        v-if="Err"
        style="color : red; font-size: 25px;margin: 15px;"
      >Merci de renseigner tous les champs</span>

      <v-list-item two-line>
        <v-list-item-content>
          <v-list-item-title class="font-weight-bold">Club</v-list-item-title>
          <v-list-item-subtitle class="headline">
            <v-select
              v-model="select"
              :items="items"
              :rules="[v => !!v || 'Item is required']"
              label
              required
            ></v-select>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-list-item two-line>
        <v-menu
          ref="menuStart"
          v-model="menuStart"
          :close-on-content-click="false"
          :nudge-right="40"
          :return-value.sync="timeStart"
          transition="scale-transition"
          offset-y
          max-width="290px"
          min-width="290px"
        >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="timeStart"
              label="Heure de début"
              readonly
              v-on="on"
              :rules="[v => !!v || 'Item is required']"
            ></v-text-field>
          </template>
          <v-time-picker
            v-if="menuStart"
            v-model="timeStart"
            full-width
            @click:minute="$refs.menuStart.save(timeStart)"
            format="24hr"
          ></v-time-picker>
        </v-menu>
      </v-list-item>

      <v-list-item two-line>
        <v-menu
          ref="menuEnd"
          v-model="menuEnd"
          :close-on-content-click="false"
          :nudge-right="40"
          :return-value.sync="timeEnd"
          transition="scale-transition"
          offset-y
          max-width="290px"
          min-width="290px"
        >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="timeEnd"
              label="Heure de fin"
              readonly
              v-on="on"
              :rules="[v => !!v || 'Item is required']"
            ></v-text-field>
          </template>
          <v-time-picker
            v-if="menuEnd"
            v-model="timeEnd"
            full-width
            @click:minute="$refs.menuEnd.save(timeEnd)"
            format="24hr"
          ></v-time-picker>
        </v-menu>
      </v-list-item>

      <v-list-item>
        <v-list-item-content>
          <v-list-item-subtitle class="headline">
            <v-text-field label="Localisation" v-model="location"></v-text-field>
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>

      <v-list-item style="width: 900px">
        <v-subheader>Min et Max de joueurs requis par équipe pour le match</v-subheader>
        <v-range-slider
          v-model="range"
          :max="max"
          :min="min"
          ticks="always"
          tick-size="4"
          hide-details
          :tick-labels="ticksLabels"
          class="align-center"
        ></v-range-slider>
      </v-list-item>

      <v-list-item>
        <v-list-item-title class="font-weight-bold">Jour de répétition:</v-list-item-title>
      </v-list-item>

      <v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Lundi" value="Monday"></v-checkbox>
        </v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Mardi" value="Tuesday"></v-checkbox>
        </v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Mercredi" value="Wednesday"></v-checkbox>
        </v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Jeudi" value="Thursday"></v-checkbox>
        </v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Vendredi" value="Friday"></v-checkbox>
        </v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Samedi" value="Saturday"></v-checkbox>
        </v-list-item>
        <v-list-item>
          <v-checkbox v-model="selected" label="Dimanche" value="Sunday"></v-checkbox>
        </v-list-item>
      </v-list-item>

      <v-card-actions>
        <v-btn text class="title" color="deep-purple accent-4" v-on:click="CreateMatch">Créer !</v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script>
import axios from "axios";
import store from "../store";
import moment from "moment";

export default {
  data: () => ({
    Err: 0,
    select: null,
    timeStart: null,
    menuStart: false,
    timeEnd: null,
    menuEnd: false,
    playerClubs_data: {},
    items: [],
    selected: [],
    location: null,
    min: 1,
    max: 15,
    range: [0, 16],
    ticksLabels: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
    daysOfWeek: [
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday",
      "Sunday"
    ]
  }),

  methods: {
    fillClubs() {
      for (let i = 0; i < this.playerClubs_data.rowCount; i++) {
        this.items.push(this.playerClubs_data.rows[i].club_name);
      }
    },

    CreateMatch: async function() {
      this.Err = 0;

      if (
        !this.select ||
        this.selected.length == 0 ||
        !this.timeEnd ||
        !this.timeEnd ||
        !this.location
      ) {
        this.Err = 1;
        return;
      }
      let playersInv = [];
      //Retrieve players
      let cid = null;
      for (let i = 0; i < this.playerClubs_data.rowCount; i++) {
        if (this.playerClubs_data.rows[i].club_name == this.select)
          cid = this.playerClubs_data.rows[i].club;
      }
      let apiRep1 = null;
      apiRep1 = await axios.get(
        "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/playerClubs/cid" +
          cid,
        {
          responseType: "json"
        }
      );
      for (let i = 0; i < apiRep1.data.rowCount; i++) {
        playersInv.push(apiRep1.data.rows[i].id);
      }

      //Create Slot (or see if it's already exist ?)
      const ts = this.timeStart;
      const te = this.timeEnd;
      let slotsId = [];
      for (let i = 0; i < this.selected.length; i++) {
        let apiRep = null;
        apiRep = await axios.post(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/slots/",
          {
            start_hour: ts,
            end_hour: te,
            repeat_day: this.selected[i]
          }
        );
        if (apiRep.data.name != "error") {
          slotsId.push(apiRep.data.id);
        } else {
          this.Err = 1;
          return;
        }
      }

      //Create Meet
      let meetsID = [];
      for (let i = 0; i < this.selected.length; i++) {
        //Get Precise_date
        const dayINeed = this.daysOfWeek.indexOf(this.selected[i]) + 1;
        const today = moment().isoWeekday();
        let gameDate = "";
        if (today <= dayINeed) {
          gameDate = moment().isoWeekday(dayINeed);
        } else {
          gameDate = moment()
            .add(1, "weeks")
            .isoWeekday(dayINeed);
        }

        let precise_date = gameDate.format("YYYY-MM-DD") + " " + ts;

        let deletion_date = moment(precise_date).add(21, "days");
        let apiRep3 = null;
        apiRep3 = await axios.post(
          "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Meets",
          {
            location: this.location,
            precise_date: precise_date,
            minimal_team_size: this.range[0],
            maximal_team_size: this.range[1],
            deletion_date: deletion_date,
            played: false
          }
        );
        if (apiRep3.data.name != "error") {
          meetsID.push(apiRep3.data.id);
        } else {
          this.Err = 1;
          return;
        }
      }

      //Create Invitations
      for (let i = 0; i < slotsId.length; i++) {
        for (let j = 0; j < playersInv.length; j++) {
          let s = null;
          if (playersInv[j] == this.id) {
            s = true;
          }
          let apiRep2 = null;
          apiRep2 = await axios.post(
            "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Invitations",
            {
              slot_id: slotsId[i],
              player_id: playersInv[j],
              event_type: "Match",
              meet_id: meetsID[i],
              status: s
            }
          );
        }
      }

      this.$router.push({ path: `/calendar` });
    }
  },

  async created() {
    const player_clubs = await axios.get(
      "https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/playerClubs/paid" +
        this.id,
      {
        responseType: "json"
      }
    );
    this.playerClubs_data = player_clubs.data;
    this.fillClubs();
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
  }
};
</script>

<style></style>
