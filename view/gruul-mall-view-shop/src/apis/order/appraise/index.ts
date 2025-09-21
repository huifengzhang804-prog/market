import { get, put } from '@/apis/http'
/**
 * 评价管理列表
 */
export const doGetEvaluate = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-order/order/evaluate',
        params,
    })
}
/**
 * 批量设为精选/取消精选
 * @param {boolean} isExcellent
 * @param {string} data id[]
 */
export const doPutIsExcellentEvaluate = (isExcellent: boolean, data: string[]) => {
    return put({
        url: `gruul-mall-order/order/evaluate/excellent/${isExcellent}`,
        data,
    })
}
/**
 * 商家回复
 * @param {boolean} evaluateId 评估id
 * @param {string} data 回复内容
 */
export const doPutShopReplyEvaluate = (evaluateId: string, data: string) => {
    return put({
        url: `gruul-mall-order/order/evaluate/${evaluateId}/reply`,
        data,
    })
}
