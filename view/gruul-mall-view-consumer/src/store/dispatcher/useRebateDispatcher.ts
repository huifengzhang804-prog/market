import { defineStore } from 'pinia'

const useRebateDispatcher = defineStore('rebateDispatcherStore', {
  state: () => ({
    rebateSubscribers: new Set<(...args: any[]) => any>(),
  }),
  actions: {
    // 添加订阅者
    addSubscriber(subscriber: (...args: any[]) => any) {
      this.rebateSubscribers.add(subscriber)
    },
    // 移除订阅者
    removeSubscriber(subscriber: (...args: any[]) => any) {
      this.rebateSubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateData() {
      // 通知所有订阅者
      this.rebateSubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
  },
})

export default useRebateDispatcher
