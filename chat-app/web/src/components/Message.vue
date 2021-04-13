<template>
 <div :class="['messageContainer', { reversed: rightDirection }]">
   <div :class="['contentDiv', { reversed: rightDirection }]">
        <div :style="{ backgroundColor: getColorStyle() }" class="messageDiv">
          <h4><b>{{ author }}</b></h4>
          <h4 v-html="content"/>
        </div>
   </div>
   <div class="dateDiv">
     {{ new Date(date).toLocaleString() }}
   </div>
 </div>
</template>


<script>

export default {
  name: 'Message',
  props: ['content', 'author', 'color', 'date', 'rightDirection'],
  methods: {
    getColorStyle() {
      const { r, g, b } = this.color;
      return `rgba(${r}, ${g}, ${b}, 0.5)`;
    }
  }
}
</script>

<style scoped>
   .messageContainer {
     display: flex;
     flex-direction: row;
   }

   .messageDiv {
     width: 40%;
     border-radius: 14px;
     text-align: left;
     padding: 2%;
     margin: 1%;
   }

  .reversed {
    display: flex;
    flex-direction: row-reverse;
  }

  .dateDiv {
    color: lightgrey;
    margin-top: 2%;
  }

  .contentDiv {
    width: 75%;
  }
</style>
