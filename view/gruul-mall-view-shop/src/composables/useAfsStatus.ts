import { Order } from '@/apis/order/types'
import { A_REfUND_WHY, AFSSTATUS, ApiOrderAfsItem, ARefundType, PACKAGESTATUS } from '@/views/afs/types'
import { DISTRIBUTION, ORDERPAYMENT } from '@/views/order/types/order'
import { calculate } from '@/views/order/orderDetails/OrderStatusCalculate'
import Morentouxiang from '@/assets/images/morentouxiang.png'
interface AfsStatus {
    AFSSTATUS: keyof typeof AFSSTATUS
    ARefundType: keyof typeof ARefundType
    A_REfUND_WHY: keyof typeof A_REfUND_WHY
    ORDER_PAY_MENT: keyof typeof ORDERPAYMENT
    PACKAGESTATUS: keyof typeof PACKAGESTATUS
}

const packageStatus = {
    WAITING_FOR_DELIVER: '待发货',
    WAITING_FOR_RECEIVE: '待收货',
    BUYER_WAITING_FOR_COMMENT: '待评价',
    SYSTEM_WAITING_FOR_COMMENT: '待评价',
    BUYER_COMMENTED_COMPLETED: '已完成',
    SYSTEM_COMMENTED_COMPLETED: '已完成',
}
export { aRefundWhy, afsStatus, TabItem, systemInfo, getAfsNumStatusCn, getAfsOrderStatusCn, getAfsStatusCn, packageStatus }

export interface Desc {
    title: string
    isShowAgreebtn: boolean
    desc: string
    isConsumer: boolean
    list: string
    type: string
    afs: string
    isSystem: boolean
    msg: string
    isCountdown: boolean
    agreebtnText?: string
}

interface Why {
    title: string
    isReturnRefund: boolean
    undelivered: boolean
}

/**
 * 售后工单tab切换
 */
const TabItem = [
    { id: '1', label: 'all', name: ' ', title: '全部订单' },
    { id: '2', label: 'await', name: 'PENDING', title: '待审核' },
    { id: '3', label: 'being', name: 'PROCESSING', title: '处理中' },
    { id: '4', label: 'completed', name: 'COMPLETED', title: '已完成' },
    { id: '5', label: 'close', name: 'CLOSED', title: '已关闭' },
] as const
/**
 * 协商历史  系统回复配置
 */
const systemInfo = {
    avatar: Morentouxiang,
    name: '系统',
}
/**
 * @param NONE 无售后
 * @param REFUND_REQUEST 申请退款
 * @param REFUND_AGREE 已同意退款申请
 * @param REFUND_REJECT 拒绝了退款申请
 * @param REFUNDED 退款已到账
 * @param RETURN_REFUND_REQUEST 申请退货退款
 * @param RETURN_REFUND_AGREE 已同意退货退款申请
 * @param RETURN_REFUND_REJECT 拒绝了退货退款申请
 * @param RETURNED_REFUND   退货已发出
 * @param RETURNED_REFUND_CONFIRM 确认退货已收到
 * @param RETURNED_REFUND_REJECT 已拒绝收货
 * @param  RETURNED_REFUNDED 退货退款已完成
 * @param  BUYER_CLOSED  主动撤销了售后申请
 */

/**
 * 退款原因
 * @param isReturnRefund 退货退款
 * @param undelivered  未发货
 *
 */
const aRefundWhy: Record<AfsStatus['A_REfUND_WHY'] | string, Why> = {
    DONT_NEED_IT: { title: '多拍/拍错/不想要', isReturnRefund: false, undelivered: true },
    WRONG_FORM: { title: '地址/联系电话 填写错误', isReturnRefund: false, undelivered: true },
    EXPRESS_STAGNATED: { title: '快递停滞/未送达', isReturnRefund: false, undelivered: false },
    EXPRESS_REJECTION: { title: '货物破损已拒签', isReturnRefund: false, undelivered: false },
    EMPTY_PARCEL: { title: '空包裹', isReturnRefund: false, undelivered: false },
    LOGISTICS_NO_UPDATE: { title: '快递/物流无跟踪记录', isReturnRefund: false, undelivered: false },
    SEVEN_DAYS_NO_REASON: { title: '七天无理由退货（商品无损）', isReturnRefund: true, undelivered: false },
    WRONG_PRODUCT_DESC: { title: '尺寸/容量/参数与商品描述不符', isReturnRefund: true, undelivered: false },
    DONT_LIKE_IT: { title: '不喜欢或效果不好', isReturnRefund: true, undelivered: false },
    SOME_MISSING: { title: '缺件/漏发', isReturnRefund: true, undelivered: false },
    QUALITY_PROBLEM: { title: '质量问题', isReturnRefund: true, undelivered: false },
    WRONG_PRODUCT: { title: '商家发错货', isReturnRefund: true, undelivered: false },
}
/**
 * 退款类型
 */
