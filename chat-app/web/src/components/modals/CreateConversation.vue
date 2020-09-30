<template>
  <Modal>
    <span slot = "close" id = 'closeSymbol' @click.prevent = "close">x</span><br>
    <h3 slot = "header">Create New Conversation</h3>
    <div slot = "body">
      <input type="text"
             v-model="inputMessage"
             placeholder="Conversation Title">
    </div>
    <div slot = "footer" class = "text-center">
      <button type = "button"
              class = "btn btn-primary"
              @click.prevent = "create()">
              Confirm
     </button>
    </div>
  </Modal>
</template>

<script>
  import { mapActions, mapGetters } from 'vuex';
  import Modal from '@/components/modals/GenericModal';

  export default {
    name: 'CreateConversation',
    components: { Modal },
    data() {
      return {
        inputMessage: '',
      }
    },
    methods: {
      create() {
        this.$emit('create', this.inputMessage);
      },
      close() {
        this.$emit('close');
      }
    },
  }
</script>
