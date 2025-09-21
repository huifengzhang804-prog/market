import { defineStore } from 'pinia'
const useCollectionDispatcher = defineStore('collectionDispatcherStore', {
  state: () => ({
    commoditySubscribers: new Set<(...args: any[]) => any>(),
    shopSubscribers: new Set<(...args: any[]) => any>(),
  }),
  actions: {
    // 添加订阅者
    addCommoditySubscriber(subscriber: (...args: any[]) => any) {
      this.commoditySubscribers.add(subscriber)
    },
    // 移除订阅者
    removeCommoditySubscriber(subscriber: (...args: any[]) => any) {
      this.commoditySubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateCommodityData() {
      // 通知所有订阅者
      this.commoditySubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
    // 添加订阅者
    addShopSubscriber(subscriber: (...args: any[]) => any) {
      this.shopSubscribers.add(subscriber)
    },
    // 移除订阅者
    removeShopSubscriber(subscriber: (...args: any[]) => any) {
      this.shopSubscribers.delete(subscriber)
    },
    // 更新数据并通知订阅者
    updateShopData() {
      // 通知所有订阅者
      this.shopSubscribers.forEach((subscriber) => {
        subscriber()
      })
    },
  },
})

export default useCollectionDispatcher
