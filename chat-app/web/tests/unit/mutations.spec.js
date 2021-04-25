/*
---------- tests for mutations -----------------
- testing vuex:  https://vue-test-utils.vuejs.org/guides/using-with-vuex.html
- jest expect docs: https://jestjs.io/docs/en/expect
*/
import store from '@/store/index';

test('"setUserImage" updates "state.userInfo.image" correctly', () => {
  store.commit('setUserImage', { value: 'https://www.google.com' });
  expect(store.state.userInfo.image).toBe('https://www.google.com');
});

test('"setPersonalInfo" updates "state.userInfo" correctly', () => {
  const infoObject = {
    id: 1,
    image: 'https://www.google.com',
    loginUsername: 'testUser',
    email: 'jz@test.com',
    messages: []
  };
  store.commit('setPersonalInfo', { value: infoObject });
  expect(store.state.userInfo).toMatchObject(infoObject);

  const desiredInfoObject = {
    email: 'jz@test2.com'
  };
  store.commit('setPersonalInfo', { value: desiredInfoObject });
  expect(store.state.userInfo).not.toMatchObject(infoObject);
  expect(store.state.userInfo).toMatchObject(desiredInfoObject);
  expect(store.state.userInfo.loginUsername).toBe('testUser');
  expect(store.state.userInfo.id).toBe(1);
  expect(store.state.userInfo.messages).toEqual([]);
});

test('"setPushNotificationPublicKey" updates "state.pushNotificationPublicKey" correctly', () => {
  store.commit('setPushNotificationPublicKey', { value: '1234' });
  expect(store.state.pushNotificationPublicKey).toBe('1234');
});
