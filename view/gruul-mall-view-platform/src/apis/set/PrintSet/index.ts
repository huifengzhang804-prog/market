import { get, put, post, del } from '@/apis/http'
import { PrinterSetConfig } from './types'
/**
 * 配置飞鹅打印机开放平台配置
 */
export const doPrintUpdateAndEdit = (data: { user: string; ukey: string }) => {
    return post({ url: 'gruul-mall-order/order/printer/open/api/feie', data })
}
/**
 * 获取飞鹅打印机开放平台配置
 */
export const doGetPrintInfo = () => {
    return post<PrinterSetConfig>({ url: 'gruul-mall-order/order/printer/open/api/feie/get' })
}
