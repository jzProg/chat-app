<template>
 <div id="conversationDiv">
   <div @click.prevent="openConversation()">
     <h3 style="font-weight: bold;">{{ title }}</h3>
     <span v-for="(member, index) in members"> {{ member }}</span>
     <div><i style="color:gray"> created: {{ new Date(date).toLocaleString() }}</i></div>
     <button @click.prevent="deleteConv()"
             class="btn btn-danger">
            <i class="far fa-trash-alt"></i>
    </button>
   </div>
 </div>
</template>


<script>
import { mapGetters } from 'vuex';

export default {
  name: 'Conversation',
  props: ['id', 'title', 'date', 'getMessages', 'members'],
  methods: {
    deleteConv() {
      console.log('deleting conversation...');
      this.$emit('delete');
    },
    openConversation() {
      console.log('opening conversation...' + this.id);
      this.getMessages(this.id);
    },
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
    background-color: white;
  }

  #conversationDiv:hover{
    border-color: red;
  }
</style>
