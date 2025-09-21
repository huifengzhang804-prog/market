export enum FUNCTIONTYPE {
    TABBAR,
    CLASSIFY_PAGE,
    PERSONAL_CENTER,
    PAGE,
}
enum DECORATION_TYPE {
    WECHAT_MINI_APP,
    PC,
    OTHERS,
}

enum ENDPOINT_TYPE {
    WECHAT_MINI_APP,
    H5_APP,
    PC_MALL,
}
export enum PAGES_TYPE {
    RECOMMENDED_MALL_HOME_PAGE = '商城首页(推荐)',
    SAME_CITY_MALL_HOME_PAGE = '商城首页(同城)',
    PRODUCT_CATEGORY_PAGE = '商品分类',
    BOTTOM_NAVIGATION_PAGE = '底部导航',
    PERSONAL_CENTER_PAGE = '个人中心',
    CUSTOMIZED_PAGE = '自定义页面',
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
    isLocal?: boolean
    pageName?: string
    platforms?: DecorationType
    platform?: DecorationType
}
export type DecorationType = keyof typeof DECORATION_TYPE | string
export type EndpointType = keyof typeof ENDPOINT_TYPE | string
/**
 * 检索商品接口返回
 */
export interface ApiRetrieveComItemType {
    createTime: string
    id: string
    initSalesVolume: number
    pic: string
    platformCategoryFirstId: string
    platformCategorySecondId: string
    platformCategoryThirdId: string
    productId: string
    productName: string
    salePrices: string[]
    salesVolume: string
    shopId: string
    shopName: string
    specs: string
    status: 'SELL_ON' | 'SELL_OFF'
}

export interface ApiRetrieveTemplateList {
    id: string
    name: string
    description: string
    enabled: boolean
    pages: {
        pageId: string
        pageType: keyof typeof PAGES_TYPE
    }[]
    templateType: string
    endpointType: string
}
// 弹窗广告
export interface AdvertisementFormType {
    endPoint?: string
    endTime: string
    showFlag: boolean
    showFrequency: string
    skipSecond: string
    skipWay: string
    startTime: string
    times: string
    displayTime?: any
    imageList: ImageListType[]
    showTime?: number
}

export interface ImageListType {
    url: string
    showTime: number
    link?: {
        id: string
        type: number
        name: string
        url: string
        append: string
    }
}

export interface HomeBulletFrameFormType {
    showFlag: boolean //是否展示
    startTime: string //展示开始时间
    endTime: string //展示结束时间
    showTime?: string //展示时长
    endPoint: string //终端
    displayTime?: any[]
    imageInfo: {
        url: string
        showTime: string
        link: any
    }
    url?: string
}

export interface LinkSelectItem {
    id: string
    type: Long
    name: string
    url: string
    append: string
}
