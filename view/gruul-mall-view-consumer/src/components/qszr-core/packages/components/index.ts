import Auth from '@/components/auth/auth.vue'
const enumComponents = [{ name: 'Auth', component: Auth }]
export const qszrUI = {
  install(Vue: import('vue').App<any>) {
    enumComponents.forEach((item) => {
      Vue.component(item.name, item.component)
    })
  },
}
