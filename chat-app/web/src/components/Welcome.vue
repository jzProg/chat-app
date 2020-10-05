<template>
  <div>
    <i class="fas fa-comment-dots fa-10x"
       style="color:crimson">
    </i>
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
     bus.$on('login', () => {
       this.$router.push('home');
     });
   },
  data () {
    return {
      msg: 'Welcome to JZ Chat App'
    }
  },
  methods: {
      ...mapActions([
          'storeUsername',
      ]),
    },
}
</script>

<style scoped>
  h1 {
    font-weight: normal;
  }
</style>
