<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import bus from '@/common/eventBus';

export default {
  name: 'App',
  created() {
    bus.$on('logout', () => {
      this.$router.push('/');
    });
    this.fetchPushNotificationKey();
  },
  methods: {
    ...mapActions([
      'fetchPushNotificationKey',
    ]),
  },
}
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    background-color: #282c34;
    color: white;
  }

  body {
    background-color: #282c34;
  }
</style>
