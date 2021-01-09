<template>
  <form class="formContainer">
      <div class='form-group'
           v-for="(field, index) in fields">
          <label :for="field.id">{{ field.text }}</label>
          <input :id="field.id"
                 :type="field.type"
                 class='form-control'
                 :placeholder='field.placeholder'
                  @focus="onFocus()"
                 v-model="form.values[index]">
      </div>
      <span id='errorMessageSpan' v-if="errorMessage">{{ errorMessage }}</span>
      <div id='buttonDiv'>
       <button id='submitBtn'
               type='submit'
               class='btn btn-primary'
               @click.prevent="onSubmit(form.values)">
         Submit
       </button>
     </div>
  </form>
</template>


<script>
  export default {
    name: 'Form',
    props: ['fields', 'errorMessage', 'onSubmit', 'onFocus'],
    data () {
      return {
        form: {
          values: []
        }
      }
    },
  }
</script>

<style scoped>
  .formContainer {
    text-align: left;
    margin: 0 auto;
    margin-top: 2%;
    width: 20%;
  }

  @media only screen and (max-width: 750px) {
    .formContainer {
      width: 50%;
    }
  }

  #errorMessageSpan {
    color: red;
  }

  #submitBtn {
    margin-right: 2%;
  }

  #buttonDiv{
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 4%;
  }
</style>
