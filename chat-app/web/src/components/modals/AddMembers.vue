<template>
  <modal>
    <span slot="close" id="closeSymbol" @click.prevent="close">x</span><br>
    <h3 slot="header">Add members to conversation</h3>
    <div slot="body" style="text-align: left">
       <search-user :members="members" :update-members="updateMembers"/>
    </div>
    <div slot="footer"
         class="text-center">
      <button type="button"
              class="btn btn-primary"
              :disabled="showConfirm()"
              @click.prevent="confirm()">
              Confirm
      </button>
    </div>
  </modal>
</template>

<script>
  import Modal from '@/components/modals/GenericModal';
  import SearchUser from '@/components/shared/SearchUser';

  export default {
    name: 'add-members',
    emits: ['confirm', 'close'],
    components: {
      Modal,
      SearchUser,
     },
    data() {
      return {
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
      confirm() {
        this.$emit('confirm', this.members);
      },
      close() {
        this.$emit('close');
      },
    },
  };
</script>
