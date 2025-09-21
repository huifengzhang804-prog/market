<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import search from '@/basePackage/pages/concern/components/search.vue'
import Auth from '@/components/auth/auth.vue'
import { doGetConcernListFromMine } from '@/apis/concern'
import ConcernShopList from '@/basePackage/pages/concern/components/concern-shop-list.vue'
import { EMPTY_GB } from '@/constant'
import type { ConcernItem, RequestParams } from '@/basePackage/pages/concern/types'

const tabList = [
  {
    name: '全部店铺',
    value: 'ALL_SHOP',
  },
  {
    name: '最近看过',
    value: 'RECENTLY',
  },
  {
    name: '有上新',
    value: 'NEW_PRODUCTS',
  },
]
const currentActive = ref(0)
const scrollTTop = ref(0)
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
const reachBottomStatus = ref('loading')
const pagesHeights = reactive({
  searchHeight: uni.upx2px(124),
  uTabsHeight: uni.upx2px(80), // px
  screenHeight: uni.getSystemInfoSync().screenHeight,
})
const scrollHeight = computed(() => {
  const { searchHeight, uTabsHeight, screenHeight } = pagesHeights
  return screenHeight - searchHeight - uTabsHeight
})
// loadmore 上拉加载更多 nomore 没有更多了 loading 加载中

initConcernList()

const handleTabsChange = (index: number) => {
  scrollTTop.value = scrollTTop.value === 1 ? 0 : 1
  searchConfig.status = tabList[index].value
  initConcernList()
}
const handleSearchConcern = () => {
  uni.navigateTo({ url: '/basePackage/pages/concern/SearchConcern' })
}
async function initConcernList(isLoad = false) {
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
 * 获取关注列表
 * @param {RequestParams} param
 */
async function getConcern(params: RequestParams) {
  reachBottomStatus.value = 'loading'
  const { data, code, msg } = await doGetConcernListFromMine(params)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取关注列表失败'}`, icon: 'none' })
    reachBottomStatus.value = 'nomore'
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
</script>

<template>
  <search @keyword-change="handleSearchConcern" />
  <u-tabs v-model="currentActive" font-size="24" item-width="104" active-color="#000" :list="tabList" :is-scroll="false" @change="handleTabsChange" />
  <concern-shop-list
    :list="list"
    :reach-bottom-status="reachBottomStatus"
    :scroll-height="scrollHeight"
    :scroll-top="scrollTTop"
    @on-scrolltolower="initConcernList(true)"
  />
  <u-empty v-if="!list.length" margin-top="200" text="暂无关注" font-size="30" icon-size="550" :src="EMPTY_GB.MESSAGE_EMPTY"></u-empty>
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
</style>
