<script setup lang="ts">
import { ref, inject, computed, type PropType, type Ref } from 'vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import PPTag from '@/pluginPackage/o2o-goods/components/o2o-goods-preferential-popup/p-p-tag.vue'
import PriceDescribe from '@/pluginPackage/o2o-goods/components/o2o-goods-preferential-popup/price-describe.vue'
import QRoundDefault from '@/components/q-btns/q-round-default.vue'
import PopupLoading from '@/components/popup/popup-loading.vue'
import CouponList from '@plugin/coupon/components/goodsCouponPopup/coupon-list.vue'

import { doPostConsumerCollectCoupon } from '@pluginPackage/coupon/apis/index'
import Empty from '@pluginPackage/coupon/components/empty/empty.vue'
import type { StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { CartApiCouponVO } from '@/apis/plugin/coupon/model'

const props = defineProps({
  show: { type: Boolean, default: false },
  shopId: {
    type: String,
    required: true,
  },
  sku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
})
const emit = defineEmits(['update:show'])

const $comProvide = inject('comProvide') as {
  couponList: Ref<CartApiCouponVO[]>
  handleCouponList: any
  memberPrice: any
  discountAmount: Ref<string>
}
// const $parentCoupon = $comProvide.discountPlugin['addon-coupon'] as CouponDispatcherType
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
const { mulTenThousand } = useConvert()

const basePrice = computed(() => {
  return +mulTenThousand($comProvide.memberPrice(props.sku.salePrice)) - +$comProvide.discountAmount.value
})
/**
 * 领取当前优惠券
 */
const handleReceiveCoupon = async (coupon: CartApiCouponVO) => {
  loading.value = true
  try {
    const { code, msg } = await doPostConsumerCollectCoupon(coupon.shopId, coupon.id)
    if (code === 200) {
      const currentCoupon = $comProvide.couponList.value.find((item: any) => item.id === coupon.id)
      if (currentCoupon) {
        currentCoupon.couponUserId = '1'
      }
      uni.showToast({ title: '领取成功', icon: 'none' })
    } else {
      uni.showToast({
        title: msg || '领取失败',
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
  <u-popup v-model="show" mode="bottom" border-radius="30" @touchmove.stop @close="handleSelected">
    <popup-title title="优惠促销" @close="handleSelected" />
    <popup-loading :show="loading" />
    <scroll-view
      scroll-y
      class="coupon-view"
      style="height: 800rpx; width: 100%"
      @scrolltolower="$comProvide.handleCouponList(true, { productId: sku.productId, shopId: sku.shopId, amount: sku.salePrice })"
      @touchmove.stop
    >
      <view class="p20">
        <view class="forecast">
          <p-p-tag :price="'' + basePrice" />
        </view>
        <price-describe :price="sku.salePrice" />
        <!-- 促销活动 s -->
        <!-- 促销活动 e -->
        <!-- 优惠券 s-->
        <coupon-list :coupon-list="$comProvide.couponList.value" @receive-click="handleReceiveCoupon" />
        <empty :show="!$comProvide.couponList.value.length && !loading" height="350"></empty>
        <!-- 优惠券 e-->
      </view>
    </scroll-view>
    <view style="height: 134rpx" />
    <q-round-default text="确认" :box-height="100" @click="handleSelected" />
  </u-popup>
</template>

<style scoped lang="scss">
@include b(forecast) {
  text-align: center;
}

@include b(coupon-view) {
  height: calc(100% - 280rpx);
  width: 100%;
}

.p20 {
  padding: 0 20rpx;
}
</style>
