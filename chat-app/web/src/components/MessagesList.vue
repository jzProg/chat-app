<template>
 <div id="messagesDiv">
   <div id="contentDiv"
        class="container scrollable">
     <div v-for="mes in messages"
          class="inner-message row">
        <div style="text-align: left;"
             class="col-md-9">
          <span :style="getColorStyle(mes.authorId)">{{ mes.authorUsername }}: </span>
          <span v-html="getMessageContent(mes.text)"></span>
        </div>
        <div style="color:lightgrey;"
             class="col-md-3">
             {{ new Date(mes.createdDate).toLocaleString() }}
        </div>
     </div>
   </div>
   <div id="createMessageDiv">
     <input type="text"
            id="inputMessage"
            v-model="newMessage"
            placeholder="New Message"
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

export default {
  name: 'Messages',
  components: { EmojiSelection },
  props: ['messages', 'sendNewMessage'],
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
      content.scrollTop = content.scrollHeight;
    });
  },
  methods: {
    getMessageContent(message) {
      return message.replace(/(https?:\/\/)?([\w\-])+\.{1}([a-zA-Z]{2,63})([\/\w-]*)*\/?\??([^#\n\r\s]*)?#?([^\n\r\s]*)?/gi, (match) => {
        return `<a href='${match}' target='_blank'>${match}</a>`;
      });
    },
    getColorStyle(id) {
      return {
        color: this.getColor(id)
      }
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
    min-height: 500px;
    padding: 1%;
    margin-left: 2%;
    border-radius: 5px;
  }

  .inner-message {
    width: 100%;
    margin-top: 1%;
  }

  #contentDiv{
    min-height: 450px;
    width: 100%;
  }

  #createMessageDiv{
    margin-top: 0%;
  }

  #inputMessage {
    width: 60%;
    border-radius: 50px;
    padding: 1%;
    outline: none;
  }

  .scrollable {
    height: 300px;
    overflow-y:auto;
    max-width: 100%;
    overflow-x: hidden;
    word-wrap:break-word;
}
</style>
