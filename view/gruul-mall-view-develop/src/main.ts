/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-03-18 16:50:10
 * @LastEditors: vikingShip
 * @LastEditTime: 2022-04-15 15:28:30
 */
import { createApp } from "vue";

import App from "./App.vue";
import router from "./router";
import store from "./store";
import { cusDirective } from "@/directive";
import "element-plus/dist/index.css";
import "@/assets/css/font/iconfont.css";
import "@/assets/css/base.scss";
import ElementPlus from "element-plus";

const app = createApp(App);
//全局函数与属性
import { get, post } from "./apis/http";

app.config.globalProperties.$get = get;
app.config.globalProperties.$post = post;
app.use(ElementPlus).use(router).use(store).use(cusDirective).mount("#app");
