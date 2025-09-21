import type { ApiCouponVO, CouponJointType, ApiOrderCouponVO, CartApiCouponVO } from '@/apis/plugin/coupon/model'
import type { GoodListType } from '@/pages/modules/car/types'
import type { OrderProductType } from '@/pluginPackage/order/confirmOrder/types'
import { Decimal } from 'decimal.js-light'
const { divTenThousand } = useConvert()
export const couponIndexType = {
  PRICE_DISCOUNT: '无门槛折扣券',
  PRICE_REDUCE: '无门槛现金券',
  REQUIRED_PRICE_DISCOUNT: '满折券',
  REQUIRED_PRICE_REDUCE: '满减券',
} as const
/**
 * 优惠券风格配置
 */
export const couponStyleConfig = {
  PRICE_DISCOUNT: {
    backgroundColor: '#FCEFE6',
    color: '#F07F2A',
    border: '#FFE5D1',
  },
  PRICE_REDUCE: {
    backgroundColor: '#FCECED',
    color: '#F12F22',
    border: '#FFD8D1',
  },
  REQUIRED_PRICE_DISCOUNT: {
    backgroundColor: '#F0FFF2',
    color: '#70BA6B',
    border: '#D1FFDB',
  },
  REQUIRED_PRICE_REDUCE: {
    backgroundColor: '#E9FBFF',
    color: '#5EB9CF',
    border: '#D1FFDB',
  },
} as const
/**
 * @param ALL 全部商品
 * @param SHOP_ALL 店铺全部商品
 * @param ASSIGNED 指定商品
 * @param ASSIGNED_NOT 指定商品不生效
 */
export const productTypeCn = (params: ApiCouponVO) => {
  if (params.productType === 'ALL') {
    return '全场商品通用'
  }
  const chinesObj = {
    ALL: '全场商品通用',
    SHOP_ALL: '全店商品可用',
    ASSIGNED: '部分商品可用',
    ASSIGNED_NOT: '部分商品可用',
  }
  return chinesObj[params.productType]
}
/**
 * true折扣 false 满减
 * @param {CouponJointType} status
 * @returns {*} boolean
 */
export const isDiscountFn = (status: CouponJointType) => {
  return ['PRICE_DISCOUNT', 'REQUIRED_PRICE_DISCOUNT'].includes(status)
}
/**
 * 简易版 showToast
 * @param {string} title
 */
export const showToast = (title: string) => {
  uni.showToast({
    title,
    icon: 'none',
  })
}
export const claimedStatus = {
  /**
   * 可以领取
   */
  CLAIM: { isUseCoupon: true, text: '立即领券' },
  /**
   * 领取达上限
   */
  LIMIT: {
    isUseCoupon: false,
    text: '领取达上限',
  },
  /**
   * 已领完
   */
  FINISHED: {
    isUseCoupon: false,
    text: '已领完',
  },
}

/**
 * 领券按钮配置
 */
export const couponQueryStatusCn = {
  UNUSED: '去使用',
  UNCLAIMED: '立即领券',
  USED: '已使用',
  EXPIRED: '已过期',
}

/**
 * 查询店铺优惠券
 * @param {OrderType} shopProducts
 */
export const queryStoreCoupon = (shopProducts: {
  products: {
    productId: Long
    salePrice: Long
    num: number
  }[]
}) => {
  const map = new Map<string, Decimal>()
  shopProducts.products.forEach((item) => {
    const currentPrice = map.get(item.productId as string)
    const currentTotalPrice = new Decimal(item.salePrice).mul(new Decimal(item.num))
    map.set(item.productId as string, currentPrice ? currentPrice.add(currentTotalPrice) : currentTotalPrice)
  })
  let arr = []
  for (const iterator of map) {
    arr.push({ productId: iterator[0], amount: iterator[1].toString() })
  }
  return arr
}
/**
 * 优惠券有效期格式化
 * @param {ApiCouponVO} item
 */
export function formattingTime(item: ApiCouponVO) {
  if (item.effectType === 'IMMEDIATELY' && item.days) {
    return item.days <= 1 ? '领券当天内可用' : `领券当日起${item.days}天内可用`
  }
  return `${formatTime(item.startDate)}-${formatTime(item.endDate)}`
}
function formatTime(time: string) {
  return time ? time.replace(/[-]/g, '.') : ''
}
/**
 * 无门槛格式化
 * @param {ApiCouponVO} item
 */
export function formattingPrice(item: ApiCouponVO) {
  const isD = isDiscountFn(item.type)
  if (!isD) {
    return divTenThousand(item.amount).toString()
  }
  return item.discount
}
/**
 *  优惠券规则格式化
 */
export const formattingCouponRules = (cou: ApiOrderCouponVO | CartApiCouponVO | undefined, autocomplete = true) => {
  let text
  if (!cou || !Object.values(cou).length) {
    text = ''
    return text
  }
  switch (cou.type) {
    case 'PRICE_REDUCE':
      text = autocomplete ? `无门槛现金券减${cou.amount && divTenThousand(cou.amount)}元` : `无门槛现金券`
      break
    case 'PRICE_DISCOUNT':
      text = autocomplete ? `无门槛折扣券打${cou.discount}折` : `无门槛折扣券`
      break
    case 'REQUIRED_PRICE_REDUCE':
      text = autocomplete
        ? `满${divTenThousand(cou.requiredAmount)}元减${cou.amount && divTenThousand(cou.amount)}元`
        : `满${divTenThousand(cou.requiredAmount)}元可用`
      break
    case 'REQUIRED_PRICE_DISCOUNT':
      text = autocomplete ? `满${divTenThousand(cou.requiredAmount)}元打${cou.discount}折` : `满${divTenThousand(cou.requiredAmount)}元可用`
      break
    default:
      break
  }
  return text
}
