<template>
  <view style="height: 20rpx"></view>
  <scroll-view scroll-y :refresher-threshold="30" :style="{ height: scrollViewHeight }" @scrolltolower="emit('initList', true)">
    <view style="padding: 14rpx; background: #f2f2f2">
      <u-waterfall v-if="list.length" ref="waterFallRef" v-model="list" idKey="productId">
        <template #left="{ leftList }: { leftList: Shops[] }">
          <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleItemClick(item)">
            <u-lazy-load threshold="-450" border-radius="20" imgMode="scaleToFill" :image="item.logo" height="400rpx" :index="index"></u-lazy-load>
            <view class="shopInfo">
              <text
                v-if="item.shopType && item.shopType !== 'ORDINARY'"
                class="shopInfo__tag"
                :style="{ background: item.shopType === 'SELF_OWNED' ? '#fd0505' : '#7728f5' }"
              >
                {{ shopType[item.shopType] }}
              </text>
              <text class="shopInfo__name">
                {{ item.name }}
              </text>
            </view>
            <view class="see">进店看看</view>
          </view>
        </template>
        <template #right="{ rightList }: { rightList: Shops[] }">
          <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleItemClick(item)">
            <u-lazy-load threshold="-450" border-radius="20" :image="item.logo" :index="index"></u-lazy-load>
            <view class="shopInfo">
              <text
                v-if="item.shopType && item.shopType !== 'ORDINARY'"
                class="shopInfo__tag"
                :style="{ background: item.shopType === 'SELF_OWNED' ? '#fd0505' : '#7728f5' }"
              >
                {{ shopType[item.shopType] }}
              </text>
              <text class="shopInfo__name">
                {{ item.name }}
              </text>
            </view>
            <view class="see">进店看看</view>
          </view>
        </template>
      </u-waterfall>
      <u-loadmore v-if="list.length" :status="loadMoreStatus" />
      <q-empty v-if="!list.length" :title="emptyConfig.title" :background="emptyConfig.background" />
    </view>
  </scroll-view>
</template>

<script setup lang="ts">
import { type PropType, ref, computed } from 'vue'
import { useStatusBar } from '@/hooks'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { useVModel } from '@vueuse/core'
import type { Shops } from '@/apis/good/model'

const emptyConfig = {
  title: '店铺不存在',
  background: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220531/1ce266a5e3fb43a4ae9b764fd8a166df.png',
}

const shopType = {
  SELF_OWNED: '自营',
  PREFERRED: '优选',
}

const props = defineProps({
  list: {
    type: Array as PropType<any[]>,
    default: () => [],
  },
  loadMoreStatus: {
    type: String,
    default: 'nomore',
  },
})
const emit = defineEmits(['initList', 'update:list'])

const list = useVModel(props, 'list', emit)

const waterFallRef = ref()
const searchNode = uni.upx2px(70)
const sortNode = uni.upx2px(102)
const statusBarHeight = useStatusBar()
const systermInfo = uni.getSystemInfoSync()

// 可视区域滑动高度计算
const scrollViewHeight = computed(() => {
  return `${systermInfo.screenHeight - (searchNode + sortNode + statusBarHeight.value)}px`
})

/**
 * 跳转页面
 * @param {*} e
 */
const handleItemClick = (e: any) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${e.id}`,
  })
}

const refresh = () => {
  waterFallRef.value?.clear()
}

defineExpose({
  refresh,
})
</script>

<style scoped lang="scss">
@include b(search) {
  background: #fff;
  padding: 6rpx 16rpx 0 16rpx;
  // #ifndef MP-WEIXIN
  @include flex(flex-start);
  // #endif
}
@include b(shopInfo) {
  padding: 20rpx 10rpx 0;
  display: flex;
  align-items: center;
  @include e(tag) {
    padding: 0 10rpx;
    height: 40rpx;
    background-color: #fd0505;
    color: #fff;
    border-radius: 8rpx;
    font-size: 24rpx;
    line-height: 40rpx;
  }
  @include e(name) {
    width: 100rpx;
    margin-left: 10rpx;
    @include utils-ellipsis(1);
    font-weight: 700;
    line-height: 60rpx;
    flex: 1;
  }
}
.see {
  color: #fa3534;
  font-weight: 400;
  text-align: center;
  margin-bottom: 20rpx;
}
:deep(.u-lazy-item) {
  height: 400rpx !important;
}
</style>
