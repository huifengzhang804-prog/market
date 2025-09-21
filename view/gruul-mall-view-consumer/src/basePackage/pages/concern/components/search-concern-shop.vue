<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useNavBack } from '@/hooks/useNavBack'
import storage from '@/utils/storage'
import { debounce } from 'lodash'
import Auth from '@/components/auth/auth.vue'
import { doGetConcernListFromMine } from '@/apis/concern'
import ConcernShopList from '@/basePackage/pages/concern/components/concern-shop-list.vue'
import type { ConcernItem, RequestParams } from '@/basePackage/pages/concern/types'

const pageConfig = reactive({
  size: 10,
  current: 1,
  pages: 1,
})
const searchConfig = reactive({
  shopName: '',
  status: 'ALL_SHOP',
})
const list = ref<ConcernItem[]>([])
const scrollHeight = uni.upx2px(100)
const searchHistory = ref([])
const scrollTTop = ref(0)
const showDelIcon = ref(false)
const delBtn = ref(true)
const reachBottomStatus = ref('loading')

initSearchHistory()

const handleCancel = () => {
  searchConfig.shopName = ''
  useNavBack()
}
/**
 * 搜索
 */
const handleChangeSearch = async (e: string) => {
  try {
    if (!e.trim().length) {
      return
    }
    SearchConcernList()
    const currentHis = storage.get('shopSearchHistory')
    if (Array.isArray(currentHis)) {
      const newHis = [e, ...currentHis]
      storage.set('shopSearchHistory', [...new Set([...newHis])])
    } else {
      storage.set('shopSearchHistory', [e])
    }
    initSearchHistory()
  } catch (error) {
    console.log(error)
  }
}
async function SearchConcernList(isLoad = false) {
  scrollTTop.value = scrollTTop.value === 1 ? 0 : 1
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    list.value = await getConcern({ ...pageConfig, ...searchConfig })
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    list.value = list.value.concat(await getConcern({ ...pageConfig, ...searchConfig }))
  }
  reachBottomStatus.value = 'loadmore'
  console.log('searchConfig.shopName', searchConfig.shopName, list.value)
  if (!list.value.length) {
    // 如果没有数据 就展示没有更多
    reachBottomStatus.value = 'nomore'
    return
  }
  if (!(pageConfig.current < pageConfig.pages)) {
    // 没有更多
    reachBottomStatus.value = 'nomore'
  }
}
/**
 * 输入框变化不计入历史搜搜
 */
const handleChangeThrottle = debounce((e: string) => {
  SearchConcernList()
}, 500)
const handleChange = function (e: string) {
  if (!e.trim().length) {
    return
  }
  list.value = []
  handleChangeThrottle(e)
}
/**
 * 初始化历史数据
 */
function initSearchHistory() {
  searchHistory.value = storage.get('shopSearchHistory')
}
/**
 * 获取关注列表
 * @param {RequestParams} param
 */
async function getConcern(params: RequestParams) {
  const { data } = await doGetConcernListFromMine(params)
  pageConfig.pages = data.pages
  return data.records
}
const deleteSearchHistory = () => {
  if (searchHistory.value.length) {
    showDelIcon.value = true
    delBtn.value = false
  }
}
const complete = () => {
  delBtn.value = true
  showDelIcon.value = false
}
const deleteAll = () => {
  storage.set('shopSearchHistory', [])
  initSearchHistory()
  delBtn.value = true
}
const deleteItem = (itemHis: string) => {
  const searchShopHistoryArr = storage.get('shopSearchHistory') as string[]
  if (!Array.isArray(searchShopHistoryArr)) return
  storage.set(
    'shopSearchHistory',
    searchShopHistoryArr.filter((item) => item !== itemHis),
  )
  initSearchHistory()
}
const handleSearchShop = (shopName: string) => {
  if (showDelIcon.value) return
  searchConfig.shopName = shopName
  SearchConcernList()
}
/**
 * 用户点击右侧控件时触发
 */
const handleCustom = () => {
  if (searchConfig.shopName) {
    // 搜索
  } else {
    // 取消
    handleCancel()
  }
}
</script>

<template>
  <view id="search-box" class="search__box">
    <u-search
      v-model.trim="searchConfig.shopName"
      shape="square"
      :show-action="false"
      height="52"
      margin="0 15rpx 0 0"
      :action-text="searchConfig.shopName ? '搜索' : '取消'"
      bg-color="#f6f7f9"
      @change="handleChange"
      @custom="handleCustom"
      @search="handleChangeSearch"
    />
  </view>
  <view v-if="!list.length || !searchConfig.shopName.length" class="search__history">
    <view class="search__history-title">
      <view>搜索历史</view>
      <q-icon v-if="delBtn" name="icon-shanchu" color="#696969" @click="deleteSearchHistory" />
      <view v-else class="search__history-complete"><text @click="deleteAll">全部删除</text> <text @click="complete">完成</text></view>
    </view>
    <view class="search__history-main">
      <view v-for="item in searchHistory" :key="item" class="search__history-main-container">
        <q-icon v-show="showDelIcon" name="icon-guanbi" color="#fa3534" size="5px" class="search__history-main--del-icon" @click="deleteItem(item)" />
        <text class="search__history-main--item" @click="handleSearchShop(item)">{{ item }}</text>
      </view>
    </view>
  </view>
  <concern-shop-list
    v-show="searchConfig.shopName.length && list.length"
    :list="list"
    :reach-bottom-status="reachBottomStatus"
    :scroll-height="scrollHeight"
    :scroll-top="scrollTTop"
    @on-scrolltolower="SearchConcernList(true)"
  />
  <Auth />
</template>

<style scoped lang="scss">
@include b(search) {
  @include e(box) {
    padding: 30rpx 28rpx;
    height: 100rpx;
    @include m(text) {
      font-size: 24rpx;
      color: #696969;
    }
  }
  @include e(history) {
    margin-top: 62rpx;
  }
  @include e(history-title) {
    padding: 0 28rpx;
    @include flex(space-between);
    font-size: 30rpx;
    font-weight: bold;
    text-align: LEFT;
    color: #000000;
  }
  @include e(history-complete) {
    font-size: 24rpx;
    color: #696969;
    font-weight: 400;
  }
  @include e(history-main-container) {
    position: relative;
  }
  @include e(history-main) {
    padding: 0 10rpx;
    @include flex(flex-start);
    flex-wrap: wrap;
    @include m(del-icon) {
      position: absolute;
      right: 5rpx;
      top: 20rpx;
    }
    @include m(item) {
      display: block;
      height: 52rpx;
      padding: 0 30rpx;
      line-height: 52rpx;
      background: #f2f2f2;
      border-radius: 24rpx;
      font-size: 24rpx;
      text-align: CENTER;
      color: #696969;
      margin: 20rpx 5rpx;
    }
  }
}
</style>
