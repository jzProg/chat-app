import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import bus from "@/common/eventBus";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    userInfo: {
      notifications: [],
      messages: [],
    },
    errorLoginMessage: '',
    errorRegisterMessage: '',
  },
  getters: {
    getMessages(state) {
      return state.userInfo.messages;
    },
    getNotifications(state) {
      return state.userInfo.notifications;
    },
    getErrorRegisterMessage(state) {
      return state.errorRegisterMessage;
    },
    getErrorLoginMessage(state) {
      return state.errorLoginMessage;
    },
    getLoginUsername(state) {
      return state.userInfo.loginUsername;
    }
  },
  mutations: {
    setNotifications(state, payload) {
      state.userInfo.notifications = payload.value;
    },
    setRegisterErrorMessage(state, payload) {
      state.errorRegisterMessage = payload.value;
    },
    setLoginErrorMessage(state, payload) {
      state.errorLoginMessage = payload.value;
    },
    setLoginUsername(state, payload) {
      state.userInfo.loginUsername = payload.value;
    },
  },
  actions: {
    userLogin({ commit }, payload) {
      return axios.post('/api/user/auth', { username: payload.username, password: payload.password })
                  .then((response) => {
                    localStorage.setItem('token', response.data);
                    bus.$emit('login', payload.username);
                  })
                  .catch((error) => {
                    console.log('login error: ', error);
                    commit({ type: 'setLoginErrorMessage', value: error });
                  });
    },
    createUserProfile({ commit }, payload) {
      return axios.post('/api/user/registerUser', { username: payload.username, password: payload.password, email: payload.email })            
                  .catch((error) => {
                    console.log('register error: ', error);
                    commit({ type: 'setRegisterErrorMessage', value: error });
                  });
    },
    userLogout({ commit }) {
      localStorage.setItem('token', '');
      bus.$emit('logout');
    },
    storeUsername({ commit }, username) {
      commit({ type: 'setLoginUsername', value: username });
    },
    clearUserData({ commit }) {
      commit({ type: 'setLoginUsername', value: '' });
    }
  }
});
