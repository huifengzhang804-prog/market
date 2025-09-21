import { ClassItemType } from '@/views/classification/types'
import { get, post, put, del, patch } from '../http'
import { L } from '../http.type'

/**
 * @description: 获取平台多级分类列表
 */
export const doGetCommodityByLevel = (params: any) => {
  return get<L<ClassItemType>>({ url: `gruul-mall-addon-platform/platform/category/list/level`, params })
}
/**
 * @description: 获取店铺分类列表
 */
export const doGetCategory = (shopId: string, params: any): Promise<any> => {
  return get({
    url: 'gruul-mall-goods/goods/product/category',
    params,
    headers: { 'Shop-Id': shopId },
  })
}
