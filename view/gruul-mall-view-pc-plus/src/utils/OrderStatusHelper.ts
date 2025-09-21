//import { AFSSTATUS } from '@/basePackage/pages/applyAfterSales/types'
import {
  ShopOrderItem,
  ORDERSTATUS,
  SHOPORDERSTATUS,
  SHOPITEMSTATUS,
  ShopOrder,
  PACKAGESTATUS,
  AFSSTATUS,
  ApiOrder,
} from '@/views/personalcenter/order/myorder/types'
/* export const getStatus = (order: ApiOrder) => {
    const orderStatusHandler = orderStatusHandle[order.status]
    if (!orderStatusHandler.next) {
        return orderStatusHandler.desp
    }
    const shopOrders = order.shopOrders
} */

interface StatusHandle {
  desp: string
  next?: boolean
  toPaid?: boolean
  reason?: string
}

export const orderStatusHandle: Record<keyof typeof ORDERSTATUS, StatusHandle> = {
  UNPAID: {
    desp: '等待付款',
    toPaid: true,
  },
  PAID: {
    desp: '已支付',
    next: true,
  },
  BUYER_CLOSED: {
    desp: '已取消',
    reason: '用户关闭',
    next: true,
  },
  SYSTEM_CLOSED: {
    desp: '已取消',
    reason: '超时关闭',
    next: true,
  },
  SELLER_CLOSED: {
    desp: '已取消',
    reason: '商家关闭',
    next: true,
  },
  ACCOMPLISH: {
    desp: '已完成',
  },
  ON_DELIVERY: {
    desp: '待收货',
  },
}
export const isAllItemClosed = (items: Array<ShopOrderItem>): boolean => {
  return !items.find((item) => !shopOrderStatusHandle[item.status].close)
}

// export const getItemStatus = (item:ShopOrderItem)=>{
//   const itemHandler = shopOrderStatusHandle[item.status]
//   if(itemHandler.close){
//     return afsStatusHandle[item.afsStatus]
//   }
//   return packageStatusHandle[item.packageStatus]
// }

// export const afsStatusHandle: Record<keyof typeof AFSSTATUS, any> ={
//   NONE:{desp:""},
//   REFUND_REQUEST:{desp:""},
//   SYSTEM_REFUND_AGREE:{desp:""},
//   REFUND_AGREE:{desp:""},
//   REFUND_REJECT:{desp:""},
//   SYSTEM_RETURN_REFUND_AGREE:{desp:""},
//   REFUNDED:{desp:""},
//   SYSTEM_RETURNED_REFUND_CONFIRM:{desp:""},
//   RETURN_REFUND_REQUEST:{desp:""},
//   RETURN_REFUND_AGREE:{desp:""},
//   RETURN_REFUND_REJECT:{desp:""},
//   RETURNED_REFUND:{desp:""},
//   SYSTEM_CLOSED:{desp:""},
//   RETURNED_REFUND_CONFIRM:{desp:""},
//   RETURNED_REFUND_REJECT:{desp:""},
//   RETURNED_REFUNDED:{desp:""},
//   BUYER_CLOSED:{desp:""},
// }

export const packageStatusHandle: Record<keyof typeof PACKAGESTATUS, any> = {
  WAITING_FOR_DELIVER: { desp: '待发货', steps: 2 },
  WAITING_FOR_RECEIVE: { desp: '等待收货', color: '#71B247', track: true, Confirmreceipt: true, steps: 3 },
  BUYER_WAITING_FOR_COMMENT: { desp: '交易成功', color: '#ccc', track: true, evaluate: true, steps: 4 },
  SYSTEM_WAITING_FOR_COMMENT: { desp: '交易成功', color: '#ccc', track: true, evaluate: true, steps: 4 },
  BUYER_COMMENTED_COMPLETED: { desp: '交易成功', color: '#ccc', track: true, steps: 4 },
  SYSTEM_COMMENTED_COMPLETED: { desp: '交易成功', color: '#ccc', track: true, steps: 4 },
}
export const shopOrderStatusHandle: Record<keyof typeof SHOPITEMSTATUS, any> = {
  OK: {},
  CLOSED: { close: true },
}

export enum IcStatus {
  '' = '',
  /**
   * 运费价格计算 待发单（未发货）
   * 这种状态的单子可以随时丢弃
   */
  PRICE_PADDING = '待发货',
  /**
   * 待接单
   */
  PENDING = '待接单',
  /**
   * 已接单待到店
   */
  TAKEN = '待到店',
  /**
   * 已到店 待取货
   */
  ARRIVED_SHOP = '待取货',
  /**
   * 已取货 配送中
   */
  PICKUP = '配送中',
  /**
   * 已送达
   */
  DELIVERED = '已送达',
  /**
   * 异常 配送异常
   */
  ERROR = '配送异常',
}

/**
 * 订单状态 超级版
 */
export interface OrderStatusPlus {
  //订单状态 描述
  desc: string
  //是否已关闭
  isClosed: boolean
  //关闭时间
  closeTime?: string
}

export enum OrderStatusZh {
  WAITING_FOR_DELIVER = '待发货',
  WAITING_FOR_RECEIVE = '待收货',
  WAITING_FOR_DELIVER_PART = '待发货 (部分发货)',
  COMMENTED_COMPLETED = '已完成',
  WAITING_FOR_COMMENT = '待评价',
}

