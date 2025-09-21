import { get, post, put, del } from '../http'
import type { LocationQueryValue } from 'vue-router'
import type { FreeMemberInfo, MemberConfig, MemberRights, PageMemberRights } from './types'

/**
 * 获取会员列表
 */
export const doGetMemberBenefit = (params: any) => {
    return get<PageMemberRights>({
        url: 'gruul-mall-user/user/member/rights',
        params,
    })
}
/**
 * 新增会员权益
 */
export const doPostMemberBenefit = (data: any) => {
    return post({
        url: 'gruul-mall-user/user/member/rights/save',
        data,
    })
}
/**
 * 修改会员权益
 */
export const doPutMemberBenefit = (data: any) => {
    return post({
        url: 'gruul-mall-user/user/member/rights/update',
        data,
    })
}
/**
 * 开启关闭会员权益
 */
export const doPutMemberBenefitStatus = (switchType: boolean, id: string) => {
    return put({
        url: `gruul-mall-user/user/member/rights/${switchType}`,
        params: {
            id,
        },
    })
}
/**
 * 删除会员权益
 */
export const doDelMemberBenefit = (ids: number[]) => {
    return del({
        url: `gruul-mall-user/user/member/rights/del/${ids} `,
    })
}
/**
 * 恢复默认
 */
export const doGetMemberBenefitDefault = (type: string) => {
    return get<MemberRights>({
        url: `gruul-mall-user/user/member/rights/default?rightsType=${type}`,
    })
}

/**
 * 获取免费会员列表
 */
export const doGetFreeMemberList = (): Promise<any> => {
    return get({
        url: 'gruul-mall-user/user/free/member/list',
    })
}

/**
 * 获取会员权益配置
 */
export const doGetAvailableMemberConfig = () => {
    return get<MemberConfig[]>({
        url: 'gruul-mall-user/user/member/rights/usable',
    })
}
/**
 * 保存免费会员配置
 */
export const doPostAvailableMemberConfig = (data: any) => {
    return post({
        url: 'gruul-mall-user/user/free/member/save',
        data,
    })
}
/**
 * 删除免费会员
 */
export const doPostAvailableMember = (id: string) => {
    return del({
        url: `gruul-mall-user/user/free/member/${id}`,
    })
}
/**
 * 获取免费会员配置
 */
export const doGetAvailableMember = (id: string) => {
    return get<FreeMemberInfo>({
        url: `gruul-mall-user/user/free/member/info?id=${id}`,
    })
}
/**
 * 更新免费会员
 */
export const doPutAvailableMember = (data: any) => {
    return post({
        url: 'gruul-mall-user/user/free/member/update',
        data,
    })
}
/**
 * 获取会员详情
 */
export const doGetMemberDetail = (id: string) => {
    return get({
        url: `gruul-mall-user/user/info?userId=${id}`,
    })
}
/**
 * 获取会员详情余额支付信息
 */
export const doGetPaymentInfo = (userId: string) => {
    return get({
        url: `gruul-mall-payment/user/payment/history/savings/info?userId=${userId}`,
    })
}
/**
 * 获取会员详情余额明细
 */
export const doGetBalanceHistory = (userId: string, params: any): Promise<any> => {
    return get({
        url: `gruul-mall-payment/user/payment/history`,
        params: {
            userId,
            ...params,
        },
    })
}
/**
 * 获取会员详情交易明细
 */
export const doGetTradeList = (userId: string | LocationQueryValue[], params: any): Promise<any> => {
    return get({
        url: `gruul-mall-user/user/deal/detail/list/${userId}`,
        params: {
            ...params,
        },
    })
}
/**
 * 会员详情修改用户信息
 */
export const doPostMemberInfo = (data: any) => {
    return post({
        url: 'gruul-mall-uaa/uaa/user/data',
        data,
    })
}

/**
 * 免费会员设置会员标签
 */
export const doPostFreeMemberSetLabel = (data: any) => {
    return post({ url: 'gruul-mall-user/user/free/member/saveLabel', data })
}
