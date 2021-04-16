import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import bus from "@/common/eventBus";
import utils from '@/common/utils';
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex);

export default new Vuex.Store({
  plugins: [createPersistedState()],
  state: {
    userInfo: {
      id: '',
      image: '',
      loginUsername: '',
      email: '',
      messages: [],
    },
    pushNotificationPublicKey: '',
    errorLoginMessage: '',
    errorRegisterMessage: '',
  },
  getters: {
    getPushNotificationPublicKey(state) {
      return state.pushNotificationPublicKey;
    },
    getUserPersonalInfo(state) {
      const { id, image, loginUsername, email } = state.userInfo;
      return { id, image, loginUsername, email };
    },
    getMessages(state) {
      return state.userInfo.messages;
    },
    getErrorRegisterMessage(state) {
      return state.errorRegisterMessage;
    },
    getErrorLoginMessage(state) {
      return state.errorLoginMessage;
    }
  },
  mutations: {
    setPushNotificationPublicKey(state, payload) {
      state.pushNotificationPublicKey = payload.value;
    },
    setPersonalInfo(state, payload) {
      state.userInfo = Object.assign(state.userInfo, payload.value);
    },
    setUserImage(state, payload) {
      state.userInfo.image = payload.value;
    },
    setRegisterErrorMessage(state, payload) {
      state.errorRegisterMessage = payload.value;
    },
    setLoginErrorMessage(state, payload) {
      state.errorLoginMessage = payload.value;
    },
  },
  actions: {
    searchUsers: utils.debounce(({ commit }, usernameTerm) => {
      const token = localStorage.getItem('token');
      return axios({
        url: `api/user/getUsers?name=${usernameTerm}`,
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    }, 300),
    fetchConversations({ commit }, page) {
      const token = localStorage.getItem('token');
      return axios({
        url: `api/messages/getConversations?page=${page}`,
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    },
    fetchConversationMessages({ commit }, convId) {
      const token = localStorage.getItem('token');
      return axios({
        url: `api/messages/getConversationMessages?id=${convId}`,
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    },
    sendPushSubscriptionInfoToServer({ commit }, sub) {
      const token = localStorage.getItem('token');
      return axios({
        url: 'api/notifications/sendPushSubInfo',
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: {
          pushSubInfo: JSON.stringify(sub),
        },
      }).then((response) => {
        localStorage.setItem('subId', response.data);
      });
    },
    removePushNotificationSubscriptionFromServer({ commit }, sub) {
      const token = localStorage.getItem('token');
      return axios({
        url: 'api/notifications/removePushSubInfo',
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        data: {
          subId: sub,
        },
      }).then((response) => {
        console.log('Push Notification Subscription removed!');
        localStorage.setItem('subId', '');
      });
    },
    removePushNotificationSubscription({ commit, dispatch }) {
      if ('serviceWorker' in navigator && 'PushManager' in window) {
        navigator.serviceWorker.getRegistrations().then((registrations) => {
          const pushRegistration = registrations.filter(item => item.active.scriptURL.includes('push'))[0];
          pushRegistration.pushManager.getSubscription()
          .then((subscription) => {
            if (subscription) {
              subscription.unsubscribe().then(() => {
                dispatch('removePushNotificationSubscriptionFromServer', localStorage.getItem('subId'));
              });
            }
          });
        });
      }
    },
    fetchPushNotificationKey({ commit }) {
      return axios({
        url: 'api/notifications/getPublicKey',
        method: 'GET',
      }).then((response) => {
        commit({ type: 'setPushNotificationPublicKey', value: response.data });
      });
    },
    broadcastLogin({ commit }) {
      const token = localStorage.getItem('token');
      return axios.post('/api/notifications/sendLoginEvent',  {}, { headers: { 'Authorization': `Bearer ${token}` }})
                  .then((response) => {
                    console.log('sendLoginEvent sent!');
                  })
                  .catch((error) => {
                    console.log('sendLoginEvent error: ', error);
                  });
    },
    broadcastMessage({ commit }, { id }) {
      const token = localStorage.getItem('token');
      return axios.post('/api/notifications/sendMessageEvent',  { id }, { headers: { 'Authorization': `Bearer ${token}` }})
                  .then((response) => {
                    console.log('sendMessageEvent sent!');
                  })
                  .catch((error) => {
                    console.log('sendMessageEvent error: ', error);
                  });
    },
    uploadImage({ commit, state }, image) {
      const formData = new FormData();
      formData.append("imageFile", image);
      formData.append("username", state.userInfo.loginUsername);
      return axios.post('/api/user/updateProfileImage', formData);
    },
    userLogin({ commit, dispatch }, payload) {
      return axios.post('/api/user/auth', { username: payload.username, password: payload.password })
                  .then((response) => {
                    const { token, username, userId, image, email } = response.data;
                    localStorage.setItem('token', token);
                    commit({ type: 'setPersonalInfo', value: { loginUsername: username, id: userId, image, email } });
                    dispatch('broadcastLogin');
                    bus.$emit('login', payload.username);
                  })
                  .catch((error) => {
                    console.log('login error: ', error);
                    commit({ type: 'setLoginErrorMessage', value: 'Oops! Seems that you provide wrong credentials...' });
                  });
    },
    createUserProfile({ commit, dispatch }, payload) {
      return axios.post('/api/user/registerUser', { username: payload.username, password: payload.password, email: payload.email })
                  .then((response) => {
                    dispatch('userLogin', payload);
                  })
                  .catch((error) => {
                    console.log('register error: ', error);
                    if (error.response.status === 302) commit({ type: 'setRegisterErrorMessage', value: 'User exists...' });
                    else commit({ type: 'setRegisterErrorMessage', value: 'Failed to create an account...Please try again later' });
                  });
    },
    userLogout({ commit, dispatch }) {
      localStorage.setItem('token', '');
      commit({ type: 'setPersonalInfo', value: {
        id: '',
        image: '',
        loginUsername: '',
        email: '',
        messages: []
      }});
      dispatch('removePushNotificationSubscription');
      bus.$emit('logout');
    }
  }
});
