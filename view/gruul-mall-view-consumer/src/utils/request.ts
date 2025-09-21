/* eslint-disable no-undef */
/*
 * 请求/返回参数类型接口
 */
import type { Result } from '@/utils/types'

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE'
export interface IRequestConfig extends UniApp.RequestOptions {
  [key: string]: any
  duration?: number
  cache?: boolean
  reqState?: boolean
  baseUrl?: string
  method: Method
  cancelToken?: boolean
}

export interface PromiseHandlers<T = any, R = any> {
  fulfilled: (res: T) => Promise<T>
  rejected: (res: R) => Promise<R>
}

export interface UnireqResponse<T = any> extends UniApp.RequestSuccessCallbackResult {
  data: Result<T>
  errMsg: string
}
