import Vue from 'vue'
import Router from 'vue-router'
import Welcome from '@/components/Welcome'
import Home from '@/components/Home'
import Register from '@/components/Register'
import Login from '@/components/Login'
import Messages from '@/components/MessagesList'
import Conversations from '@/components/ConversationList'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      component: Welcome,
      children: [
        {
          path: 'register',
          name: 'Register',
          component: Register
        },
        {
          path: '',
          name: 'Login',
          component: Login
        },
      ],
      beforeEnter: (to, from, next) => {
        if (localStorage.getItem('token')) next('/home');
        else next();
      }
    },
    {
      path: '/home/',
      component: Home,
      children: [
        {
          path: '',
          name: 'Conversations',
          component: Conversations
        },
      ],
      beforeEnter: (to, from, next) => {
        if (localStorage.getItem('token')) next();
        else next('/');
      }
    },
  ]
})
