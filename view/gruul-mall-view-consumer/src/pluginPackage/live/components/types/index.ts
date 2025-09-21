enum GOODS_TYPE {
  UNAPPROVED = 'UNAPPROVED',
  UNDER_REVIEW = 'UNDER_REVIEW',
  APPROVED = 'APPROVED',
  FAILED_APPROVED = 'FAILED_APPROVED',
  VIOLATION__OFF_SHELF = 'VIOLATION__OFF_SHELF',
}
type GoodsJointType = keyof typeof GOODS_TYPE

/**
 * 波波间状态
 * @param {LIVE_BROADCAST} 直播中
 * @param {NOT_STARTED} 没有开始
 * @param {CLOSED} 关闭
 */
enum ROOM_STATUS {
  LIVE_BROADCAST,
  NOT_STARTED,
  CLOSED,
  PROCESSING,
  OVER,
  ILLEGAL_SELL_OFF,
}
export type RoomStatusJointType = keyof typeof ROOM_STATUS

export interface ApiLiveItem {
  coverImg: string
  examineList?: LiveGoods[]
  id: string
  roomName: string
  shopId: Long
  shopLogo: string
  shopName: string
  status: RoomStatusJointType
  wechatRoomId: string
}
interface LiveGoods {
  auditId: string
  auditStatus: GoodsJointType
  coverImgUrl: string
  createTime: string
  deleted: false
  goodsId: string
  id: string
  ossImgUrl: string
  price: Long
  price2: string
  priceType: 0 | 1 | 2
  productId: Long
  productName: string
  shopId: Long
  shopName: string
  updateTime: string
  url: string
  verifyTime: string
}
