import { L } from '@/utils/types'
import { get, post } from '../http'
import { OrderDetail } from './types'

export const doGetUserTags = (shopId = '' as string | number, bound = false): Promise<any> => {
    return get({
        url: 'gruul-mall-user/user/userTag',
        params: { shopId, bound },
    })
}

export const doPostUserTag = (data: any) => {
    return post({
        url: 'gruul-mall-user/user/userTag',
        data,
    })
}

export const doGetShopMemberList = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-user/user/shopUserAccount',
        params,
    })
}

export const doGetShopMemberDetails = (userId: string) => {
    return get({
        url: 'gruul-mall-user/user/shopUserAccount/detail',
        params: { userId },
    })
}

export const doGetMemberTransactionList = (userId: string, params = {}) => {
    return get<L<OrderDetail>>({ url: `gruul-mall-user/user/deal/detail/list/${userId}`, params })
}
