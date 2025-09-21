import { ShopOrderItem, OrderReceiver } from '@/views/order/types/order'
export interface ApiNotDelivey extends ShopOrderItem {
    createTime: string
    fixPrice: string
    deliveryNum: number
}
/**
 * @description: 地区信息
 * @returns {*}
 */
export interface ApiOrderReceiver extends OrderReceiver {
    createTime: string
    deleted: false
    orderNo: string
    shopId: string
    updateTime: string
    version: number
}
/**
 * @param: WITHOUT  无需物流发货
 * @param: EXPRESS 普通发货 自己填 物流公司与 单号
 * @param: PRINT_EXPRESS 打印发货
 * @returns {*}
 */
export enum DeliverType {
    WITHOUT,
    EXPRESS,
    PRINT_EXPRESS,
}
/**
 * @description: 发货的携带参数
 * @returns {*}
 */
export interface ParamsDeliver {
    deliverType: keyof typeof DeliverType | string
    receiver?: { name: string; mobile: string; address: string; area: [string, string, string?] }
    sender: { name: string; mobile: string; address: string }
    cargo: string
    remark: string
    dropDeliver: boolean
    expressCompany?: ExpressCompanyForm | null
    items: {
        itemId: string
        num: number
    }[]
    shopId?: string
}
export interface ExpressCompanyForm {
    expressCompanyCode: string
    expressCompanyName: string
    expressNo?: string
}
export interface BatchParamsDeliver {
    orderNo: string
    orderDelivery: ParamsDeliver
}
export interface ExpressCompany {
    logisticsCompanyCode: string
    expressNo: string
}
type Infos = Pick<ParamsDeliver, 'deliverType'>
// deliverType: 'EXPRESS',
// sender: '',
// expressCompany: { expressCompanyName: '' expressCompanyCode;""},
