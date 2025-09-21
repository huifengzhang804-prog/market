import { del, get, post, put } from '../http'
import { http } from '@/utils/http'
import type { ApiRetrieve, RetrieveParam } from './model'

/**
 * @description: 检索商品接口
 */
export const doGetRetrieveProduct = (retrieveParams: Partial<RetrieveParam>) => {
    return http.post<ApiRetrieve>({
        url: 'gruul-mall-search/search/product',
        data: retrieveParams,
    })
}

/**
 * @description: 获取分类列表
 */
export const doGetCategory = (params: any) => {
    return get({
        url: 'gruul-mall-goods/goods/product/category',
        params,
    })
}

/**
 * @description: 获取商品列表
 * @param {any} params
 */
export const doGetCommodity = (params: any): Promise<any> => {
    return get({
        url: 'addon-supplier/supplier/manager/product/list',
        params,
    })
}
/**
 * @description: 库存中心 获取商品列表
 * @param {any} params
 */
export const doGetInventoryCommodity = (params: any): Promise<any> => {
    return get({
        url: 'addon-supplier/supplier/manager/product/productStock/list',
        params,
    })
}

/**
 * @description: 获取平台类目列表
 */
export const doGetPlatformCategory = (params: any = {}) => {
    return get({
        url: 'gruul-mall-addon-platform/platform/shop/signing/category/choosable/list',
        params,
    })
}
/**
 * @description: 发布商品
 * @param {any} data
 */
export const doReleaseGood = (data: any) => {
    return post({
        url: 'addon-supplier/supplier/manager/product/issue',
        data,
    })
}
/**
 * @description: 编辑商品
 * @param {any} data
 */
export const doUpdateCommodity = (data: any) => {
    return put({
        url: 'addon-supplier/supplier/manager/product/update',
        data,
    })
}
/**
 * @description: 更新商品状态
 * @param {string} ids
 * @param {string} status
 * @returns {*}
 */
export const doUpdateSellStatus = (productIds: string[], status: string) => {
    return put({
        url: `addon-supplier/supplier/manager/product/updateStatus/${status}`,
        data: {
            productIds,
        },
    })
}
/**
 * @description: 删除商品
 * @param {string} ids
 */
export const doDeleteCommodity = (ids: any) => {
    return del({
        url: `addon-supplier/supplier/manager/product/delete/${ids}`,
    })
}
/**
 * @description: 获取单个商品信息
 * @param {string} id
 */
export const doGetSingleCommodity = (id: any, shopId: string) => {
    return get({
        url: `addon-supplier/supplier/manager/product/get/${shopId}/${id}`,
    })
}
/**
 * @description: 设置限购
 * @param {any} data
 * @returns {*}
 */
export const doSetPurchase = (productId: string, data: any) => {
    return post({
        url: `/gruul-mall-storage/storage/product/${productId}/limit`,
        data,
    })
}
export const doSetInventory = (productId: string, data: any) => {
    return post({
        url: `gruul-mall-storage/storage/product/${productId}/stock`,
        data,
    })
}
/**
 * @description: 查询商品规格组与sku列表
 */
export const doGetCommoditySku = (shopId: string, productId: any): Promise<any> => {
    return get({
        url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}?isSupplier=true`,
    })
}
/**
 * @description: 查询商品库存与限购
 */
export const doGetProductPurchasing = (productId: string): Promise<any> => {
    return get({
        url: `gruul-mall-storage/storage/product/${productId}`,
    })
}

/**
 * @description: 查询一级分类和分类下的商品数量
 */
export const doGetHighestCategoryLevel = (params: any) => {
    return get({
        url: `gruul-mall-goods/goods/product/category/categoryLevel1WithProductNum`,
        params,
    })
}

/**
 *获取品牌列表
 */
export const getBrandList = (data: any) => {
    return get({
        url: 'gruul-mall-search/search/brand/brandInfo',
        params: data,
    })
}
/*
 * @description: 商品一键复制
 * @param {string} id
 */
export const doGetCopyGoods = (params: any) => {
    return get({
        url: `gruul-mall-goods/api/copy/goods/detail`,
        params,
    })
}
/*
 * @description: 产品 特性列表 I
 * @param {string} id
 */
export const doGetfeatureList = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-goods/manager/feature/list`,
        params,
    })
}

/*
 * @description: 产品 特性新增
 * @param {string} data
 */
export const dofeatureSave = (data: any) => {
    return post({
        url: `gruul-mall-goods/manager/feature/save`,
        data,
    })
}

/*
 * @description: 产品 特性编辑
 * @param {string} data
 */
export const dofeatureUpdate = (data: any) => {
    return post({
        url: `gruul-mall-goods/manager/feature/update`,
        data,
    })
}

/*
 * @description: 产品 特性编辑
 * @param {string} id
 */

export const dofeatureDel = (featuresType: string, data: any) => {
    return put({
        url: `gruul-mall-goods/manager/feature/del?featuresType=${featuresType}`,
        data,
    })
}

/*
 * @description: 商品名称修改
 * @param {string} id
 */
export const doNameUpdate = ({ id, name }: { id: string; name: string }) => {
    return put({
        url: `addon-supplier/supplier/manager/product/update/${id}`,
        data: {
            name,
        },
    })
}

/*
 * @description: 更新商品价格
 * @param {string} id
 */
export const doPriceUpdate = (data: any, productId: string) => {
    return post({
        url: `gruul-mall-storage/storage/product/${productId}/price`,
        data,
    })
}

/**
 * @description 获取供应商商品详情接口
 * @param id 商品id号
 * @param params 查询参数信息
 * @returns
 */
export const doGetSupplierCommodityDetails = (id: any, params: any = {}) => {
    return get({ url: `addon-supplier/supplier/manager/product/show/${id}`, params })
}

/**
 * @description 获取商品审核列表信息
 * @param params 参数信息
 * @returns
 */
export const doGetExamineGoodsList = (params: any = {}) => {
    return get({ url: 'addon-supplier/supplier/manager/product/audit', params })
}
/**
 * @description 重新审核商品信息
 * @param id 商品ID号
 * @returns
 */
export const doPutReviewGoodsInfo = (id: string) => {
    return put({ url: `addon-supplier/supplier/manager/product/audit/product/submit/${id}` })
}
