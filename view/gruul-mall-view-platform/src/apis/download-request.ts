import axios from 'axios'
import { useAdminInfo } from '@/store/modules/admin'
// 取消请求
import { AxiosCanceler } from '@/utils/http/axiosCancel'
import { uuidHandle } from '@/apis/request'

const axiosCance = new AxiosCanceler()
axios.defaults.headers.post['Content-Type'] = 'application/json'
axios.defaults.headers.put['Content-Type'] = 'application/json'
const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
    withCredentials: false,
    headers: {
        'Device-Id': uuidHandle(),
        'Client-Type': 'PLATFORM_CONSOLE',
        'Shop-Id': '0',
        Platform: 'PC',
    },
})
//是否是单体应用
const isSingle = import.meta.env.VITE_IS_SINGLE && import.meta.env.VITE_IS_SINGLE.toLowerCase() === 'true'
//单体应用矫正正则
const singleUrlCorrectRegex = /\/?.*?\//
//url矫正函数
const urlCorrect = (currentUrl: undefined | string) => {
    return !currentUrl ? currentUrl : isSingle ? currentUrl.replace(singleUrlCorrectRegex, '/') : currentUrl
}
// 是否正在刷新的标记
let isRefreshing = false
// 重试队列
let requests: any = []
//请求拦截器
request.interceptors.request.use(
    (config) => {
        const token = useAdminInfo().getterToken
        // @ts-ignore
        if (!isRefreshing && token) {
            config.headers.Authorization = token
        }
        config.url = urlCorrect(config.url)
        axiosCance.addPending(config)
        return config
    },
    (error) => {
        return Promise.reject(error)
    },
)

export default request
