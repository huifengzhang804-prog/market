<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { reactive, toRefs, computed, ref } from 'vue'
import { doGetLogisticsTrajectoryByWaybillNo } from '@/apis/order'
import LogisticsSteps from '@/pluginPackage/order/orderDetail/components/logistics-steps.vue'

const LogisticsData = reactive({
  deliveryPackages: { expressCompanyName: '', expressNo: '' },
  orderLocation: [{ area: [] as string[], areaName: '', context: '', ftime: '', status: '', time: '' }],
})
const { deliveryPackages, orderLocation } = toRefs(LogisticsData)
const pathQuery = reactive({
  expressCompanyCode: '',
  expressNo: '',
  receiverMobile: '',
  img: '',
  expressName: '',
})
onLoad((res) => {
  if (res) {
    Object.assign(pathQuery, res)
    deliveryPackages.value = {
      expressCompanyName: res.expressName,
      expressNo: res.expressNo,
    }
    initCurrentLogistics()
  }
})

const handleCopyOrderNo = (no: string) => {
  uni.setClipboardData({ data: no })
}

/**
 * 初始化当前物流
 * @param {*} companyCode
 * @param {*} waybillNo
 */
const loading = ref(false)
async function initCurrentLogistics() {
  loading.value = true
  try {
    const { expressCompanyCode, expressNo, receiverMobile } = pathQuery
    const { code, data, msg } = await doGetLogisticsTrajectoryByWaybillNo(expressCompanyCode, expressNo, '', receiverMobile)
    if (code !== 200) {
      uni.showToast({ title: `${msg ? msg : '获取物流追踪失败'}`, icon: 'none' })
    } else if (!data.status || data.status !== '200') {
      orderLocation.value[0].context = data.message
    } else if (data.data && Array.isArray(data.data)) {
      orderLocation.value = data.data.reverse()
    }
  } catch (error) {
    uni.showToast({ title: '获取物流追踪失败', icon: 'none' })
  }
  loading.value = false
}
const isExpress = computed(() => {
  const { expressCompanyName, expressNo } = deliveryPackages.value
  return expressCompanyName !== 'undefined' && expressNo !== 'undefined'
})
</script>

<template>
  <!-- 商品信息 s -->
  <view class="goods-container">
    <view style="padding: 0 20rpx">商品信息</view>
    <view class="goods-container__image_box">
      <view style="display: flex">
        <u-image class="image-scroll" :width="130" :height="130" :style="{ flexShrink: 0 }" :src="pathQuery.img"> </u-image>
      </view>
    </view>
  </view>
  <!-- 商品信息 e -->
  <!-- 物流信息 s -->
  <view class="logistics-container">
    <view style="padding: 0 0 0 20rpx">物流信息</view>
    <view style="padding: 20rpx 50rpx">
      <view v-if="!isExpress"> 无需物流 </view>
      <view v-else>
        <view class="logistics-container__label">
          快递公司&nbsp;:&nbsp;<text class="logistics-container__value">
            {{ deliveryPackages.expressCompanyName }}
          </text>
        </view>
        <view class="logistics-container__label">
          快递单号&nbsp;:&nbsp;<view class="logistics-container__no">
            <text class="logistics-container__value">{{ deliveryPackages.expressNo }}</text>
            <text class="logistics-container__no--copy" @click="handleCopyOrderNo(deliveryPackages.expressNo)"> 复制 </text>
          </view>
        </view>
        <view class="logistics-container__label">官方电话&nbsp;:&nbsp;<text class="logistics-container__value">0987-923834234 </text></view>
      </view>
    </view>
  </view>
  <!-- 物流信息 e -->
  <!-- 物流步骤条 -->
  <view v-if="isExpress" class="logistics-steps">
    <logistics-steps :context="orderLocation" :loading="loading"></logistics-steps>
  </view>
  <!-- 物流步骤条 -->
</template>

<style scoped lang="scss">
@include b(goods-container) {
  padding: 20rpx 0;
  background: #fff;
  @include e(image_box) {
    margin: 20rpx 52rpx;
  }
}
@include b(logistics-container) {
  background: #fff;
  @include e(label) {
    margin-bottom: 15rpx;
    display: flex;
    color: #666;
    font-size: 26rpx;
  }
  @include e(value) {
    color: #333333;
  }
  @include e(no) {
    flex: 1;
    display: flex;
    justify-content: space-between;
    @include m(copy) {
      color: #005cf4;
    }
  }
}
@include b(logistics-steps) {
  margin-top: 20rpx;
  background: #fff;
}
</style>
