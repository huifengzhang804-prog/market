<script setup lang="ts">
import type { PropType } from 'vue'
const $props = defineProps({
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})
const handleSearch = () => {
  uni.navigateTo({ url: `/basePackage/pages/searchPage/SearchPage?shopId=${$props.shopId}` })
}
</script>
<template>
  <view class="search-box">
    <u-search
      :height="66"
      class="search-box__search"
      placeholder="请输入搜索内容"
      shape="round"
      :show-action="false"
      disabled
      @click="handleSearch"
    />
  </view>
</template>
<style lang="scss" scoped>
@include b(search-box) {
  padding: 0 16rpx 24rpx 28rpx;
  background: #fff;
  @include e(search) {
    font-size: 28rpx;
    width: 706rpx;
  }
}
</style>
