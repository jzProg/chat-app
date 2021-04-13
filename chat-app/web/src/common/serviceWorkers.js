import store from '../store';
import utils from './utils';

export const initPushServiceWorker = () => {
  if ('serviceWorker' in navigator && 'PushManager' in window) {
    navigator.serviceWorker.register('static/push-notification-sw.js').then((swReg) => {
      console.log('Push Notification SW is registered! ' + swReg);
      swReg.pushManager.getSubscription().then((subscription) => {
        const isSubscribed = subscription !== null;
        if (isSubscribed) {
          console.log('User is already subscribed for notifications!');
        } else if (Notification.permission !== 'granted') {
          Notification.requestPermission().then((permission) => {
            if (permission === 'granted') {
              console.log('User is NOT subscribed for notifications! About to be subscribed...');
              subscribeUserForPushNotifications(swReg);
            }
          });
        } else {
          subscribeUserForPushNotifications(swReg);
        }
      });
    }).catch((error) => {
      console.log('Push Notification SW error: ' + error);
    });
  } else {
    console.log('Push Messaging not supported!');
  }
};

const subscribeUserForPushNotifications = (swReg) => {
  const appServerKey = utils.urlB64ToUint8Array(store.getters['getPushNotificationsPublicKey']);
  swReg.pushManager.subscribe({ userVisibleOnly: true, applicationServerKey: appServerKey })
                   .then((sub) => {
                     console.log('User subscribed successfully for notifications! Sending info to server...');
                     console.log(JSON.stringify(sub));
                     store.dispatch('sendPushSubscriptionInfoToServer', sub);
                   })
                   .catch((err) => {
                     console.log('Failed to subscribe user for notifications: ' + err);
                   });
};
