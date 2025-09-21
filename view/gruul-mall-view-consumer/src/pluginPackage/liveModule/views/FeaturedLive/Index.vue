<script setup lang="ts">
import { ref } from 'vue'
import DiscoverSection from '@/pluginPackage/liveModule/views/components/liveList/discover-section.vue'
import FollowWidget from '@/pluginPackage/liveModule/views/components/liveList/follow-widget.vue'
import { useSettingStore } from '@/store/modules/setting'

const current = ref(0)
// 单位 rpx
const search_height = 122
const $settingStore = useSettingStore()

// 单位 rpx
const u_tabs_height = 80
const list = [
  {
    name: '关注',
  },
  {
    name: '发现',
  },
]

const handleNavToSearchPage = () => {
  uni.navigateTo({ url: '/pluginPackage/liveModule/views/Search/Index' })
}
const handelNavToHome = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
</script>

<template>
  <view>
    <view class="top_tabs">
      <u-tabs v-model="current" :list="list" :is-scroll="false" active-color="#fa3534"></u-tabs>
      <view class="search" :style="{ height: search_height + 'rpx' }" @click="handleNavToSearchPage">
        <view class="search__input">
          <u-icon name="search" size="40" color="#999999" />
          <text class="search__input--text">请输入关键词搜索</text>
        </view>
        <text>搜索</text>
      </view>
    </view>
    <follow-widget v-if="current === 0" :used-height="search_height + u_tabs_height" @change="current = 1"></follow-widget>
    <discover-section v-if="current === 1" :used-height="search_height + u_tabs_height" @change="handelNavToHome"></discover-section>
  </view>
</template>

<style scoped lang="scss">
.top_tabs {
  position: fixed;
  top: 0;
  z-index: 9;
}
@include b(search) {
  width: 100vw;
  background: #fff;
  @include flex;
  @include e(input) {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding: 16rpx 28rpx;
    width: 630rpx;
    height: 71rpx;
    background: #f2f2f2;
    border-radius: 52rpx;
    margin-right: 15rpx;
    @include m(text) {
      color: #bbbbbb;
    }
  }
}
</style>
