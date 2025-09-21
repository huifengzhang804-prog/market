import { get, put, post } from '../http'
/**
 * @description: 订单备注
 * @param {string} ids  总订单号 或者 店铺订单号 平台端 为总订单号列表 商家端 为店铺订单号列表
 * @param {string} status
 */
export const doPutOrderRemark = (nos: string[], remark: string) => {
    return put({
        url: `gruul-mall-order/order/remark/batch`,
        data: {
            nos,
            remark,
        },
    })
}
/**
 * @description: 售后订单备注
 * @param {string} ids
 * @param {string} status
 */
export const doPutAfsOrderRemark = (nos: string[], remark: string) => {
    return put({
        url: `gruul-mall-afs/afs/order/remark/batch`,
        data: {
            nos,
            remark,
        },
    })
}
