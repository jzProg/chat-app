<template>
  <div>
    <div id="addConversationDiv"
         v-if="conversations.length"
         @click.prevent="showModal = true">
         <i class="fas fa-plus fa-2x"></i>
   </div>
    <div class="container"
         style="margin-top:2%;width:100%" >
      <div class="row" v-if="conversations.length">
        <div id="convDiv" class="col-md-5 scrollable">
          <Conversation  v-for="(conv, index) in conversations"
                         :style="getConvStyle(conv.id)"
                         :key="index"
                         :id="conv.id"
                         :title="conv.title"
                         :date="conv.date"
                         :indicator-count="indicators[conv.id]"
                         :members="conv.members.filter(member => member !== getLoginUsername).map(member => `@${member}`)"
                         @delete="deleteConversation(conv.id)"
                         :get-messages="goToConversationMessages"/>
        </div>
        <div id="messDiv" class="col-md-7">
          <Messages-of-Active-conversation v-if="activeConversationId"
                                           :isHomeUser="isHomeUser"
                                           :send-new-message="sendNewMessage"
                                           :typer="typer"
                                           :send-typing="sendTyping"
                                           :messages="activeConvMessages"/>
        </div>
      </div>
      <div v-else>
       <h3><i>You don't have any conversation yet...</i></h3>
       <button type="button"
              class="btn btn-primary"
              @click.prevent="showModal = true">
              Create Conversation
       </button>
      </div>
      </div>
      <CreateConversationModal v-if="showModal"
                              @create="addNewConversation"
                              @close="showModal = false"/>
   </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import Conversation from '@/components/Conversation';
import MessagesOfActiveConversation from '@/components/MessagesList';
import CreateConversationModal from '@/components/modals/CreateConversation';
import localForage from '../../static/localforage.min.js';

export default {
  name: 'Conversations',
  components: { Conversation, MessagesOfActiveConversation, CreateConversationModal },
  data () {
    return {
      conversations: [],
      stompClient: null,
      socket: null,
      activeConversationId: '',
      activeConvMessages: [],
      authorId: '',
      showModal: false,
      activeSubscription: null,
      typer: '',
      typingTimeout: null,
      indicators: {}
    }
  },
  watch: {
    activeConversationId(value) {
      localForage.setItem('activeConv', value);
    }
  },
  created () {
    localForage.setItem('activeConv', null);
    const token = localStorage.getItem('token');
    this.authorId = this.getUserLoginInfo()[0];
    this.axios('/api/messages/getConversations', { headers: { Authorization: `Bearer ${token}` } }).then(response => {
      this.conversations = response.data.sort((item1, item2) => item1.date - item2.date);
    });
    this.connectToSocket();
  },
  methods: {
    ...mapActions([
      'broadcastMessage'
    ]),
    connectToSocket() {
      this.socket = new SockJS('/ws-messaging');
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        this.stompClient.subscribe(`/topic/conversations`, (message) => {
          const conv = JSON.parse(message.body);
          if (!conv.deleted) {
            if (conv.members.indexOf(this.getUserLoginInfo()[1]) !== -1) {
              if (this.conversations.findIndex(c => conv.id === c.id) !== -1) {
                if (conv.id !== this.activeConversationId) {
                  let indicator = this.indicators[conv.id];
                  indicator ? indicator = this.$set(this.indicators, conv.id, indicator + 1) : this.$set(this.indicators, conv.id, 1);
                }
              } else {
                this.$set(this.indicators, conv.id, conv.messagesCount);
                this.conversations.push(conv);
              }
            }
          } else {
            this.clearIfIsActiveConversation(conv.id);
            this.conversations.splice(this.conversations.findIndex(c => conv.id === c.id), 1);
          }
        });
      }, (message) => {
        console.log('Disconnect! Retrying connection...');
        this.connectToSocket();
      });
    },
    getConvStyle(id) {
      return id === this.activeConversationId ? { backgroundColor: '#337ab7', color: 'white',   borderColor: '#337ab7' } : {  color: '#337ab7' };
    },
    findConversationPositionById(id) {
      return this.conversations.findIndex(x => x.id === id);
    },
    closePreviousConversation() {
      this.clearTyping();
      if (this.activeSubscription) this.activeSubscription.unsubscribe();
    },
    goToConversationMessages(convId) {
      this.closePreviousConversation();
      this.indicators[convId] = 0;
      const token = localStorage.getItem('token');
      this.activeConversationId = convId;
      this.axios.get(`/api/messages/getConversationMessages?id=${convId}`, { headers: { Authorization: `Bearer ${token}` } }).then(response => {
        this.activeConvMessages = response.data;
      });
      this.activeSubscription = this.stompClient.subscribe(`/topic/conversation/${convId}`, (message) => {
        const messageObj = JSON.parse(message.body);
        if (messageObj.typer) {
          if (this.authorId !== messageObj.authorId) {
            this.setTyper(messageObj.authorUsername);
            this.checkForStopTyping();
          }
        } else {
          this.clearTyping();
          this.activeConvMessages.push(messageObj);
        }
      });
    },
    getUserLoginInfo() {
      const storageInfo = JSON.parse(localStorage.getItem('userInfo'));
      const storageId = storageInfo ? storageInfo.id : '';
      const storageUsername = storageInfo ? storageInfo.username : '';
      return [ this.getUserId || storageId, this.getLoginUsername || storageUsername ];
    },
    addNewConversation(name, members) {
      members.push(this.getUserLoginInfo()[1]);
      this.stompClient.send(`/app/src/${this.getUserLoginInfo()[0]}`, {}, JSON.stringify({ 'title': name || '', 'members' : members }));
      this.showModal = false;
    },
    deleteConversation(id) {
      this.clearIfIsActiveConversation(id);
      this.stompClient.send(`/app/src/delete/${this.getUserLoginInfo()[0]}`, {}, JSON.stringify({ 'id': id }));
    },
    clearIfIsActiveConversation(id) {
      if (this.activeConversationId === id) {
        this.closePreviousConversation();
        this.activeConvMessages = [];
        this.activeConversationId = '';
      }
    },
    sendNewMessage(newMessage) {
      if (newMessage) {
        this.stompClient.send(`/app/messages/${this.activeConversationId}`, {}, JSON.stringify({ 'text': newMessage, 'authorId': this.authorId }));
        setTimeout(() => {
          this.stompClient.send(`/app/src/newMessage`, {}, JSON.stringify({ 'id': this.activeConversationId }));
        }, 700);
        this.broadcastMessage({ id: this.activeConversationId });
      }
    },
    sendTyping() {
      this.stompClient.send(`/app/messages/typing/${this.activeConversationId}`, {}, JSON.stringify({ 'authorId': this.authorId }));
    },
    clearTyping() {
      this.typer = '';
    },
    setTyper(typer) {
      this.typer = typer;
    },
    isHomeUser(username) {
      return username === this.getLoginUsername;
    },
    checkForStopTyping() {
      clearTimeout(this.typingTimeout);
      this.timeout = setTimeout(() => {
       this.clearTyping();
      }, 1000);
    }
  },
  computed: {
    ...mapGetters([
      'getLoginUsername',
      'getUserId'
    ])
  }
}
</script>

<style scoped>
  #addConversationDiv {
    background-color: #337ab7;
    color: white;
    width: 30px;
    height: 30px;
    cursor: pointer;
    margin-left: 5%;
    border-radius: 30px
  }

  .scrollable {
    height: 700px;
    overflow-y:auto;
    max-width: 100%;
    overflow-x: hidden;
    word-wrap:break-word;
   }
</style>
