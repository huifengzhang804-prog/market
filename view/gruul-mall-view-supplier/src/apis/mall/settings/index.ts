import { get, post, put, del, patch } from '../../http'

/**
 * 商家设置信息获取
 * @param data
 */
export const getsettings = (data: any): Promise<any> => {
    return get({
        url: '/gruul-mall-shop/shop/info',
        params: data,
    })
}

/**
 * 商家设置信息修改
 * @param data
 */
export const postsettings = (data: any) => {
    return post({
        url: '/gruul-mall-shop/shop/info/update',
        data,
    })
}
