interface GetUserMessage {
  id: string
  shopId: Long
  anchorId?: string
  anchorNickname: string
  anchorSynopsis: string
  anchorIcon: string
  status: Joint_User_Status
  gender: string
  phone: string
  followCount?: string
  viewership: string
  duration?: string
}
/**
 * 主播状态
 * @param NORMAL 正常
 * @param FORBIDDEN 禁用
 * @param VIOLATION 违规禁用
 */
enum USER_STATUS {
  NORMAL = 'NORMAL',
  FORBIDDEN = 'FORBIDDEN',
  VIOLATION = 'VIOLATION',
}
type Joint_User_Status = keyof typeof USER_STATUS
interface GetLiveRoomAnchorList {
  id: string
  anchorId: string
  userId: string
  liveTitle: string
  shopId: Long
  liveSynopsis: string
  reservationTime: string
  pushAddress: string
  pullAddress: string
  beginTime: string
  endTime: string
  status: LIVE_LIST_STATUS
  createTime: string
  updateTime: string
  pic: string
}
/**
 * 直播列表状态
 * @param NOT_STARTED = '未开播',
 * @param PROCESSING = '直播中',
 * @param OVER = '已结束',
 * @param ILLEGAL_SELL_OFF = '违规下架',
 */
enum LIVE_LIST_STATUS {
  NOT_STARTED = 'NOT_STARTED',
  PROCESSING = 'PROCESSING',
  OVER = 'OVER',
  ILLEGAL_SELL_OFF = 'ILLEGAL_SELL_OFF',
}
type Joint_Live_List_Status = keyof typeof LIVE_LIST_STATUS
interface CreateLiveRoomParameter {
  title: string
  liveSynopsis: string
  pic: string
  userId?: string
  beginTime?: string
}
interface LiveRoomDetail {
  id: string
  createTime: string
  updateTime: string
  anchorId: string
  anchorNickname: string
  shopId: Long
  shopName: string
  shopType: string
  shopLogo: string
  liveTitle: string
  liveSynopsis: string
  pic: string
  reservationTime: string
  streamName: string
  pushAddress: string
  pullAddress: string
  beginTime: string
  endTime: string
  status: Joint_Live_List_Status
  viewership: string
  duration: string
  anchor: Anchor
}
interface Anchor {
  id: string
  createTime: string
  updateTime: string
  shopId: Long
  userId: string
  phone: string
  anchorNickname: string
  anchorSynopsis: string
  anchorIcon: string
  status: Joint_User_Status
  gender: string
}

interface LvData {
  skinCareData: {
    index: number
  }
  whiteningData: {
    index: number
  }
}

export {
  type GetUserMessage,
  USER_STATUS,
  type Joint_User_Status,
  type GetLiveRoomAnchorList,
  type Joint_Live_List_Status,
  LIVE_LIST_STATUS,
  type CreateLiveRoomParameter,
  type LiveRoomDetail,
  type LvData,
}
