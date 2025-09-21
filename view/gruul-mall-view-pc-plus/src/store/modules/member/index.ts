import { defineStore } from 'pinia'
import { UserStateType } from '../user/state'
import Storage from '@/libs/storage'

const storage = new Storage()
const useMemberStore = defineStore('memberStore', {
  state: () => ({
    member: null as any,
  }),
  actions: {
    SET_MEMBER_INFO(val: UserStateType['member']) {
      this.member = val
      storage.setItem('memberInfo', val)
    },
  },
  getters: {
    getterMemberInfo: (state): UserStateType['member'] => {
      if (!state.member) {
        const memberInfo = storage.getItem('memberInfo')
        if (memberInfo) {
          return JSON.parse(memberInfo)
        }
        return null
      } else {
        return state.member
      }
    },
  },
})

export default useMemberStore
