import directives from '@/directives'
import { VueDraggableNext } from 'vue-draggable-next'
import VueCropper from 'vue-cropperjs'
import Vue3DraggableResizable from 'vue3-draggable-resizable'
import VueDOMPurifyHTML from 'vue-dompurify-html'
export const initPlugin = {
    install(Vue: import('vue').App<any>) {
        // 注册指令
        registerDirective(Vue)
        // 注册自定义UI库
        registerComponent(Vue)
        Vue.use(VueDOMPurifyHTML)
    },
}
const registerComponent = (Vue: import('vue').App<any>) => {
    Vue.component('VueDraggableNext', VueDraggableNext)
    Vue.component('VueCropper', VueCropper)
    Vue.component('VueDragResize', Vue3DraggableResizable)
}
const registerDirective = (Vue: import('vue').App<any>) => {
    Object.keys(directives).forEach((item) => {
        Vue.directive(item, directives[item as keyof typeof directives])
    })
}
