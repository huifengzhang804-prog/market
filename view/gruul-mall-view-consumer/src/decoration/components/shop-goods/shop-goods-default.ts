// 店铺默认值
export const shop: {
  name: string
  id: Long
  logo: string
  advertisement: string
  promotion: string
  couponList: any[]
} = { name: '', id: '', logo: '', advertisement: '', promotion: '', couponList: [] }

// 商品默认值
export const goods: {
  name: string
  id: Long
  logo: string
  price: string
  onlyId?: string
}[] = [
  { name: '', id: '', logo: '', price: '' },
  { name: '', id: '', logo: '', price: '' },
  { name: '', id: '', logo: '', price: '' },
]

// 店铺+商品默认值
export const shopInfo = [
  {
    shop,
    goods,
  },
]

// 整体数据
export const formData = {
  showStyle: 'is-style-one',
  shopBigImg: '',
  shopInfo,
  title: '', //推荐店铺标题
  firstFew: 35,
}
