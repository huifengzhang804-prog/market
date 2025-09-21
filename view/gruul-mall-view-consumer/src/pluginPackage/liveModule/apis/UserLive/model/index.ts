import type { Joint_Live_List_Status } from '@/pluginPackage/liveModule/apis/CreateLive/model'
// pullAddress 是一个逗号字符串拼接 顺序为 RTMP地址  FLV地址  HLS地址  WebRTC地址
interface FollowLiveRoomList {
  beginTime: string
  liveId: string
  shopId: Long
  shopName: string
  shopLogo: string
  shopType: string
  viewership: string
  liveTitle: string
  pic: string
  pushAddress: string
  pullAddress: string
  status: Joint_Live_List_Status
  isReservation: boolean
}
interface ReservationLiveRoom {
  liveId: string
  shopId: Long
  isReservation: boolean
}
interface FollowLiveRoomUser {
  anchorId: string
  shopId: Long
  isFollow: boolean
}
export type { FollowLiveRoomList, ReservationLiveRoom, FollowLiveRoomUser }
