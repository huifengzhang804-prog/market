import { get, put, post, del } from '../../http'

export const doGetShopDeliveryConfig = (): Promise<any> => {
    return get({ url: '/gruul-mall-shop/shop/deliver' })
}

export const doPutShopDeliveryConfig = (data: any) => {
    return put({ url: '/gruul-mall-shop/shop/deliver', data })
}

export const doGetLogisticsList = (data: any): Promise<any> => {
    return get({
        url: 'gruul-mall-shop/shop/logistics/address/list',
        params: data,
    })
}

//修改或者新增地址
export const setLogisticsDddress = (data: any) => {
    return post({
        url: 'gruul-mall-shop/shop/logistics/address/set',
        data,
    })
}

export const delLogisticsById = (id: string) => {
    return del({
        url: `gruul-mall-shop/shop/logistics/address/del/${id}`,
    })
}

/**
 * 快递设置信息获取
 */
export const doGetCourierInfo = () => {
    return get({ url: 'gruul-mall-freight/logistics/settings/get' })
}
interface PrintSetForm {
    id: string
    customer: string
    key: string
    secret: string
}
/**
 * 快递设置新增/修改
 */
export const doCourierUpdateAndEdit = (data: PrintSetForm) => {
    return post({ url: 'gruul-mall-freight/logistics/settings/edit', data })
}
/**
 * 联系买家
 */
export const postAddContact = (shopId: number, userId: string) => {
    return post({ url: `gruul-mall-carrier-pigeon/pigeon/platform-chat-rooms/${shopId}/${userId}` })
}
