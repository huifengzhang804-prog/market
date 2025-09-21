<template>
  <view>
    <O2oSwiper :swiper-list="swiperList" :skus="skuGroup.skus" :info="goodInfo" @choosed-sku-change="changeSku" />
    <!-- 秒杀 -->
    <!-- <GoodsDetailsSecodsKill
      v-if="activityType === '秒杀'"
      :activitiesBegan="activitiesBegan"
      :activityInfo="activityInfo"
      :current-choosed-sku="currentChoosedSku"
      :goods-info="goodInfo"
      @end="refreshPage"
    /> -->
    <O2oCommodityLine
      ref="commodityLineRef"
      :current-choosed-sku="currentChoosedSku"
      :current-params="currentParams"
      :info="goodInfo"
      :sku="skuGroup.skus"
      :o2o-shop-id="o2oShopId"
    />

    <!-- 进店看看  s  -->
    <view id="shop" class="goods-card">
      <CommodityInShop :shopInfo="shopInfo" />
    </view>
    <!-- 进店看看  e  -->

    <!-- 看了又看  s  -->
    <view id="see" class="goods-card see-main">
      <view class="see-title">
        <view class="see-content">
          <view class="see-content__item" :class="{ 'see-active': seeActive === 'SEE' }" @click.stop="seeActive = 'SEE'">看了又看</view>
          <view class="see-content__line" />
          <view class="see-content__item" :class="{ 'see-active': seeActive === 'SALES' }" @click.stop="seeActive = 'SALES'"> 排行榜 </view>
        </view>
        <view class="see-more" @click="handleSeeAll">
          更多
          <view style="transform: rotate(180deg); margin: 2rpx 0 0 4rpx">
            <q-icon name="icon-zuojiantou" size="20rpx" />
          </view>
        </view>
      </view>
      <RankingList :shop-id="shopInfo.shopId" :see-active="seeActive" @see-all="handleSeeAll" />
    </view>
    <!-- 看了又看  e  -->
    <CardSetMeal :shop-id="shopInfo.shopId" :set-meal-list="setMealList" />
    <!-- 商品详情 s -->
    <template v-if="goodInfo.detail">
      <view id="detail" class="goods-card">
        <view class="line-title">商品详情</view>
        <view>
          <rich-text :nodes="formatRichText(goodInfo.detail)"></rich-text>
        </view>
      </view>
    </template>
    <!-- 商品详情 e -->

    <!-- 底部购物车 s -->
    <CarBar ref="carBarRef" :deliveryMoney="deliveryMoney" :shopInfo="o2oShopInfo" />
    <!-- 底部购物车 e -->
    <view style="height: 100rpx"></view>
  </view>
</template>
<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { ref, type Ref, computed, provide, type ComputedRef, nextTick } from 'vue'
import O2oSwiper from '@/pluginPackage/o2o-goods/components/o2o-swiper.vue'
import O2oCommodityLine from '@/pluginPackage/o2o-goods/components/o2o-commodity-line.vue'
import hooksOfCoupon from '@/pluginPackage/coupon/hooks/getCouponInfo'
import CommodityInShop from '@/pluginPackage/goods/commodityInfo/components/commodity-in-shop.vue'
import RankingList from '@/pluginPackage/goods/commodityInfo/components/ranking-list.vue'
import CarBar from './components/car-bar.vue'
import { doGetShopInfo } from '@/apis/o2oshop'
import formatRichText from '@/pluginPackage/utils/formatRichText'
// import GoodsDetailsSecodsKill from '@pluginPackage/scondsKill/components/goods-details-secods-kill.vue'
import { requestGoodsInfo } from '@/pluginPackage/goods/commodityInfo/hooks/request'
import { DGoodInfo, DCurrentChoosedSku, DSkuGroup } from '@/pluginPackage/goods/commodityInfo/hooks/defaultDispatcher'

import type { CartApiCouponVO, GetCouponListParams } from '@/apis/plugin/coupon/model'
import type { ApiGoodSkuCombination, StorageSku, ProductResponse, SetMealBasicInfoVO, ProductSpecsSkusVO, ActivityDetailVO } from '@/apis/good/model'
import useMember from '@/composables/useMember'
import CardSetMeal from '../setMeal/components/cardSetMeal.vue'
import type { TParamsSetMealBasicInfo } from '../setMeal/apis/model/api-params-types'
import { doGetSetMealBasicInfo } from '../setMeal/apis'
import { GeometryType } from '@/apis/address/type'
import { useLocationStore } from '@/store/modules/location'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'
import { ORDER_TYPE } from '@/pluginPackage/order/confirmOrder/types'

