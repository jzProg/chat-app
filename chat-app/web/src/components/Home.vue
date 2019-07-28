<template>
  <div>
    <h4>{{ name }}</h4>
     <input type="text" v-model="inputMessage">
     <button type='button' @click='sendMessage()'>Send Message</button>
     {{ title }}
     <div id="messDiv">
       <ul>
         <li v-for="(mes, index) in messages" :key="index">User {{ mes.postedBy }} : "  {{ mes.text }}  "</li>
       </ul>
     </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data () {
    return {
      inputMessage: '',
      messages: [],
      name: 'Home page',
      stompClient: null,
      socket: null,
      title: ''
    }
  },
  created () {
    console.log('CREATED')
    this.axios('/api/getMessages').then(response => {
      this.messages = response.data
    })
    /* eslint-disable */
    var self = this
    this.socket = new SockJS('http://localhost:8080/gs-guide-websocket')
    this.stompClient = Stomp.over(this.socket)
    this.stompClient.connect({}, function (frame) {
      console.log('Connected: ' + frame)
      this.subscribe('/topic/greetings', function (message) {
        self.messages.push(JSON.parse(message.body))
      })
    })
      /* eslint-enable */
  },
  methods: {
    sendMessage () {
      this.stompClient.send('/app/src', {}, JSON.stringify({'message': this.inputMessage}))
    }
  }
}
</script>
