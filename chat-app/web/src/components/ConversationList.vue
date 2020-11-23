<template>
  <div>
    <div id="addConversationDiv"
         @click.prevent='showModal = true;'>
         <i class="fas fa-plus fa-2x"></i>
   </div>
    <div class="container"
         style="margin-top:2%;width:100%" >
      <div class="row">
        <div id = "convDiv"
             class = "col-md-5">
          <Conversation  v-for="(conv, index) in conversations"
                         :style="getConvStyle(conv.id)"
                         :key="index"
                         :id="conv.id"
                         :title="conv.title"
                         :date="conv.date"
                         :members="conv.members"
                         @delete="deleteConversation(conv.id)"
                         :get-messages="goToConversationMessages">
           </Conversation>
        </div>
        <div id = "messDiv"
             class = "col-md-7">
          <Messages-of-Active-conversation v-if="activeConversationId"
                                           :send-new-message="sendNewMessage"
                                           :messages="activeConvMessages">
          </Messages-of-Active-conversation>
        </div>
      </div>
      </div>
      <CreateConversationModal v-if="showModal"
                              @create="addNewConversation"
                              @close="showModal = false;">
      </CreateConversationModal>
   </div>
</template>

<script>
import { mapGetters } from 'vuex';
import Conversation from '@/components/Conversation';
import MessagesOfActiveConversation from '@/components/MessagesList';
import CreateConversationModal from '@/components/modals/CreateConversation';

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
    }
  },
  created () {
    const token = localStorage.getItem('token');
    this.authorId = this.getUserLoginInfo()[0];
    this.axios('/api/messages/getConversations', { headers: { Authorization: `Bearer ${token}` } }).then(response => {
      this.conversations = response.data.sort((item1, item2) => item1.date - item2.date);
    });
    this.connectToSocket();
  },
  methods: {
    connectToSocket() {
      this.socket = new SockJS('/ws-messaging');
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        this.stompClient.subscribe(`/topic/conversations`, (message) => {
          const conv = JSON.parse(message.body);
          if (!conv.deleted) {
            if (conv.members.indexOf(this.getUserLoginInfo()[1]) != -1) this.conversations.push(conv);
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
      return id === this.activeConversationId ? { backgroundColor: '#337ab7' } : {};
    },
    findConversationPositionById(id) {
      return this.conversations.findIndex(x => x.id === id);
    },
    closePreviousConversation() {
      if (this.activeSubscription) this.activeSubscription.unsubscribe();
    },
    goToConversationMessages(convId) {
      this.closePreviousConversation();
      const token = localStorage.getItem('token');
      this.activeConversationId = convId;
      this.axios.get(`/api/messages/getConversationMessages?id=${convId}`, { headers: { Authorization: `Bearer ${token}` } }).then(response => {
        this.activeConvMessages = response.data;
      });
      this.activeSubscription = this.stompClient.subscribe(`/topic/conversation/${convId}`, (message) => {
        this.activeConvMessages.push(JSON.parse(message.body));
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
        this.stompClient.send(`/app/messages/${this.activeConversationId}`, {}, JSON.stringify({'text': newMessage, 'authorId': this.authorId }));
    },
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
</style>
