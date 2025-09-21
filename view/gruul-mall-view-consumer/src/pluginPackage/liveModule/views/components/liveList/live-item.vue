<script setup lang="ts">
import { type PropType, computed } from 'vue'
import LiveItemStatus from './live-item-status.vue'
import { doPostReservationLiveRoom } from '@/pluginPackage/liveModule/apis/UserLive'
import type { Joint_Live_List_Status } from '@/pluginPackage/liveModule/apis/CreateLive/model'

const $props = defineProps({
  liveTitle: {
    type: String,
    default: '',
  },
  shopName: {
    type: String,
    default: '',
  },
  image: {
    type: String,
    default: '',
  },
  liveId: {
    type: String,
    default: '',
  },
  shopId: {
    type: String,
    default: '',
  },
  beginTime: {
    type: String,
    default: '',
  },
  index: { type: Number, default: 0 },
  makeAnAppointment: { type: Boolean, default: false },
  isReservation: { type: Boolean, default: false },
  active: { type: String as PropType<Joint_Live_List_Status>, default: 'PROCESSING' },
})
const emit = defineEmits(['reservation-live', 'init-list'])
const time = computed(() => {
  let title = ''
  const setTime = new Date($props.beginTime)
  const nowTime = new Date()
  const restSec = setTime.getTime() - nowTime.getTime()
  const day = parseInt(String(restSec / (60 * 60 * 24 * 1000)))
  if (day >= 0) {
    switch (day) {
      case 0:
        {
          title = '今天'
        }
        break
      case 1:
        {
          title = '明天'
        }
        break
      case 2:
        {
          title = '后天'
        }
        break
      default:
        {
          title = day + '天后'
        }
        break
    }
  } else {
    title = '未知'
  }
  return {
    time: $props.beginTime.split(' ')[1],
    title,
  }
})

const handleClickMakeAnAppointment = async (isReserva: boolean, liveId: string, shopId: Long) => {
  const { code, data, msg } = await doPostReservationLiveRoom({ isReservation: isReserva, liveId, shopId })
  if (code !== 200) {
    uni.showToast({ title: `${msg || '操作失败'}`, icon: 'none' })
    return
  }
  emit('init-list')
}
</script>

<template>
  <view class="imgae">
    <u-lazy-load threshold="-450" border-radius="10" :image="image"></u-lazy-load>
    <live-item-status :active="active" />
  </view>
  <view class="good good-right">
    <view class="good__content">
      <view class="good__content--title u-line-1">{{ shopName }}</view>
      <view class="good__content-price">
        <view class="good__content-price--count u-line-1">
          {{ liveTitle }}
        </view>
      </view>
      <view v-if="makeAnAppointment" class="good__content-price">
        <view>
          {{ time.title }}<text style="color: #e50c00">{{ time.time }}</text>
        </view>
        <view v-if="!isReservation" class="good__content--btn" @click.stop="handleClickMakeAnAppointment(true, liveId, shopId)">
          <text>预约</text>
        </view>
        <view v-else class="good__content--btn MakeAnAppointment" @click.stop="handleClickMakeAnAppointment(false, liveId, shopId)">
          <text>已预约</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
$color: #fa3534;
@include b(imgae) {
  position: relative;
}
@include b(good) {
  border-radius: 0 0 16rpx 16rpx;
  margin-bottom: 10rpx;
  background-color: #ffffff;
  padding: 20rpx;
  position: relative;
  @include e(content) {
    @include m(title) {
      margin-top: 5px;
      font-size: 24rpx;
      color: #000000;
    }
    @include m(btn) {
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      padding: 6rpx 20rpx;
      flex-shrink: 0;
      height: 46rpx;
      /* 主色 */
      background: $color;
      border-radius: 60rpx;
      color: #fff;
    }
  }
  @include e(content-price) {
    margin: 10rpx 0 4rpx 0;
    @include flex(space-between);
    @include m(count) {
      font-size: 22rpx;
      color: #9a9a9a;
    }
  }
}
@include b(good-left) {
  margin-left: 0;
}
@include b(good-right) {
  margin-right: 0;
}
.MakeAnAppointment {
  background: #fff;
  border: 1px solid $color;
  color: $color;
  padding: 6rpx 10rpx;
}
</style>
