import { defineStore } from 'pinia'

const useAddressDispatcher = defineStore('addressDispatcherStore', {
  state: () => ({
    addressSubscribers: new Set<(...args: any[]) => any>(),
  }),
  actions: {
    // 添加订阅者
    addCartSubscriber(subscriber: (...args: any[]) => any) {
      this.addressSubscribers.add(subscriber)
    },
    // 移除订阅者
    removeCartSubscriber(subscriber: (...args: any[]) => any) {
      this.addressSubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateCartData() {
      // 通知所有订阅者
      this.addressSubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
  },
})

export default useAddressDispatcher
