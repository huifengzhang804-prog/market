<script setup lang="ts">
import type { PropType } from 'vue'
import type { ApiMemberCardInfo } from '@/apis/plugin/member/model'

const $props = defineProps({
  memberInfo: { type: Object as PropType<ApiMemberCardInfo>, default: () => ({}) },
})
const { divHundred } = useConvert()

function convertText(item: any, key: any) {
  if (key === 'GOODS_DISCOUNT') {
    return `商品折扣${divHundred(item.extendValue)}折`
  } else if (key === 'INTEGRAL_MULTIPLE') {
    return `积分加${divHundred(item.extendValue)}倍`
  } else {
    return item.rightsName
  }
}
</script>

<template>
  <view v-if="memberInfo.currentMemberVO?.relevancyRights" class="interests">
    <view class="perm-title">已享权益</view>
    <view class="perm-container">
      <scroll-view scroll-x enhanced :show-scrollbar="false" style="white-space: nowrap; flex: 1; text-align: left; font-weight: 700">
        <u-tag
          v-for="(item, key) of memberInfo.currentMemberVO?.relevancyRights"
          :key="key"
          :text="convertText(item[0], key)"
          size="mini"
          bg-color="#E0C0A7"
          border-color="transparent"
          color="#101010"
          style="margin: 0 10rpx"
        ></u-tag>
      </scroll-view>
      <view class="perm-growup">更多权益可开通升级</view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(interests) {
  // align-items: center;
  margin-top: 80rpx;
  display: flex;
  justify-content: flex-start;
  flex-direction: column;
  color: #cccccc;
  font-size: 24rpx;
}
@include b(perm-title) {
  padding: 0 30rpx;
  text-align: left;
}
@include b(perm-container) {
  width: 100%;
  margin-top: 30rpx;
  padding: 0 30rpx;
  scroll-view {
    height: 100%;
  }
}
@include b(perm-growup) {
  color: #ff564b;
  font-size: 28rpx;
  text-align: left;
  padding-top: 30rpx;
}
</style>
