import { afsStatusHandler } from './orderDetails/OrderStatusCalculate'
import type { ApiOrder, ShopOrderItem } from './types/order'
import { calculate } from './orderDetails/OrderStatusCalculate'

/**
 * @description 获取当前订单是否处于售后状态
 * @param row 订单数据
 * @returns { boolean } 是否处于售后状态
 */
export const getOrderAfterSaleStatus = (row: ApiOrder) => {
    const shopOrderItems = row.shopOrders?.[0]?.shopOrderItems || []
    for (const shopOrderItem of shopOrderItems) {
        const { ing } = afsStatusHandler[shopOrderItem.afsStatus]
        if (ing) return true
    }
    return false
}

/**
 * @description 获取配送方式
 * @param row 当前订单信息
 * @returns 配送方式
 */
export const getDeliveryMode = (row: ApiOrder) => {
    const distributionModeMap = {
        EXPRESS: '快递配送',
        INTRA_CITY_DISTRIBUTION: '同城配送',
        SHOP_STORE: '到店自提',
        VIRTUAL: '无需物流',
        MERCHANT: '快递配送',
    }
    const distributionMode = row?.extra?.distributionMode
    return distributionModeMap[distributionMode] || ''
}
