import { defineStore } from 'pinia'
import type { ApiOrder } from '@/pluginPackage/order/orderList/types'

export const useOrderMore = defineStore({
  id: 'useOrderMore',
  state() {
    return {
      moreMap: new Map(),
    }
  },
  actions: {
    setMoreMap(order: ApiOrder) {
      if (order.status !== 'PAID') {
        // 未成功支付  订单未支付组件依靠唯一 order.no 控制更多按钮
        this.moreMap.set(order.no, false)
        return
      }
      // 已支付订单 拆分店铺 依靠订单号 + shopid 控制更多按钮
      order.shopOrders.forEach((item) => {
        this.moreMap.set(order.no + item.shopId, false)
      })
    },
    updateMoreMap(key: string) {
      if (this.checkStatus()) return
      this.moreMap.set(key, true)
    },
    checkStatus() {
      let flag = false
      for (const [k, v] of this.moreMap) {
        if (v) {
          // 如果存在开启更多按钮 将其关闭
          this.moreMap.set(k, false)
          flag = true
        }
      }
      return flag
    },
  },
  getters: {
    getMoreMap: (state) => state.moreMap,
  },
})
