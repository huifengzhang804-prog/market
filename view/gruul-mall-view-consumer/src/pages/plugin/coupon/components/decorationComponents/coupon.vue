<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { type PropType, computed, reactive, ref } from 'vue'
import { formattingCouponRules } from '@plugin/coupon/utils'
import { doGetCouponList, doPostConsumerCollectCoupon } from '@/apis/plugin/coupon'
import useConvert from '@/composables/useConvert'
import type { CartApiCouponVO, CouponConfig, CouponQueryStatusJointType } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<CouponConfig>,
    default() {
      return {}
    },
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})
const pageConfig = reactive({
  current: 1,
  size: 6,
  pages: 1,
})
const searchConfig = reactive<{
  shopId: Long
  isPlatform: boolean
  status: CouponQueryStatusJointType
}>({
  isPlatform: false,
  status: 'UNCLAIMED',
  shopId: $props.shopId,
})
const couponList = ref<CartApiCouponVO[]>([])

// onLoad((res = { shopId: '' }) => {
//     console.log('res---------', res)
//     if (res.shopId) {
//         searchConfig.shopId = res.shopId
//     }
// })
initCouponList()

const scrolltolower = () => {
  initCouponList(true)
}
const handleCollectCoupon = async (couponInfo: CartApiCouponVO) => {
  if (couponInfo.claimedStatus !== 'CLAIM') {
    return
  }
  const { code, msg } = await doPostConsumerCollectCoupon(couponInfo.shopId, couponInfo.id)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: `${msg ? msg : '领取失败'}`,
    })
  } else {
    uni.showToast({
      icon: 'none',
      title: '领取成功',
    })
  }
  initCouponList()
}
async function initCouponList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    couponList.value = await getCouponListFn()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    couponList.value = couponList.value.concat(await getCouponListFn())
  }
}
async function getCouponListFn() {
  if (searchConfig.shopId === '') {
    setTimeout(() => {
      getCouponListFn()
    }, 20)
    return
  }
  const { code, data, msg } = await doGetCouponList({ ...searchConfig, ...pageConfig })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取优惠券列表失败'}`, icon: 'none' })
    return []
  }
  pageConfig.pages = data.pages
  if ($props.decorationProperties.hideCoupon) {
    return data.records.filter((item: CartApiCouponVO) => item.claimedStatus !== 'FINISHED')
  }
  return data.records
}
const couponHandler = reactive({
  FINISHED: {
    bg: '#ccc',
    text: '已抢光',
  },
  CLAIM: {
    bg: $props.decorationProperties.bg,
    text: '立即领取',
  },
  LIMIT: {
    bg: '#ccc',
    text: '已领取',
  },
})
const computedCouponBg = computed(() => (item: CartApiCouponVO) => couponHandler[item.claimedStatus]?.bg)
const currentBgStyle = computed(() => (row: CartApiCouponVO) => {
  return {
    background: couponHandler[row.claimedStatus]?.bg,
  }
})
</script>

<script lang="ts">
export default {
  options: { styleIsolation: 'shared' },
}
</script>
<template>
  <scroll-view class="coupon" scroll-x enhanced :show-scrollbar="false" @scrolltolower="scrolltolower">
    <view v-for="item in couponList" :key="item.id" style="display: inline-block">
      <view class="coupon__item" :style="currentBgStyle(item)">
        <view class="coupon__item-left">
          <view
            :class="[['PRICE_DISCOUNT', 'REQUIRED_PRICE_DISCOUNT'].includes(item.type) ? 'coupon__item-left--reduce' : 'coupon__item-left--price']"
          >
            {{ ['PRICE_DISCOUNT', 'REQUIRED_PRICE_DISCOUNT'].includes(item.type) ? item.discount : useConvert().divTenThousand(item.amount) }}
          </view>
          <view class="coupon__item-left--row">{{ formattingCouponRules(item, false) }}</view>
        </view>
        <!-- 样式一 -->
        <view
          v-if="$props.decorationProperties.style === 'style1'"
          class="coupon__styleOne"
          :style="currentBgStyle(item)"
          @click="handleCollectCoupon(item)"
        >
          {{ couponHandler[item.claimedStatus].text }}
        </view>
        <!-- 样式二 -->
        <view v-else class="coupon__styleTwo" @click="handleCollectCoupon(item)">{{ couponHandler[item.claimedStatus].text }}</view>
      </view>
    </view>
  </scroll-view>
</template>
<style lang="scss" scoped>
@include b(coupon) {
  width: 100%;
  // height: 220rpx;
  font-size: 24rpx;
  color: #fff;
  white-space: nowrap;
  @include e(item) {
    position: relative;
    flex-shrink: 0;
    width: 280rpx;
    height: 160rpx;
    border-radius: 20rpx;
    margin-left: 30rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  @include e(item-left) {
    margin-left: 42rpx;
    @include m(price) {
      font-size: 48rpx;
      color: #fff;
      text-align: center;
      margin-bottom: 20rpx;
      &::after {
        content: '元';
        display: inline-block;
        vertical-align: baseline;
        font-size: 24rpx;
        margin-left: 6rpx;
      }
    }
    @include m(reduce) {
      font-size: 48rpx;
      color: #fff;
      text-align: center;
      margin-bottom: 20rpx;
      &::after {
        content: '折';
        display: inline-block;
        vertical-align: baseline;
        font-size: 24rpx;
        margin-left: 6rpx;
      }
    }
    @include m(row) {
      width: 120rpx;
      height: 20rpx;
    }
  }
  @include e(styleOne) {
    position: absolute;
    width: 96rpx;
    height: 160rpx;
    right: 0;
    top: 0;
    letter-spacing: 10rpx;
    background: green;
    writing-mode: vertical-lr;
    text-align: center;
    font-weight: bold;
    line-height: 96rpx;
    border-radius: 0 20rpx 20rpx 0;
    z-index: 1;
    clip-path: ellipse(96rpx 70% at right center);
  }
  @include e(styleOne-border) {
    content: '';
    width: 60rpx;
    height: 160rpx;
    border-radius: 50% 0 0 50%;
    background: green;
    position: absolute;
    top: 0;
    right: 36rpx;
  }
  @include e(styleTwo) {
    width: 80rpx;
    height: 160rpx;
    line-height: 80rpx;
    text-align: center;
    writing-mode: vertical-lr;
    border-radius: 0 20rpx 20rpx 0;
    border-left: 2rpx dashed #cdcdcd;
    position: relative;
    &::after {
      position: absolute;
      content: '';
      width: 32rpx;
      height: 32rpx;
      background: #fff;
      border-radius: 50%;
      left: -16rpx;
      bottom: -16rpx;
    }
    &::before {
      position: absolute;
      content: '';
      width: 32rpx;
      height: 32rpx;
      background: #fff;
      border-radius: 50%;
      left: -16rpx;
      top: -16rpx;
    }
  }
}
</style>
