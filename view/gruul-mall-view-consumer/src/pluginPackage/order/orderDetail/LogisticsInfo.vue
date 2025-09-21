<script setup lang="ts">
import { onLoad } from '@dcloudio/uni-app'
import { reactive, toRefs, ref, computed } from 'vue'
import { useQueryLogistics } from '@/pluginPackage/order/orderList/hooks/logisticsHooks'
import { doGetDeliveryPackage, doGetLogisticsTrajectoryByWaybillNo } from '@/apis/order'
import LogisticsSteps from './components/logistics-steps.vue'
import type { ApiLogistics01 } from '@/pluginPackage/order/orderDetail/types'
import type { ApiOrder, ShopOrderItem } from '@/pluginPackage/order/orderList/types'

const LogisticsData = reactive({
  pathQuery: {
    orderNo: '',
    shopId: '',
    shopOrderNo: '',
  },
  deliveryPackages: [] as Array<ApiLogistics01>,
  current: 0,
  scrollLeft: 0,
  imageWidth: 130, // rpx
  imageMarginRight: 42, // rpx
  orderLocation: [{ area: [] as string[], areaName: '', context: '', ftime: '', status: '', time: '' }],
})
const shopOrderMap = ref(new Map<string, Array<{ image: string; productName: string; id: string; num: number }>>())
const orderInfo = ref<Array<Array<{ image: string; productName: string; id: string; num: number }>>>([])
const { getLogisticsStorage } = useQueryLogistics()
const { scrollLeft, pathQuery, deliveryPackages, current, imageWidth, imageMarginRight, orderLocation } = toRefs(LogisticsData)
const moveNum = computed(() => uni.upx2px(imageWidth.value) + uni.upx2px(imageMarginRight.value))
const max = (15 - 4) * moveNum.value
const tabs = computed(() => deliveryPackages.value.map((item, index) => ({ name: `包裹${index + 1}` })))

onLoad((res: any) => {
  initPathQuery(res)
  initDeliveryPackage()
  const storage = getLogisticsStorage() as ApiOrder
  orderInfo.value = initParcel(storage)
})

/**
 * 格式化包裹信息
 * @param {*} storage
 */
function initParcel(storage: ApiOrder) {
  shopOrderMap.value = new Map<string, Array<{ image: string; productName: string; id: string; num: number }>>()
  storage.shopOrders[0].shopOrderItems.forEach((item) => {
    if (item.packageId) {
      const packageObj = shopOrderMap.value.get(item.packageId)
      const { image, productName, packageId, num } = item
      if (packageObj) {
        packageObj.push({ image, productName, id: packageId, num })
      } else {
        shopOrderMap.value.set(item.packageId, [
          {
            id: packageId,
            image,
            productName,
            num,
          },
        ])
      }
    }
  })
  return Array.from(shopOrderMap.value.values())
}
function initPathQuery(res: Obj) {
  for (const key in res) {
    pathQuery.value[key as keyof typeof pathQuery.value] = res[key]
  }
}

const change = (e: any) => {
  initCurrentLogistics(deliveryPackages.value[e])
}
const scroll = (e: any) => {
  scrollLeft.value = e.detail.scrollLeft
}
const handleScrollChangeR = async () => {
  if (scrollLeft.value + moveNum.value >= max) return
  // @ts-ignore
  uni.$u.throttle(() => {
    scrollLeft.value += moveNum.value
  }, 300)
}
const handleScrollChangeL = () => {
  if (scrollLeft.value <= 0) return
  // @ts-ignore
  uni.$u.throttle(() => {
    scrollLeft.value -= moveNum.value
  }, 300)
}
const handleCopyOrderNo = (no: string) => {
  uni.setClipboardData({
    data: no,
    showToast: false,
    success: function () {
      uni.showToast({ title: '复制成功', icon: 'none' })
    },
  })
}
async function initDeliveryPackage() {
  const { orderNo, shopId, shopOrderNo } = pathQuery.value
  const { code, data, msg } = await doGetDeliveryPackage(orderNo, shopOrderNo, shopId)
  if (code !== 200) {
    uni.showToast({ title: `${msg || '查询发货包裹失败'}`, icon: 'none' })
    return
  }
  deliveryPackages.value = data
  initCurrentLogistics(deliveryPackages.value[current.value])
}
/**
 * 初始化当前物流
 * @param {*} companyCode
 * @param {*} waybillNo
 */
