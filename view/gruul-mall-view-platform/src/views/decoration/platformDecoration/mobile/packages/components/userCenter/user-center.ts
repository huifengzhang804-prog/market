import type { LinkSelectItem } from '@/components/link-select/linkSelectItem'
/**
 * 用户中心配置
 * @param {CustomStyleType} customStyle 头部自定义风格
 * @param {string} getCartText 领卡文案
 * @param {number} headStyle 头部风格 1系统风格 2自定义风格
 * @param {number} hideCartInlet 非会员显示领卡入口 0为隐藏 1为显示
 * @param {number} scanCode 扫码入口 0为隐藏 1为显示
 * @param {number} membershipCode 会员码入口 0为隐藏 1为显示
 * @param {number} menuStyle 菜单栏样式 1列表式 2位九宫格
 * @param {UserCenterMenuItem[]} menuVos 菜单栏列表
 */
export interface UserCenterType {
    customStyle: CustomStyleType
    getCartText: string
    headStyle: number
    id: string
    hideCartInlet: number
    scanCode: number
    membershipCode: number
    menuStyle: number
    menuList: UserCenterMenuItem[]
    menuScratchable: UserCenterMenuItem[]
    orderInfo: OrderItem[]
}
/**
 * 菜单栏类型
 * @param {number} allowUse 0不可用 1可用
 * @param {string} defaultIcon 默认图标地址
 * @param {boolean} hideMenu 菜单是否展示 0隐藏1显示
 * @param {string} menuIconUrl 菜单当前图标url
 * @param {string} menuName 菜单名称
 * @param {string} sortIndex 排序位置
 * @param {boolean} splitFlag 分隔
 */
export interface UserCenterMenuItem {
    id: string | null
    allowUse: number
    defaultIcon: string
    showMenu: boolean
    linkSelectItem: LinkSelectItem
    menuIconUrl: string
    menuName: string
    sortIndex: number
    splitFlag?: boolean
}
/**
 * 自定义头部类型
 * @param {string} backgroundImage 背景图片
 * @param {string} cardColor 卡面颜色
 * @param {string} textColor 文字颜色
 * @param {number} infoColor （昵称/储值/返利/收藏/足迹）颜色
 * @param {number} scanCode 扫码入口颜色
 * @param {number} membershipCode 会员码入口颜色
 * @param {number} activateNowColor 付费会员立即开通按钮文本颜色
 * @param {number} activateNowBtnColor 付费会员立即开通按钮颜色
 */
export interface CustomStyleType {
    backgroundImage: string
    cardColor: string
    textColor: string
    infoColor: string
    scanCodeColor: string
    membershipCodeColor: string
    activateNowColor: string
    activateNowBtnColor: string
}

