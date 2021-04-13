<template>
  <div>
    <header>
      <div class="logo">
        <i class="fas fa-comment-dots fa-1x" style="color:#337ab7"/>
        <b>JZ Chat App</b>
      </div>
    </header>
    <h4 style='font-weight: bold'>
      <i class="fas fa-at"/>{{ getUserPersonalInfo.loginUsername }}
    </h4>
    <img :src="imageNew"
         @click.prevent="editProfile()"
         class="profileImg"
         height="100px"
         width="100px">
    <router-view></router-view>
    <EditProfile v-if="showEdit"
                @confirm="getImage()"
                @close="showEdit = false">
    </EditProfile>
  </div>
</template>

<script>
import EditProfile from '@/components/modals/EditProfile';
import utils from '@/common/utils';
import { initPushServiceWorker } from '@/common/serviceWorkers';
import { mapGetters } from 'vuex';

export default {
  name: 'Home',
  components: { EditProfile },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.getPushNotificationPublicKey && initPushServiceWorker();
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
  },
  methods: {
    getImage() {
      const userImage = this.getUserPersonalInfo.image;
      if (userImage) {
        this.imageNew = utils.readBlobImage(userImage);
      } else {
        this.imageNew = require('@/assets/profile_default.png');
      }
    },
    editProfile() {
      this.showEdit = true;
    },
  },
  computed: {
    ...mapGetters([
      'getUserPersonalInfo',
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

  header {
    background-color: lightgray;
    width: 100%;
    text-align: left;
    color: black;
  }
</style>
