import { AxiosRequestConfig, AxiosResponse } from 'axios'
import { RequestOptions, Result } from '#/types/axios'
export interface CreateAxiosOptions extends AxiosRequestConfig {
    transform?: AxiosTransform
    requestOptions?: RequestOptions
}
export abstract class AxiosTransform {
    // 发送请求前钩子
    beforeRequestHook?: (config: AxiosRequestConfig, options: RequestOptions) => AxiosRequestConfig
    // 请求成功
    transformRequestHook?: (res: AxiosResponse<Result>, options: RequestOptions) => any
    // 请求失败处理
    requestCatchHook?: (e: Error, options: RequestOptions) => Promise<any>
    // 请求之前的拦截器
    requestInterceptors?: (config: AxiosRequestConfig, options: CreateAxiosOptions) => AxiosRequestConfig
    // 请求之后的拦截器
    responseInterceptors?: (res: AxiosResponse<any>) => AxiosResponse<any>
    // 请求之前的拦截器错误处理
    requestInterceptorsCatch?: (error: Error) => void
    // 请求之后的拦截器错误处理
    responseInterceptorsCatch?: (error: Error) => void
}
