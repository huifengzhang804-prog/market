import { defineStore } from 'pinia'
import storage from '@/utils/Storage'
import { SignResp, MineUserData } from '@apis/sign/index.type'

const storageExample = new storage()

const adminInfo_: ApiAdminInfo = {
    value: '',
    expireAt: '',
    expiresIn: 0,
    open: {
        shopId: -1,
        userId: 0,
    },
    refreshToken: {
        expireAt: '',
        expiresIn: 0,
        value: '',
    },
    shopId: '',
    userId: '',
    nickname: '',
    mobile: '',
    email: '',
}

interface ApiAdminInfo extends MineUserData, SignResp {}

export const useAdminInfo = defineStore('useAdminInfo', {
    actions: {
        SET_ADMIN_INFO(payload: SignResp) {
            this.adminInfo = { ...this.adminInfo, ...payload }
            storageExample.setItem('adminInfo', payload, Number(payload.refreshToken.expiresIn))
            return this.adminInfo.value
        },
        REMOVE_ADMIN_INFO() {
            this.adminInfo = adminInfo_
            storageExample.removeItem('adminInfo')
        },
        SET_CHANGE_PASSWORD_FLAG(payload: boolean) {
            this.changePasswordFlag = payload
        },
    },
    state: () => ({
        adminInfo: (storageExample.getItem('adminInfo') || adminInfo_) as ApiAdminInfo,
        changePasswordFlag: false,
    }),
    getters: {
        getterToken: (state) => state.adminInfo.value,
        getterAdminInfo: (state) => state.adminInfo,
        getterChangePasswordFlag: (state) => state.changePasswordFlag,
    },
})
