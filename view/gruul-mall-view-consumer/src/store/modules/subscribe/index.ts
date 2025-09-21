import { defineStore } from 'pinia'
import $storage from '@/utils/storage'
import type { MessageSubscribeType } from '@/apis/message/model'

export const useSubscribeStore = defineStore('subscribeStore', {
  state: () => ({
    Subscribe: { ORDER_PAY: '' },
  }),
  actions: {
    SET_SUBSCRIBE_LIST(val: MessageSubscribeType) {
      this.Subscribe = val
      $storage.set('SUBSCRIBE_LIST', val, 30)
    },
    SEND_SUBSCRIBE_MESSAGE(subscribeList: (keyof MessageSubscribeType)[]) {
      const Subscribe = $storage.get('SUBSCRIBE_LIST') || this.Subscribe
      const tmplIds = subscribeList.map((key) => {
        return Subscribe[key]
      })

      if (!tmplIds.length) return Promise.resolve(false)
      return new Promise((resolve) => {
        uni.requestSubscribeMessage({
          tmplIds,
          success: () => resolve(true),
          fail: (err) => {
            resolve(false)
          },
        })
      })
    },
  },
  getters: {},
})
