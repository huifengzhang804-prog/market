import api from '@/libs/request'
import type { GetLiveRoomAnchorList, GetUserMessage, CreateLiveRoomParameter, LiveRoomDetail } from './model'
import type { Pagination } from '@/utils/types'

/**
 * C端获取主播信息
 */
export const doGetUserMessage = () => {
  return api.get<GetUserMessage>('addon-live/user/message')
}
/**
 * 查询主播对应的直播间列表
 */
export const doGetLiveRoomAnchorList = (params: any) => {
  return api.get<Pagination<GetLiveRoomAnchorList[]>>('addon-live/live/room/anchor/list', params)
}
/**
 * 删除直播间
 */
export const doDelLiveRoom = (id: string) => {
  return api.delete(`addon-live/live/room/deleted/${id}`)
}
/**
 * 创建直播间
 */
export const doPostCreateLiveRoom = (data: CreateLiveRoomParameter) => {
  return api.post<{ id: string; pushAddress: string }>(`addon-live/live/room/create`, data)
}
/**
 * 直播间详情
 */
export const doGetLiveRoomDetail = (id: string) => {
  return api.get<LiveRoomDetail>(`addon-live/live/room/detail/${id}`)
}
/**
 * 直播分享图文字转图片
 */
export const doGetUserCharacters = (data: { liveTitle: string; liveSynopsis: string }) => {
  return api.get(`addon-live/user/characters`, data)
}
/**
 * 查询是否有已经开播的直播间
 */
export const doGetBeginLiveRoom = (id: string) => {
  return api.get(`addon-live/live/room/begin/${id}`)
}
/**
 * 下播
 */
export const doPutLiveRoomLower = (id: string) => {
  return api.put(`addon-live/live/room/lower/broadcast/${id}`)
}
/**
 * 直播间聊天室userSig
 */
export const doGetLiveRoomUserSig = (userId: string) => {
  return api.get(`addon-live/live/room/userSig/${userId}`)
}
