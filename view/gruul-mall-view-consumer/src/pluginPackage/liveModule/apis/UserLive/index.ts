import api from '@/libs/request'
import type { FollowLiveRoomList, ReservationLiveRoom, FollowLiveRoomUser } from './model'
import type { LiveRoomDetail } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import type { Pagination, Result } from '@/utils/types'
/**
 * 关注直播间列表
 */
export const doGetFollowLiveRoomList = (data: any) => {
  return api.get<Pagination<FollowLiveRoomList[]>>(`addon-live/user/follow/live/list`, data)
}
/**
 * 用户发现直播间
 */
export const doGetDiscoverLiveRoomList = (data: any) => {
  return api.get<Pagination<FollowLiveRoomList[]>>(`addon-live/user/discover/live/list`, data)
}
/**
 * 用户预约直播间
 */
export const doPostReservationLiveRoom = (data: ReservationLiveRoom) => {
  return api.post(`addon-live/user/add/reservation`, data)
}
/**
 * 用户关注主播
 */
export const doPostFollowLiveRoom = (data: FollowLiveRoomUser) => {
  return api.post(`addon-live/user/add/follow`, data)
}
/**
 * 用户随机获取一条直播间信息
 */
export const doGetRandomLiveRoom = (id: string) => {
  return api.get<LiveRoomDetail>(`addon-live/user/random/view/${id}`)
}
/**
 * 用户进入直播间时，添加直播间观看人数
 */
export const doPostViewership = (data: { liveId: string }) => {
  return api.post(`addon-live/user/viewership/${data.liveId}`)
}
/**
 * 用户是否关注 前端需要过滤一遍用户登陆状态
 */
export const doGetViewershipStatus = (anchorId: string) => {
  return api.get(`addon-live/user/viewership/status/${anchorId}`)
}
export function userIslogin() {
  // @ts-ignore
  return getApp().globalData.userInfo.token
}
