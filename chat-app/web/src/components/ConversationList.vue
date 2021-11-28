<template>
  <div>
    <div id="addConversationDiv" v-if="conversations.length" @click.prevent="toggleConversationModal()">
      <i class="fas fa-plus fa-2x"/>
    </div>
    <div class="container chatContainer">
      <div class="row" v-if="conversations.length">
        <div id="convDiv" class="col-md-5 scrollable" v-if="showConvTab()">
          <conversation  v-for="(conv, index) in getSortedConversations"
                         :style="getConvStyle(conv.id)"
                         :key="index"
                         :id="conv.id"
                         :title="conv.title"
                         :date="conv.date"
                         :is-owner="conv.ownerId === authorId"
                         :is-deleted="conv.deleted"
                         :indicator-count="getIndicator(conv.id)"
                         :members="getMembers(conv)"
                         @delete="deleteConversation(conv.id)"
                         @remove="removeConversation(conv.id)"
                         @addMember="toggleMembersModal(conv.id)"
                         :get-messages="goToConversationMessages"/>
           <navigation v-if="totalConversations > CONVERSATION_LIMIT"
                       :go-to-next="goToNext"
                       :go-to-prev="goToPrev"
                       :has-prev="hasPrev"
                       :has-next="hasNext"/>
        </div>
        <div id="messDiv" class="col-md-7" v-if="showMessageTab()">
          <back-arrow v-if="isMobile()" :action="() => activeConversationId = ''"/>
          <messages-of-active-conversation :isHomeUser="isHomeUser"
                                           :send-new-message="sendNewMessage"
                                           :typer="typer"
                                           :is-deleted="isConversationDeleted()"
                                           :send-typing="sendTyping"
                                           :messages="activeConvMessages[activeConversationId]"
                                           @loadMore="loadOlderMessages"
                                           />
        </div>
      </div>
      <empty-conversation-list-state v-else @addConversation="toggleConversationModal()"/>
    </div>
    <component :is="currentModal" @confirm="addNewConversationOrMember" @close="closeModal()"/>
   </div>
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex';
import Conversation from '@/components/Conversation';
import MessagesOfActiveConversation from '@/components/MessagesList';
import CreateConversationModal from '@/components/modals/CreateConversation';
import AddMemberModal from '@/components/modals/AddMembers';
import EmptyConversationListState from '@/components/EmptyConversationListState';
import Navigation from '@/components/shared/Navigation';
import BackArrow from '@/components/shared/BackArrow';
import utils from '@/common/utils';
import localForage from '../../static/localforage.min.js';

