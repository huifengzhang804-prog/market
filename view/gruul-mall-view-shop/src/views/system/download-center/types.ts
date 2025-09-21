export enum DATA_TYPE_ENUM {
    USER_ORDER = '用户订单',
    PURCHASE_ORDER = '采购订单',
    AFTER_SALES_WORK_ORDER = '售后工单',
    STATEMENT_OF_ACCOUNT = '对账单',
    STORE_SETTLEMENT = '店铺结算',
    DISTRIBUTE_ORDER = '分销订单',
    STORAGE_DETAIL = '库存明细',
    COUPON_RECORD = '用券记录',
}

export enum EXPORT_STATUS_ENUM {
    PROCESSING = '生成中',
    SUCCESS = '已完成',
    FAILED = '失败',
}
