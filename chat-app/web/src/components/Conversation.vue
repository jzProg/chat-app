<template>
 <div id="conversationDiv">
   <div @click.prevent="openConversation()">
     <h4 style="font-weight: bold;">{{ title }}</h4>
     <div> created: {{ new Date(date).toLocaleString() }}</div>
   </div>
   <button @click.prevent="deleteConv()"
          class="btn btn-danger">
          delete
  </button>
 </div>
</template>


<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'Conversation',
  props: ['id', 'title', 'date', 'getMessages'],
  methods: {
    deleteConv() {
      console.log('deleting conversation...');
      this.$emit('delete');
    },
    openConversation() {
      console.log('opening conversation...' + this.id);
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
