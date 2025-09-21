<template>
  <view v-if="loading" style="text-align: center; margin-top: 80rpx">
    <u-loading mode="flower" size="60"></u-loading>
  </view>
  <view v-else>
    <DetailHeader :order="detailOrder" />
    <view style="padding: 0 20rpx">
      <DetailAddress :order="detailOrder" />
      <view class="goods baseCard">
        <image class="goods__image" :src="detailOrder?.image" mode="scaleToFill" />
        <view class="goods__info">
          <view class="goods__info--name" style="margin-top: 20rpx">{{ detailOrder?.productName }}</view>
          <view class="goods__info-price">
            <Price :integral="detailOrder?.price" :salePrice="detailOrder?.salePrice" />
            <view>X{{ detailOrder?.num }}</view>
          </view>
        </view>
      </view>
      <DetailPrice :order="detailOrder" />
      <view class="orderInfo baseCard">
        <view class="orderInfo__line">
          <view>
            订单号:<text class="orderInfo__line--content"> &nbsp;{{ detailOrder?.no }}</text>
          </view>
          <text style="color: #005cf4" @click="setClipboard(detailOrder?.no)">复制</text>
        </view>
        <view v-for="(item, index) in orderList" :key="index" class="orderInfo__line">
          <view>
            {{ index }}:<text class="orderInfo__line--content"> &nbsp;{{ item }}</text>
          </view>
        </view>
      </view>
      <DetailFooter :status="detailOrder?.status" :order="detailOrder" @init-order="handleGetOredrDetail" />
    </view>

    <view class="toHome" @click="handleGoHome">
      <q-icon name="icon-shouye1" size="50rpx" color="#FA3534"></q-icon>
    </view>
  </view>
  <Auth />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

import { useSettingStore } from '@/store/modules/setting'

import { doGetIntegralOrderDetail } from '@/pluginPackage/integral/api'
import type { IOrderList } from '@/pluginPackage/integral/api/types'

import DetailHeader from './components/detailHeader.vue'
import DetailAddress from './components/detailAddress.vue'
import DetailPrice from './components/detailPrice.vue'
import DetailFooter from './components/detailFooter.vue'
import Price from '../../components/price.vue'
import Auth from '@/components/auth/auth.vue'

let orderNo = ''
onLoad((res: AnyObject | undefined) => {
  if (res) {
    orderNo = res.orderNo
    handleGetOredrDetail()
  }
})

const $settingStore = useSettingStore()
/**
 * 导航去首页
 */
const handleGoHome = () => {
  $settingStore.NAV_TO_INDEX('首页')
}

const detailOrder = ref<IOrderList>()

type orderListType = {
  创建时间: string
  支付时间?: string
  支付方式?: string
}

const orderList = ref<orderListType>({
  创建时间: '',
})

const loading = ref(false)

const handleGetOredrDetail = async () => {
  loading.value = true
  const { code, data, msg } = await doGetIntegralOrderDetail(orderNo)
  if (code !== 200 || !data) {
    uni.showToast({ title: `${msg ? msg : '获取订单详情失败'}`, icon: 'none' })
  } else {
    detailOrder.value = data
    orderList.value.创建时间 = data.createTime
    if (data.integralOrderPayment) {
      orderList.value.支付时间 = data.integralOrderPayment.payTime
      orderList.value.支付方式 = payType[data.integralOrderPayment.payType || '']
    }
  }
  loading.value = false
}
enum payType {
  '' = '积分',
  BALANCE = '积分+余额支付',
  WECHAT = '积分+微信支付',
  ALIPAY = '积分+支付宝支付',
}
const setClipboard = (data = '') => {
  uni.setClipboardData({
    data,
    showToast: true,
  })
}
</script>

<style scoped lang="scss">
.baseCard {
  background: rgb(255, 255, 255);
  border-radius: 20rpx;
  margin-top: 20rpx;
  padding: 20rpx;
}

@include b(goods) {
  display: flex;
  font-size: 24rpx;
  @include e(image) {
    width: 128rpx;
    height: 128rpx;
    border-radius: 10rpx;
    margin-right: 20rpx;
    flex-shrink: 1;
  }
  @include e(info) {
    flex: 1;
    @include m(name) {
      width: 526rpx;
      @include utils-ellipsis(1);
    }
  }
  @include e(info-price) {
    margin-top: 20rpx;
    font-size: 28rpx;
    display: flex;
    justify-content: space-between;
  }
}
@include b(orderInfo) {
  @include e(line) {
    display: flex;
    justify-content: space-between;
    color: #666666;
    margin-top: 10rpx;
    align-items: center;
    @include m(content) {
      color: #333333;
    }
  }
}
.toHome {
  position: fixed;
  right: 40rpx;
  bottom: 200rpx;
  border-radius: 60rpx;
  background-color: #fff;
  width: 96rpx;
  height: 96rpx;
  box-shadow: 0px 10px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  line-height: 96rpx;
}
</style>
