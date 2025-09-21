import { Decimal } from 'decimal.js'
import { OrderDiscount, ShopOrder } from '../../myorder/types'
import { DiscountDataByType } from '../types'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()
const useCalculateDiscount = () => {
  const discountDataByType = reactive({
    PLATFORM_COUPON: {
      name: '平台优惠',
      price: new Decimal(0),
    },
    SHOP_COUPON: {
      name: '店铺优惠',
      price: new Decimal(0),
    },
    freightPrice: {
      name: '运费',
      price: new Decimal(0),
    },
    MEMBER_DEDUCTION: {
      name: '会员优惠',
      price: new Decimal(0),
    },
    FULL_REDUCTION: { name: '满减', price: new Decimal(0) },
    CONSUMPTION_REBATE: { name: '消费返利优惠', price: new Decimal(0) },
  }) as DiscountDataByType
  const shopDiscontMap = reactive<Map<string, Decimal>>(new Map())
  const calucateDiscountDetails = (orderDiscounts: OrderDiscount[], shopOrders: ShopOrder[]) => {
    orderDiscounts.forEach((item: OrderDiscount) => {
      if (discountDataByType[item.sourceType]) {
        discountDataByType[item.sourceType].price =
          discountDataByType[item.sourceType].price.add(
            item.discountItems.map((item) => new Decimal(item.discountAmount)).reduce((pre, current) => current.add(pre)),
          ) || 0
      }
      if (item.sourceType === 'SHOP_COUPON') {
        item.discountItems.forEach((discount) => {
          if (discount.shopId) {
            if (!shopDiscontMap.has(discount.shopId)) {
              shopDiscontMap.set(discount.shopId, new Decimal(0))
            }
            shopDiscontMap.set(discount.shopId, shopDiscontMap.get(discount.shopId)?.add(divTenThousand(discount.discountAmount)) || new Decimal(0))
          }
        })
      }
    })
    discountDataByType.freightPrice.price = shopOrders
      .flatMap((shopOrder) => shopOrder.shopOrderItems)
      .map((shopOrderItem) => new Decimal(shopOrderItem.freightPrice))
      .reduce((pre, current) => current.add(pre))
  }
  return {
    discountDataByType,
    calucateDiscountDetails,
    shopDiscontMap,
  }
}

export default useCalculateDiscount