const aRefundType: Record<AfsStatus['ARefundType'], string> = {
    REFUND: '退款',
    RETURN_REFUND: '退货退款',
}
/**
 * 支付方式
 */
const payType: Record<AfsStatus['ORDER_PAY_MENT'], string> = {
    WECHAT: '微信',
    ALIPAY: '支付宝',
    BALANCE: '余额',
}
/**
 * 退款详情提示
 * @param isCountdown 售后信息__是否展示倒计时
 * @param isShowAgreebtn 售后信息__是否展示同意退款按钮
 * @param isConsumer (协商历史页面 消费者？true:false)
 * @param list 售后列表__订单状态cn
 * @param afs  售后信息__订单状态cn
 * @param msg  售后信息__提示文本
 * @param type 退款  |
 * @param  desc 协商历史  , , ,
 */
const afsStatus: Record<AfsStatus['AFSSTATUS'], Desc> = {
    NONE: {
        title: '',
        list: '',
        type: '',
        desc: '',
        afs: '',
        msg: '',
        isCountdown: false,
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: false,
    },
    REFUND_REQUEST: {
        isCountdown: true,
        msg: '如您逾期未响应申请，视作同意买家申请，系统会自动退款给买家',
        list: '申请退款 待审核',
        afs: '退款 待商家审核',
        title: '请等待卖家处理',
        type: '退款',
        desc: '买家发起了退款申请',
        agreebtnText: '同意退款',
        isShowAgreebtn: true,
        isConsumer: true,
        isSystem: false,
    },
    REFUND_AGREE: {
        msg: '',
        isCountdown: false,
        title: '退款中',
        desc: '已同意退款申请',
        list: '已同意退款申请',
        type: '退款',
        afs: '退款中',
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: false,
    },
    SYSTEM_REFUND_AGREE: {
        msg: '',
        title: '退款中',
        isCountdown: false,
        desc: '系统已同意退款申请',
        list: '系统已同意退款申请',
        type: '退款',
        afs: '退款成功',
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: true,
    },
    REFUND_REJECT: {
        msg: '',
        title: '退款关闭',
        list: '退款关闭',
        desc: '已拒绝退款申请',
        type: '退款',
        afs: '退款关闭',
        isShowAgreebtn: false,
        isCountdown: false,
        isConsumer: false,
        isSystem: false,
    },
    REFUNDED: {
        msg: '',
        list: '退款成功',
        title: '退款成功',
        desc: '退款成功',
        afs: '退款成功',
        type: '退款',
        isCountdown: false,
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: false,
    },
    RETURN_REFUND_REQUEST: {
        list: '申请退货退款 待审核',
        title: '请等待卖家处理',
        desc: '买家发起了退货退款申请',
        type: '退货退款',
        afs: '退货退款 待商家审核',
        msg: '如您逾期未响应申请，视作同意买家申请，系统会自动退款给买家',
        isCountdown: true,
        agreebtnText: '同意退货退款',
        isShowAgreebtn: true,
        isConsumer: true,
        isSystem: false,
    },
    RETURN_REFUND_AGREE: {
        msg: '等待买家退货，如用户逾期未退货。系统会自动关闭售后申请',
        isCountdown: true,
        list: '申请退货退款 待退货',
        type: '退货退款',
        title: '已同意退货退款',
        desc: '已同意退货退款申请 待退货',
        afs: '退货退款 待买家退货',
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: false,
    },
    SYSTEM_RETURN_REFUND_AGREE: {
        msg: '',
        list: '申请退货退款 待退货',
        isCountdown: true,
        type: '退货退款',
        title: '系统已同意退货退款',
        desc: '系统已同意退货退款申请 待退货',
        afs: '退货退款 待买家退货',
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: true,
    },
    RETURN_REFUND_REJECT: {
        msg: '',
        list: '退货退款关闭',
        type: '退货退款',
        isCountdown: false,
        title: '退款关闭',
        desc: '拒绝退货退款申请',
        afs: '退货退款关闭',
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: false,
    },
    RETURNED_REFUND: {
        msg: '如您逾期未响应申请，视作同意买家申请，系统会自动退款给买家',
        list: '申请退货退款 待商家收货',
        type: '退货退款',
        isCountdown: true,
        title: '等待商家确认收货',
        desc: '买家已发起退货，待收货',
        afs: '退货退款 待商家确认收货',
        isShowAgreebtn: true,
        agreebtnText: '确认收货并同意退款',
        isConsumer: true,
        isSystem: false,
    },
    RETURNED_REFUND_CONFIRM: {
        msg: '',
        list: '确认退货已收到',
        type: '退货退款',
        title: '商家已收到退货，等待退款',
        afs: '已收到退货，退款中',
        isCountdown: false,
        desc: '已收到退货，等待退款',
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: false,
    },
    SYSTEM_RETURNED_REFUND_CONFIRM: {
        msg: '',
        list: '系统确认退货已收到',
        title: '退款成功',
        desc: '已收到退货，等待退款',
        afs: '退款成功',
        type: '退货退款',
        isCountdown: false,
        isShowAgreebtn: false,
        isConsumer: false,
        isSystem: true,
    },
    RETURNED_REFUND_REJECT: {
        msg: '',
        list: '退货退款关闭',
        type: '退货退款',
        title: '退款关闭',
        desc: '拒收买家已发起退货',
        afs: '退货退款关闭',
        isShowAgreebtn: false,
        isCountdown: false,
        isConsumer: false,
        isSystem: false,
    },
    RETURNED_REFUNDED: {
        msg: '',
        list: '退货退款成功',
        title: '退款成功',
        desc: '退货退款成功',
        afs: '退货退款成功',
        type: '退货退款',
        isShowAgreebtn: false,
        isCountdown: false,
        isConsumer: false,
        isSystem: false,
    },
    BUYER_CLOSED: {
        msg: '',
        list: '关闭',
        title: '售后关闭',
        desc: '买家撤销',
        afs: '售后关闭，买家撤销',
        type: '关闭',
        isShowAgreebtn: false,
        isCountdown: false,
        isConsumer: true,
        isSystem: false,
    },
    SYSTEM_CLOSED: {
        msg: '',
        list: '超时未处理，系统关闭',
        title: '售后关闭',
        desc: '超时未处理，系统关闭',
        afs: '售后关闭',
        type: '关闭',
        isShowAgreebtn: false,
        isCountdown: false,
        isConsumer: false,
        isSystem: true,
    },
}
/*
 * 售后订单信息状态
 */
