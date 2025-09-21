<script lang="ts" setup>
import { computed, type ComputedRef, inject, onMounted, onUnmounted, type PropType, reactive, ref, unref, watch } from 'vue'
import { doGetOrderEvaluateInfo, doGetShopInfo } from '@/apis/good'
import QIcon from '@/components/q-icon/q-icon.vue'

// 优惠券 s
import PreferentialPopup from '@/pluginPackage/goods/commodityInfo/goodsPreferentialPopup/preferential-popup.vue'
// 优惠券 e
//根据传入类型决定秒杀商品是否可用传入的优惠
import SkuImageScroll from '@/pluginPackage/goods/commodityInfo/components/skuImageScroll/skuImageScroll.vue'
import { doGetDefaultAddress } from '@/apis/address'
import { doGetFreightCalculation } from '@/apis/order'
import GoodsDetailsSecodsKill from '@pluginPackage/scondsKill/components/goods-details-secods-kill.vue'
import RankingList from '@/pluginPackage/goods/commodityInfo/components/ranking-list.vue'
import SellingInfo from '@/pluginPackage/goods/commodityInfo/components/selling-info.vue'
import MyBargain from '@/pluginPackage/bargain/views/goodsDateilComponents/myBargain/myBargain.vue'
import BargainProcess from '@/pluginPackage/bargain/views/goodsDateilComponents/bargainProcess/bargainProcess.vue'
import BargainCountDown from '@/pluginPackage/bargain/views/goodsDateilComponents/countDown/countDown.vue'
import { EXPRESS_CODE, INTRA_CITY_DISTRIBUTION_CODE_1, INTRA_CITY_DISTRIBUTION_CODE_2 } from '@/apis/address/model'
// 拼团组件
import GroupCardCommodity from '@pluginPackage/group/views/group-card-commodity.vue'
import GroupListCommodity from '@pluginPackage/group/views/group-list-commodity.vue'
// 套餐优惠
import CardSetMeal from '@/pluginPackage/setMeal/components/cardSetMeal.vue'

// 预计赚
import ProfitCard from '@/pluginPackage/goods/commodityInfo/components/profit-card.vue'

// 进店看看
import CommodityInShop from '@/pluginPackage/goods/commodityInfo/components/commodity-in-shop.vue'
import { serviceHandler } from '@/pluginPackage/goods/commodityInfo'
import formatRichText from '@/pluginPackage/utils/formatRichText'

import { useUserStore } from '@/store/modules/user'

import type { GroupDispatcherType } from '@/apis/plugin/group/model'
import type { OrderType, ReceiverAreaDataParams } from '@/pluginPackage/order/confirmOrder/types'
import { ORDER_TYPE } from '@/pluginPackage/order/confirmOrder/types'
import type { ApiSeckILLGoodsDetails } from '@/apis/plugin/secKill/model'
import type { BargainDispatcherType } from '@/pluginPackage/bargain/apis/model'
import {
  type ApiEvaluation,
  type comDispatcherType,
  EvaluationType,
  type ProductResponse,
  ServiceBarrier,
  type StorageSku,
} from '@/pluginPackage/goods/commodityInfo/types'
import useAddressDispatcher from '@/store/dispatcher/useAddressDispatcher'
import useConvert from '@/composables/useConvert'
import useMember from '@/composables/useMember'
import type { Shop } from '@/apis/shops/type'
import { type ActivityDetailVO, DiscountType } from '@/apis/good/model'
import { debounce } from 'lodash'
import type { Address } from '@/apis/address/type'
import { SHOW_LOGIN_MODAL } from '@/utils/tokenConfig'

const $useAddressDispatcher = useAddressDispatcher()
const $comProvide = inject('comProvide') as comDispatcherType

