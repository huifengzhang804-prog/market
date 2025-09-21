import { get, post, put } from '../http'
import { http } from '@/utils/http'
import type { RetrieveParam, ApiRetrieve } from './model'
import type { DataType, ProductItem, ProductType, ShopInfoType } from '@/apis/good/types/productList'
import { L, R } from '../http.type'

/**
 * 分页获取商品和规格信息
 */
export const doGetProductSkus = (data?: any) => {
    return get({
        url: 'gruul-mall-goods/manager/product/getProductSkus',
        params: data,
    })
}
/**
 * 检索商品接口
 */
export const doGetRetrieveProduct = (retrieveParams: Partial<RetrieveParam>): Promise<any> => {
    return http.post<ApiRetrieve>({
        url: 'gruul-mall-search/search/product',
        data: retrieveParams,
        headers: {
            unRemovePending: true,
        },
    })
}

/**
 * 批量查询店铺信详情
 */
export const doPostShopInfo = (shopIds: string[]) => {
    return post<ShopInfoType[]>({ url: `gruul-mall-shop/shop/info/batch`, data: shopIds })
}

/**
 * 获取商品列表
 */
export const doGetProductList = (params: any) => {
    return get<L<ProductItem>>({
        url: 'gruul-mall-addon-platform/platform/product/get/all',
        params,
    })
}

/**
 * 更新商品状态
 */

export const doUpdateSellStatus = (ids: any, status: string) => {
    return put({
        url: `gruul-mall-goods/manager/product/updateStatus/${status}`,
        data: ids,
    })
}

export const doGetHighestCategoryLevel = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-goods/goods/product/category/categoryLevel1WithProductNum`,
        params,
    })
}

export const doGetSupplierList = (params: any) => {
    return get({
        url: '/addon-supplier/supplier/manager/product/list',
        params,
    })
}
/**
 * 供应商商品下架
 * @param data 下架参数
 * @param status 状态
 */
export const doUpdateSupplierSellStatus = (data: any, status = '') => {
    return put({
        url: `addon-supplier/supplier/manager/product/updateStatus/${status}`,
        data,
    })
}

export const doGetSeachSupplierSearchList = (params: any = {}) => {
    return get({ url: 'gruul-mall-shop/shop/info/getSupplierInfo', params })
}

/**
 * 获取商品详情接口
 * @param id 商品id号
 */
export const doGetCommodityDetails = (id: any, params: any) => {
    return get({ url: `gruul-mall-goods/manager/product/show/${id}`, params })
}
/**
 * 获取供应商商品详情接口
 * @param id 商品id号
 * @param params 查询参数信息
 */
export const doGetSupplierCommodityDetails = (id: any, params: any = {}) => {
    return get({ url: `addon-supplier/supplier/manager/product/show/${id}`, params })
}

/**
 * 获取平台端商家待审核商品
 * @param params 查询参数信息
 */
export const doGetShopExamineGoods = (params: any = {}) => {
    return get<L<ProductType>>({ url: 'gruul-mall-addon-platform/platform/product/get/audit/all', params })
}

export const doPutShopExamineGoods = (status: string, data: any) => {
    return put({ url: `gruul-mall-goods/manager/product/updateStatus/${status}`, data })
}

/**
 * 店铺违规下架商品恢复销售
 * @param params 查询参数信息
 */
export const doPostShopRestoreSale = (data: DataType) => {
    return post({
        url: `gruul-mall-goods/manager/product/restoreSale`,
        data,
    })
}
/**
 * 供应商违规下架商品恢复销售
 * @param params 查询参数信息
 */
export const doPostSupplierRestoreSale = (data: DataType) => {
    return post({
        url: `addon-supplier/supplier/manager/product/restoreSale`,
        data,
    })
}
