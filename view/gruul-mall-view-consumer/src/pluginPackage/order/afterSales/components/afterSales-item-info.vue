<script setup lang="ts">
import type { PropType } from 'vue'
import storage from '@/utils/storage'
import type { ApiOrderAfsItem } from '@pluginPackage/order/afterSales'

const $props = defineProps({
  info: { type: Object as PropType<ApiOrderAfsItem>, required: true },
})
const { divTenThousand } = useConvert()

function setAfsOrderInfo(afsItem: ApiOrderAfsItem) {
  if (afsItem.no) {
    const { no: afsNo, afsOrderItem, refundAmount: dealPrice, status: afsstatus, packageId, shopId, shopOrderItemId: id } = afsItem
    const { productId, image, num, productName, salePrice, services, skuId, specs } = afsOrderItem
    const params = {
      productId,
      afsNo,
      dealPrice,
      afsstatus,
      packageId,
      id,
      shopId,
      image,
      num,
      productName,
      salePrice,
      services,
      skuId,
      specs,
    }
    storage.set('applyAfterSalesOrder', params)
  }
}
const handleClick = (afsItem: ApiOrderAfsItem) => {
  setAfsOrderInfo(afsItem)
  uni.navigateTo({
    url: `/pluginPackage/order/detailsRefund/DetailsRefund?no=${afsItem.no}&packageId=${afsItem.packageId}`,
  })
}
</script>
<template>
  <view class="item" @click="handleClick($props.info)">
    <view class="item__main-left">
      <image class="item__main-left--image" :src="$props.info.afsOrderItem.image" />
      <view class="item__main-left-text">
        <view class="item__main-left-text-title">
          <text class="item__main-left-text-title--main">{{ $props.info.afsOrderItem.productName }}</text>
          <text class="item__main-left-text-title--num">{{ divTenThousand($props.info.afsOrderItem.dealPrice) }}</text>
        </view>
        <view class="item__main-left-text-msg">
          <text v-if="$props.info.afsOrderItem.specs" class="item__main-left-text-msg--left">{{ $props.info.afsOrderItem.specs.join(' ; ') }}</text>
          <text v-else />
          <text class="item__main-left-text-msg--right">×{{ $props.info.afsOrderItem.num }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(item) {
  height: 200rpx;
  margin: 24rpx 0;
  @include e(main-left) {
    @include flex(space-between);
    @include m(image) {
      width: 200rpx;
      height: 200rpx;
      border-radius: 30rpx;
      margin-right: 22rpx;
    }
  }

  @include e(main-left-text) {
    flex: 1;
    height: 200rpx;
    padding: 5rpx 0;
  }
  @include e(main-left-text-title) {
    font-size: 28rpx;
    font-weight: Bold;
    color: #000000;
    @include flex(space-between);
    @include m(main) {
      width: 328rpx;
      @include utils-ellipsis;
      margin-bottom: 14rpx;
    }
    @include m(num) {
      font-size: 24rpx;
      color: #121212;
      &::before {
        content: '￥';
      }
    }
  }
  @include e(main-left-text-msg) {
    @include flex(space-between);
    margin-bottom: 10rpx;
    @include m(main) {
      width: 328rpx;
      @include utils-ellipsis;
    }
    @include m(left) {
      font-size: 24rpx;
      font-weight: normal;
      color: #777777;
    }
    @include m(num) {
      font-size: 24rpx;
      color: #121212;
    }
  }
}
</style>
