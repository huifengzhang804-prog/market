import { computed, provide, ref, type Ref } from 'vue'
import type { ApiSeckILLGoodsDetails, SecDispatcherType } from '@/apis/plugin/secKill/model'
import type { StorageSku, OrderParamsType } from '@/pluginPackage/goods/commodityInfo/types'
import type { ProductResponse } from '@/apis/good/model'

type HookCallback = (params: OrderParamsType) => void

export type HooksOfSecKillType = (currentSku: Ref<StorageSku>, callback?: HookCallback) => SecDispatcherType

export default function hooksOfSecKill(goodsInfo: ProductResponse, callback?: HookCallback) {
  const secKillGoodsInfo = ref<ApiSeckILLGoodsDetails>({
    startTime: '',
    endTime: '',
    productId: '',
    secKillId: '',
  })
  /**
   * 是否有会员折扣
   */
  const whetherAMemberDiscount = computed(() => goodsInfo.stackable.vip)

  /**
   * 判断秒杀是否进行中
   */
  const isJoinSecKill = computed(() => {
    if (goodsInfoRef.value.activity) {
      return goodsInfoRef.value.activity.type === 'SPIKE' && goodsInfoRef.value.activityOpen && goodsInfoRef.value.skuActivity
    }
    return false
  })

  /**
   * 获取秒杀价格
   */
  const getPrice = () => {
    if (goodsInfo.activity) {
      return goodsInfo.activity.activityPrice
    }
    return null
  }
  /**
   * 判断当前商品是否参加秒杀活动
   */
  function doRequest(_: any, goodsInfo: ProductResponse) {
    initGetSeckillConsumerByShopId(goodsInfo)
  }
  const goodsInfoRef = ref<ProductResponse>(goodsInfo)
  /**
   * 查询是否为秒杀商品 获取秒杀信息
   */
  async function initGetSeckillConsumerByShopId(goodsInfo: ProductResponse) {
    goodsInfoRef.value = goodsInfo
    if (goodsInfo.activity) {
      secKillGoodsInfo.value = {
        ...goodsInfo.activity,
        endTime: goodsInfo.activity.time.end,
        startTime: goodsInfo.activity.time.start,
        productId: goodsInfo.productId,
        secKillId: goodsInfo.activity.activityId,
      }
      const { secKillId, productId } = secKillGoodsInfo.value

      callback?.({
        activityId: secKillId,
        type: 'SPIKE',
        productId: productId,
        extra: {
          activityId: secKillId,
        },
        stackable: goodsInfo.activity.stackable,
      })
    }
  }
  /**
   * 获取商品详情页秒杀活动商品信息
   * @param {*} productId
   */
  const exportSlice: Omit<SecDispatcherType, 'getSkuIds'> = {
    doRequest,
    initGetSeckillConsumerByShopId,
    isJoinSecKill,
    secKillGoodsInfo,
    whetherAMemberDiscount,
    getPrice,
  }
  return exportSlice
}
