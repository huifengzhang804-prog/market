import { describe, expect, test } from 'vitest'
import { getOrderDetailStatusPlus, orderStatusPlus } from '@/hooks/useOrderStatus.ts'
import testData from './data'

/**
 * 已关闭
 */
describe('已关闭', () => {
  test('未支付', () => {
    testData.closed.unpaidClosed.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '已关闭',
        isClosed: true,
        closeTime: data.updateTime,
      })
    })
  })

  test('支付', () => {
    testData.closed.paidClosed.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '已关闭',
        isClosed: true,
        closeTime: data?.shopOrders?.[0]?.updateTime,
      })
    })
  })

  test('拼团失败', () => {
    testData.closed.TeamFailClosed.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '拼团失败',
        isClosed: true,
        closeTime: data?.updateTime,
      })
    })
  })

  test('售后关闭', () => {
    testData.closed.afterSalesClosed.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '已关闭',
        isClosed: true,
        closeTime: data?.shopOrders?.[0]?.updateTime,
      })
    })
  })

  // 待评价申请售后关闭
  test('待评价申请售后关闭', () => {
    testData.closed.commentAfterSaleClosed.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '已关闭',
        isClosed: true,
        closeTime: data?.shopOrders?.[0]?.updateTime,
      })
    })
  })
})

/**
 * 未付款
 */
describe('未付款', () => {
  test('待支付', () => {
    testData.unpaid.unpaid.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待支付',
        isClosed: false,
      })
    })
  })

  // 拼团待支付
  test('拼团待支付', () => {
    testData.unpaid.teamUnpaid.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待支付',
        isClosed: false,
      })
    })
  })
})

/**
 * 待发货
 */
describe('待发货', () => {
  test('全部待发货', () => {
    testData.unshipped.allUnshipped.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待发货',
        isClosed: false,
      })
    })
  })

  test('部分待发货', () => {
    testData.unshipped.partUnshipped.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待发货 (部分发货)',
        isClosed: false,
      })
    })
  })

  test('同城待发货', () => {
    testData.unshipped.localUnshipped.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待发货',
        isClosed: false,
      })
    })
  })

  // 分批售后成功后待发货
  test('分批售后成功后待发货', () => {
    testData.unshipped.afterSaleUnshipped.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待发货',
        isClosed: false,
      })
    })
  })
})

/**
 * 待收货
 */
describe('待收货', () => {
  test('普通订单待收货', () => {
    testData.waitDelivery.waitDelivery.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待收货',
        isClosed: false,
      })
    })
  })

  // 补充: 到店自提待收货
  test('到店自提待收货', () => {
    testData.waitDelivery.selfPickupWaitDelivery.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待收货',
        isClosed: false,
      })
    })
  })

  describe('同城订单待收货', () => {
    test('同城待接单', () => {
      testData.waitDelivery.localWaitDelivery.waitingForAccept.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '待接单',
          isClosed: false,
        })
      })
    })

    test('同城待到店', () => {
      testData.waitDelivery.localWaitDelivery.waitingForArrival.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '待到店',
          isClosed: false,
        })
      })
    })

    test('同城待取货', () => {
      testData.waitDelivery.localWaitDelivery.waitingForPickup.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '待取货',
          isClosed: false,
        })
      })
    })

    test('同城配送中', () => {
      testData.waitDelivery.localWaitDelivery.delivering.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '配送中',
          isClosed: false,
        })
      })
    })

    test('同城已送达', () => {
      testData.waitDelivery.localWaitDelivery.delivered.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '待收货',
          isClosed: false,
        })
      })
    })

    test('同城配送异常', () => {
      testData.waitDelivery.localWaitDelivery.error.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '配送异常',
          isClosed: false,
        })
      })
    })
    test('同城配送配送异常之后处理成待发货', () => {
      testData.waitDelivery.localWaitDelivery.errorToUnshipped.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '待发货',
          isClosed: false,
        })
      })
    })
    test('配送异常之后处理成待收货已送达', () => {
      testData.waitDelivery.localWaitDelivery.errorToDelivered.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '待收货',
          isClosed: false,
        })
      })
    })
    test('配送异常之后处理成已关闭(全额退款)', () => {
      testData.waitDelivery.localWaitDelivery.errorToClosed.forEach((data) => {
        expect(orderStatusPlus(data.shopOrders[0], data)).toEqual({
          desc: '已关闭',
          isClosed: true,
          closeTime: data.shopOrders[0].updateTime,
        })
      })
    })
  })
})

/**
 * 待评价
 */
describe('待评价', () => {
  test('买家待评价', () => {
    testData.waitComment.buyerWaitComment.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待评价',
        isClosed: false,
      })
    })
  })

  test('系统待评价', () => {
    testData.waitComment.systemWaitComment.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '待评价',
        isClosed: false,
      })
    })
  })
})

/**
 * 已完成
 */
describe('已完成', () => {
  test('买家已评价完成', () => {
    testData.completed.buyerCommented.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '已完成',
        isClosed: true,
      })
    })
  })

  test('系统自动好评完成', () => {
    testData.completed.systemCommented.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '已完成',
        isClosed: true,
      })
    })
  })
})

/**
 * 拼团中
 */
describe('拼团中', () => {
  test('拼团中状态', () => {
    testData.teaming.teaming.forEach((data) => {
      expect(getOrderDetailStatusPlus(data)).toEqual({
        desc: '拼团中',
        isClosed: false,
      })
    })
  })
})
