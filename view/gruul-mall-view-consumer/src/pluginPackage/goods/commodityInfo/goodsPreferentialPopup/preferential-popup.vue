<script setup lang="ts">
import { computed, inject, ref, type PropType } from 'vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import PPTag from '@/pluginPackage/goods/commodityInfo/goodsPreferentialPopup/p-p-tag.vue'
import QRoundDefault from '@/components/q-btns/q-round-default.vue'
import PriceDescribe from '@/pluginPackage/goods/commodityInfo/goodsPreferentialPopup/price-describe.vue'
import PopupLoading from '@/components/popup/popup-loading.vue'
import CouponList from '@plugin/coupon/components/goodsCouponPopup/coupon-list.vue'
import { doPostConsumerCollectCoupon } from '@pluginPackage/coupon/apis/index'
import Empty from '@pluginPackage/coupon/components/empty/empty.vue'
import type { StorageSku, comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'
import type { CartApiCouponVO, CouponDispatcherType } from '@/apis/plugin/coupon/model'
import type { Shop } from '@/apis/shops/type'

const props = defineProps({
  show: { type: Boolean, default: false },
  shopInfo: {
    type: Object as PropType<Shop>,
    default: () => {},
  },
  sku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
})
const emit = defineEmits(['update:show'])
const $comProvide = inject('comProvide') as comDispatcherType
const $parentCoupon = $comProvide.discountPlugin['addon-coupon'] as CouponDispatcherType
const loading = ref(false)
const show = computed({
  get() {
    return props.show
  },
  set(value) {
    emit('update:show', value)
    return value
  },
})

/**
 * 领取当前优惠券
 */
const handleReceiveCoupon = async (coupon: CartApiCouponVO) => {
  loading.value = true
  try {
    const { code, msg } = await doPostConsumerCollectCoupon(coupon.shopId, coupon.id)
    if (code === 200) {
      const currentCoupon = $parentCoupon.goodsDetailCouponList.value.find((item) => item.id === coupon.id)
      if (currentCoupon) {
        currentCoupon.couponUserId = '1'
      }
      await $comProvide.initGoodsInfo()
      uni.showToast({ title: '领取成功', icon: 'none' })
    } else {
      uni.showToast({
        title: `${msg || '领取失败'}`,
        icon: 'none',
      })
    }
  } catch (error) {
    console.log('error', error)
  }
  loading.value = false
}
const handleSelected = () => {
  emit('update:show', false)
}
</script>

<template>
  <u-popup v-model="show" mode="bottom" border-radius="30" @close="handleSelected">
    <PopupTitle title="优惠促销" @close="handleSelected" />
    <PopupLoading :show="loading" />
    <scroll-view
      scroll-y
      style="height: 800rpx; width: 100%"
      @scrolltolower="$comProvide.getProductCoupons(true, { productId: sku.productId, shopId: shopInfo.shopId, amount: sku.salePrice })"
      @touchmove.stop
    >
      <view class="p20">
        <view class="forecast">
          <PPTag :price="$comProvide.forecastPrice.value as string" />
        </view>

        <!-- 价格计算详情 -->
        <PriceDescribe />

        <!-- 优惠券 s-->
        <CouponList :coupon-list="$comProvide.discountMap.value.COUPON?.data" :name="shopInfo?.shopName" @receive-click="handleReceiveCoupon" />
        <Empty :show="!$comProvide.discountMap.value.COUPON?.data.length && !loading" height="350"></Empty>
        <!-- 优惠券 e-->
      </view>
    </scroll-view>
    <scroll-view>
      <view>
        <view style="height: 220rpx" />
        <q-round-default text="确认" @click="handleSelected" />
      </view>
    </scroll-view>
  </u-popup>
</template>

<style scoped lang="scss">
@include b(forecast) {
  text-align: center;
}
.p20 {
  padding: 0 20rpx;
}
</style>
