<template>
 <div id="conversationDiv">
   <div id="indicatorSpan" v-show="indicatorCount">
      {{ indicatorCount }}
   </div>
   <div @click.prevent="openConversation()">
     <h3 style="font-weight: bold; color: white">{{ title }}</h3>
     <span> {{ members.join(', ') }}</span>
     <div>
       <i style="color:gray"> created: {{ new Date(date).toLocaleString() }}</i>
     </div>
     <button @click.stop="deleteConv()" class="btn btn-danger">
      <i class="far fa-trash-alt"/>
    </button>
   </div>
 </div>
</template>


<script>
  export default {
    name: 'Conversation',
    emits: ['delete'],
    props: ['id', 'title', 'date', 'getMessages', 'members', 'indicatorCount'],
    methods: {
      deleteConv() {
        console.log(`deleting conversation with id: ${this.id}...`);
        this.$emit('delete');
      },
      openConversation() {
        console.log(`opening conversation with id: ${this.id}...`);
        this.getMessages(this.id);
      },
    }
  }
</script>

<style scoped>
  #conversationDiv{
    cursor: pointer;
    border-style: solid;
    border-color: white;
    min-height: 50px;
    padding: 2%;
    margin-left: 2%;
    width: 100%;
    margin-bottom: 1%;
    border-radius: 5px;
  }

  #conversationDiv:hover{
    border-color:  #337ab7;
  }

  #indicatorSpan {
    background-color: #337ab7;
    color: white;
    width: 30px;
    height: 30px;
    float: left;
    padding: 1%;
    border-radius: 50%;
  }
</style>
