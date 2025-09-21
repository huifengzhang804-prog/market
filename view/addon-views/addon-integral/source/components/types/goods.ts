export { IntegralGoods }
interface IntegralGoods {
  createTime: string
  id: string
  integralPrice: string
  name: string
  pic: string
  salesVolume: string
  status: 'SELL_ON' | 'SELL_OFF'
  stock: number
  salePrice: string
  productType: 'REAL_PRODUCT' | 'VIRTUAL_PRODUCT'
  checked: boolean
}
