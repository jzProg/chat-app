<template>
  <Modal>
    <span slot="close" id="closeSymbol" @click.prevent="close">x</span><br>
    <h3 slot="header">Create New Conversation</h3>
    <div slot="body" style="text-align: left">
       <input type="text"
             v-model="inputMessage"
             style="margin:5%"
             placeholder="Conversation Title"><br>
       <input type="text"
             v-model="user"
             style="margin:5%"
             @input="search()"
             @click.prevent=""
             placeholder="Search Members">
        <i class="fas fa-search"></i>
        <div v-for="(cand, index) in candidates" @click.prevent="addMember(cand, index)" class="candidate">
           <input type="checkbox" :id="cand.username">
            <label :for="cand.username">{{ cand.username }}</label>
        </div>
        <div v-for="(member, index) in members"
             style="text-align: center"
             class="member">
          {{ member }}
        </div>
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
  </Modal>
</template>

<script>
  import { mapActions } from 'vuex';
  import Modal from '@/components/modals/GenericModal';

  export default {
    name: 'CreateConversation',
    components: { Modal },
    data() {
      return {
        inputMessage: '',
        user: '',
        candidates: [],
        members: []
      }
    },
    methods: {
      ...mapActions([
        'searchUsers',
      ]),
      addMember(member, index) {
        if (this.members.indexOf(member.username) === -1) {
          this.members.push(member.username);
          this.candidates.splice(index, 1);
        }
      },
      search() {
        this.searchUsers(this.user).then(response => {
          this.candidates = response.data;
        });
      },
      create() {
        this.$emit('create', this.inputMessage, this.members);
      },
      showConfirm() {
        return !this.members.length;
      },
      close() {
        this.$emit('close');
      }
    },
  }
</script>

<style scoped>
 .member {
   background-color: green;
   color: white;
   width: 50%;
   border-radius: 15px;
   margin: 0 auto 2%;
 }
 .candidate {
   cursor: pointer;
 }
</style>
