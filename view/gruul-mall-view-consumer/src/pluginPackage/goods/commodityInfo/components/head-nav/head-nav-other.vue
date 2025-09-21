<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
import { useStatusBar } from '@/hooks/useStatusBar'
import { useNavBack } from '@/hooks/useNavBack'
import { useSettingStore } from '@/store/modules/setting'
import { ref } from 'vue'

const $settingStore = useSettingStore()
const statusBarHeight = useStatusBar()
const showMenu = ref(false)

const handleNavBack = () => {
  useNavBack()
}
const handleNavToCar = () => {
  $settingStore.NAV_TO_INDEX('购物车')
}
const handleShowMenu = () => {
  showMenu.value = !showMenu.value
}

defineExpose({
  handleShowMenu,
})
</script>

<template>
  <view class="nav" :style="{ height: statusBarHeight + 'px' }">
    <view class="nav__btn">
      <view class="nav__btn--arrow" @click="handleNavBack">
        <q-icon name="icon-zuojiantou" size="14px" color="#000" />
      </view>
      <view class="nav__btn--col"></view>
      <view class="nav__btn--menu" @click="handleShowMenu">
        <q-icon name="icon-caidan1" size="22px" color="#000" />
      </view>
    </view>
  </view>
  <view v-if="showMenu" class="nav__slide" :style="{ top: statusBarHeight + 'px' }">
    <view class="nav__slide--item">
      <q-icon name="icon-gouwucheshangcheng-xianxing" color="#101010" />
      <text class="nav__slide--text" @click="handleNavToCar">购物车</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(nav) {
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 99;
  display: flex;
  justify-content: flex-start;
  align-items: flex-end;
  padding: 0 0 8rpx 28rpx;
  @include e(btn) {
    width: 172rpx;
    height: 62rpx;
    border: 1px solid rgba(236, 236, 236, 1);
    border-radius: 50rpx;
    display: flex;
    align-items: center;
    background: #fff;
    @include m(col) {
      width: 1px;
      height: 40rpx;
      background: rgba(236, 236, 236, 1);
    }
    @include m(arrow) {
      width: 85rpx;
      height: 62rpx;
      @include flex;
    }
    @include m(menu) {
      width: 85rpx;
      height: 62rpx;
      @include flex;
    }
  }
  @include e(slide) {
    position: fixed;
    left: 28rpx;
    background: #fff;
    width: 204rpx;
    z-index: 99;
    padding: 0 8rpx;
    font-size: 24rpx;
    border: 1px solid #ececec;
    color: rgba(16, 16, 16, 1);
    @include m(item) {
      height: 90rpx;
      @include flex(flex-start);
      padding: 0 14rpx;
    }
    @include m(text) {
      margin-left: 20rpx;
    }
  }
}
</style>
