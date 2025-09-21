export { AddonIntegralGoods, IntegralProductAttributes }
/**
 * 添加积分商品
 * @param {*} id
 * @param {*} name 积分商品名称
 * @param {*} price 市场价格
 * @param {*} integralPrice 积分价
 * @param {*} stock 库存
 * @param {*} virtualSalesVolume 虚拟销量
 * @param {*} detail 商品详情
 * @param {*} pic 展示主图
 * @param {*} albumPics 画册图片
 * @param {*} freightPrice 运费金额
 */
interface AddonIntegralGoods {
  id?: string
  name: string
  price: number
  integralPrice: number
  stock: number
  virtualSalesVolume: number
  detail: string
  pic: string
  albumPics: string
  freightPrice: number
  integralProductAttributes: IntegralProductAttributes[]
  salePrice: number
  productType: 'REAL_PRODUCT' | 'VIRTUAL_PRODUCT'
}
/**
 * 商品属性信息
 */
interface IntegralProductAttributes {
  attributeName: string
  attributeValue: string
}
