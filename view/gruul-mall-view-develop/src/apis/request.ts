/*
 * @description:
 * @Author: vikingShip
 * @Date: 2022-03-15 09:24:01
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:21:56
 */
/**
 * axios api网络请求 设置拦截
 * @author 张治保
 */
import axios, { AxiosResponse } from "axios";
import store from "../store/index";
import router from "../router/index";
import { R } from "./http.type";

axios.defaults.headers.post["Content-Type"] = "application/json";
axios.defaults.headers.put["Content-Type"] = "application/json";

const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
  withCredentials: false,
  headers: {
    "Shop-Id": "0",
    "Client-Type": "DEVELOPER_CONSOLE",
    Platform: "PC",
  },
});

//请求拦截器
request.interceptors.request.use(
  (config) => {
    // @ts-ignore
    const token = store.state.user.token;
    if (!!token) {
      config.headers.Authorization = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

//响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<R>) => {
    const result = response.data;
    if (response.status != 200 || !result) {
      console.log("!!!!!response");
      return Promise.reject({
        msg: "服务器异常",
      });
    }
    const code = result.code;
    if (code === 200) {
      return Promise.resolve(response);
    }
    if (code === 4) {
      store.commit("user/clear");
      router
        .push({
          path: "/login",
          query: {
            redirect: router.currentRoute.value.fullPath,
          },
        })
        .catch((fail) => {
          console.log("跳转失败", fail);
        });
    }
    return Promise.reject(result);
  },
  (error) => {
    console.log("error[interceptors.response]");
    return Promise.reject(error);
  }
);

export default request;
