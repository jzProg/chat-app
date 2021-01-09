<template>
 <div id='container' class="container">
   <div id="rowDiv" class="row">
     <h1 class='text-center' style="margin-top: 20%">Be A Member!</h1>
     <form style="margin-top: 7%">
        <div class="formContainer">
         <div class='form-group'>
           <label for="username">Username</label>
           <input id='username'
                  type ='text'
                  class='form-control'
                  @focus='removeErrorMessage()'
                  placeholder= 'enter username here'
                  v-model="enteredName">
         </div>
         <div class='form-group'>
           <label for="mail">Email</label>
           <input id='mail'
                  type='email'
                  class='form-control'
                  @focus='removeErrorMessage()'
                  placeholder='enter mail here'
                  v-model="enteredMail">
         </div>
         <div class='form-group'>
           <label for="pass">Password</label>
           <input id='pass'
                  type='password'
                  class='form-control'
                  @focus='removeErrorMessage()'
                  placeholder='enter password here'
                  v-model="enteredPass">
         </div>
        </div>
        <span v-if="getErrorRegisterMessage" id="errorRegisterSpan">
          {{ getErrorRegisterMessage }}
        </span><br/>
        <button id='submitBtn'
                type='submit'
                class='btn btn-primary'
                @click.prevent="register">
                Sign Up
        </button>
        <div id="alreadyAccount">
          <i>Already an account? </i>
          <router-link :to = "{ path:'/' }">Sign in here</router-link>
        </div>
       </form>
   </div>
 </div>
</template>

<script>
  import { mapActions, mapGetters, mapMutations } from 'vuex';

  export default {
    name: 'Register',
    data () {
      return {
        enteredName: '',
        enteredMail: '',
        enteredPass: '',
        showModal: false,
      }
    },
    mounted() {
      this.removeErrorMessage();
    },
    methods: {
      ...mapMutations([
          'setRegisterErrorMessage',
      ]),
      ...mapActions([
        'createUserProfile',
      ]),
      register() {
        if (!this.validateFields()) return;
        const newUserEntry = {
          email: this.enteredMail,
          password: this.enteredPass,
          username: this.enteredName
        };
        this.createUserProfile(newUserEntry);
      },
      validateFields() {
        if (!this.enteredMail || !this.enteredPass || !this.enteredName) {
          this.setRegisterErrorMessage({ value: 'No field should be empty!' });
          return false;
        }
        return true;
      },
      removeErrorMessage() {
        this.setRegisterErrorMessage({ value: '' });
      },
    },
    computed: {
      ...mapGetters([
          'getErrorRegisterMessage',
      ])
    }
  }
</script>

<style scoped>
  .formContainer {
    text-align: left;
  }

  #container {
    margin: 0 auto;
    width: 20%;
  }

  #submitBtn {
    margin-right: 2%;
  }

  #alreadyAccount {
    margin-top: 2%;
  }

  #errorRegisterSpan {
    color: red;
  }

  @media only screen and (max-width: 750px) {
    #container {
      width: 40%;
    }
  }
</style>
