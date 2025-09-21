/**
 * 示例
 *  v-debounce="{
    func: funcallback,
    wait: 2000,
    type: 'click',
    params: [1, 2],
    }"
    funcallback:第一个参数为元素信息,后续为参数params
 */
import debounce from '@/utils/debounce'
import type { DirectiveBinding, ObjectDirective } from 'vue'

/**
 * v-debounce v-throttle 参数
 * @param func 执行函数
 * @param wait 等待时间
 * @param immediate 立即执行标识符
 * @param params 执行函数参数
 * @param type 选择debounce throttle类型
 */
interface DirectiveOptions {
    func: FN
    wait: number
    immediate: boolean
    params: any[]
    type: string
}
interface resizeHtml extends HTMLElement {
    $type: string
    $handle: () => void
}
const common_config: ObjectDirective = {
    beforeMount(el: resizeHtml, binding: DirectiveBinding<DirectiveOptions>) {
        let { func, wait = 2000, immediate = true, params = [], type = 'click' } = binding.value
        const proxy = function proxy(this: FN, ...args: any[]) {
            return func.call(this, ...args.concat(params))
        }
        el.$type = type
        el.$handle = debounce(proxy, wait, immediate)
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

export default common_config
