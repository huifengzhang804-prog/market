import { defineStore } from 'pinia'
import state, { UserStateType } from './state'
import storage from '@/libs/storage'
// import type { ApiUserData } from '@/hooks'

export interface ApiUserData {
  avatar: string
  createTime: string
  deleted: false
  gender: string
  id: string
  nickname: string
  updateTime: string
  userId: string
}
export const useUserStore = defineStore('userStore', {
  state: () => state,
  actions: {
    addUserInfo(val: ApiUserData) {
      this.userInfo.info = val
      new storage().setItem('CLIENT-userStoreInfo', this.userInfo.info, 60 * 60 * 24 * 30)
    },
    addToken(val: string, expiresIn: number) {
      this.userInfo.token = val
      new storage().setItem('CLIENT-userStore', this.userInfo.token, expiresIn)
    },
    addRefreshToken(refreshToken: string, expiresIn: number) {
      this.userInfo.refreshToken = refreshToken
      new storage().setItem('CLIENT-userStoreRefreshToken', this.userInfo.refreshToken, expiresIn)
    },
    delToken() {
      this.userInfo.token = ''
      this.userInfo.refreshToken = ''
      this.delUserInfo()
      new storage().removeItem('CLIENT-userStore')
      new storage().removeItem('CLIENT-userStoreRefreshToken')
    },
    delUserInfo() {
      this.userInfo.info = { avatar: '', createTime: '', deleted: false, gender: '', id: '', nickname: '', updateTime: '', userId: '' }
      new storage().removeItem('CLIENT-userStoreInfo')
    },
    loginTypeTrue() {
      this.userInfo.loginType = true
    },
    loginTypeFalse() {
      this.userInfo.loginType = false
    },
  },
  getters: {
    getterUserInfo: (state) => {
      return getUserInfoFn(state.userInfo)
    },
    getterToken: (state) => state.userInfo.token,
  },
})

function getUserInfoFn(userInfo: any) {
  if (judgeNotEmpty(userInfo.info)) {
    const localuserInfo = new storage().getItem('CLIENT-userStoreInfo')
    if (judgeNotEmpty(localuserInfo)) {
      return state.userInfo.info
    } else {
      userInfo.info = localuserInfo
      return localuserInfo
    }
  } else {
    return userInfo.info
  }
}

/**
 * 判断userInfo是否为空
 * @param {*} obj
 */
function judgeNotEmpty(userInfo: any) {
  if (JSON.stringify(userInfo) === '{}' || JSON.stringify(userInfo) === 'null') return true
  if (Object.keys(userInfo).length === 0) return true
  if (!userInfo.id) return true
  return false
}
