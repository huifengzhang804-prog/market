<script setup lang="ts">
import { ref, reactive } from 'vue'
import RetrieveComUnit from '@/components/retrieve-com-unit/retrieve-com-unit.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doGetRetrieveCommodity } from '@/apis/good'
import type { RetrieveOrderByParams, RetrieveParam } from '@/apis/good'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { RetrieveCommodity } from '@/apis/good/model'

const $props = defineProps({
  scrollHeight: {
    type: Number,
    default: 0,
  },
  shopInfo: {
    type: Object,
    default() {
      return {}
    },
  },
})
const goodList = ref<RetrieveCommodity[]>([])
const waterFallRef = ref()
const sortList = [
  {
    title: '人气',
  },
  {
    title: '销售',
  },
  {
    title: '新品',
  },
]
// loadmore高度
const loadmoreNodeHeight = uni.upx2px(140)
// tab下标
const currentIndex = ref(0)
const enumSortKey: RetrieveOrderByParams = [
  [
    { order: 'salesVolume', sort: 'DESC' },
    { order: 'createTime', sort: 'DESC' },
  ],
  { order: 'salesVolume', sort: 'DESC' },
  { order: 'createTime', sort: 'DESC' },
  { order: 'salePrices', sort: 'ASC' },
  { order: 'salePrices', sort: 'DESC' },
]
const searchParams = reactive<Partial<RetrieveParam>>({
  keyword: '',
  orderByParams: [
    { order: 'salesVolume', sort: 'DESC' },
    { order: 'createTime', sort: 'DESC' },
  ],
  shopId: $props.shopInfo.id,
})
const pageConfig = reactive({
  current: 1,
  size: 10,
  pages: 1,
  status: 'nomore',
})

initGoodList()

const loadMore = () => {
  if (pageConfig.current < pageConfig.pages) {
    initGoodList(true)
  }
}

const handleItemClick = (e: { productId: Long; shopId: Long }) => {
  jumpGoods(e.shopId, e.productId)
}
const handleChangeSort = (idx: number) => {
  if (idx >= sortList.length) {
    currentIndex.value = currentIndex.value === 3 ? 4 : 3
  } else {
    currentIndex.value = idx
  }
  if (currentIndex.value === 0) {
    // 综合排序
    searchParams.orderByParams = [enumSortKey[1], enumSortKey[2]]
  } else {
    searchParams.orderByParams = [enumSortKey[currentIndex.value]]
  }
  initGoodList()
}
/**
 * 获取
 * @param {number} index
 */
async function initGoodList(isLoad = false) {
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    pageConfig.pages = 1
    pageConfig.status = 'loadmore'
    goodList.value = await getGoodFn()
  } else {
    // 更新
    pageConfig.current++
    goodList.value = goodList.value.concat(await getGoodFn())
  }
}
async function getGoodFn() {
  const { code, data, msg } = await doGetRetrieveCommodity({ ...searchParams, ...pageConfig, searchTotalStockGtZero: true })
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: `${msg || '获取商品列表失败'}`,
    })
    return goodList.value.concat([])
  }
  if (data) {
    pageConfig.pages = data.pages
    if (data.pageNum >= data.pages) {
      pageConfig.status = 'nomore'
    } else {
      pageConfig.status = 'loadmore'
    }
    return data.list
  }
  return []
}
const safeHeight = useBottomSafe()
defineExpose({
  loadMore,
})
</script>

<template>
  <view>
    <view class="sort">
      <view v-for="(item, index) in sortList" :key="item.title" :class="{ active: currentIndex === index }" @click="handleChangeSort(index)">{{
        item.title
      }}</view>
      <view class="sort__price" @click="handleChangeSort(3)">
        <view :class="{ active: currentIndex >= sortList.length }">价格</view>
        <view class="sort__flex">
          <q-icon name="icon-xiajiantou" :color="currentIndex === 3 ? '#F54319' : '#000'" class="sort__top" />
          <q-icon name="icon-xiajiantou" :color="currentIndex === 4 ? '#F54319' : '#000'" class="sort__bottom" />
        </view>
      </view>
    </view>
    <u-waterfall
      v-if="goodList.length"
      ref="waterFallRef"
      v-model="goodList"
      idKey="id
"
    >
      <template #left="{ leftList }: { leftList: RetrieveCommodity[] }">
        <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleItemClick(item)">
          <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
          <RetrieveComUnit :good-name="item.productName" :price="item.salePrices" :sale-volume="Number(item.salesVolume)" :commodity-info="item" />
        </view>
      </template>
      <template #right="{ rightList }">
        <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleItemClick(item)">
          <!-- 警告：微信小程序中需要hx2.8.11版本才支持在template中结合其他组件，比如下方的lazy-load组件 -->
          <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
          <RetrieveComUnit :good-name="item.productName" :price="item.salePrices" :sale-volume="Number(item.salesVolume)" :commodity-info="item" />
        </view>
      </template>
    </u-waterfall>
    <u-loadmore v-if="goodList.length" :status="pageConfig.status" />
    <view :style="{ height: 50 + safeHeight + 'px' }"></view>
    <!-- </scroll-view> -->
  </view>
</template>

<style lang="scss" scoped>
@include b(sort) {
  height: 114rpx;
  padding: 0 40rpx;
  background: #fff;
  margin-bottom: 10rpx;
  @include flex(space-between);
  font-size: 24rpx;
  font-weight: bold;
  @include e(price) {
    @include flex();
  }
  @include e(flex) {
    display: flex;
    flex-direction: column;
  }
  @include e(top) {
    transform: rotate(180deg) translateY(-10rpx);
  }
  @include e(bottom) {
    transform: translateY(-10rpx);
  }
}
.active {
  color: #f54319;
}
</style>
