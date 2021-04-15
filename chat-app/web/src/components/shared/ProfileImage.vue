<template>
  <img :src="getImage()"
       @click.prevent="$emit('clicked')"
       alt="profile image"
       width="100px"
       height="100px">
</template>

<script>
import { mapGetters } from 'vuex';
import utils from '@/common/utils';

export default {
  name: 'profileImage',
  emits: ['clicked'],
  methods: {
    getImage() {
      const userImage = this.getUserPersonalInfo.image;
      if (userImage) {
        return utils.readBlobImage(userImage);
      }
      return require('@/assets/profile_default.png');
    },
  },
  computed: {
    ...mapGetters([
      'getUserPersonalInfo'
    ])
  },
}
</script>
