<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import GoodsSave from '@/basePackage/pages/assessList/components/goodsSave.vue'
import ShopSave from '@/basePackage/pages/assessList/components/shopSave.vue'
import Auth from '@/components/auth/auth.vue'

const tabsSwiperData = reactive({
  list: [
    {
      name: '商品收藏',
    },
    {
      name: '店铺收藏',
    },
  ],
  scroll: false,
  activeColor: '#FD0505',
  currentIndex: 0,
  tabsChange: (e: number) => {
    console.log('e', e)
  },
})
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(80)
})
const GoodsSaveRef = ref()
const ShopSaveRef = ref()
const loadMore = () => {
  GoodsSaveRef.value?.loadMore()
}
</script>

<template>
  <u-tabs
    v-model="tabsSwiperData.currentIndex"
    style="height: 80rpx; position: fixed; left: 0; right: 0; z-index: 9999"
    :list="tabsSwiperData.list"
    :is-scroll="tabsSwiperData.scroll"
    active-color="#FD0505"
    @change="tabsSwiperData.tabsChange"
  >
  </u-tabs>
  <!-- tabs 占位 误删 -->
  <view style="height: 80rpx"></view>
  <!-- tabs 占位 误删 -->
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="loadMore">
    <view style="padding: 0 10rpx">
      <GoodsSave v-if="tabsSwiperData.currentIndex === 0" ref="GoodsSaveRef"></GoodsSave>
      <ShopSave v-else ref="ShopSaveRef"></ShopSave>
    </view>
  </scroll-view>
  <Auth></Auth>
</template>

<style scoped lang="scss"></style>
