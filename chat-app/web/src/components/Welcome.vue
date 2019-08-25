<template>
  <div class="hello">
    <img src="../assets/logo.png">
    <h1>{{ msg }}</h1>
    <router-view></router-view>
  </div>
</template>

<script>
import  Login  from './Login.vue'
import bus from "@/common/eventBus";
import { mapActions } from 'vuex';

export default {
  name: 'Welcome',
  components: { Login },
  created(){
     bus.$on('login', (username) => {
       this.storeUsername(username).then(() => {
         this.$router.push('home');
       });
     });
   },
  data () {
    return {
      msg: 'Welcome to JZ messenger'
    }
  },
  methods: {
      ...mapActions([
          'storeUsername',
      ]),
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
