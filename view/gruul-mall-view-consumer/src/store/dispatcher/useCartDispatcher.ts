import { defineStore } from 'pinia'

const useCartDispatcher = defineStore('cartDispatcherStore', {
  state: () => ({
    cartSubscribers: new Set<(...args: any[]) => any>(),
  }),
  actions: {
    // 添加订阅者
    addCartSubscriber(subscriber: (...args: any[]) => any) {
      this.cartSubscribers.add(subscriber)
    },
    // 移除订阅者
    removeCartSubscriber(subscriber: (...args: any[]) => any) {
      this.cartSubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateCartData() {
      // 通知所有订阅者
      this.cartSubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
  },
})

export default useCartDispatcher
