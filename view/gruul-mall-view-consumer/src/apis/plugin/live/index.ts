import api from '@/libs/request'
/**
 * 查询直播间列表
 */
export const doGetLiveList = (params: any) => {
  return api.get('gruul-mall-live/platform/live/applets/liveRoom', params)
}
