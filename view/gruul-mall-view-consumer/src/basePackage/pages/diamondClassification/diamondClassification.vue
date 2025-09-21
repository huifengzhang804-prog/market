<script setup lang="ts">
import { doGetRetrieveCommodity } from '@/apis/good'
import { doGetCategory } from '@/apis/home'
import type { ApiCategoryData } from '@/pages/platform/types'
import QIcon from '@/components/q-icon/q-icon.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { computed, nextTick, reactive, ref } from 'vue'
import type { RetrieveCommodity } from '@/apis/good/model'

const { divTenThousand } = useConvert()
const showmore = ref(false)
const slideNavIndex = ref(0)
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const commodityList = ref<RetrieveCommodity[]>([])
const firstCateList = ref<ApiCategoryData[]>([])
const secCateList = ref<ApiCategoryData[]>([])
const currentCategoryInfo = ref<ApiCategoryData>()
const scrollOptions = reactive({
  old: 0,
  new: 0,
})

initfirstCateList()

async function initfirstCateList() {
  const { code, data } = await doGetCategory('LEVEL_1', 0, 9999, 1)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取分类失败',
    })
  }
  if (data) {
    currentCategoryInfo.value = data.records[0]
    firstCateList.value = data.records
    initSecCateList()
    initCommodityBySecId()
  }
}
async function initSecCateList() {
  const { code, data } = await doGetCategory('LEVEL_2', currentCategoryInfo.value!.id, 9999, 1)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取分类失败',
    })
  }
  if (data) {
    if (data.records.length > 10) {
      data.records.push({
        id: '-1',
        categoryImg: '',
        name: '收起',
        children: [],
      })
    }
    secCateList.value = data.records
  }
}
const hanleChangeTab = (idx: number) => {
  slideNavIndex.value = idx
  currentCategoryInfo.value = firstCateList.value[idx]
  scrollOptions.new = scrollOptions.old
  nextTick(() => {
    scrollOptions.new = 0
  })
  initSecCateList()
  initPageConfig()
  initCommodityBySecId()
}
const scrolltolower = () => {
  initCommodityBySecId(true)
}
const handleNavTodetail = (info: RetrieveCommodity) => {
  jumpGoods(info.shopId, info.productId)
}
const handleNavToRetrive = (id: string) => {
  uni.navigateTo({
    url: `/basePackage/pages/searchRetrieve/SearchRetrieve?platformCategorySecondId=${id}&isGoods=true&keyword=`,
  })
}
/**
 * 根据一级id查询商品列表
 */
async function initCommodityBySecId(isLoad = false) {
  if (!isLoad) {
    // 刷新
    commodityList.value = await getGoodList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    commodityList.value = commodityList.value.concat(await getGoodList())
  }
}
async function getGoodList() {
  if (!currentCategoryInfo.value) {
    uni.showToast({
      icon: 'none',
      title: '分类加载失败',
    })
    return []
  }
  const { code, data } = await doGetRetrieveCommodity({
    platformCategoryFirstId: currentCategoryInfo.value.id,
    current: pageConfig.current,
    searchTotalStockGtZero: true,
    size: pageConfig.size,
  })
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取商品失败',
    })
    return []
  }
  if (data) {
    pageConfig.pages = data.pages
    return data.list
  }
  return []
}
function initPageConfig() {
  pageConfig.current = 1
  pageConfig.pages = 1
}
function handleScroll({ detail: { scrollTop } }: { detail: { scrollTop: number } }) {
  scrollOptions.old = scrollTop
}
const h5Height = ref(0)
// #ifdef H5
h5Height.value = 44
// #endif

const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(96)
})
</script>

