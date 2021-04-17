<template>
  <div id="container" class="container">
    <div id="rowDiv" class="row">
      <InputForm :fields="formItems"
                 :error-message="getErrorLoginMessage"
                 :on-focus="removeErrorMessage"
                 :on-submit="login"/>
      <div id="notRegistered">
        <i>Not registered? </i>
        <router-link :to = "{ path:'register' }">Sign up here</router-link>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapActions, mapGetters, mapMutations, mapState } from 'vuex';
  import InputForm from '@/components/InputForm';

  export default {
    name: 'Login',
    components: { InputForm },
    data () {
      return {
        formItems: [
          { type: 'text', id: 'username', text: 'Username', placeholder: 'enter username here' },
          { type: 'password', id: 'pass', text: 'Password', placeholder: 'enter password here' }
        ],
      };
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
      login(values) {
        if (!this.validateFields(values)) return;
        this.userLogin({ username: values[0], password: values[1] });
      },
      validateFields(values) {
        if (!values[0] || ! values[1]) {
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
  };
</script>

<style scoped>
  .formContainer {
    text-align: left;
  }

  #errorMessageSpan {
    color: red;
  }

  #container {
    margin: 0 auto;
    width: 100%;
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
