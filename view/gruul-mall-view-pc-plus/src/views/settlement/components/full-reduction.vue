<template>
  <div class="shop-coupon">
    <div class="shop-coupon__line" :class="{ checked: !checkedCoupon?.id }" @click="handleChangeCoupon()">
      <span class="shop-coupon__line--label">不使用</span>
    </div>
    <div
      v-for="coupon in couponList"
      :key="coupon.id"
      class="shop-coupon__line"
      :class="{ checked: checkedCoupon?.id === coupon?.id }"
      @click="handleChangeCoupon(coupon)"
    >
      <span class="shop-coupon__line--label">{{ formatFullDiscountRules(coupon) }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { OrderType } from '../types'
import { doPostFullReductionConfirmOrder } from '@/apis/mycoupon'
import { formatFullDiscountRules } from '@/views/personalcenter/assets/types/mycoupon'
import useConvert from '@/composables/useConvert'
import { Decimal } from 'decimal.js'
import type { StoragePackage } from '@/views/shoppingcart/types'
import { CartFullReductionResponseParameters } from '../plugins/fullDiscount/model'
import type { FullReductionRules } from '@/views/settlement/plugins/fullDiscount/model'
import Storage from '@/libs/storage'

const storage = new Storage()

const { divTenThousand } = useConvert()
const checkedCoupon = ref<any>({})
const $props = defineProps({
  commodityItem: {
    type: Object as PropType<OrderType>,
    default: () => ({}),
  },
})
const $emit = defineEmits(['changeCoupon'])
const couponList = ref<FullReductionRules[]>([])
const pageConfig = reactive({ current: 1, size: 10, pages: 1 })
function fullRequestDataProcessing(products: StoragePackage['products']) {
  return products.map((product) => {
    const { productId, num, salePrice } = product
    const amount = new Decimal(salePrice).mul(num).toString()
    return { productId, amount }
  })
}
const getShopCouponList = async () => {
  const storagePackage = storage.getItem('commodityInfo')
  const fullDiscountRequestParams = storagePackage.map((item: StoragePackage) => {
    const { shopId, products } = item
    const productAmounts = fullRequestDataProcessing(products)
    return { shopId, productAmounts }
  })
  const { code, data } = await doPostFullReductionConfirmOrder(fullDiscountRequestParams)
  if (code === 200) {
    const currentRules = data.find(
      (item: CartFullReductionResponseParameters) => item.shopId === $props?.commodityItem?.shopId,
    ) as CartFullReductionResponseParameters
    couponList.value = currentRules?.fullReductionRules.map((v: FullReductionRules) => {
      return {
        ...v,
        ...currentRules,
        productType: currentRules?.productType,
      }
    })
  }
}
getShopCouponList()

defineExpose({ checkedCoupon })

const handleChangeCoupon = (couponInfo = {}) => {
  checkedCoupon.value = couponInfo
  // $emit('changeCoupon', couponInfo)
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