const orderStatus = {
    UNPAID: '未支付',
    PAID: '已支付',
    BUYER_CLOSED: '买家关闭订单',
    SYSTEM_CLOSED: '系统关闭订单',
}

export const distributionModeStatus: Record<keyof typeof DISTRIBUTION, string> = {
    MERCHANT: '快递配送',
    EXPRESS: '快递配送',
    INTRA_CITY_DISTRIBUTION: '同城配送',
    SHOP_STORE: '门店自提',
    VIRTUAL: '无需物流',
}
/**
 * 售后订单信息状态转中文
 * @param {keyof} status
 */
export const getAfsOrderInfoStatusCn = (order: Order) => {
    return calculate(order).state.status
}

/**
 * 售后订单状态转化中文
 * @param {ApiOrderAfsItem} row
 */
const getAfsOrderStatusCn = (status: AfsStatus['AFSSTATUS']) => {
    return afsStatus[status]
}

/**
 * 售后工单类型转化中文
 * @returns {*} 退款单 |  退货退款单
 */
const getAfsNumStatusCn = (status: AfsStatus['ARefundType']) => {
    return aRefundType[status]
}

/**
 * 退款详情
 * @param {keyof} status
 */
const getAfsStatusCn = (status: AfsStatus['AFSSTATUS']) => {
    return afsStatus[status]
}

// -----------------
/**
 * 退款详情title提示转为中文
 * @param {keyof} status
 */
const getAfsStatusTitleCn = (status: AfsStatus['AFSSTATUS']) => {
    return afsStatus[status].title
}

/**
 * 退款原因提示转为中文
 * @param {keyof} status
 */
const getARefundReasonCn = (status: AfsStatus['ARefundType'] | string) => {
    return aRefundWhy[status].title
}
/**
 * 退款详情desc提示转为中文
 * @param {keyof} status
 */
const getAfsStatusDescCn = (status: AfsStatus['AFSSTATUS']) => {
    return afsStatus[status].desc
}
