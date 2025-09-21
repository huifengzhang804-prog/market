<script lang="ts" setup>
import { computed, onUnmounted, ref, type PropType } from 'vue'
import { PAY_TYPE } from '@/apis/paymentDetail/model'
import { getAfsStatusCn } from '@/pluginPackage/order/hooks/useAfssStatus'
import { useQueryLogistics } from '@/pluginPackage/order/orderList/hooks/logisticsHooks'
import { doconfirmGoods } from '@/apis/afs'
import { getOrderDetailStatusPlus, isPayOrder, isReceive, isTeam, isUnpaidOrder, OrderStatusZh } from '@/hooks'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doPutCloseOrderByOrderNo, doPutOrderReceiver } from '@/apis/order'
import type { TConfirmAddress } from '@/hooks/useChooseAddress'
import { useChosseAddress } from '@/hooks/useChooseAddress'
import { IcStatus, type ApiOrder } from '@/pluginPackage/order/orderList/types'
import { doGetInvoiceTryRequest } from '@/apis/invoice'
import { useUserStore } from '@/store/modules/user'
import { useInvoiceStore } from '@/store/modules/invoice'
import { doGetMessagesChatRoom } from '@/apis/consumerSever'
import useOrderDispatcherStore from '@/store/dispatcher/useOrderDispatcher'
import { Decimal } from 'decimal.js-light'
import storage from '@/utils/storage'
import { orderDetailIconConfig } from '@/hooks/useOrderStatus'

const orderDispatcherStore = useOrderDispatcherStore()
const $props = defineProps({
  info: {
    type: Object as PropType<ApiOrder>,
    required: true,
  },
  status: {
    type: String,
    default: '待支付',
  },
  amountRealPay: {
    type: [Number, Decimal],
    default: 0,
  },
  orderStatusZh: { type: String, default: OrderStatusZh.COMMENTED_COMPLETED },
  isEvaluateShowing: { type: Boolean, default: false },
})
const { orderBtnAddress } = useChosseAddress()
const { offOrderBtnAddress, getOrderBtnAddress } = orderBtnAddress()
const { setLogisticsStorage, toLogisticsPage } = useQueryLogistics()
const $emit = defineEmits(['update-order', 'update-order-address'])
const moreControl = ref(false)
const moreinvoice = ref(false)
const { divTenThousand } = useConvert()

onUnmounted(() => {
  // 页面卸载移除事件监听
  offOrderBtnAddress()
})
getOrderBtnAddress(updataOrderReceiver)

const handleContactCustomerService = (order: ApiOrder) => {
  if (order.shopOrders[0]) {
    const { shopLogo, shopName, shopId, totalAmount } = order.shopOrders[0]
    const { productName, image, productId } = order.shopOrders[0].shopOrderItems[0]
    const params = {
      id: productId,
      name: productName,
      salePrices: totalAmount,
      pic: image,
      no: order.no,
      amountRealPay: divTenThousand($props.amountRealPay).toFixed(2),
      afsStatu: getOrderDetailStatusPlus(order),
      commodityList: order.shopOrders[0].shopOrderItems.map((text: any) => {
        return {
          ...text,
          pic: text.image,
          name: text.productName,
          amountRealPay: divTenThousand(text.dealPrice)?.toFixed(2),
          dealPrice: divTenThousand(text.dealPrice)?.toFixed(2),
        }
      }),
      my: true,
    }
    storage.set('cSProduct', params)
    uni.navigateTo({
      url: `/basePackage/pages/customerService/CustomerService?shopId=${shopId}&shopLogo=${shopLogo}&shopName=${shopName}`,
    })
  }
}
/**
 * orderNo  shopOrderNo packageId
 * 确认收货
 */
