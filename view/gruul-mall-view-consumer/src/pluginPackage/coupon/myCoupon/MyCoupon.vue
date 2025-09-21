<script setup lang="ts">
import { ref, nextTick } from 'vue'
import ChooseCouponNav from '@pluginPackage/coupon/myCoupon/components/choose-coupon-nav.vue'
import CouponCardList from '@pluginPackage/coupon/components/coupon-card-list.vue'
import storage from '@/utils/storage'
import { useUserStore } from '@/store/modules/user'
import type { TagItem, CouponQueryStatusJointType } from '@/apis/plugin/coupon/model'
import Auth from '@/components/auth/auth.vue'

const current = ref(0)
const isPlatform = ref(true)
const cardRef = ref()
const total = ref<{ name: CouponQueryStatusJointType; total: number }>({ name: 'UNUSED', total: 0 })
const currentChoose = ref<CouponQueryStatusJointType>('UNUSED')
const $useUserStore = useUserStore()
const navList = [
  {
    name: '平台券',
  },
  {
    name: '店铺券',
  },
]

const change = () => {
  isPlatform.value = !!current.value
  total.value.name = currentChoose.value
  currentChoose.value = 'UNUSED'
  nextTick(async () => {
    total.value.total = (await cardRef.value.initGetCouponListFn()) || 0
    storage.set(currentChoose.value + current.value, { name: currentChoose.value, total: total.value.total })
  })
}
const handleActive = (e: TagItem) => {
  currentChoose.value = e.key
  total.value.name = currentChoose.value
  nextTick(async () => {
    total.value.total = (await cardRef.value.initGetCouponListFn()) || 0
    storage.set(currentChoose.value + current.value, { name: currentChoose.value, total: total.value.total })
  })
}
</script>

<template>
  <u-tabs
    v-model="current"
    :list="navList"
    :is-scroll="false"
    inactive-color="#000"
    :bar-style="{ backgroundColor: 'red' }"
    active-color="#000"
    @change="change"
  />
  <view style="margin: 20rpx 0">
    <ChooseCouponNav :total="total" :current="current" @active="handleActive" />
  </view>
  <CouponCardList ref="cardRef" :is-platform="isPlatform" :status="currentChoose" />
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
@include b(coupon-item) {
  margin: 10rpx 30rpx;
  &:nth-child(1) {
    margin-top: 30rpx;
  }
  &:last-child {
    padding-bottom: 30rpx;
  }
}
</style>
