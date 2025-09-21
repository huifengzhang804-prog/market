import { A_REfUND_WHY, ARefundType, AFSSTATUS, PACKAGESTATUS, type ApiOrderAfsItem } from '@/views/afs/types'
import { ORDERPAYMENT, ORDERSTATUS } from '@/views/order/types/order'
import defaultAvatar from '@/assets/image/defaultAvatar.png'
export {
    aRefundWhy,
    afsStatus,
    systemInfo,
    TabItem,
    getAfsListStatusCn,
    getAfsNumStatusCn,
    getAfsOrderStatusCn,
    getAfsListPagesStatusCn,
    getAfsStatusCn,
}
interface AfsStatus {
    AFSSTATUS: keyof typeof AFSSTATUS
    ARefundType: keyof typeof ARefundType
    A_REfUND_WHY: keyof typeof A_REfUND_WHY
    ORDER_PAY_MENT: keyof typeof ORDERPAYMENT
    PACKAGESTATUS: keyof typeof PACKAGESTATUS
}
export interface Desc {
    title: string
    desc: string
    closable: boolean
    applyForAgain: boolean
    isConsumer: boolean
    list: string
    type: string
    afs: string
    isSystem: boolean
}
interface Why {
    title: string
    isReturnRefund: boolean
    undelivered: boolean
}
/**
 * 协商历史系统展示配置
 */
const systemInfo = {
    avatar: defaultAvatar,
    name: '系统',
}
/**
 * 售后工单tab切换
 */
const TabItem = [
    { id: '1', label: 'all', name: '', title: '全部订单' },
    { id: '2', label: 'await', name: 'PENDING', title: '待处理' },
    { id: '3', label: 'being', name: 'PROCESSING', title: '处理中' },
    { id: '4', label: 'completed', name: 'COMPLETED', title: '已完成' },
    { id: '5', label: 'close', name: 'CLOSED', title: '已关闭' },
]
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
 * 售后列表订单状态
 * @param  list 列表展示
 * @param  history  协商历史展示
 */
const afsPagesStatus: Record<AfsStatus['PACKAGESTATUS'], { list: string; history: string }> = {
    WAITING_FOR_DELIVER: { list: '待发货', history: '待发货' },
    WAITING_FOR_RECEIVE: { list: '待收货', history: '待收货' },
    BUYER_WAITING_FOR_COMMENT: { list: '交易成功', history: '已确认收货' },
    SYSTEM_WAITING_FOR_COMMENT: { list: '交易成功', history: '已确认收货' },
    BUYER_COMMENTED_COMPLETED: { list: '交易成功', history: '已确认收货' },
    SYSTEM_COMMENTED_COMPLETED: { list: '交易成功', history: '已确认收货' },
}
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
 * closable 是否可以撤销申请
 * applyForAgain 是否可以再次申请售后
 * isConsumer (协商历史页面 消费者？true:false)
 * @param list 售后列表
 * @param afs  售后订单详情
 * @param type 退款  | 退货退款
 * @param  desc 协商历史  , , SYSTEM_RETURNED_REFUND_CONFIRM,
 */
