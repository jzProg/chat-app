<template>
  <modal>
    <span slot="close" id="closeSymbol" @click.prevent="close">x</span><br>
    <h3 slot="header">Create New Conversation</h3>
    <div slot="body" style="text-align: left">
       <input type="text"
             v-model="inputMessage"
             style="margin:5%"
             placeholder="Conversation Title"><br>
       <search-user :members="members" :update-members="updateMembers"/>
    </div>
    <div slot="footer"
         class="text-center">
      <button type="button"
              class="btn btn-primary"
              :disabled="showConfirm()"
              @click.prevent="create()">
              Confirm
      </button>
    </div>
  </modal>
</template>

<script>
  import Modal from '@/components/modals/GenericModal';
  import SearchUser from '@/components/shared/SearchUser';

  export default {
    name: 'create-conversation',
    emits: ['confirm', 'close'],
    components: {
      Modal,
      SearchUser,
     },
    data() {
      return {
        inputMessage: '',
        members: [],
      };
    },
    methods: {
      updateMembers(member) {
        if (this.members.indexOf(member) === -1) this.members.push(member);
      },
      showConfirm() {
        return !this.members.length;
      },
      create() {
        this.$emit('confirm', this.members, this.inputMessage);
      },
      close() {
        this.$emit('close');
      },
    },
  };
</script>
