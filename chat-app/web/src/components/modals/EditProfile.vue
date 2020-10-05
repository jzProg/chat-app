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
      <button v-if="!showUpload"
              class="btn btn-primary"
              type="button"
              @click.prevent="changeImage()">
              Change Image
      </button>
      <br>
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
  </Modal>
</template>

<script>
  import Modal from '@/components/modals/GenericModal';
  import { mapActions, mapMutations } from 'vuex';

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
        close() {
          this.$emit('close');
        },
      },
  }
</script>

<style scoped>
 button {
   margin: 5%;
 }
</style>
