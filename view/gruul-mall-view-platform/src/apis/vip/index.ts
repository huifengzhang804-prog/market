import { get, post } from '../http'
import { L } from '../http.type'
import { PurchaseType, UserTagVo } from './types'

/**
 * 请求参数
 */
export interface ParamsUserTag {
    addTagList?: TagItemA[]
    updateTagList?: TagItem[]
    delUserTagIdList?: string[]
    userIdList: string[]
}
/**
 * 添加标签
 * @param {*}id 会员标签id
 * @param {*}tagName 会员标签名称
 * @param {*} option 是否选中true选中,false未选中
 */
interface TagItemA {
    tagName: string
    option: boolean
}
/**
 * 添加标签
 * @param {*}id 会员标签id
 * @param {*}tagName 会员标签名称
 * @param {*} option 是否选中true选中,false未选中
 */
interface TagItem extends TagItemA {
    id: string
}
/**
 * 分页查询会员信息
 */
export const doGetBaseVipList = (params: any) => {
    return get({
        url: '/gruul-mall-user/user/list',
        params,
    })
}
/**
 * 更新标签
 */
export const doPostUserTag = (data: ParamsUserTag) => {
    return post({
        url: '/gruul-mall-user/user/userTag',
        data,
    })
}
/**
 * 查询会员所有标签
 */
export const doGetUserTag = (params = {}) => {
    return get<UserTagVo[]>({
        url: '/gruul-mall-user/user/userTag',
        params,
    })
}
/**
 * 会员余额调整(充值/扣除)
 */
export const doPostbalanceChange = (userId: number, value: number, changeType: 'INCREASE' | 'REDUCE', extendInfo = '') => {
    return post({
        url: '/gruul-mall-user/user/balance/change',
        data: { userId, value, changeType, extendInfo },
    })
}
/**
 * 获取黑名单列表
 */
export const doGetBlackList = (params: any) => {
    return get({
        url: 'gruul-mall-user/user/blacklist',
        params,
    })
}
/**
 * 拉入或者移除
 */
export const doPutLimitPermission = (userIds: string[], roles: string[], explain?: string) => {
    return post({
        url: `gruul-mall-uaa/uaa/user/data/update/authority`,
        data: {
            userIds,
            roles,
            explain,
        },
    })
}
/**
 * 获取成长值配置信息
 */
export const doGetGrowthValueSettings = (): Promise<any> => {
    return get({
        url: 'gruul-mall-user/user/growthValue/settings',
    })
}
/**
 * 修改成长值配置信息
 */
export const doPostGrowthValueSettings = (data: any) => {
    return post({
        url: 'gruul-mall-user/user/growthValue/settings',
        data,
    })
}
/**
 * 调整成长值
 * @param { string } userId 用户id号
 * @param { string } growthValue 被调整的成长值
 * @param { 'INCREASE' | 'REDUCE' } changeType 调整类型
 */
export const doPostGrowthValueChange = (userId: number, growthValue: number, changeType: 'INCREASE' | 'REDUCE') => {
    return post({
        url: '/gruul-mall-user/user/growthValue/change',
        data: { userId, growthValue, changeType },
    })
}
/**
 * 获取会员记录信息
 */
export const doGetMemberPurchaseList = (params: any = {}) => {
    return get<L<PurchaseType>>({ url: 'gruul-mall-user/member/purchase/list', params })
}
/**
 * 导出会员记录
 */
export const doPostExportMemberPurchaseList = (data: any = {}) => {
    return post({ url: 'gruul-mall-user/member/purchase/export', data })
}
/**
 * 获取储值流水列表
 */
export const doGetUserBalanceList = (params: any = {}): Promise<any> => {
    return get({ url: 'gruul-mall-user/user/balance/list', params })
}
/**
 * 导出储值流水列表
 */
export const doPostExportUserBalanceList = (data: any = {}) => {
    return post({ url: 'gruul-mall-user/user/balance/export', data })
}
/**
 * 批量备注储值流水
 */
export const doPostBatchRemarkUserBalance = (data: any = {}) => {
    return post({ url: 'gruul-mall-user/user/balance/remark', data })
}