const $props = defineProps({
  info: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
  sku: {
    type: Array as PropType<StorageSku[]>,
    default() {
      return []
    },
  },
  currentChoosedSku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  count: {
    type: Number,
    default: 1,
  },
  isJoinSecKill: {
    type: Boolean,
    default: false,
  },
  currentParams: {
    type: Array as PropType<string[]>,
    default() {
      return []
    },
  },
  secKillGoodsInfo: { type: Object as PropType<ApiSeckILLGoodsDetails>, default: () => ({}) },
})
// 活动信息
const activityInfo: ComputedRef<ActivityDetailVO> = computed(() => {
  if ($props.info?.activity) {
    return $props.info.activity
  }
  return {
    type: 'COMMON' as OrderType,
    activityId: '',
    stackable: {
      coupon: false,
      full: false,
      vip: false,
    },
    time: {
      start: '',
      end: '',
    },
    data: {},
  }
})
// 活动类型
const activityType = computed(() => {
  return ORDER_TYPE[activityInfo.value.type]
})
// 活动是否开始
const activitiesBegan = computed(() => {
  return !!($props.info.activityOpen && $props.info.activity?.activityId)
})
const $emit = defineEmits(['skuImageChoose', 'choosedSkuChange', 'chooseBuy', 'seckillEnd', 'updateShopInfo'])
const groupProvide = inject('groupProvide') as GroupDispatcherType
const bargainProvide = inject('bargainProvide') as BargainDispatcherType
const { includeBenefit } = useMember()
// SEE 看了又看 SALES 销量排行
const seeActive = ref<'SEE' | 'SALES'>('SEE')
const $useUserStore = useUserStore()
const shopInfo = ref<Shop>({
  shopId: '' as Long,
  shopLogo: '',
  shopName: '',
  newTips: '',
  status: '',
  shopType: 'ORDINARY',
  followCount: '0',
  score: '0',
})
// 服务保障pop
const servicePop = ref(false)
const evaluateInfo = ref<ApiEvaluation>({
  contentCount: '',
  evaluate: {
    comment: '',
    createTime: '',
    deleted: false,
    id: '',
    image: '',
    isExcellent: false,
    itemId: '',
    medias: [],
    name: '',
    orderNo: '',
    packageId: '',
    productId: '',
    rate: 5,
    shopId: '',
    skuId: '',
    specs: [],
    updateTime: '',
    userId: '',
    shopReply: '',
    avatar: '',
    nickname: '',
  },
  mediaCount: '',
  praiseCount: '',
  praiseRatio: '',
  totalCount: '',
})
const cuurrentItemId = ref($props.currentChoosedSku.id)
// 优惠弹窗
const isPreferentialPopup = ref(false)
// 快递弹窗
const freightPop = ref(false)

enum Freight {
  EXPRESS = 'EXPRESS',
  INTRA_CITY_DISTRIBUTION = 'INTRA_CITY_DISTRIBUTION',
  SHOP_STORE = 'SHOP_STORE',
  VIRTUAL = 'VIRTUAL',
  MERCHANT = 'MERCHANT',
  WITHOUT = 'WITHOUT',
}

// 权益相关
const interestsConfig = reactive<{
  delivery: {
    sendTime: string
    receiveTime: string
  }
  freight: {
    [key in keyof typeof Freight]: {
      price: number
      label: string
      msg: string
      errCode: number[]
      code: number
    }
  }
}>({
  // 发货
  delivery: {
    sendTime: '',
    // 预计收货时间
    receiveTime: '',
  },
  // 运费配置
  freight: {
    EXPRESS: { price: 0, label: '快递', msg: '免费', errCode: [EXPRESS_CODE], code: 0 },
    // 60001 超出配送范围 60002 不足配送金额 不展示同城运费信息
    INTRA_CITY_DISTRIBUTION: { price: 0, label: '同城', msg: '免费', errCode: [INTRA_CITY_DISTRIBUTION_CODE_1], code: 0 },
    SHOP_STORE: { price: 0, label: '自提', msg: '免费', errCode: [INTRA_CITY_DISTRIBUTION_CODE_1], code: 0 },
    VIRTUAL: { price: 0, label: '', msg: '', errCode: [INTRA_CITY_DISTRIBUTION_CODE_1], code: 0 },
    MERCHANT: { price: 0, label: '商家配送', msg: '免费', errCode: [EXPRESS_CODE], code: 0 },
    WITHOUT: {
      price: 0,
      label: '无需配送',
      msg: '',
      errCode: [],
      code: 0,
    },
  },
})

// 记录当前 productId
let curProductId: Long = ''

