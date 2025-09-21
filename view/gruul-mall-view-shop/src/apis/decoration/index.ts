import { get, post, put, del } from '../http'
import { FUNCTIONTYPE } from '@/views/decoration/on-line-set/types'
import type { RetrieveParam } from '@/apis/good/model'
import { L } from '@/utils/types'
import { DecorationPage } from './types'

/**
 * 保存装修控件和页面
 */
export const doSubmitControl = (data: any) => {
    return post({
        url: 'gruul-mall-shop/decoration/edit',
        data,
    })
}
/**
 * 获取自定义页面列表
 */
export const doGetCustomPageList = (type: string) => {
    return get({
        url: `gruul-mall-shop/decoration/page/list?aggregationPlatform=${type}`,
    })
}

/**
 * 修改装修自定义页面名称
 * @param {string} name
 * @param {string} componentId
 */
export const doChangeCutomPageName = (name: string, componentId: string) => {
    return put({
        url: `gruul-mall-shop/decoration/page/update/${name}?id=${componentId}`,
    })
}
/**
 * 设置装修自定义页面为首页展示
 */
export const doSetDefault = (componentId: string, platforms: string) => {
    return put({
        url: `gruul-mall-shop/decoration/set/home/page/${componentId}?aggregationPlatform=${platforms}`,
    })
}
/**
 * 删除自定义页面
 */
export const doDelCustomPage = (componentId: string) => {
    return del({
        url: `gruul-mall-shop/decoration/del/${componentId}`,
        headers: { Platform: 'H5' },
    })
}

/**
 * 商品检索
 */
export const doGetRetrieveCommodity = (data: Partial<RetrieveParam>): Promise<any> => {
    return post({
        url: 'gruul-mall-search/search/product',
        data,
    })
}
/**
 * 代销商品
 * @param params
 * @returns
 */
export const getSupplierProductList = <T>(params?: any) => {
    return get<L<T>>({
        url: 'addon-supplier/supplier/manager/product/getSupplyList',
        params: { ...params },
    })
}

/**
 * 获取非页面装修数据
 */
export const doGetNotPageInfo = (platform: string, functionType: keyof typeof FUNCTIONTYPE) => {
    return get({
        url: `gruul-mall-shop/decoration/not/page/info?aggregationPlatform=${platform}&functionType=${functionType}`,
    })
}
/**
 * 装修分类页查询分类
 * @param {string} ids
 * @param {string} categoryLevel
 */
export const doGetCategoryLevel = (ids: string[], categoryLevel: string) => {
    return get({
        url: '/gruul-mall-goods/goods/product/category/by/ids',
        params: { ids: ids.join(','), categoryLevel },
    })
}
/**
 * 根据二级分类id查询商品
 */
export const doGetCommodityBySecCateId = (ids: string, categoryLevel: string) => {
    return get({
        url: '/gruul-mall-goods/api/product/by/categoryId',
        params: {
            ids,
            categoryLevel,
        },
    })
}

/**
 * 分页获取店铺装修模板
 */
export const doGetShopTemplates = (params: any) => {
    return get({
        url: 'gruul-mall-shop/decoration-templates',
        params,
    })
}
/**
 * 获取店铺装修模板详情
 */
export const doGetShopTemplatesDetail = (id: string): Promise<any> => {
    return get({
        url: `gruul-mall-shop/decoration-templates/${id}`,
    })
}
/**
 * 创建店铺装修模板
 */
export const doPostShopTemplates = (data: any) => {
    return post({
        url: 'gruul-mall-shop/decoration-templates',
        data,
    })
}
/**
 * 删除模板
 */
export const doDelShopTemplates = (id: string) => {
    return del({
        url: `gruul-mall-shop/decoration-templates`,
        data: { templateId: id },
    })
}
/**
 * 复制模板
 */
export const doPostShopTemplatesClone = (id: string) => {
    return post({
        url: `gruul-mall-shop/decoration-templates/clone`,
        data: { templateId: id },
    })
}
/**
 * 编辑模板
 */
