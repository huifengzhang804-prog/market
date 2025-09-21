import directives from '@/directives'
import selectLoad from '@/directives/selectLoad'
import { VueDraggableNext } from 'vue-draggable-next'
import Vue3DraggableResizable from 'vue3-draggable-resizable'
import VueDOMPurifyHTML from 'vue-dompurify-html'

export const initPlugin = {
    install(Vue: import('vue').App<any>) {
        // 注册指令
        registerDirective(Vue)
        Vue.directive('loadMore', selectLoad)
        // 注册组件
        registerComponent(Vue)
        Vue.use(VueDOMPurifyHTML)
    },
}
const registerDirective = (Vue: import('vue').App<any>) => {
    Object.keys(directives).forEach((item) => {
        Vue.directive(item, directives[item as keyof typeof directives])
    })
}
const registerComponent = (Vue: import('vue').App<any>) => {
    Vue.component('VueDraggableNext', VueDraggableNext)
    Vue.component('VueDragResize', Vue3DraggableResizable)
}
