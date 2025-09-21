enum ENDPOINT_TYPE {
    WECHAT_MINI_APP,
    H5_APP,
    PC_MALL,
}
export enum PAGES_TYPE {
    SHOP_HOME_PAGE = '店铺首页',
    SHOP_CATEGORY_PAGE = '店铺分类',
    SHOP_BOTTOM_NAVIGATION_PAGE = '底部导航',
    SHOP_CUSTOMIZED_PAGE = '自定义页面',
}
/**
 * 装修页面类型
 * @param customize 自定义页面
 * @param classification 分类
 * @param control 控件
 */
enum PAGETYPE {
    customize,
    classification,
    control,
    userCenter,
}
export type PageType = keyof typeof PAGETYPE

export type EndpointType = keyof typeof ENDPOINT_TYPE

/**
 * O2OPageType
 */
export interface Pages {
    pageId: string
    pageType: PageType
}

/**
 * 在线PageType
 */
export interface OnLinePages {
    pageId: string
    pageType: keyof typeof PAGES_TYPE
}

/**
 * 模板详情
 */
export interface PlatformTemplatesDetail<T = Pages> {
    businessType: string
    description: string
    enabled: true
    endpointType: EndpointType
    id: string
    name: string
    pages: T[]
    templateType: string
}

/**
 * 页面列表
 */
export interface TemplatePagesList {
    [key: string]: { id: string; name: string }[]
}
