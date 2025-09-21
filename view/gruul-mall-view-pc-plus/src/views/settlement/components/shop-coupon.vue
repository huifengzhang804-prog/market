<template>
  <div class="shop-coupon">
    <div class="shop-coupon__line" :class="{ checked: !checkedCoupon?.couponUserId }" @click="handleChangeCoupon()">
      <span class="shop-coupon__line--label">不使用</span>
    </div>
    <div
      v-for="coupon in couponList"
      :key="coupon.id"
      class="shop-coupon__line"
      :class="{ checked: checkedCoupon?.couponUserId === coupon?.couponUserId }"
      @click="handleChangeCoupon(coupon)"
    >
      <span class="shop-coupon__line--label">{{ formattingCouponRules(coupon) }}</span>
      <span class="shop-coupon__line--discount">减{{ coupon?.discountAmount && divTenThousand(coupon?.discountAmount) }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { OrderType } from '../types'
import { doGetOrderShopCouponPage } from '@/apis/mycoupon'
import { formattingCouponRules } from '@/views/personalcenter/assets/types/mycoupon'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()
const checkedCoupon = ref<any>({})
const $props = defineProps({
  commodityItem: {
    type: Object as PropType<OrderType>,
    default: () => ({}),
  },
})
const couponList = ref<any[]>([])
const pageConfig = reactive({ current: 1, size: 10, pages: 1 })
const getShopCouponList = async () => {
  const { code, data } = await doGetOrderShopCouponPage({
    size: pageConfig.size,
    current: pageConfig.current,
    shopId: $props?.commodityItem?.shopId,
    productAmounts: $props.commodityItem?.products?.map((item) => ({
      productId: item?.productId,
      amount: item?.num * item?.salePrice,
    })),
  })
  couponList.value = [...couponList.value, ...(data.records || [])]
}
getShopCouponList()

defineExpose({ checkedCoupon })

const handleChangeCoupon = (couponInfo = {}) => {
  checkedCoupon.value = couponInfo
}
</script>

<style lang="scss" scoped>
@include b(shop-coupon) {
  @include e(line) {
    @include flex(space-between);
    padding: 8px 15px;
    line-height: 1.8;
    user-select: none;
    @include m(label) {
      font-weight: 600;
    }
    @include m(discount) {
      color: #f00;
      font-size: 1.1em;
    }
  }
  @include b(checked) {
    border: 1px solid #f00;
  }
}
</style>
