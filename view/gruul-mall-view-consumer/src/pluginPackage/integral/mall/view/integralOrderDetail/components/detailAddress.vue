<template>
  <view class="address-container f12">
    <view v-if="interalAddress?.name || interalAddress?.mobile" class="fcenter">
      <q-icon color="#FA3534" name="icon-dizhi2" size="50rpx" style="margin-right: 10rpx; margin-bottom: 15rpx" />
      <view class="fdc">
        <view class="flex-space-between address-container__top">
          <text class="address-container__name">{{ interalAddress?.name }}&nbsp;&nbsp;</text>
          <text class="address-container__name" style="font-weight: 500">{{ interalAddress?.mobile }}</text>
        </view>
        <view
          v-if="interalAddress?.area?.join('') || interalAddress?.address?.replace(/\s/g, '')"
          class="address-container__info flex-space-between"
          style="margin-top: 15rpx; font-size: 24rpx"
          @click="handleNavToAddress"
        >
          <text>{{ `${interalAddress?.area?.join('')} ${interalAddress?.address?.replace(/\s/g, '')}` }}</text>
          <u-icon v-if="order.status === 'UNPAID'" color="" name="arrow-right" size="25"></u-icon>
        </view>
      </view>
    </view>
    <view :style="{ paddingTop: interalAddress?.name || interalAddress?.mobile ? '15rpx' : '' }" class="address-container__fn flex-space-between f13">
      <text>配送方式</text>
      <text>{{ order.productType === 'VIRTUAL_PRODUCT' ? '无需物流' : '快递配送' }}</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { computed, onUnmounted, type PropType, ref } from 'vue'
import { doGetIntegralReceiver } from '@/pluginPackage/integral/api'
import type { IOrderList } from '@/pluginPackage/integral/api/types'
import type { TConfirmAddress } from '@/hooks/useChooseAddress'
import { useChosseAddress } from '@/hooks/useChooseAddress'
// :order-address="detailOrder?.integralOrderReceiver" :no="detailOrder?.no"
const props = defineProps({
  order: {
    type: Object as PropType<IOrderList>,
    default: () => ({}),
  },
})

const interalAddress = ref(props.order.integralOrderReceiver)

// 确认订单页面地址变更
const { interalAreasAddress } = useChosseAddress()
const { getInteralAreasAddress, offInteralAreasAddress } = interalAreasAddress()

const handleNavToAddress = () => {
  if (props.order.status === 'UNPAID') {
    uni.navigateTo({
      url: `/basePackage/pages/addressManage/AddressManage?callKey=integral`,
    })
  }
}

getInteralAreasAddress(async (e: TConfirmAddress) => {
  const { code, msg } = await doGetIntegralReceiver(props.order.no, interalAddress.value!)
  if (code !== 200) return uni.showToast({ title: `${msg || '修改地址失败失败'}`, icon: 'none' })
  interalAddress.value = { ...interalAddress.value, ...e }
})

onUnmounted(() => {
  offInteralAreasAddress()
})
const showAddress = computed(() => {
  return props.order.productType !== 'VIRTUAL_PRODUCT'
})
</script>

<style lang="scss" scoped>
@include b(address-container) {
  background: #fff;
  margin: 15rpx 10rpx;
  padding: 20rpx;
  border-radius: 15rpx;
  .fcenter {
    display: flex;
    align-items: center;
    border-bottom: 1px dashed #bdbdbd;
  }
  .fdc {
    flex-direction: column;
    padding-bottom: 15rpx;
  }
  @include e(top) {
    justify-content: flex-start;
  }
  @include e(info) {
  }
  @include e(name) {
    color: #222;
    font-size: 34rpx;
    font-weight: 700;
  }
  @include e(fn) {
    font-size: 24rpx;
    color: #999;
  }
}
</style>
