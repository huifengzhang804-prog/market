import { LinkSelectItem } from '@/components/link-select/linkSelectItem'
import { get, post, put, del } from '../http'
import {
    SeckillRoundPageDTO,
    SeckillRoundProductPageDTO,
    SeckillRoundProductVO,
    SeckillRoundVO,
    ShopListParams,
    AdvertisementFormType,
    HomeBulletFrameFormType,
    PlatformPageVO,
    TemplateType,
} from './type'
import { L } from '../http.type'
import { ClassItemType } from '@/components/q-select-goods/type'
import { ApiCategoryData } from '@/views/decoration/platformDecoration/mobile/packages/components/classification/classification'

/**
 * 获取聚合首页名称
 */
export const doGetEnumOfDecoration = (list: string[]) => {
    return get({
        url: `gruul-mall-addon-platform/platform/decoration/aggregation/decorate?aggregationPlatforms=${list.join(',')}`,
    })
}

/**
 * 获取平台一级类目和对应商品数量
 */
export const doGetPlatformLevelAndComNum = (params: any) => {
    return get<L<ClassItemType>>({
        url: 'gruul-mall-addon-platform/platform/category/platformCategoryFirstIdWithProductNum',
        params,
    })
}

/**
 * 装修分类页查询分类
 * @param {string} ids
 * @param {string} categoryLevel
 */
export const doGetCategoryLevel = (ids: string[], categoryLevel: string) => {
    return get<ApiCategoryData[]>({
        url: '/gruul-mall-addon-platform/platform/category/by/ids',
        params: { ids: ids.join(','), categoryLevel },
    })
}
/**
 * 根据二级分类id查询商品
 */
export const doGetCommodityBySecCateId = (ids: string, categoryLevel: string): Promise<any> => {
    return get({
        url: '/gruul-mall-addon-platform/platform/category/by/platformCategoryId',
        params: {
            ids,
            categoryLevel,
        },
    })
}

/**
 * 分页获取平台装修模板
 */
export const doGetPlatformTemplates = (params: any) => {
    return get<L<TemplateType>>({
        url: '/gruul-mall-addon-platform/templates',
        params,
    })
}
/**
 * 获取平台装修模板详情
 */
export const doGetPlatformTemplatesDetail = (id: string): Promise<any> => {
    return get({
        url: `/gruul-mall-addon-platform/templates/${id}`,
    })
}
/**
 * 创建平台装修模板
 */
export const doPostPlatformTemplates = (data: any) => {
    return post({
        url: 'gruul-mall-addon-platform/templates',
        data,
    })
}
/**
 * 删除模板
 */
export const doDelPlatformTemplates = (id: string) => {
    return del({
        url: `gruul-mall-addon-platform/templates/${id}`,
    })
}
/**
 * 复制模板
 */
export const doPostPlatformTemplatesClone = (id: string) => {
    return post({
        url: `gruul-mall-addon-platform/templates/clone`,
        data: { templateId: id },
    })
}
/**
 * 编辑模板
 */
export const doPutPlatformTemplates = (data: any) => {
    return put({
        url: `gruul-mall-addon-platform/templates`,
        data,
    })
}
/**
 * 修改模板启用状态
 */
export const doPutPlatformTemplatesState = (data: any) => {
    return put({
        url: `gruul-mall-addon-platform/templates/default-template`,
        data,
    })
}
/**
 * 获取全部平台装修页面列表
 */
export const doGetPlatformTemplatesPages = (params: any): Promise<any> => {
    return get({
        url: `gruul-mall-addon-platform/templates/pages`,
        params,
    })
}

/**
 * 获取平台装修页面详情
 */
export const doGetPlatformPagesDetail = (id: string): Promise<any> => {
    return get({
        url: `/gruul-mall-addon-platform/pages/${id}`,
    })
}
/**
 * 分页获取平台装修页面
 */
export const doGetPlatformPages = (params: any) => {
    return get<L<PlatformPageVO>>({
        showLoading: false,
        url: `gruul-mall-addon-platform/pages`,
        params,
    })
}
/**
 * 删除页面
 */
export const doDelPlatformPages = (id: string) => {
    return del({
        url: `gruul-mall-addon-platform/pages/${id}`,
    })
}
/**
 * 复制页面
 */
export const doPostPlatformPagesClone = (id: string) => {
    return post({
        url: `gruul-mall-addon-platform/pages/clone/${id}`,
    })
}
/**
 * 保存页面
 */
export const doPostPlatformPagesSave = (data: any) => {
    return post({
        showLoading: false,
        url: `gruul-mall-addon-platform/pages`,
        data,
    })
}
/**
 * 编辑页面
 */
export const doPutPlatformPages = (data: any) => {
    return put({
        url: `gruul-mall-addon-platform/pages`,
        data,
    })
}

/**
 * 查询PC装修页面数据
 */
export const doGetOpeningUp = (pageType = 'RECOMMENDED_MALL_HOME_PAGE') => {
    return get<PlatformPageVO>({
        url: `gruul-mall-addon-platform/pages/opening-up/PC_MALL/${pageType}`,
    })
}

/**
 * 分页查询秒杀场次信息
 * @param data 分页查询条件
 */
export const doGetSeckillRounds = (data: SeckillRoundPageDTO) => {
    return post<Pagination<SeckillRoundVO>>({
        url: 'addon-seckill/seckill/activity/round/page',
        data,
        showLoading: false,
    })
}

/**
 * 根据场次开始时间 分页查询商品列表
 */
export const doGetSeckillProductsOfRound = (data: SeckillRoundProductPageDTO) => {
    return post<Pagination<SeckillRoundProductVO>>({
        url: 'addon-seckill/seckill/activity/round/product/page',
        data,
        showLoading: false,
    })
}

// gruul-mall-shop/decoration-pages

/**
 * 获取店铺
 */
export const doGetShopList = (data: ShopListParams): Promise<any> => {
    return post({ url: `gruul-mall-shop/shop/info/search/shop`, data })
}

/**
 * 根据终端查询广告信息
 */
export const doGetOpenAdvertisement = (params: any) => {
    return get<AdvertisementFormType>({
        url: `gruul-mall-addon-platform/splash/query/${params}`,
    })
}

/**
 * 新增或更新开屏广告
 */
export const upDateOpenAdvertisement = (data: AdvertisementFormType) => {
    delete data.displayTime
    if (data.skipSecond === '') {
        delete data.skipSecond
    }
    if (data.times === '') {
        delete data.times
    }
    return post({ url: `gruul-mall-addon-platform/splash/saveOrUpdate`, data })
}

/**
 * 根据终端查询弹框信息
 */
export const QueryByEndPoint = (params: any) => {
    return get<HomeBulletFrameFormType>({
        url: `gruul-mall-addon-platform/home/page/win/queryByEndPoint/${params}`,
    })
}

/**
 * 新增或更新首页弹框
 */
export const saveOrUpdate = (data: HomeBulletFrameFormType) => {
    delete data.displayTime
    return post({ url: `gruul-mall-addon-platform/home/page/win/saveOrUpdate`, data })
}
