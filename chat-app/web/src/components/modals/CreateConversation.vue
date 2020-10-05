<template>
  <Modal>
    <span slot = "close" id = 'closeSymbol' @click.prevent = "close">x</span><br>
    <h3 slot = "header">Create New Conversation</h3>
    <div slot = "body">
       <input type="text"
             v-model="inputMessage"
             placeholder="Conversation Title"><br>
       <input type="text"
             v-model="user"
             @keyup.enter="search"
             placeholder="Search Members">
        <ul>
          <li v-for="(cand, index) in candidates"
              style="cursor: pointer"
              @click.prevent="addMember(cand)">
              {{ cand.username }}
          </li>
        </ul>
        <div v-for="(member, index) in members"
             class="member">
          {{ member }}
        </div>
    </div>
    <div slot = "footer" class = "text-center">
      <button type = "button"
              class = "btn btn-primary"
              :disabled="showConfirm()"
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
        user: '',
        candidates: [],
        members: []
      }
    },
    methods: {
      addMember(member) {
        console.log("adding member...");
        if (this.members.indexOf(member.username)) {
          this.members.push(member.username);
        }
      },
      search() {
        const token = localStorage.getItem('token');
        this.axios(`/api/user/getUsers?name=${this.user}`, { headers: { Authorization: `Bearer ${token}` }}).then(response => {
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
</style>
