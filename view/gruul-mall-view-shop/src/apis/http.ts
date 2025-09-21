/**
 * api请求接口封装
 * @author 张治保
 */
import {
    HttpGetInstance,
    HttpPostInstance,
    HttpPutInstance,
    HttpDeleteInstance,
    HttpHeadInstance,
    HttpOptionsInstance,
    HttpPatchInstance,
} from './http-handle'
import { RequestData } from './http.type'

const httpGet = HttpGetInstance
/**
 * get 请求
 * @param config 请求参数配置
 */
const get = <T>(config: RequestData) => {
    return httpGet.request<T>(config)
}

/**
 * post 请求
 * @param config 请求参数配置
 */
const httpPost = HttpPostInstance
const post = <T>(config: RequestData) => {
    return httpPost.request<T>(config)
}
/**
 * put 请求
 * @param config 请求参数配置
 */
const httpPut = HttpPutInstance
const put = <T>(config: RequestData) => {
    return httpPut.request<T>(config)
}
/**
 * delete 请求
 * @param config 请求参数配置
 */
const httpDelete = HttpDeleteInstance
const del = <T>(config: RequestData) => {
    return httpDelete.request<T>(config)
}
/**
 * head 请求
 * @param config 请求参数配置
 */
const httpHead = HttpHeadInstance
const head = <T>(config: RequestData) => {
    return httpHead.request<T>(config)
}
/**
 * options 请求
 * @param config 请求参数配置
 */
const httpOptions = HttpOptionsInstance
const options = <T>(config: RequestData) => {
    return httpOptions.request<T>(config)
}
/**
 * patch 请求
 * @param config 请求参数配置
 */
const httpPatch = HttpPatchInstance
const patch = <T>(config: RequestData) => {
    return httpPatch.request<T>(config)
}

export { get, post, put, del, head, options, patch }