export const doPutShopTemplates = (data: any) => {
    return put({
        url: `gruul-mall-shop/decoration-templates`,
        data,
    })
}
/**
 * 修改模板启用状态
 */
export const doPutShopTemplatesState = (data: any) => {
    return put({
        url: `gruul-mall-shop/decoration-templates/default-template`,
        data,
    })
}
/**
 * 获取全部店铺装修页面列表
 */
export const doGetShopTemplatesPages = (params: any) => {
    return get({
        url: `gruul-mall-shop/decoration-templates/pages`,
        params,
    })
}

/**
 * 获取店铺装修页面详情
 */
export const doGetShopPagesDetail = (id: string) => {
    return get({
        url: `gruul-mall-shop/decoration-pages/${id}`,
    })
}
/**
 * 分页获取店铺装修页面
 */
export const doGetShopPages = (params: any) => {
    return get<L<DecorationPage>>({
        url: `gruul-mall-shop/decoration-pages`,
        params,
    })
}
/**
 * 删除页面
 */
export const doDelShopPages = (id: string) => {
    return del({
        url: `gruul-mall-shop/decoration-pages`,
        data: {
            id,
        },
    })
}
/**
 * 复制页面
 */
export const doPostShopPagesClone = (id: string) => {
    return post({
        url: `gruul-mall-shop/decoration-pages/clone`,
        data: {
            id,
        },
    })
}
/**
 * 保存页面
 */
export const doPostShopPagesSave = (data: any) => {
    return post({
        url: `gruul-mall-shop/decoration-pages`,
        data,
    })
}
/**
 * 编辑页面
 */
export const doPutShopPages = (data: any) => {
    return put({
        url: `gruul-mall-shop/decoration-pages`,
        data,
    })
}

/**
 * 分页查询平台装修模板
 */
export const doGetPlatformTemplates = (params: { templateType: string; businessType: string; endpointType: string }) => {
    return get({
        url: `gruul-mall-addon-platform/templates`,
        params: { ...params, current: 1, size: 100 },
    })
}

/**
 * 克隆平台装修模板
 */
export const doPostClonePlatformTemplate = (data: { id: string; business: string; endpoint: string }) => {
    return post({
        url: `gruul-mall-shop/decoration-templates/clone/platform`,
        data,
    })
}

/**
 * 查询平台装修页面
 */
export const doGetPlatformPages = (params: { type: string; templateType: string; businessType: string; endpointType: string }) => {
    return get({
        url: `gruul-mall-addon-platform/pages`,
        params: { ...params, current: 1, size: 100 },
    })
}

/**
 * 克隆平台装修页面
 */
export const doPostClonePlatformPage = (data: { id: string; pageType: string; business: string; endpoint: string }) => {
    return post({
        url: `gruul-mall-shop/decoration-pages/clone/platform`,
        data,
    })
}

/**
 * 查询PC 平台端 装修页面数据
 */
export const doGetOpeningUp = (pageType = 'RECOMMENDED_MALL_HOME_PAGE') => {
    return get({
        url: `gruul-mall-addon-platform/pages/opening-up/PC_MALL/${pageType}`,
    })
}

/**
 * 查询PC 商家端 装修页面数据
 */
export const doGetShopOpeningUp = (shopId: string, pageType = 'SHOP_HOME_PAGE') => {
    return get({
        url: `gruul-mall-shop/decoration-pages/opening-up/${shopId}/PC_MALL/${pageType}`,
    })
}

/**
 * 查询秒杀场次
 */
export const doGetSecondsKill = (params = { shopId: '' }) => {
    return get({ url: `addon-seckill/seckillPromotion/consumer/sessions`, params })
}

/**
 * 获取平台一级类目和对应商品数量
 */
export const doGetPlatformLevelAndComNum = (params: any) => {
    return get({
        url: 'gruul-mall-addon-platform/platform/category/platformCategoryFirstIdWithProductNum',
        params,
    })
}
