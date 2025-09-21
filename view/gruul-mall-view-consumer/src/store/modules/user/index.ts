import { defineStore } from 'pinia'
import { useAppStore } from '@/store/modules/app'
import state from './state'
import storage from '@/utils/storage'
import { doGetRoleMenu } from '@/apis/consumer/footprint'
import type { UserInfoType, UserStateType, GenderType } from './state'
import { doGetUserData } from '@/apis/afs'

const avatarDefault = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564e7de4b0dd23f6b86f2f.png'
const discodeKey = `discode`
state.discode = storage.get(discodeKey)

export const useUserStore = defineStore('userStore', {
  state: () => state,
  actions: {
    ADD_USER_INFO(val: UserInfoType['info']) {
      this.userInfo.info = val
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo = val
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo.token = this.userInfo.token
      storage.set('userStore', this.userInfo)
      return val
    },
    PUT_DISTRIBUTOR_CODE(code: string) {
      this.userInfo.info.distributorCode = code
      storage.set('userStore', this.userInfo, 30)
    },
    PUT_USER_INFO(avatar: string, nickname: string, gender: GenderType, birthday: string) {
      this.userInfo.info.avatar = avatar
      this.userInfo.info.nickname = nickname
      this.userInfo.info.gender = gender
      this.userInfo.info.birthday = birthday
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo = this.userInfo.info
      storage.set('userStore', this.userInfo, 30)
    },
    ADD_TOKEN(val: { access_token: string; refresh_token: string }) {
      this.userInfo.token = val.access_token
      this.userInfo.refresh_token = val.refresh_token
      storage.set('userStore', this.userInfo, 30)
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo.token = val.access_token
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo.refresh_token = val.refresh_token

      if (!val.access_token) {
        this.ADD_USER_INFO({ nickname: '', avatar: avatarDefault, gender: 'UNKNOWN', userId: null, birthday: '', distributorCode: '' })
        return ''
      }
      doGetUserData().then(({ code, data, msg }) => {
        if (code !== 200) {
          this.ADD_USER_INFO({ nickname: '', avatar: avatarDefault, gender: 'UNKNOWN', userId: null, birthday: '', distributorCode: '' })
          return
        }
        this.ADD_USER_INFO(data)
      })
      this.GET_ROLE_MENU()
      return val.access_token
    },
    DEL_TOKEN() {
      // 删除用户和会员信息
      this.userInfo = {
        info: { avatar: '', gender: 'UNKNOWN', nickname: '', userId: '', birthday: '', distributorCode: '' },
        token: '',
        refresh_token: '',
      }
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo = this.userInfo.info
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo.token = ''
      // 兼容 nvue
      // @ts-ignore

      getApp().globalData.userInfo.refresh_token = ''
      this.member = {
        memberType: '',
        memberBenefit: null,
        memberName: null,
        memberCardValidTime: null,
        rankCode: null,
      }
      storage.remove('userStore')
      storage.remove('memberInfo')
    },
    async GET_ROLE_MENU() {
      if (!this.userInfo.token) return
      const { code, data } = await doGetRoleMenu()
      if (code !== 200) {
        return
      }
      useAppStore().SET_ROLE_MENUS(data)
    },
    SET_MEMBER_INFO(val: UserStateType['member']) {
      this.member = val
      storage.set('memberInfo', val)
    },
    SET_DIS_CODE(val: string) {
      this.discode = val
      storage.set(discodeKey, val)
    },
    DEL_DIS_CODE() {
      this.discode = ''
      storage.remove(discodeKey)
    },
  },

  getters: {
    getterUserInfo: (state) => {
      return getUserInfoFn(state)
    },
    getterToken: (state) => state.userInfo.token,
    getterMemberInfo: (state): UserStateType['member'] => {
      if (!state.member) {
        return storage.get('memberInfo')
      } else {
        return state.member
      }
    },
  },
})
function getUserInfoFn(state: UserStateType) {
  if (judgeNotEmpty(state.userInfo)) {
    const userInfo = storage.get('userStore')
    if (judgeNotEmpty(userInfo)) {
      return {
        info: { avatar: '', gender: '', nickname: '', userId: '' },
        token: '',
        refresh_token: '',
      }
    } else {
      state.userInfo = userInfo
      return userInfo
    }
  } else {
    return state.userInfo
  }
}
/**
 * 判断shopInfo是否为空
 * @param {*} obj
 */
function judgeNotEmpty(userInfo: UserInfoType) {
  if (JSON.stringify(userInfo) === '{}' || JSON.stringify(userInfo) === 'null') return true
  if (Object.keys(userInfo).length === 0) return true
  if (!userInfo.token) return true
  return false
}
