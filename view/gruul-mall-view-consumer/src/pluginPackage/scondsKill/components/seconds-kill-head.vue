<script lang="ts" setup>
import { ref, type PropType } from 'vue'
import { type SeckillRoundVO, SeckillQueryStatus, SeckillStatus } from '@/apis/plugin/secKill/model'
import Countdown from '@plugin/secKill/components/decorationComponents/countdown.vue'

defineProps({
  data: { type: Array as PropType<SeckillRoundVO[]>, default: () => [] },
  modelValue: { type: Object as PropType<SeckillRoundVO | null>, default: () => ({}) },
})
const emit = defineEmits(['update:modelValue', 'chooseSecondsKill', 'updateSecondsKill'])
const acitve = ref(0)
const countdownRef = ref()

function startTimeFormat(time: string) {
  return time.split(' ')[1], time.split(' ')[1].substring(0, 5)
}

const handleClick = (item: SeckillRoundVO, index: number) => {
  if (acitve.value === index) {
    return
  }
  updateCountdown()
  emit('update:modelValue', item)
  emit('chooseSecondsKill')
  acitve.value = index
}
const handleEnd = () => {
  emit('updateSecondsKill')
}
/**
 * 更新倒计时
 */
const updateCountdown = () => {
  if (countdownRef.value) {
    countdownRef.value.loadCountdown()
  }
}
defineExpose({ updateCountdown })
</script>

<template>
  <view class="seconds_skill_head_container">
    <view class="red_container">
      <scroll-view scroll-x enhanced :show-scrollbar="false" class="scroll_venue">
        <view class="venue_container">
          <view v-for="(item, index) in data" :key="index" class="venue_item" :class="{ active: acitve === index }" @click="handleClick(item, index)">
            <view :class="{ active_ing: item.status === SeckillQueryStatus.IN_PROGRESS }" class="venue_item_time">
              {{ startTimeFormat(item.time.start) }}
            </view>
            <view :class="{ active_ing: item.status === SeckillQueryStatus.IN_PROGRESS }" class="venue_item_status">
              {{ SeckillStatus[item.status].desc }}
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
    <view v-if="modelValue" class="seconds-kill-head__end-time">
      <u-icon color="#dddddd" name="minus" />
      <view class="seconds-kill-head__end-time--text">
        <text style="margin-right: 10rpx">
          {{ SeckillStatus[modelValue.status].timerDesc }}
        </text>
        <Countdown ref="countdownRef" :isStart="false" :startTime="SeckillStatus[modelValue.status].timePick(modelValue)" @end="handleEnd" />
      </view>
      <u-icon color="#dddddd" name="minus" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
.seconds_skill_head_container {
  height: 316rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-bottom: 24rpx;

  .red_container {
    background: linear-gradient(90deg, #e94927, #dd3324);
    height: 184rpx;
    display: flex;
    flex-direction: column;
    .scroll_venue {
      margin-top: auto;
      .venue_container {
        display: flex;
        align-items: center;
        .venue_item {
          position: relative;
          display: flex;
          flex-direction: column;
          align-items: center;
          margin: 0 35rpx;
          &.active::before {
            position: absolute;
            content: '';
            width: calc(100% + 70rpx);
            height: 100%;
            top: 0;
            right: 0;
            background: white;
            z-index: 1; /*背景色要在文字的下一层，以免挡住文字*/
            transform: translateX(35rpx) scaleY(1.45) perspective(100px) rotateX(30deg);
            transform-origin: bottom;
            // 左上右上的圆角
            border-radius: 20rpx 20rpx 0 0;
          }
          &.active {
            .venue_item_time,
            .venue_item_status {
              color: #333;
            }
            .active_ing {
              color: #f54319;
            }
          }
          .venue_item_time {
            font-size: 44rpx;
            font-weight: bold;
            color: white;
            z-index: 2;
          }
          .venue_item_status {
            font-size: 28rpx;
            color: rgba($color: #fff, $alpha: 0.7);
            z-index: 2;
          }
        }
      }
    }
  }
}
@include b(seconds-kill-head) {
  @include e(end-time) {
    color: #000;
    height: 80rpx;
    background: #ffffff;
    font-size: 24rpx;
    width: 100%;
    @include flex;
    @include m(text) {
      display: flex;
      align-items: center;
      margin: 0 20rpx;
    }
  }
}
</style>