watch(
  () => $props.info,
  (val) => {
    if (!curProductId) {
      curProductId = val.productId
      initEvaluateInfo()
      const shopId = val?.shopId
      initShopInfo(shopId)
    }
    if (val.earningMap?.DISTRIBUTE) {
      bonusPrice.value = divTenThousand(val?.earningMap?.DISTRIBUTE || 0).toFixed(2)
    }
  },
)
watch(
  () => $props.currentChoosedSku.id,
  (val) => {
    cuurrentItemId.value = val
    // 获取 默认地址 并查询运费
    initDefaultAddress()
  },
)

const defaultAddress = ref<Address>()

async function initDefaultAddress() {
  // 用户未登录不调取运费查询接口
  if (!$useUserStore.getterToken) return
  if (defaultAddress.value) return
  const { data, code } = await doGetDefaultAddress()
  if (code === 200) {
    if (data?.area?.length) {
      defaultAddress.value = data
      getFreightInformation()
    }
  }
}

const dispatcher = () => {
  if ($props.currentChoosedSku.id) {
    initDefaultAddress()
  }
}

onMounted(() => $useAddressDispatcher.addCartSubscriber(dispatcher))
onUnmounted(() => $useAddressDispatcher.removeCartSubscriber(dispatcher))

/**
 * 货运信息
 */
async function getFreightInformation() {
  const { freightTemplateId: templateId, distributionMode, shopId } = $props.info
  if (!templateId && templateId !== 0) {
    return
  }
  const { weight, id: skuId, salePrice: price } = $props.currentChoosedSku
  if (defaultAddress.value) {
    const params: ReceiverAreaDataParams = {
      ...defaultAddress.value,
      //freeRight 是否有会员免运费
      freeRight: includeBenefit('LOGISTICS_DISCOUNT'),
      shopFreights: [
        {
          shopId,
          freights: [
            {
              templateId,
              skuInfos: [
                {
                  skuId,
                  weight,
                  price,
                  num: 1,
                },
              ],
            },
          ],
        },
      ],
      distributionMode,
    }
    const { data: res, code, msg } = await doGetFreightCalculation(params)
    if (code !== 200 && code !== 100002) {
      uni.showToast({ title: `${msg ? msg : '运费获取失败'}`, icon: 'none' })
      return
    }
    for (const resKey in res) {
      const key = resKey as keyof typeof Freight
      if (res[key].code) {
        interestsConfig.freight[key].code = res[key].code
        if (interestsConfig.freight[key].errCode.includes(res[key].code)) {
          interestsConfig.freight[key].msg = '超出配送范围'
        }
        continue
      }
      interestsConfig.freight[key].price = Object.values(res[key]).length ? (Object.values(res[key])[0] as number) : 0
    }
  }
}

async function initShopInfo(shopId: Long) {
  const { code, data, msg } = await doGetShopInfo({ shopId, type: 'PRODUCT_DETAIL' })
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取店铺信息失败'}`, icon: 'none' })
  if (data) {
    const { logo: shopLogo, shopType, name: shopName, score } = data
    const params = {
      shopId: shopId,
      shopName,
      shopLogo,
      shopType,
      followCount: data?.follow || '0',
      score,
      newTips: '',
      status: '',
    }
    shopInfo.value = params
    $emit('updateShopInfo', params)
  }
}

async function initEvaluateInfo(type?: keyof typeof EvaluationType) {
  const { code, msg, data } = await doGetOrderEvaluateInfo($props.info.shopId, $props.info.productId, type)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取评价概述失败'}`, icon: 'none' })
  evaluateInfo.value = data
  uni.$emit('totalEvaluateChange', evaluateInfo.value.totalCount)
}

/**
 * 导航去查看评价
 */
const handleGoOrderAssess = () => {
  const { totalCount, contentCount, mediaCount } = evaluateInfo.value
  const params = `&totalCount=${totalCount}&contentCount=${contentCount}&mediaCount=${mediaCount}`
  uni.navigateTo({
    url: `/basePackage/pages/orderAssess/OrderEvaluate?shopId=${$props.info.shopId}&productId=${$props.info.productId}${params}`,
  })
}
const handleSkusChange = (sku: StorageSku, skuIndex: number, request = true) => {
  cuurrentItemId.value = sku.id
  $emit('choosedSkuChange', sku, skuIndex, request)
}
/**
 * 点击查看优惠
 */
const handleReceiveCoupon = () => {
  isPreferentialPopup.value = true
}

