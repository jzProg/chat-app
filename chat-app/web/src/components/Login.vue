<template>
  <div id='container' class="container">
    <div id="rowDiv" class="row">
      <form>
          <div class="formContainer">
            <div class='form-group'>
              <label for="email">Username</label>
              <input id='username'
                     type='text'
                     class='form-control'
                     placeholder='enter username here'
                     @focus='removeErrorMessage()'
                     v-model="enteredName">
            </div>
            <div class='form-group'>
              <label for="pass">Password</label>
              <input id='pass'
                     type='password'
                     class='form-control'
                     placeholder='enter password here'
                     @focus='removeErrorMessage()'
                     v-model="enteredPass">
            </div>
          </div>
          <span id='errorMessageSpan' v-if="getErrorLoginMessage">
            {{ getErrorLoginMessage }}
          </span><br/>
          <button id='submitBtn'
                 type='submit'
                 class='btn btn-primary'
                 @click.prevent="login">
            Sign In
          </button>
          <div id="notRegistered">
            <i>Not registered? </i>
            <router-link :to = "{ path:'register' }">Sign up here</router-link>
          </div>
      </form>
    </div>
  </div>
</template>

<script>
  import { mapActions, mapGetters, mapMutations, mapState } from 'vuex';

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
  .formContainer {
    text-align: left;
  }

  #rowDiv {
    margin-top: 20%;
  }

  #errorMessageSpan {
    color: red;
  }

  #container {
    margin: 0 auto;
    width: 20%;
  }

  #submitBtn {
    margin-right: 2%;
  }

  #notRegistered {
    margin-top: 2%;
  }

  @media only screen and (max-width: 750px) {
    #container {
      width: 40%;
    }
}
</style>
