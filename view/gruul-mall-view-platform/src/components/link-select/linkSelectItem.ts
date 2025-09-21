/**
 * 链接选择器
 * @param {number | string} id
 * @param {number | string} type 链接类型 0 -功能页面 1-商超商品 Goods 2活动营销 3自定义页面
 * @param {string} name 链接名称
 * @param {string} url 链接地址
 * @param {string} append 附加参数
 */
export interface LinkSelectItem {
    id: string
    type: string | number
    name: string
    url: string
    append?: string
    shopId?: string
}
type TypeNameMap = {
    [key: number]: {
        text: string
        name: CurrentComponent
    }
}

export type CurrentComponent = 'FunctionPage' | 'Goods' | 'classification' | 'ActivityMarket' | 'CustomPage' | 'FunctionPage'

export const typeNameMap: TypeNameMap = {
    0: {
        text: '功能页面',
        name: 'FunctionPage',
    },
    1: {
        text: '商品',
        name: 'Goods',
    },
    2: {
        text: '活动营销',
        name: 'ActivityMarket',
    },
    3: {
        text: '自定义页面',
        name: 'CustomPage',
    },
}
// id为字符串适配C端swiper item-id为string
// 此处默认数据勿改 改动需要与C端平台选择组件id对应
export const navBarDefaultData = [
    {
        id: '1',
        type: 0,
        name: '首页',
        url: '/',
        append: 0,
    },
    {
        id: '2',
        type: 0,
        name: '分类',
        url: '/',
        append: 1,
    },
    {
        id: '3',
        type: 0,
        name: '购物车',
        url: '/',
        append: 2,
    },
    {
        id: '4',
        type: 0,
        name: '个人中心',
        url: '/',
        append: 3,
    },
]
