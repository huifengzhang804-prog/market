enum PACKAGESTATUS {
  WAITING_FOR_DELIVER,
  WAITING_FOR_RECEIVE,
  BUYER_WAITING_FOR_COMMENT,
  SYSTEM_WAITING_FOR_COMMENT,
  BUYER_COMMENTED_COMPLETED,
  SYSTEM_COMMENTED_COMPLETED,
}
enum A_REfUND_WHY {
  DONT_NEED_IT,
  WRONG_FORM,
  EXPRESS_STAGNATED,
  EXPRESS_REJECTION,
  SEVEN_DAYS_NO_REASON,
  WRONG_PRODUCT_DESC,
  DONT_LIKE_IT,
  SOME_MISSING,
  QUALITY_PROBLEM,
  WRONG_PRODUCT,
}

enum AFSSTATUS {
  NONE,
  REFUND_REQUEST,
  SYSTEM_REFUND_AGREE,
  REFUND_AGREE,
  REFUND_REJECT,
  SYSTEM_RETURN_REFUND_AGREE,
  REFUNDED,
  SYSTEM_RETURNED_REFUND_CONFIRM,
  RETURN_REFUND_REQUEST,
  RETURN_REFUND_AGREE,
  RETURN_REFUND_REJECT,
  RETURNED_REFUND,
  SYSTEM_CLOSED,
  RETURNED_REFUND_CONFIRM,
  RETURNED_REFUND_REJECT,
  RETURNED_REFUNDED,
  BUYER_CLOSED,
}
/**
 * 后端返回的User接口信息
 */
interface ApiUserData {
  avatar: string
  createTime: string
  deleted: false
  gender: string
  id: string
  nickname: string
  updateTime: string
  userId: string
  version: 0
}

/**
 * 加工后的协商历史数据
 */
interface ProcessingHistory {
  type: string
  title: string
  isConsumer: boolean
  afsStatusCn?: string
  reason?: keyof typeof A_REfUND_WHY
  refundAmount?: string
  afsStatus: keyof typeof AFSSTATUS
  createTime: string
  evidences: string[]
  id: string
  logo: string
  name: string
  packageStatus: keyof typeof PACKAGESTATUS
  remark: string
  updateTime: string
  historyBuyerReturnedInfo?: Obj
}

export type { ApiUserData, ProcessingHistory }
