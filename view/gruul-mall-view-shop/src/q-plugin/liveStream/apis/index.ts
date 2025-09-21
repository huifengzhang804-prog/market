import { get, post, put, del, patch } from '@/apis/http'
import type { LocationQueryValue } from 'vue-router'
export const BASE_URL = 'gruul-mall-live/live/broadcast/'
/**
 * 创建直播间
 * @param {any} data
 */
export const doPostCreateRoom = (data?: any) => {
    return post({
        url: BASE_URL + 'createRoom',
        data: data,
    })
}
/**
 * 获取直播间列表
 * @param {any} data
 */
export const doGetLiveList = (data?: any): Promise<any> => {
    return get({
        url: BASE_URL + 'getLiveList',
        params: data,
    })
}
/**
 * 获取直播间信息
 * @param {any} data
 */
export const doGetLiveInfo = (id: string | LocationQueryValue[]) => {
    return get({
        url: BASE_URL + 'getLiveInfo',
        params: { id },
    })
}
/**
 * 删除直播间
 * @param {any} data
 */
export const doDelDeleteRoom = (ids: string[]) => {
    return del({
        url: BASE_URL + `deleteRoom/${ids}`,
    })
}
// 角色相关
/**
 * 添加直播间角色
 * @param {string} username
 */
export const doPostAddRole = (data: { username: string; role: string }) => {
    return post({
        url: BASE_URL + `addRole`,
        data,
    })
}
/**
 * 获取直播间角色列表
 * @param {string} username
 */
export const doGetLiveMemberList = (params: any): Promise<any> => {
    return get({
        url: BASE_URL + `getLiveMember`,
        params,
    })
}
/**
 * 删除直播间角色
 * @param {string} ids
 */
export const doDelDeleteLiveUser = (ids: any) => {
    return del({
        url: BASE_URL + `delete/liveUser/${ids}`,
    })
}
