<template>
  <view class="position" :style="{ color: $props.decorationProperties.color }" @click="handleChooseLocation">
    <q-icon name="icon-dizhi" size="20px"></q-icon>
    <text v-if="$props.decorationProperties.showType === 'province'" class="position__address">{{ getterArea?.join('') }}</text>
    <text v-if="$props.decorationProperties.showType === 'city'" class="position__address">{{ getterArea?.filter((_, i) => i > 0)?.join('') }}</text>
    <text v-if="$props.decorationProperties.showType === 'cityDetails'" class="position__address"
      >{{ getterArea?.filter((_, i) => i > 0)?.join('') }} {{ getterAddress }}</text
    >
    <text v-if="$props.decorationProperties.showType === 'details'" class="position__address">{{ getterName || getterAddress }}</text>
    <q-icon name="icon-youjiantou_huaban1" size="20rpx"></q-icon>
  </view>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type { styleFormData } from '../types'
import { useLocationStore } from '@/store/modules/location'
import { storeToRefs } from 'pinia'
import { locToAddress } from '@/apis/amap'

const { getterArea, getterAddress, getterLocation, getterName } = storeToRefs(useLocationStore())

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<styleFormData>,
    default() {
      return {}
    },
  },
  mode: { type: String as PropType<'default' | 'shop'>, default: 'default' },
})

const handleChooseLocation = () => {
  // #ifndef H5
  uni.chooseLocation({
    latitude: getterLocation.value?.coordinates[1],
    longitude: getterLocation.value?.coordinates[0],
    success: (res) => {
      locToAddress(res)
    },
    fail: (err) => {
      console.log('err', err)
    },
  })
  // #endif
  // #ifdef H5
  uni.$emit('showChooseMap', true)
  // #endif
}
</script>

<style lang="scss" scoped>
@include b(position) {
  padding: 20rpx 20rpx;
  @include flex(space-between);
  color: #fff;

  @include e(address) {
    overflow: hidden;
    margin: 0 20rpx;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    flex: 1;
  }

  @include b(iconfont) {
    flex-shrink: 0;
  }
}
</style>
