<template>
  <div>
    <header style="background-color: lightgray; width: 100%; text-align: left; color: black">
      <div class="logo">
        <i class="fas fa-comment-dots fa-1x" style="color:#337ab7"/>
        <b>JZ Chat App</b>
      </div>
    </header>
    <h4 style='font-weight: bold'>
      <i class="fas fa-at"/>{{ getLoginUsername || getUserLoginInfo()[1] }}
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
import { mapGetters, mapMutations, mapActions } from 'vuex';

export default {
  name: 'Home',
  components: { EditProfile },
  mixins: [ImageMixin],
  beforeRouteEnter(to, from, next) {
    next(vm => {
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
    ...mapActions([
      'sendPushSubscriptionInfoToServer',
    ]),
    ...mapMutations([
      'setLoginUsername',
      'setUserImage',
    ]),
    registerPushNotificationSW() {
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
    cursor: pointer;
  }

  .logo {
    padding: 10px;
    font-size: 150%;
  }
</style>
