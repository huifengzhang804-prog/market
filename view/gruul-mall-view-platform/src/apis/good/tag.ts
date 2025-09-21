import { get, post, put, del } from '../http'
import { L } from '../http.type'
import { LabelType } from './types/productList'
/**
 * @description 获取商品标签列表
 * @param data 标签列表传递参数信息
 * @returns
 */
export const doGetProductLabel = (data?: any) => {
    return get<L<LabelType>>({
        url: 'gruul-mall-goods/goods/product/label',
        params: data,
    })
}
/**
 * @description 添加商品标签
 * @param data 标签信息数据
 * @returns
 */
export const doPostProductLabel = (data: any) => {
    return post({ url: 'gruul-mall-goods/goods/product/label', data })
}

/**
 * @description 编辑商品标签
 * @param data 标签信息数据
 * @returns
 */
export const doPutProductLabel = (data: any) => {
    return put({ url: `gruul-mall-goods/goods/product/label/${data?.id}`, data })
}
/**
 * @description 删除商品标签
 * @param id 标签信息编号
 * @returns
 */
export const doDeleteProductLabel = (id: any) => {
    return del({ url: `gruul-mall-goods/goods/product/label/${id}` })
}
