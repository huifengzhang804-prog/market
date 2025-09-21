<script setup lang="ts">
import { ref } from 'vue'
import CouponCardList from '@pluginPackage/coupon/components/coupon-card-list.vue'
import Auth from '@/components/auth/auth.vue'

const cardRef = ref<InstanceType<typeof CouponCardList> | null>()
</script>

<template>
  <CouponCardList ref="cardRef" :coupon-center="true" show-stock />
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
</style>
