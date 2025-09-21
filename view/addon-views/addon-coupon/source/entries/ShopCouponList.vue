<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import HeadOperation from '../components/head-operation.vue'
import CouponList from '../components/coupon-list.vue'
import { couponStatusComputed } from '../index'
import { ElMessage, ElMessageBox } from 'element-plus'
import 'uno.css'

const search = ref({
  status: '',
  type: '',
  keywords: '',
})
const couponListRef = ref()
const router = useRouter()
const batchDisabled = computed(() => {
  if (!couponListRef.value) return true
  return !couponListRef.value.chooseList.length
})

const handleSearch = () => {
  couponListRef.value.initCouponList()
}

const handleDelCoupon = () => {
  const findDoing = couponListRef.value.chooseList.find((item) => couponStatusComputed(item) === '进行中')
  if (findDoing) {
    ElMessage({
      message: '进行中的优惠券无法删除',
      type: 'warning',
    })
    return
  }
  ElMessageBox.confirm('是否批量删除选中的优惠券', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const ids = couponListRef.value.chooseList.map((item) => item.id)
    couponListRef.value.handleDelBatch(ids)
  })
}
const handleAddCoupon = () => router.push('/coupons/baseInfo')
</script>

<template>
  <div class="q_plugin_container">
    <el-config-provider :empty-values="[undefined, null]">
      <HeadOperation v-model="search" @search="handleSearch"> </HeadOperation>
    </el-config-provider>
    <CouponList ref="couponListRef" :search="search" :batch-disabled="batchDisabled" @add-coupon="handleAddCoupon" @del-coupon="handleDelCoupon" />
  </div>
</template>
