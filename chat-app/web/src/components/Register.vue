<template>
 <div id="container" class="container">
   <div id="rowDiv" class="row">
     <h1 class="text-center registerTitle">Be A Member!</h1>
     <input-form :fields="formItems"
                :error-message="getErrorRegisterMessage"
                :on-focus="removeErrorMessage"
                :on-submit="register"/>
     <div id="alreadyAccount">
       <i>Already an account? </i>
       <router-link :to = "{ path:'/' }">Sign in here</router-link>
     </div>
   </div>
 </div>
</template>

<script>
  import { mapActions, mapGetters, mapMutations } from 'vuex';
  import InputForm from '@/components/InputForm';

  export default {
    name: 'Register',
    components: { InputForm },
    data () {
      return {
        formItems: [
          { type: 'text', id: 'username', text: 'Username', placeholder: 'enter username here' },
          { type: 'text', id: 'email', text: 'Email', placeholder: 'enter mail here' },
          { type: 'password', id: 'pass', text: 'Password', placeholder: 'enter password here' }
        ],
        showModal: false,
      };
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
      register(values) {
        if (!this.validateFields(values)) return;
        const newUserEntry = {
          email: values[1],
          password: values[2],
          username: values[0]
        };
        this.createUserProfile(newUserEntry);
      },
      validateFields(values) {
        if (!values[0] || !values[1] || !values[2]) {
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
    },
  };
</script>

<style scoped>
  .formContainer {
    text-align: left;
  }

  .registerTitle {
    margin-top: 5%;
  }

  #container {
    margin: 0 auto;
    width: 100%;
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
