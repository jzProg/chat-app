import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import bus from "@/common/eventBus";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    userInfo: {
      id: '',
      loginUsername: '',
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
    },
    getUserId(state) {
      return state.userInfo.id;
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
    setUserId(state, payload) {
      state.userInfo.id = payload.value;
    },
  },
  actions: {
    userLogin({ commit }, payload) {
      return axios.post('/api/user/auth', { username: payload.username, password: payload.password })
                  .then((response) => {
                    localStorage.setItem('token', response.data.token);
                    commit({ type: 'setLoginUsername', value: response.data.username });
                    commit({ type: 'setUserId', value: response.data.userId });
                    localStorage.setItem('userInfo', JSON.stringify({ id: response.data.userId, username: response.data.username }))
                    bus.$emit('login', payload.username);
                  })
                  .catch((error) => {
                    console.log('login error: ', error);
                    commit({ type: 'setLoginErrorMessage', value: 'Oops! Seems that you provide wrong credentials...' });
                  });
    },
    createUserProfile({ commit }, payload) {
      return axios.post('/api/user/registerUser', { username: payload.username, password: payload.password, email: payload.email })
                  .catch((error) => {
                    console.log('register error: ', error);
                    commit({ type: 'setRegisterErrorMessage', value: 'Failed to create an account...Please try again later' });
                  });
    },
    userLogout({ commit }) {
      localStorage.setItem('token', '');
      commit({ type: 'setLoginUsername', value: '' });
      commit({ type: 'setUserId', value: '' });
      bus.$emit('logout');
    }
  }
});