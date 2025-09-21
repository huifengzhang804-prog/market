import type { OrderType } from '@/views/settlement/types'
import { ApiOrderCouponVO, ProductType } from '@/types/coupon/index'
// import { useConversionPrice } from '@/hooks'
import { Decimal } from 'decimal.js'
import useConvert from '@/composables/useConvert'
const { mulTenThousand } = useConvert()

export interface CostCalculate {
  //商品总价
  totalAmount: string
  //店铺优惠
  shopDiscount: string
  //平台优惠
  platformDiscount: string
  // 总运费
  totalFreight: string
  //需要支付的费用
  payAmount: string
}

export const commodityPrice = (
  shopProducts: OrderType[],
  allCoupons: Map<string, ApiOrderCouponVO>,
  freightInfo: { [x: string]: string } | undefined,
  fullReductions: Map<string, ApiOrderCouponVO>,
): CostCalculate => {
  const result = {
    totalAmount: '0',
    shopDiscount: '0',
    platformDiscount: '0',
    totalFreight: '0',
    payAmount: '0',
  }
  if (!shopProducts || shopProducts.length === 0) {
    return result
  }
  return getCalculatedPrice(shopProducts, !allCoupons ? new Map() : allCoupons, freightInfo, fullReductions)
}

const getCalculatedPrice = (
  shopProducts: OrderType[],
  allCoupons: Map<string, ApiOrderCouponVO>,
  freightInfo: { [x: string]: string } | undefined,
  fullReductions: Map<string, ApiOrderCouponVO>,
): CostCalculate => {
  //所有商品的总价
  let totalAmount = new Decimal(0)
  //所有店铺的折扣价
  const shopDiscountMap = new Map<string, Decimal>()
  const productFreightMap = new Map<string, Decimal>()
  shopProducts.forEach((shop) => {
    const shopId = shop.shopId
    //所有商品的总价
    const productAmountMap = new Map<string, Decimal>()
    shop.products.forEach((product) => {
      const shopFreightKey = `${shopId}:${product.freightTemplateId}`
      const productId = product.productId
      const salePrice = new Decimal(product.salePrice).mul(product.num)
      const currentAmount = productAmountMap?.get(productId)
      if (freightInfo) {
        productFreightMap?.set(shopFreightKey, new Decimal(freightInfo?.[shopFreightKey] || 0))
      }
      productAmountMap?.set(productId, !currentAmount ? new Decimal(salePrice) : currentAmount.add(salePrice))
      totalAmount = totalAmount.add(salePrice)
    })
    const coupon = allCoupons.get(shopId)
    const full = fullReductions.get(shopId)
    if (coupon?.id && full?.id) {
      shopDiscountMap.set(
        shopId,
        !coupon?.id || !full?.id ? new Decimal(0) : getShopDiscount(productAmountMap, coupon).add(getShopDiscount(productAmountMap, full)),
      )
    } else if (coupon?.id) {
      shopDiscountMap.set(shopId, !coupon?.id ? new Decimal(0) : getShopDiscount(productAmountMap, coupon))
    } else {
      shopDiscountMap.set(shopId, !full?.id ? new Decimal(0) : getShopDiscount(productAmountMap, full))
    }
  })
  const shopDiscount = Array.from(shopDiscountMap.values()).reduce((pre, cur) => pre.add(cur))
  const platformDiscountDecimal = allCoupons.get('0')?.discountAmount
  const platformDiscount = !platformDiscountDecimal
    ? new Decimal('0')
    : totalAmount.lessThan(platformDiscountDecimal)
    ? totalAmount
    : platformDiscountDecimal
  const totalFreight = productFreightMap.size ? Array.from(productFreightMap.values()).reduce((pre, cur) => pre.add(cur)) : new Decimal(0)
  const payAmount = totalAmount.lessThan(shopDiscount.add(platformDiscount)) ? new Decimal(0) : totalAmount.sub(shopDiscount).sub(platformDiscount)
  return {
    totalAmount: totalAmount.toString(),
    shopDiscount: shopDiscount.toString(),
    platformDiscount: platformDiscount.toString(),
    totalFreight: totalFreight.toString(),
    payAmount: payAmount.toNumber() < 0 ? mulTenThousand(totalFreight).toString() : payAmount.add(mulTenThousand(totalFreight)).toString(),
  }
}
/**
 * @description: 策略模式
 * @param {array} productAmount
 * @param {string} productIds
 * @returns {*}
 */
const couponHandler: Record<keyof typeof ProductType, (productAmount: [string, Decimal], productIds: string[]) => boolean> = {
  ALL: () => true,
  SHOP_ALL: () => true,
  ALL_PRODUCT: () => true,
  ASSIGNED: (productAmount, productIds) => productIds.includes(productAmount[0]),
  ASSIGNED_NOT: (productAmount, productIds) => !productIds.includes(productAmount[0]),
}

const getShopDiscount = (productAmountMap: Map<string, Decimal>, coupon: ApiOrderCouponVO): Decimal => {
  const activeProductAmount = Array.from(productAmountMap.entries())
    .filter((productAmount) => couponHandler[coupon.productType](productAmount, coupon.productIds))
    .map((productAmount) => productAmount[1])
    .reduce((pre, cur) => pre.add(cur))
  return activeProductAmount.lessThan(coupon.discountAmount) ? activeProductAmount : new Decimal(coupon.discountAmount)
}
