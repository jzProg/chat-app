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
                         :indicator-count="getIndicator(conv.id)"
                         :members="conv.members.filter(member => member !== getUserPersonalInfo.loginUsername).map(member => `@${member}`)"
                         @delete="deleteConversation(conv.id)"
                         :get-messages="goToConversationMessages"/>
        </div>
        <div id="messDiv" class="col-md-7">
          <Messages-of-Active-conversation v-if="activeConversationId"
                                           :isHomeUser="isHomeUser"
                                           :send-new-message="sendNewMessage"
                                           :typer="typer"
                                           :send-typing="sendTyping"
                                           :messages="activeConvMessages[activeConversationId]"/>
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
      eventTypes: {
        TYPING: 'TYPING',
        RECEIVE_MESSAGE: 'RECEIVE_MESSAGE',
        CONVERSATION_CREATED: 'CONVERSATION_CREATED',
        DELETE_CONVERSATION: 'DELETE_CONVERSATION',
      },
      conversations: [],
      stompClient: null,
      socket: null,
      activeConversationId: '',
      activeConvMessages: {},
      authorId: '',
      showModal: false,
      conversationSubscription: null,
      activeSubscriptions: [],
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
    localForage.setItem('activeConv', null); // reset active conversation
    const token = localStorage.getItem('token');
    this.authorId = this.getUserPersonalInfo.id;
    this.fetchConversations().then(response => {
      this.conversations = response.data.sort((item1, item2) => item1.date - item2.date);
      this.connectToSocket();
    });
  },
  beforeDestroy() {
    this.unsubscribeFromAllConversations();
    if (this.conversationSubscription) this.conversationSubscription.unsubscribe();
    if (this.stompClient) this.stompClient.disconnect();
  },
  methods: {
    ...mapActions([
      'broadcastMessage',
      'fetchConversationMessages',
      'fetchConversations',
    ]),
    getIndicator(id) {
      return this.indicators[id];
    },
    connectToSocket() {
      this.socket = new SockJS('/ws-messaging');
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({}, (frame) => {
        console.log(`Connected: ${frame}`);
        this.subscribeToAllConversations();
        this.listenForNewConversations();
      }, (message) => {
        console.log('Disconnect! Retrying connection...');
        this.connectToSocket();
      });
    },
    listenForNewConversations() {
      this.conversationSubscription = this.stompClient.subscribe(`/topic/conversations`, (message) => {
        const conv = JSON.parse(message.body);
        this.handleReceivedConversation(conv);
      });
    },
    handleReceivedConversation({ id, members, eventType }) {
      const { CONVERSATION_CREATED, DELETE_CONVERSATION } = this.eventTypes;

      switch (eventType) {
      case CONVERSATION_CREATED: // add new conversation
        if (members.indexOf(this.getUserPersonalInfo.loginUsername) !== -1) {
          this.subscribeToConversation({ id });
          this.conversations = [
            ...this.conversations,
            { id, members, eventType },
          ];
        }
        break;
      case DELETE_CONVERSATION: // remove conversation
        this.clearIfIsActiveConversation(id);
        this.conversations.splice(this.findConversationPositionById(id), 1);
        break;
      default: break;
      }
    },
    getConvStyle(id) {
      return id === this.activeConversationId ? { backgroundColor: '#337ab7', color: 'white',   borderColor: '#337ab7' } : {  color: '#337ab7' };
    },
    findConversationPositionById(id) {
      return this.conversations.findIndex(x => x.id === id);
    },
    closePreviousConversation() {
      this.clearTyping();
    },
    subscribeToAllConversations() {
      this.conversations.forEach((item, i) => {
        this.subscribeToConversation(item);
      });
    },
    unsubscribeFromAllConversations() {
      this.activeSubscriptions.forEach((item, i) => {
        item.unsubscribe();
      });
    },
    subscribeToConversation(conv) {
      this.activeSubscriptions.push(this.stompClient.subscribe(`/topic/conversation/${conv.id}`, (message) => {
        const messageObj = JSON.parse(message.body);
        this.handleReceivedMessage(messageObj, conv.id);
      }));
    },
    goToConversationMessages(convId) {
      this.closePreviousConversation();
      this.indicators[convId] = 0;
      this.activeConversationId = convId;
      this.fetchConversationMessages(convId).then(response => {
        this.$set(this.activeConvMessages, convId, response.data.sort((mess1, mess2) => mess1.createdDate - mess2.createdDate));
      });
    },
    handleReceivedMessage(messageObject, convId) {
      const { id, text, authorId, authorUsername, createdDate, messageType } = messageObject;
      const { TYPING, RECEIVE_MESSAGE } = this.eventTypes;

      switch (messageType) {
      case TYPING:
        if (this.authorId !== authorId && convId === this.activeConversationId) {
          this.setTyper(authorUsername);
          this.checkForStopTyping();
        }
        break;
      case RECEIVE_MESSAGE:
        this.clearTyping();
        if (convId !== this.activeConversationId) {
            let indicator = this.indicators[convId];
            indicator ? indicator = this.$set(this.indicators, convId, indicator + 1) : this.$set(this.indicators, convId, 1);
        } else {
           this.activeConvMessages = {
                ...this.activeConvMessages,
                [convId]: [
                    ...this.activeConvMessages[convId],
                    messageObject,
                ],
            };
        }
        break;
      default: break;
      }
    },
    addNewConversation(name, members) {
      const { id, loginUsername } = this.getUserPersonalInfo;
      members.push(loginUsername);
      this.stompClient.send(`/app/src/${id}`, {}, JSON.stringify({ 'title': name || '', 'members' : members }));
      this.showModal = false;
    },
    deleteConversation(id) {
      this.clearIfIsActiveConversation(id);
      this.stompClient.send(`/app/src/delete/${this.getUserPersonalInfo.id}`, {}, JSON.stringify({ 'id': id }));
    },
    clearIfIsActiveConversation(id) {
      if (this.activeConversationId === id) {
        this.closePreviousConversation();
        this.activeConvMessages = {};
        this.activeConversationId = '';
      }
    },
    sendNewMessage(newMessage) {
      if (newMessage) {
        this.stompClient.send(`/app/messages/${this.activeConversationId}`, {}, JSON.stringify({ 'text': newMessage, 'authorId': this.authorId }));
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
      return username === this.getUserPersonalInfo.loginUsername;
    },
    checkForStopTyping() {
      clearTimeout(this.typingTimeout);
      this.typingTimeout = setTimeout(() => {
       this.clearTyping();
      }, 1000);
    }
  },
  computed: {
    ...mapGetters([
      'getUserPersonalInfo'
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
