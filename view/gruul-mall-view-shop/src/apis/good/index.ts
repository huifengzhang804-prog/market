import { del, get, patch, post, put } from '../http'
import { http } from '@/utils/http'
import type { ApiRetrieve, CategoryLevel1WithProductNum, ListOfProductTagsType, RetrieveParam } from './model'
import { CommoditySaleType } from '@/views/goods/types'
import { L } from '@/utils/types'

/**
 * 检索商品接口
 */
export const doGetRetrieveProduct = (retrieveParams: Partial<RetrieveParam>) => {
    return http.post<ApiRetrieve>({
        url: 'gruul-mall-search/search/product',
        data: retrieveParams,
    })
}

/**
 * 获取分类列表
 */
export const doGetCategory = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-goods/goods/product/category',
        params,
    })
}

/**
 * 获取select类目选择项
 */
export const doGetCategoryLevel = (level: string | number, parentId: string | number): Promise<any> => {
    return get({
        url: `gruul-mall-goods/goods/product/category/by/parent/${parentId}`,
        params: {
            level: `LEVEL_${Number(level) + 1}`,
            size: 1000,
        },
        showLoading: false,
    })
}
/**
 * 新增分类
 */
export const doNewCategory = (data: any) => {
    return post({
        url: 'gruul-mall-goods/goods/product/category',
        data,
    })
}
/**
 * 分类排序
 */
export const doSortCategory = (ids: string[], parentId: string) => {
    return patch({
        url: 'gruul-mall-goods/goods/product/category',
        data: {
            sortedIds: ids,
            parentId,
        },
        headers: { 'Content-Type': 'application/json' },
        showLoading: false,
    })
}
/**
 * 更新类目
 */
export const doUpdateCategory = (id: Long, data: any) => {
    return put({
        url: `gruul-mall-goods/goods/product/category/${id}`,
        data,
    })
}
/**
 * 删除类目
 */
export const doDelCategory = (id: string) => {
    return del({
        url: `gruul-mall-goods/goods/product/category/${id}`,
    })
}

/**
 * 获取供应商列表
 */
export const doGetSupList = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-goods/manager/supplier/list',
        params,
    })
}
/**
 * 删除供应商
 */
export const doDelSupplier = (ids: string) => {
    return del({
        url: `gruul-mall-goods/manager/supplier/delete/${ids}`,
    })
}
/**
 * 新增供应商
 */
export const doSaveSupplier = (data: any) => {
    return post({
        url: 'gruul-mall-goods/manager/supplier/save',
        data,
    })
}
/**
 * 修改供应商
 */
export const doUpdateSupplier = (data: any) => {
    return put({
        url: 'gruul-mall-goods/manager/supplier/update',
        data,
    })
}
/**
 * 审核供应商
 */
export const doVerifySupplier = (data: any) => {
    return put({
        url: 'gruul-mall-goods/manager/supplier/check',
        data,
    })
}
/**
 * 获取商品列表
 */
export const doGetCommodity = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-goods/manager/product/list',
        params,
    })
}

/**
 * 获取平台类目列表
 */
export const doGetPlatformCategory = (): Promise<any> => {
    return get({
        url: 'gruul-mall-addon-platform/platform/shop/signing/category/choosable/list',
    })
}

/**
 * 检查是分类否可用
 * @param currentLevel
 * @param records
 */
export function checkCategoryEnable(currentLevel: number, records: any[]) {
    const isLastLevel = currentLevel === 3
    for (let index = 0; index < records.length; ) {
        const record = records[index]
        if (isLastLevel) {
            record.disabled = false
            index++
            continue
        }
        const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
        delete record.secondCategoryVos
        delete record.categoryThirdlyVos
        const disable = !children || children.length === 0
        record.disabled = disable
        if (disable) {
            records.splice(index, 1)
            continue
        }
        checkCategoryEnable(currentLevel + 1, children)
        if (children.length === 0) {
            records.splice(index, 1)
            continue
        }
        record.children = children
        index++
    }

    return records
}

/**
 * 发布商品
 */
export const doReleaseGood = (data: any) => {
    return post({
        url: 'gruul-mall-goods/manager/product/issue',
        data,
    })
}
/**
 * 编辑商品
 */
export const doUpdateCommodity = (data: any) => {
    return put({
        url: 'gruul-mall-goods/manager/product/update',
        data,
    })
}
/**
 * 更新商品状态
 * @param {string} status
 */
export const doUpdateSellStatus = (keys: { shopId: string | number; productId: string }[], status: string) => {
    return put({
        url: `gruul-mall-goods/manager/product/updateStatus/${status}`,
        data: {
            keys,
        },
    })
}
/**
 * 删除商品
 */
