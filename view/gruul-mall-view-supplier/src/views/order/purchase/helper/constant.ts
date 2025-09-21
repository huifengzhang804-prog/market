enum QUERYORDERSTATUS {
    UNPAID,
    PAYMENT_AUDIT,
    WAITING_FOR_DELIVER,
    WAITING_FOR_PUTIN,
    FINISHED,
    CLOSED,
}
export const queryOrderStatus: Record<keyof typeof QUERYORDERSTATUS, string> = {
    UNPAID: '待支付',
    PAYMENT_AUDIT: '待审核',
    WAITING_FOR_DELIVER: '待发货',
    WAITING_FOR_PUTIN: '待入库',
    FINISHED: '已完成',
    CLOSED: '已关闭',
}
