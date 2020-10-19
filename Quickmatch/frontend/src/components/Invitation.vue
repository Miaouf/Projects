<template>
  <div v-if="isSignedIn">
    <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

    <div class="my_card">
      <div class="title">Invitation(s) pour un match</div>

      <div v-if="InvitationsToShow.length != 0">
        <div class="tab_head tab_inv">Localisation</div>
        <div class="tab_head tab_inv">Date</div>
        <div class="tab_head tab_inv">Nombre minimum de joueurs</div>
        <div class="tab_head tab_inv">Nombre maximum de joueurs</div>
        <div class="tab_head tab_inv">Refuser</div>
        <div class="tab_head tab_inv">Accepter</div>

        <div v-for="row in InvitationsToShowPage" :key="row.id">
          <hr>
          <div class="tab_row tab_inv">{{ row.location }}</div>
          <div class="tab_row tab_inv">Le {{DayInFrench(row.repeat_day)}} de {{row.start_hour}} à {{row.end_hour}}</div>
          <div class="tab_row tab_inv">{{ row.minimal_team_size }}</div>
          <div class="tab_row tab_inv">{{ row.maximal_team_size }}</div>
          <div class="tab_btn tab_inv">
            <v-btn dark small rounded color="#666" v-on:click="declineInv(row.meet)">Refuser</v-btn>
          </div>
          <div class="tab_btn tab_inv">
            <v-btn dark small rounded color="#666" v-on:click="acceptInv(row.meet)">Accepter</v-btn>
          </div>
        </div>
        <br>

        <div class="pages">
          <div class="numeric_icon" v-on:click="page_left_inv()"><v-icon color="black">mdi-chevron-left</v-icon></div>
          <!-- <div class="numeric_icon" v-if="this.InvitationsPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
          <div style="display: inline-block;" v-for="index in this.InvitationsPageMax" :key="index">
            <div v-bind:id="'i'+index" class="numeric_icon" v-on:click="page_inv(index)">{{ index }}</div>
          </div>
          <div class="numeric_icon" v-on:click="page_right_inv()"><v-icon color="black">mdi-chevron-right</v-icon></div>
        </div>
      </div>

      <div v-else>
        <hr><br>
        <div class="tab_nothing">Vous n'avez pas d'invitations pour un match</div>
      </div>
    </div>

    <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

    <div class="my_card">
      <div class="title">Invitation(s) dans un club</div>

      <div v-if="InvitationsClubToShow.length != 0">
        <div class="tab_head tab_inv_c">Nom du Club</div>
        <div class="tab_head tab_inv_c">Date de création</div>
        <div class="tab_head tab_inv_c">Nombre de joueur(s)</div>
        <div class="tab_head tab_inv_c">Nombre de match(s) joué(s)</div>
        <div class="tab_head tab_inv_c">Refuser</div>
        <div class="tab_head tab_inv_c">Accepter</div>

        <div v-for="row in InvitationsClubToShowPage" :key="row.id">
          <hr>
          <div class="tab_row tab_inv_c">{{ row.club_name }}</div>
          <div class="tab_row tab_inv_c">{{ new Date(row.creation_date).toLocaleDateString('fr-FR') }}</div>
          <div class="tab_row tab_inv_c">{{ row.nb_match_played }}</div>
          <div class="tab_row tab_inv_c">{{ row.nb_player }}</div>
          <div class="tab_btn tab_inv_c">
            <v-btn dark small rounded color="#666" v-on:click="declineClubInv(row.player, row.club, row.club_name)">Refuser</v-btn>
          </div>
          <div class="tab_btn tab_inv_c">
            <v-btn dark small rounded color="#666" v-on:click="acceptClubInv(row.player, row.club, row.club_name)">Accepter</v-btn>
          </div>
        </div>
        <br>

        <div class="pages">
          <div class="numeric_icon" v-on:click="page_left_inv_c()"><v-icon color="black">mdi-chevron-left</v-icon></div>
          <!-- <div class="numeric_icon" v-if="this.InvitationsClubPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
          <div style="display: inline-block;" v-for="index in this.InvitationsClubPageMax" :key="index">
            <div v-bind:id="'c'+index" class="numeric_icon" v-on:click="page_inv_c(index)">{{ index }}</div>
          </div>
          <div class="numeric_icon" v-on:click="page_right_inv_c()"><v-icon color="black">mdi-chevron-right</v-icon></div>
        </div>
      </div>

      <div v-else>
        <hr><br>
        <div class="tab_nothing">Vous n'avez pas d'invitations dans un club</div>
      </div>
    </div>

    <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->
  </div>
  <WorkInProgress v-else></WorkInProgress>
