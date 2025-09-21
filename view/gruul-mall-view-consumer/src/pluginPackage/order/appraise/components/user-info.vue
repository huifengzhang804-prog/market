<script setup lang="ts">
import { withDefaults } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { cropImg } from '@/utils'
const baseAvatar = cropImg(
  'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/s2b2c_2.0.8/2024/12/967564e7de4b0dd23f6b86f2f.png',
  150,
  150,
)
console.log(baseAvatar)

interface Props {
  name: string
  src: string
}
const $props = withDefaults(defineProps<Props>(), {
  name: '我',
  src: '',
})

const $userStore = useUserStore()

function initIdentity() {
  return $userStore.getterMemberInfo?.memberName
}
</script>

<template>
  <view class="user">
    <image class="user__image" :src="cropImg($props.src, 150, 150) || baseAvatar" />
    <view class="user__info">
      <text class="user__info--name">{{ $props.name }}</text>
      <view v-if="!!initIdentity()" class="user__info--identity">
        <u-icon name="level" />
        <text class="user__info--identity-text">{{ initIdentity() }}</text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(user) {
  @include flex(flex-start);
  @include e(image) {
    width: 88rpx;
    height: 88rpx;
    border-radius: 44rpx;
    margin-right: 25rpx;
  }
  @include e(info) {
    height: 88rpx;
    @include flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: space-around;
    @include m(name) {
      font-size: 26rpx;
      font-weight: Bold;
      color: #000000;
    }
    @include m(identity) {
      @include flex;
      margin-top: 2rpx;
      padding: 2rpx 15rpx;
      background: #333333;
      border-radius: 30rpx;
      text-align: CENTER;
      color: #fff6d3;
    }
    @include m(identity-text) {
      font-size: 15rpx;
    }
  }
}
</style>
