<script setup lang="ts">
import { reactive, ref, watch, type PropType } from 'vue'
import { doGetRetrieveCommodity } from '@/apis/good'
import type { RetrieveOrderByParams } from '@/apis/good'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import type { RetrieveCommodity } from '@/apis/good/model'

const { divTenThousand } = useConvert()
const props = defineProps({
  shopId: {
    type: String as PropType<Long>,
    required: true,
  },
  productId: {
    type: String as PropType<Long>,
    required: true,
  },
  seeActive: {
    type: String as PropType<'SEE' | 'SALES'>,
    default: 'SEE',
  },
})
const currentIndex = ref(0)
const swiperChangeHandle = (e: { detail: { current: number } }) => {
  currentIndex.value = e.detail.current
}
const pageConfig = reactive({
  size: 12,
  current: 1,
  pages: 1,
  orderByParams: [] as RetrieveOrderByParams,
})
const list = ref<RetrieveCommodity[][]>([[], []])

watch(
  () => props.shopId,
  () => {
    getList()
  },
  {
    immediate: true,
  },
)
watch(
  () => props.seeActive,
  (val) => {
    if (val === 'SALES') {
      pageConfig.orderByParams = [
        {
          order: 'salesVolume',
          sort: 'DESC',
        },
      ]
      getList()
      return
    }
    pageConfig.orderByParams = []
    getList()
  },
  {
    immediate: true,
  },
)

async function getList() {
  if (!props.shopId) {
    return
  }
  const { code, data, msg } = await doGetRetrieveCommodity({
    ...pageConfig,
    shopId: props.shopId,
    searchTotalStockGtZero: true,
    excludeProductId: props.productId,
  })
  if (code !== 200 || !data) {
    uni.showToast({ title: `${msg ? msg : '获取列表失败'}`, icon: 'none' })
    return
  }
  list.value = []
  pageConfig.pages = data.pages
  const len = data.list.length
  if (len > 6) {
    list.value[0] = data.list.slice(0, 6)
    list.value[1] = data.list.slice(6, data.list.length)
    return
  }
  list.value.push(data.list)
}
const handleItemClick = (good: RetrieveCommodity) => {
  const { productId, shopId } = good
  jumpGoods(shopId, productId)
}
</script>

<template>
  <view class="ranking">
    <swiper
      :style="{ height: (list?.[0]?.length > 3 ? 700 : 350) + 'rpx' }"
      :current="currentIndex"
      :disable-touch="list.length === 1"
      @change="swiperChangeHandle"
    >
      <swiper-item v-for="(item, idx) in list" :key="idx">
        <u-grid :col="3" :border="false">
          <u-grid-item v-for="good in item" :key="good.id" :custom-style="{ padding: '15rpx 0' }" @click="handleItemClick(good)">
            <u-image name="photo" :src="good.pic" :width="210" :height="210" border-radius="8rpx" />
            <view class="grid-text">{{ good.productName }}</view>
            <view class="grid-price">{{ Number(divTenThousand(good.salePrices[0])).toFixed(2) }}</view>
          </u-grid-item>
        </u-grid>
      </swiper-item>
    </swiper>
    <view v-if="list.length > 1" class="indicator">
      <view v-for="i in list.length" :key="i" class="indicator__bg" :class="{ active: currentIndex === i - 1 }"> </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(ranking) {
  border-bottom-left-radius: 15rpx;
  border-bottom-right-radius: 15rpx;
}
@include b(grid-text) {
  width: 210rpx;
  margin: 12rpx 0;
  text-align: left;
  color: #101010;
  font-size: 28rpx;
  @include utils-ellipsis(1);
}
@include b(grid-price) {
  width: 210rpx;
  text-align: left;
  color: #e31436;
  font-weight: 700;
  font-size: 32rpx;
  &::before {
    font-size: 24rpx;
    font-weight: normal;
    content: '￥';
  }
}
@include b(indicator) {
  margin: 20rpx 0;
  @include flex();
  @include e(bg) {
    width: 14rpx;
    height: 14rpx;
    background: #ff3a3a;
    border-radius: 7rpx;
    opacity: 0.4;
    margin-left: 20rpx;
  }
}
.active {
  opacity: 1;
}
</style>