</template>

<style>
  .pages{
    text-align: center;
  }
  .numeric_icon{
    margin: 2px;
    display: inline-block;
    background-color: white;
    color: black;
    border-radius: 5px;
    width: 30px;
    height: 30px;
  }
  .my_card{
    margin-top: 1%;
    margin-left: 2%;
    margin-right: 2%;
    margin-bottom: 1%;
    background-color: black;
    border-radius: 5px;
    box-shadow: 0px 4px 8px #AAA;
  }
  .title{
    color: white;
    font-size: 200%;
    margin: 5px;
  }
  .tab_nothing{
    text-align: center;
    font-weight: bold;
    color: white;
  }
  .tab_head, .tab_row, .tab_btn{
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-top: 5px;
    margin-left: 5px;
  }
  .tab_head{
    font-weight: bold;
    color: white;
  }
  .tab_row{
    color: #CCC;
    font-size: 120%;
  }
  .tab_inv{
    width: 16%;
  }
  .tab_inv_c{
    width: 16%;
  }
</style>

<script>
import WorkInProgress from "./WorkInProgress";
import axios from "axios";
import store from "../store";

export default {
  components: {
    WorkInProgress
  },
  data() {
    return {
      //////////////////////////////////////////////////////////////////////////
      InvitationsToShow: {},
      InvitationsNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      InvitationsPage: 1,
      InvitationsPageMax: 1,
      InvitationsNbRowPerPage: 5,
      InvitationsNbRow: 0,
      InvitationsToShowPage: [],
      //////////////////////////////////////////////////////////////////////////
      InvitationsClubToShow: {},
      InvitationsClubNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      InvitationsClubPage: 1,
      InvitationsClubPageMax: 1,
      InvitationsClubNbRowPerPage: 5,
      InvitationsClubNbRow: 0,
      InvitationsClubToShowPage: [],
      //////////////////////////////////////////////////////////////////////////
    };
  },
  async created() {
    const player = await axios.get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBPNotDecided/" + this.id, {responseType: "json"});
    this.InvitationsToShow = player.data.rows;
    this.InvitationsNbRow = player.data.rowCount;
    this.InvitationsPageMax = Math.floor((player.data.rowCount -1) / this.InvitationsNbRowPerPage) + 1;
    for (let i = 0; i < Math.min(this.InvitationsNbRowPerPage, this.InvitationsNbRow); i++){
      this.InvitationsToShowPage.push(this.InvitationsToShow[i]);
    }
    document.getElementById("i1").style.backgroundColor = "orange";

    const invitationsClub = await axios.get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + this.id, {responseType: "json"});
    this.InvitationsClubToShow = invitationsClub.data.rows;
    this.InvitationsClubNbRow = invitationsClub.data.rowCount;
    this.InvitationsClubPageMax = Math.floor((invitationsClub.data.rowCount -1) / this.InvitationsClubNbRowPerPage) + 1;
    for (let i = 0; i < Math.min(this.InvitationsClubNbRowPerPage, this.InvitationsClubNbRow); i++){
      this.InvitationsClubToShowPage.push(this.InvitationsClubToShow[i]);
    }
    document.getElementById("c1").style.backgroundColor = "orange";
  },
  methods: {
    ////////////////////////////////////////////////////////////////////////////
    async inv_tab_refresh(){
      const player = await axios.get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarBPNotDecided/" + this.id, {responseType: "json"});

      this.InvitationsToShow = player.data.rows;
      this.InvitationsNbRow = player.data.rowCount;
      this.InvitationsPageMax = Math.floor((this.InvitationsNbRow -1) / this.InvitationsNbRowPerPage) + 1;
    },
    async inv_c_tab_refresh(){
      const invitationsClub = await axios.get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + this.id, {responseType: "json"});

      this.InvitationsClubToShow = invitationsClub.data.rows;
      this.InvitationsClubNbRow = invitationsClub.data.rowCount;
      this.InvitationsClubPageMax = Math.floor((invitationsClub.data.rowCount -1) / this.InvitationsClubNbRowPerPage) + 1;
    },
    ////////////////////////////////////////////////////////////////////////////
    page_left_inv(){
      if (this.InvitationsPage != 1){
        this.page_inv(this.InvitationsPage - 1);
      }
    },
    page_right_inv(){
      if (this.InvitationsPage != this.InvitationsPageMax){
        this.page_inv(this.InvitationsPage + 1);
      }
    },
    page_inv(n){
      document.getElementById('i' + this.InvitationsPage).style.backgroundColor = "white";
      this.InvitationsPage = n;
      document.getElementById('i' + this.InvitationsPage).style.backgroundColor = "orange";
      let len = this.InvitationsToShowPage.length
      for (let i = 0; i < len; i++){
        this.InvitationsToShowPage.pop();
      }
      for (let i = (this.InvitationsPage - 1) * this.InvitationsNbRowPerPage; i < Math.min(this.InvitationsNbRow, this.InvitationsPage * this.InvitationsNbRowPerPage); i++){
        this.InvitationsToShowPage.push(this.InvitationsToShow[i]);
      }
    },
    //
    DayInFrench: function(day) {
      if (day == "Monday") return "Lundi";
      if (day == "Tuesday") return "Mardi";
      if (day == "Wednesday") return "Mercredi";
      if (day == "Friday") return "Vendredi";
      if (day == "Saturday") return "Samedi";
      if (day == "Sunday") return "dimanche";
      if (day == "Thursday") return "jeudi";
    },
    acceptInv: async function(mid) {
      let invID;
      let apiRep = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMInv/" + this.id + "/" + mid, { responseType: "json" });
      invID = apiRep.data.rows[0].id;
      await axios
        .put("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Invitations/" + invID, {status: true})
        .then(response => {
          alert("Match accepté !");
          let n_page = this.InvitationsPage;
          if (this.InvitationsToShowPage.length == 1 && n_page != 1){
            n_page--;
          }
          document.getElementById('i' + this.InvitationsPage).style.backgroundColor = "white";
          this.inv_tab_refresh().then(response => {
            this.page_inv(n_page);
          });
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    declineInv: async function(mid) {
      let invID;
      let apiRep = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/CalendarMInv/" + this.id + "/" + mid, { responseType: "json" });
      invID = apiRep.data.rows[0].id;
      await axios
        .put("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Invitations/" + invID, {status: false})
        .then(response => {
          alert("Match refusé !");
          let n_page = this.InvitationsPage;
          if (this.InvitationsToShowPage.length == 1 && n_page != 1){
            n_page--;
          }
          document.getElementById('i' + this.InvitationsPage).style.backgroundColor = "white";
          this.inv_tab_refresh().then(response => {
            this.page_inv(n_page);
          });
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    ////////////////////////////////////////////////////////////////////////////
    page_left_inv_c(){
      if (this.InvitationsClubPage != 1){
        this.page_inv_c(this.InvitationsClubPage - 1);
      }
    },
    page_right_inv_c(){
      if (this.InvitationsClubPage != this.InvitationsClubPageMax){
        this.page_inv_c(this.InvitationsClubPage + 1);
      }
    },
    page_inv_c(n){
      document.getElementById('c' + this.InvitationsClubPage).style.backgroundColor = "white";
      this.InvitationsClubPage = n;
      document.getElementById('c' + this.InvitationsClubPage).style.backgroundColor = "orange";
      let len = this.InvitationsClubToShowPage.length
      for (let i = 0; i < len; i++){
        this.InvitationsClubToShowPage.pop();
      }
      for (let i = (this.InvitationsClubPage - 1) * this.InvitationsClubNbRowPerPage; i < Math.min(this.InvitationsClubNbRow, this.InvitationsClubPage * this.InvitationsClubNbRowPerPage); i++){
        this.InvitationsClubToShowPage.push(this.InvitationsClubToShow[i]);
      }
    },
    //
    acceptClubInv: async function(pid, cid, club_name) {
      await axios
        .delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + pid + "&" + cid)
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
      await axios
        .post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs",
          {
            club: cid,
            player: pid,
            is_admin: false
          })
        .then(response => {
          alert("Invitation du club '" + club_name + "' accepté !");
          let n_page = this.InvitationsClubPage;
          if (this.InvitationsClubToShowPage.length == 1 && n_page != 1){
            n_page--;
          }
          document.getElementById('c' + this.InvitationsClubPage).style.backgroundColor = "white";
          this.inv_c_tab_refresh().then(response => {
            this.page_inv_c(n_page);
          });
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    declineClubInv: async function(pid, cid, club_name) {
      await axios
        .delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + pid + "&" + cid)
        .then(response => {
          alert("Invitation du club '" + club_name + "' refusé !");
          let n_page = this.InvitationsClubPage;
          if (this.InvitationsClubToShowPage.length == 1 && n_page != 1){
            n_page--;
          }
          document.getElementById('c' + this.InvitationsClubPage).style.backgroundColor = "white";
          this.inv_c_tab_refresh().then(response => {
            this.page_inv_c(n_page);
          });
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
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