const afsStatus: Record<AfsStatus['AFSSTATUS'], Desc> = {
    NONE: {
        isSystem: false,
        title: '',
        list: '',
        type: '',
        desc: '',
        afs: '',
        closable: false,
        applyForAgain: true,
        isConsumer: false,
    },
    REFUND_REQUEST: {
        isSystem: false,
        list: '申请退款 待审核',
        afs: '退款 待商家审核',
        title: '请等待卖家处理',
        type: '退款',
        desc: '买家发起了退款申请',
        closable: true,
        applyForAgain: false,
        isConsumer: true,
    },
    REFUND_AGREE: {
        isSystem: false,
        title: '退款中',
        desc: '已同意退款申请',
        list: '已同意退款申请',
        type: '退款',
        afs: '已同意退款申请',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    SYSTEM_REFUND_AGREE: {
        isSystem: true,
        title: '退款中',
        desc: '系统同意退款申请',
        list: '系统已同意退款申请',
        type: '退款',
        afs: '系统已同意退款申请',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    REFUND_REJECT: {
        isSystem: false,
        title: '退款关闭',
        list: '退款关闭',
        desc: '已拒绝退款申请',
        type: '退款',
        afs: '退款关闭',
        closable: true,
        applyForAgain: true,
        isConsumer: false,
    },
    REFUNDED: {
        isSystem: false,
        list: '退款成功',
        title: '退款成功',
        desc: '退款成功',
        afs: '退款成功',
        type: '退款',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    RETURN_REFUND_REQUEST: {
        isSystem: false,
        list: '申请退货退款 待审核',
        title: '请等待卖家处理',
        desc: '买家发起了退货退款申请',
        type: '退货退款',
        afs: '申请退货退款 待审核',
        closable: true,
        applyForAgain: false,
        isConsumer: true,
    },
    RETURN_REFUND_AGREE: {
        isSystem: false,
        list: '申请退货退款 待退货',
        type: '退货退款',
        title: '已同意退货退款',
        desc: '已同意退货退款申请 待退货',
        afs: '申请退货退款 待退货',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    SYSTEM_RETURN_REFUND_AGREE: {
        isSystem: true,
        list: '申请退货退款 待退货',
        type: '退货退款',
        title: '系统已同意退货退款',
        desc: '系统已同意退货退款申请 待退货',
        afs: '申请退货退款 待退货',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    RETURN_REFUND_REJECT: {
        isSystem: false,
        list: '退货退款关闭',
        type: '退货退款',
        title: '退款关闭',
        desc: '拒绝退货退款申请',
        afs: '退货退款关闭',
        closable: true,
        applyForAgain: true,
        isConsumer: false,
    },
    RETURNED_REFUND: {
        isSystem: false,
        list: '申请退货退款 待商家收货',
        type: '退货退款',
        title: '等待商家确认收货',
        desc: '买家已发起退货，待收货',
        afs: '等待商家确认收货',
        closable: false,
        applyForAgain: false,
        isConsumer: true,
    },
    RETURNED_REFUND_CONFIRM: {
        isSystem: false,
        list: '确认退货已收到',
        type: '退货退款',
        title: '商家已收到退货，等待退款',
        afs: '商家已收到退货，等待退款',
        desc: '已收到退货，等待退款',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    SYSTEM_RETURNED_REFUND_CONFIRM: {
        isSystem: true,
        list: '系统确认退货已收到',
        type: '退货退款',
        title: '商家已收到退货，等待退款',
        afs: '系统确认退货已收到',
        desc: '已收到退货，等待退款',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    RETURNED_REFUND_REJECT: {
        isSystem: false,
        list: '退货退款关闭',
        type: '退货退款',
        title: '退款关闭',
        desc: '拒收买家已发起退货',
        afs: '退货退款关闭',
        closable: true,
        applyForAgain: true,
        isConsumer: false,
    },
    RETURNED_REFUNDED: {
        isSystem: false,
        list: '退货退款成功',
        title: '退款成功',
        desc: '退货退款成功',
        afs: '退货退款成功',
        type: '退货退款',
        closable: false,
        applyForAgain: false,
        isConsumer: false,
    },
    BUYER_CLOSED: {
        isSystem: false,
        list: '关闭',
        title: '售后关闭',
        desc: '买家撤销',
        afs: '关闭',
        type: '关闭',
        closable: false,
        applyForAgain: true,
        isConsumer: true,
    },
    SYSTEM_CLOSED: {
        isSystem: true,
        list: '关闭',
        title: '售后超时，系统关闭',
        desc: '系统关闭',
        afs: '关闭',
        type: '关闭',
        closable: false,
        applyForAgain: true,
        isConsumer: false,
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
/**
 * 售后订单信息状态转中文
 * @param {keyof} status
 */
export const getAfsOrderInfoStatusCn = (status: keyof typeof ORDERSTATUS) => {
    return orderStatus[status as keyof typeof orderStatus]
}
/**
 * 售后列表包裹 状态转化中文
 * @param {ApiOrderAfsItem} row
 * @returns {*} type
 */
const getAfsListPagesStatusCn = (status: AfsStatus['PACKAGESTATUS']) => {
    return afsPagesStatus[status].list
}
/**
 * 售后列表售后状态转化中文
 * @param {ApiOrderAfsItem} row
 * @returns {*} type
 */
const getAfsListStatusCn = (row: ApiOrderAfsItem) => {
    if (row.status === 'BUYER_CLOSED') return `${aRefundType[row.type]}${afsStatus[row.status].list}`
    return afsStatus[row.status].list
}
/**
 * 售后订单状态转化中文
 * @param {ApiOrderAfsItem} row
 */
const getAfsOrderStatusCn = (status: AfsStatus['AFSSTATUS']) => {
    return afsStatus[status].afs
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
