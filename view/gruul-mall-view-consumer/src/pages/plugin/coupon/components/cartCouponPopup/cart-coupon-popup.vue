<script setup lang="ts">
import { ref, reactive, computed, type PropType, watch } from 'vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import CouponLoading from '@plugin/coupon/components/loading/coupon-loading.vue'
import CouponList from '@plugin/coupon/components/goodsCouponPopup/coupon-list.vue'
import { doGetCouponList, doPostConsumerCollectCoupon } from '@/apis/plugin/coupon'
import type { ProductAmount, ApiCouponVO, CartApiCouponVO } from '@/apis/plugin/coupon/model'

const pros = defineProps({
  show: { type: Boolean, default: false },
  shopId: {
    type: String as PropType<Long>,
    default: '0',
  },
  name: {
    type: String,
    default: '',
  },
  filterStock: {
    type: Boolean,
    default: false,
  },
  productAmounts: {
    type: Array as PropType<ProductAmount[]>,
    default: () => [],
  },
})
const emit = defineEmits(['update:show'])
const show = computed({
  get() {
    return pros.show
  },
  set(value) {
    emit('update:show', value)
    return value
  },
})
const pageConfig = reactive({ current: 1, size: 10, pages: 1 })
const coupons = ref<CartApiCouponVO[]>([])
const loading = ref(true)
const customStyle = {
  width: '570rpx',
  height: '78rpx',
  fontSize: '28rpx',
  textAlign: 'CENTER',
  color: '#ffffff',
  borderRadius: '50rpx',
  lineHeight: '78rpx',
}

watch(
  () => pros.show,
  (val) => {
    if (val) {
      initCouponList().then(() => {})
    }
  },
  {
    immediate: true,
  },
)

async function initCouponList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    coupons.value = await getCouponList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    coupons.value = coupons.value.concat(await getCouponList())
  }
}
async function getCouponList() {
  loading.value = true
  try {
    const { code, data, msg } = await doGetCouponList({
      isPlatform: false,
      size: pageConfig.size,
      current: pageConfig.current,
      shopId: pros.shopId,
      status: 'UNCLAIMED',
    })
    if (code !== 200) {
      loading.value = false
      return []
    }
    pageConfig.pages = data.pages
    loading.value = false
    return data.records.map((item: ApiCouponVO) => ({
      ...item,
      watermark: false,
    }))
  } catch (error) {
    loading.value = false
  }
}
const scrolltolower = () => {
  // console.log(' 滚动到底部/右边，会触发事件')
  initCouponList(true)
}
const handleSelected = () => {
  // initCouponList(true)
  coupons.value = []
  emit('update:show', false)
}
/**
 * 领取优惠券
 * @param {*} e
 */
const handleReceiveClick = async (e: CartApiCouponVO) => {
  // 调取领取优惠券的接口
  if (!e) {
    uni.showToast({ title: '该优惠券不存在', icon: 'none' })
    return
  }
  const { code } = await doPostConsumerCollectCoupon(pros.shopId, e.id)
  if (code !== 200) {
    uni.showToast({ title: '领取失败', icon: 'none' })
    return
  }
  uni.showToast({ title: '领取成功', icon: 'none' })
  e.couponUserId = '1'
}
</script>

<template>
  <u-popup v-model="show" mode="bottom" border-radius="30" height="700rpx" @close="handleSelected">
    <popup-title title="优惠券领取" @close="handleSelected" />
    <coupon-loading :show="loading" />
    <scroll-view scroll-y style="height: 590rpx; width: 100%" @touchmove.stop @scrolltolower="scrolltolower">
      <view v-if="!coupons.length && !loading" style="font-size: 250rpx; height: 550rpx; width: 750rpx">
        <u-empty text="暂无优惠券" mode="coupon" icon-size="250" />
      </view>
      <view class="p20">
        <!-- 优惠券 s-->
        <coupon-list :name="pros.name" :is-car="true" :coupon-list="coupons" title="可领取优惠券" @receive-click="handleReceiveClick" />
        <!-- 优惠券 e-->
      </view>
    </scroll-view>
    <!-- <view style="height: 134rpx" /> -->
    <!-- <view v-if="coupons.length" class="btn">
            <u-button :hair-line="false" :custom-style="customStyle" type="error" @click="handleSelected">确认</u-button>
        </view> -->
  </u-popup>
</template>

<style scoped lang="scss">
@include b(forecast) {
  text-align: center;
}
@include b(btn) {
  position: fixed;
  width: 100%;
  bottom: 0;
  left: 0;
  right: 0;
  height: 134rpx;
  @include flex;
}
.p20 {
  padding: 0 20rpx;
}
</style>
