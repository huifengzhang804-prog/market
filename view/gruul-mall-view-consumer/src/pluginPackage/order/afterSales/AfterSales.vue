<script setup lang="ts">
import { computed, ref } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { doGetAfsOrder } from '@/apis/afs'
import AfterSalesItemInfo from '@pluginPackage/order/afterSales/components/afterSales-item-info.vue'
import { EMPTY_GB } from '@/constant'
import { getAfsStatusCn } from '@/pluginPackage/order/hooks/useAfssStatus'
import type { ApiOrderAfsItem } from '@pluginPackage/order/afterSales'
import Auth from '@/components/auth/auth.vue'

const { divTenThousand } = useConvert()
const afsOrderList = ref<ApiOrderAfsItem[]>([])
const isShowEmpty = ref(false)
const pageConfig = ref({ current: 1, size: 10, pages: 1, status: 'loadmore' })
const aRefundTypeCn = {
  REFUND: '退款',
  RETURN_REFUND: '退货退款',
}

initGetAfsOrder()

const totalPrice = (price: Long, num: number) => {
  return divTenThousand(price).mul(num)
}
const handleNavToShop = (shopId: Long) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
async function initGetAfsOrder(isLoad = false) {
  isShowEmpty.value = false
  if (!isLoad) {
    // 刷新
    pageConfig.value.current = 1
    afsOrderList.value = await getAfsOrderFn()
  } else if (isLoad && pageConfig.value.current < pageConfig.value.pages) {
    // 更新
    pageConfig.value.current++
    afsOrderList.value = afsOrderList.value.concat(await getAfsOrderFn())
  }
  isShowEmpty.value = true
}
async function getAfsOrderFn() {
  const { code, data, msg } = await doGetAfsOrder({ current: pageConfig.value.current, size: pageConfig.value.size })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取列表失败'}`, icon: 'none' })
    return []
  }
  if (pageConfig.value.current === data.pages) {
    pageConfig.value.status = 'nomore'
  }
  pageConfig.value.pages = data.pages
  return data.records
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="initGetAfsOrder(true)">
    <view class="afterSales">
      <view v-for="afsOrder in afsOrderList" :key="afsOrder.id" class="afterSales__item">
        <view class="afterSales__item-top">
          <view class="afterSales__item-top-left">
            <q-icon name="icon-dianpu" size="36rpx" />
            <view class="afterSales__item-top-left--name" @click="handleNavToShop(afsOrder.shopId)">
              <text class="shop-name">{{ afsOrder.shopName }}</text>
              <text style="font-weight: 400; color: #d5d5d5; flex-shrink: 0">&nbsp;&gt;</text>
            </view>
          </view>
          <view class="afterSales__item-top-right">
            <q-icon name="icon-tui" size="36rpx" color="#F54319" class="afterSales__item-top-right--icon" />
            <text class="afterSales__item-top-right--">{{ aRefundTypeCn[afsOrder.type] }}</text>
          </view>
        </view>
        <after-sales-item-info :info="afsOrder" />
        <view class="afterSales__item-footer">
          <text class="afterSales__item-footer--left">{{ getAfsStatusCn(afsOrder.status).list }}</text>
          <view class="afterSales__item-footer--right"
            >实付款<text class="afterSales__item-footer--right-price">{{
              totalPrice(afsOrder.afsOrderItem.dealPrice, afsOrder.afsOrderItem.num)
            }}</text></view
          >
        </view>
      </view>
      <q-empty v-if="isShowEmpty && !afsOrderList.length" title="暂无订单" :background="EMPTY_GB.EVALUATION_EMPTY" />
    </view>
    <Auth />
  </scroll-view>
</template>

<style scoped lang="scss">
@include b(afterSales) {
  padding: 0 10rpx;
  @include e(item) {
    margin-bottom: 14rpx;
    background: #ffff;
    border-radius: 15rpx;
    padding: 20rpx 20rpx 30rpx 20rpx;
  }
  @include e(item-top) {
    height: 40rpx;
    @include flex;
    justify-content: space-between;
    width: 690rpx;
    overflow: hidden;
  }
  @include e(item-top-left) {
    @include flex;
    flex: 1;
    overflow: hidden;
    @include m(name) {
      margin-left: 24rpx;
      margin-right: 14rpx;
      font-size: 26rpx;
      color: #333333;
      font-weight: 700;
      flex: 1;
      @include flex(flex-start);
      overflow: hidden;
    }
  }
  @include e(item-top-right) {
    @include flex;
    align-items: center;
    flex-shrink: 0;
    @include m(icon) {
      font-weight: 400;
      margin-right: 10rpx;
    }
  }
  @include e(item-footer) {
    @include flex(space-between);
    @include m(left) {
      font-size: 22rpx;
      font-weight: bold;
      color: #ffbf00;
    }
    @include m(right) {
      font-size: 28rpx;
      font-weight: bold;
      color: #000000;
    }
    @include m(right-price) {
      font-size: 34rpx;
      font-weight: bold;
      color: #000000;
      &::before {
        content: '￥';
        font-size: 26rpx;
        font-family: Bold;
        color: #000000;
      }
    }
  }
}
@include b(shop-name) {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
