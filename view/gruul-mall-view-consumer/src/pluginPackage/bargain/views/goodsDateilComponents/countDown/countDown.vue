<script setup lang="ts">
import { ref, computed, inject, type PropType } from 'vue'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import type { BargainHelpPeopleListRes } from '@/pluginPackage/bargain/apis/model'
import type { StorageSku, comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'
import type { ActivityDetailVO, ProductResponse } from '@/apis/good/model'

const $props = defineProps({
  currentChoosedSku: {
    type: Object as PropType<StorageSku>,
    default() {
      return {}
    },
  },
  // true 活动进行时 / false 预热
  activitiesBegan: {
    type: Boolean,
    default: true,
  },
  activityInfo: {
    type: Object as PropType<ActivityDetailVO>,
    required: true,
  },
  goodsInfo: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
})
const { divTenThousand } = useConvert()
const countdownTime = computed(() => {
  const {
    time: { start: startTime, end: endTime },
  } = $props.activityInfo
  return $props.activitiesBegan ? endTime : startTime
})
// console.log(countdownTime.value)
// 倒计时时间
const { timeSet } = useCountdown(countdownTime.value, { immediate: true }, () => {
  $emit('end')
})

const $emit = defineEmits(['end'])
</script>

<template>
  <view class="count_down">
    <view class="count_down__left">
      <view class="count_down__left_topPrice">
        砍价至<text class="count_down__left_topPrice--price">
          {{ goodsInfo.activityOpen && goodsInfo.skuActivity ? divTenThousand(activityInfo.activityPrice) : '???' }}
        </text>
      </view>
      <view class="count_down__left_footerPrice">
        <text class="count_down__left_footerPrice--price f14">
          {{ currentChoosedSku.salePrice && divTenThousand(currentChoosedSku.salePrice) }}
        </text>
        <slot name="card" />
      </view>
    </view>
    <view class="count_down__right">
      <view class="count_down__right--title">全民砍价</view>
      <view class="count_down__right_time">
        <text class="count_down__right_time--text">距{{ activitiesBegan ? '结束' : '开始' }}</text>
        <text class="count_down__right_time--num">{{ timeSet.days }}</text>
        <text style="color: #ff5176; margin: 0 4rpx">:</text>
        <text class="count_down__right_time--num">{{ timeSet.hours }}</text>
        <text style="color: #ff5176; margin: 0 4rpx">:</text>
        <text class="count_down__right_time--num">{{ timeSet.minutes }}</text>
        <text style="color: #ff5176; margin: 0 4rpx">:</text>
        <text class="count_down__right_time--num">{{ timeSet.seconds }}</text>
      </view>
      <view class="count_down__right_stock">限{{ goodsInfo.activityStock || '???' }}件</view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(count_down) {
  @include flex(space-between);
  height: 232rpx;
  width: 100%;
  background: linear-gradient(95.47deg, #ff0844 0%, #ffb199 100%);
  padding: 30rpx 35rpx 33rpx;
  color: #fff;
  font-size: 32rpx;
  @include e(left) {
    height: 100%;
    @include flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
  }
  @include e(left_topPrice) {
    color: #fff;
    @include m(price) {
      font-size: 45rpx;
      font-weight: 700;
      &::before {
        content: '￥';
        display: inline-block;
        vertical-align: baseline;
        font-size: 32rpx;
      }
    }
  }
  @include e(left_footerPrice) {
    @include flex;
    justify-content: flex-start;
    @include m(price) {
      color: #fff;
      margin-right: 20rpx;
      text-decoration-line: line-through;
      &::before {
        content: '￥';
        display: inline-block;
        vertical-align: baseline;
        transform: scale(0.7);
      }
    }
  }
  @include e(right) {
    color: #ffff;
    height: 100%;
    @include flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-end;
    @include m(title) {
      font-family: PMZDBiaoTi-regular;
      margin-right: 5px;
    }
  }

  @include e(right_time) {
    @include flex;
    font-size: 24rpx;
    @include m(text) {
      margin-right: 10rpx;
      line-height: 20rpx;
      font-size: 28rpx;
    }
    @include m(num) {
      background: #ff5176;
      border-radius: 6rpx;
      padding: 4rpx;
      margin: 0 2px;
    }
  }
  @include e(right_stock) {
    margin-right: 15px;
  }
}
</style>
