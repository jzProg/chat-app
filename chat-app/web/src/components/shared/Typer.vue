<template functional>
 <div>
   <span style="text-align: left">{{ props.typer }} is typing</span><br>
   <div class="typing">
    <div class="typing__dot"></div>
    <div class="typing__dot"></div>
    <div class="typing__dot"></div>
   </div>
 </div>
</template>

<style scoped>
  .typing {
    width: 4em;
    height: 2em;
    padding: 10px;
    margin: 0 auto;
    margin-bottom: 1%;
    background: #e6e6e6;
    border-radius: 20px;
  }

  .typing__dot {
  float: left;
  width: 4px;
  height: 4px;
  margin: 0 4px;
  background: #8d8c91;
  border-radius: 50%;
  opacity: 0;
  animation: loadingFade 1s infinite;
  }

  .typing__dot:nth-child(1) {
  animation-delay: 0s;
  }

  .typing__dot:nth-child(2) {
  animation-delay: 0.2s;
  }

  .typing__dot:nth-child(3) {
  animation-delay: 0.4s;
  }

  @keyframes loadingFade {
    0% {
      opacity: 0;
    }
    50% {
      opacity: 0.8;
    }
    100% {
      opacity: 0;
    }
  }
</style>
