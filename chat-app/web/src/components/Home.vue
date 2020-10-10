<template>
  <div>
    <h4 style='font-weight:bold'>
      User {{ getUserId || getUserLoginInfo()[0] }} - {{ getLoginUsername || getUserLoginInfo()[1] }}
    </h4>
    <img :src="imageNew"
         @click.prevent="editProfile()"
         class="profileImg"
         height="100px"
         width="100px">
    <router-view></router-view>
    <EditProfile v-if="showEdit"
                @confirm="getImage()"
                @close="showEdit = false;">
    </EditProfile>
  </div>
</template>

<script>
import EditProfile from '@/components/modals/EditProfile';
import ImageMixin from '@/common/utils';
import { mapGetters, mapMutations } from 'vuex';

export default {
  name: 'Home',
  components: { EditProfile },
  mixins: [ImageMixin],
  beforeRouteEnter(to, from, next) {
    next((vm) => {
      vm.registerPushNotificationSW();
    });
  },
  data () {
    return {
      conversations: [],
      showEdit: false,
      imageNew: '',
    }
  },
  created () {
    this.getImage();
    if (!this.getLoginUsername) this.setLoginUsername({ value: this.getUserLoginInfo()[1] });
  },
  methods: {
    ...mapMutations([
      'setLoginUsername',
      'setUserImage',
    ]),
    registerPushNotificationSW() {
      if ('serviceWorker' in navigator && 'PushManager' in window) {
        navigator.serviceWorker.register('static/push-notification-sw.js').then((swReg) => {
          console.log('Push Notification SW is registered! ' + swReg);
          swReg.pushManager.getSubscription().then((subscription) => {
            console.log(subscription);
            const isSubscribed = subscription !== null;
            if (isSubscribed) {
              console.log('User is already subscribed for notifications!');
            } else if (Notification.permission !== 'granted') {
              Notification.requestPermission().then((permission) => {
                if (permission === 'granted') {
                  console.log('User is NOT subscribed for notifications! About to be subscribed...');
                  this.subscribeUserForPushNotifications(swReg);
                }
              });
            } else {
              this.subscribeUserForPushNotifications(swReg);
            }
          });
        }).catch((error) => {
          console.log('Push Notification SW error: ' + error);
        });
      } else {
        console.log('Push Messaging not supported!');
      }
    },
    subscribeUserForPushNotifications(swReg) {
      const appServerKey = this.urlB64ToUint8Array(this.getPushNotificationPublicKey);
      swReg.pushManager.subscribe({ userVisibleOnly: true, applicationServerKey: appServerKey })
                       .then((sub) => {
                         console.log('User subscribed successfully for notifications! Sending info to server...');
                         console.log(JSON.stringify(sub));
                         this.sendPushSubscriptionInfoToServer(sub);
                       })
                       .catch((err) => {
                         console.log('Failed to subscribe user for notifications: ' + err);
                       });
    },
    sendPushSubscriptionInfoToServer(sub) {
      const token = localStorage.getItem('token');
      this.axios({
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
    urlB64ToUint8Array(base64String) {
      const padding = '='.repeat((4 - base64String.length % 4) % 4);
      const base64 = (base64String + padding).replace(/\-/g, '+').replace(/_/g, '/');
      const rawData = window.atob(base64);
      const outputArray = new Uint8Array(rawData.length);
      for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
      }
      return outputArray;
    },
    getImage() {
      this.setUserImage({ value: this.getUserLoginInfo()[2] });
      const userImage = this.getUserImage;
      if (userImage) {
        this.imageNew = this.readBlobImage(userImage);
      } else {
        this.imageNew = require('@/assets/profile_default.png');
      }
    },
    editProfile() {
      this.showEdit = true;
    },
    getUserLoginInfo() {
      const storageInfo = JSON.parse(localStorage.getItem('userInfo'));
      const storageId = storageInfo ? storageInfo.id : '';
      const storageUsername = storageInfo ? storageInfo.username : '';
      const storageImage = storageInfo ? storageInfo.image : '';
      return [ this.getUserId || storageId, this.getLoginUsername || storageUsername, this.getUserImage || storageImage];
    },
  },
  computed: {
    ...mapGetters([
        'getLoginUsername',
        'getUserId',
        'getUserImage',
        'getPushNotificationPublicKey',
    ])
  }
}
</script>

<style>
.profileImg {
  border-radius: 50px;
  margin-bottom: 1%;
  cursor: pointer;
}
</style>
