<script lang="ts" setup>
import { ref, computed } from 'vue'
import HeadOperation from '../components/head-operation2.vue'
import CouponList from '../components/coupon-list2.vue'
import type { CouponDTO } from '../index'
import 'uno.css'
import { ElMessageBox } from 'element-plus'

const search = ref({
  status: '',
  type: '',
  keywords: '',
})
const couponListRef = ref()
const batchDisabled = computed(() => {
  if (!couponListRef.value) return true
  return !couponListRef.value.chooseList.length
})

const handleSearch = () => {
  couponListRef.value.initCouponList()
}
/**
 * 批量删除
 */
const handleDelCoupon = async () => {
  const isValidate = await ElMessageBox.confirm('确定删除这些优惠券吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (!isValidate) return
  const ids = couponListRef.value.chooseList.map((item: CouponDTO) => ({
    couponId: item.id,
    shopId: item.shopId,
  }))
  couponListRef.value.handleDelBatch(ids)
}
</script>

<template>
  <div class="q_plugin_container">
    <HeadOperation v-model="search" :showBatchBtn="false" :batch-disabled="batchDisabled" @search="handleSearch" @del-coupon="handleDelCoupon" />
    <CouponList ref="couponListRef" :search="search" />
  </div>
</template>

<style lang="scss" scoped></style>
