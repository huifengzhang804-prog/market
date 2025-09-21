// import CalculateHandler from '@pluginPackage/hooks/calculate'
// import { ChainHandleParam } from '@plugin/types'
// import type { CartFullReductionResponseParameters } from '@/apis/plugin/fullDiscount/model'
// import type { Process } from '@pluginPackage/hooks/registerDisPlugin'
import Decimal from 'decimal.js'
import CalculateHandler from '../../hooks/CalculateHandler'
import { Process } from '@/views/settlement/hooks/RegisterDisPlugin'
import { ChainHandleParam } from '../../types'
import { CartFullReductionResponseParameters } from '../model'

export default class CalculateForFull extends CalculateHandler {
  private fullRule = {} as CartFullReductionResponseParameters
  constructor(process: Process) {
    super(process.productInfoArr || [])
    this.fullRule = process.rule
  }
  public handle(params?: Partial<ChainHandleParam>) {
    const fullReductionRule = this.fullRule.flag ? this.fullRule : this.fullRule.fullReductionRules[0]
    let disPrice = new Decimal('0')
    let filterProduct = this.productInfo
    if (this.fullRule.productType === 'SPECIFIED_PRODUCT_PARTICIPATE') {
      filterProduct = this.productInfo.filter((item) => {
        return this.fullRule.productIds.find((id) => id === item.productId)
      })
    }
    if (this.fullRule.productType === 'SPECIFIED_PRODUCT_NOT_PARTICIPATE') {
      filterProduct = this.productInfo.filter((item) => {
        return this.fullRule.productIds.find((id) => id !== item.productId)
      })
    }
    let totalPrice = filterProduct.reduce((pre, cur) => {
      return new Decimal(pre).add(new Decimal(cur.productPrice).mul(cur.quantity))
    }, new Decimal('0'))
    if (fullReductionRule.fullReductionRule === 'FULL_REDUCTION') {
      disPrice = totalPrice.lessThanOrEqualTo(fullReductionRule.discountAmount || 0) ? totalPrice : new Decimal(fullReductionRule.discountAmount || 0)
    } else {
      disPrice = totalPrice.mul(new Decimal('10').minus(fullReductionRule.discountRatio || 0).div(10))
    }
    const resultPrice = Number(params?.discountPrice) ? disPrice.add(Number(params?.discountPrice)).toString() : disPrice.toString()
    const tempObj: ChainHandleParam = {
      shopId: params?.shopId || this.productInfo[0].shopId,
      totalPrice: params?.totalPrice || this.totalPrice,
      discountPrice: resultPrice,
      preferTreatment: Object.assign(params && params.preferTreatment ? params.preferTreatment : {}, {
        FULL: {
          discountId: fullReductionRule.id,
          discount: disPrice.toString(),
          id: this.fullRule.fullReductionId,
        },
      }),
    }
    return super.handle(Object.assign(params || {}, tempObj))
  }
}
