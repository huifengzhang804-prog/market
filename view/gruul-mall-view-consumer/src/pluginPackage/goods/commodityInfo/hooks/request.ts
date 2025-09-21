import { doGetProduct } from '@/apis/good'

// 商品不存在或已下架直接跳页面
async function initGoodsInfo(goodId: Long, shopId: Long, skuId?: Long) {
  const { data, code, msg } = await doGetProduct({ productId: goodId, shopId, skuId })
  if (code === 20006) {
    uni.showToast({
      title: `${msg || '商品不可用'}`,
      icon: 'none',
      success: () => {
        const time = setTimeout(() => {
          uni.redirectTo({ url: '/basePackage/pages/abnormalGoods/AbnormalGoods' })
          clearTimeout(time)
        }, 1500)
      },
    })
    return Promise.reject('商品不可用')
  }
  return data!
}

async function requestGoodsInfo(params: { goodId: Long; shopId: Long; skuId?: Long }) {
  const goodsInfo = await initGoodsInfo(params.goodId, params.shopId, params.skuId)
  console.log(
    '%c 商品信息:====⩔ %c',
    `background: red;border:1px solid red; padding: 1px; border-radius: 2px 0 0 2px; color: #fff;`,
    `border:1px solid red; padding: 1px; border-radius: 0 2px 2px 0; color: red;`,
    goodsInfo,
  )
  // 规格总信息
  const goodsSku = goodsInfo.specsSkus
  // 秒杀价格
  const flashSaleSku = goodsInfo?.activity?.type === 'SPIKE' ? goodsInfo?.activity.activityPrice : null
  // 当前参与的活动类型
  const activityType = goodsInfo.activity?.type || 'COMMON'
  // 活动是否够进行中
  const activityIsStart = activityType !== 'COMMON' && goodsInfo.activityOpen && goodsInfo.skuActivity
  // 当前sku信息
  const currentSku = params.skuId ? goodsSku.skus.find((sku) => sku.id === params.skuId) : goodsSku.skus[0]
  // 全部sku信息
  const currentSkuArr = goodsSku.skus
  return {
    currentSku,
    goodsSku,
    goodsInfo,
    currentSkuArr,
    flashSaleSku,
    activityType,
    activityIsStart,
  }
}
export { requestGoodsInfo }
