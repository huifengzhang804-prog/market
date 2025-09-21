import type { SubmitForm, PageType, DecorationType, EndpointType } from '@/views/decoration/platformDecoration/mobile/types'
interface DecorationStateType {
    isUsercenterCompontents: boolean
    userCenterType: string
    activeTab: number
    // 是否底部导航添加链接
    getFrom: string
    // 当前页面操作组件下标
    activeComIndex: number | null
    // 管理页面管理tab栏 customize:自定义 userCenter:用户中心 bussiness:营销
    activePageType: PageType
    // 复制链接
    copyLink: string
    // 存储当前操作自定义页面
    activePage: SubmitForm
    decorationType: DecorationType
    //以下我装修模板新增
    //终端类型
    endpointType: EndpointType
    //平台类型
    templateType: 'PLATFORM' | 'SHOP' | string
}
export const decorationState: DecorationStateType = {
    // 判断是否是用户中心
    isUsercenterCompontents: false,
    userCenterType: '',
    activeTab: 0,
    // 是否底部导航添加链接
    getFrom: '',
    // 当前页面操作组件下标
    activeComIndex: null,
    // 管理页面管理tab栏 customize:自定义 userCenter:用户中心 bussiness:营销
    activePageType: 'customize',
    // 复制链接
    copyLink: '',
    // 存储当前操作自定义页面
    activePage: {
        id: '',
        functionType: 'PAGE',
        platform: 'H5',
        properties: [],
    },
    //装修端对应枚举
    decorationType: '',
    //以下我装修模板新增
    //终端类型
    endpointType: '',
    templateType: '',
}
