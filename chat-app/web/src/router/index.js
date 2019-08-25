import Vue from 'vue'
import Router from 'vue-router'
import Welcome from '@/components/Welcome'
import Home from '@/components/Home'
import Register from '@/components/Register'
import Profile from '@/components/Profile'
import Login from '@/components/Login'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Welcome',
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
      path: '/home',
      name: 'Home',
      component: Home,
      children: [
        {
          path: '/profile',
          name: 'Profile',
          component: Profile
        },
      ],
      beforeEnter: (to, from, next) => {
        if (localStorage.getItem('token')) next();
        else next('/');
      }
    }
  ]
})