const interestsConfigService = computed(() => {
  if (!$props.info.serviceIds.length) return ''
  const services: string[] = []
  $props.info.serviceIds.forEach((item) => {
    const currentHandler = serviceHandler[item]
    services.push(currentHandler.name)
    if (currentHandler.isSendTimeService) {
      interestsConfig.delivery.sendTime = currentHandler.name
      interestsConfig.delivery.receiveTime = currentHandler.sendTime()
    }
  })
  return services.join('、')
})
const handleClickFreightCell = debounce(() => {
  freightPop.value = true
}, 200)

function freightPriceFormat(item: keyof typeof interestsConfig.freight) {
  return +interestsConfig.freight[item].price > 0 ? interestsConfig.freight[item].price : interestsConfig.freight[item].msg
}

/**
 * 查看全部 进店铺
 */
const handleNavToShop = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${$props.info.shopId}`,
  })
}
const handleSeeAll = () => {
  handleNavToShop()
}
/**
 * 跳转开通会员
 */
const jumpToMember = () => {
  const curmemberDiscount = memberDiscount.value
  //如果已经是会员 则不需要跳转开通付费会员
  if (curmemberDiscount?.data?.isVip) {
    //如果有折扣 则查看优惠
    if (curmemberDiscount.discount || discountList.value.length) {
      return handleReceiveCoupon()
    }
    return
  }
  if (!$useUserStore.getterToken) {
    uni.$emit(SHOW_LOGIN_MODAL, true)
    return
  }
  uni.navigateTo({
    url: '/pluginPackage/member/views/MemberCenter',
  })
}
// 付费会员折扣信息
const memberDiscount = computed(() => $comProvide.discountMap.value?.VIP)
const { divTenThousand } = useConvert()

const discountList = computed(() => {
  const list: string[] = []
  const discountMap = $comProvide.discountMap.value
  Object.keys(discountMap).forEach((key) => {
    const mapKey = key as keyof typeof DiscountType
    const discount = divTenThousand(discountMap[mapKey].discount)
    if (discount.lte(0)) {
      return
    }
    if (DiscountType[mapKey]) {
      list.push(`${DiscountType[mapKey]}优惠 ${discount.toFixed(2)}`)
    }
  })
  return list
})

defineExpose({ shopInfo })
// 价格是否展示
const isHideGoodsPrice = computed(() => {
  return $props.info.activityOpen && $props.info.skuActivity
})
const bonusPrice = ref('0.00') // 分销预计赚

const params = computed(() => {
  return $props.info.extra?.productParameters?.map((item) => item.featureName).join(' ')
})
const paraPop = ref(false)

const rebateAmount = computed(() => {
  return divTenThousand($comProvide.goodInfo.value?.earningMap?.REBATE || '0').toFixed(2)
})

const skuConfig = reactive<{ currentSkuId: Long; isMainPic: boolean }>({ currentSkuId: '', isMainPic: true })
/**
 * 当前sku变化
 */
watch(
  () => $comProvide.currentChoosedSku.value?.id,
  (val) => {
    if (val) {
      skuConfig.currentSkuId = val
    }
  },
)
const SkuImageScrollRef = ref()
/**
 * 轮播变化
 */
watch(
  () => $comProvide.swiperList.currenuIdx,
  (val, old) => {
    if ($comProvide.swiperList.mainList.length === 0) {
      skuConfig.isMainPic = true
    } else {
      skuConfig.isMainPic = val < $comProvide.swiperList.mainList.length
    }
    if (!skuConfig.isMainPic) {
      // 如果不是主图,切换到相应规格
      const skuIndex = val - $comProvide.swiperList.mainList.length
      if (skuIndex >= 0 && skuIndex < $props.sku.length) {
        const sku = $props.sku[skuIndex]
        $emit('choosedSkuChange', sku, skuIndex)
        // 更新小图滚动位置
        SkuImageScrollRef.value?.handleSkusClick(sku, skuIndex, false)
      }
    } else {
      // 如果是主图，切换到主图位置
      SkuImageScrollRef.value?.handleSkusClick($props.sku[0], -1, false)
    }
  },
  { immediate: true },
)
const showSku = computed(() => {
  return $props.sku.filter((item) => item.image).length > 1
})
</script>
<template>
  <view id="swiper_two">
    <SkuImageScroll
      v-if="showSku"
      ref="SkuImageScrollRef"
      :info="info"
      :sku="sku"
      :skuConfig="skuConfig"
      @skuImageChange="
        (sku, index) => {
          handleSkusChange(sku, index, false)
        }
      "
    />
    <!-- 拼团 -->
    <GroupCardCommodity
      v-if="activityType === '拼团'"
      :activitiesBegan="activitiesBegan"
      :activityInfo="activityInfo"
      :currentChoosedSku="currentChoosedSku"
      :goodsInfo="info"
      :groupExist="groupProvide.existGroup.value"
      :groupInfo="groupProvide.groupInfo.value"
      :groupJoin="groupProvide.isJoinForGroup.value"
      @end="$emit('seckillEnd')"
    />
    <!-- 砍价倒计时 -->
    <BargainCountDown
      v-if="activityType === '砍价'"
      :activitiesBegan="activitiesBegan"
      :activityInfo="activityInfo"
      :currentChoosedSku="currentChoosedSku"
      :goodsInfo="info"
      @end="$emit('seckillEnd')"
    />
    <!-- 秒杀 -->
    <GoodsDetailsSecodsKill
      v-if="activityType === '秒杀'"
      :activitiesBegan="activitiesBegan"
      :activityInfo="activityInfo"
      :current-choosed-sku="currentChoosedSku"
      :goods-info="info"
      :sec-kill-goods-info="secKillGoodsInfo"
      @end="$emit('seckillEnd')"
    />
    <!-- 套餐 -->
    <div v-if="activityType === '套餐' && $props.info.skuActivity">套餐</div>
    <!-- 常规商品展示价格 s-->
    <view :class="[activityType === '秒杀' && 'seckill']" class="line">
      <SellingInfo :evaluate-info="evaluateInfo" :good-info="info" :name="info.name" :showPrice="!isHideGoodsPrice" :sku="currentChoosedSku" />
      <view v-if="info.saleDescribe" class="line-describe">
        {{ info.saleDescribe }}
      </view>
    </view>
    <!-- 常规商品展示价格 e-->
    <view class="goods-card">
      <u-cell-group id="eval" :border="false">
        <u-cell-item
          :arrow="!memberDiscount?.data?.isVip"
          :border-bottom="false"
          :value="memberDiscount?.data?.isVip ? '' : '去开通'"
          :value-style="{ color: 'red' }"
          @click="jumpToMember"
        >
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">会员</text>
              <view class="goods-card--value">
                <text v-if="memberDiscount?.data?.discountDesc" class="goods-card__tag">
                  {{ memberDiscount?.data?.discountDesc + ' 优惠' + divTenThousand(memberDiscount?.discount).toFixed(2) }}
                </text>
                <text v-else-if="memberDiscount?.data?.isVip">暂无优惠</text>
                <text v-else> 开通会员获得优惠多多</text>
              </view>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="discountList.length" :border-bottom="false" @click="handleReceiveCoupon">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">优惠</text>
              <text class="goods-card--value">
                <text v-for="(item, index) in discountList" :key="index" class="goods-card__tag">
                  {{ item }}
                  <text v-if="index !== discountList.length - 1">,</text>
                </text>
              </text>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="bonusPrice !== '0.00' || rebateAmount !== '0.00'" :arrow="false" :border-bottom="false">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">赚钱</text>
              <view class="goods-card--value">
                <ProfitCard v-if="bonusPrice !== '0.00'" :price="bonusPrice" :un-active="true" style="display: inline-flex; margin-right: 8rpx" />
                <ProfitCard v-if="rebateAmount !== '0.00'" :price="rebateAmount" style="display: inline-flex" text="预计返" un-active />
              </view>
            </view>
          </template>
        </u-cell-item>
        <!-- 运费 服务 规格 -->
        <u-cell-item v-if="!info.distributionMode.includes('VIRTUAL')" :arrow="false" :border-bottom="false" @click="handleClickFreightCell">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">运费</text>
              <view class="goods-card--value">
                <template v-for="item in info.distributionMode" :key="item">
                  <view
                    v-if="interestsConfig.freight[item].label && interestsConfig.freight[item].code !== INTRA_CITY_DISTRIBUTION_CODE_2"
                    style="margin-right: 20rpx; display: inline-block"
                  >
                    <text> {{ interestsConfig.freight[item].label }}</text>
                    <text
                      :class="{
                        'freight-free': freightPriceFormat(item) === '免费',
                        'freight-unfree-beyond': freightPriceFormat(item) === '超出配送范围',
                      }"
                      class="freight-unfree"
                    >
                      {{ freightPriceFormat(item) }}
                    </text>
                  </view>
                </template>
              </view>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="interestsConfigService" :arrow="false" :border-bottom="false">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">服务</text>
              <text class="goods-card--value">{{ interestsConfigService }}</text>
            </view>
          </template>
        </u-cell-item>

        <u-cell-item v-if="defaultAddress?.area" :arrow="false" :border-bottom="false">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">送至</text>
              <view class="goods-card--value">
                {{ defaultAddress.area.join(' ') }}
              </view>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-show="sku.length > 1" :border-bottom="false" @click="$emit('chooseBuy')">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">已选</text>
              <text v-if="currentChoosedSku.specs?.length" class="goods-card--value" style="color: #fa3534">
                {{ [...currentChoosedSku.specs, ...currentParams].join(' , ') }} , {{ count }}件
              </text>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-if="params" :border-bottom="false" @click="paraPop = true">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">参数</text>
              <view class="goods-card--value">
                {{ params }}
              </view>
            </view>
          </template>
        </u-cell-item>
        <view style="height: 6rpx"></view>
      </u-cell-group>
    </view>
  </view>

  <!-- 活动ing详情 -->
  <template v-if="info.activityOpen">
    <!-- 我的砍价 s -->
    <view v-if="unref(bargainProvide.isJoinForBargain) && bargainProvide.bargainProductInfo.value?.sponsorProductSku?.activityId" class="goods-card">
      <MyBargain :bargainInfo="bargainProvide.bargainProductInfo.value.sponsorProductSku" @navToBargain="bargainProvide.navigateToBargain" />
    </view>
    <!-- 我的砍价 e -->
    <!-- 砍价流程 s -->
    <view v-if="unref(bargainProvide.isJoinForBargain)" class="goods-card">
      <BargainProcess />
    </view>
    <!-- 砍价流程 e -->
    <!-- 我要开团入口 & 正在拼团的列表(列表中自己看不到自己正在拼的团) -->
    <GroupListCommodity
      :currentChoosedSku="currentChoosedSku"
      :groupExist="groupProvide.existGroup.value"
      :groupInfo="groupProvide.groupInfo.value"
      :groupJoin="groupProvide.isJoinForGroup.value"
      :groupList="groupProvide.groupList.value"
    />
  </template>

  <!-- 评价 -->
  <view v-if="evaluateInfo.totalCount !== '0'" class="goods-card">
    <u-cell-group :border="false">
      <u-cell-item
        :border-bottom="false"
        :title="`评价  (${Number(evaluateInfo.contentCount) > 100 ? '100+' : evaluateInfo.contentCount})`"
        :title-style="{ 'font-weight': 500, color: '#000', 'font-size': '30rpx' }"
        :value="`好评率${evaluateInfo.praiseRatio ? evaluateInfo.praiseRatio : 0}%`"
        :value-style="{ color: 'rgb(245, 67, 25)', fontSize: '24rpx' }"
        use-label-slot
        @click="handleGoOrderAssess"
      />
    </u-cell-group>
  </view>
  <!-- 进店看看  s  -->
  <view id="shop" class="goods-card">
    <CommodityInShop :shop-info="shopInfo" />
    <!-- 进店看看  e  -->
    <!-- 看了又看  s  -->
    <view id="see" class="see-title">
      <view class="see-content">
        <view :class="{ 'see-active': seeActive === 'SEE' }" class="see-content__item" @click.stop="seeActive = 'SEE'">
          <q-icon :color="seeActive === 'SEE' ? 'rgb(245, 67, 25)' : 'rgb(153, 153, 153)'" name="icon-remen" size="26rpx"></q-icon>
          看了又看
        </view>
        <view class="see-content__line" />
        <view :class="{ 'see-active': seeActive === 'SALES' }" class="see-content__item" @click.stop="seeActive = 'SALES'">
          <q-icon :color="seeActive === 'SALES' ? 'rgb(245, 67, 25)' : 'rgb(153, 153, 153)'" name="icon-honor" size="26rpx"></q-icon>
          排行榜
        </view>
      </view>
    </view>
    <RankingList :see-active="seeActive" :shop-id="shopInfo.shopId" :product-id="$props.info.productId" @see-all="handleSeeAll" />
  </view>
  <!-- 看了又看  e  -->
  <!-- 优惠套餐 s -->
  <CardSetMeal :setMealList="info.packages" :shopId="info.shopId" />
  <!-- 优惠套餐 e -->
  <template v-if="$props.info.detail">
    <view id="detail" style="margin-bottom: 50rpx; background: #fff">
      <view>
        <!-- #ifdef APP-PLUS -->
        <rich-text :nodes="formatRichText($props.info.detail)"></rich-text>
        <!-- #endif -->

        <!-- #ifndef APP-PLUS -->
        <u-parse :html="formatRichText($props.info.detail)"></u-parse>
        <!-- #endif -->
      </view>
    </view>
  </template>
  <!-- 服务保障 -->
  <u-popup v-model="servicePop" border-radius="20" height="300px" mode="bottom" safe-area-inset-bottom>
    <view class="parameter__header">服务保障</view>
    <view v-for="item in $props.info.serviceIds" :key="item" class="parameter__item">
      <view class="parameter__item--top">{{ ServiceBarrier[item] }}</view>
    </view>
  </u-popup>

  <u-popup v-model="paraPop" border-radius="20" height="500px" mode="bottom" safe-area-inset-bottom>
    <view class="parameter__header">商品参数</view>
    <view v-for="item in $props.info.extra?.productParameters" :key="item.id" class="parameter__item">
      <view class="parameter__item--top">{{ item.featureName }}</view>
      <view class="parameter__item--bottom">{{ item.featureValues.map((item) => item.firstValue).join(' ') }}</view>
    </view>
  </u-popup>
  <!-- 优惠弹窗 -->
  <PreferentialPopup v-if="isPreferentialPopup" v-model:show="isPreferentialPopup" :shopInfo="shopInfo" :sku="currentChoosedSku" />
</template>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

@include b(swiper-sku) {
  padding: 4rpx;
  display: flex;
  flex-wrap: nowrap;
  @include e(image) {
    border-radius: 30rpx;
    transform: translateX(0);
    flex-shrink: 0;
    width: calc(20% - 4rpx);
    height: 150rpx;
    margin-right: 4rpx;
    border: 2px solid transparent;
    text-align: center;
    line-height: 150rpx;
    font-weight: 700;
    transition: transform 1s;
  }
}

@include b(see-mian) {
  @include e(u-cell) {
    padding-left: 0;
    padding-bottom: 0;
  }
}

.seckill {
  padding: 20rpx !important;

  :deep(.selling__line) {
    margin-top: 0;
  }
}

@include b(line) {
  padding: 32rpx 20rpx 20rpx;
  margin: 0 20rpx;
  margin-top: 38rpx;
  border-radius: 16rpx;
  background: #fff;
  @include e(name) {
    font-size: 32rpx;
    line-height: 52rpx;
    color: #1e1c1c;
  }

  @include e(ctx) {
    @include flex(space-between);
    margin-top: 18rpx;
  }
  @include e(ctx-left) {
    font-size: 26rpx;
    @include flex();
    @include m(title) {
      font-weight: bold;
    }
    @include m(item) {
      color: #7c7c7c;
      margin-left: 30rpx;
      font-weight: bold;
    }
    @include m(red) {
      color: #fb375e;
    }
  }
  @include e(ctx-right) {
    color: #7c7c7c;
    & > .icon {
      margin-right: 8rpx;
    }
  }
  @include e(desc) {
    padding: 16rpx;
    background: rgb(228, 228, 228);
    border-radius: 10rpx;
    color: #6c6a6a;
    word-break: break-all;
  }
}

@include b(u-cell) {
  padding: 0 20rpx;
  margin-top: 5rpx;
  margin-bottom: 5rpx;
}

@include b(detail) {
  @include e(title) {
    margin-top: 20px;
    color: #5e5e5e;
    font-weight: bold;
    text-align: center;
  }
}

@include b(shopInfo) {
  @include flex();
  @include e(title) {
    width: 400rpx;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    font-weight: 700;
    font-size: 28rpx;
    color: #1a1a1a;
  }
  @include e(img) {
    margin-right: 32rpx;
  }
  @include e(tips) {
    font-size: 24rpx;
    color: #9b9b9b;
  }
}

@include b(assess) {
  color: red;
}

@include b(parameter) {
  @include e(header) {
    text-align: center;
    line-height: 44rpx;
    font-size: 32rpx;
    color: #000;
    font-weight: bold;
    padding-top: 24rpx;
  }
  @include e(item) {
    margin-top: 40rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
    line-height: 40rpx;
    font-weight: bold;
    @include m(top) {
      color: #f54319;
    }
    @include m(bottom) {
      color: #666;
      margin-top: 20rpx;
    }
  }
}

@include b(cell) {
  @include e(title) {
    font-size: 28rpx;
    font-weight: bold;
    color: #000;
  }
}

@include b(active-sku) {
  border-color: #e31436;
}

@include b(goods-card) {
  margin: 20rpx;
  overflow: hidden;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
  @include e(content) {
    font-size: 28rpx;
    display: flex;
  }
  @include e(tag) {
    color: #fa3534;
  }
  @include m(title) {
    min-width: 100rpx;
    color: #999999;
  }
  @include m(text) {
    display: inline-block;
    margin-right: 20rpx;
  }
  @include m(value) {
    color: #333;
    word-break: break-all;
  }
}

@include b(see-title) {
  padding: 0rpx 20rpx 20rpx;
  display: flex;
  font-size: 28rpx;
  align-items: center;
  justify-content: space-between;
}

@include b(see-more) {
  display: flex;
  font-size: 24rpx;
  align-items: center;
  color: #999999;
}

@include b(see-content) {
  margin-top: 10rpx;
  // width: 250rpx;
  height: 50rpx;
  @include flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  color: #767676;

  @include e(item) {
    border-bottom: 4px solid transparent;
    text-align: center;
    background: #fff;
    line-height: 50rpx;
  }
  @include e(line) {
    width: 27rpx;
    border: 1px solid rgb(246, 246, 246);
    transform: rotate(90deg);
    // margin-bottom: 32rpx;
    // margin-top: 32rpx;
  }
}

@include b(see-active) {
  color: rgb(245, 67, 25);
  // border-bottom: 4px solid rgb(245, 67, 25);
  font-weight: bold;
}

@include b(line-title) {
  font-weight: 700;
  padding: 20rpx 30rpx;
  @include flex;
  justify-content: flex-start;
  line-height: 25rpx;
  margin-top: 20rpx;
}

@include b(introduction-title) {
  font-size: 24rpx;
  color: #101010;
  padding: 20rpx 30rpx;
}

@include b(parameter-box) {
  padding: 20rpx 30rpx 30rpx 30rpx;
}

@include b(parameter-item) {
  @include flex(space-between);
  margin-bottom: 10rpx;
  @include e(name) {
    width: 200rpx;
    height: 80rpx;
    background-color: rgba(249, 249, 249, 1);
    color: rgba(16, 16, 16, 1);
    font-size: 28rpx;
    text-align: center;
    margin-right: 8rpx;
    line-height: 80rpx;
  }
  @include e(parame) {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    background-color: rgba(249, 249, 249, 1);
    color: rgba(16, 16, 16, 1);
    font-size: 28rpx;
    text-align: center;
    margin-right: 8rpx;
  }
}

@include b(shipping-instructions) {
  padding: 30rpx;
  color: #666666;
  font-size: 24rpx;
}

@include b(freight-unfree) {
  color: $qszr-red;
  &::before {
    content: '￥';
    color: $qszr-red;
  }
}

@include b(freight-unfree-beyond) {
  color: $qszr-red;
  margin-left: 5rpx;
  &::before {
    content: '';
  }
}

@include b(freight-free) {
  color: #333;
  &::before {
    content: '';
  }
}

@include b(line-describe) {
  margin-top: 20rpx;
  color: rgb(156, 155, 155);
  font-size: 24rpx;
  @include utils-ellipsis(1);
  width: 670rpx;
  height: 58rpx;
  line-height: 58rpx;
  border-radius: 4rpx;
  background: rgba(169, 169, 169, 0.08);
  padding: 0 20rpx;
}

:deep(.u-cell-item-box) {
  padding: 8rpx 0;
}
</style>
