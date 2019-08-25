<template>
  <div>
    <h4 style='font-weight: bold;'>User {{ getUserId || getUserLoginInfo()[0] }} - {{ getLoginUsername || getUserLoginInfo()[1] }}</h4>
     <input type="text" v-model="inputMessage" placeholder="Conversation Title">
     <button type='button' class='btn btn-success' @click.prevent='addNewConversation()'>Create New Conversation</button>
     <button type='button' class='btn btn-danger' @click.prevent='logout()'>Logout</button>
     <div id="messDiv">
       <Conversation  v-for="(conv, index) in conversations" :key="index"
                      :title="conv.title"
                      :date="conv.date">
        </Conversation>
     </div>
  </div>
</template>

<script>
import bus from "@/common/eventBus";
import Conversation from '@/components/Conversation';
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'Home',
  components: { Conversation },
  data () {
    return {
      inputMessage: '',
      conversations: [],
      stompClient: null,
      socket: null
    }
  },
  created () {
    bus.$on('logout', () => {
      this.$router.push('/');
    });
    const token = localStorage.getItem('token');
    this.axios('/api/messages/getConversations', { headers: { Authorization: `Bearer ${token}` } }).then(response => {
      this.conversations = response.data;
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
    getUserLoginInfo() {
      const storageInfo = JSON.parse(localStorage.getItem('userInfo'));
      const storageId = storageInfo ? storageInfo.id : '';
      const storageUsername = storageInfo ? storageInfo.username : '';
      return [ this.getUserId || storageId, this.getLoginUsername || storageUsername ];
    },
    addNewConversation() {
      this.stompClient.send(`/app/src/${this.getUserLoginInfo()[0]}`, {}, JSON.stringify({'title': this.inputMessage}));
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
