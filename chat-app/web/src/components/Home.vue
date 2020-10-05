<template>
  <div>
    <h4 style='font-weight:bold'>
      User {{ getUserId || getUserLoginInfo()[0] }} - {{ getLoginUsername || getUserLoginInfo()[1] }}
    </h4>
    <router-view></router-view>
  </div>
</template>

<script>
import bus from "@/common/eventBus";
import { mapGetters } from 'vuex';

export default {
  name: 'Home',
  data () {
    return {
      conversations: []
    }
  },
  created () {
    bus.$on('logout', () => {
      this.$router.push('/');
    });
  },
  methods: {
    getUserLoginInfo() {
      const storageInfo = JSON.parse(localStorage.getItem('userInfo'));
      const storageId = storageInfo ? storageInfo.id : '';
      const storageUsername = storageInfo ? storageInfo.username : '';
      return [ this.getUserId || storageId, this.getLoginUsername || storageUsername ];
    },
  },
  computed: {
    ...mapGetters([
        'getLoginUsername',
        'getUserId'
    ])
  }
}
</script>
