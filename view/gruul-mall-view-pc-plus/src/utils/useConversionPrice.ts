import Decimal from 'decimal.js'

export function useConversionPrice(price: string | number | Decimal = 0): Decimal {
  if (!price) price = 0
  return new Decimal(price).div(10000)
}
/**
 * @description: 元转毫
 * @param {string} price
 * @returns {*}
 */
export function useConversionHaoPrice(price: string | number | Decimal): Decimal {
  return new Decimal(price).mul(10000)
}
