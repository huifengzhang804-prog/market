<script setup lang="ts">
import { ref } from 'vue'
import qService from '@/components/q-service/q-service.vue'

const $props = defineProps({
  bgColor: {
    type: String,
    default: '#fff',
  },
})

const keyword = ref('')
const isStyle = 2
const handleNavigateTo = () => {
  uni.navigateTo({
    url: `/basePackage/pages/searchPage/SearchPage?shopId=0&formData={}`,
  })
}
</script>
<template>
  <view style="display: flex; align-items: center; background: #fff">
    <view class="search-box" :style="{ background: bgColor }" @click="handleNavigateTo">
      <u-search
        v-model="keyword"
        readonly
        :height="60"
        class="search-box__search"
        placeholder="   请输入搜索内容"
        shape="round"
        :bgColor="bgColor && '#fff'"
        :show-action="false"
      />
    </view>
    <!-- <q-service :size="50" style="margin-bottom: 24rpx; margin-right: 25rpx" /> -->
  </view>
</template>
<style lang="scss" scoped>
@include b(search-box) {
  padding: 0 28rpx;
  display: flex;
  align-items: center;
  width: 100%;
  background: #fff;
  @include e(search) {
    font-size: 28rpx;
    width: 100%;
  }
}
</style>
