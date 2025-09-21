import { ShopOrderItem, OrderReceiver } from '@/views/order/types/order'
import { DeliverType } from '@/apis/order/types'
export { DeliverType }

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
    deleted: false
    orderNo: string
    shopId: string
    updateTime: string
    version: number
}
/**
 * 发货的携带参数
 */
export interface ParamsDeliver {
    deliverType: keyof typeof DeliverType
    receiver?: { name: string; mobile: string; address: string; area: [string, string, string?] }
    sender?: { name: string; mobile: string; address: string; area: string[] }
    cargo: string
    remark: string
    dropDeliver: boolean
    expressCompany?: ExpressCompanyForm | null
    items: {
        itemId: number
        num: number
    }[]
}
export interface ExpressCompanyForm {
    // 物流公司名称
    expressName?: string
    // 物流公司编码
    expressCompanyName?: string
    // 运单号
    expressNo?: string
    expressCompanyCode?: string
}

export interface ExpressCompany {
    expressCompanyCode: string
    expressCompanyName: string
    expressNo?: string
}

export interface Address {
    address: string
    area: string[]
    contactName: string
    contactPhone: string
    createTime: string
    defReceive: string
    defSelfShop: string
    defSelfSupplier: string
    defSend: string
    deleted: boolean
    id: string
    shopId: string
    updateTime: string
    version: number
    zipCode: string
}