const { doRequest, memberPrice, isPaidMember } = useMember()

doRequest()

const { divTenThousand } = useConvert()
const productIds = ref('')
const shopIds = ref('')
const currentChoosedSku = ref<StorageSku>(DCurrentChoosedSku())
const skuGroup = ref<ProductSpecsSkusVO>(DSkuGroup())
const goodInfo = ref<ProductResponse>(DGoodInfo())
const swiperList = ref<string[]>([])
const o2oShopId = ref()
const o2oShopInfo = ref<GetShopInfoRes>()
const shopInfo = ref({
  shopId: '',
  shopLogo: '',
  shopName: '',
  newTips: '',
  status: '',
  shopType: '' as 'ORDINARY' | 'SELF_OWNED' | 'PREFERRED',
  followCount: '0' as Long,
  score: '',
})

// 活动是否开始
const activitiesBegan = computed(() => {
  return !!(goodInfo.value.activityOpen && goodInfo.value.activity?.activityId)
})

const activityType = computed(() => {
  return ORDER_TYPE[activityInfo.value.type]
})

// 活动信息
const activityInfo: ComputedRef<ActivityDetailVO> = computed(() => {
  if (goodInfo.value?.activity) {
    return goodInfo.value.activity
  }
  return {
    type: 'COMMON',
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

// 刷新页面
const refreshPage = async () => {
  await initGoodsInfo(productIds?.value, shopIds?.value)
  await nextTick()
}

async function initShopInfo(shopId: string) {
  const { code, data, msg } = await doGetShopInfo({ shopId, type: 'PRODUCT_DETAIL' })
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取店铺信息失败'}`, icon: 'none' })
  if (data) {
    const { name: shopName, logo: shopLogo, shopType, follow, status, score } = data
    o2oShopInfo.value = data
    shopInfo.value.shopId = shopId
    shopInfo.value.status = status
    shopInfo.value.shopName = shopName
    shopInfo.value.shopLogo = shopLogo
    shopInfo.value.shopType = shopType
    shopInfo.value.score = score
    shopInfo.value.followCount = follow || '0'
  }
}
const setMealList = ref<SetMealBasicInfoVO[]>([])

/**
 * 套餐基本信息接口
 */
const handleGetSetMealBasicInfo = async (params: TParamsSetMealBasicInfo) => {
  const { code, data, msg } = await doGetSetMealBasicInfo(params)
  if (code !== 200 || !data) return uni.showToast({ title: msg || '获取套餐基本信息失败', icon: 'none' })
  setMealList.value = data
}

onLoad((res) => {
  if (res) {
    const { goodId, shopId } = res
    productIds.value = res?.goodId.split('?')[0] ? res?.goodId.split('?')[0] : goodId
    shopIds.value = res?.shopId.split(',')[0] ? res?.shopId.split(',')[0] : shopId
    initGoodsInfo(goodId, shopId)
    o2oShopId.value = shopId
    initShopInfo(shopId)
    getIntraCityDistribution(shopId)
    handleGetSetMealBasicInfo({ shopId, productId: goodId })
  }
})

// 初始化商品信息
const initGoodsInfo = async (goodId: string, shopId: string) => {
  // 获取商品信息 当前商品的sku + 添加足迹
  const { currentSku, goodsSku, goodsInfo } = await requestGoodsInfo({ goodId, shopId })
  if (currentSku) {
    currentChoosedSku.value = currentSku
  }
  if (goodsSku) {
    skuGroup.value = goodsSku
  }
  if (goodsInfo) {
    goodInfo.value = goodsInfo
  }
  swiperList.value = goodsInfo.albumPics
  swiperList.value.push(goodsInfo.pic)
  initCoupon(currentChoosedSku)
}
// 优惠券列表
const couponList = ref<CartApiCouponVO[]>([])
// 优惠券优惠后的价格
const forecastPrice = ref<string | number | DecimalType>(0)

// 优惠券优惠的价格
const discountAmount = ref<string | number | DecimalType>(0)
let pInitGoodsDetailCouponList: (isLoad: boolean, options: GetCouponListParams) => Promise<void>
// 优惠券
const initCoupon = async (currentChoosedSku: Ref<StorageSku>) => {
  const { doRequest, goodsDetailCouponList, initGoodsDetailCouponList } = hooksOfCoupon(currentChoosedSku)
  const prime: any = await doRequest()
  couponList.value = goodsDetailCouponList.value
  pInitGoodsDetailCouponList = initGoodsDetailCouponList
  if (prime) {
    discountAmount.value = prime.discountAmount
    forecastPrice.value = +basePrice.value - prime.discountAmount
    return
  }
  forecastPrice.value = basePrice.value
}

// 更改sku
const changeSku = (sku: StorageSku) => {
  swiperList.value = [swiperList.value[0] !== sku.image ? sku.image : goodInfo.value.pic]
  currentChoosedSku.value = sku
  initCoupon(currentChoosedSku)
}

// 当前参数
const currentParams = ref<string[]>([])

// 当前sku的价格
const basePrice = computed(() => currentChoosedSku.value.salePrice)

// 会员优惠的价格
const memberPriceC = computed(() => {
  return +divTenThousand(basePrice.value) - +memberPrice(basePrice.value)
})
const handleCouponList = (isLoad: boolean, options: GetCouponListParams) => {
  pInitGoodsDetailCouponList(isLoad, options)
}

// SEE 看了又看 SALES 销量排行
const seeActive = ref<'SEE' | 'SALES'>('SEE')
/**
 * 查看全部 进店铺
 */
const handleNavToShop = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${o2oShopId.value}`,
  })
}
const handleSeeAll = () => {
  handleNavToShop()
}

const { getterLocation } = useLocationStore()

/**
 * 获取起送金额
 */
const deliveryMoney = ref<Long>(0)
async function getIntraCityDistribution(id: string) {
  if (getterLocation?.coordinates) {
    const { code, data, msg } = await doGetShopInfo({
      shopId: id,
      location: {
        type: GeometryType.Point,
        coordinates: getterLocation.coordinates,
      },
      type: 'SHOP_HOME',
    })
    if (code !== 200) {
      uni.showToast({ title: msg || '获取起送金额失败', icon: 'none' })
      return
    }
    deliveryMoney.value = divTenThousand(data?.minLimitPrice || 0).toNumber()
  }
}
// 购物车
const carBarRef = ref<InstanceType<typeof CarBar>>()
// 更新购物车数据
const carBarRefresh = () => {
  carBarRef.value?.initCarData()
}
// 传递更新购物车数据方法
provide('refreshCar', carBarRefresh)

provide('comProvide', {
  forecastPrice,
  memberPriceC,
  isPaidMember,
  couponList,
  handleCouponList,
  memberPrice,
  discountAmount,
})
</script>

<style lang="scss" scoped>
@include b(spec) {
  box-sizing: border-box;
  padding: 50rpx 36rpx 10rpx;
  @include e(info) {
    width: 100%;
    display: flex;
    box-sizing: border-box;
    margin-bottom: 40rpx;
  }
  @include e(product) {
    flex: 1;
    margin-left: 10rpx;
    @include m(price) {
      font-size: 38rpx;
      font-weight: bold;
      color: #fa5555;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 30rpx;
      }
    }
    @include m(space) {
      font-size: 24rpx;
      color: #000000;
      @include utils-ellipsis(3);
    }
  }
  @include e(product-line) {
    font-size: 24rpx;
    font-weight: 400;
    @include m(stock) {
      height: 30rpx;
      line-height: 30rpx;
      margin: 20rpx 20rpx 0 0;
    }
    @include m(limit) {
      color: #fa5555;
    }
  }
  @include e(btn) {
    margin-top: 20rpx;
    background: linear-gradient(95.47deg, #fa3534 0%, #ff794d 78.13%);
    color: #fff;
    font-size: 28rpx;
    text-align: center;
    line-height: 80rpx;
    height: 80rpx;
    border-radius: 60rpx;
  }
  @include e(number) {
    margin-top: 40rpx;
    @include flex(space-between);
  }
}
@include b(goods-card) {
  margin: 20rpx;
  overflow: hidden;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
  @include e(content) {
    font-size: 24rpx;
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
  }
}
@include b(see-mian) {
  @include e(u-cell) {
    padding-left: 0;
    padding-bottom: 0;
  }
}
@include b(see-title) {
  padding: 20rpx;
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
  width: 250rpx;
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
    border: 1rpx solid #999999;
    transform: rotate(90deg);
    margin-bottom: 12rpx;
  }
}
@include b(see-active) {
  color: #fa3534;
  border-bottom: 4px solid #fa3534;
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
</style>
