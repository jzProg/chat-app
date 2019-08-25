<template>
 <div id="conversationDiv" @click.prevent="openConversation()">
   <h4 style="font-weight: bold;">{{ title }}</h4>
   <div> created: {{ date }}</div>
 </div>
</template>


<script>
import { mapActions, mapGetters } from 'vuex';

export default {
  name: 'Conversation',
  props: ['title', 'date'],
  data () {
    return {

    }
  },
  created () {

  },
  methods: {
    openConversation() {
      console.log('opening conversation...');
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
    padding: 1%;
    margin-top: 1%;
    margin-left: 30%;
    margin-right: 30%;
  }

  #conversationDiv:hover{
    border-color: red;
  }
</style>
