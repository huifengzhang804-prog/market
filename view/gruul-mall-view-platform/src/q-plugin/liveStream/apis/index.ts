import { get, post, put, del, patch } from '@/apis/http'
export const BASE_URL = 'gruul-mall-live/platform/live/'
/**
 * 平台查询直播列表
 * @param {any} data
 */
export const doGetLiveList = (data?: any): Promise<any> => {
    return get({
        url: BASE_URL + 'liveRoom',
        params: data,
    })
}
/**
 * 分享直播间
 * @param {any} data
 */
export const doGetShareLiveRoom = (roomId: string): Promise<any> => {
    return get({
        url: BASE_URL + '/share/live/room',
        params: { roomId },
    })
}
/**
 * 删除直播间
 * @param {string} roomIds
 */
export const doDelDeleteRoom = (roomIds: string[]) => {
    return del({
        url: BASE_URL + `liveRoom/${roomIds}`,
    })
}

/**
 * 获取直播间角色列表
 * @param {string} username
 */
export const doGetLiveMemberList = (params: any): Promise<any> => {
    return get({
        url: BASE_URL + `get/liveUser`,
        params,
    })
}
/**
 * 删除直播间角色
 * @param {string} ids
 */
export const doDelDeleteLiveUser = (roomUserIds: any) => {
    return del({
        url: BASE_URL + `delete/liveUser/${roomUserIds}`,
    })
}
