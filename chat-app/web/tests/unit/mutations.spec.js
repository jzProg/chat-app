/*
---------- tests for mutations -----------------
- testing vuex:  https://vue-test-utils.vuejs.org/guides/using-with-vuex.html
- jest expect docs: https://jestjs.io/docs/en/expect
*/
import store from '@/store/index';

test('"setUserImage" update "state.userInfo.image" correctly', () => {
  store.commit('setUserImage', { value: 'https://www.google.com' });
  expect(store.state.userInfo.image).toBe('https://www.google.com');
});
