<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
import { doPostGoods } from '../apis'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { computed, reactive, ref } from 'vue'

// import type { RetrieveParam, RetrieveOrderByParams } from '@/apis/good'
interface searchParamsType {
  productName: string
  orders: {
    column: string
    asc: boolean
  }[]
}

const { divTenThousand } = useConvert()
const searchParams = reactive<searchParamsType>({
  productName: '',
  orders: [{ column: 'bonus', asc: true }],
})
const currentIndex = ref(0)
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const enumSortKey = [
  { column: 'bonus', asc: true },
  { column: 'bonus', asc: false },
  { column: 'sales', asc: true },
  { column: 'sales', asc: false },
  { column: 'minPrice', asc: false },
  { column: 'minPrice', asc: true },
]
const disgoodslist = ref<any>([])

const loadMore = () => {
  pageConfig.current++
  initGoodsList()
}
initGoodsList()

const handleChangeSort = (idx: number) => {
  // eslint-disable-next-line default-case
  switch (idx) {
    case 0:
      currentIndex.value = currentIndex.value === 0 ? 1 : 0
      break
    case 2:
      currentIndex.value = currentIndex.value === 2 ? 3 : 2
      break
    case 4:
      currentIndex.value = currentIndex.value === 4 ? 5 : 4
      break
  }

  searchParams.orders = [enumSortKey[currentIndex.value]]
  pageConfig.current = 1
  disgoodslist.value = []
  initGoodsList()
}
async function initGoodsList() {
  const { code, data, msg } = await doPostGoods({ ...pageConfig, ...searchParams })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取分销商品失败'}`, icon: 'none' })
  }
  disgoodslist.value = [...disgoodslist.value, ...data.records]
}
const handleSearch = () => {
  pageConfig.current = 1
  disgoodslist.value = []
  initGoodsList()
}
const handleNavToGoodsDetail = (productId: Long, shopId: Long) => {
  jumpGoods(shopId, productId)
}
const height = computed(() => {
  let contentHeight = useScreenHeight().value - useBottomSafe().value - uni.upx2px(122) - uni.upx2px(96)
  return contentHeight
})
</script>

