import CalculateHandler from '@pluginPackage/hooks/calculate'
import type { ChainHandleParam } from '@plugin/types'
import type { Process } from '@pluginPackage/hooks/registerDisPlugin'
import type { ApiOrderCouponVO } from '@/apis/plugin/coupon/model'
import { Decimal } from 'decimal.js-light'

export default class CalculateForCoupon extends CalculateHandler {
  private couponRule = {} as ApiOrderCouponVO
  constructor(process: Process) {
    super(process.productInfoArr || [])
    this.couponRule = process.rule
  }
  public handle(params?: Partial<ChainHandleParam>) {
    let disPrice = new Decimal('0')
    let filterProduct = this.productInfo
    if (this.couponRule.productType === 'ASSIGNED') {
      filterProduct = this.productInfo.filter((item) => {
        return (this.couponRule.productIds ?? [item.productId]).find((id) => id === item.productId)
      })
    }
    if (this.couponRule.productType === 'ASSIGNED_NOT') {
      filterProduct = this.productInfo.filter((item) => {
        if (!this.couponRule.productIds) {
          return item
        }
        return this.couponRule.productIds.find((id) => id !== item.productId)
      })
    }
    let totalPrice = filterProduct.reduce((pre, cur) => {
      return new Decimal(pre).add(new Decimal(cur.productPrice).mul(cur.quantity))
    }, new Decimal('0'))
    if (this.couponRule.type === 'PRICE_REDUCE' || this.couponRule.type === 'REQUIRED_PRICE_REDUCE') {
      disPrice = totalPrice.lessThanOrEqualTo(this.couponRule.amount) ? totalPrice : new Decimal(this.couponRule.amount)
    } else {
      disPrice = totalPrice.mul(new Decimal('10').minus(this.couponRule.discount).div(10))
    }
    const resultPrice = Number(params?.discountPrice) ? disPrice.add(Number(params?.discountPrice)).toString() : disPrice.toString()
    const tempObj: ChainHandleParam = {
      shopId: params?.shopId || this.productInfo[0].shopId,
      totalPrice: params?.totalPrice || this.totalPrice,
      discountPrice: resultPrice,
      preferTreatment: Object.assign(params && params.preferTreatment ? params.preferTreatment : {}, {
        COUPON: {
          discountId: this.couponRule.couponUserId,
          discount: disPrice.toString(),
        },
      }),
    }
    return super.handle(Object.assign(params || {}, tempObj))
  }
}
