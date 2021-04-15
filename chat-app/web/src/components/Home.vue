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
    <profile-image class="profileImg"
                   @clicked="editProfile()"/>
    <router-view></router-view>
    <EditProfile v-if="showEdit"
                @close="showEdit = false">
    </EditProfile>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import ProfileImage from '@/components/shared/ProfileImage';
import EditProfile from '@/components/modals/EditProfile';
import { initPushServiceWorker } from '@/common/serviceWorkers';

export default {
  name: 'Home',
  components: { EditProfile, ProfileImage },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.getPushNotificationPublicKey && initPushServiceWorker();
    });
  },
  data () {
    return {
      conversations: [],
      showEdit: false,
    }
  },
  methods: {
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
