import {
    QUERYORDERSTATUS,
    ORDERSTATUS,
    SHOPORDERSTATUS,
    PACKAGESTATUS,
    SHOPITEMSTATUS,
    ApiOrder,
    ShopOrderItem,
    DISTRIBUTION,
} from '@/views/order/types/order'
const queryStatus: Record<keyof typeof QUERYORDERSTATUS, string> = {
    UNPAID: '待支付',
    UN_DELIVERY: '待发货',
    UN_RECEIVE: '待收货',
    COMPLETED: '已完成',
    CLOSED: '已关闭',
}

const distributionModeStatus: Record<keyof typeof DISTRIBUTION, string> = {
    MERCHANT: '快递配送',
    EXPRESS: '快递配送',
    INTRA_CITY_DISTRIBUTION: '同城配送',
    SHOP_STORE: '门店自提',
    VIRTUAL: '无需物流',
}
const shopItemsStatus: Record<keyof typeof SHOPITEMSTATUS, string> = {
    OK: '确认',
    CLOSED: '取消',
}

/**
 * TabsName
 */
const TabsName = ['近一个月订单', '近三个月订单', '全部订单']

export { queryStatus, TabsName, distributionModeStatus }