const handleConfirmGoods = (order: ApiOrder) => {
  const showModalProps = {
    content: '是否确认收货',
    showClose: true,
    isSubmit: true,
  }
  // 通过每一个商品的售后得到 可以改变包状态的数组
  const shop = order.shopOrders[0]
  if (!shop.shopOrderItems.every((item) => getAfsStatusCn(item.afsStatus).canChangePackageStatus)) {
    showModalProps.content = '该订单中存在退款宝贝，等待商家确认收货'
    showModalProps.isSubmit = false
    showModalProps.showClose = false
  } else if (!shop.shopOrderItems.every((item) => !getAfsStatusCn(item.afsStatus).canChangePackageStatusText)) {
    showModalProps.content = '该订单中存在退款宝贝，确认收货将关闭退款'
  }
  // 该订单中存在退款宝贝，等待商家确认收货  该订单中存在退款宝贝，确认收货将关闭退款
  uni.showModal({
    // title: '提示',
    confirmColor: '#F54319',
    content: `${showModalProps.content}`,
    showCancel: showModalProps.showClose,
    success: async (res) => {
      if (res.cancel || !showModalProps.isSubmit) return
      const { packageId } = shop.shopOrderItems[0]
      const { code, msg } = await doconfirmGoods(order.no, shop.shopId)
      if (code !== 200) return uni.showToast({ title: `${msg ? msg : '确认收货失败'}`, icon: 'none' })
      $emit('update-order')
      // navigateToReleaseAssess()
    },
  })
}

/**
 * 修改收货地地址
 * @param {*} res
 */
async function updataOrderReceiver(res: TConfirmAddress) {
  try {
    const params = res
    const { code, msg, data } = await doPutOrderReceiver(storage.get('setAddRessOrderNo'), params)
    if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '修改地址失败'}` })
    uni.showToast({ icon: 'none', title: `修改地址成功` })
    $emit('update-order-address')
  } catch (error) {
    console.log(error)
  } finally {
    storage.remove('setAddRessOrderNo')
  }
}

const handleNavToPay = (no: string) => {
  uni.navigateTo({ url: `/basePackage/pages/pay/Index?no=${no}&orderType=${PAY_TYPE.ORDER}` })
}
// 取消订单
const handleCloseOrder = (no: string) => {
  uni.showModal({
    title: '请确认',
    content: '是否需要取消？',
    success: async ({ confirm }) => {
      console.log('confirm', confirm)
      if (!confirm) return
      const { code, data, msg } = await doPutCloseOrderByOrderNo(no)
      if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '取消订单失败'}` })
      uni.showToast({ icon: 'none', title: `订单已取消` })
      // $emit('update-order')
      orderDispatcherStore.updateData()
    },
  })
}
const closeMoreControl = () => {
  moreControl.value = false
}
/**
 * 导航去物流追踪
 */
const navGoTracking = (info: ApiOrder) => {
  const orderNo = info.no
  const shopOrderNo = info.shopOrders[0].no
  const shopId = info.shopOrders[0].shopId
  if (info.extra.distributionMode === 'INTRA_CITY_DISTRIBUTION') {
    // 进入同城配送物流界面
    uni.navigateTo({ url: `/pluginPackage/order/orderDetail/SameCityDeliveryLogistics?shopId=${shopId}&orderNo=${orderNo}` })
    return
  }
  setLogisticsStorage(info)
  const params = `?orderNo=${orderNo}&shopOrderNo=${shopOrderNo}&shopId=${shopId}`
  toLogisticsPage(params)
}
/**
 * 我的拼团
 * @param {*} teamNo
 */
const handleMySpelling = (teamNo: string | undefined) => {
  if (teamNo) {
    uni.navigateTo({ url: `/pluginPackage/group/views/OwnGroup?teamNo=${teamNo}` })
  }
}
defineExpose({
  closeMoreControl,
})

async function navGoInvoice(orderNo: string, shopId: Long, shopName: string) {
  moreinvoice.value = false
  const { data, code, msg } = await doGetInvoiceTryRequest({
    invoiceOwnerType: 'USER',
    applicantId: useUserStore().userInfo.info.userId,
    applicantShopId: shopId,
    orderNo,
  })
  if (code !== 200 || !data) return uni.showToast({ icon: 'none', title: `${msg ? msg : '获取开票信息失败'}` })
  if (data.invoiceStatus === 'REQUEST_HAS_EXPIRED') return uni.showToast({ icon: 'none', title: `已过开票时间` })
  if (data.invoiceStatus === 'SERVER_NOT_SUPPORTED')
    return uni.showToast({
      icon: 'none',
      title: `该店铺暂不提供开票服务`,
    })
  useInvoiceStore().SET_INVOICE_INFO({
    orderNo,
    shopId,
    shopName,
    invoiceAmount: data.billMoney,
    isDetail: data.invoiceStatus !== 'ALLOWED_INVOICING',
    id: data.id,
  })
  useInvoiceStore().DEL_INVOICE_TYPE()
  uni.navigateTo({
    url: `/basePackage/pages/Invoicing/Invoicing`,
  })
}

