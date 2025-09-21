import { defineStore } from 'pinia'
import Storage from '@/utils/Storage'
import defaultShopInfo from './state'
import type { ShopInfoStore } from './state'

const localStorageExample = new Storage()
export const useShopInfoStore = defineStore('shopStore', {
    state: () => ({
        shopInfo: defaultShopInfo,
        changePasswordFlag: false,
    }),
    actions: {
        SET_SHOP_INFO(payload: ShopInfoStore) {
            // this.shopInfo = { ...this.shopInfo, ...payload }
            Object.assign(this.shopInfo, payload)
            localStorageExample.setItem('shopStore', this.shopInfo, 60 * 60 * 24)
        },
        DEL_SHOP_INFO() {
            this.shopInfo = defaultShopInfo
            localStorageExample.removeItem('shopStore')
        },
        SET_SHOP_TOKEN(payload: { access_token: string; refresh_token: string }) {
            this.shopInfo.token = payload.access_token
            this.shopInfo.refresh_token = payload.refresh_token
            localStorageExample.setItem('shopStore', this.shopInfo, 60 * 60 * 24 * 30)
            return payload.access_token
        },
        SET_SHOP_ADMIN_DATA(payload: any) {
            Object.assign(this.shopInfo, payload)
            localStorageExample.setItem('shopStore', this.shopInfo, 60 * 60 * 24 * 30)
        },
        SET_CHANGE_PASSWORD_FLAG(flag: boolean) {
            this.changePasswordFlag = flag
        },
    },
    getters: {
        getterShopInfo: (state) => {
            return getShopInfoFn(state)
        },
        token: (state) => state.shopInfo.token,
        refresh_token: (state) => state.shopInfo.refresh_token,
        getterChangePasswordFlag: (state) => state.changePasswordFlag,
    },
})

function getShopInfoFn(state: { shopInfo: ShopInfoStore }): ShopInfoStore {
    if (judgeNotEmpty(state.shopInfo)) {
        const localShopInfo = localStorageExample.getItem('shopStore')
        if (judgeNotEmpty(localShopInfo)) {
            return { ...defaultShopInfo }
        } else {
            state.shopInfo = { ...localShopInfo }
            return { ...localShopInfo }
        }
    } else {
        return { ...state.shopInfo }
    }
}

/**
 * 判断shopInfo是否为空
 * @param {*} obj
 */
function judgeNotEmpty(shopInfo: any) {
    if (JSON.stringify(shopInfo) === '{}' || JSON.stringify(shopInfo) === 'null') return true
    if (Object.keys(shopInfo).length === 0) return true
    if (!shopInfo.id) return true
    return false
}
