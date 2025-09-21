<script lang="ts" setup>
import { computed, ref, shallowReactive } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import QNav from '@/components/q-nav/q-nav.vue'
import DefaultEmpty from '@/components/qszr-core/packages/components/q-empty/default-empty.vue'
import OrderPayItem from '@/pluginPackage/order/orderList/components/order-pay-item.vue'
import OrderUnpaySpec from '@/pluginPackage/order/orderList/components/order-unpay-spec.vue'
import { useOrderMore } from '@/pluginPackage/store/modules/order'
import { queryStatus, useStatusBar } from '@/hooks'
import { EMPTY_GB } from '@/constant'
import { doGetOrder } from '@/apis/order'
import type { ApiOrder } from './types'
import useOrderDispatcherStore from '@/store/dispatcher/useOrderDispatcher'

const orderDispatcherStore = useOrderDispatcherStore()
const orderList = ref<ApiOrder[]>([])
const tabList = ref<{ name: string; label: string }[]>([])
const currentTabIndex = ref(0)
const isEmpty = ref(false)
const $useOrderMore = useOrderMore()
const pageConfig = shallowReactive({
  size: 10,
  current: 1,
})

initTab()
const dispatcher = (index: number) => initList(index)
onLoad((payload) => {
  uni.$emit('updateTitle')
  currentTabIndex.value = Number(payload?.id) || 0
  orderDispatcherStore.addSubscriber(() => dispatcher(currentTabIndex.value))
  initList(currentTabIndex.value)
})
onUnload(() => {
  pageConfig.current = 1
  orderDispatcherStore.removeSubscriber(dispatcher)
})

function initTab() {
  const temp = []
  temp.push({ name: '全部', label: '' })
  for (const [key, value] of Object.entries(queryStatus)) {
    temp.push({ name: value, label: key })
  }
  tabList.value = temp
}

async function initList(index: number, loadMore = false) {
  isEmpty.value = false
  const { size, current } = pageConfig
  const { code, data } = await doGetOrder({ size, current, status: tabList.value[index].label })
  if (code !== 200) return uni.showToast({ icon: 'none', title: '获取订单列表失败' })
  data.records.forEach((order: ApiOrder) => {
    $useOrderMore.setMoreMap(order)
  })
  if (loadMore) {
    orderList.value = orderList.value.concat(data.records)
  } else {
    orderList.value = data.records
  }
  isEmpty.value = true
}

/**
 * tabs切换 列表重置 获取新数据
 * @param {*} index
 */
const handletabsChange = (index: number) => {
  pageConfig.current = 1
  currentTabIndex.value = index
  orderList.value = []
  initList(index)
}
/**
 * 触底请求下一页
 */
const handleScrolltolower = () => {
  if (pageConfig.size * pageConfig.current > orderList.value.length) {
    return
  }
  pageConfig.current++
  initList(currentTabIndex.value, true)
}
// // 更多按钮发生变化逻辑处理
// const handleMoreChange = () => {
//     $useOrderMore.updateMoreMap('')
// }
const handleCancelOrder = () => {
  initList(currentTabIndex.value)
}

const qNavRef = ref()
const height = computed(() => {
  let contentHeight
  // #ifdef MP-WEIXIN
  contentHeight = useScreenHeight().value - useBottomSafe().value - uni.upx2px(90) - useStatusBar().value
  // #endif
  // #ifndef MP-WEIXIN
  contentHeight = useScreenHeight().value - useBottomSafe().value - uni.upx2px(90) - 44 - useStatusBar().value
  // #endif
  return contentHeight
})
</script>
<template>
  <view style="overflow: hidden">
    <q-nav ref="qNavRef" bg-color="#fff" title="我的订单" />
    <u-tabs
      v-model="currentTabIndex"
      :list="tabList"
      :show-bar="false"
      active-color="#F12F22"
      bg-color="#ffff"
      height="90"
      swiper-width="750"
      @change="handletabsChange"
    />
    <scroll-view v-show="orderList.length" scroll-y :style="{ height: `${height}px` }" class="order" @scrolltolower="handleScrolltolower">
      <view v-for="order in orderList" :key="order.no">
        <template v-if="order.status === 'UNPAID'">
          <OrderUnpaySpec :order="order" @cancel-order="handleCancelOrder" />
        </template>
        <template v-else>
          <OrderPayItem :currentTabIndex="currentTabIndex" :order="order" @update-order="handleCancelOrder" />
        </template>
      </view>
    </scroll-view>
    <!-- #ifndef MP-WEIXIN -->
    <DefaultEmpty v-if="isEmpty && !orderList.length" :background="EMPTY_GB.EVALUATION_EMPTY" height="700rpx" title="暂无订单" />
    <!-- #endif -->
    <!-- #ifdef MP-WEIXIN -->
    <u-empty v-if="isEmpty && !orderList.length" :src="EMPTY_GB.EVALUATION_EMPTY" font-size="30" icon-size="550" margin-top="200" text="暂无订单" />
    <!-- #endif -->
    <Auth />
  </view>
</template>

<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

@include b(order) {
  box-sizing: border-box;
  padding: 10rpx 12rpx;
  @include e(footer-btn) {
    margin-top: 16rpx;
    text-align: right;
  }
  @include e(item) {
    box-sizing: border-box;
    border-radius: 10rpx;
    background: #fff;
    margin-bottom: 14rpx;
    padding: 0 20rpx 20rpx 20rpx;
  }
  @include e(item-header) {
    @include flex(space-between);
    height: 90rpx;
    @include m(left) {
      @include flex();
    }
    @include m(right) {
      color: #f54319;
    }
  }
  @include e(realPay) {
    font-size: 28rpx;
    font-weight: bold;
    text-align: right;
    @include m(number) {
      font-size: 34rpx;
      &::before {
        content: '￥';
        display: inline-block;
      }
    }
  }
}

@include b(changeColor) {
  color: red !important;
}
</style>
