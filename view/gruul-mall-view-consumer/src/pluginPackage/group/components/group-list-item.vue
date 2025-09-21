<script setup lang="ts">
import { computed, inject, type PropType } from 'vue'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import DateUtils from '@/utils/date'
import type { ApiGroupListType } from '@/apis/plugin/group/model'
import type { comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'

const $props = defineProps({
  isAll: { type: Boolean, default: false },
  type: { type: String as PropType<keyof typeof typeString>, default: 'IMMEDIATE' },
  groupItem: {
    type: Object as PropType<ApiGroupListType>,
    default() {
      return {}
    },
  },
  chooseGroupIndex: {
    type: Number,
    default: 0,
  },
})
const $emit = defineEmits(['countEnd'])
const dateUtils = new DateUtils()
useCountdown(
  dateUtils.getTime($props.groupItem.openTime) + Number($props.groupItem.effectTimeout) * 60 * 1000,
  {
    immediate: true,
  },
  countEnd,
)
const getFontSize = computed(() => {
  return $props.isAll ? 'f12' : 'f14'
})
const comProvideGoodsUse = inject('comProvideGoodsUse') as comDispatcherType
const updateSetOperation = inject('updateSetOperation') as (key: any, value: any, isGroup?: boolean) => void

enum typeString {
  IMMEDIATE = '立即开团',
  JOIN = '立即加入',
}
const getStr = computed(() => {
  return typeString[$props.type]
})
/**
 */
const handleBatch = () => {
  if (comProvideGoodsUse) {
    const orderParam = comProvideGoodsUse.getParam(comProvideGoodsUse.productId.value)
    if (orderParam) {
      let extra = {
        userNum: $props.type === 'JOIN' ? $props.groupItem.totalNum : void 0,
        teamNo: $props.type === 'JOIN' ? $props.groupItem.teamNo : void 0,
      }
      orderParam.extra = extra
      // 修改下单类型
      const groupIndex = comProvideGoodsUse.TEAM.groupInfo.value.users.findIndex((item) => item === $props.chooseGroupIndex)
      comProvideGoodsUse.TEAM.groupIndex.value = groupIndex
      updateSetOperation('control', true)
      updateSetOperation('immediately', false)
      updateSetOperation('type', 'BUY')
      updateSetOperation('source', 'ACTIVITY', true)
      comProvideGoodsUse.addParam(orderParam)
    }
  }
}
function countEnd() {
  $emit('countEnd', $props.groupItem)
}
</script>

<template>
  <view :class="['group__list-item', getFontSize, $props.isAll && 'group__list-item--col']" :style="{ height: $props.isAll ? '68px' : '' }">
    <view class="group__list-item-left">
      <image class="group__list-item-left--img" :src="$props.groupItem.commanderAvatar"></image>
      <view :class="[getFontSize, $props.isAll && 'group__list-item-left--text']">{{ $props.groupItem.commanderNickname }}</view>
    </view>
    <view class="group__list-item-right">
      <view class="group__list-item-right--mg"
        ><text>还差</text><text class="group__red">{{ $props.groupItem.totalNum - $props.groupItem.currentNum }}人</text><text>拼成</text></view
      >
      <view v-if="$props.groupItem.canJoin" class="group__btn" @click="handleBatch">{{ getStr }}</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(group) {
  @include e(list-item) {
    @include flex(space-between);
    margin-bottom: 16rpx;
    @include m(col) {
      border-bottom: 2rpx solid #eeeeee;
    }
  }
  @include e(list-item-left) {
    @include flex;
    @include m(img) {
      width: 60rpx;
      height: 60rpx;
      border-radius: 50%;
      margin-right: 20rpx;
    }
    @include m(text) {
      width: 180rpx;
      @include utils-ellipsis;
    }
  }
  @include e(list-item-right) {
    @include flex;
    @include m(mg) {
    }
    @include m(sec) {
      margin-right: 10rpx;
      text-align: right;
    }
    @include m(time) {
      margin-top: 4rpx;
    }
  }
  @include e(red) {
    color: #f00707;
    font-weight: bold;
    margin: 0 4rpx;
  }
  @include e(btn) {
    margin-left: 36rpx;
    width: 140rpx;
    height: 54rpx;
    line-height: 54rpx;
    text-align: center;
    color: #fff;
    border-radius: 6rpx;
    background-color: rgba(222, 50, 36, 1);
    font-weight: bold;
  }
}
</style>
