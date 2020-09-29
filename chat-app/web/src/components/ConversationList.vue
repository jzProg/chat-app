<template>
  <div>
    <input type="text"
           v-model="inputMessage"
           placeholder="Conversation Title">
    <button type='button'
            class='btn btn-success'
            @click.prevent='addNewConversation()'>Create New Conversation</button>
    <button type='button'
            class='btn btn-danger'
            @click.prevent='logout()'>Logout</button>
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
   </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import Conversation from '@/components/Conversation';
import MessagesOfActiveConversation from '@/components/MessagesList';

export default {
  name: 'Home',
  components: { Conversation, MessagesOfActiveConversation },
  data () {
    return {
      inputMessage: '',
      conversations: [],
      stompClient: null,
      socket: null,
      activeConversationId: '',
      activeConvMessages: [],
      authorId: '',
    }
  },
  created () {
    const token = localStorage.getItem('token');
    this.authorId = this.getUserLoginInfo()[0];
    this.axios('/api/messages/getConversations', { headers: { Authorization: `Bearer ${token}` } }).then(response => {
      this.conversations = response.data.sort((item1, item2) => item1.date - item2.date);
    })
    /* eslint-disable */
    var self = this;
    this.socket = new SockJS('http://localhost:8080/gs-guide-websocket');
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.connect({}, function (frame) {
      console.log('Connected: ' + frame);
      this.subscribe(`/topic/conversations/${self.getUserLoginInfo()[0]}`, function (message) {
        self.conversations.push(JSON.parse(message.body));
      });
    });
      /* eslint-enable */
  },
  methods: {
    getConvStyle(id) {
      return id === this.activeConversationId ? { backgroundColor: 'yellow' } : {};
    },
    findConversationPositionById(id) {
      return this.conversations.findIndex(x => x.id === id);
    },
    closePreviousConversation() {
      // todo close topic of previous conversation
    },
    goToConversationMessages(convId) {
      this.closePreviousConversation();
      const token = localStorage.getItem('token');
      this.activeConversationId = convId;
      this.axios.get(`/api/messages/getConversationMessages?id=${convId}`, { headers: { Authorization: `Bearer ${token}` } }).then(response => {
        this.activeConvMessages = response.data;
      });
      this.stompClient.subscribe(`/topic/conversation/${convId}`, (message) => {
        this.activeConvMessages.push(JSON.parse(message.body));
      });
    },
    getUserLoginInfo() {
      const storageInfo = JSON.parse(localStorage.getItem('userInfo'));
      const storageId = storageInfo ? storageInfo.id : '';
      const storageUsername = storageInfo ? storageInfo.username : '';
      return [ this.getUserId || storageId, this.getLoginUsername || storageUsername ];
    },
    addNewConversation() {
      this.stompClient.send(`/app/src/${this.getUserLoginInfo()[0]}`, {}, JSON.stringify({'title': this.inputMessage}));
    },
    sendNewMessage(newMessage) {
        this.stompClient.send(`/app/messages/${this.activeConversationId}`, {}, JSON.stringify({'text': newMessage, 'authorId': this.authorId }));
    },
    logout() {
      this.userLogout();
    },
    ...mapActions([
        'userLogout',
    ]),
  },
  computed: {
    ...mapGetters([
        'getLoginUsername',
        'getUserId'
    ])
  }
}
</script>
