<script setup lang="ts">
import { ref, reactive, watch, toRefs } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import SearchHistory from './search-history.vue'
import { onHide, onUnload } from '@dcloudio/uni-app'
import { STORAGE_KEY, addStorage } from './config'
import { doGetSearchSuggest } from '@/apis/good'
import SearchInput from './search-input.vue'
import type { RetrieveParam } from '@/apis/good'

const searchParams = reactive<Partial<RetrieveParam>>({
  keyword: '',
  orderByParams: [
    { order: 'salesVolume', sort: 'DESC' },
    { order: 'createTime', sort: 'DESC' },
  ],
  categoryThirdId: '',
  platformCategoryThirdId: '',
  shopId: '0',
})
const suggestsParam = reactive({
  suggests: [] as Array<{ productName: string; highLight: string }>,
})
const setTimeoutArr: Array<NodeJS.Timeout> = []
const searchRef = ref(null) as unknown as typeof SearchInput | null
const hotPlaceholderData = reactive({
  placeholder: '请填写关键词',
  hotWords: [] as Array<string>,
  index: 0,
  isGoods: true,
  focus: false,
  historyWords: [] as Array<string>,
})
const { placeholder, hotWords, index, isGoods, focus, historyWords } = toRefs(hotPlaceholderData)
// 热词搜索
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
onUnload(() => clearTimeoutFn(setTimeoutArr))
// 单位 rpx
const search_top_height = 122
// 切换商品或者直播
const changeGoods = () => {
  clearTimeoutFn(setTimeoutArr)
  isGoods.value = !isGoods.value
  searchRef?.value?.setValue('')
  searchParams.keyword = ''
  if (!isGoods.value) {
    placeholder.value = '请填写关键词'
    hotWords.value = []
    suggestsParam.suggests = []
  }
}
const handleCustom = (value: string) => {
  const storageValue = getStorageValue(value)
  const storagekey = isGoods.value ? STORAGE_KEY.historyWords : STORAGE_KEY.liveWords
  historyWords.value = addStorage(storageValue, storagekey)
  uni.navigateTo({ url: `/pluginPackage/liveModule/views/Search/SearchList?key=${storagekey}&words=${storageValue}` })
}
function getStorageValue(value: string) {
  if (value) return value
  const storagekey = isGoods.value ? placeholder.value : value
  return storagekey
}
function sethotWordPlaceholder() {
  if (focus.value) return
  if (!hotWords.value.length) return
  placeholder.value = hotWords.value[index.value]
  index.value++
  timeout()
}
function timeout() {
  const time = setTimeout(() => {
    if (index.value === hotWords.value.length) {
      index.value = 0
    }
    sethotWordPlaceholder()
  }, 3000)
  setTimeoutArr.push(time)
}
const handleupdatehotWords = (hots: Array<string>) => {
  hotWords.value = hots
  sethotWordPlaceholder()
}
const handleFocus = () => {
  focus.value = true
}
const handleBlur = () => {
  focus.value = false
  const time = setTimeout(() => {
    sethotWordPlaceholder()
  }, 3000)
  setTimeoutArr.push(time)
}
const getSuggests = (value: string) => {
  if (!value) {
    suggestsParam.suggests = []
    return
  }
  if (isGoods.value) {
    const params: { keyword: string; shopId?: string } = {
      keyword: value,
    }
    doGetSearchSuggest(params).then(({ code, data }) => {
      if (code !== 200) {
        suggestsParam.suggests = []
        return
      }
      suggestsParam.suggests = data
    })
  }
}
/**
 * 选中关键词检索
 */
const handleChooseKey = (key: string) => {
  searchParams.keyword = (key ? key.trim() : '').substring(0, 20)
  handleCustom(searchParams.keyword)
}

function clearTimeoutFn(timeoutArr: typeof setTimeoutArr) {
  if (timeoutArr.length) {
    timeoutArr.forEach((item) => clearTimeout(item))
  }
}
</script>

<template>
  <view>
    <search-input
      ref="searchRef"
      v-model="searchParams.keyword"
      :height="search_top_height"
      :is-goods="isGoods"
      :placeholder="placeholder"
      @custom="handleCustom"
      @focus="handleFocus"
      @change="getSuggests"
      @blur="handleBlur"
      @search="handleCustom"
      @change-label="changeGoods"
    />
    <scroll-view v-if="suggestsParam.suggests.length" scroll-y :style="{ height: `calc(100vh - ${search_top_height}rpx` }">
      <u-cell-group :border="false">
        <u-cell-item v-for="(suggest, idx) in suggestsParam.suggests" :key="idx" :arrow="false" @click="handleChooseKey(suggest.productName)">
          <template #title>
            <rich-text :nodes="suggest.highLight" />
          </template>
        </u-cell-item>
      </u-cell-group>
    </scroll-view>
    <search-history
      v-else
      v-model="historyWords"
      :is-goods="isGoods"
      :hot-words="hotWords"
      :used-height="search_top_height"
      @update-hotwords="handleupdatehotWords"
    />
  </view>
</template>

<style lang="scss" scoped></style>
