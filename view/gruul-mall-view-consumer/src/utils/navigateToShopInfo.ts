import { doGetShopInfo } from '@/apis/good'
import { ShopMode } from '@/constant/global'
// 跳转向商品详情
export const jumpGoods = async (shopId: Long, goodId: Long, shopingCart: boolean = false, skuId?: string) => {
  try {
    const { data, code, msg } = await doGetShopInfo({ shopId, type: 'PRODUCT_DETAIL' })
    if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取商铺信息失败'}`, icon: 'none' })
    if (data) {
      const { shopMode } = data
      if (shopMode === ShopMode.COMMON) {
        uni.navigateTo({
          url: `/pluginPackage/goods/commodityInfo/InfoEntrance?shopId=${shopId}&goodId=${goodId}&shopingCart=${shopingCart}&skuId=${skuId}`,
        })
      } else if (shopMode === ShopMode.O2O) {
        uni.navigateTo({
          url: `/pluginPackage/o2o-goods/o2o-goods?shopId=${shopId}&goodId=${goodId}&shopingCart=${shopingCart}`,
        })
      } else {
        uni.showToast({
          title: '获取店铺信息失败',
          icon: 'none',
        })
      }
    }
  } catch (error) {
    console.log(error)
    return uni.showToast({
      title: '跳转失败',
      icon: 'none',
    })
  }
}
