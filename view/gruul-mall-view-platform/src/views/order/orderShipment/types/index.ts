import { ShopOrderItem, OrderReceiver } from '@/views/order/types/order'
export interface ApiNotDelivey extends ShopOrderItem {
    createTime: string
    fixPrice: number
    deliveryNum: number
}
/**
 * 地区信息
 */
export interface ApiOrderReceiver extends OrderReceiver {
    createTime: string
    deleted?: boolean
    orderNo?: string
    shopId?: string
    updateTime?: string
    version?: number
}
/**
 * @param: WITHOUT  无需物流发货
 * @param: EXPRESS 普通发货 自己填 物流公司与 单号
 * @param: PRINT_EXPRESS 打印发货
 */
export enum DeliverType {
    WITHOUT,
    EXPRESS,
    PRINT_EXPRESS,
}
/**
 * 发货的携带参数
 */
export interface ParamsDeliver {
    deliverType: keyof typeof DeliverType | string
    receiver?: { name: string; mobile: string; address: string }
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
    selfShopType: 'SELF_SHOP' | 'SELF_SUPPLIER'
}
export interface ExpressCompanyForm {
    expressCompanyCode: string
    expressCompanyName: string
    expressNo?: string
}

export interface ExpressCompany {
    expressCompanyCode: string
    expressCompanyName: string
    expressNo?: string
}
type Infos = Pick<ParamsDeliver, 'deliverType'>
// deliverType: 'EXPRESS',
// sender: '',
// expressCompany: { expressCompanyName: '' expressCompanyCode;""},
