// 消息store
import { defineStore } from 'pinia'
import type { SystemMessage } from '@/hooks/stomp/typs'

/**
 * 未读消息统计
 */
export const useMsgCountStore = defineStore({
  id: 'msgCountStore',
  state() {
    return {
      count: '0',
    }
  },
  actions: {
    setCount(count: string) {
      this.count = count
    },
  },
  getters: {
    getCount: (state) => state.count,
  },
})

/**
 * 用于监听客服消息
 */
export const useCustomerServiceStore = defineStore({
  id: 'customerServiceStore',
  state() {
    return {
      value: {} as SystemMessage,
    }
  },
  actions: {
    setMsg(msg: SystemMessage) {
      this.value = msg
    },
  },
  getters: {
    getMsg: (state) => state.value,
  },
})
