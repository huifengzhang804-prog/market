<script setup lang="ts">
import { computed, type PropType, reactive, ref, watch, watchEffect } from 'vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import PopupItem from '@pluginPackage/coupon/components/couponPopup/popup-item.vue'
import QRoundDefault from '@/components/q-btns/q-round-default.vue'
import { doGetOrderShopCouponPage } from '@pluginPackage/coupon/apis'
import CouponLoading from '@plugin/coupon/components/loading/coupon-loading.vue'
import type { ApiOrderCouponVO, ProductAmount, ProductTypeJointType } from '@/apis/plugin/coupon/model'

const $props = defineProps({
  show: { type: Boolean, default: false },
  shopId: {
    type: [String, Number],
    default: '0',
  },
  productAmounts: {
    type: Array as PropType<ProductAmount[]>,
    default: () => [],
  },
  images: { type: Array as PropType<{ id: Long; image: string }[]>, default: () => [] },
})
const emit = defineEmits(['update:show', 'chooseCoupon'])
const show = computed({
  get() {
    return $props.show
  },
  set(value) {
    emit('update:show', value)
    return value
  },
})
const pageConfig = reactive({ current: 1, size: 10, pages: 1 })
const groupValue = ref('reductionx')
const couponList = ref<ApiOrderCouponVO[]>([])
const animationData = ref({})
const imagesArr = computed(() => {
  if (groupValue.value === 'reductionx') {
    return $props.images
  }
  const coupon = couponList.value.find((item) => item.couponUserId === groupValue.value)
  if (!coupon) {
    return $props.images
  }
  return couponHandler[coupon.productType]($props.images, coupon.productIds)
})
const loading = ref(true)

watch(
  () => $props.show,
  (val) => {
    if (val) {
      initCouponList()
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
    couponList.value = await getCouponList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    couponList.value = unique(couponList.value.concat(await getCouponList()))
  }
}
/**
 * 数组去重
 * @param {*} arr
 */
function unique(arr: ApiOrderCouponVO[]) {
  const res = new Map()
  return arr.filter((a) => !res.has(a.couponUserId) && res.set(a.couponUserId, 1))
}
async function getCouponList() {
  loading.value = true
  const { code, data } = await doGetOrderShopCouponPage({
    size: pageConfig.size,
    current: pageConfig.current,
    productAmounts: $props.productAmounts,
    shopId: $props.shopId,
  })
  if (code !== 200) {
    loading.value = false
    return []
  }
  loading.value = false
  pageConfig.pages = data.pages
  return data.records
}
const scrolltolower = () => {
  initCouponList(true)
}
const couponHandler: Record<ProductTypeJointType, (products: { id: Long; image: string }[], ids: Long[]) => { id: Long; image: string }[]> = {
  ALL: (products) => products,
  SHOP_ALL: (products) => products,
  ASSIGNED: (products, ids) => products.filter((product) => ids.includes(product.id)),
  ASSIGNED_NOT: (products, ids) => products.filter((product) => !ids.includes(product.id)),
}
const handleGroupChange = (e: any) => {
  groupValue.value = e.detail.value
}
const handleSelected = () => {
  const coupon = groupValue.value === 'reductionx' ? null : couponList.value.find((item) => item.couponUserId === groupValue.value)
  emit('chooseCoupon', coupon)
  reset()
}
const reset = () => {
  emit('update:show', false)
  groupValue.value = 'reductionx'
  couponList.value = []
}
watchEffect(() => {
  if ($props.shopId !== '0' && imagesArr.value.length && couponList.value.length) {
    const animation = uni.createAnimation({
      duration: 1000,
      timingFunction: 'ease',
    })
    animation.height(0).height('150rpx').step()
    animationData.value = animation.export()
  } else {
    const animation = uni.createAnimation({
      duration: 1000,
      timingFunction: 'ease',
    })
    animation.height('150rpx').height(0).step()
    animationData.value = animation.export()
  }
})
defineExpose({ initCouponList })
</script>

<template>
  <u-popup v-model="show" mode="bottom" border-radius="30" height="810" @close="reset()">
    <popup-title @close="reset()" />
    <coupon-loading :show="loading" />
    <radio-group @change="handleGroupChange">
      <!-- <popup-item v-if="couponList.length" required-amount="500" :groupValue="groupValue" amount="20" radioName="reductions" price="30.00" /> -->
      <scroll-view scroll-y style="height: 600rpx; width: 100%" @scrolltolower="scrolltolower">
        <popup-item :radio-msg="false" radioName="reductionx" tag :groupValue="groupValue">
          不使用
          <template #radio>
            <radio value="reductionx" :checked="'reductionx' === groupValue" color="red" style="transform: scale(0.7)" />
          </template>
        </popup-item>
        <popup-item v-for="(item, index) in couponList" :key="index" :coupon="item" :groupValue="groupValue" :radioName="item.couponUserId">
          <template #radio>
            <radio :value="item.couponUserId" :checked="item.couponUserId === groupValue" color="red" style="transform: scale(0.7)" />
          </template>
        </popup-item>
        <view v-if="!couponList.length && !loading" style="font-size: 250rpx">
          <u-empty text="暂无优惠券" mode="coupon" font-size="30" style="height: 400rpx" />
        </view>
      </scroll-view>
    </radio-group>
    <q-round-default text="确认" @click="handleSelected" />
  </u-popup>
</template>

<style scoped lang="scss">
@include b(images) {
  display: inline-block;
  margin: 5rpx;
}
@include b(loading) {
  @include flex;
}
@include b(images-show) {
  animation: imagesShow 0.5s linear;
  @keyframes imagesShow {
    from {
      height: 0;
      opacity: 0;
    }
    to {
      height: 150rpx;
      opacity: 1;
    }
  }
}
@include b(images-heig) {
  animation: imagesheig 0.5s linear;
  @keyframes imagesheig {
    from {
      height: 150rpx;
      opacity: 1;
    }
    to {
      height: 0;
      opacity: 0;
    }
  }
}
</style>
