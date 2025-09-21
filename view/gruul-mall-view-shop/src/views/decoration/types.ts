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
    platform: string
}
export type DecorationType = keyof typeof DECORATION_TYPE | string
export type EndpointType = keyof typeof ENDPOINT_TYPE | string
