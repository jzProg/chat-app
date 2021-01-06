<template>
 <div id="messagesDiv">
   <div id="contentDiv"
        class="container scrollable">
     <div v-for="mes in messages"
          class="inner-message">
          <Message :content="getMessageContent(mes.text)"
                   :author="mes.authorUsername"
                   :date="mes.createdDate"
                   :right-direction="isHomeUser(mes.authorUsername)"
                   :color="getColor(mes.authorId)" />
     </div>
   </div>
   <div id="createMessageDiv">
     <div class="typerContainer">
       <template v-if="typer">
         <span style="text-align: left">{{ typer }} is typing</span><br>
         <div class="typing">
          <div class="typing__dot"></div>
          <div class="typing__dot"></div>
          <div class="typing__dot"></div>
         </div>
       </template>
     </div>
     <input type="text"
            id="inputMessage"
            v-model="newMessage"
            placeholder="New Message"
            @input="sendTyping"
            @keyup.enter="sendNewMessage(newMessage); newMessage = '';">
            <span @click.prevent="showEmojis()">
              😀
            </span>
   </div>
   <EmojiSelection v-if="displayEmojis"
                  @close="displayEmojis = false;"
                  @select="onEmojiSelect">
   </EmojiSelection>
 </div>
</template>


<script>
import EmojiSelection from '@/components/modals/EmojiSelection';
import Message from '@/components/Message';

export default {
  name: 'Messages',
  components: { EmojiSelection, Message },
  props: ['messages', 'sendNewMessage', 'isHomeUser', 'sendTyping', 'typer'],
  data () {
    return {
      newMessage: '',
      userColors: {},
      displayEmojis: false,
    }
  },
  updated() {
    // scroll to latest messages
    this.$nextTick(() => {
      const content = document.getElementById('contentDiv');
      if (content) content.scrollTop = content.scrollHeight;
    });
  },
  methods: {
    getMessageContent(message) {
      return message.replace(/(https?:\/\/)?([\w\-])+\.{1}([a-zA-Z]{2,63})([\/\w-]*)*\/?\??([^#\n\r\s]*)?#?([^\n\r\s]*)?/gi, (match) => {
        return `<a href='${match}' target='_blank'>${match}</a>`;
      });
    },
    getColor(id) {
      return  this.userColors[id] || this.assignRandomColor(id);
    },
    onEmojiSelect(emoji) {
      this.newMessage += String.fromCodePoint(emoji);
      this.displayEmojis = false;
    },
    showEmojis() {
      this.displayEmojis = true;
    },
    assignRandomColor(id){
      var letters = '0123456789ABCDEF';
      var color = '#';
      for (var i = 0; i < 6; i++) {
       color += letters[Math.floor(Math.random() * 16)];
      }
      this.userColors[id] = color;
      return color;
    },
  }
}
</script>

<style scoped>
  #messagesDiv{
    cursor: pointer;
    border-style: solid;
    min-height: 570px;
    padding: 1%;
    margin-left: 2%;
    border-radius: 5px;
    background-color: white;
  }

  .inner-message {
    width: 100%;
    margin-top: 1%;
  }

  #contentDiv{
    min-height: 520px;
    width: 100%;
    color: black;
  }

  #createMessageDiv{
    background-color: #282c34;
    padding-bottom: 1%;
    min-height: 100px;
    margin-top: 0%;
  }

  #inputMessage {
    width: 60%;
    border-radius: 50px;
    padding: 1%;
    outline: none;
    color: black;
  }

  .scrollable {
    height: 300px;
    overflow-y:auto;
    max-width: 100%;
    overflow-x: hidden;
    word-wrap:break-word;
   }

  .typing {
    width: 4em;
    height: 2em;
    padding: 10px;
    margin: 0 auto;
    margin-bottom: 1%;
    background: #e6e6e6;
    border-radius: 20px;
 }

.typing__dot {
  float: left;
  width: 4px;
  height: 4px;
  margin: 0 4px;
  background: #8d8c91;
  border-radius: 50%;
  opacity: 0;
  animation: loadingFade 1s infinite;
}

.typerContainer {
  min-height: 48px;
}

.typing__dot:nth-child(1) {
  animation-delay: 0s;
}

.typing__dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing__dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes loadingFade {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 0.8;
  }
  100% {
    opacity: 0;
  }
}
</style>
