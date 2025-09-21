import type { ApiOrder, ShopOrder, ShopOrderItem } from '@/pluginPackage/order/orderList/types'
import { GeometryType } from '@/apis/address/type'

/**
 * 创建基础订单数据
 */
export function createBaseOrder(override: Partial<ApiOrder> = {}): ApiOrder {
  return {
    id: 'order-id',
    buyerId: 'buyer-id',
    no: 'order-no',
    createTime: '2025-01-01 00:00:00',
    updateTime: '2025-01-01 00:00:00',
    status: 'UNPAID',
    type: 'COMMON',
    remark: '',
    timeout: {
      commentTimeout: '',
      confirmTimeout: '',
      payTimeout: '',
      confirmTimeoutMills: '',
      payTimeoutMills: '',
      commentTimeoutMills: '',
    },
    orderPayment: {
      id: '',
      sn: '',
      createTime: '',
      shopId: 1,
      orderNo: '',
      payerId: '',
      type: 'WECHAT',
      totalAmount: '',
      freightAmount: '',
      discountAmount: '',
      payAmount: '',
      payTime: '',
    },
    orderDiscounts: [],
    shopOrders: [],
    orderReceiver: {
      id: '',
      name: '',
      mobile: '',
      address: '',
      isDefault: false,
      area: ['省', '市', '区'],
      location: {
        type: GeometryType.Point,
        coordinates: [0, 0],
      },
    },
    extra: {
      distributionMode: 'EXPRESS',
    },
    ...override,
  } as ApiOrder
}

/**
 * 创建店铺订单项
 */
export function createShopOrderItem(override: Partial<ShopOrderItem> = {}): ShopOrderItem {
  return {
    id: 'item-id',
    dealPrice: '100',
    fixPrice: '100',
    status: 'OK',
    afsStatus: 'NONE',
    freightPrice: '0',
    freightTemplateId: '',
    image: '',
    num: 1,
    productId: 1,
    productName: '测试商品',
    salePrice: 100,
    shopId: 1,
    skuId: 1,
    specs: [],
    weight: 0,
    packageStatus: 'WAITING_FOR_DELIVER',
    services: [],
    sellType: 'PURCHASE',
    ...override,
  }
}

/**
 * 创建店铺订单
 */
export function createShopOrder(override: Partial<ShopOrder> = {}): ShopOrder {
  return {
    id: 'shop-order-id',
    no: 'shop-order-no',
    status: 'OK',
    shopId: 1,
    orderNo: 'order-no',
    shopName: '测试店铺',
    shopLogo: '',
    remark: {
      orderNotify: true,
    },
    shopOrderItems: [],
    createTime: '2025-01-01 00:00:00',
    updateTime: '2025-01-01 00:00:00',
    more: false,
    extra: {
      deliverTime: '',
      receiveTime: '',
    },
    ...override,
  }
}
