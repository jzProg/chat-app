<template>
 <div>
    <input type="text"
          v-model="user"
          style="margin:5%"
          @input="search()"
          @click.prevent=""
          placeholder="Search Members">
     <i class="fas fa-search"></i>
     <div v-for="(cand, index) in candidates" @click.prevent="addMember(cand, index)" class="candidate">
        <input type="checkbox" :id="cand.username">
         <label :for="cand.username">{{ cand.username }}</label>
     </div>
     <div v-for="(member, index) in members"
          style="text-align: center"
          class="member">
       {{ member }}
     </div>
 </div>
</template>

<script>
  import { mapActions } from 'vuex';

  export default {
    name: 'search-user',
    props: {
      members: Array,
      updateMembers: Function,
    },
    data() {
      return {
        user: '',
        candidates: [],
      };
    },
    methods: {
      ...mapActions([
        'searchUsers',
      ]),
      addMember(member, index) {
        this.updateMembers(member.username);
        this.candidates.splice(index, 1);
      },
      search() {
        this.searchUsers(this.user).then(response => {
          this.candidates = response.data;
        });
      },
    }
  };
</script>

<style scoped>
 .member {
   background-color: green;
   color: white;
   width: 50%;
   border-radius: 15px;
   margin: 0 auto 2%;
 }
 .candidate {
   cursor: pointer;
 }
</style>
