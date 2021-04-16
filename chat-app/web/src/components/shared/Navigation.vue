<template functional>
  <div>
    <span :disabled="!props.hasPrev()">
      <i class="fas fa-chevron-circle-left fa-2x navigation" @click.prevent="props.goToPrev()"/>
    </span>
    <span :disabled="!props.hasNext()">
      <i class="fas fa-chevron-circle-right fa-2x navigation" @click.prevent="props.goToNext()"/>
    </span>
  </div>
</template>

<style scoped>
  .navigation {
    cursor: pointer;
  }

  span[disabled] {
    opacity: 0.5;
    pointer-events: none;
  }
</style>
