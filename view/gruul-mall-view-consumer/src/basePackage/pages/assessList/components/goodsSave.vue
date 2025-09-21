<script setup lang="ts">
import { ref, shallowReactive, reactive, onMounted, onUnmounted } from 'vue'
import RetrieveComUnit from '@/components/retrieve-com-unit/retrieve-com-unit.vue'
import { doGetAssessOrderList } from '@/apis/assess'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { EMPTY_GB } from '@/constant'
import type { ApiAssessItem } from '../types'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'

const $collectionDispatcherStore = useCollectionDispatcher()
const assessList = ref<ApiAssessItem[]>([])
const isShowEmpty = ref(false)
// 已经下架
const pageConfig = shallowReactive({
  size: 5,
  current: 1,
  pages: 1,
})
const waterFallRef = ref()

initAssessList()

/**
 * 收藏列表
 */
async function initAssessList(isLoad = false) {
  isShowEmpty.value = false
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    assessList.value = await getCommodityList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    assessList.value = [...assessList.value, ...(await getCommodityList())]
  }
  isShowEmpty.value = true
}
async function getCommodityList() {
  const { code, data, msg } = await doGetAssessOrderList(pageConfig)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取收藏列表失败'}`, icon: 'none' })
    return []
  }
  if (!data) {
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
const handleNavToGoodsDetail = (goods: ApiAssessItem) => {
  jumpGoods(goods.shopId, goods.productId)
}
onMounted(() => $collectionDispatcherStore.addCommoditySubscriber(() => initAssessList()))
onUnmounted(() => $collectionDispatcherStore.removeCommoditySubscriber(() => initAssessList()))
const loadMore = () => {
  initAssessList(true)
}
defineExpose({
  loadMore,
})
</script>

<template>
  <u-waterfall v-if="assessList.length" ref="waterFallRef" v-model="assessList" idKey="productId">
    <template #left="{ leftList }">
      <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleNavToGoodsDetail(item)">
        <u-lazy-load threshold="-450" border-radius="16" :image="item.productPic" :index="index"></u-lazy-load>
        <RetrieveComUnit :good-name="item.productName" :price="[item.productPrice]" :sale-volume="Number(item.salesVolume)" />
      </view>
    </template>
    <template #right="{ rightList }">
      <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleNavToGoodsDetail(item)">
        <u-lazy-load threshold="-450" border-radius="16" :image="item.productPic" :index="index"></u-lazy-load>
        <RetrieveComUnit :good-name="item.productName" :price="[item.productPrice]" :sale-volume="Number(item.salesVolume)" />
      </view>
    </template>
  </u-waterfall>
  <q-empty v-if="!assessList.length && isShowEmpty" :background="EMPTY_GB.COLLECTION_EMPTY" title="暂无收藏~" />
</template>

<style scoped lang="scss"></style>
