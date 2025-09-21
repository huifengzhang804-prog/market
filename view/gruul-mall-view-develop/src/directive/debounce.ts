/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-04-15 13:41:42
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-04-15 17:09:51
 */
import { Fn } from "@vueuse/shared";
import { DirectiveBinding } from "vue";
import { debounce, throttle } from "@/utils";
/**
 * @LastEditors: vikingShip
 * @description: v-debounce v-throttle 参数
 * @param func 执行函数
 * @param wait 等待时间
 * @param immediate 立即执行标识符
 * @param params 执行函数参数
 * @param type 选择debounce throttle类型
 */
interface DirectiveOptions {
  func: Fn;
  wait: string | number;
  immediate: boolean;
  params: any[];
  type: string;
}
interface Bind extends DirectiveBinding<DirectiveOptions> {
  name: string;
}
const common_config = {
  beforeMount(el, binding: Bind) {
    let {
      func,
      wait = 300,
      immediate = false,
      params = [],
      type = "click",
    } = binding.value;
    const handle = binding.name === "debounce" ? debounce : throttle,
      proxy = function proxy(...args: any[]) {
        return func.call(this, ...params.concat(args));
      };
    el.$type = type;
    el.$handle = handle(proxy, wait, immediate);
    el.addEventListener(el.$type, el.$handle);
  },
  // 安装绑定元素的父组件时...「等同于inserted」
  mounted() {},
  // 在包含组件的VNode更新之前...
  beforeUpdate() {},
  // 在包含组件的VNode及其子VNode更新后...「等同于componentUpdated」
  updated() {},
  // 在卸载绑定元素的父组件之前...
  beforeUnmount() {},
  // 指令与元素解除绑定且父组件已卸载时...「等同于unbind」
  unmounted(el) {
    el.removeEventListener(el.$type, el.$handle);
  },
};

export default common_config;
