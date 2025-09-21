<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import Empty from '@/basePackage/pages/abnormalGoods/components/empty.vue'
import RetrieveComUnit from '@/components/retrieve-com-unit/retrieve-com-unit.vue'
import { doGetGuessYouLike } from '@/apis/consumer/footprint'
import { jumpGoods } from '@/utils/navigateToShopInfo'

const likeData = reactive<{ list: any[] }>({
  list: [],
})
const waterFallRef = ref()
const pageConfig = reactive({
  current: 1,
  size: 10,
  pages: 1,
  status: 'loadmore',
})

initGuessYouLike()

const handleClickItem = (e: { productId: Long; shopId: Long }) => {
  jumpGoods(e.shopId, e.productId)
}
async function initGuessYouLike(isLoad = false) {
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    likeData.list = await getGuessYouLike()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    likeData.list = likeData.list.concat(await getGuessYouLike())
  }
}
async function getGuessYouLike() {
  const { code, data, msg } = await doGetGuessYouLike(pageConfig.current, pageConfig.size)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取猜你喜欢失败'}`, icon: 'none' })
  if (data.current >= data.pages) {
    pageConfig.status = 'nomore'
  }
  pageConfig.pages = data.pages
  return data.records
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="initGuessYouLike(true)">
    <empty id="abnormal-goods-empty" />
    <u-divider bg-color="raba(244,244,244)" height="80" color="#000">为您推荐了其他宝贝</u-divider>
    <u-waterfall v-if="likeData.list.length" ref="waterFallRef" v-model="likeData.list" idKey="productId">
      <template #left="{ leftList }">
        <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleClickItem(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.productAlbumPics" :index="index"></u-lazy-load>
          <RetrieveComUnit :good-name="item.productName" :price="item.lowestPrice" :sale-volume="Number(item.salesVolume)" />
        </view>
      </template>
      <template #right="{ rightList }">
        <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleClickItem(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.productAlbumPics" :index="index"></u-lazy-load>
          <RetrieveComUnit :good-name="item.productName" :price="item.lowestPrice" :sale-volume="Number(item.salesVolume)" />
        </view>
      </template>
    </u-waterfall>
    <u-loadmore v-if="likeData.list.length" :status="pageConfig.status" />
  </scroll-view>
</template>

<style scoped lang="scss"></style>
