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
const orderStatus: Record<keyof typeof ORDERSTATUS, string> = {
    UNPAID: '未支付',
    PAID: '已支付',
    BUYER_CLOSED: '买家关闭订单',
    SYSTEM_CLOSED: '超时未支付 系统关闭',
    SELLER_CLOSED: '卖家关闭订单',
    TEAMING: '拼团中',
    TEAM_FAIL: '拼团失败',
}
const shopOrderStatus: Record<keyof typeof SHOPORDERSTATUS, string> = {
    OK: '正常状态',
    SYSTEM_CLOSED: '系统关闭',
    BUYER_CLOSED: '买家关闭订单',
    SELLER_CLOSED: '卖家关闭订单',
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
const packageStatus: Record<keyof typeof PACKAGESTATUS, string> = {
    WAITING_FOR_DELIVER: '未发货',
    WAITING_FOR_RECEIVE: '待收货',
    BUYER_WAITING_FOR_COMMENT: '待评价',
    SYSTEM_WAITING_FOR_COMMENT: '待评价',
    BUYER_COMMENTED_COMPLETED: '已完成',
    SYSTEM_COMMENTED_COMPLETED: '已完成',
}

/**
 * @description: TabsName
 */
const TabsName = ['近一个月订单', '近三个月订单', '全部订单']

export { queryStatus, TabsName, distributionModeStatus }
