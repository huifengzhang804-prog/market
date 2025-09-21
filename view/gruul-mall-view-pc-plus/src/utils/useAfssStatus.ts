import { A_REfUND_WHY, ARefundType, AFSSTATUS } from '@/views/personalcenter/order/aftersales/types'
export {
  systemInfo,
  aRefundWhy,
  aRefundType,
  afsStatusBtn,
  getARefundReasonCn,
  getAsfsStatusBtnCn,
  getAfsStatusTitleCn,
  getAfsStatusDescCn,
  getAfsStatusCn,
  getOrderListShowAfsStatusCn,
}
interface AfsStatus {
  AFSSTATUS: keyof typeof AFSSTATUS
  ARefundType: keyof typeof ARefundType
  A_REfUND_WHY: keyof typeof A_REfUND_WHY
}
/**
 * @description:
 * @param success 退货成功（控制撤销和申请售后按钮不显示）
 * @param title  退款详情title展示
 * @param desc 协商历史展示
 * @param closable  是否可以撤消
 * @param applyForAgain 是不是可以再次申请
 * @param isConsumer  是不是买家
 * @param isSystem 是不是系统
 * @param msg  控制撤销和申请售后按钮的提示文本
 * @param countDown  isCountDown是否显示倒计时 text提示文本
 * @param type  退款 / 退货退款
 * @param isRefunded 是否已退款成功
 * @param canChangePackageStatusText 可以改变包状态的情况下发起提示
 * @param list 售后列表
 */
interface Desc {
  title: string
  desc: string
  isSystem: boolean
  type: string
  closable: boolean
  applyForAgain: boolean
  isConsumer: boolean
  success: boolean
  msg: {
    title: string
  }[]
  list: string
  countDown: { isCountDown: boolean; leftText: string; rightText: string }
  canChangePackageStatus: boolean
  isRefunded: boolean
  canChangePackageStatusText: boolean
}
interface Why {
  title: string
  isReturnRefund: boolean
  undelivered: boolean
}
export const platformName = '启山智软'
/**
 * @description: 协商历史系统展示配置
 * @returns {*}
 */
const systemInfo = {
  avatar:
    'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F013db35c13455ca801209252da8d24.png%402o.png&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1663811779&t=1b3f21bb7e561dd1b75123739daea082',
  name: '系统',
}
// * @description:
// * @param EXPRESS 手动发货
// * @param PRINT_EXPRESS  打印快递单并发货
// * @param WITHOUT 无需物流发货
export enum DELIVERY_STATUS {
  EXPRESS,
  PRINT_EXPRESS,
  WITHOUT,
}
export const deliveryStaus = {
  WITHOUT: 'ss',
  EXPRESS: '物流配送',
  PRINT_EXPRESS: '物流配送',
}
/**
 * @description: 退款原因
 * @param isReturnRefund 退货退款
 * @param undelivered  未发货
 *
 */
const aRefundWhy: Record<AfsStatus['A_REfUND_WHY'] | string, Why> = {
  DONT_NEED_IT: { title: '多拍/拍错/不想要', isReturnRefund: false, undelivered: true },
  WRONG_FORM: { title: '地址/联系电话 填写错误', isReturnRefund: false, undelivered: true },
  EXPRESS_STAGNATED: { title: '快递停滞/未送达', isReturnRefund: false, undelivered: false },
  EXPRESS_REJECTION: { title: '货物破损已拒签', isReturnRefund: false, undelivered: false },
  SEVEN_DAYS_NO_REASON: { title: '七天无理由退货（商品无损）', isReturnRefund: true, undelivered: false },
  WRONG_PRODUCT_DESC: { title: '尺寸/容量/参数与商品描述不符', isReturnRefund: true, undelivered: false },
  DONT_LIKE_IT: { title: '不喜欢或效果不好', isReturnRefund: true, undelivered: false },
  SOME_MISSING: { title: '缺件/漏发', isReturnRefund: true, undelivered: false },
  QUALITY_PROBLEM: { title: '质量问题', isReturnRefund: true, undelivered: false },
  WRONG_PRODUCT: { title: '商家发错货', isReturnRefund: true, undelivered: false },
}
/**
 * @description: 退款类型
 * @returns {*}
 */
