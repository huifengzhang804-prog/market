<script setup lang="ts">
import { watch, type PropType } from 'vue'
import { DistributionMode, type ProductResponse } from '@/pluginPackage/goods/commodityInfo/types'
import type { DistributionKeyType } from '@/apis/good/model'
import { useVModel } from '@vueuse/core'

const $props = defineProps({
  list: {
    type: Array as PropType<ProductResponse['distributionMode']>,
    default: () => [],
  },
  show: {
    type: Boolean,
    default: true,
  },
  curDistributionMode: {
    type: String as PropType<DistributionKeyType>,
    default: 'EXPRESS',
  },
})
const emit = defineEmits(['update:curDistributionMode'])
const curDistributionMode = useVModel($props, 'curDistributionMode', emit)
watch(
  () => $props.list,
  (val) => {
    if (val.length) {
      handleClick(val[0])
    }
  },
  { immediate: true },
)

function handleClick(i: DistributionKeyType) {
  curDistributionMode.value = i
  curDistributionMode.value = i
}

function deliveryMethod() {
  if ($props.list?.indexOf('EXPRESS') !== -1) {
    curDistributionMode.value = 'EXPRESS'
  } else if ($props.list?.indexOf('INTRA_CITY_DISTRIBUTION') !== -1) {
    curDistributionMode.value = 'INTRA_CITY_DISTRIBUTION'
  } else if ($props.list?.indexOf('SHOP_STORE') !== -1) {
    curDistributionMode.value = 'SHOP_STORE'
  }
}
deliveryMethod()
</script>
<template>
  <view v-if="list.length">
    <view class="fontBold distribution">配送方式</view>
    <scroll-view scroll-x enhanced :show-scrollbar="false" style="white-space: nowrap">
      <view class="distribution_list">
        <text
          v-for="(i, index) in $props.list"
          :key="index"
          class="distribution_list__item f12"
          :class="{ active: i === curDistributionMode }"
          @click="handleClick(i)"
        >
          {{ DistributionMode[i] }}
        </text>
      </view>
    </scroll-view>
  </view>
</template>

<style scoped lang="scss">
@include b(distribution) {
  margin: 30rpx 0;
  font-size: 32rpx;
}
@include b(distribution_list) {
  @include flex(flex-start);
  @include e(item) {
    background: #f6f6f6;
    min-width: 152rpx;
    padding: 0 28rpx;
    height: 64rpx;
    text-align: center;
    line-height: 64rpx;
    color: #333;
    margin: 0 20rpx 20rpx 0;
    border: 1px solid transparent;
    border-radius: 10rpx;
  }
}
@include b(active) {
  color: rgb(245, 67, 25);
  border-color: rgb(245, 67, 25);
  background-color: rgba(245, 67, 25, 0.04);
}
</style>
