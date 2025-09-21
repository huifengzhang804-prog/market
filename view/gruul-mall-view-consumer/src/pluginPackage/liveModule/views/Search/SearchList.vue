<script setup lang="ts">
import { ref, reactive, toRefs } from 'vue'
import SearchInput from './search-input.vue'
import { onLoad } from '@dcloudio/uni-app'
import { STORAGE_KEY } from './config'
import SearchGoods from './search-goods.vue'
import SearchLive from './search-live.vue'

const searchRef = ref(null) as unknown as typeof SearchInput | null
const searchData = reactive({ keyword: '', isGoods: true, placeholder: '请填写关键词' })
const { keyword, isGoods, placeholder } = toRefs(searchData)
// 组件默认高度 单位 rpx
const search_input_height = 122

onLoad((res = { key: STORAGE_KEY.historyWords, words: '' }) => {
  isGoods.value = res.key === STORAGE_KEY.historyWords
  keyword.value = res.words
})

const handleCustom = () => {}
const handleFocus = () => {}
const getSuggests = () => {}
const handleBlur = () => {}
const changeGoods = () => {
  isGoods.value = !isGoods.value
  keyword.value = ''
  searchRef?.value?.setValue('')
}
</script>

<template>
  <view>
    <search-input
      ref="searchRef"
      v-model="keyword"
      :is-goods="isGoods"
      :placeholder="placeholder"
      @custom="handleCustom"
      @focus="handleFocus"
      @change="getSuggests"
      @blur="handleBlur"
      @search="handleCustom"
      @change-label="changeGoods"
    />
    <search-goods v-if="isGoods" :keyword="keyword" :used-height="search_input_height"></search-goods>
    <search-live v-else :keyword="keyword" :used-height="search_input_height"></search-live>
  </view>
</template>

<style scoped lang="scss"></style>
