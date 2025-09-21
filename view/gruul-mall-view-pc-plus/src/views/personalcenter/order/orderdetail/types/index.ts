import { ORDERSTATUS, PACKAGESTATUS, DeliverType } from '@/views/personalcenter/order/myorder/types'
import Decimal from 'decimal.js'
export type OrderRuleEnum = keyof typeof ORDERSTATUS
export type OrderRuleConfig = Record<'title' | 'text' | 'img' | 'bgColor', string>
/**
 * @description: 订单详情状态规则
 * @param {string} tradeClose 订单关闭
 */
export type OrderStatusRule = Record<OrderRuleEnum, OrderRuleConfig>
/**
 * @description: 第一个包裹的物流查询接口返回类型
 * @returns {*}
 */
export interface ApiLogistics01 {
  createTime: string
  deleted: boolean
  expressCompanyCode: string
  expressCompanyName: string
  expressNo: string
  id: string
  orderNo: string
  receiverAddress: string
  receiverMobile: string
  receiverName: string
  remark: string
  shopId: string
  status: keyof typeof PACKAGESTATUS
  type: keyof typeof DeliverType
  updateTime: string
  version: 0
  success: boolean
}

export type DiscountDataByType = {
  PLATFORM_COUPON: {
    name: string
    price: Decimal
  }
  SHOP_COUPON: {
    name: string
    price: Decimal
  }
  freightPrice: {
    name: string
    price: Decimal
  }
  MEMBER_DEDUCTION: {
    name: string
    price: Decimal
  }
  FULL_REDUCTION: { name: string; price: Decimal }
  CONSUMPTION_REBATE: { name: string; price: Decimal }
}

/**
 * 订单状态 超级版
 */
export interface OrderStatusPlus {
  //订单状态 描述
  desc: OrderStatusZh
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
 * 根据订单详情 获取订单状态
 */
export const getOrderDetailStatusPlus = (order: any): OrderStatusPlus => {
  if (!order) return { desc: '', isClosed: false }
  //未支付只需要检查总订单状态
  if (order.status !== 'PAID') {
    const statusPlus = OrderStatusPlusHandler[order.status]
    statusPlus.closeTime = order.updateTime
    return statusPlus
  }
  //已支付 订单已拆分显示 只需要取第一条店铺订单数据
  const shopOrder = order.shopOrders[0]
  if (shopOrder.status !== 'OK') {
    const statusPlus = ShopOrderStatusPlusHandler[shopOrder.status]
    statusPlus.closeTime = shopOrder.updateTime
    return statusPlus
  }
  const shopOrderItems = shopOrder.shopOrderItems
  const statusPlus: OrderStatusPlus = {
    desc: '已关闭',
    isClosed: true,
  }
  //检查是否全部关闭
  const deliverConfig = {
    deliverNum: 0,
    unDeliverNum: 0,
    okNum: 0,
    // 已评价
    evaluation: 0,
  }
  for (let shopOrderItem of shopOrderItems) {
    if (shopOrderItem.status === 'OK') {
      deliverConfig.okNum += 1
      deliverConfig.unDeliverNum += shopOrderItem.packageStatus === 'WAITING_FOR_DELIVER' ? 1 : 0
      deliverConfig.deliverNum += shopOrderItem.packageStatus === 'WAITING_FOR_RECEIVE' ? 1 : 0
      deliverConfig.evaluation += ['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes(shopOrderItem.packageStatus) ? 1 : 0
      // statusPlus.desc = packageStatus[shopOrderItem.packageStatus]
      // statusPlus.isClosed = false
      // return statusPlus
    }

    const currentCloseTime = statusPlus.closeTime
    const closeTime = shopOrderItem.updateTime
    if (!closeTime) {
      continue
    }
    statusPlus.closeTime = !currentCloseTime ? closeTime : closeTime > currentCloseTime ? closeTime : currentCloseTime
  }
  const { unDeliverNum, evaluation, deliverNum, okNum } = deliverConfig
  if (okNum) {
    const deliverConfigTotal = deliverNum + unDeliverNum
    statusPlus.isClosed = false
    statusPlus.desc =
      unDeliverNum === okNum
        ? OrderStatusZh.WAITING_FOR_DELIVER
        : deliverNum === okNum
        ? OrderStatusZh.WAITING_FOR_RECEIVE
        : deliverConfigTotal === okNum
        ? OrderStatusZh.WAITING_FOR_DELIVER_PART
        : evaluation === okNum
        ? OrderStatusZh.COMMENTED_COMPLETED
        : OrderStatusZh.WAITING_FOR_COMMENT
  }
  return statusPlus
}
/**
 * @description: 商铺订单状态
 * @param SYSTEM_CLOSED 系统关闭
 * @param BUYER_CLOSED  买家关闭订单
 * @param SELLER_CLOSED  卖家关闭订单
 */
export enum SHOPORDERSTATUS {
  OK,
  SYSTEM_CLOSED,
  BUYER_CLOSED,
  SELLER_CLOSED,
}

/**
 *     OK: '正常状态',
 *     SYSTEM_CLOSED: '系统关闭',
 *     BUYER_CLOSED: '买家关闭订单',
 *     SELLER_CLOSED: '卖家关闭订单',
 */
export const ShopOrderStatusPlusHandler: Record<keyof typeof SHOPORDERSTATUS, OrderStatusPlus> = {
  OK: {
    desc: '正常状态',
    isClosed: false,
  },
  SYSTEM_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  BUYER_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  SELLER_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
}
/**
 * UNPAID: '等待买家付款',
 * PAID: '已支付',
 * BUYER_CLOSED: '买家关闭订单',
 * SYSTEM_CLOSED: '系统关闭订单',
 * SELLER_CLOSED: '卖家关闭订单',
 */
export const OrderStatusPlusHandler: Record<keyof typeof ORDERSTATUS, OrderStatusPlus> = {
  UNPAID: {
    desc: '待支付',
    isClosed: false,
  },
  PAID: {
    desc: '待发货',
    isClosed: false,
  },
  BUYER_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  SYSTEM_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  SELLER_CLOSED: {
    desc: '已关闭',
    isClosed: true,
  },
  TEAMING: {
    desc: '拼团中',
    isClosed: false,
  },
  TEAM_FAIL: {
    desc: '拼团失败',
    isClosed: true,
  },
}