export const doDeleteCommodity = (ids: any) => {
    return del({
        url: `gruul-mall-goods/manager/product/delete/${ids}`,
    })
}
/**
 * 获取单个商品信息
 */
export const doGetSingleCommodity = (id: any, shopId: Long): Promise<any> => {
    return get({
        url: `gruul-mall-goods/manager/product/get/${shopId}/${id}`,
    })
}
/**
 * 设置限购
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
 * 查询商品规格组与sku列表
 */
export const doGetCommoditySku = (shopId: Long, productId: any) => {
    return get<CommoditySaleType>({
        url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}`,
    })
}
/**
 * 查询商品库存与限购
 */
export const doGetProductPurchasing = (productId: Long): Promise<any> => {
    return get({
        url: `gruul-mall-storage/storage/product/${productId}`,
    })
}

/**
 * 查询一级分类和分类下的商品数量
 */
export const doGetHighestCategoryLevel = (params: any) => {
    return get<L<CategoryLevel1WithProductNum>>({
        url: `gruul-mall-goods/goods/product/category/categoryLevel1WithProductNum`,
        params,
    })
}

/**
 *获取品牌列表
 */
export const getBrandList = (data: any): Promise<any> => {
    return get({
        url: 'gruul-mall-search/search/brand/brandInfo',
        params: data,
    })
}
/*
 * 商品一键复制
 */
export const doGetCopyGoods = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-goods/api/copy/goods/detail`,
        params,
        timeout: 50000,
    })
}
/*
 * 产品 特性列表 I
 */
export const doGetfeatureList = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-goods/manager/feature/list`,
        params,
    })
}

/*
 * 产品 特性新增
 * @param {string} data
 */
export const dofeatureSave = (data: any) => {
    return post({
        url: `gruul-mall-goods/manager/feature/save`,
        data,
    })
}

/*
 * 产品 特性编辑
 * @param {string} data
 */
export const dofeatureUpdate = (data: any) => {
    return post({
        url: `gruul-mall-goods/manager/feature/update`,
        data,
    })
}

/*
 * 产品 特性编辑
 */

export const dofeatureDel = (featuresType: string, data: any) => {
    return put({
        url: `gruul-mall-goods/manager/feature/del?featuresType=${featuresType}`,
        data,
    })
}

/*
 * 商品名称修改
 */
export const doNameUpdate = ({ id, name }: { id: string; name: string }) => {
    return put({
        url: `gruul-mall-goods/manager/product/update/${id}`,
        data: {
            name,
        },
    })
}

/*
 * 更新商品价格
 */
export const doPriceUpdate = (data: any, productId: string) => {
    return post({
        url: `gruul-mall-storage/storage/product/${productId}/price`,
        data,
    })
}
/**
 * 库存中心 获取商品列表
 */
export const doGetInventoryCommodity = (params: any): Promise<any> => {
    return get({
        url: 'gruul-mall-goods/manager/product/getProductStockBaseInfo',
        params,
    })
}
/**
 * 获取商品详情接口
 * @param id 商品id号
 */
export const doGetCommodityDetails = (id: any) => {
    return get({ url: `gruul-mall-goods/manager/product/show/${id}` })
}

/**
 * 获取商品审核列表信息
 * @param params 参数信息
 */
export const doGetExamineGoodsList = (params: any = {}): Promise<any> => {
    return get({ url: 'gruul-mall-goods/manager/product/audit', params })
}
/**
 * 重新审核商品信息
 * @param id 商品ID号
 */
export const doPutReviewGoodsInfo = (id: string) => {
    return put({ url: `gruul-mall-goods/manager/product/audit/product/submit/${id}` })
}

/**
 * 根据店铺类型，查询商品标签
 * @param {any} shopType
 */
export const doGetLabelList = (shopType: any): Promise<any> => {
    return get({
        url: `gruul-mall-goods/goods/product/label/searchByShopType/${shopType}`,
        params: {
            size: 1000,
        },
    })
}
/**
 * @description 获取商品标签列表
 * @returns
 */
export const doGetProductLabel = () => {
    return get<ListOfProductTagsType[]>({
        url: 'gruul-mall-goods/goods/product/label/labels',
    })
}
/*
 * 更新商品标签
 */
export const updateProductLabel = (labelId: string, id: string) => {
    return put({
        url: `gruul-mall-goods/manager/product/updateProductLabel/${id}${labelId ? '/' + labelId : ''}`,
    })
}
/**
 * 获取同城配送信息
 */
export const getIntraCityInfo = () => {
    return post({
        url: 'addon-ic/ic/shop/config/get',
    })
}
/**
 * 店铺门店列表
 */
export const GetStoreList = (): Promise<any> => {
    return get({
        url: 'addon-shop-store/store/list',
    })
}
