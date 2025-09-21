/*
 * @description: 
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-05-22 20:41:40
 */
declare module '*.vue' {
    import {defineComponent} from 'vue'
    const component: ReturnType<typeof defineComponent>
    export default component
}
