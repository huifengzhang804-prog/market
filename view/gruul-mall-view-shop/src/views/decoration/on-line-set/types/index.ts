export enum FUNCTIONTYPE {
    TABBAR,
    PAGE,
    CLASSIFY_PAGE,
}

enum DECORATION_TYPE {
    WECHAT_MINI_APP,
    PC,
    OTHERS,
    H5,
}
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
export interface SubmitForm {
    id: string
    functionType: keyof typeof FUNCTIONTYPE
    properties: any
    isDef?: boolean
    pageName?: string
    platforms: DecorationType
}
export type DecorationType = keyof typeof DECORATION_TYPE | string
export type EndpointType = keyof typeof ENDPOINT_TYPE | string
