<script setup lang="ts">
import { ref } from 'vue'
import PopupTitle from '@/components/popup/popup-title.vue'

const show = ref(false)

function switchPopup(val: boolean) {
  show.value = val
}
const handleClose = () => {
  show.value = false
}
defineExpose({ switchPopup })
</script>

<template>
  <u-popup v-model="show" mode="bottom">
    <popup-title title="返利订单说明" @close="handleClose" />
    <view style="padding: 0 20rpx 30rpx 20rpx">
      <view class="content mb30"> 统计您的所有返利订单，消费返利以商品为统计粒度计算。</view>
      <view class="content fontBold"> 订单状态</view>
      <view class="content"> 1、已付款：是指支付成功，订单处于待发货或物流配送中 </view>
      <view class="content"> 2、已完成：是指商品客户已确认收货未退款或部分商品退款 </view>
      <view class="content mb30"> 3、已关闭：是指订单内所有商品都【退款成功】 </view>
      <view class="content fontBold"> 结算状态</view>
      <view class="content"> 1、待结算：是指订单未完结(可能退款退货等情况)暂不计入累计返利； </view>
      <view class="content"> 2、已返：是指对应商品没有退款(或退款失败)，返利统计到【累计返利】中 </view>
      <view class="content mb30"> 3、已失效：是指对应商品已退款成功，您无法获得对应返利 </view>
      <view class="content fontBold"> 返利统计维度</view>
      <view class="content"> 1、累计返利：已返的返利之和 </view>
      <view class="content"> 2、待结算返利：待结算返利之和 </view>
      <view class="content mb30"> 3、已失效返利：已失效返利之和 </view>
    </view>
  </u-popup>
</template>

<style scoped lang="scss">
.mb30 {
  margin-bottom: 30rpx;
}
</style>
