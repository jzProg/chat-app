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
import bus from '@/common/eventBus';
import EditProfile from '@/components/modals/EditProfile';
import { mapGetters, mapMutations } from 'vuex';

export default {
  name: 'Home',
  components: { EditProfile },
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
    bus.$on('logout', () => {
      this.$router.push('/');
    });
  },
  methods: {
    ...mapMutations([
      'setLoginUsername',
      'setUserImage',
    ]),
    getImage() {
      this.setUserImage({ value: this.getUserLoginInfo()[2] });
      if (this.getUserImage) {
        const byteCharacters = atob(this.getUserImage);
        const byteArrays = [];
        for (let offset = 0; offset < byteCharacters.length; offset += 512) {
          const slice = byteCharacters.slice(offset, offset + 512);
          const byteNumbers = new Array(slice.length);
          for (let i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
          }
          const byteArray = new Uint8Array(byteNumbers);
          byteArrays.push(byteArray);
        }
        const blob = new Blob(byteArrays, {type: 'image/png'});
        this.imageNew = window.URL.createObjectURL(blob);
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
