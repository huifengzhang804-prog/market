<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import SearchSort from './search-sort.vue'
import { doGetRetrieveCommodity, type RetrieveOrderByParams } from '@/apis/good'
import { EMPTY_GB } from '@/constant'
import HighlightRetrieveComUnit from '@/components/highlight-retrieve-com-unit/highlight-retrieve-com-unit.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { RetrieveCommodity } from '@/apis/good/model'

const $props = defineProps({
  keyword: {
    type: String,
    default: '',
  },
  usedHeight: {
    type: Number,
    default: 122,
  },
})
const searchParams = reactive<{ keyword: string; orderByParams: RetrieveOrderByParams }>({
  keyword: '',
  orderByParams: [
    { order: 'salesVolume', sort: 'DESC' },
    { order: 'createTime', sort: 'DESC' },
  ],
})
const list = ref<RetrieveCommodity[]>([])
// 组件默认高度 114 rpx
const search_sort_height = 114
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const isLoding = ref(false)
const status = computed(() => {
  if (isLoding.value) return 'loading'
  if (pageConfig.size * pageConfig.pages === list.value.length) return 'nomore'
  return 'loadmore'
})

watch(
  () => $props.keyword,
  (val) => {
    initList()
  },
  {
    immediate: true,
  },
)
const waterFallRef = ref()

async function initList(isLoad = false) {
  isLoding.value = true
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    pageConfig.pages = 1
    list.value = await getGoodList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    list.value = list.value.concat(await getGoodList())
  }
  isLoding.value = false
}
// 获取商品列表
async function getGoodList() {
  searchParams.keyword = $props.keyword
  const { code, data, msg } = await doGetRetrieveCommodity({ ...searchParams, ...pageConfig })
  if (code !== 200 || !data) {
    uni.showToast({
      icon: 'none',
      title: `${msg || '获取商品列表失败'}`,
    })
    return []
  }
  pageConfig.pages = data.pages
  const temp = data.list.map((item) => {
    const highLight = item.highLight ? item.highLight : item.productName
    return {
      ...item,
      highLight: highLight,
    }
  })
  return temp
}
const handleItemClick = (e: any) => {
  jumpGoods(e.shopId, e.productId)
}
</script>
<template>
  <search-sort @updateorder-by-params="searchParams.orderByParams = $event" @init-list="initList"></search-sort>
  <scroll-view scroll-y :style="{ height: `calc(100vh - ${usedHeight}rpx - ${search_sort_height}rpx)` }" @scrolltolower="initList(true)">
    <view v-if="list.length" style="padding: 10rpx">
      <u-waterfall v-if="list.length" ref="waterFallRef" v-model="list" idKey="productId">
        <template #left="{ leftList }">
          <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleItemClick(item)">
            <u-lazy-load threshold="-450" border-radius="20" :image="item.pic" :index="index"></u-lazy-load>
            <HighlightRetrieveComUnit :good-name="item.highLight" :price="item.salePrices" :sale-volume="item.salesVolume" />
          </view>
        </template>
        <template #right="{ rightList }">
          <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleItemClick(item)">
            <u-lazy-load threshold="-450" border-radius="20" :image="item.pic" :index="index"></u-lazy-load>
            <HighlightRetrieveComUnit :good-name="item.highLight" :price="item.salePrices" :sale-volume="item.salesVolume" />
          </view>
        </template>
      </u-waterfall>
      <view style="padding: 20rpx 0">
        <u-loadmore :status="status" />
      </view>
    </view>
    <u-empty v-if="!list.length" text="没有找到相关的宝贝" icon-size="400" :src="EMPTY_GB.COLLECTION_EMPTY"></u-empty>
  </scroll-view>
</template>

<style scoped lang="scss"></style>
