<template>
  <Modal>
    <span slot="close" id="closeSymbol" @click.prevent="close">x</span><br>
    <h3 slot="header">
      Choose Emoji!
    </h3>
    <div slot="body">
      <div class="container" style="width: 100%">
         <div class="row"
              v-for="(emojiRow, index) in Math.ceil((emojis.length)/4)"
              style="width: 100%"
              :key="index">
               <div v-for="(emoji, ind) in emojis"
                    :class="['col-md-' + Math.ceil(12/(emojis.length/4))]"
                    @click.prevent="choose(emoji)"
                    v-if="ind >= index*Math.floor((emojis.length)/(emojis.length/4)) && ind < index*Math.floor((emojis.length)/(emojis.length/4)) + Math.floor((emojis.length)/4)">
                 {{ String.fromCodePoint(emoji) }}
               </div>
         </div>
    </div>
    </div>
  </Modal>
</template>

<script>
import Modal from '@/components/modals/GenericModal';

export default {
  name: 'EmojiSelection',
  emits: ['select', 'close'],
  components: { Modal },
  data() {
    return {
      emojis:[ 0x1F600, 0x1F603, 0x1F604, 0x1F601,
               0x1F606, 0x1F605, 0x1F923, 0x1F602,
               0x1F642,	0x1F643, 0x1F609,	0x1F60A,
               0x1F607, 0x1F970, 0x1F60D, 0x1F618],
    };
  },
  methods: {
    choose(emoji) {
      this.$emit('select', emoji);
    },
    close() {
      this.$emit('close');
    }
  }
}
</script>
