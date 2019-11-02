<template>
 <div id="messagesDiv">
   <ul>
     <li v-for="mes in messages">
        <div>User {{ mes.authorId }} : {{ mes.text }} </div>
     </li>
   </ul>
   <input type="text" v-model="newMessage" placeholder="New Message">
   <button type='button' class='btn btn-success' @click.prevent='sendNewMessage(newMessage)'>Send Message</button>
 </div>
</template>


<script>

export default {
  name: 'Messages',
  props: ['messages', 'sendNewMessage'],
  data () {
    return {
      newMessage: '',
    }
  },
}
</script>

<style scoped>
  #messagesDiv{
    cursor: pointer;
    border-style: solid;
    min-height: 500px;
    padding: 1%;
    margin-left: 2%;
  }
</style>
