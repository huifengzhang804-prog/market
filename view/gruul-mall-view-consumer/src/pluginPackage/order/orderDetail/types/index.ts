import type { PACKAGESTATUS } from '@/pluginPackage/order/orderList/types'
import type { DistributionTypeWithIc } from '@/apis/good/model'
import { DISCOUNTSOURCETYPE } from '@/pluginPackage/order/orderList/types'

/**
 * 第一个包裹的物流查询接口返回类型
 * @returns {*} id 核销码
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
  shopId: Long
  status: keyof typeof PACKAGESTATUS
  type: keyof typeof ApiLogistics01Type
  updateTime: string
  version: 0
  success: boolean
}

enum ApiLogistics01Type {
  /**
   * 无需物流发货
   */
  WITHOUT = 'WITHOUT',
  /**
   * 普通发货 自己填 物流公司与 单号
   */
  EXPRESS = 'EXPRESS',
  /**
   * 打印发货
   */
  PRINT_EXPRESS = 'PRINT_EXPRESS',
  /**
   * 同城 商家配送
   */
  IC_MERCHANT = 'IC_MERCHANT',
  /**
   * 同城 开放平台配送（如：UU跑腿）
   * 暂时只支持 uu跑腿
   */
  IC_OPEN = 'IC_OPEN',
}

interface DiscountTypeItem {
  //是否是店铺折扣
  isShopDiscount: boolean
  //折扣描述
  name: string
  //用于控制订单详情折扣展示排序
  sort: number
}

/**
 * 默认的折扣配置
 */
export const discountTypeConf: Record<keyof typeof DISCOUNTSOURCETYPE, DiscountTypeItem> = {
  PLATFORM_COUPON: {
    isShopDiscount: false,
    name: '平台优惠',
    sort: 1,
  },
  SHOP_COUPON: {
    isShopDiscount: true,
    name: '店铺优惠券',
    sort: 2,
  },
  MEMBER_DEDUCTION: {
    isShopDiscount: false,
    name: '会员折扣',
    sort: 3,
  },
  FULL_REDUCTION: {
    isShopDiscount: true,
    name: '满减',
    sort: 4,
  },
  CONSUMPTION_REBATE: {
    isShopDiscount: false,
    name: '消费返利',
    sort: 5,
  },
}

export interface OrderDiscountDetail extends DiscountTypeItem {
  //折扣价
  price: DecimalType
}
