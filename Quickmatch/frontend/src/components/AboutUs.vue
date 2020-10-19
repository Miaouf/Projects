<template>
  <div id="app">
    <v-app id="inspire">
      <v-container fluid>
        <v-row justify="center">
          <v-expansion-panels popout>
            <v-expansion-panel v-for="(dev, i) in devs" :key="i" hide-actions>
              <v-expansion-panel-header>
                <v-row align="center" class="spacer" no-gutters>
                  <!-- avatar -->
                  <v-col cols="2" sm="1" md="1">
                    <v-avatar size="75px">
                      <img v-if="dev.avatar" alt="Avatar" v-bind:src="dev.avatar" />
                    </v-avatar>
                  </v-col>

                  <!-- linkedin -->
                  <v-col cols="2" sm="1" md="1">
                    <v-btn text icon color="blue" to:dev.link>
                      <v-icon>mdi-linkedin-box</v-icon>Linkedin
                    </v-btn>
                  </v-col>

                  <!-- name and status -->
                  <v-col class="hidden-xs-only" sm="4" md="4">
                    <strong v-html="dev.name"></strong>
                    <span v-if="dev.status" class="grey--text">&nbsp;({{ dev.status }})</span>
                  </v-col>

                  <!-- title -->
                  <v-col class="text-no-wrap" cols="4" sm="5">
                    <v-icon>{{dev.icon}}</v-icon>
                    <strong v-html="dev.title"></strong>
                  </v-col>
                </v-row>
              </v-expansion-panel-header>

              <!-- description -->
              <v-expansion-panel-content>
                <v-divider></v-divider>
                <v-card-text v-text="dev.desc"></v-card-text>
              </v-expansion-panel-content>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-row>
      </v-container>
    </v-app>
  </div>
</template>

<script>
export default {
  data() {
    return {
      devs: [
        {
          avatar:
            "https://media-exp1.licdn.com/dms/image/C4D03AQG9URzsgJrwgA/profile-displayphoto-shrink_200_200/0?e=1585180800&v=beta&t=WbIBAqM6AZUwf8TqmMnkZsrbtht1i8SXdQ0S2KWMGCM",
          name: "ADBERRAHMANE Faiz",
          title: "Developpeur backend",
          link: "https://www.linkedin.com/in/abderrahmane-faiz-3920a786/",
          desc:
            "Actuellement etudiant ingénieur en Informatique, je cherche toujours à élargir mes connaissances dans ce domaine passionant et riche.",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-database"
        },
        {
          avatar:
            "https://media-exp1.licdn.com/dms/image/C4D03AQEk_MsM4PeeUw/profile-displayphoto-shrink_200_200/0?e=1585180800&v=beta&t=P5auMSglM7xzYPTsSII9s4qRovADg2Z9TiVWiE8nHts",
          name: "ISSAOUI Ali",
          title: "Developpeur backend",
          link: "https://www.linkedin.com/in/ali-issaoui/",
          desc: "lorem ipsum",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-database"
        },
        {
          avatar:
            "https://media-exp1.licdn.com/dms/image/C4D03AQEn24hpEVISug/profile-displayphoto-shrink_200_200/0?e=1585180800&v=beta&t=J1zIKjzZSvCekDDEcdPrvkelXo1lHngFS9djTavC5m0",
          name: "ROSAY Maxime",
          title: "Developpeur backend",
          link: "https://www.linkedin.com/in/maxime-rosay-967520170/",
          desc:
            "Étudiant en seconde année d'école d'ingénieur filière informatique, je m'intéresse notamment à la cybersécurité. ",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-database"
        },
        {
          avatar:
            "https://media-exp1.licdn.com/dms/image/C5603AQGM59MkTKPNBw/profile-displayphoto-shrink_200_200/0?e=1585180800&v=beta&t=SFv-fjSrVqMsTpIn2QqfSh-jTJc5zE134w7lq_yUtfg",
          name: "CHATAIGNER Johan",
          title: "Developpeur mobile & frontend",
          link: "https://www.linkedin.com/in/johan-chataigner-b9970a151/",
          desc:
            "Mon centre d'intérêt actuel est l'informatique dans tout ses domaines. Je commence tout juste à étudier les différentes branche du domaine telles que les bases de données, le Web, etc...",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-android-debug-bridge"
        },
        {
          avatar:
            "https://media-exp1.licdn.com/dms/image/C5603AQETBsQ-P7MqjA/profile-displayphoto-shrink_200_200/0?e=1585180800&v=beta&t=CPfdnsa5aFQdvNusrpkjvIp25Aa3-lNvZTywxu_h89Y",
          name: "GENTY Laurent",
          title: "Developpeur mobile & frontend",
          link: "https://www.linkedin.com/in/laurentgenty/",
          desc:
            "Actuellement en formation d'ingénieur en informatique à l'ENSEIRB Matmeca, je suis passionné par les technologies de l'informatique et la programmation. \
          J'ai toujours aimé utiliser les ordinateurs depuis ma tendre enfance, et aujourd'hui je suis à l'aube de concrétiser mon projet professionnel informatique à savoir travailler dans le domaine des jeux-vidéo ou le développement d'applications mobiles. \
          Ambition, rigueur et organisation sont mes mots d'ordre et je mettrais tout en oeuvre afin de remplir mes missions. ",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-android-debug-bridge"
        },
        {
          avatar:
            "https://scontent-mrs2-1.xx.fbcdn.net/v/t1.0-1/p240x240/73325257_1365645796933505_8179656737243004928_o.jpg?_nc_cat=104&_nc_ohc=HCtUuilaLFwAX8SIbML&_nc_ht=scontent-mrs2-1.xx&_nc_tp=1002&oh=13759502b3ca04e1094e102ee0f3b4e7&oe=5E9DE5EE",
          name: "HALA Mehdi",
          title: "Developpeur fullstack JS",
          link: "https://www.linkedin.com/in/mehdi-hala-981092137/",
          desc:
            "Actuellement en 2ème année de formation d'ingénieur en informatique à l'ENSEIRB MATMECA, je suis passionné par l'informatique et surtout la programmation (C, Python, Racket...).",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-all-inclusive"
        },
        {
          avatar:
            "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
          name: "DANQUIGNY Antoine",
          title: "Developpeur fullstack JS",
          link: "https://www.linkedin.com/in/antoine-danquigny-93a1a9170/",
          desc: "lorem ipsum",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-all-inclusive"
        },
        {
          avatar:
            "https://scontent-mrs2-2.xx.fbcdn.net/v/t1.0-1/c0.0.368.368a/s240x240/17991906_260925961037324_385102981568585481_n.jpg?_nc_cat=101&_nc_ohc=PlBLQ0eqBCkAX813qtQ&_nc_ht=scontent-mrs2-2.xx&_nc_tp=1003&oh=9d3daf066e613834aa9c07595d43837f&oe=5EA49D45",
          name: "MELINE Raphaël",
          title: "Developpeur fullstack JS",
          link: "",
          desc: "lorem ipsum",
          status: "Etudiant ENSEIRB-Matmeca",
          icon: "mdi-all-inclusive"
        }
      ]
    };
  }
};
</script>

<style>
</style>