/**
 * 获取店铺订单的详细状态
 * @param shopOrder 店铺订单
 * @param order 主订单
 * @param showIc 是否显示同城配送状态
 * @returns 订单状态信息
 */
export function orderStatusPlus(shopOrder: ShopOrder, order: ApiOrder, showIc = true) {
  // 初始化状态为已关闭
  const statusPlus = {
    desc: '已关闭',
    isClosed: true, // 是否展示评价按钮
  }
  // 如果店铺订单状态不是OK，则返回已关闭状态
  if (shopOrder?.status !== 'OK') {
    return {
      ...statusPlus,
      closeTime: shopOrder?.updateTime,
    }
  }
  // 设置为未关闭状态
  statusPlus.isClosed = false
  // 处理同城配送订单
  if (order.icStatus !== void 0 && order.extra?.distributionMode === 'INTRA_CITY_DISTRIBUTION' && showIc) {
    return handleIntraCityOrder(shopOrder, order, statusPlus)
  }
  // 处理普通订单
  return handleNormalOrder(shopOrder, statusPlus)
}

/**
 * 处理同城配送订单状态
 */
function handleIntraCityOrder(shopOrder: ShopOrder, order: ApiOrder, statusPlus: OrderStatusPlus): OrderStatusPlus {
  // 设置状态描述为同城状态
  statusPlus.desc = IcStatus[order.icStatus!]
  const shopOrderItems = shopOrder.shopOrderItems || []
  // 处理配送异常状态
  if (order.icStatus === 'ERROR') {
    const firstItem = shopOrderItems[0]
    if (firstItem?.status === 'OK' && firstItem.packageStatus === 'WAITING_FOR_DELIVER') {
      statusPlus.desc = IcStatus['PRICE_PADDING'] // 异常转待发货
    }
    return statusPlus
  }
  // 处理已送达状态: 待收货 / 待评价 / 已完成
  if (order.icStatus === 'DELIVERED') {
    const packageStatuses = shopOrderItems.map((item) => item.packageStatus)
    // 按优先级判断状态
    if (packageStatuses.includes('WAITING_FOR_RECEIVE')) {
      statusPlus.desc = '待收货'
    }
    // 待评价状态
    if (packageStatuses.some((status) => ['BUYER_WAITING_FOR_COMMENT', 'SYSTEM_WAITING_FOR_COMMENT'].includes(status))) {
      statusPlus.desc = '待评价'
    }
    // 已完成状态
    if (packageStatuses.some((status) => ['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes(status))) {
      statusPlus.desc = '已完成'
      statusPlus.isClosed = true
    }
  }
  return statusPlus
}

/**
 * 处理普通订单状态
 */
function handleNormalOrder(shopOrder: ShopOrder, statusPlus: OrderStatusPlus): OrderStatusPlus {
  const shopOrderItems = shopOrder.shopOrderItems || []
  // 统计各种状态的数量
  const stats = {
    deliverNum: 0, // 待收货数量
    unDeliverNum: 0, // 待发货数量
    okNum: 0, // 正常状态数量
    evaluation: 0, // 已评价数量
  }
  // 计算最新的关闭时间
  let latestCloseTime = statusPlus.closeTime
  // 统计各状态数量
  for (const item of shopOrderItems) {
    if (item.status === 'OK') {
      stats.okNum++
      if (item.packageStatus === 'WAITING_FOR_DELIVER') {
        stats.unDeliverNum++
      }
      if (item.packageStatus === 'WAITING_FOR_RECEIVE') {
        stats.deliverNum++
      }
      if (['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes(item.packageStatus)) {
        stats.evaluation++
      }
    }
    // 更新关闭时间
    if (item.updateTime) {
      if (!latestCloseTime) {
        latestCloseTime = item.updateTime
      } else if (item.updateTime > latestCloseTime) {
        latestCloseTime = item.updateTime
      }
    }
  }
  statusPlus.closeTime = latestCloseTime
  // 如果没有正常状态的订单项，直接返回
  if (!stats.okNum) return statusPlus
  // 状态判断优先级：
  // 1. 全部待发货 > 2. 全部待收货 > 3. 部分发货 > 4. 全部已评价 > 5. 默认待评价
  if (stats.unDeliverNum === stats.okNum) {
    // 所有订单项均未发货
    statusPlus.desc = OrderStatusZh.WAITING_FOR_DELIVER
  } else if (stats.deliverNum === stats.okNum) {
    // 所有订单项均已发货待收货
    statusPlus.desc = OrderStatusZh.WAITING_FOR_RECEIVE
  } else if (stats.deliverNum + stats.unDeliverNum === stats.okNum) {
    // 存在混合发货状态 (部分已发/部分未发)
    statusPlus.desc = OrderStatusZh.WAITING_FOR_DELIVER_PART
  } else if (stats.evaluation === stats.okNum) {
    // 所有订单项已完成评价
    statusPlus.desc = OrderStatusZh.COMMENTED_COMPLETED
    statusPlus.isClosed = true
  } else {
    // 默认：存在待评价订单项
    statusPlus.desc = OrderStatusZh.WAITING_FOR_COMMENT
  }
  return statusPlus
}
