/**
 * 平台优惠券计算
 */
import CalculateHandler from '@pluginPackage/hooks/calculate'
import type { ChainHandleParam } from '@plugin/types'
import type { ApiOrderCouponVO } from '@/apis/plugin/coupon/model'
import type { Process } from '@pluginPackage/hooks/registerDisPlugin'
import { Decimal } from 'decimal.js-light'

export default class CalculateForPlatformCoupon extends CalculateHandler {
  private couponRule = {} as ApiOrderCouponVO
  constructor(process: Process) {
    super(process.productInfoArr || [])
    this.couponRule = process.rule
  }
  public handle(params?: Partial<ChainHandleParam>) {
    let disPrice = new Decimal('0')
    let filterProduct = this.productInfo
    let totalPrice = filterProduct.reduce((pre, cur) => {
      return new Decimal(pre).add(new Decimal(cur.productPrice).mul(cur.quantity))
    }, new Decimal('0'))
    if (this.couponRule.type === 'PRICE_REDUCE' || this.couponRule.type === 'REQUIRED_PRICE_REDUCE') {
      disPrice = totalPrice.lessThanOrEqualTo(this.couponRule.amount) ? totalPrice : new Decimal(this.couponRule.amount)
    } else {
      disPrice = new Decimal(totalPrice.mul(new Decimal('10').minus(this.couponRule.discount).div(10)))
    }
    const resultPrice = Number(params?.discountPrice) ? disPrice.add(Number(params?.discountPrice)).toString() : disPrice.toString()
    const tempObj: ChainHandleParam = {
      shopId: '0',
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
