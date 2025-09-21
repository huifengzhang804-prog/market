import { get, post, put, del, patch } from '../http'
import type { SavingManageItem } from '@/views/vipMoney/types'
/**
 * 获取储值管理信息
 */
export const doGetSavingManage = (params?: any) => {
    return get({
        url: '/gruul-mall-user/user/saving/manage/get',
        params,
    })
}
/**
 * 修改储值管理信息开关
 */
export const doGPutSavingManage = (status: boolean | string | number | undefined) => {
    return put({
        url: `/gruul-mall-user/user/saving/manage/update/${status}`,
    })
}
/**
 * 编辑储值管理信息
 * @param {boolean} discountsState 优惠状态 0无优惠 1有优惠
 * @param {SavingManageItem} ruleJson  储值管理列表不许传递id
 */
export const doPostSavingManage = (id: string, discountsState: boolean, ruleJson: SavingManageItem[]) => {
    return post({
        url: `/gruul-mall-user/user/saving/manage/edit`,
        data: {
            id,
            discountsState,
            ruleJson,
        },
    })
}
//
