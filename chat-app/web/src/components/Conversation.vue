<template>
 <div id="conversationDiv"
      @click.prevent="openConversation()">
   <h4 style="font-weight: bold;">{{ title }}</h4>
   <div> created: {{ new Date(date).toLocaleString() }}</div>
 </div>
</template>


<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'Conversation',
  props: ['id', 'title', 'date', 'getMessages'],
  methods: {
    openConversation() {
      console.log('opening conversation...');
      console.log(this.id);
      this.getMessages(this.id);
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

<style scoped>
  #conversationDiv{
    cursor: pointer;
    border-style: solid;
    min-height: 50px;
    padding: 2%;
    margin-left: 2%;
    width: 100%;
    margin-bottom: 1%;
    border-radius: 5px;
  }

  #conversationDiv:hover{
    border-color: red;
  }
</style>
