import { defineStore } from 'pinia'

interface SaveOrUpdateData {
    COPYRIGHT_INFO: string // 版权信息
    COPYRIGHT_URL: string // 版权链接地址
    LOGIN_LOGO: string // 登录页logo
    PLATFORM_LOGIN_PAGE_BG: string // 平台登录页背景
    PLATFORM_LOGO: string // 平台logo
    PLATFORM_NAME: string // 平台名称
    PLATFORM_WEB_SIT_NAME: string // 平台网站名称
    RECORDER_INFO: string // 备案信息
    RECORDER_URL: string // 备案链接地址
    SHOP_LOGIN_PAGE_BG: string // 商家登录页背景图
    SHOP_WEB_SIT_NAME: string // 店铺网站名称
    SUPPLIER_LOGIN_PAGE_BG: string // 供应商登录页背景图
    SUPPLIER_WEB_SIT_NAME: string // 供应商网站名称
    WEB_SIT_ICON: string // 网站图标
}

export const configurePlatform = defineStore('configurePlatform', {
    state: () => ({
        // 网站设置
        websiteForm: {
            PLATFORM_NAME: '启山智软', // 公共设置、平台名称
            COPYRIGHT_INFO: '', // 版权信息
            COPYRIGHT_URL: '', // 版权信息、链接地址
            RECORDER_INFO: '', // 备案信息
            RECORDER_URL: '', // 备案信息、链接地址
            WEB_SIT_ICON: '', // 网站图标
            LOGIN_LOGO: '', // 登录logo
            PLATFORM_WEB_SIT_NAME: '', // 平台端、网站名称
            PLATFORM_LOGIN_PAGE_BG: '', // 平台端、背景图
            PLATFORM_LOGO: '', // 平台logo
            SHOP_WEB_SIT_NAME: '', // 商家端、网站名称
            SHOP_LOGIN_PAGE_BG: '', // 商家端、背景图
            SUPPLIER_WEB_SIT_NAME: '', // 供应商端、网站名称
            SUPPLIER_LOGIN_PAGE_BG: '', // 供应商端、背景图
        },
    }),
    actions: {
        // 设置网站设置
        SET_OPEN_ADV(websiteForm: SaveOrUpdateData) {
            this.websiteForm = websiteForm
        },
    },
    getters: {
        // 获取平台名称
        getPlatformName(state): string {
            return state.websiteForm.PLATFORM_NAME
        },
        // 获取版权信息
        getCopyrightInfo(state): string {
            return state.websiteForm.COPYRIGHT_INFO
        },
        // 获取版权信息链接地址
        getCopyrightUrl(state): string {
            return state.websiteForm.COPYRIGHT_URL
        },
        // 获取备案信息
        getRecorderInfo(state): string {
            return state.websiteForm.RECORDER_INFO
        },
        // 获取备案信息链接地址
        getRecorderUrl(state): string {
            return state.websiteForm.RECORDER_URL
        },
        // 网站图标
        getWebSitIcon(state): string {
            return state.websiteForm.WEB_SIT_ICON
        },
        // 登录logo
        getLoginLogo(state): string {
            return state.websiteForm.LOGIN_LOGO
        },
        // 获取网站名称
        getPlatformWebSitName(state): string {
            return state.websiteForm.PLATFORM_WEB_SIT_NAME
        },
        // 获取平台的登录页
        getPlatformLoginPageBg(state): string {
            return state.websiteForm.PLATFORM_LOGIN_PAGE_BG
        },
        // 获取平台log
        getPlatformLogo(state): string {
            return state.websiteForm.PLATFORM_LOGO
        },
    },
})
