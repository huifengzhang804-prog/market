export enum DATA_TYPE_ENUM {
    USER_ORDER = '用户订单',
    PURCHASE_ORDER = '采购订单',
    AFTER_SALES_WORK_ORDER = '售后工单',
    STATEMENT_OF_ACCOUNT = '对账单',
    WITHDRAWAL_ORDER = '提现工单',
    STORED_VALUE_FLOW = '储值流水',
    MEMBER_RECORDER = '开通会员',
    COUPON_RECORD = '用券记录',
    PURCHASE_REBATE_ORDER = '消费返利订单',
    DISTRIBUTE_ORDER = '分销订单',
}

export enum EXPORT_STATUS_ENUM {
    PROCESSING = '生成中',
    SUCCESS = '已完成',
    FAILED = '失败',
}
