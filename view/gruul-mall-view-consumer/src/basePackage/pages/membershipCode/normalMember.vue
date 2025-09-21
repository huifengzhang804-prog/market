<template>
  <view class="normal">
    <view class="normal__header">
      <image :src="userInfo.avatar" mode="aspectFill" class="normal__header--avatar" />
      <view class="normal__header--info">
        <view class="username">
          <text class="username__nickname">{{ userInfo.nickname }}</text>
          <view class="username__level">
            <image :src="cropImg($getCdnUrl('/image/crown.png'), 113, 46)" class="username__level--img" mode="aspectFill" />
            <text class="username__level--text">{{ memberInfo?.rankCode && 'VIP' + memberInfo.rankCode }}</text>
          </view>
        </view>
      </view>
      <view class="normal__header--level">
        <image :src="cropImg($getCdnUrl('/image/VIP.png'), 155, 148)" class="level-pic" />
        <text class="level-text">{{ memberInfo?.rankCode && 'VIP' + memberInfo.rankCode }}</text>
      </view>
    </view>
    <view class="normal__code">
      <u-qrcode
        ref="qrcodeRef"
        canvas-id="qrcode"
        size="215"
        :value="userInfo.userId"
        :options="{ areaColor: 'rgba(0, 0, 0, 0)', foregroundColor: '#fff' }"
      />
    </view>
    <view class="normal__copy">
      <text>{{ userInfo.userId }}</text>
      <text @click="copyUserId">复制</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { computed, watch } from 'vue'
import { useUserStore } from '@/store/modules/user/index'
import { cropImg } from '@/utils'
import UQrcode from '@/asyncPackages/uqrcode/components/u-qrcode/u-qrcode.vue'

const userInfo = computed(() => useUserStore().userInfo.info)
const memberInfo = computed(() => useUserStore().getterMemberInfo)
const copyUserId = () => {
  uni.setClipboardData({
    data: userInfo.value.userId || '',
  })
}
</script>

<style lang="scss" scoped>
@include b(normal) {
  background: linear-gradient(108.81deg, #8ea0ff 2.1%, #0011aa 98.37%);
  border-radius: 20rpx;
  height: 761rpx;
  box-sizing: border-box;
  padding: 48rpx 55rpx 40rpx 65rpx;
  color: #fff;
  @include e(header) {
    @include flex(space-between);
    @include m(avatar) {
      width: 110rpx;
      height: 110rpx;
      border-radius: 50%;
      flex-shrink: 0;
    }
    @include m(info) {
      flex: 1;
      margin-left: 20rpx;
      @include b(username) {
        @include flex(flex-start);
        font-size: 39rpx;
        font-weight: 700;
        @include e(nickname) {
          max-width: calc(100% - 110upx - 38upx);
        }
        @include e(level) {
          width: 113rpx;
          height: 46rpx;
          margin-left: 12rpx;
          position: relative;
          @include m(img) {
            width: 100%;
            height: 100%;
            margin-top: 1px;
          }
          @include m(text) {
            position: absolute;
            color: #4f6e9d;
            font-size: 24rpx;
            right: 2rpx;
            top: 10rpx;
          }
        }
      }
    }
    @include m(level) {
      position: relative;
      width: 155rpx;
      height: 148rpx;
      flex-shrink: 0;
      @include b(level-pic) {
        width: 100%;
        height: 100%;
      }
      @include b(level-text) {
        position: absolute;
        top: 95rpx;
        left: 0;
        right: 0;
        text-align: center;
        line-height: 29rpx;
        font-size: 24rpx;
        color: #fff;
      }
    }
  }
  .normal__code {
    @include flex();
    margin-top: 40rpx;
    @include m(img) {
      width: 430rpx;
      height: 430rpx;
    }
  }
  .normal__copy {
    margin-top: 20rpx;
    font-size: 26rpx;
    text-align: center;
    line-height: 36rpx;
    text + text {
      margin-left: 40rpx;
    }
  }
}
</style>
