<script setup lang="ts">
import { computed, ref, type PropType } from 'vue'
import Countdown from '@/pages/plugin/secKill/components/decorationComponents/countdown.vue'
import type { ApiSeckILLGoodsDetails } from '@/apis/plugin/secKill/model'
import type { StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { ActivityDetailVO, ProductResponse } from '@/apis/good/model'

const $props = defineProps({
  startTimel: {
    type: String,
    default: '',
  },
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
    required: true,
  },
})
const $emit = defineEmits(['end'])
const { divTenThousand } = useConvert()
const countdownRef = ref()
const start = ref(true)
// 倒计时时间
const getTime = computed(() => {
  const {
    time: { start: startTime, end: endTime },
  } = $props.activityInfo
  return $props.activitiesBegan ? endTime : startTime
})

const handleEnd = () => {
  $emit('end')
  start.value = false
}
</script>
<template>
  <view v-if="start" class="seckill-order">
    <view class="seckill-order__left">
      <view class="seckill-order__left_topPrice">
        秒杀至<text class="seckill-order__left_topPrice--price">
          <text v-if="!$props.activitiesBegan">???</text>
          <text v-else>{{ divTenThousand(goodsInfo?.activity?.activityPrice) }}</text>
        </text>
      </view>
      <view class="seckill-order__left_footerPrice">
        <text class="seckill-order__left_footerPrice--price f14">
          {{ divTenThousand(goodsInfo.price.underlined) }}
        </text>
        <slot name="card" />
      </view>
    </view>
    <view class="seckill-order__right">
      <text class="seckill-order__right--title">限时秒杀</text>
      <view class="seckill-order__right--countdown">
        <text class="seckill-order__right--countdown-text">距{{ activitiesBegan ? '结束' : '开始' }} &nbsp;</text>
        <Countdown ref="countdownRef" :start-time="getTime" @end="handleEnd">
          <template #default="{ timeArr, day }">
            <text v-if="day" class="seckill-order__right--countdown-item">{{ day }}</text>
            <text v-if="day" class="seckill-order__right--countdown-pendant">:</text>
            <text class="seckill-order__right--countdown-item">{{ timeArr[0] }}</text>
            <text class="seckill-order__right--countdown-pendant">:</text>
            <text class="seckill-order__right--countdown-item">{{ timeArr[1] }}</text>
            <text class="seckill-order__right--countdown-pendant">:</text>
            <text class="seckill-order__right--countdown-item">{{ timeArr[2] }}</text>
          </template>
        </Countdown>
      </view>
      <text class="seckill-order__right--purchasing"
        >限<text v-if="!$props.activitiesBegan">???</text> <text v-else>{{ goodsInfo.activityStock }}</text> 件</text
      >
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(seckill-order) {
  @include flex(space-between);
  padding: 30rpx 35rpx 33rpx;
  width: 100%;
  height: 232rpx;
  color: #fff;
  font-size: 32rpx;
  background: linear-gradient(95.47deg, #ff0844 0%, #ffb199 100%);
  @include e(left) {
    @include flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
    height: 100%;
    flex: 1;
    @include m(price) {
      font-size: 32rpx;
      margin-right: 10rpx;
    }
    @include m(msg) {
      font-size: 26rpx;
    }
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
    @include flex;
    height: 100%;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-end;
    @include m(countdown) {
      @include flex;
    }
    @include m(countdown-text) {
      margin-right: 10rpx;
      line-height: 20rpx;
      font-size: 28rpx;
    }
    @include m(countdown-item) {
      background: #ff5176;
      border-radius: 6rpx;
      padding: 4rpx;
      color: #ffff;
      font-size: 24rpx;
      margin: 0 2px;
    }
    @include m(countdown-pendant) {
      color: #ff5176;
      margin: 0 4rpx;
    }
    @include m(purchasing) {
    }
  }
}
</style>
