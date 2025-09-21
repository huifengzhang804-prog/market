<script lang="ts" setup>
import { ref, watch, type PropType } from 'vue'
import QPrice from '@/components/q-price/index.vue'
import type { StorageProducts } from '@/pages/modules/car/types'
import { handleParams } from '@/utils/goodsUtils'

const $props = defineProps({
  info: {
    type: Object as PropType<StorageProducts>,
    default() {
      return {}
    },
  },
})
const allSpecs = ref('')
watch(
  () => $props.info,
  (val) => {
    if (val) {
      const { specs = [], productFeaturesValue = [] } = val
      allSpecs.value = [...specs, ...handleParams(productFeaturesValue)].join('；')
    }
  },
  {
    immediate: true,
  },
)
</script>

<template>
  <view class="good">
    <u-image :height="200" :src="$props.info.image" :width="200" border-radius="8" />
    <view class="good__info">
      <view class="good__info--title">{{ $props.info.productName }}</view>
      <view v-if="$props.info.specs || $props.info.productFeaturesValue" class="good__info--spec">{{ allSpecs }}</view>
      <view class="good__info--bottom">
        <view class="good__info--price">
          <QPrice :price="$props.info.salePrice" font-size="34rpx" unit="¥" />
        </view>
        <view class="good__info--count">{{ $props.info.num }}</view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(good) {
  @include flex(space-between, flex-start);
  margin-bottom: 20rpx;
  @include e(info) {
    flex: 1;
    padding-left: 20rpx;
    @include m(title) {
      width: 380rpx;
      font-size: 28rpx;
      color: #000;
      line-height: 33rpx;
      @include utils-ellipsis(2);
    }
    @include m(spec) {
      font-size: 24rpx;
      color: #999;
      margin-top: 16rpx;
      margin-bottom: 32rpx;
      @include utils-ellipsis(4);
    }
    @include m(bottom) {
      @include flex(space-between);
    }
    @include m(price) {
      font-size: 34rpx;
      color: #f83f30;
    }
    @include m(count) {
      font-size: 24rpx;
      color: #121212;
      &::before {
        content: 'x';
        display: inline-block;
        font-size: 20rpx;
      }
    }
  }
}
</style>
