import type { ShopOrderMap } from './afsHooks'
import { IcStatus, type ApiOrder, type ShopOrderItem } from '@/pluginPackage/order/orderList/types'
import { OrderStatusZh } from '@/hooks/useOrderStatus'
import { Decimal } from 'decimal.js-light'

/**
 *  订单处理方法 将两个 productId skuId 一致的 num 合并
 * @param {ShopOrderItem} shopOrderItems
 */
const orderMapComputed = (shopOrderItems: ShopOrderItem[]) => {
  const shopOrderItemsMap = shopOrderItems.reduce((pre, item) => {
    const id = `${item.productId}${item.skuId}${item.specs}`
    const currentItem = pre.get(id)
    if (currentItem) {
      const current = { ...item }
      currentItem.merged = mergeItemsArr(currentItem.merged, current)
      currentItem.items.push(current)
    } else {
      pre.set(id, {
        items: [{ ...item }],
        merged: { ...item },
      })
    }
    return pre
  }, new Map<string, ShopOrderMap>())
  return shopOrderItemsMap
}

function mergeItemsArr(pre: ShopOrderItem, current: ShopOrderItem) {
  pre.num += current.num
  pre.fixPrice = new Decimal(pre.fixPrice).plus(current.fixPrice).toString()
  pre.freightPrice = new Decimal(pre.freightPrice).plus(current.freightPrice).toString()
  //当前是关闭状态 下一个是未关闭状态 则设置为正常状态
  const changeCloseStatus = pre.status === 'CLOSED' && current.status === 'OK'
  //判断是否设置为正常状态
  if (changeCloseStatus) {
    //部分关闭 说明有未关闭的 合并后的状态设置为正常
    pre.status = current.status
    pre.packageStatus = current.packageStatus
  }
  if (pre.afsStatus === 'NONE' && current.afsStatus !== 'NONE') {
    pre.afsStatus = current.afsStatus
  }
  //部分发货 设置为待发货状态
  if (!!pre.packageId && !current.packageId) {
    pre.packageId = current.packageId
  }
  if (pre.packageStatus !== 'WAITING_FOR_DELIVER' && current.packageStatus === 'WAITING_FOR_DELIVER') {
    pre.packageStatus = current.packageStatus
  }
  //如果

  pre.merged = true
  return pre
}

/**
 * 订单详情倒计时展示配置
 */
const countDownHandler = {
  UNPAID: (payload: ApiOrder) => {
    return {
      isShow: true,
      updateTime: payload?.createTime,
      payTimeOut: payload?.timeout?.payTimeout || '0',
      text: '自动关闭',
    }
  },
  [OrderStatusZh.WAITING_FOR_RECEIVE]: (payload: ApiOrder) => {
    return {
      isShow: true,
      updateTime: payload?.shopOrders[0].extra?.deliverTime,
      payTimeOut: payload?.timeout?.confirmTimeout || '0',
      text: '确认收货',
    }
  },
  [OrderStatusZh.WAITING_FOR_COMMENT]: (payload: ApiOrder) => {
    return {
      isShow: true,
      updateTime: payload?.shopOrders[0].extra?.receiveTime,
      payTimeOut: payload?.timeout?.commentTimeout || '0',
      text: '自动评价',
    }
  },
  DELIVERED: (payload: ApiOrder) => {
    if (payload?.shopOrderPackages?.[0]?.status === 'BUYER_WAITING_FOR_COMMENT') {
      // 已收货待评价
      return {
        isShow: true,
        updateTime: payload?.shopOrders[0].extra?.receiveTime,
        payTimeOut: payload?.timeout?.commentTimeout || '0',
        text: '自动评价',
      }
    }
    // 未收货
    return {
      isShow: true,
      updateTime: payload?.shopOrders[0].extra?.deliverTime,
      payTimeOut: payload?.timeout?.confirmTimeout || '0',
      text: '确认收货',
    }
  },
}
export { orderMapComputed, countDownHandler }
