<template>
  <div id = 'container'>
    <form>
        <div class = 'form-group'>
          <label for = "email">Username: </label>
          <input id = 'username'
                 type = 'text'
                 class = 'form-control'
                 placeholder = 'enter username here'
                 @focus = 'removeErrorMessage()'
                 v-model = "enteredName">
        </div>
        <div class = 'form-group'>
          <label for = "pass">Password: </label>
          <input id = 'pass'
                 type = 'password'
                 class = 'form-control'
                 placeholder = 'enter password here'
                 @focus = 'removeErrorMessage()'
                 v-model = "enteredPass">
        </div>
        <span id = 'errorMessageSpan' v-if = "getErrorLoginMessage">{{ getErrorLoginMessage }}</span>
        <div id = 'buttonDiv'>
         <button id = 'submitBtn'
                 type = 'submit'
                 class = 'btn btn-primary'
                 @click.prevent = "login">
           Sign In
         </button>
         <router-link :to = "{ path:'register' }"> Not registered? Sign up here</router-link>
       </div>
    </form>
  </div>
</template>

<script>
  import { mapActions, mapGetters, mapMutations, mapState } from 'vuex'

  export default {
    name: 'Login',
    data () {
      return {
        enteredName: '',
        enteredPass: '',
      }
    },
    mounted() {
      this.removeErrorMessage();
    },
    methods: {
      ...mapMutations([
         'setLoginErrorMessage',
      ]),
      ...mapActions([
        'userLogin',
      ]),
      login() {
        if (!this.validateFields()) return;
        this.userLogin({ username: this.enteredName, password: this.enteredPass });
      },
      validateFields() {
        if (!this.enteredPass || !this.enteredName) {
          this.setLoginErrorMessage({ value: 'No field should be empty!' });
          return false;
        }
        return true;
      },
      removeErrorMessage() {
        this.setLoginErrorMessage({ value: '' });
      },
    },
    computed: {
      ...mapGetters([
          'getErrorLoginMessage',
      ]),
    },
  }
</script>

<style scoped>
#errorMessageSpan {
  color: red;
}

#container{
  padding-left:30%;
  padding-right:30%;
  margin-top:5%;
}

#submitBtn{
  margin-right:2%;
}

#buttonDiv{
  margin-top:4%;
}
</style>
