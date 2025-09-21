export enum DATA_TYPE_ENUM {
    USER_ORDER = '用户订单',
    PURCHASE_ORDER = '采购订单',
    AFTER_SALES_WORK_ORDER = '售后工单',
    STATEMENT_OF_ACCOUNT = '对账单',
    SUPPLIER_SETTLEMENT = '供应商结算',
    STORAGE_DETAIL = '库存明细',
}

export enum EXPORT_STATUS_ENUM {
    PROCESSING = '生成中',
    SUCCESS = '已完成',
    FAILED = '失败',
}
