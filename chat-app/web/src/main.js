// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import App from './App';
import router from './router';
import axios from 'axios';
import VueAxios from 'vue-axios';
import store from './store';

// interceptor for catching token expiration case
axios.interceptors.response.use(function (config) {
    return config;
  }, function (error) {
    if (error.response.status === 401) {
      store.dispatch('userLogout');
    }
    return Promise.reject(error);
});

Vue.use(VueAxios, axios);

Vue.config.productionTip = false;
Vue.config.devtools = true;

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
});
