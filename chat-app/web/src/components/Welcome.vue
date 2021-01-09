<template>
  <div>
    <i class="fas fa-comment-dots fa-10x"/>
    <h1>
      <b>{{ msg }}</b>
    </h1>
    <router-view></router-view>
  </div>
</template>

<script>
  import Login from '@/components/Login';
  import bus from '@/common/eventBus';

  export default {
    name: 'Welcome',
    components: { Login },
    created() {
       bus.$on('login', () => {
         this.$router.push('home');
       });
     },
    data() {
      return {
        msg: 'JZ Chat App'
      }
    }
  }
</script>

<style scoped>
  h1 {
    font-weight: normal;
  }

  i {
    color: #337ab7;
  }
</style>
