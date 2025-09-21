import { get, post, put, del, patch } from '@/apis/http'
import { BASE_URL } from '@/q-plugin/liveStream/apis'
/**
 * 平台查询商品列表
 * @param {any} auditStatus 商品状态
 * @param {any} productName 商品名称
 */
export const doGetLiveGoodsList = (data?: any): Promise<any> => {
    return get({
        url: BASE_URL + 'liveGoods',
        params: data,
    })
}
/**
 * 删除直播商品
 * @param {string} goodsIds
 */
export const doDelDeleteGoods = (goodsIds: string[]) => {
    return del({
        url: BASE_URL + `delete/goods/${goodsIds}`,
    })
}
