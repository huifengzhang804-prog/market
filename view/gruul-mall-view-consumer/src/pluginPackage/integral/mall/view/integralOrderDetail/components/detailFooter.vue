<!-- 展示条件 -->
<!-- 已关闭订单不展示footer -->
<template>
  <view v-if="status !== 'SYSTEM_CLOSED'" class="footer">
    <view style="text-align: center" @click="jumpToService">
      <q-icon name="icon-a-Frame6" size="40rpx"></q-icon>
      <view>联系客服</view>
    </view>
    <u-modal
      v-model="receiptVisible"
      :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
      :show-cancel-button="true"
      :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
      confirm-color="#FA3534"
      confirm-text="确定"
      @cancel="() => (receiptVisible = false)"
      @confirm="confirmReceipt"
    >
      <view class="login-box"> 请您确认是否收货？</view>
    </u-modal>
    <view style="display: flex">
      <!-- btn 展示规则 -->
      <!-- 待支付 展示去支付按钮 -->
      <!-- 待发货 不展示按钮  -->
      <!-- 待收货 展示查看物流按钮 + 确认收货按钮 -->
      <!-- 已完成 展示查看物流  -->
      <!-- 虚拟不展示物流 -->
      <view v-if="currentMap?.primaryBtn && order.productType !== 'VIRTUAL_PRODUCT'" class="btn" @click="currentMap?.primaryFun"
        >{{ currentMap?.primaryBtn }}
      </view>
      <view v-if="currentMap?.errBtn" class="btn errBtn" @click="currentMap?.errFun"> {{ currentMap?.errBtn }}</view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { doPutCompleteIntegralOrder } from '@/pluginPackage/integral/api'
import type { orderStatus, IOrderList } from '@/pluginPackage/integral/api/types'

import { computed, type PropType, ref, watch } from 'vue'

const props = defineProps({
  status: {
    type: String as PropType<Exclude<keyof typeof orderStatus, ''>>,
    default: '',
  },
  order: {
    type: Object as PropType<IOrderList>,
    default: () => ({}),
  },
})
watch(
  () => props.order,
  (val) => {
    console.log('ahdsjahsdui ', val)
  },
  {
    deep: true,
    immediate: true,
  },
)
const jumpToService = () => {
  uni.navigateTo({
    url: '/basePackage/pages/customerService/CustomerService?shopName=平台客服',
  })
}
const priceTotal = computed(() => String(+props.order.freightPrice + +props.order.salePrice))
// 去支付
const toPay = () => {
  const { price, no, productId } = props.order
  // 支付运费
  const extra = encodeURIComponent(JSON.stringify({ ruleId: productId }))
  const infoQuery = `?no=${no}&orderType=INTEGRAL&extra=${extra}`
  const priceQuery = `&integral=${price}&price=${priceTotal.value}`
  uni.redirectTo({
    url: `/basePackage/pages/pay/Index${infoQuery + priceQuery}`,
  })
  // uni.navigateTo({
  //     path: ''
  // })
}

const receiptUpdate = () => {
  receiptVisible.value = true
}

const receiptVisible = ref(false)

const emit = defineEmits(['init-order'])
//确认收货
const confirmReceipt = async () => {
  const { code, msg } = await doPutCompleteIntegralOrder(props.order.no)
  if (code !== 200) return uni.showToast({ title: `${msg || '收货失败'}`, icon: 'none' })
  emit('init-order')
}

// 查看物流
const viewLogistics = () => {
  const { expressCompanyName, expressNo, integralOrderReceiver, image, expressName } = props.order
  const query = `expressCompanyCode=${expressCompanyName}&expressNo=${expressNo}&receiverMobile=${integralOrderReceiver?.mobile}&expressName=${expressName}&img=${image}`
  uni.navigateTo({ url: `/pluginPackage/integral/mall/view/integralLogistics/integralLogistics?${query}` })
}

const mapStatus = {
  UNPAID: {
    errBtn: '去支付',
    errFun: toPay,
    primaryBtn: '',
    primaryFun: () => {},
  },
  PAID: {
    errBtn: '',
    primaryBtn: '',
    primaryFun: () => {},
    errFun: () => {},
  },
  ON_DELIVERY: {
    errBtn: '确认收货',
    errFun: receiptUpdate,
    primaryBtn: '查看物流',
    primaryFun: viewLogistics,
  },
  ACCOMPLISH: {
    errBtn: '',
    primaryBtn: '查看物流',
    primaryFun: viewLogistics,
    errFun: () => {},
  },
  SYSTEM_CLOSED: {
    errBtn: '',
    primaryBtn: '',
    primaryFun: () => {},
    errFun: () => {},
  },
}

const currentMap = computed(() => mapStatus[props.status])
</script>

<style lang="scss" scoped>
.login-box {
  height: 120rpx;
  text-align: center;
  line-height: 120rpx;
}

.footer {
  display: flex;
  padding: 0 20rpx;
  align-items: center;
  justify-content: space-between;
  height: 112rpx;
  position: fixed;
  background-color: #fff;
  bottom: 0;
  left: 0;
  right: 0;
}

.btn {
  border: 1rpx solid rgb(209, 209, 209);
  width: 198rpx;
  height: 80rpx;
  border-radius: 6rpx;
  text-align: center;
  line-height: 80rpx;
  font-size: 28rpx;
  margin-left: 20rpx;
  color: #333333;
  background-color: #fff;
}

.errBtn {
  color: #fff;
  border: 1rpx solid #fa3534;
  background: #fa3534;
}
</style>
