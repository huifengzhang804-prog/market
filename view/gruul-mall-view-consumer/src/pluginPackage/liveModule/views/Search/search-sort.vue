<script setup lang="ts">
import { ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'

defineProps({
  height: {
    type: Number,
    default: 114,
  },
})
const currentIndex = ref(0)

// 排序列表
const sortList = [
  {
    title: '综合排序',
  },
  {
    title: '销售',
  },
  {
    title: '新品',
  },
]
const emit = defineEmits(['updateorderByParams', 'initList'])

// 更改排序
const handleChangeSort = (idx: number) => {
  if (idx >= sortList.length) {
    currentIndex.value = currentIndex.value === 3 ? 4 : 3
  } else {
    currentIndex.value = idx
  }
  if (currentIndex.value === 0) {
    // 综合排序
    emit('updateorderByParams', [enumSortKey[1], enumSortKey[2]])
  } else {
    emit('updateorderByParams', [enumSortKey[currentIndex.value]])
  }
  emit('initList')
}
// 排序key
const enumSortKey = [
  [
    { order: 'salesVolume', sort: 'DESC' },
    { order: 'createTime', sort: 'DESC' },
  ],
  { order: 'salesVolume', sort: 'DESC' },
  { order: 'createTime', sort: 'DESC' },
  { order: 'salePrices', sort: 'ASC' },
  { order: 'salePrices', sort: 'DESC' },
]
</script>

<template>
  <view class="sort" :style="{ height: height + 'rpx' }">
    <view
      v-for="(item, index) in sortList"
      :key="item.title"
      class="sort__tab"
      :class="{ active: currentIndex === index }"
      @click="handleChangeSort(index)"
    >
      {{ item.title }}
    </view>
    <view :class="{ active: currentIndex >= sortList.length }" class="sort__price sort__tab" @click="handleChangeSort(3)">
      <view style="margin-right: 10rpx">价格</view>
      <view class="sort__flex">
        <QIcon name="icon-shangjiantou1" :color="currentIndex === 3 ? '#F54319' : '#999999'" class="sort__top" />
        <QIcon name="icon-shangjiantou1" :color="currentIndex === 4 ? '#F54319' : '#999999'" class="sort__bottom" />
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(sort) {
  background: #fff;
  padding: 0 8px;
  @include flex(space-between);
  font-size: 24rpx;
  @include e(tab) {
    width: 171.5rpx;
    height: 64rpx;
    background: #f3f3f3;
    font-size: 28rpx;
    border-radius: 32rpx;
    text-align: center;
    line-height: 64rpx;
  }
  @include e(price) {
    @include flex();
  }
  @include e(flex) {
    display: flex;
    flex-direction: column;
  }
  @include e(bottom) {
    line-height: 14rpx;
    transform: rotate(180deg) scale(0.6);
  }
  @include e(top) {
    line-height: 14rpx;
    transform: scale(0.6);
  }
}
.active {
  color: #f54319;
  background: #ffe8e8;
  font-weight: bold;
}
</style>
