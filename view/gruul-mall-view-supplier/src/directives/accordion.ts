import type { DirectiveBinding, ObjectDirective } from 'vue'
let record = {
    height: '',
    paddingTop: '',
    paddingBottom: '',
    paddingLeft: '',
    paddingRight: '',
}
const accordion: ObjectDirective = {
    beforeMount(el: HTMLElement, binding: DirectiveBinding) {
        setTimeout(function () {
            const getComputedStyle = window.getComputedStyle(el)
            if (!record.height) {
                record.height = el.offsetHeight.toString()
                record.paddingTop = getComputedStyle.paddingTop
                record.paddingBottom = getComputedStyle.paddingBottom
                record.paddingLeft = getComputedStyle.paddingLeft
                record.paddingRight = getComputedStyle.paddingRight
            }
            el.style.cssText = `height:${el.offsetHeight}px;transition:all .5s;overflow: hidden;`
        }, 100)
    },
    updated(el: HTMLElement, binding: DirectiveBinding) {
        if (!binding.value) {
            el.style.height = '0px'
            el.style.padding = '0px'
        } else {
            const { height, paddingBottom, paddingLeft, paddingRight, paddingTop } = record
            el.style.height = `${height}px`
            el.style.padding = `${paddingTop} ${paddingRight} ${paddingBottom} ${paddingLeft}`
        }
    },
}

export default accordion
