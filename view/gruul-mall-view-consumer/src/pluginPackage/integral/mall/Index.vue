<script setup lang="ts">
import { ref, reactive, onBeforeMount, computed } from 'vue'
import IntegralHead from '@pluginPackage/integral/mall/components/integral-head.vue'
import Auth from '@/components/auth/auth.vue'
import { EMPTY_GB } from '@/constant'
import { doGetIntegralProductList } from '@pluginPackage/integral/api'
import IntegralGoods from '@pluginPackage/integral/mall/components/integral-goods.vue'
import type { IntegralGoodsType } from '@/apis/plugin/integral/model'

const list = ref<IntegralGoodsType[]>([])
const pageConfig = reactive({
  status: 'SELL_ON',
  size: 5,
  current: 1,
  pages: 1,
})

/**
 * 积分商品列表
 */
async function initIntegralProductList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    const data = await getIntegralProductList(pageConfig)
    list.value = data.records
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    const data = await getIntegralProductList({
      ...pageConfig,
      current: pageConfig.current + 1,
    })
    if (data) {
      pageConfig.current++
      list.value = list.value.concat(data.records)
    }
  }
}
async function getIntegralProductList(config: typeof pageConfig) {
  try {
    const { code, data, msg } = await doGetIntegralProductList(config)
    if (code !== 200) {
      uni.showToast({ title: `${msg ? msg : '获取积分商品列表失败'}`, icon: 'none' })
      return []
    }
    pageConfig.pages = data.pages
    return data
  } catch (error) {
    uni.showToast({ title: '获取积分商品列表失败', icon: 'none' })
    return []
  }
}

onBeforeMount(() => {
  initIntegralProductList()
})

/**
 * 积分商品详情跳转
 * @param shopId 积分商品为平台发布 shopId 传 0
 */
function commodityNavMethod(goods: IntegralGoodsType) {
  uni.navigateTo({
    url: `/pluginPackage/integral/mall/components/commodityInfo/InfoEntrance?goodId=${goods.id}&shopId=0`,
  })
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="initIntegralProductList(true)">
    <IntegralHead />
    <template v-if="list.length">
      <view class="physical_commodity">积分商品</view>
      <view class="guessList">
        <u-waterfall v-if="list.length" ref="waterFallRef" v-model="list">
          <template #left="{ leftList }">
            <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="commodityNavMethod(item)">
              <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
              <IntegralGoods
                :good-name="item.name"
                :price="item.price || ''"
                :integral-price="item.integralPrice"
                :commodity-info="item"
                :sale-price="item.salePrice"
              />
            </view>
          </template>
          <template #right="{ rightList }">
            <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="commodityNavMethod(item)">
              <u-lazy-load threshold="-450" border-radius="16" :image="item.pic" :index="index"></u-lazy-load>
              <IntegralGoods
                :good-name="item.name"
                :price="item.price || ''"
                :integral-price="item.integralPrice"
                :commodity-info="item"
                :sale-price="item.salePrice"
              />
            </view>
          </template>
        </u-waterfall>
      </view>
    </template>
    <!-- 空列表展示 -->
    <view v-else style="display: flex; align-items: center; justify-content: center; height: 500rpx; flex-direction: column">
      <image :src="EMPTY_GB.CART_EMPTY" style="width: 300rpx; height: 200rpx"></image>
      <text style="color: #c8c9cc; font-size: 24rpx">暂无积分商品</text>
    </view>
    <Auth />
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(physical_commodity) {
  @include flex;
  padding: 20rpx;
  justify-content: flex-start;
  font-weight: 700;
  &::before {
    content: '';
    display: inline-block;
    width: 8rpx;
    height: 30rpx;
    background: #ee553e;
    border-radius: 15rpx;
    margin-right: 10rpx;
  }
}
@include b(guessList) {
  padding: 0 28rpx 200rpx 28rpx;
  box-sizing: border-box;
}
</style>
