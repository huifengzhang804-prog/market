// 商品相关
import { get, post, put, del, patch } from '@/apis/http'
import type { LocationQueryValue } from 'vue-router'
import { BASE_URL } from '@/q-plugin/liveStream/apis'
/**
 * 添加商品
 * @param {string} data
 */
export const doPostGoodsAdd = (data: any) => {
    return post({
        url: BASE_URL + `goodsAdd`,
        data,
    })
}
/**
 * 添加商品
 * @param {string} data
 */
export const doGetGoodsAdd = (params?: any): Promise<any> => {
    return get({
        url: BASE_URL + `getGoods`,
        params,
    })
}
/**
 * 重新提交审核
 * @param {string} goodsId 微信方goodsId
 */
export const doPutResubmitAudit = (goodsId: string, data?: any) => {
    return put({
        url: BASE_URL + `resubmitAudit/${goodsId}`,
        data,
    })
}
/**
 * 查看商品
 * @param {string} goodsId 微信方goodsId
 */
export const doGetGoodsInfo = (goodsId: string, data?: any) => {
    return get({
        url: BASE_URL + `get/goodsInfo/${goodsId}`,
        data,
    })
}
/**
 * 修改商品
 * @param {string} data
 */
export const doPutGoodsUpdate = (data: any) => {
    return put({
        url: BASE_URL + `goodsUpdate`,
        data,
    })
}
/**
 * 撤销申请商品
 * @param {string} data
 */
export const doPutGoodsResetAudit = (goodsId: string, auditId: string) => {
    return put({
        url: BASE_URL + `goodsResetAudit/${goodsId}/${auditId}`,
    })
}
/**
 * 删除直播商品
 * @param {string} data
 */
export const doDeldeleteGoodsInfos = (goodsIds: string[]) => {
    return del({
        url: BASE_URL + `deleteGoodsInfos/${goodsIds}`,
    })
}
/**
 * 查看直播间的直播商品
 * @param {string} data
 */
export const doGetRoomGoods = (params: any): Promise<any> => {
    return get({
        url: BASE_URL + `getRoom/goods`,
        params,
    })
}
/**
 * 查看审核成功的直播商品
 * @param {string} data
 */
export const doGetGetGoods = (params: any, auditStatus = 'APPROVED'): Promise<any> => {
    return get({
        url: BASE_URL + `/getGoods`,
        params: {
            ...params,
            auditStatus,
        },
    })
}
/**
 * 添加直播间商品
 * @param {string} roomId
 * @param {string} goodsIds
 */
export const doPostAddRoomGoods = (roomId: string, goodsIds: string[]) => {
    return post({
        url: BASE_URL + `/addRoom/goods`,
        data: {
            roomId,
            goodsIds,
        },
    })
}