<template>
  <view class="search">
    <u-search
      v-model="searchParams.productName"
      shape="round"
      :show-action="true"
      placeholder="请输入您要搜素的商品"
      action-text="搜索"
      bg-color="#F2F2F2"
      height="71"
      style="width: 710rpx"
      @custom="handleSearch"
    />
  </view>
  <view class="sort">
    <view class="sort__price" @click="handleChangeSort(0)">
      <view :class="{ active: currentIndex === 0 || currentIndex === 1 }">佣金</view>
      <view class="sort__flex">
        <view style="width: 28rpx; height: 28rpx; display: inline; transform: translate(10rpx, 8rpx) scale(0.5) rotate(180deg)">
          <QIcon name="icon-a-zuhe11" size="28rpx" :color="currentIndex === 0 ? '#F54319' : '#999999'" />
        </view>
        <view style="width: 28rpx; height: 28rpx; display: inline; transform: translate(2rpx, -8rpx) scale(0.5)">
          <QIcon size="28rpx" name="icon-a-zuhe11" :color="currentIndex === 1 ? '#F54319' : '#999999'" />
        </view>
      </view>
    </view>
    <view class="sort__price" @click="handleChangeSort(2)">
      <view :class="{ active: currentIndex === 2 || currentIndex === 3 }">销量</view>
      <view class="sort__flex">
        <!-- <QIcon name="icon-xiajiantou" :color="currentIndex === 2 ? '#F54319' : '#000'" class="sort__top" />
                <QIcon name="icon-xiajiantou" :color="currentIndex === 3 ? '#F54319' : '#000'" class="sort__bottom" /> -->
        <view style="width: 28rpx; height: 28rpx; display: inline; transform: translate(10rpx, 8rpx) scale(0.5) rotate(180deg)">
          <QIcon name="icon-a-zuhe11" size="28rpx" :color="currentIndex === 2 ? '#F54319' : '#999999'" />
        </view>
        <view style="width: 28rpx; height: 28rpx; display: inline; transform: translate(2rpx, -8rpx) scale(0.5)">
          <QIcon size="28rpx" name="icon-a-zuhe11" :color="currentIndex === 3 ? '#F54319' : '#999999'" />
        </view>
      </view>
    </view>
    <view class="sort__price" @click="handleChangeSort(4)">
      <view :class="{ active: currentIndex === 4 || currentIndex === 5 }">价格</view>
      <view class="sort__flex">
        <view style="width: 28rpx; height: 28rpx; display: inline; transform: translate(10rpx, 8rpx) scale(0.5) rotate(180deg)">
          <QIcon name="icon-a-zuhe11" size="28rpx" :color="currentIndex === 4 ? '#F54319' : '#999999'" />
        </view>
        <view style="width: 28rpx; height: 28rpx; display: inline; transform: translate(2rpx, -8rpx) scale(0.5)">
          <QIcon size="28rpx" name="icon-a-zuhe11" :color="currentIndex === 5 ? '#F54319' : '#999999'" />
        </view>
      </view>
    </view>
  </view>
  <scroll-view scroll-y :style="{ height: `${height}px` }" @scrolltolower="loadMore">
    <view class="goods">
      <view v-for="item in disgoodslist" :key="item.id" class="goods__item" @click="handleNavToGoodsDetail(item.productId, item.shopId)">
        <view class="goods__item-pic">
          <image :src="item.pic" class="goods__item-pic--img"></image>
        </view>
        <view class="goods__item-right">
          <view class="goods__item-right-name">{{ item.name }}</view>
          <view class="goods__item-right-info">
            <view class="goods__item-right-info--price"
              ><span style="font-size: 22rpx">￥</span
              >{{ divTenThousand(currentIndex === 4 ? item.maxPrice : Math.min(...item.salePrices)).toFixed(2) }}</view
            >
            <view class="goods__item-right-info-expect">
              <view class="goods__item-right-info-expect--word">预计赚</view>
              <view class="goods__item-right-info-expect--num">￥{{ divTenThousand(item.bonus).toFixed(2) }}</view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(search) {
  height: 122rpx;
  background: #fff;
  padding: 0 20rpx;
  display: flex;
}
@include b(sort) {
  background: #fff;
  height: 96rpx;
  // padding: 16rpx 40rpx;
  // margin-bottom: 10rpx;
  @include flex(space-around);
  font-size: 28rpx;
  font-weight: 400;
  @include e(price) {
    @include flex();
    width: 171.5rpx;
    height: 64rpx;
    background-color: #f3f3f3;
    border-radius: 32rpx;
  }
  @include e(flex) {
    display: flex;
    flex-direction: column;
  }
  @include e(top) {
    transform: rotate(180deg) translateY(-20rpx);
  }
  @include e(bottom) {
    transform: translateY(-10rpx);
  }
}
.active {
  color: #f54319;
}

@include b(goods) {
  background-color: #f2f2f2;
  @include e(item) {
    width: 710rpx;
    height: 192rpx;
    display: flex;
    background: #fff;
    margin: 0 auto;
    margin-top: 20rpx;
    border-radius: 20rpx;
    overflow: hidden;
    padding-right: 20rpx;
    overflow: hidden;
  }
  @include e(item-pic) {
    @include m(img) {
      width: 192rpx;
      height: 192rpx;
    }
  }
  @include e(item-right) {
    margin-left: 10rpx;
  }
  @include e(item-right-name) {
    font-size: 26rpx;
    margin-top: 20rpx;
    height: 72rpx;
    width: 488rpx;
    font-weight: bold;
    word-break: break-all;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  @include e(item-right-info) {
    @include m(price) {
      font-weight: bold;
      float: left;
      margin-top: 35rpx;
      font-size: 30rpx;
      font-weight: bo;
      color: #fd0505;
    }
  }
  @include e(item-right-info-expect) {
    float: right;
    display: flex;
    height: 42rpx;
    line-height: 40rpx;
    text-align: center;
    margin-top: 31rpx;
    @include m(word) {
      background: #fa3534;
      color: #fff;
      font-size: 20rpx;
      padding: 0rpx 17rpx;
    }
    @include m(num) {
      padding: 0 10rpx;
      border: 1px solid #fa3534;
      color: #fa3534;
      font-size: 20rpx;
    }
  }
}
</style>
