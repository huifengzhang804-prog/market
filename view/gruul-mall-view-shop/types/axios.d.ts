export interface RequestOptions {
    // 将请求参数拼接到url
    joinParamsToUrl?: boolean
    // 请求拼接路径
    urlPrefix?: string
}
/**
 * 响应数据格式
 */
export type Result<T = any> = {
    // 响应码
    code: number
    // 响应提示信息
    msg: string
    // 响应数据
    data: T
    success: boolean
}
