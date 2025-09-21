import throttle from '@/utils/throttle'
import type { DirectiveBinding, ObjectDirective } from 'vue'
/**
 * v-debounce v-throttle 参数
 * @param func 执行函数
 * @param wait 等待时间
 * @param immediate 立即执行标识符
 * @param params 执行函数参数
 * @param type 选择debounce throttle类型
 */
/**
 * 示例
 *  v-throttle="{
    func: funcallback,
    wait: 2000,
    type: 'scroll',
    params: [1, 2],
    }"
    funcallback:第一个参数为元素信息,后续为参数params
 */
interface DirectiveOptions {
    func: FN
    wait: number
    params: any[]
    type: string
}
interface resizeHtml extends HTMLElement {
    $type: string
    $handle: () => void
}
const throttle_config: ObjectDirective = {
    beforeMount(el: resizeHtml, binding: DirectiveBinding<DirectiveOptions>) {
        let { func, wait = 1000, params = [], type = 'click' } = binding.value
        const proxy = function proxy(this: FN, ...args: any[]) {
            return func.call(this, ...args.concat(params))
        }
        el.$type = type
        el.$handle = throttle(proxy, wait)
        el.addEventListener(el.$type, el.$handle)
    },
    // 安装绑定元素的父组件时...「等同于inserted」
    mounted(el, binding) {},
    // 在包含组件的VNode更新之前...
    beforeUpdate() {},
    // 在包含组件的VNode及其子VNode更新后...「等同于componentUpdated」
    updated() {},
    // 在卸载绑定元素的父组件之前...
    beforeUnmount() {},
    // 指令与元素解除绑定且父组件已卸载时...「等同于unbind」
    unmounted(el) {
        el.removeEventListener(el.$type, el.$handle)
    },
}

export default throttle_config
