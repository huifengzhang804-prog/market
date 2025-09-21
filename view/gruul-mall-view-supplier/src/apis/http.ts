import { HttpGet, HttpPost, HttpPut, HttpDelete, HttpHead, HttpOptions, HttpPatch } from './http-handle'
import { RequestData } from './http.type'

const httpGet = new HttpGet()
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
const httpPost = new HttpPost()
const post = <T>(config: RequestData) => {
    return httpPost.request<T>(config)
}
/**
 * put 请求
 * @param config 请求参数配置
 */
const httpPut = new HttpPut()
const put = <T>(config: RequestData) => {
    return httpPut.request<T>(config)
}
/**
 * delete 请求
 * @param config 请求参数配置
 */
const httpDelete = new HttpDelete()
const del = <T>(config: RequestData) => {
    return httpDelete.request<T>(config)
}
/**
 * head 请求
 * @param config 请求参数配置
 */
const httpHead = new HttpHead()
const head = <T>(config: RequestData) => {
    return httpHead.request<T>(config)
}
/**
 * options 请求
 * @param config 请求参数配置
 */
const httpOptions = new HttpOptions()
const options = <T>(config: RequestData) => {
    return httpOptions.request<T>(config)
}
/**
 * patch 请求
 * @param config 请求参数配置
 */
const httpPatch = new HttpPatch()
const patch = <T>(config: RequestData) => {
    return httpPatch.request<T>(config)
}

export { get, post, put, del, head, options, patch }
