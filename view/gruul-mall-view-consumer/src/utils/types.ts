import type { Ref, ComputedRef } from 'vue'
import type { IRequestConfig } from '@/utils/request'

export type Fn = () => void
export type Fns = (...args: any[]) => void
export type MaybeRef<T> = T | Ref<T>
export type NonNullable<T> = T extends null | undefined ? never : T
export type MaybeReactive<T> = ComputedRef<T> | Ref<T> | T
export type Result<T = any> = {
  code: number
  data?: T
  success: boolean
  msg?: string
  error?: boolean
  config: IRequestConfig
}
export type R<T = any> = {
  code: number
  data?: T
}

/**
 * 分页查询结果
 */
export type Pagination<T> = {
  current: number
  size: number
  total: number
  pages: number
  records: T
  // 可能还有其他字段
  [key: string]: any
}

/**
 * 分页查询条件
 */
export type PageParam = {
  current: number
  size: number
}

export function isValiidKey(key: string | number | symbol, object: object): key is keyof typeof object {
  return key in object
}
