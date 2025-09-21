export interface PaymentHistoryItem {
    createTime: string
    changeType: keyof typeof CJANG_TYPE
    dealType: keyof typeof DEAL_TYPE
    id: string
    money: string
    payType: keyof typeof PAY_TYPE
    userHeadPortrait: string
    userId: string
    remark?: string
    userNikeName: string
}
enum CJANG_TYPE {
    'INCREASE' = '收入',
    'REDUCE' = '支出',
}
enum DEAL_TYPE {
    SYSTEM_GIVE = '系统赠送',
    PERSONAL_CHARGING = '个人充值',
    SYSTEM_CHARGING = '系统充值',
    SHOPPING_PURCHASE = '购物消费',
    PURCHASE_MEMBER = '购买会员',
    REFUND_SUCCEED = '退款成功',
    WITHDRAW = '提现',
}
enum PAY_TYPE {
    ALIPAY = '支付宝',
    WECHAT = '微信',
    BALANCE = '余额',
}
