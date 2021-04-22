<template>
  <modal :width="'400px'">
    <span slot="close" id="closeSymbol" @click.prevent="close">x</span><br>
    <h3 slot="header">
      Choose Emoji!
    </h3>
    <div slot="body">
      <v-emoji-picker @select="choose"/>
    </div>
  </modal>
</template>

<script>
import Modal from '@/components/modals/GenericModal';
import { VEmojiPicker } from 'v-emoji-picker';

export default {
  name: 'EmojiSelection',
  emits: ['select', 'close'],
  components: { Modal, VEmojiPicker },
  methods: {
    choose(emoji) {
      this.$emit('select', emoji);
    },
    close() {
      this.$emit('close');
    },
  },
};
</script>
