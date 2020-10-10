import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import bus from "@/common/eventBus";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    userInfo: {
      id: '',
      image: '',
      loginUsername: '',
      notifications: [],
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
    getUserImage(state) {
      return state.userInfo.image;
    },
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
    setPushNotificationPublicKey(state, payload) {
      state.pushNotificationPublicKey = payload.value;
    },
    setUserImage(state, payload) {
      state.userInfo.image = payload.value;
    },
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
    uploadImage({ commit, state }, image) {
      const formData = new FormData();
      formData.append("imageFile", image);
      formData.append("username", state.userInfo.loginUsername);
      return axios.post('/api/user/updateProfileImage', formData);
    },
    userLogin({ commit, dispatch }, payload) {
      return axios.post('/api/user/auth', { username: payload.username, password: payload.password })
                  .then((response) => {
                    localStorage.setItem('token', response.data.token);
                    commit({ type: 'setLoginUsername', value: response.data.username });
                    commit({ type: 'setUserId', value: response.data.userId });
                    commit({ type: 'setUserImage', value: response.data.image });
                    localStorage.setItem('userInfo', JSON.stringify({ id: response.data.userId, username: response.data.username, image: response.data.image  }));
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
      commit({ type: 'setLoginUsername', value: '' });
      commit({ type: 'setUserId', value: '' });
      dispatch('removePushNotificationSubscription');
      bus.$emit('logout');
    }
  }
});
