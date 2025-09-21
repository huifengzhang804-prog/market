import { get } from '../http'
import { L } from '../http.type'
import { ShopListVO } from '@/apis/shops/types/response'

/**
 * 获取商铺详细列表
 */
export const doGetShopList = (params: any) => {
    return get<L<ShopListVO>>({ url: '/gruul-mall-shop/shop/list', params })
}

/**
 * 获取商铺搜索栏信息
 * @param params
 */
export const doGetShopSearchList = (params: any) => {
    return get<ShopListVO[]>({ url: 'gruul-mall-shop/shop/info/getShopInfo', params })
}