// 联系客服
const customerService = async () => {
  await doGetMessagesChatRoom($props.info?.shopOrders?.[0]?.shopId, $props.info.buyerId)
}

/**
 * 查看物流按钮的显隐
 */
const watchLogistics = computed(() => {
  if (['VIRTUAL', 'SHOP_STORE'].includes($props.info.extra.distributionMode)) {
    return false
  }
  if (['EXPRESS'].includes($props.info.extra.distributionMode)) {
    return isPayOrder($props.info.status) && $props.info.shopOrders[0].shopOrderItems.some((item) => isReceive(item.packageStatus))
  }
  // 同城订单在 待支付 已关闭 待发货 状态不展示 查看物流按钮
  return !['待支付', '已关闭', '待发货'].includes($props.status)
})

const showConfirmReceiptOfGoodsBtn = computed(() => {
  // @ts-ignore
  return $props.orderStatusZh === IcStatus.DELIVERED && !$props.isEvaluateShowing
})
</script>

<template>
  <view class="flex-space-between bar" @click.stop="closeMoreControl">
    <!-- 联系客服 s -->
    <view class="flex-space-between customer-service" @click="handleContactCustomerService(info)">
      <q-icon color="#223D60" name="icon-kefu" size="40rpx" />
      <text class="f12" @click="customerService">联系客服</text>
    </view>
    <!-- 联系客服 e -->
    <view :style="{ justifyContent: 'flex-end' }" class="flex-space-between" style="flex: 1">
      <view v-if="isUnpaidOrder(info.status)" class="f14 bar__left">
        <text class="bar__more" @click.stop="moreControl = true">更多</text>
        <text v-show="moreControl" class="bar__left--close" @click="handleCloseOrder(info.no)">取消订单</text>
      </view>
      <view v-if="orderStatusZh === OrderStatusZh.COMMENTED_COMPLETED" class="f14 bar__left">
        <text class="bar__more" @click.stop="moreinvoice = !moreinvoice">更多</text>
        <text
          v-show="moreinvoice"
          class="bar__left--close"
          @click.stop="navGoInvoice(info.no, info.shopOrders[0].shopId, info.shopOrders[0].shopName)"
          >申请开票
        </text>
      </view>
      <view v-if="watchLogistics" class="bar__more" @click="navGoTracking(info)"> 查看物流 </view>
      <view v-if="isTeam(info)" class="bar__more" @click="handleMySpelling(info.extra?.teamNo)"> 我的拼团</view>
      <view v-if="!isPayOrder(info.status)" class="bar__more bar__to-pay" @click="handleNavToPay(info.no)"> 去支付 </view>
      <view v-if="orderStatusZh === OrderStatusZh.WAITING_FOR_RECEIVE" class="bar__more bar__to-pay" @click="handleConfirmGoods(info)">
        确认收货
      </view>
      <!-- 评价按钮有了就不展示同城配送的确认收货 -->
      <view v-if="showConfirmReceiptOfGoodsBtn" class="bar__more bar__to-pay" @click="handleConfirmGoods(info)"> 确认收货 </view>
    </view>
  </view>
  <view style="height: 112rpx"></view>
</template>

<style lang="scss" scoped>
@include b(bar) {
  background: #fff;
  border-radius: 15rpx;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 112rpx;
  align-items: center;
  padding: 0 20rpx;
  justify-content: flex-start;
  z-index: 99;
  @include e(more) {
    display: inline-flex;
    padding: 24rpx 32rpx;
    justify-content: center;
    align-items: center;
    border-radius: 46rpx;
    background: rgb(245, 67, 25);
    color: #fff;
    flex-shrink: 0;
    & + .bar__more {
      margin-left: 10rpx;
    }
  }
  @include e(to-pay) {
    color: #fff;
    background: #fa3534;
    border: 1px solid transparent;
  }
  @include e(left) {
    position: relative;
    margin-right: 20rpx;
    @include m(close) {
      position: absolute;
      color: $qszr-red;
      top: -70rpx;
      left: 0;
      border: 1px solid #ccc;
      border-radius: 15rpx;
      padding: 10rpx 20rpx;
      width: 160rpx;
      z-index: 99;
      background: #fff;
    }
  }
}

@include b(customer-service) {
  flex-direction: column;
  align-items: center;
  margin-right: 50rpx;
}
</style>
