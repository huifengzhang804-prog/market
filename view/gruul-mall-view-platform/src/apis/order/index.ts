import { get, post, put } from '../http'
import type { L } from '@/apis/http.type'
import type { OrderDetail } from '@/apis/order/type/order'
import { ApiOrder } from '@/views/order/types/order'
import type { CourierLoctionInfo, DistributionMode, ExpressCompanyDTO, IcOrderDetail, Order } from './types'

/**
 * 获取订单列表
 */
export const doGetOrderList = <T>(params: any, ids?: string): Promise<any> => {
    return get<L<T>>({ url: `gruul-mall-order/order`, params })
}

/**
 * 获取订单详情
 */
export const doGetOrder = (params: any, ids: string) => {
    return get<OrderDetail>({ url: `gruul-mall-order/order/${ids}`, params })
}

/**
 * 通过orderNo获取未发货订单
 */
export const dogetOrderNotDetailsByOrderNo = (orderNo: string, shopOrderNo: string, shopId: string) => {
    return get({ url: `/gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/undelivered`, params: { shopId } })
}

/**
 * 商品批量发货
 */
export const doPostOrderBatchDeliver = (data: any) => {
    return put({ url: `gruul-mall-order/order/batch/deliver`, data })
}
/**
 * 关闭店铺订单
 * @param {string} orderNo
 * @param {string} shopOrderNo
 */
export const doPutCloseShopOrder = (orderNo: string, shopOrderNo: string) => {
    return put({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/close` })
}

/**
 * 商品发货
 */
export const doPutOrderDetailsByOrderNo = (orderNo: string, data: any) => {
    return put({ url: `/gruul-mall-order/order/${orderNo}/deliver`, data })
}
export interface ApiOrderWithExpressCompany extends ApiOrder {
    expressCompany: {
        expressCompanyCode: string
        expressCompanyName: string
        expressNo: string
        logisticsCompanyCode: string
        logisticsCompanyName: string
    }
}
export const importNeedDeliveryOrders = (orderNos?: string[], distributionMode?: keyof typeof DistributionMode) => {
    return post<{ shopOrders: ApiOrderWithExpressCompany[]; supplierOrders: ApiOrderWithExpressCompany[] }>({
        url: '/gruul-mall-order/order/getPlatFormOrder',
        data: { orderNos, distributionMode },
    })
}

type ICParament = { orderNo: string; shopId: Long }
/**
 * 获取同城配送订单信详情
 * @param {string} orderNo
 * @param {string} shopId
 */
export const doGetICOrder = (data: ICParament) => {
    return post<IcOrderDetail>({ url: `addon-ic/ic/shop/order/deliver/info`, data })
}

/**
 * 获取UU跑腿配送员最新信息和定位
 */
export const doGetUuptCourierInfo = (data: ICParament) => {
    return post<CourierLoctionInfo>({ url: `addon-ic/ic/shop/order/courier/uupt`, data })
}
