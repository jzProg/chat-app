<template>
  <div>
    <h3><i>You don't have any conversation yet...</i></h3>
    <button type="button"
          class="btn btn-primary"
          @click.prevent="$emit('addConversation')">
          Create Conversation
    </button>
  </div>
</template>

<script>
  export default {
    name: 'empty-conversation-list-state',
    emits: ['addConversation'],
  };
</script>
