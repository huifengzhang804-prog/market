<template>
  <view>
    <q-nav title="搜索" bg-color="#fff" />
    <u-search v-model="searchParams.name" shape="round" placeholder="请填写关键词" action-text="搜索" bg-color="#F6F7F9" @custom="handleSearch" />
  </view>
  <scroll-view scroll-y :refresher-threshold="30" :style="{ height: scrollViewHeight }" @scrolltolower="initList(true)">
    <view style="padding: 0 10rpx">
      <u-waterfall v-if="list.length" ref="waterFallRef" v-model="list" idKey="id">
        <template #left="{ leftList }">
          <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleItemClick(item.id)">
            <u-lazy-load threshold="-450" border-radius="16" :image="item.logo" :index="index"></u-lazy-load>
            <view class="shopInfo">
              {{ item.name }}
              <u-button type="error" size="medium" @click="handleItemClick(item.id)">进店看看</u-button>
            </view>
          </view>
        </template>
        <template #right="{ rightList }">
          <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleItemClick(item.id)">
            <u-lazy-load threshold="-450" border-radius="16" :image="item.logo" :index="index"></u-lazy-load>
            <view class="shopInfo">
              {{ item.name }}
              <u-button type="error" size="medium" @click="handleItemClick(item.id)">进店看看</u-button>
            </view>
          </view>
        </template>
      </u-waterfall>
      <u-loadmore v-if="list.length" :status="loadMoreStatus" />
      <q-empty v-if="!list.length" :title="emptyConfig.title" :background="emptyConfig.background" />
    </view>
  </scroll-view>
  <!-- </view> -->
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useStatusBar } from '@/hooks'
import { doGetshops } from '@/apis/good'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { onLoad } from '@dcloudio/uni-app'
import type { Shops } from '@/apis/good/model'

// 商品为空展示
const emptyConfig = {
  title: '店铺不存在',
  background: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220531/1ce266a5e3fb43a4ae9b764fd8a166df.png',
}
onLoad((res) => {
  uni.$emit('updateTitle')
  if (res) {
    const { name } = res
    searchParams.name = name
    initList()
  }
})
const searchNode = uni.upx2px(70)
const statusBarHeight = useStatusBar()
const systermInfo = uni.getSystemInfoSync()
// 可视区域滑动高度计算
const scrollViewHeight = computed(() => {
  return `${systermInfo.screenHeight - (searchNode + statusBarHeight.value)}px`
})

// 分页数据
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
// 是否触底
const loadMoreStatus = ref('nomore')
// 列表数据
const list = ref<Shops[]>([])
// 瀑布流组件
const waterFallRef = ref()
/**
 * 更新 or 刷新
 */
async function initList(isLoad = false) {
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    pageConfig.pages = 1
    loadMoreStatus.value = 'loadmore'
    list.value = await getGoodList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    list.value = list.value.concat(await getGoodList())
  }
}

// 搜索参数
const searchParams = reactive({
  name: '',
})
// 点击搜索
const handleSearch = (e: string) => {
  searchParams.name = e
  initList()
}
/**
 * 获取店铺列表
 */
async function getGoodList() {
  const { code, data, msg } = await doGetshops({ ...searchParams, ...pageConfig })
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: `${msg || '获取商品列表失败'}`,
    })
    return []
  }
  if (data) {
    pageConfig.pages = data.pages
    if (data.pageNum >= data.pages) {
      loadMoreStatus.value = 'loadmore'
    } else {
      loadMoreStatus.value = 'nomore'
    }
    return data.records
  }
  return []
}
/**
 * 跳转页面
 * @param {*} e
 */
const handleItemClick = (e: any) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${e}`,
  })
}
</script>

<style scoped lang="scss">
@include b(search) {
  background: #fff;
  padding: 6rpx 16rpx 0 16rpx;
  // #ifndef MP-WEIXIN
  @include flex(flex-start);
  // #endif
}
.shopInfo {
  padding: 0 40rpx;
  font-weight: 700;
  margin-top: 20rpx;
  .u-btn {
    margin: 20rpx 0 20rpx;
    font-weight: normal;
    font-size: 30rpx;
    height: 60rpx;
    width: 280rpx;
  }
}
</style>
