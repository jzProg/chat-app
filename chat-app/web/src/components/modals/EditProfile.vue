<template>
  <modal :width="'400px'">
    <span slot="close" id="closeSymbol" @click.prevent="close">x</span><br>
    <h3 slot="header">
      Edit Profile Info
    </h3>
    <div slot="body">
      <h4><i class="fas fa-user"></i> <b>{{ getUserPersonalInfo.loginUsername }}</b></h4>
      <h4><i class="far fa-envelope"></i> {{ getMail() }}</h4>
      <profile-image style="border-radius:150px; margin:2%"/><br>
      <span v-if="showUpload">
        <input type="file"
               @change="onImageSelected($event)"
               @focus="clearErrors()"
               accept="image/png,image/gif,image/jpeg"
               style="width: 340px">
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
  </modal>
</template>

<script>
  import { mapActions, mapMutations, mapGetters } from 'vuex';
  import Modal from '@/components/modals/GenericModal';
  import ProfileImage from '@/components/shared/ProfileImage';

  export default {
    name: 'EditProfile',
    emits: ['close'],
    components: { Modal, ProfileImage },
    data() {
      return {
        showUpload: false,
        selectedImage: '',
        uploadSuccess: false,
        uploadError: '',
      };
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
      getMail() {
        return this.getUserPersonalInfo.email;
      },
      uploadImg() {
        this.uploadImage(this.selectedImage).then(res => {
          this.showUpload = false;
          this.uploadSuccess = true;
          this.setUserImage({ value: res.data.image });
          this.selectedImage = '';
        }).catch(error => {
          this.uploadError = error.response.data;
        });
      },
      logout() {
        this.userLogout();
      },
      close() {
        this.$emit('close');
      },
    },
    computed: {
      ...mapGetters([
        'getUserPersonalInfo'
      ]),
    },
  };
</script>
