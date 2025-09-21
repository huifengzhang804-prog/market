import { defineStore } from 'pinia'
const useOrderDispatcherStore = defineStore('orderDispatcherStore', {
  state: () => ({
    orderSubscribers: new Set<(...args: any[]) => any>(),
  }),
  actions: {
    // 添加订阅者
    addSubscriber(subscriber: (...args: any[]) => any) {
      this.orderSubscribers.add(subscriber)
    },
    // 移除订阅者
    removeSubscriber(subscriber: (...args: any[]) => any) {
      this.orderSubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateData() {
      // 通知所有订阅者
      this.orderSubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
  },
})

export default useOrderDispatcherStore
