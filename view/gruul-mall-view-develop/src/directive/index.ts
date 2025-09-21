/*
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-04-15 13:41:19
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-04-15 15:38:22
 */
import debounce from "./debounce"
export const cusDirective={
    install(Vue:import("vue").App<any>){
        Vue.directive('debounce',debounce)
        Vue.directive('throttle',debounce)
    }
}
