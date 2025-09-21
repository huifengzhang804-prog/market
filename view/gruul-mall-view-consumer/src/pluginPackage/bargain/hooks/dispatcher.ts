import { computed, provide, ref, type Ref } from 'vue'
import { doPostBargainSponsor } from '@/pluginPackage/bargain/apis'
import { useUserStore } from '@/store/modules/user'
import type { BargainHelpPeopleListRes, BargainDispatcherType, BargainSponsorRes } from '@/pluginPackage/bargain/apis/model'
import type { StorageSku, OrderParamsType } from '@/pluginPackage/goods/commodityInfo/types'
import type { ProductResponse } from '@/apis/good/model'

type HookCallback = (params: OrderParamsType) => void
export type HooksOfBargainType = (choosedSku: Ref<StorageSku>, callback?: HookCallback) => BargainDispatcherType
/**
 * 获取砍价信息
 */
export default function hooksOfBargain(
  choosedSku: Ref<StorageSku>,
  goodsInfo: ProductResponse,
  callback?: HookCallback,
  refreshPage?: () => Promise<void>,
) {
  const bargainProductInfo = ref<BargainHelpPeopleListRes>({
    sponsorProductSku: {
      userId: '',
      activityId: '',
      userHeadPortrait: '',
      userNickName: '',
      endTime: '',
      shopId: '',
      productId: '',
      skuId: '',
      skuName: '',
      skuImage: '',
      skuPrice: '',
      floorPrice: '',
      amountCut: '',
      bargainSponsorSkuStatus: 'END',
    },
    ActivityDetailVO: {
      type: 'COMMON',
      activityId: '',
      activityPrice: '',
      time: {
        start: '',
        end: '',
      },
      stackable: {
        vip: false,
        coupon: false,
        full: false,
      },
      data: {},
    },
  })
  const bargainPagePathQuery = ref<BargainSponsorRes>({
    activityId: '',
    productId: '',
    shopId: '',
    skuId: '',
    bargainOrderId: '',
    userId: '',
  })
  const goodsInfoRef = ref<ProductResponse>(goodsInfo)
  const doRequest = async function (_: any, goodsInfo: ProductResponse) {
    goodsInfoRef.value = goodsInfo
    bargainProductInfo.value = {
      sponsorProductSku: goodsInfo?.activity?.data || {},
      ActivityDetailVO: goodsInfo.activity,
    }
    if (goodsInfo?.activity?.data?.bargainOrderId) {
      const { bargainOrderId, activityId, productId, shopId, skuId, userId } = goodsInfo.activity.data
      bargainPagePathQuery.value = { bargainOrderId, activityId, productId, shopId, skuId, userId }
    }
  }
  /**
   * 发起砍价
   */
  const handlerBargainSponsor = async (productName: string, currentSpec: any) => {
    const { nickname: userNickname, avatar: userHeadPortrait } = useUserStore().userInfo.info
    const skuImage = choosedSku.value.image || goodsInfoRef.value.pic
    const params = {
      shopId: goodsInfoRef.value.shopId,
      productId: goodsInfoRef.value.productId,
      skuId: goodsInfoRef.value.skuId,
      skuImage,
      activityId: goodsInfoRef.value.activity!.activityId,
      userHeadPortrait,
      userNickname,
      productFeaturesValue: currentSpec,
    }
    const { code, msg, data } = await doPostBargainSponsor(params)

    if (code !== 200) {
      uni.showToast({ title: `${msg || '发起砍价失败'}`, icon: 'none' })
      return
    }
    if (data) {
      bargainPagePathQuery.value = data
    }
    setTimeout(() => {
      shareBargainLinks(productName, bargainPagePathQuery.value)
    })
    return Promise.resolve()
  }
  // 发起砍价链接
  function shareBargainLinks(productName: string, params: BargainSponsorRes) {
    const { nickname } = useUserStore().userInfo.info
    const baseUrl = import.meta.env.VITE_BASE_URL.replace(/api\//, '')
    const url = `您的好友：${nickname} ，喊你帮忙砍价！！！
        商品名称：${productName} ${baseUrl}h5/#/pluginPackage/bargain/views/bargain?extra=${encodeURIComponent(JSON.stringify(params))}`
    generateCode(url)
  }
  // 每个营销活动都需提供一个 getPrice 供商品详情 dispatcher 调度
  const getPrice = () => {
    return null
  }
  /**
   * 商品是否参与砍价活动
   */
  const isJoinForBargain = computed(() => {
    return goodsInfoRef.value.activity?.type === 'BARGAIN'
  })
  // 砍价进行时
  const isBargaining = computed(() => {
    return goodsInfoRef.value.activityOpen && goodsInfoRef.value.skuActivity
  })
  const generateCode = (data: string) => {
    if (data.length) {
      uni.setClipboardData({
        data,
        showToast: false,
        success: async () => {
          uni.showToast({ title: '活动商品链接已复制，邀请好友一起砍价吧！！！', icon: 'none' })
          // 刷新砍价列表
          await refreshPage?.()
          if (goodsInfoRef.value?.activity?.data?.isSelfBargain) {
            // 自我砍价
            setTimeout(() => {
              uni.showToast({ title: `自我砍价成功`, icon: 'none' })
            }, 1000)
          }
        },
        fail: function (res) {
          uni.showToast({ title: '复制失败', icon: 'none' })
        },
      })
    }
  }
  function navigateToBargain() {
    if (!goodsInfoRef.value?.activity?.data?.activityId) return
    uni.navigateTo({
      url: `/pluginPackage/bargain/views/bargain?extra=${encodeURIComponent(JSON.stringify(bargainPagePathQuery.value))}`,
    })
  }
  const exportSlice: BargainDispatcherType = {
    doRequest,
    isJoinForBargain,
    bargainProductInfo,
    handlerBargainSponsor,
    navigateToBargain,
    getPrice,
    isBargaining,
    goodsInfoRef,
  }
  provide('bargainProvide', exportSlice)
  return exportSlice
}
