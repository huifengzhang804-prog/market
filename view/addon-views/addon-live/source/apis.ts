import { get, post, put, del, patch } from '@/apis/http'

/**
 * 直播间列表
 */
export const doGetRoomList = (params: any) => {
  return get({
    url: 'addon-live/manager/live/list',
    params,
  })
}
/**
 * 查看违禁原因
 */
export const doGetReason = (id: string, type: string) => {
  return get({
    url: `addon-live/manager/ban/reason/${id}/${type}`,
  })
}
/**
 * 管理端主播列表
 */
export const doGetAnchorList = (params: any) => {
  return get({
    url: 'addon-live/manager/anchor/list',
    params,
  })
}
/**
 * 启用/禁用主播
 */
export const putAnchorStaus = (id: string, isEnable: boolean) => {
  return put({
    url: `addon-live/manager/update/anchor/${id}/${isEnable}`,
  })
}

/**
 * 平台 恢复/违规禁播主播
 */
export const putAnchorStatusPlatform = (data) => {
  return put({
    url: `addon-live/manager/platform/update/anchor`,
    data,
  })
}
/**
 * 直播手机号列表
 */
export const doGetAnchorPhoneList = (params: any) => {
  return get({
    url: 'gruul-mall-uaa/uaa/shop/admin/anchor/list',
    params,
  })
}
/**
 * 添加主播
 */
export const postAddAnchor = (data) => {
  return post({
    url: `addon-live/manager/add/anchor`,
    data,
  })
}
