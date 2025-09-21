<script setup lang="ts">
import type { PropType } from 'vue'
import type { AddressItemType } from '@/apis/address/model'
import type { IntegralGoodsInfoType } from '@/apis/plugin/integral/model'

const $props = defineProps({
  info: {
    type: Object as PropType<Omit<AddressItemType, 'id' | 'isDefault'>>,
    default() {
      return {}
    },
  },
  order: {
    type: Object as PropType<IntegralGoodsInfoType>,
    default() {
      return {}
    },
  },
})

const handleNavToAddress = () => {
  uni.navigateTo({
    url: `/basePackage/pages/addressManage/AddressManage?callKey=callConfirm`,
  })
}

// const address = ref('')
// const updateAreaStr = (areaStr: string) => {
//     address.value = areaStr + ' ' + $props.info.address
// }
</script>
<template>
  <view v-if="order.productType !== 'VIRTUAL_PRODUCT'" class="address" @click="handleNavToAddress">
    <template v-if="$props.info.area?.length">
      <view class="address__third">
        <q-icon name="icon-a-Frame5" color="#FA3534" size="42rpx" />
        &nbsp;
        <text style="font-size: 34rpx; color: #333">
          <text style="font-weight: 700">{{ $props.info.name }}</text>
          {{ $props.info.mobile }}
        </text>
      </view>

      <view style="margin-top: 30rpx; display: flex; align-items: center">
        <text class="detail">
          {{ $props.info.area.join(' ') }}
          {{ $props.info.address }}
        </text>
        <q-icon name="icon-youjiantou_huaban1" />
      </view>

      <view class="sendWay">
        <text> 配送方式 </text>
        <text>快递配送</text>
      </view>
    </template>
    <view v-else style="position: relative">
      <u-empty text="请选择收货地址" mode="address" icon-size="80" font-size="24" />
      <u-icon name="arrow-right" class="address__icon"></u-icon>
    </view>
  </view>
  <view v-else class="address">
    <view class="virtual_products">
      <text> 配送方式 </text>
      <text>{{ order.productType === 'VIRTUAL_PRODUCT' ? '无需物流' : '快递配送' }}</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@include b(address) {
  padding: 34rpx 46rpx 20rpx 36rpx;
  background: #fff;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #000;
  @include e(icon) {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translate(-50%, -50%);
  }
  @include e(fir) {
    @include flex(flex-start);
    @include m(btn) {
      width: 160rpx;
      height: 50rpx;
      text-align: center;
      line-height: 50rpx;
      color: #e57a77;
      border: 2rpx solid #e57a77;
      background: #faeeee;
      border-radius: 40rpx;
      margin-right: 28rpx;
    }
  }
  @include e(sec) {
    @include flex(space-between);
    margin: 24rpx 0;
    @include m(text) {
      width: 95%;
    }
  }
  @include e(third) {
    font-size: 26rpx;
    color: #777777;
  }
}
.detail {
  width: 640rpx;
  font-size: 28rpx;
  color: #666;
  @include utils-ellipsis(1);
}

.sendWay {
  margin-top: 20rpx;
  height: 60rpx;
  padding-top: 20rpx;
  border-top: 1px dashed rgb(189, 189, 189);
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #999999;
}
.virtual_products {
  padding-bottom: 15rpx;
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #999999;
}
</style>
