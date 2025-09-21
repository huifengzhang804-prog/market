<template>
  <view class="super">
    <view class="super__header">
      <image :src="cropImg(userInfo.avatar, 110, 110)" mode="aspectFill" class="super__header--avatar" />
      <view class="super__header--info">
        <view class="username">
          <text class="username__nickname">{{ userInfo.nickname }}</text>
          <view class="username__level">
            <image :src="cropImg($getCdnUrl('/image/diamond.png'), 48, 48)" class="username__level--img" mode="aspectFill" />
          </view>
        </view>
        <view v-show="memberInfo?.memberCardValidTime" class="date">{{ memberInfo?.memberCardValidTime }}到期</view>
      </view>
      <view class="super__header--level">
        <image :src="cropImg($getCdnUrl('/image/SVIP.png'), 155, 148)" class="level-pic" />
        <text class="level-text">{{ memberInfo?.rankCode && 'SVIP' + memberInfo?.rankCode }}</text>
      </view>
    </view>
    <view class="super__code">
      <u-qrcode
        ref="qrcodeRef"
        canvas-id="qrcode"
        size="215"
        :value="userInfo.userId"
        :options="{ areaColor: 'rgba(0, 0, 0, 0)', foregroundColor: '#AA5200' }"
      />
    </view>
    <view class="super__copy">
      <text>{{ userInfo.userId }}</text>
      <text @click="copyUserId">复制</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/modules/user/index'
// @ts-ignore
import UQrcode from '@/asyncPackages/uqrcode/components/u-qrcode/u-qrcode.vue'
import { cropImg } from '@/utils'
const userInfo = computed(() => useUserStore().userInfo.info)
const memberInfo = computed(() => useUserStore().getterMemberInfo)
const copyUserId = () => {
  uni.setClipboardData({
    data: userInfo.value.userId || '',
  })
}
</script>

<style lang="scss" scoped>
@include b(super) {
  background: linear-gradient(108.69deg, #ffd25e 1.63%, #fff4cd 47.63%, #ffd540 98.4%);
  border-radius: 20rpx;
  height: 761rpx;
  box-sizing: border-box;
  padding: 48rpx 55rpx 40rpx 65rpx;
  color: #aa5200;
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
          max-width: calc(100% - 100upx);
        }
        @include e(level) {
          width: 48rpx;
          height: 48rpx;
          margin-left: 12rpx;
          position: relative;
          @include m(img) {
            width: 100%;
            height: 100%;
          }
        }
      }
      @include b(date) {
        margin-top: 16rpx;
        font-size: 26rpx;
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
        top: 87rpx;
        left: 0;
        right: 0;
        text-align: center;
        line-height: 29rpx;
        font-size: 24rpx;
        color: #fff;
      }
    }
  }
  .super__code {
    @include flex();
    margin-top: 40rpx;
    @include m(img) {
      width: 430rpx;
      height: 430rpx;
    }
  }
  .super__copy {
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
