<template>
 <div id="messagesDiv">
   <div id="contentDiv" class="container">
     <div v-for="mes in messages"
          class="inner-message row">
        <div style="text-align: left;"
             class="col-md-9">
          <span :style="getColorStyle(mes.authorId)">{{ mes.authorUsername }}: </span>
          {{ mes.text }}
        </div>
        <div style="color:lightgrey;"
             class="col-md-3">{{ new Date(mes.createdDate).toLocaleString() }}</div>
     </div>
   </div>
   <div id="createMessageDiv">
     <input type="text"
            v-model="newMessage"
            placeholder="New Message"
            style="width: 60%; border-radius: 50px;padding:1%"
            @keyup.enter="sendNewMessage(newMessage); newMessage = '';">
   </div>
 </div>
</template>


<script>

export default {
  name: 'Messages',
  props: ['messages', 'sendNewMessage'],
  data () {
    return {
      newMessage: '',
      userColors: {},
    }
  },
  methods: {
    getColorStyle(id) {
      return {
        color: this.getColor(id)
      }
    },
    getColor(id) {
      return  this.userColors[id] || this.assignRandomColor(id);
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
</style>