export default {
  name: 'Conversations',
  components: {
    Conversation,
    MessagesOfActiveConversation,
    EmptyConversationListState,
    Navigation,
    BackArrow,
  },
  data () {
    return {
      page: 0,
      totalConversations: 0,
      CONVERSATION_LIMIT: 3,
      eventTypes: {
        TYPING: 'TYPING',
        RECEIVE_MESSAGE: 'RECEIVE_MESSAGE',
        CONVERSATION_CREATED: 'CONVERSATION_CREATED',
        DELETE_CONVERSATION: 'DELETE_CONVERSATION',
        LEAVE_CONVERSATION: 'LEAVE_CONVERSATION',
        MEMBER_ADDED: 'MEMBER_ADDED',
      },
      conversations: [],
      stompClient: null,
      socket: null,
      activeConversationId: '',
      activeConvMessages: {},
      authorId: '',
      currentModal: null,
      showAddMemberModal: false,
      conversationSubscription: null,
      activeSubscriptions: [],
      typer: '',
      typingTimeout: null,
      indicators: {},
    };
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
    this.getUserConversations();
  },
  beforeDestroy() {
    this.disconnectSockets();
  },
  methods: {
    ...mapMutations([
      'setEditedConversationId',
    ]),
    ...mapActions([
      'broadcastMessage',
      'fetchConversationMessages',
      'fetchConversations',
    ]),
    isMobile() {
      return utils.isMobile() || window.innerWidth < 1014;
    },
    showMessageTab() {
       return !!this.activeConversationId;
    },
    showConvTab() {
      return !this.isMobile() || !this.activeConversationId;
    },
    closeModal() {
      this.setEditedConversationId({ value: -1 });
      this.currentModal = null;
    },
    toggleConversationModal() {
      this.currentModal = CreateConversationModal;
    },
    toggleMembersModal(convId) {
      this.setEditedConversationId({ value: convId });
      this.currentModal = AddMemberModal;
    },
    getUserConversations() {
      this.fetchConversations(this.page).then(response => {
        const { conversationDTO, total } = response.data;
        this.conversations = conversationDTO;
        this.totalConversations = total;
        this.connectToSocket();
      });
    },
    hasNext() {
      return (this.page + 1) * this.CONVERSATION_LIMIT < this.totalConversations;
    },
    hasPrev() {
      return !!this.page;
    },
    goToNext() {
      if (this.hasNext()) {
        this.disconnectSockets();
        this.page = this.page + 1;
        this.getUserConversations();
      }
    },
    goToPrev() {
      if (this.hasPrev()) {
        this.disconnectSockets();
        this.page = this.page - 1;
        this.getUserConversations();
      }
    },
    getMembers(conv) {
      const members = conv.members;
      if (!conv.members) return [];
      return members.filter(member => member !== this.getUserPersonalInfo.loginUsername).map(member => `@${member}`)
    },
    isConversationDeleted() {
      const activeConversation = this.conversations.filter(conv => conv.id === this.activeConversationId)[0];
      return activeConversation && activeConversation.deleted;
    },
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
    disconnectSockets() {
      this.unsubscribeFromAllConversations();
      if (this.conversationSubscription) this.conversationSubscription.unsubscribe();
      if (this.stompClient) this.stompClient.disconnect();
    },
    listenForNewConversations() {
      this.conversationSubscription = this.stompClient.subscribe(`/topic/conversations`, (message) => {
        const conv = JSON.parse(message.body);
        this.handleReceivedConversation(conv);
      });
    },
    handleReceivedConversation({ id, members, eventType, date, title, ownerId }) {
      const { CONVERSATION_CREATED, DELETE_CONVERSATION, LEAVE_CONVERSATION, MEMBER_ADDED } = this.eventTypes;

      switch (eventType) {
      case CONVERSATION_CREATED: // add new conversation
        if (members.indexOf(this.getUserPersonalInfo.loginUsername) !== -1) { // if you are member
          if (!this.page) { // if you are to initial page
            this.subscribeToConversation({ id });
            this.totalConversations += 1;
            if (this.conversations.length === 3) {
              this.removeConversation(this.findConversationIdByPosition(2)); // remove last conversation if limit reached
            }
            this.conversations = [
              ...this.conversations,
              { id, date, title, members, eventType },
            ];
          }
        }
        break;
      case DELETE_CONVERSATION: // remove conversation
        if (this.authorId !== ownerId) {
          this.unsubscribeFromConversation(id);
          this.conversations = this.conversations.map(conv => {
            if (conv.id === id) {
              return { ...conv, deleted: true };
            }
            return conv;
          });
        } else { // if owner
          this.disconnectSockets();
          this.getUserConversations(); // fetch conversation by current page
        }
        break;
        case LEAVE_CONVERSATION: // member left
          let missingMember = null;
          this.conversations = this.conversations.map(conv => {
            if (conv.id === id) {
              const oldMembers = conv.members;
              missingMember = oldMembers.filter(member => !members.includes(member))[0];
              if (missingMember === this.getUserPersonalInfo.loginUsername) {
                this.disconnectSockets();
                this.getUserConversations(); // fetch conversation by current page
              }
              return { ...conv, members }; // replace members
            }
            return conv;
          });
          if (id === this.activeConversationId) {
            this.activeConvMessages = {
                 ...this.activeConvMessages,
                 [id]: this.activeConvMessages[id].map(mes => {
                   if (mes.authorUsername === missingMember) {
                     return { ...mes, authorUsername: null }
                   }
                   return mes;
                 }),
             };
          }
          break;
      case MEMBER_ADDED:
        if (members.indexOf(this.getUserPersonalInfo.loginUsername) !== -1) { // if you are member and not owner
          if (!this.page) { // if you are to initial page
            if (this.conversations.filter(conv => conv.id === id).length) { // if you are already member
              this.conversations = this.conversations.map(conv => {
                if (conv.id === id) {
                  return { ...conv, members: [...conv.members, members]}; // update members of conversation
                }
                return conv;
              });
            } else { // if you are new member
              this.subscribeToConversation({ id });
              this.totalConversations += 1;
              if (this.conversations.length === 3) {
                this.removeConversation(this.findConversationIdByPosition(2)); // remove last conversation if limit reached
              }
              this.conversations = [
                ...this.conversations,
                { id, date, title, members, eventType },
              ];
            }
          }
        }
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
    findConversationIdByPosition(index) {
      return this.conversations[index].id;
    },
    closePreviousConversation() {
      this.clearTyping();
    },
    subscribeToAllConversations() {
      this.conversations.forEach((item, i) => {
        this.subscribeToConversation(item);
      });
    },
    unsubscribeFromConversation(convId) {
      this.activeSubscriptions.filter(s => s.id === convId)[0].sub.unsubscribe();
    },
    unsubscribeFromAllConversations() {
      this.activeSubscriptions.forEach((item, i) => {
        item.sub.unsubscribe();
      });
    },
    subscribeToConversation(conv) {
      this.activeSubscriptions.push({ id: conv.id, sub: this.stompClient.subscribe(`/topic/conversation/${conv.id}`, (message) => {
        const messageObj = JSON.parse(message.body);
        this.handleReceivedMessage(messageObj, conv.id);
      })});
    },
    goToConversationMessages(convId) {
      this.closePreviousConversation();
      this.indicators[convId] = 0;
      this.activeConversationId = convId;
      const activeConversation = this.conversations.find(conv => conv.id === this.activeConversationId);
      this.fetchConversationMessages({ convId, index: 0 }).then(response => {
        this.$set(this.activeConvMessages, convId, response.data.sort((mess1, mess2) => mess1.createdDate - mess2.createdDate).map(mes => {
          if (!activeConversation.members || !activeConversation.members.includes(mes.authorUsername)) {
            return { ...mes, authorUsername: null };
          }
          return mes;
        }));
      });
    },
    loadOlderMessages () {
      const convId = this.activeConversationId;
      const activeConversation = this.conversations.find(conv => conv.id === this.activeConversationId);
      this.fetchConversationMessages({ convId, index: this.activeConvMessages[convId].length }).then(response => {
        if (!response.data.length) return;
        this.activeConvMessages = {
          ...this.activeConvMessages,
          [convId]:
            [...this.activeConvMessages[convId], ...response.data].sort((mess1, mess2) => mess1.createdDate - mess2.createdDate).map(mes => {
              if (!activeConversation.members || !activeConversation.members.includes(mes.authorUsername)) {
                return { ...mes, authorUsername: null };
              }
              return mes;
            })
        }
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
    addNewConversationOrMember(members, name) {
      let convObject = {};
      const { id, loginUsername } = this.getUserPersonalInfo;
      if (name) { // add conversation case
        members.push(loginUsername);
        convObject = {
          'title': name || '',
          'members': members,
        };
      } else { // add member case
        convObject = {
          'id': this.getEditedConversationId,
          'members': members,
        };
      }
      this.stompClient.send(`/app/src/${id}`, {}, JSON.stringify(convObject));
      this.closeModal();
    },
    deleteConversation(id) {
      this.stompClient.send(`/app/src/delete/${this.getUserPersonalInfo.id}`, {}, JSON.stringify({ 'id': id }));
    },
    removeConversation(id) {
      this.clearIfIsActiveConversation(id);
      this.unsubscribeFromConversation(id);
      this.conversations.splice(this.findConversationPositionById(id), 1); // remove conversation for you
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
      'getUserPersonalInfo',
      'getEditedConversationId',
    ]),
    getSortedConversations() {
      return this.conversations.sort((item1, item2) => item2.date - item1.date);
    },
  },
};
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

   .chatContainer {
     margin-top: 2%;
     width: 100%;
   }
</style>
