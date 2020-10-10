<template>
  <Modal :width="'500px'">
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
      <img :src="getImage()"
           alt="profile image"
           width="150px"
           height="150px"
           style="border-radius:150px; margin:2%;"><br>
      <span v-if="showUpload">
        <input type="file" @change="onImageSelected($event)">
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
  import { mapActions, mapMutations, mapGetters } from 'vuex';

  export default {
      name: 'EditProfile',
      components: { Modal },
      data() {
        return {
          showUpload: false,
          selectedImage: '',
          uploadSuccess: false,
        }
      },
      methods: {
        ...mapMutations([
          'setUserImage',
        ]),
        ...mapActions([
          'uploadImage',
        ]),
        onImageSelected(event) {
          this.selectedImage = event.target.files[0];
        },
        changeImage() {
          this.uploadSuccess = false;
          this.showUpload = true;
        },
        uploadImg() {
          this.uploadImage(this.selectedImage).then((res) => {
            this.showUpload = false;
            this.uploadSuccess = true;
            this.setUserImage({ value: res.data.image });
            this.selectedImage = '';
            this.$emit('confirm');
          });
        },
        getImage() {
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
            return window.URL.createObjectURL(blob);
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
            'getLoginUsername'
        ])
      }
  }
</script>

<style scoped>

</style>