async function initCurrentLogistics(query: ApiLogistics01) {
  const { expressCompanyCode, expressNo, receiverMobile, shopId } = query
  if (!expressCompanyCode || !expressNo) return
  // companyCode: string, waybillNo: string, recipientsPhone?: string
  const { code, data, msg } = await doGetLogisticsTrajectoryByWaybillNo(expressCompanyCode, expressNo, shopId, receiverMobile)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取物流追踪失败'}`, icon: 'none' })
    return
  }
  if (!data.status || data.status !== '200') {
    orderLocation.value[0].context = data.message
    return
  }
  if (data.data && Array.isArray(data.data)) {
    orderLocation.value = data.data.reverse()
  }
  console.log('orderLocation.value', orderLocation.value)
}
</script>

<template>
  <view class="tabs">
    <u-tabs v-model="LogisticsData.current" :height="96" active-color="#fa3534" :list="tabs" @change="change" />
  </view>
  <!-- 商品信息 s -->
  <view class="goods-container">
    <view style="padding: 0 20rpx">商品信息</view>
    <view class="goods-container__image_box">
      <view
        v-show="orderInfo[LogisticsData.current]?.length > 4"
        style="width: 50rpx; height: 130rpx; display: flex; align-items: center"
        class="goods-container__image_box--icon-left"
        @click="handleScrollChangeL"
      >
        <u-icon v-show="orderInfo[LogisticsData.current]?.length > 4" name="arrow-left" color="#2979ff" size="28"></u-icon>
      </view>
      <scroll-view
        scroll-x
        enhanced
        :show-scrollbar="false"
        scroll-with-animation
        style="white-space: nowrap; width: calc(100vw - 100rpx); margin: 20rpx 50rpx 0 50rpx"
        :scroll-left="scrollLeft"
        @scroll="scroll"
      >
        <view style="display: flex">
          <view v-for="(i, idx) in orderInfo[LogisticsData.current]" :key="idx" style="position: relative">
            <u-image
              class="image-scroll"
              :width="imageWidth"
              :height="130"
              :style="{ marginRight: `${imageMarginRight}rpx`, flexShrink: 0 }"
              :src="i.image"
            >
            </u-image>
            <u-badge size="mini" type="error" :offset="[0, 20]" :count="i.num"></u-badge>
          </view>
        </view>
      </scroll-view>
      <view v-show="orderInfo[LogisticsData.current]?.length > 4" class="goods-container__image_box--icon-right" @click="handleScrollChangeR">
        <u-icon name="arrow-right" color="#2979ff" size="28"></u-icon>
      </view>
    </view>
  </view>
  <!-- 商品信息 e -->
  <!-- 物流信息 s -->
  <view class="logistics-container">
    <view style="padding: 0 0 0 20rpx">物流信息</view>
    <view style="padding: 20rpx 50rpx">
      <template v-if="deliveryPackages?.length && deliveryPackages[LogisticsData.current].type === 'EXPRESS'">
        <view class="logistics-container__label">
          快递公司&nbsp;:&nbsp;<text class="logistics-container__value">
            {{ deliveryPackages[LogisticsData.current].expressCompanyName }}
          </text>
        </view>
        <view class="logistics-container__label">
          快递单号&nbsp;:&nbsp;<view class="logistics-container__no">
            <text class="logistics-container__value">{{ deliveryPackages[LogisticsData.current].expressNo }}</text>
            <text class="logistics-container__no--copy" @click="handleCopyOrderNo(deliveryPackages[LogisticsData.current].expressNo)"> 复制 </text>
          </view>
        </view>
        <view class="logistics-container__label">官方电话&nbsp;:&nbsp;<text class="logistics-container__value">0987-923834234 </text></view>
      </template>
      <view v-else class="logistics-container__label"> 无需物流配送 </view>
    </view>
  </view>
  <!-- 物流信息 e -->
  <!-- 物流步骤条 -->
  <view v-if="deliveryPackages[LogisticsData.current]?.type === 'EXPRESS'" class="logistics-steps">
    <LogisticsSteps :context="orderLocation"></LogisticsSteps>
  </view>
  <!-- 物流步骤条 -->
</template>

<style scoped lang="scss">
@include b(tabs) {
  border-bottom: 1px solid #e7e7e7;
  border-top: 1px solid #e7e7e7;
}
@include b(goods-container) {
  padding: 20rpx 0;
  background: #fff;
  @include e(image_box) {
    position: relative;
    @include m(icon-left) {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      left: 0;
    }
    @include m(icon-right) {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      right: 0;
    }
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
  height: 40vh;
}
</style>