enum OrderInfoKey {
    // 待付款
    unpay,
    // 待发货
    waitSend,
    // 待提货
    waitPick,
    // 售后
    afterSale,
    //评价
    waitRate,
}
export interface OrderItem {
    id: keyof typeof OrderInfoKey
    name: string
    url: string
    link: LinkSelectItem
    key: string
}
export default {
    /** 自定义风格样式,json存储 */
    customStyle: {
        backgroundImage: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220928/ccf24502a08642a8a9a4ed1aab622ebb.jpg',
        cardColor: '#CD7A27',
        textColor: '#F1C8C8',
    },
    getCartText: '',
    headStyle: 1,
    hideCartInlet: 0,
    id: '',
    menuStyle: 1,
    menuList: [
        {
            id: '1',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/d517511764824280840012b28e834fd7.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 2, // 类型 type 对应链接选择器的 index
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/d517511764824280840012b28e834fd7.png',
            menuName: '领券中心',
            sortIndex: 0,
            splitFlag: false,
        },
        {
            id: '2',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/1f471a7fab9c4a47ac714d6a6515992c.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/1f471a7fab9c4a47ac714d6a6515992c.png',
            menuName: '我的优惠券',
            sortIndex: 1,
            splitFlag: false,
        },
        {
            id: '3',
            allowUse: 1,
            defaultIcon: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/%E5%9B%BE%E6%A0%87/gouwuche.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 0,
            },
            menuIconUrl: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/%E5%9B%BE%E6%A0%87/gouwuche.png',
            menuName: '购物车',
            sortIndex: 2,
            splitFlag: false,
        },
        {
            id: '4',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/708afcdfc62443938c9f07ef5d07daf5.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/708afcdfc62443938c9f07ef5d07daf5.png',
            menuName: '地址管理',
            sortIndex: 3,
            splitFlag: false,
        },
        {
            id: '5',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/80c68ba092174f3ea2067adf8944dbb6.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/80c68ba092174f3ea2067adf8944dbb6.png',
            menuName: '设置',
            sortIndex: 4,
            splitFlag: false,
        },
    ],
    menuScratchable: [
        {
            id: '1',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/d517511764824280840012b28e834fd7.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '领券中心',
                url: '/pluginPackage/coupon/couponCenter/CouponsCenter',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/d517511764824280840012b28e834fd7.png',
            menuName: '领券中心',
            sortIndex: 0,
            splitFlag: false,
        },
        {
            id: '2',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/1f471a7fab9c4a47ac714d6a6515992c.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '我的优惠券',
                url: '/pluginPackage/coupon/myCoupon/MyCoupon',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/1f471a7fab9c4a47ac714d6a6515992c.png',
            menuName: '我的优惠券',
            sortIndex: 1,
            splitFlag: false,
        },
        {
            id: '3',
            allowUse: 1,
            defaultIcon: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/%E5%9B%BE%E6%A0%87/gouwuche.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '购物车',
                url: '/',
                type: 0,
            },
            menuIconUrl: 'http://medusa-small-file.oss-cn-hangzhou.aliyuncs.com/%E5%9B%BE%E6%A0%87/gouwuche.png',
            menuName: '购物车',
            sortIndex: 2,
            splitFlag: false,
        },
        {
            id: '4',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/708afcdfc62443938c9f07ef5d07daf5.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/708afcdfc62443938c9f07ef5d07daf5.png',
            menuName: '地址管理',
            sortIndex: 3,
            splitFlag: false,
        },
        {
            id: '5',
            allowUse: 1,
            defaultIcon: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/80c68ba092174f3ea2067adf8944dbb6.png',
            showMenu: true,
            linkSelectItem: {
                id: '',
                name: '',
                url: '',
                type: 0,
            },
            menuIconUrl: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20200418/80c68ba092174f3ea2067adf8944dbb6.png',
            menuName: '设置',
            sortIndex: 4,
            splitFlag: false,
        },
    ],
    orderInfo: [
        {
            id: 'unpay',
            name: '待付款',
            url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211221/b160c674136f4d9985b5df6aab4b9d66.png',
            link: {
                id: '',
                name: '',
                url: '/pluginPackage/order/orderList/orderList?id=1',
                type: 0,
            },
            key: 'unpaid',
        },
        {
            id: 'waitSend',
            name: '待发货',
            url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/file-read-12014.png',
            link: {
                id: '',
                name: '',
                url: '/pluginPackage/order/orderList/orderList?id=2',
                type: 0,
            },
            key: 'undelivered',
        },
        {
            id: 'waitPick',
            name: '待收货',
            url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211221/3dd39e45222546d585f0fed7aa83dd82.png',
            link: {
                id: '',
                name: '',
                url: '/pluginPackage/order/orderList/orderList?id=3',
                type: 0,
            },
            key: 'unreceived',
        },
        {
            id: 'waitRate',
            name: '待评价',
            url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220122/c636d13705014f6d87e8370e864976c6.png',
            link: {
                id: '',
                name: '',
                url: '/pluginPackage/order/orderList/orderList?id=5',
                type: 0,
            },
            key: 'unrate',
        },
        {
            id: 'afterSale',
            name: '售后',
            url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211221/331a0e38aa2945cc9517ca9a820ad64f.png',
            link: {
                id: '',
                name: '',
                url: '/pluginPackage/order/afterSales/AfterSales',
                type: 0,
            },
            key: 'unhandledAfs',
        },
    ],
} as UserCenterType
