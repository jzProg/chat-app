<template>
  <Modal :width="'400px'">
    <span slot="close"
          id='closeSymbol'
          @click.prevent="close">x
    </span>
    <br>
    <h3 slot="header">
      Edit Profile Info
    </h3>
    <div slot="body">
      <h4><i class="fas fa-user"></i> <b>{{ getLoginUsername }}</b></h4>
      <h4><i class="far fa-envelope"></i> {{ getUserEmail }}</h4>
      <img :src="getImage()"
           alt="profile image"
           width="100px"
           height="100px"
           style="border-radius:150px; margin:2%;"><br>
      <span v-if="showUpload">
        <input type="file" @change="onImageSelected($event)" @focus="clearErrors()">
      </span>
      <button class="btn btn-primary"
              type="button"
              v-if="selectedImage"
              @click.prevent="uploadImg()">
              Upload image
      </button>
      <span v-if="uploadSuccess">
        <i class = "fas fa-check" style="color:green"></i>
         Photo Profile updated!
      </span>
      <div v-if="uploadError">
        <i class = "fas fa-exclamation-triangle" style="color:red"></i>
        {{ uploadError }}
      </div>
    </div>
    <div slot="footer"
         class="text-center">
      <button v-if="!showUpload"
              class="btn btn-primary"
              type="button"
              @click.prevent="changeImage()">
              Change Image <i class="fas fa-id-badge"></i>
      </button>
      <button type="button"
              class="btn btn-danger"
              @click.prevent="logout()">
              Logout <i class="fas fa-sign-out-alt"></i>
      </button>
    </div>
  </Modal>
</template>

<script>
  import Modal from '@/components/modals/GenericModal';
  import ImageMixin from '@/common/utils';
  import { mapActions, mapMutations, mapGetters } from 'vuex';

  export default {
      name: 'EditProfile',
      components: { Modal },
      mixins: [ImageMixin],
      data() {
        return {
          showUpload: false,
          selectedImage: '',
          uploadSuccess: false,
          uploadError: ''
        }
      },
      methods: {
        ...mapMutations([
          'setUserImage',
        ]),
        ...mapActions([
          'uploadImage',
          'userLogout',
        ]),
        clearErrors() {
          this.uploadError = '';
        },
        onImageSelected(event) {
          this.selectedImage = event.target.files[0];
        },
        changeImage() {
          this.uploadSuccess = false;
          this.showUpload = true;
        },
        uploadImg() {
          this.uploadImage(this.selectedImage).then(res => {
            this.showUpload = false;
            this.uploadSuccess = true;
            this.setUserImage({ value: res.data.image });
            this.updateUserImageForSession(res.data.image);
            this.selectedImage = '';
            this.$emit('confirm');
          }).catch(error => {
            this.uploadError = error.response.data;
          });
        },
        logout() {
          this.userLogout();
        },
        updateUserImageForSession(image) {
          const { id, username } = JSON.parse(localStorage.getItem('userInfo'));
          localStorage.setItem('userInfo', JSON.stringify({ id, username, image }));
        },
        getImage() {
          const userImage = this.getUserImage
          if (userImage) {
            return this.readBlobImage(userImage);
          } else {
            return require('@/assets/profile_default.png');
          }
        },
        close() {
          this.$emit('close');
        },
      },
      computed: {
        ...mapGetters([
            'getUserImage',
            'getLoginUsername',
            'getUserEmail'
        ])
      }
  }
</script>

<style scoped>

</style>