const aRefundType: Record<AfsStatus['ARefundType'], string> = {
  REFUND: '申请退款',
  RETURN_REFUND: '申请退货退款',
}
/**
 * @description: 申请售后按钮提示
 * @returns {*}
 */
const afsStatusBtn: Record<AfsStatus['AFSSTATUS'], string> = {
  NONE: '申请售后',
  REFUND_REQUEST: '待商家审核',
  REFUND_AGREE: '退款中',
  SYSTEM_REFUND_AGREE: '退款中',
  REFUND_REJECT: '拒绝退款',
  REFUNDED: '退款成功',
  SYSTEM_RETURN_REFUND_AGREE: '待买家退货',
  RETURN_REFUND_REQUEST: '待商家审核',
  RETURN_REFUND_AGREE: '待买家退货',
  RETURN_REFUND_REJECT: '拒绝退货退款',
  RETURNED_REFUND: '待商家确认收货',
  RETURNED_REFUND_CONFIRM: '退款中',
  RETURNED_REFUND_REJECT: '商家已拒绝收货',
  RETURNED_REFUNDED: '退款成功',
  SYSTEM_RETURNED_REFUND_CONFIRM: '退款成功',
  BUYER_CLOSED: '申请售后',
  SYSTEM_CLOSED: '申请售后',
}
/**
 * @description: 订单列表显示afs状态
 * @returns {*}
 */
const orderListShowAfsStatus: Record<AfsStatus['AFSSTATUS'], string> = {
  NONE: '',
  REFUND_REQUEST: '商家审核中',
  REFUND_AGREE: '退款中',
  SYSTEM_REFUND_AGREE: '退款中',
  REFUND_REJECT: '退款关闭',
  REFUNDED: '退款成功',
  SYSTEM_RETURN_REFUND_AGREE: '待退货',
  RETURN_REFUND_REQUEST: '商家审核中',
  RETURN_REFUND_AGREE: '待退货',
  RETURN_REFUND_REJECT: '退货退款关闭',
  RETURNED_REFUND: '待商家确认收货',
  RETURNED_REFUND_CONFIRM: '退款中',
  RETURNED_REFUND_REJECT: '商家拒绝收货',
  RETURNED_REFUNDED: '退款成功',
  SYSTEM_RETURNED_REFUND_CONFIRM: '退款成功',
  BUYER_CLOSED: '',
  SYSTEM_CLOSED: '',
}
/**
 * @description: 退款详情提示
 * closable 是否可以撤销申请
 * applyForAgain 是否可以再次申请售后
 * isConsumer (协商历史页面 消费者？true:false)
 * canChangePackageStatus
 * @returns {*}
 */
