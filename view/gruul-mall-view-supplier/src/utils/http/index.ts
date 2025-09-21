import { QAxios } from './qAxios'
import { checkStatus } from './checkStatus'
import { deepMerge } from '@/utils/dataProcess'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElNotification } from 'element-plus'
import urlManager from './correctUrl'
import type { CreateAxiosOptions, AxiosTransform } from './type'
import type { AxiosResponse } from 'axios'
import { Result } from '#/types/axios'
const transform: AxiosTransform = {
    // 发送请求前钩子
    beforeRequestHook: (config, options) => {
        console.log('config3', config)
        //单体应用矫正正则
        if (config.url) {
            const baseUrl = options.isMock ? `http://localhost:${import.meta.env.VITE_SERVER_PORT}/shop/` : import.meta.env.VITE_BASE_URL
            config.url = baseUrl + urlManager.getUrl(config.url, options)
        }
        return config
    },
    // 请求成功
    transformRequestHook: (res: AxiosResponse<any>, options) => {
        return res
    },
    // 请求拦截器
    requestInterceptors: (config, options) => {
        const { token, id } = useShopInfoStore().getterShopInfo
        if (token) {
            config.headers.Authorization = token
        }
        if (id) {
            config.headers['Shop-Id'] = id
        }
        return config
    },
    // 响应拦截器
    responseInterceptors: (res: AxiosResponse<any>) => {
        console.log('res.data', res.data)
        return res.data
    },
    // 请求之前的拦截器错误处理
    requestInterceptorsCatch: (error: Error) => {
        return error
    },
    // 请求之后的拦截器错误处理
    responseInterceptorsCatch: (error: any) => {
        const { response, code, message, config } = error || {}
        const msg: string = response?.data?.message ?? ''
        const err: string = error?.toString?.() ?? ''
        console.log(' res.code', code)

        if (err.includes('Network Error')) {
            ElNotification.error('网络异常')
            return
        }
        if (message.includes('timeout')) {
            ElNotification.error('服务超时')
            return
        }
        checkStatus(error?.response?.status)
        return Promise.reject(error)
    },
}
function createAxios(options?: Partial<CreateAxiosOptions>) {
    return new QAxios(
        deepMerge(
            {
                headers: {
                    'Client-Type': import.meta.env.VITE_CLIENT_TYPE,
                    'Device-Id': 1,
                    post: {
                        'Content-Type': 'application/json',
                    },
                    put: {
                        'Content-Type': 'application/json',
                    },
                },
                timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
                transform,
                // 接口可配置参数
                requestOptions: {
                    isSingleServer: /^true$/i.test(import.meta.env.VITE_IS_SINGLE),
                    isMock: false,
                },
            },
            options || {},
        ),
    )
}
export const http = createAxios()
