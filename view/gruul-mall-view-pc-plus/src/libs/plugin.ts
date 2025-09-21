import directives from '@/directive/index'
import VueDOMPurifyHTML from 'vue-dompurify-html'

export const installs = {
  install(Vue: import('vue').App<any>) {
    directives.forEach((item) => {
      Vue.directive(item.name, item.val)
    })
    Vue.use(VueDOMPurifyHTML)
  },
}
