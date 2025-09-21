import axios from 'axios'
import { AxiosCanceler } from './axiosCancel'
import { isFunction } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import type { CreateAxiosOptions } from './type'
import type { RequestOptions, Result } from '#/types/axios'
export class QAxios {
    private axiosInstance: AxiosInstance
    private options: CreateAxiosOptions
    constructor(options: CreateAxiosOptions) {
        this.options = options
        this.axiosInstance = axios.create(options)
        this.setupInterceptors()
    }
    private getTransform() {
        const { transform } = this.options
        return transform
    }
    // 拦截器配置
    private setupInterceptors() {
        const transform = this.getTransform()
        if (!transform) {
            return
        }
        const { requestInterceptors, requestInterceptorsCatch, responseInterceptors, responseInterceptorsCatch } = transform
        const axiosCanceler = new AxiosCanceler()
        // 请求拦截器
        this.axiosInstance.interceptors.request.use((config: AxiosRequestConfig) => {
            axiosCanceler.addPending(config)
            if (requestInterceptors && isFunction(requestInterceptors)) {
                config = requestInterceptors(config, this.options)
            }
            return config
        }, undefined)
        // 请求错误拦截
        if (requestInterceptorsCatch && isFunction(requestInterceptorsCatch)) {
            this.axiosInstance.interceptors.request.use(undefined, requestInterceptorsCatch)
        }
        // 响应拦截
        this.axiosInstance.interceptors.response.use((res: AxiosResponse<any>) => {
            if (res) {
                axiosCanceler.removePending(res.config)
            }
            if (responseInterceptors && isFunction(responseInterceptors)) {
                res = responseInterceptors(res)
            }
            return res
        })
        // 响应错误拦截
        if (responseInterceptorsCatch && isFunction(responseInterceptorsCatch)) {
            this.axiosInstance.interceptors.response.use(undefined, responseInterceptorsCatch)
        }
    }
    request<T = any>(config: AxiosRequestConfig, options?: RequestOptions): Promise<T> {
        let conf: CreateAxiosOptions = cloneDeep(config)
        const transform = this.getTransform()
        const { requestOptions } = this.options
        const opt: RequestOptions = Object.assign({}, requestOptions, options)
        const { beforeRequestHook, requestCatchHook, transformRequestHook } = transform || {}
        if (beforeRequestHook && isFunction(beforeRequestHook)) {
            conf = beforeRequestHook(conf, opt)
        }
        conf.requestOptions = opt
        return new Promise((resolve, reject) => {
            this.axiosInstance
                .request<any, AxiosResponse<Result>>(conf)
                .then((res: AxiosResponse<Result>) => {
                    if (transformRequestHook && isFunction(transformRequestHook)) {
                        try {
                            const ret = transformRequestHook(res, opt)
                            resolve(ret)
                        } catch (err) {
                            reject(err || new Error('request error!'))
                        }
                        return
                    }
                    resolve(res as unknown as Promise<T>)
                })
                .catch((e: Error | AxiosError) => {
                    if (requestCatchHook && isFunction(requestCatchHook)) {
                        reject(requestCatchHook(e, opt))
                        return
                    }
                    reject(e)
                })
        })
    }
    get<T = any>(config: AxiosRequestConfig, options?: RequestOptions): Promise<Result<T>> {
        return this.request({ ...config, method: 'GET' }, options)
    }
    post<T = any>(config: AxiosRequestConfig, options?: RequestOptions): Promise<Result<T>> {
        return this.request({ ...config, method: 'POST' }, options)
    }
    put<T = any>(config: AxiosRequestConfig, options?: RequestOptions): Promise<Result<T>> {
        return this.request({ ...config, method: 'PUT' }, options)
    }
    delete<T = any>(config: AxiosRequestConfig, options?: RequestOptions): Promise<Result<T>> {
        return this.request({ ...config, method: 'DELETE' }, options)
    }
}
