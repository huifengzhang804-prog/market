import { DirectiveBinding } from 'vue'

interface Bind extends DirectiveBinding {
    name: string
}
export default {
    beforeMount() {},
    mounted() {},
    beforeUpdate() {},
    updated(el: HTMLElement, binding: Bind) {
        const child = el.querySelector('.el-tooltip__trigger') as HTMLElement
        const id = child?.getAttribute('aria-describedby') as string
        if (!id) return
        const poper = document.getElementById(id)?.parentNode
        const SELECTDOWN_DOM = poper?.querySelector('.el-scrollbar .el-select-dropdown__wrap') as HTMLElement
        if (SELECTDOWN_DOM) {
            let { fn } = binding.value
            SELECTDOWN_DOM.addEventListener('scroll', function () {
                // console.log(el);
                const CONDITION = SELECTDOWN_DOM.scrollHeight - SELECTDOWN_DOM.scrollTop <= SELECTDOWN_DOM.clientHeight
                if (CONDITION) {
                    fn()
                }
                // if (CONDITION) {
                //   if (!document.getElementById("selectLoading")) {
                //     const loadingNode = document.createElement("div");
                //     loadingNode.innerHTML = "loading....";
                //     loadingNode.setAttribute("id", "selectLoading");
                //     SELECTDOWN_DOM.appendChild(loadingNode);
                //     binding.value(loadingNode);
                //   }
                // } else {
                //   const loadingNode = document.getElementById(
                //     "selectLoading"
                //   ) as HTMLElement;
                //   loadingNode.innerHTML = "";
                // }
            })
        }
    },
    beforeUnmount() {},
    unmounted(el) {
        console.log('unmounted')
        // el.removeEventListener(el.$type, el.$handle);
    },
}
