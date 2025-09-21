/**
 * axios api网络请求 设置拦截
 * @author 张治保
 */
import axios, { AxiosResponse } from 'axios'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { AxiosCanceler } from '@/utils/http/axiosCancel'

axios.defaults.headers.post['Content-Type'] = 'application/json'
axios.defaults.headers.put['Content-Type'] = 'application/json'
const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
    withCredentials: false,
    headers: {
        'Client-Type': import.meta.env.VITE_CLIENT_TYPE,
        'Device-Id': 1,
    },
})

// 取消请求的方法
const axiosCance = new AxiosCanceler()

//是否是单体应用
const isSingle = import.meta.env.VITE_IS_SINGLE && import.meta.env.VITE_IS_SINGLE.toLowerCase() === 'true'
//单体应用矫正正则
const singleUrlCorrectRegex = /\/?.*?\//
//矫正url函数
const urlCorrect = (currentUrl: undefined | string) => {
    return !currentUrl ? currentUrl : isSingle ? currentUrl.replace(singleUrlCorrectRegex, '/') : currentUrl
}

//请求拦截器
request.interceptors.request.use(
    (config) => {
        const { token, id } = useShopInfoStore().getterShopInfo
        if (!isRefreshing && token) {
            config.headers.Authorization = token
        }
        if (id) {
            config.headers['Shop-Id'] = id
        }
        config.url = urlCorrect(config.url)
        axiosCance.addPending(config)
        return config
    },
    (error) => {
        return Promise.reject(error)
    },
)
let isRefreshing = false
export default request