<template>
  <div class="classificate">
    <div class="classificate__sort">
      <div v-for="(item, index) in firstCateList" :key="item.id" class="classificate__sort-item" @click="hanleChangeTab(index)">
        <div style="position: relative; z-index: 100">{{ item.name }}</div>
        <div v-if="slideNavIndex === index" class="classificate__sort-item--red"></div>
      </div>
    </div>
    <scroll-view scroll-y :style="{ height: `${height}px` }" :scroll-top="scrollOptions.new" @scroll="handleScroll" @scrolltolower="scrolltolower">
      <div class="classificate__sec" :style="showmore ? {} : { overflow: 'hidden', maxHeight: '354rpx' }">
        <div v-for="(item, index) in secCateList" :key="item.id" @click="handleNavToRetrive(item.id)">
          <div
            v-if="(index !== 9 || secCateList.length === 10 || showmore) && (secCateList.length <= 10 || secCateList.length !== index + 1)"
            class="classificate__sec-item"
          >
            <image :src="item.categoryImg" class="classificate__sec-item--pic"></image>
            <div class="classificate__sec-item--name">{{ item.name }}</div>
          </div>
          <div
            v-else-if="showmore && secCateList.length > 10 && secCateList.length === index + 1"
            class="classificate__sec-item"
            @click.stop="showmore = false"
          >
            <div class="classificate__sec-item--pic">
              <q-icon size="84rpx" color="#DEDEDE;" name="icon-jiantoushang" />
            </div>
            <div class="classificate__sec-item--name" style="color: #999">收起</div>
          </div>
          <div v-else-if="!showmore && secCateList.length > 10" class="classificate__sec-item" @click.stop="showmore = true">
            <div class="classificate__sec-item--pic">
              <q-icon size="84rpx" color="#DEDEDE;" name="icon-xiajiantou" />
            </div>
            <div class="classificate__sec-item--name" style="color: #999">查看更多</div>
          </div>
        </div>
      </div>
      <div class="classificate__list--title">
        <div style="position: absolute; z-index: 100">热门商品</div>
        <div class="classificate__list--red"></div>
      </div>
      <div class="classificate__list">
        <div v-for="item in commodityList" :key="item.productId" class="classificate__list-item" @click="handleNavTodetail(item)">
          <image :src="item.pic" class="classificate__list-item--pic"></image>
          <div class="classificate__list-item-info">
            <div class="classificate__list-item-info--name">{{ item.productName }}</div>
            <div class="classificate__list-item-info--price">￥{{ divTenThousand(item.salePrices[0]) }}</div>
          </div>
        </div>
      </div>
    </scroll-view>
  </div>
</template>

<style lang="scss" scoped>
@include b(classificate) {
  position: relative;
  overflow: hidden;
  @include e(sort) {
    padding: 16rpx;
    height: 96rpx;
    background: #fff;
    box-sizing: border-box;
    @include flex(flex-start);
    overflow-x: auto;
    &::-webkit-scrollbar {
      display: none;
      width: 0 !important;
    }
  }
  @include e(sort-item) {
    flex-shrink: 0;
    font-size: 14px;
    padding: 16rpx 12rpx;
    background: #fff;
    margin-right: 10px;
    cursor: pointer;
    position: relative;
    @include m(red) {
      position: absolute;
      height: 10rpx;
      width: calc(100% - 25rpx);
      border-radius: 20rpx;
      bottom: 0;
      background-color: #fa3534;
    }
  }
  @include e(sec) {
    width: 686rpx;
    background: #ffffff;
    border-radius: 20rpx;
    display: flex;
    flex-wrap: wrap;
    margin: 20rpx auto 30rpx;
    padding: 20rpx 22rpx;
    gap: 55rpx;
  }
  @include e(sec-item) {
    width: 84rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    @include m(pic) {
      width: 84rpx;
      height: 84rpx;
      margin-bottom: 10px;
      border-radius: 50%;
    }
    @include m(name) {
      overflow: hidden;
      width: 130rpx;
      color: #333333;
      font-size: 12px;
      font-weight: 500;
      text-align: center;
    }
  }
  @include e(list) {
    width: 355px;
    margin: 15px auto 0;
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;

    @include m(title) {
      position: relative;
      margin-top: 30rpx;
      margin-bottom: 7px;
      width: 56px;
      height: 34rpx;
      font-size: 14px;
      margin-left: 20rpx;
    }
    @include m(red) {
      position: absolute;
      height: 10rpx;
      width: 112rpx;
      border-radius: 20rpx;
      z-index: 90;
      bottom: -18rpx;
      background-color: #fa3534;
    }
  }
  @include e(list-item) {
    background: #fff;
    width: 172px;
    height: 219px;
    margin-bottom: 14rpx;
    border-radius: 20rpx;
    @include m(pic) {
      border-radius: 20rpx 20rpx 0 0;
      width: 172px;
      height: 163px;
    }
  }
  @include e(list-item-info) {
    height: 59px;
    padding: 10px;
    font-size: 11px;
    @include m(name) {
      width: 155px;
      margin-bottom: 6rpx;
      @include utils-ellipsis();
    }
    @include m(price) {
      color: #fa3534;
    }
  }
}
</style>
