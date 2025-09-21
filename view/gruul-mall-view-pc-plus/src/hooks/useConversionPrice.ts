import Decimal from 'decimal.js'

export function useConversionPrice(price: string | number | Decimal) {
  return new Decimal(price).div(10000)
}
