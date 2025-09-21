<script setup lang="ts">
import { type PropType, inject } from 'vue'
import qIcon from '@/components/q-icon/q-icon.vue'
import type { CommodityItem } from '@/basePackage/pages/merchant/types'

const $props = defineProps({
  width: {
    type: String,
    default: '492rpx',
  },
  height: {
    type: String,
    default: '100rpx',
  },
  info: {
    type: Object as PropType<CommodityItem>,
    default() {
      return {}
    },
  },
})
const { divTenThousand } = useConvert()
const selectSpec = inject('selectSpec') as (info: string) => void
</script>

<template>
  <view class="classification" :style="{ height: $props.height, width: $props.width }">
    <view class="classification__title">{{ $props.info.name }}</view>
    <view class="classification__bottom">
      <view class="classification__bottom--price">{{ divTenThousand($props.info.salePrices[0]) }}</view>
      <view style="height: 70rpx; width: 70rpx; line-height: 70rpx; text-align: center" @click.stop="selectSpec(info.id)">
        <q-icon name="icon-gouwuche5" size="70" color="#FA3534" />
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(classification) {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding-left: 11rpx;
  // margin-right: 26rpx;
  @include e(title) {
    width: inherit;
    font-size: 14px;
    color: #323232;
    @include utils-ellipsis;
  }
  @include e(bottom) {
    width: inherit;
    padding-right: 40rpx;
    @include flex(space-between);
    @include m(price) {
      font-size: 16px;
      color: #dd3324;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 10px;
        color: #dd3324;
      }
    }
    @include m(img) {
      width: 22px;
      height: 22px;
    }
  }
}
@include b(redBtn) {
  width: 25px;
  height: 25px;
  background: linear-gradient(164deg, #f3f3f3, #e5382e, #fd4e26);
  box-shadow: 0px 2px 7px 0px rgb(255 14 0 / 27%);
  border-radius: 50%;
  @include flex();
  @include e(img) {
    width: 16px;
    height: 16px;
  }
}
</style>
