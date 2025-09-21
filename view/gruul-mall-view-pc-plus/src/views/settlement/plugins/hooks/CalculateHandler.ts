import Decimal from 'decimal.js'
import { ProductInfo, ChainHandleParam, ChainHandler } from '../types'
export default abstract class CalculateHandler implements ChainHandler {
  private nextHandler: ChainHandler | undefined
  public totalPrice = '0'
  public productInfo: ProductInfo[]
  constructor(productInfo: ProductInfo[]) {
    this.productInfo = productInfo
    this.totalPrice = productInfo
      .reduce((pre, cur) => {
        return pre + new Decimal(cur.productPrice).mul(cur.quantity).toNumber()
      }, 0)
      .toString()
  }
  public setNext(handler: ChainHandler): ChainHandler {
    this.nextHandler = handler
    return handler
  }

  public handle(params: ChainHandleParam) {
    if (this.nextHandler) {
      return this.nextHandler.handle(params)
    }
    return params
  }
}
