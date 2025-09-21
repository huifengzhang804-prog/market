import { defineStore } from 'pinia'

const useGoodsInfoDispatcherStore = defineStore('goodsInfoDispatcherStore', {
  state: () => ({
    goodsSubscribers: new Set<(...args: any[]) => any>(),
  }),
  actions: {
    // 添加订阅者
    addSubscriber(subscriber: (...args: any[]) => any) {
      this.goodsSubscribers.add(subscriber)
    },
    // 移除订阅者
    removeSubscriber(subscriber: (...args: any[]) => any) {
      this.goodsSubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateData() {
      // 通知所有订阅者
      this.goodsSubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
  },
})

export default useGoodsInfoDispatcherStore
