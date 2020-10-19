<template>
  <div>
    <div v-if="isSignedIn">
      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

      <div v-if="switch_menu == 'p'">
        <div class="my_card">
          <v-form v-model="valid1">
            <div class="title">Créer un club</div>
            <br>
            <v-col class="py-0" cols="4" md="12">
              <v-text-field dark required v-model="club_name" :rules="club_nameRules" :counter="50" label="Nom du club" outlined filled></v-text-field>
              <v-checkbox dark v-model="private_club" label="Club privé"></v-checkbox>
            </v-col>
            <div style="text-align: right; margin-right: 5px;">
              <v-btn dark rounded :disabled="!valid1" align="left" color="#666" v-on:click="create_club">Créer le club</v-btn>
            </div>
          </v-form>

          <br><br>
        </div>

        <div class="my_card">
          <div class="title">Vos clubs</div>
          <br>

          <div v-if="clubsInToShow.length != 0">
            <div class="tab_head tab_v_clubs">Nom du Club</div>
            <div class="tab_head tab_v_clubs">Date de création</div>
            <div class="tab_head tab_v_clubs">Nombre de joueur(s)</div>
            <div class="tab_head tab_v_clubs">Nombre de match(s) joué(s)</div>
            <div class="tab_head tab_v_clubs">Club privé</div>
            <div class="tab_head tab_v_clubs">Administrateur</div>
            <div class="tab_head tab_v_clubs">Quitter le club</div>
            <div class="tab_head tab_v_clubs">Gérer le club</div>

            <div v-for="row in clubsInToShowPage" :key="row.id">
              <hr>
              <div class="tab_row tab_v_clubs">{{ row.club_name }}</div>
              <div class="tab_row tab_v_clubs">{{ new Date(row.creation_date).toLocaleDateString('fr-FR') }}</div>
              <div class="tab_row tab_v_clubs">{{ row.nb_player }}</div>
              <div class="tab_row tab_v_clubs">{{ row.nb_match_played }}</div>
              <div v-if="row.private_club" class="tab_row tab_v_clubs">oui</div>
              <div v-else class="tab_row tab_v_clubs">non</div>
              <div v-if="row.is_admin" class="tab_row tab_v_clubs">oui</div>
              <div v-else class="tab_row tab_v_clubs">non</div>
              <div class="tab_btn tab_v_clubs">
                <v-btn dark small rounded color="#666" v-on:click="leave_club(row.id, row.club_name, row.is_admin)">Quitter le club</v-btn>
              </div>
              <div class="tab_btn tab_v_clubs" v-if="row.is_admin" >
                <v-btn dark small rounded color="#666" v-on:click="manage_club_menu(row.id, row.is_admin).then(response => {page_gc(1);})">Gérer le club</v-btn>
              </div>
              <div class="tab_btn tab_v_clubs" v-else >
                <v-btn dark small rounded color="#666" v-on:click="manage_club_menu(row.id, row.is_admin).then(response => {page_gc(1);})">Voir le club</v-btn>
              </div>
            </div>
            <br>

            <div class="pages">
              <div class="numeric_icon" v-on:click="page_left_vc()"><v-icon color="black">mdi-chevron-left</v-icon></div>
              <!-- <div class="numeric_icon" v-if="this.clubsInPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
              <div style="display: inline-block;" v-for="index in this.clubsInPageMax" :key="index">
                <div v-bind:id="index" class="numeric_icon" v-on:click="page_vc(index)">{{ index }}</div>
              </div>
              <div class="numeric_icon" v-on:click="page_right_vc()"><v-icon color="black">mdi-chevron-right</v-icon></div>
            </div>
          </div>

          <div v-else>
            <hr><br>
            <div class="tab_nothing">Vous n'êtes dans aucun club</div>
          </div>

          <br>
          <div style="text-align: right; margin-right: 5px;">
            <v-btn dark rounded align="left" color="#666" v-on:click="join_club_menu().then(response => {page_rc(1);})">Rejoindre un club</v-btn>
          </div>
          <br>
        </div>
      </div>

      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

      <div v-if="switch_menu == 'r'">
        <div class="my_card">
          <div style="margin-left: 5px; padding-top: 5px;">
            <v-btn dark small rounded align="left" color="#666" v-on:click="main_menu().then(response => {page_vc(1);})">Retourner au menu principale</v-btn>
          </div>
          <div class="title">Rejoindre un club</div>

          <div v-if="clubsNotInToShow.length != 0">
            <div class="tab_head tab_r_clubs">Nom du Club</div>
            <div class="tab_head tab_r_clubs">Date de création</div>
            <div class="tab_head tab_r_clubs">Nombre de joueur(s)</div>
            <div class="tab_head tab_r_clubs">Nombre de match(s) joué(s)</div>
            <div class="tab_head tab_r_clubs">Demander à rejoindre</div>

            <div v-for="row in clubsNotInToShowPage" :key="row.id">
              <hr>
              <div class="tab_row tab_r_clubs">{{ row.club_name }}</div>
              <div class="tab_row tab_r_clubs">{{ new Date(row.creation_date).toLocaleDateString('fr-FR') }}</div>
              <div class="tab_row tab_r_clubs">{{ row.nb_player }}</div>
              <div class="tab_row tab_r_clubs">{{ row.nb_match_played }}</div>
              <div class="tab_btn tab_r_clubs">
                <v-btn dark small rounded color="#666" v-on:click="join_club(row.id, row.club_name)">Demander à rejoindre</v-btn>
              </div>
            </div>
            <br>

            <div class="pages">
              <div class="numeric_icon" v-on:click="page_left_rc()"><v-icon color="black">mdi-chevron-left</v-icon></div>
              <!-- <div class="numeric_icon" v-if="this.clubsInPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
              <div style="display: inline-block;" v-for="index in this.clubsNotInPageMax" :key="index">
                <div v-bind:id="index" class="numeric_icon" v-on:click="page_rc(index)">{{ index }}</div>
              </div>
              <div class="numeric_icon" v-on:click="page_right_rc()"><v-icon color="black">mdi-chevron-right</v-icon></div>
            </div>
          </div>

          <div v-else>
            <hr><br>
            <div class="tab_nothing">Aucun club disponible</div>
          </div>
        </div>
      </div>

      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

      <div v-if="switch_menu == 'g'">
        <div class="my_card">
          <div style="margin-left: 5px; padding-top: 5px; display: inline-block">
            <v-btn dark small rounded align="left" color="#666" v-on:click="main_menu().then(response => {page_vc(1);})">Retourner au menu principale</v-btn>
          </div>
          <div v-if="!update_club && admin_club_switch" style="margin-right: 5px; padding-top: 5px; display: inline-block; float: right">
            <v-btn dark small rounded align="right" color="#666" v-on:click="update_club_btn()">Modifier le club</v-btn>
          </div>
          <div v-if="update_club && admin_club_switch" style="margin-right: 5px; padding-top: 5px; display: inline-block; float: right">
            <v-btn dark small rounded align="right" color="#666" v-on:click="update_club_btn()">Annuler</v-btn>
          </div>

          <div v-if="update_club">
            <br><hr>
            <v-form v-model="valid">
              <div class="title">Modifier le nom et/ou statut du club</div>
              <br>
              <v-col class="py-0" cols="4" md="12">
                <v-text-field dark v-model="club_name_update" :rules="club_nameRules" :counter="50" label="Nom du club" outlined filled></v-text-field>
                <v-checkbox dark v-model="private_club_update" label="Club privé"></v-checkbox>
              </v-col>
            </v-form>
            <div style="text-align: right; margin-right: 5px;">
              <v-btn dark rounded align="left" color="#666" v-on:click="update_club_confirm()">Confirmer</v-btn>
            </div>
            <br><hr>
          </div>

          <div class="title">Vos statistiques au sein du club {{ name_club_switch }}</div>
          <br>

          <hr>
          <div class="tab_head tab_g_stat_clubs">Nom</div>
          <div class="tab_head tab_g_stat_clubs">Prénom</div>
          <div class="tab_head tab_g_stat_clubs">Nombre de but(s)</div>
          <div class="tab_head tab_g_stat_clubs">Nombre de but(s) encaissé(s)</div>
          <div class="tab_head tab_g_stat_clubs">Nombre de match joué(s)</div>
          <div class="tab_head tab_g_stat_clubs">Nombre de victoire(s)</div>

          <br>
          <div class="tab_row tab_g_stat_clubs">{{ playersInClubStatToShow.surname }}</div>
          <div class="tab_row tab_g_stat_clubs">{{ playersInClubStatToShow.first_name }}</div>
          <div class="tab_row tab_g_stat_clubs">{{ playersInClubStatToShow.scored_goals_club }}</div>
          <div class="tab_row tab_g_stat_clubs">{{ playersInClubStatToShow.conceded_goals_club }}</div>
          <div class="tab_row tab_g_stat_clubs">{{ playersInClubStatToShow.matches_played_club }}</div>
          <div class="tab_row tab_g_stat_clubs">{{ playersInClubStatToShow.victories_club }}</div>
        </div>

        <div class="my_card">
          <div class="title">Joueurs du club {{ name_club_switch }}</div>
          <br>

          <div class="tab_head tab_g_clubs">Nom</div>
          <div class="tab_head tab_g_clubs">Prénom</div>
          <div class="tab_head tab_g_clubs">Pseudo</div>
          <div class="tab_head tab_g_clubs">Nombre de but(s)</div>
          <div class="tab_head tab_g_clubs">Nombre de but(s) encaissé(s)</div>
          <div class="tab_head tab_g_clubs">Nombre de match joué(s)</div>
          <div class="tab_head tab_g_clubs">Nombre de victoire(s)</div>
          <div class="tab_head tab_g_clubs">Nommer admin</div>
          <div class="tab_head tab_g_clubs">Supprimer</div>

          <div v-for="row in playersInClubToShowPage" :key="row.id">
            <hr>
            <div class="tab_row tab_g_clubs">{{ row.surname }}</div>
            <div class="tab_row tab_g_clubs">{{ row.first_name }}</div>
            <div class="tab_row tab_g_clubs">{{ row.pseudo }}</div>
            <div class="tab_row tab_g_clubs">{{ row.scored_goals_club }}</div>
            <div class="tab_row tab_g_clubs">{{ row.conceded_goals_club }}</div>
            <div class="tab_row tab_g_clubs">{{ row.matches_played_club }}</div>
            <div class="tab_row tab_g_clubs">{{ row.victories_club }}</div>
            <div class="tab_btn tab_g_clubs" v-if="! row.is_admin && admin_club_switch">
              <v-btn dark small rounded color="#666" v-on:click="promote_to_admin(row.id, id_club_switch, row.pseudo)">Nommer admin</v-btn>
            </div>
            <div class="tab_btn tab_g_clubs" v-if="! row.is_admin && admin_club_switch" >
              <v-btn dark small rounded color="#666" v-on:click="delete_from_club(row.id, id_club_switch, row.pseudo)">Supprimer</v-btn>
            </div>
          </div>
          <br>

          <div class="pages">
            <div class="numeric_icon" v-on:click="page_left_gc()"><v-icon color="black">mdi-chevron-left</v-icon></div>
            <!-- <div class="numeric_icon" v-if="this.clubsInPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
            <div style="display: inline-block;" v-for="index in this.playersInClubPageMax" :key="index">
              <div v-bind:id="index" class="numeric_icon" v-on:click="page_gc(index)">{{ index }}</div>
            </div>
            <div class="numeric_icon" v-on:click="page_right_gc()"><v-icon color="black">mdi-chevron-right</v-icon></div>
          </div>

          <br>
          <div v-if="admin_club_switch" style="text-align: right; margin-right: 5px;">
            <v-btn dark rounded align="left" color="#666" v-on:click="add_club_menu().then(response => {page_ac(1);})">Ajouter un joueur</v-btn>
          </div>
          <br>
        </div>
      </div>

      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->

      <div v-if="switch_menu == 'a'">
        <div class="my_card">
          <div style="margin-left: 5px; padding-top: 5px;">
            <v-btn dark small rounded align="left" color="#666" v-on:click="main_menu().then(response => {page_vc(1);})">Retourner au menu principale</v-btn>
          </div>
          <div style="margin-left: 5px; margin-top: 5px;">
            <v-btn dark small rounded align="left" color="#666" v-on:click="manage_club_menu(id_club_switch, admin_club_switch).then(response => {page_gc(1);})">Retourner au menu de gestion du club "{{ name_club_switch }}"</v-btn>
          </div>

          <v-form v-model="valid2">
            <div class="title">Ajouter un joueur qui n'est pas encore membre de Quickmatch</div>
            <br>

            <v-col class="py-0" cols="4" md="12">
              <v-text-field dark required v-model="add_player_email" :rules="emailRules" :counter="50" label="Email" outlined filled></v-text-field>
            </v-col>
            <div style="text-align: right; margin-right: 5px;">
              <v-btn dark rounded :disabled="!valid2" align="left" color="#666" v-on:click="add_to_club_by_email().then(response => {add_player_email = '';})">Envoyer</v-btn>
            </div>
          </v-form>

          <br><hr><br>
          <div class="title">Ajouter un joueur</div>
          <br>

          <div v-if="playersNotInClubToShow.length != 0">
            <div class="tab_head tab_a_clubs">Nom</div>
            <div class="tab_head tab_a_clubs">Prénom</div>
            <div class="tab_head tab_a_clubs">Pseudo</div>
            <div class="tab_head tab_a_clubs">Nombre de but(s)</div>
            <div class="tab_head tab_a_clubs">Nombre de but(s) encaissé(s)</div>
            <div class="tab_head tab_a_clubs">Nombre de match joué(s)</div>
            <div class="tab_head tab_a_clubs">Nombre de victoire(s)</div>
            <div class="tab_head tab_a_clubs">Ajouter</div>

            <div v-for="row in playersNotInClubToShowPage" :key="row.id">
              <hr>
              <div class="tab_row tab_a_clubs">{{ row.surname }}</div>
              <div class="tab_row tab_a_clubs">{{ row.first_name }}</div>
              <div class="tab_row tab_a_clubs">{{ row.pseudo }}</div>
              <div class="tab_row tab_a_clubs">{{ row.scored_goals }}</div>
              <div class="tab_row tab_a_clubs">{{ row.conceded_goals }}</div>
              <div class="tab_row tab_a_clubs">{{ row.matches_played }}</div>
              <div class="tab_row tab_a_clubs">{{ row.victories }}</div>
              <div class="tab_btn tab_a_clubs">
                <v-btn dark small rounded color="#666" v-on:click="add_to_club(row.id, id_club_switch, row.pseudo)">Ajouter</v-btn>
              </div>
            </div>
            <br>

            <div class="pages">
              <div class="numeric_icon" v-on:click="page_left_ac()"><v-icon color="black">mdi-chevron-left</v-icon></div>
              <!-- <div class="numeric_icon" v-if="this.clubsInPage > "><v-icon color="black">mdi-dots-horizontal</v-icon></div> -->
              <div style="display: inline-block;" v-for="index in this.playersNotInClubPageMax" :key="index">
                <div v-bind:id="index" class="numeric_icon" v-on:click="page_ac(index)">{{ index }}</div>
              </div>
              <div class="numeric_icon" v-on:click="page_right_ac()"><v-icon color="black">mdi-chevron-right</v-icon></div>
            </div>
          </div>

          <div v-else>
            <hr><br>
            <div class="tab_nothing">Aucun joueur disponible</div>
          </div>
        </div>
      </div>

      <!-- /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// -->
    </div>
    <Not-connected v-else></Not-connected>
  </div>
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
  .tab_v_clubs{
    width: 12%;
  }
  .tab_r_clubs{
    width: 19%;
  }
  .tab_g_stat_clubs{
    width: 16%;
  }
  .tab_g_clubs{
    width: 10.6%;
  }
  .tab_a_clubs{
    width: 12.2%;
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
      clubsInToShow: {},
      clubsInNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      clubsInPage: 1,
      clubsInPageMax: 1,
      clubsInNbRowPerPage: 5,
      clubsInNbRow: 0,
      clubsInToShowPage: [],
      //////////////////////////////////////////////////////////////////////////
      clubsNotInToShow: {},
      clubsNotInNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      clubsNotInPage: 1,
      clubsNotInPageMax: 1,
      clubsNotInNbRowPerPage: 5,
      clubsNotInNbRow: 0,
      clubsNotInToShowPage: [],
      //////////////////////////////////////////////////////////////////////////
      playersInClubStatToShow: {},
      //
      playersInClubToShow: {},
      playersInClubNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      playersInClubPage: 1,
      playersInClubPageMax: 1,
      playersInClubNbRowPerPage: 5,
      playersInClubNbRow: 0,
      playersInClubToShowPage: [],
      //////////////////////////////////////////////////////////////////////////
      playersNotInClubToShow: {},
      playersNotInClubNbPageMenu: 5, // si nombre plus grand que 4, utilisation des ..., ex les btn du bas : " < 1 ... 5 6 7 8 9 ... 18 > "
      playersNotInClubPage: 1,
      playersNotInClubPageMax: 1,
      playersNotInClubNbRowPerPage: 5,
      playersNotInClubNbRow: 0,
      playersNotInClubToShowPage: [],
      //////////////////////////////////////////////////////////////////////////
      /**possible value of switch_menu
       * 'p' (principale) : vos club + creer un club
       * 'r' (rejoindre) : rejoindre un club
       * 'g' (gerer) : gerer les joueurs de son club
       * 'a' (ajouter) : ajouter des joueurs à son club
       */
      switch_menu: 'p',
      //////////////////////////////////////////////////////////////////////////
      valid1: false,
      valid2: false,
      club_name: null,
      private_club: false,
      //
      add_player_email: "",
      //
      club_nameRules: [
        v => !!v || "Nom de club requis",
        v => v.length >= 2 || "Nom de club trop court",
        v =>
          /^[a-zA-Z0-9_ \-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
          "Nom de club invalide"
      ],
      pseudoRules: [
        v => !!v || "Pseudo requis",
        v => v.length >= 2 || "Pseudo trop court",
        v =>
          /^[a-zA-Z0-9 _\-éèçîïœžâêôàûùâãäåæçëìíîïðñòóôõúûüýö]+$/.test(v) ||
          'Pseudo invalide (lettres, nombres, espace, "_" et "-" seulement)'
      ],
      emailRules: [
        v => !!v || 'E-mail requis',
        v => /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/.test(v) || 'E-mail invalide'
      ],
      //
      id_club_switch: -1,
      name_club_switch: "",
      admin_club_switch: false,
      private_club_switch: false,
      //
      update_club: false,
      club_name_update: null,
      private_club_update: false
    };
  },
  async created() {
    var clubsIn = await axios
      .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/pid" + this.id, {responseType: "json"})
      .catch(e => {
        if (this.isSignedIn()) {
          alert("Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page");
          this.$router.go();
        }
      });
    this.clubsInToShow = clubsIn.data.rows;
    this.clubsInNbRow = clubsIn.data.rowCount;
    this.clubsInPageMax = Math.floor((clubsIn.data.rowCount -1) / this.clubsInNbRowPerPage) + 1;
    for (let i = 0; i < Math.min(this.clubsInNbRowPerPage, this.clubsInNbRow); i++){
      this.clubsInToShowPage.push(this.clubsInToShow[i]);
    }
    document.getElementById("1").style.backgroundColor = "orange";
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
    ////////////////////////////////////////////////////////////////////////////
    async main_menu(){
      let clubsIn = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/pid" + this.id, {responseType: "json"})
        .catch(e => {
          alert("Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page");
          this.$router.go();
        });
      this.clubsInToShow = clubsIn.data.rows;
      this.clubsInNbRow = clubsIn.data.rowCount;
      this.clubsInPageMax = Math.floor((clubsIn.data.rowCount -1) / this.clubsInNbRowPerPage) + 1;
      this.switch_menu = 'p';
    },
    page_left_vc(){
      if (this.clubsInPage != 1){
        this.page_vc(this.clubsInPage - 1);
      }
    },
    page_right_vc(){
      if (this.clubsInPage != this.clubsInPageMax){
        this.page_vc(this.clubsInPage + 1);
      }
    },
    page_vc(n){
      document.getElementById(this.clubsInPage).style.backgroundColor = "white";
      this.clubsInPage = n;
      document.getElementById(this.clubsInPage).style.backgroundColor = "orange";
      let len = this.clubsInToShowPage.length
      for (let i = 0; i < len; i++){
        this.clubsInToShowPage.pop();
      }
      for (let i = (this.clubsInPage - 1) * this.clubsInNbRowPerPage; i < Math.min(this.clubsInNbRow, this.clubsInPage * this.clubsInNbRowPerPage); i++){
        this.clubsInToShowPage.push(this.clubsInToShow[i]);
      }
    },
    //
    async create_club(){
      let apiRep = null;
      apiRep = await axios
        .post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Clubs", {
          club_name: this.club_name,
          private_club: this.private_club
        })
        .catch(e => {
          if (this.name_already_exist(this.club_name)) {
            alert("un club porte déjà le même nom, choisissez-en un autre");
          }
          else{
            alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
            this.$router.go();
          }
        });
      let cid = apiRep.data.id;
      let apiRep1 = null;
      apiRep1 = await axios
        .post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs", {
          club: cid,
          player: this.id,
          is_admin: true
        })
        .then(response => {
          alert("Club " + this.club_name + " créé !");
          let n_page = this.clubsInPage;
          this.main_menu().then(response => {
            this.page_vc(n_page);
          });
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    name_already_exist(club_name){
      for (var row_in in this.clubsInToShow){
        if (this.clubsInToShow[row_in]["club_name"] == club_name){
          return true;
        }
      }
      for (var row_not_in in this.clubsNotInToShow){
        if (this.clubsNotInToShow[row_not_in]["club_name"] == club_name){
          return true;
        }
      }
      return false;
    },
    async leave_club(cid, club_name, is_admin){
      let count_admin = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubsCountAdmin/" + cid, {responseType: "json"})
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
      let nb = 0;
      if (count_admin.data.rowCount != 0){
        nb = count_admin.data.rows[0]["nb"];
      }
      let destroy_club = true;
      if (nb == 1 && is_admin) {
        if (!confirm("vous êtes le dernier admin de ce club, le quitter le détruira, souhaitez vous continuer ?")){
          destroy_club = false;
        }
      }
      if (destroy_club) {
        await axios
          .delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/" + cid + "&" + this.id)
          .then(response => {
            alert("Vous avez quitté le club " + club_name);
          })
          .catch(e => {
            alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
            this.$router.go();
          });
        if (nb == 1 && is_admin) {
          await axios
            .delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Clubs/" + cid)
            .then(response => {
              alert("Le club " + club_name + " a été détruit");
            });
        }
        let n_page = this.clubsInPage;
        document.getElementById(this.clubsInPage).style.backgroundColor = "white";
        if (this.clubsInToShowPage.length == 1 && n_page != 1){
          n_page--;
        }
        this.main_menu().then(response => {
          this.page_vc(n_page);
        });
      }
    },
    ////////////////////////////////////////////////////////////////////////////
    async join_club_menu(){
      let clubsNotIn = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/npid" + this.id, {responseType: "json"})
        .catch(e => {
          if (this.isSignedIn()){
            alert("Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page");
            this.$router.go();
          }
        });
      this.clubsNotInToShow = clubsNotIn.data.rows;
      this.clubsNotInNbRow = clubsNotIn.data.rowCount;
      this.clubsNotInPageMax = Math.floor((clubsNotIn.data.rowCount -1) / this.clubsNotInNbRowPerPage) + 1;
      this.switch_menu = 'r';
    },
    page_left_rc(){
      if (this.clubsNotInPage != 1){
        this.page_rc(this.clubsNotInPage - 1);
      }
    },
    page_right_rc(){
      if (this.clubsNotInPage != this.clubsNotInPageMax){
        this.page_rc(this.clubsNotInPage + 1);
      }
    },
    page_rc(n){
      document.getElementById(this.clubsNotInPage).style.backgroundColor = "white";
      this.clubsNotInPage = n;
      document.getElementById(this.clubsNotInPage).style.backgroundColor = "orange";
      let len = this.clubsNotInToShowPage.length
      for (let i = 0; i < len; i++){
        this.clubsNotInToShowPage.pop();
      }
      for (let i = (this.clubsNotInPage - 1) * this.clubsNotInNbRowPerPage; i < Math.min(this.clubsNotInNbRow, this.clubsNotInPage * this.clubsNotInNbRowPerPage); i++){
        this.clubsNotInToShowPage.push(this.clubsNotInToShow[i]);
      }
    },
    //
    async join_club(cid, club_name){
      await axios.delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + this.id + "&" + cid)
      .catch(e => {
        alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
        this.$router.go();
      });
      let apiRep1 = null;
      apiRep1 = await axios.post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs",{
          club: cid,
          player: this.id,
          is_admin: false
        }
      )
      .then(response => {
        alert("Vous avez rejoins le club " + club_name);
        let n_page = this.clubsNotInPage;
        document.getElementById(this.clubsNotInPage).style.backgroundColor = "white";
        if (this.clubsNotInToShowPage.length == 1 && n_page != 1){
          n_page--;
        }
        this.join_club_menu().then(response => {
          this.page_rc(n_page);
        });
      })
      .catch(e => {
        alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
        this.$router.go();
      });
    },
    ////////////////////////////////////////////////////////////////////////////
    async manage_club_menu(cid, is_admin){
      let club_info = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Clubs/" + cid, {responseType: "json"})
        .catch(e => {
          alert(
            "Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page"
          );
          this.$router.go();
        });
      this.name_club_switch = club_info.data.club_name;
      this.club_name_update = club_info.data.club_name;
      this.id_club_switch = cid;
      this.admin_club_switch = is_admin;
      this.private_club_switch = club_info.data.private_club;
      this.private_club_update = club_info.data.private_club;

      let playersIn = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/cid" + cid, {responseType: "json"})
        .catch(e => {
          alert(
            "Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page"
          );
          this.$router.go();
        });
      this.playersInClubToShow = playersIn.data.rows;
      this.playersInClubNbRow = playersIn.data.rowCount;
      this.playersInClubPageMax = Math.floor((playersIn.data.rowCount -1) / this.playersInClubNbRowPerPage) + 1;

      const player_stat = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/one" + this.id + "&" + cid, {responseType: "json"})
        .catch(e => {
          alert(
            "Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page"
          );
          this.$router.go();
        });
      this.playersInClubStatToShow = player_stat.data.rows[0];

      this.switch_menu = 'g';
    },
    page_left_gc(){
      if (this.playersInClubPage != 1){
        this.page_gc(this.playersInClubPage - 1);
      }
    },
    page_right_gc(){
      if (this.playersInClubPage != this.playersInClubPageMax){
        this.page_gc(this.playersInClubPage + 1);
      }
    },
    page_gc(n){
      document.getElementById(this.playersInClubPage).style.backgroundColor = "white";
      this.playersInClubPage = n;
      document.getElementById(this.playersInClubPage).style.backgroundColor = "orange";
      let len = this.playersInClubToShowPage.length
      for (let i = 0; i < len; i++){
        this.playersInClubToShowPage.pop();
      }
      for (let i = (this.playersInClubPage - 1) * this.playersInClubNbRowPerPage; i < Math.min(this.playersInClubNbRow, this.playersInClubPage * this.playersInClubNbRowPerPage); i++){
        this.playersInClubToShowPage.push(this.playersInClubToShow[i]);
      }
    },
    //
    async promote_to_admin(pid, cid, pseudo){
      await axios
      .put("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubsPromoteToAdmin/" + pid + "&" + cid)
      .then(response => {
        alert("Vous avez promu admin " + pseudo);
        let n_page = this.playersInClubPage;
        document.getElementById(this.playersInClubPage).style.backgroundColor = "white";
        this.manage_club_menu(this.id_club_switch, this.admin_club_switch).then(response => {
          this.page_gc(n_page);
        });
      })
      .catch(e => {
        alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
        this.$router.go();
      });
    },
    async delete_from_club(pid, cid, pseudo){
      await axios
      .delete("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/" + cid + "&" + pid)
      .then(response => {
        alert("Vous avez renvoyer " + pseudo + " du club " + this.name_club_switch);
        let n_page = this.playersInClubPage;
        document.getElementById(this.playersInClubPage).style.backgroundColor = "white";
        if (this.playersInClubToShowPage.length == 1 && n_page != 1){
          n_page--;
        }
        this.manage_club_menu(this.id_club_switch, this.admin_club_switch).then(response => {
          this.page_gc(n_page);
        });
      })
      .catch(e => {
        alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
        this.$router.go();
      });
    },
    async update_club_btn(){
      if (this.update_club){
        this.update_club = false;
      }
      else{
        this.update_club = true;
      }
    },
    async update_club_confirm(){
      await axios
        .put("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Clubs/" + this.id_club_switch + "&" + this.club_name_update + "&" + this.private_club_update)
        .then(response => {
          alert("Vos modification ont bien été pris en compte");
          this.update_club_btn();
          this.manage_club_menu(this.id_club_switch, this.admin_club_switch).then(response => {
            this.page_gc(this.playersInClubPage);
          });
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    ////////////////////////////////////////////////////////////////////////////
    async add_club_menu(){
      this.add_player_email = "";
      let playersNotIn = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/ncid" + this.id_club_switch, {responseType: "json"})
        .catch(e => {
          alert("Une erreur s'est produite, nous allons rafraichir la page, si le problème persiste, quittez la page");
          this.$router.go();
        });
      this.playersNotInClubToShow = playersNotIn.data.rows;
      this.playersNotInClubNbRow = playersNotIn.data.rowCount;
      this.playersNotInClubPageMax = Math.floor((playersNotIn.data.rowCount -1) / this.playersNotInClubNbRowPerPage) + 1;
      this.switch_menu = 'a';
    },
    page_left_ac(){
      if (this.playersNotInClubPage != 1){
        this.page_ac(this.playersNotInClubPage - 1);
      }
    },
    page_right_ac(){
      if (this.playersNotInClubPage != this.playersNotInClubPageMax){
        this.page_ac(this.playersNotInClubPage + 1);
      }
    },
    page_ac(n){
      document.getElementById(this.playersNotInClubPage).style.backgroundColor = "white";
      this.playersNotInClubPage = n;
      document.getElementById(this.playersNotInClubPage).style.backgroundColor = "orange";
      let len = this.playersNotInClubToShowPage.length
      for (let i = 0; i < len; i++){
        this.playersNotInClubToShowPage.pop();
      }
      for (let i = (this.playersNotInClubPage - 1) * this.playersNotInClubNbRowPerPage; i < Math.min(this.playersNotInClubNbRow, this.playersNotInClubPage * this.playersNotInClubNbRowPerPage); i++){
        this.playersNotInClubToShowPage.push(this.playersNotInClubToShow[i]);
      }
    },
    //
    async add_to_club(pid, cid, pseudo){
      await axios
        .post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + pid + "&" + cid)
        .then(response => {
          if (response.data.message == "invitation send"){
            alert("Une invitation a été envoyé à " + pseudo);
            let n_page = this.playersNotInClubPage;
            document.getElementById(this.playersNotInClubPage).style.backgroundColor = "white";
            if (this.playersNotInClubToShowPage.length == 1 && n_page != 1){
              n_page--;
            }
            this.add_club_menu().then(response => {
              this.page_ac(n_page);
            });
          }
          else{
            alert("Une invitation avait déjà été envoyé à " + pseudo);
            let n_page = this.playersNotInClubPage;
            document.getElementById(this.playersNotInClubPage).style.backgroundColor = "white";
            this.add_club_menu().then(response => {
              this.page_ac(n_page);
            });
          }
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    async add_to_club_by_email(){
      let actual_player = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Players/id" + this.id)
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
      let actual_name = actual_player.data.first_name;
      let actual_surname = actual_player.data.surname;

      let apiRep1 = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/Players/ma" + this.add_player_email)
        .then(response => {
          if (response.data.message == "player not found"){
            this.send_mail_to_add(actual_name, actual_surname);
          }
          else{
            this.already_in_club(response.data.id);
          }
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        })
    },
    async send_mail_to_add(name, surname){
      axios.post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/sendMail", {
        to: this.add_player_email, // list of receivers
        subject: "Invitation Quickmatch", // Subject line
        text:
          "[HTML NOT SUPPORTED] Bonjour, vous avez reçu une invitation de " + name + " " + surname + " dans le club " + this.name_club_switch +
          ". Vous pouvez venir vous inscrire sur https://www.quickmatch.fr/signin. A bientôt sur Quickmatch.fr !\n\n" +
          "Ce mail a été envoyé automatiquement, merci de ne pas y répondre.",
        html:
          "<p>Bonjour, vous avez reçu une invitation de " + name + " " + surname + " dans le club " + this.name_club_switch +
          '. Vous pouvez venir vous inscrire <a href="https://www.quickmatch.fr/signin">ici</a> . A bientôt sur Quickmatch.fr !<br><br></p>' +
          '<font size="-5"><i> Ce mail a été envoyé automatiquement, merci de ne pas y répondre.</i></front>'
      })
      .catch(e => {
        alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
        this.$router.go();
      });
      /*
      console.log(this.add_player_email);
      console.log(this.id_club_switch);
      axios.post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/MailInvitationClub/" + this.add_player_email + "&" + this.id_club_switch)
      .catch(e => {
        console.log(e);
        alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
        this.$router.go();
      });
      */

      alert("Un mail d'invitation a été envoyé à " + this.add_player_email);
    },
    async already_in_club(pid){
      let apiRep1 = await axios
        .get("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/PlayerClubs/pid" + pid)
        .then(response => {
          for (let i = 0; i < response.data.rowCount; i++){
            if (response.data.rows[i].id == this.id_club_switch){
              alert("Il semblerait qu'un joueur de votre club possède déjà cette email\n"
                    + "Peut être que vous ou un autre administrateur avez déjà invité cette personne");
              return;
            }
          }
          this.already_in_club_refresh(pid);
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    },
    async already_in_club_refresh(pid){
      await axios
        .post("https://dbcontrol.quickmatch.fr/dbcontrol/api/v1/InvitationClub/" + pid + "&" + this.id_club_switch)
        .then(response => {
          if (response.data.message == "invitation send"){
            alert("Cette personne est déjà un membre de Quickmatch, une invitation lui a été envoyée");
            let n_page = this.playersNotInClubPage;
            document.getElementById(this.playersNotInClubPage).style.backgroundColor = "white";
            if (this.playersNotInClubToShowPage.length == 1 && n_page != 1){
              n_page--;
            }
            this.add_club_menu().then(response => {
              this.page_ac(n_page);
            });
          }
          else{
            alert("Cette personne est déjà un membre de Quickmatch, de plus une invitation lui a déjà été envoyée");
            let n_page = this.playersNotInClubPage;
            document.getElementById(this.playersNotInClubPage).style.backgroundColor = "white";
            this.add_club_menu().then(response => {
              this.page_ac(n_page);
            });
          }
        })
        .catch(e => {
          alert("Echec, veuillez réessayer, si le problème persiste, réessayer plus tard");
          this.$router.go();
        });
    }
  }
};
</script>
