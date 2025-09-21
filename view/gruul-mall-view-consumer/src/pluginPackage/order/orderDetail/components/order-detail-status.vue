<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { orderDetailIconConfig } from '@/hooks'
import DateUtils from '@/utils/date'

const $props = defineProps({
  updateTime: { type: String, default: '2022-08-12 09:38:48' },
  payTimeOut: { type: String, default: '0' },
  status: {
    type: String,
    default: '暂无状态',
  },
  rightShow: { type: Boolean, default: true },
  countDownShow: { type: Boolean, default: true },
  height: { type: [String, Number], default: '327' },
  countDownLeft: { type: String, default: '剩' },
  countDownRight: { type: String, default: '自动关闭' },
  info: { type: Object, default: () => {} },
})
const time = ref(0)
const dateTool = new DateUtils()
watch(
  () => $props.payTimeOut,
  (val) => {
    time.value = countdownTime() as number
  },
)
const isDown = computed(() => {
  return ['BUYER_COMMENTED_COMPLETED', 'SYSTEM_COMMENTED_COMPLETED'].includes($props.info?.shopOrders?.[0]?.shopOrderItems?.[0]?.packageStatus)
})
const statusDescMap: Obj = {
  待接单: '等待骑手接单',
  待到店: '骑手正赶往商家',
  待取货: '骑手正在店内取货',
  配送中: '骑手正在送货',
  配送异常: '配送异常，请联系商家处理',
}
const statusDesc = computed(() => {
  return statusDescMap[$props.status]
})

function countdownTime() {
  if (!$props.payTimeOut) return
  const createDate = dateTool.getTime($props.updateTime)
  const timeDifference = createDate + Number($props.payTimeOut) * 1000 - new Date().getTime()
  const time = timeDifference > 0 ? timeDifference : 0
  return time
}
</script>

<template>
  <view
    :style="{
      background: 'linear-gradient(135.00deg, rgb(253, 129, 33) 0%,rgb(254, 73, 9) 100%)',
      borderRadius: '30rpx 30rpx 0 0',
    }"
    class="status"
  >
    <view v-if="$props.rightShow" class="status_right">
      <q-icon :name="orderDetailIconConfig[status as keyof typeof orderDetailIconConfig]" color="#fff" size="66rpx" />
    </view>
    <div class="status_container">
      <view class="status_left">
        <view class="status_title">{{ isDown ? '已完成' : status }}</view>
        <view v-if="statusDesc" class="status_desc">{{ statusDesc }}</view>
      </view>
      <view v-if="$props.countDownShow && time && !['已关闭'].includes(status) && !isDown" class="status_time">
        {{ $props.countDownLeft }}
        <u-count-down :show-days="false" :timestamp="time" separator="zh" />
        {{ $props.countDownRight }}
      </view>
    </div>
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

@include b(status) {
  box-sizing: border-box;
  width: 100%;
  padding: 42rpx 52rpx 200rpx 42rpx;
  color: #fff;
  display: flex;
  align-items: center;
  .status_container {
    display: flex;
    flex-direction: column;
  }

  .status_right {
    text-align: center;
    margin: 0 30rpx;
  }
  .status_title {
    font-size: 36rpx;
    font-weight: bold;
  }
  .status_desc {
    font-size: 24rpx;
    font-weight: 500;
  }
  .status_time {
    margin-top: 10rpx;
    display: flex;
    align-items: center;
  }
}
</style>
