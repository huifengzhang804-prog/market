import axios from 'axios'
import { isFunction } from '@/utils/is'
import uuid from '@/utils/uuid'
import type { AxiosRequestConfig, Canceler } from 'axios'

let pendingMap = new Map<string, Canceler>()
const getPendingUrl = (config: AxiosRequestConfig) => [config.method, config.url].join('&') + uuid(8)
export class AxiosCanceler {
    // 添加请求
    addPending(config: AxiosRequestConfig) {
        this.removePending(config)
        const url = getPendingUrl(config)
        config.cancelToken =
            config.cancelToken ||
            new axios.CancelToken((cancel) => {
                if (!pendingMap.has(url)) {
                    pendingMap.set(url, cancel)
                }
            })
    }
    // 去除请求
    removePending(config: AxiosRequestConfig) {
        const url = getPendingUrl(config)
        if (pendingMap.has(url)) {
            const cancel = pendingMap.get(url)
            cancel?.(url)
            pendingMap.delete(url)
        }
    }
    removeAllPending() {
        pendingMap.forEach((cancel) => {
            if (cancel && isFunction(cancel)) {
                cancel()
            }
        })
        pendingMap.clear()
    }
    // 充值Map
    reset(): void {
        pendingMap = new Map<string, Canceler>()
    }
}
