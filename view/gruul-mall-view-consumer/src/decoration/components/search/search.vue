<script setup lang="ts">
import { ref, type PropType } from 'vue'
import type { SearchFormData } from '../types'
// 热词接口
import { doGetHistoriesAndHotWords } from '@/apis/good'
import storage from '@/utils/storage'

const $props = defineProps({
  decorationProperties: {
    type: Object as PropType<SearchFormData>,
    default() {
      return {}
    },
  },
  shopId: {
    type: String as PropType<Long>,
    default: '0',
  },
  mode: { type: String as PropType<'default' | 'shop'>, default: 'default' },
})
// 搜索热词
const hotWords = ref($props?.decorationProperties?.keyWord || '请输入')
/**
 * 获取搜索热词
 */
const handleGetHotWords = () => {
  doGetHistoriesAndHotWords().then(({ code, data }) => {
    if (code !== 200) return
    if (data?.hotWords?.[0]) {
      hotWords.value = data.hotWords[0]
    }
  })
}

handleGetHotWords()
/**
 * 点击搜索
 */
const handleSeachClick = () => {
  uni.navigateTo({
    url: `/basePackage/pages/searchRetrieve/SearchRetrieve?keyword=${hotWords.value}&shopId=0&isGoods=true`,
  })
  addStorage()
}

/**
 * 历史记录搜索放入storage
 */
function addStorage() {
  const value = hotWords.value
  if (!value || !value.trim()) {
    return
  }
  const historyWordStorage = storage.get('historyWords') || []
  const sameWordIndex = historyWordStorage.findIndex((word: string) => word === value)
  if (sameWordIndex !== -1) {
    historyWordStorage.splice(sameWordIndex, 1)
  }
  historyWordStorage.unshift(value)
  if (historyWordStorage.length > 6) historyWordStorage.pop()
  storage.set('historyWords', historyWordStorage)
}

const handleShowPop = () => {
  uni.navigateTo({
    url: `/basePackage/pages/searchPage/SearchPage?shopId=${$props.shopId}&formData=${JSON.stringify($props.decorationProperties)}`,
  })
}
</script>

<template>
  <view class="page">
    <view class="page__box van-search">
      <view
        class="page__header"
        :style="{
          color: $props.decorationProperties.color,
          background: $props.decorationProperties.background,
          'border-color': $props.decorationProperties.borderColor,
          'border-radius': `${$props.decorationProperties.borderRadius}px`,
          height: `${$props.decorationProperties.height}px`,
          lineHeight: `${$props.decorationProperties.height}px`,
          fontSize: `${$props.decorationProperties.height * 0.8}rpx`,
        }"
        @click="handleShowPop"
      >
        <u-icon name="search" class="page__header__icon" :size="`${$props.decorationProperties.height}rpx`"></u-icon>
        {{ hotWords }}
        <view
          :style="{
            'border-radius': `${$props.decorationProperties.btnBorderRadius || $props.decorationProperties.borderRadius}px`,
            fontSize: `${$props.decorationProperties.height}rpx`,
            color: $props.decorationProperties.btnFontColor,
            backgroundColor: $props.decorationProperties.btnBorderColor,
          }"
          class="page__text"
          @click.stop="handleSeachClick"
        >
          搜&nbsp;索
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page {
  &__header {
    display: inline-block;
    display: flex;
    align-items: center;
    font-size: 14px;
    width: 100%;
    border: 1px solid transparent;
    position: relative;
    overflow: hidden;
    height: 72rpx;
    &__icon {
      font-size: 14px;
      margin: 0 10px;
    }
  }

  &__box {
    width: 100%;
    padding: 5px 20rpx;
    display: flex;
  }
  &__text {
    position: absolute;
    right: 6rpx;
    top: 6rpx;
    bottom: 6rpx;
    background-color: red;
    color: #fff;
    padding: 0 32rpx;
    font-size: 24rpx;
    display: flex;
    align-items: center;
  }
  .page__item {
    width: 33%;
    padding: 7px 10px;
    float: left;
    text-align: center;

    &--word {
      display: block;
      width: 80%;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      border-radius: 20px;
      background: #f5f5f5;
      font-size: 14px;
      padding: 10px 10px;
      text-align: center;
    }
  }
}
</style>