const afsStatus: Record<AfsStatus['AFSSTATUS'], Desc> = {
  NONE: {
    title: '申请售后',
    desc: '暂未申请售后',
    closable: false,
    list: '申请退款 待审核',
    applyForAgain: true,
    isConsumer: false,
    msg: [{ title: '' }],
    success: false,
    isSystem: false,
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    type: '',
    canChangePackageStatus: true,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  REFUND_REQUEST: {
    title: '请等待卖家处理',
    desc: '你已成功发起退款申请，请耐心请等待商家处理',
    list: '申请退货退款 待审核',
    type: '退款',
    isSystem: false,
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    closable: true,
    applyForAgain: false,
    isConsumer: true,
    success: false,
    msg: [{ title: '商家同意或者超时未支付，系统将退款给你' }, { title: '如果商家拒绝，你可以再次发起售后，卖家会重新处理' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: true,
    isRefunded: false,
  },
  REFUND_AGREE: {
    title: '退款中',
    type: '退款',
    desc: '商家已同意退款申请，请耐心请等待退款到账',
    list: '已同意退款申请',
    closable: false,
    isSystem: false,
    success: true,
    applyForAgain: false,
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    isConsumer: false,
    msg: [{ title: '如你的问题未解决，你还可以继续发起申请' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: false,
  },

  SYSTEM_REFUND_AGREE: {
    title: '退款中',
    type: '退款',
    desc: '系统已同意退款申请，请耐心请等待退款到账',
    closable: false,
    list: '系统已同意退款申请',
    success: true,
    applyForAgain: false,
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    isConsumer: false,
    isSystem: true,
    msg: [{ title: '如你的问题未解决，你还可以继续发起申请' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  REFUND_REJECT: {
    title: '退款关闭',
    type: '退款',
    list: '退款关闭',
    desc: '因商家拒绝了你的退款申请，本次售后关闭',
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    closable: false,
    isSystem: false,
    success: false,
    applyForAgain: true,
    isConsumer: false,
    msg: [{ title: '如你的问题未解决，你还可以继续发起申请' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  REFUNDED: {
    title: '退款成功',
    desc: '退款成功',
    list: '退款成功',
    type: '退款',
    success: true,
    isSystem: false,
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    closable: false,
    applyForAgain: false,
    isConsumer: false,
    msg: [{ title: '' }, { title: '' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: true,
  },
  RETURN_REFUND_REQUEST: {
    title: '请等待卖家处理',
    type: '退货退款',
    list: '申请退货退款 待审核',
    desc: '你已成功发起退货退款申请，请耐心请等待商家处理',
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    success: false,
    isSystem: false,
    closable: true,
    applyForAgain: false,
    isConsumer: true,
    msg: [{ title: '商家同意或者超时未支付，系统将退款给你' }, { title: '如果商家拒绝，你可以再次发起售后，卖家会重新处理' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: true,
    isRefunded: false,
  },
  RETURN_REFUND_AGREE: {
    title: '已同意退货退款',
    type: '退货退款',
    list: '申请退货退款 待退货',
    desc: '商家已同意退货退款申请，请尽早退货',
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    closable: true,
    isSystem: false,
    success: false,
    applyForAgain: false,
    isConsumer: false,
    msg: [
      { title: '超时未处理，系统将自动关闭退款申请' },
      { title: '未予商家协商一致，请勿使用到付或者平邮' },
      { title: '以免商家拒签货物请填写真实物流信息' },
    ],
    canChangePackageStatus: true,
    canChangePackageStatusText: true,
    isRefunded: false,
  },
  SYSTEM_RETURN_REFUND_AGREE: {
    title: '系统已同意退货退款',
    list: '申请退货退款 待退货',
    type: '退货退款',
    desc: '系统已同意退货退款申请，请尽早退货',
    isSystem: true,
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    closable: true,
    success: false,
    applyForAgain: false,
    isConsumer: false,
    msg: [
      { title: '超时未处理，系统将自动关闭退款申请' },
      { title: '未予商家协商一致，请勿使用到付或者平邮' },
      { title: '以免商家拒签货物请填写真实物流信息' },
    ],
    canChangePackageStatus: true,
    canChangePackageStatusText: true,
    isRefunded: false,
  },
  RETURN_REFUND_REJECT: {
    title: '退款关闭',
    type: '退货退款',
    list: '退货退款关闭',
    desc: '因商家拒绝了你的退货退款申请，本次售后关闭',
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    closable: false,
    isSystem: false,
    success: false,
    applyForAgain: true,
    isConsumer: false,
    msg: [{ title: '商家同意或者超时未支付，系统将退款给你' }, { title: '如果商家拒绝，你可以再次发起售后，卖家会重新处理' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  RETURNED_REFUND: {
    title: '请等待商家收货并退款',
    list: '申请退货退款 待商家收货',
    type: '退货退款',
    desc: '如果商家收到货并验货无误，将操作退款给您',
    success: false,
    closable: false,
    isSystem: false,
    applyForAgain: false,
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    isConsumer: true,
    msg: [{ title: '如果商家拒绝退款，需要您和商家协商' }, { title: '如果商家超时未处理，将自动退款给您' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  RETURNED_REFUND_CONFIRM: {
    title: '商家已收到退货，等待退款',
    type: '退货退款',
    list: '确认退货已收到',
    success: false,
    isSystem: false,
    desc: '商家已收到退货，请耐心请等待退款到账',
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    closable: false,
    applyForAgain: false,
    isConsumer: false,
    msg: [{ title: '商家同意或者超时未支付，系统将退款给你' }, { title: '如果商家拒绝，你可以再次发起售后，卖家会重新处理' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  SYSTEM_RETURNED_REFUND_CONFIRM: {
    title: '系统确认收到退货，等待退款',
    type: '退货退款',
    list: '系统确认退货已收到',
    success: false,
    isSystem: true,
    desc: '系统确认收到退货，请耐心请等待退款到账',
    countDown: {
      isCountDown: true,
      leftText: '还剩',
      rightText: '',
    },
    closable: false,
    applyForAgain: false,
    isConsumer: false,
    msg: [{ title: '商家同意或者超时未支付，系统将退款给你' }, { title: '如果商家拒绝，你可以再次发起售后，卖家会重新处理' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  RETURNED_REFUND_REJECT: {
    title: '退款关闭',
    type: '退货退款',
    list: '退货退款关闭',
    isSystem: false,
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    desc: '因商家拒绝收货，本次售后关闭',
    success: false,
    closable: true,
    applyForAgain: true,
    isConsumer: false,
    msg: [{ title: '商家同意或者超时未支付，系统将退款给你' }, { title: '如果商家拒绝，你可以再次发起售后，卖家会重新处理' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  RETURNED_REFUNDED: {
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    title: '退款成功',
    list: '退货退款成功',
    desc: '退款成功',
    type: '退货退款',
    success: true,
    closable: false,
    isSystem: false,
    applyForAgain: false,
    isConsumer: false,
    msg: [{ title: '' }, { title: '' }],
    canChangePackageStatus: false,
    canChangePackageStatusText: false,
    isRefunded: true,
  },

  BUYER_CLOSED: {
    title: '售后关闭',
    type: '售后',
    list: '售后关闭',
    desc: '买家撤销售后申请',
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    success: false,
    isSystem: false,
    closable: false,
    applyForAgain: true,
    isConsumer: true,
    msg: [{ title: '如你的问题未解决，你还可以继续发起申请' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
  SYSTEM_CLOSED: {
    title: '售后关闭',
    list: '超时未处理，系统关闭',
    type: '售后',
    desc: '系统自动关闭',
    countDown: {
      isCountDown: false,
      leftText: '',
      rightText: '',
    },
    success: false,
    closable: false,
    isSystem: true,
    applyForAgain: true,
    isConsumer: false,
    msg: [{ title: '如你的问题未解决，你还可以继续发起申请' }],
    canChangePackageStatus: true,
    canChangePackageStatusText: false,
    isRefunded: false,
  },
}
/**
 * @description: 撤销申请和再次申请按钮的提示文本
 * @returns {*}
 */
const getOrderListShowAfsStatusCn = (status: AfsStatus['AFSSTATUS']) => {
  if (['REFUND_REJECT', 'BUYER_CLOSED', 'SYSTEM_CLOSED'].includes(status)) return
  return orderListShowAfsStatus[status]
}

/**
 * @description: 退款详情title提示转为中文
 * @param {keyof} status
 * @returns {*}
 */
const getAfsStatusTitleCn = (status: AfsStatus['AFSSTATUS']) => {
  return afsStatus[status].title
}

/**
 * @description: 退款原因提示转为中文
 * @param {keyof} status
 * @returns {*}
 */
const getARefundReasonCn = (status: AfsStatus['ARefundType'] | string) => {
  return aRefundWhy[status].title
}
/**
 * @description: 退款详情desc提示转为中文
 * @param {keyof} status
 * @returns {*}
 */
const getAfsStatusDescCn = (status: AfsStatus['AFSSTATUS']) => {
  return afsStatus[status].desc
}
/**
 * @description: 售后状态配置
 * @param {keyof} status
 * @returns {*}
 */
const getAfsStatusCn = (status: AfsStatus['AFSSTATUS']) => {
  return afsStatus[status]
}
/**
 * @description: Btn售后状态转中文
 * @param {keyof} status
 * @returns {*}
 */
const getAsfsStatusBtnCn = (status: AfsStatus['AFSSTATUS']) => {
  return afsStatusBtn[status]
}
