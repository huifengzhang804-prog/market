<script lang="ts" setup>
import { computed, onUnmounted, ref, type PropType } from 'vue'
import { isUnpaidOrder, isCloseOrder, getOrderDetailStatusPlus } from '@/hooks'
import { Decimal } from 'decimal.js-light'
import { useOrderMore } from '@/pluginPackage/store/modules/order'
import { doPutOrderReceiver, doPutCloseOrderByOrderNo } from '@/apis/order'
import { PAY_TYPE } from '@/apis/paymentDetail/model'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import { useChosseAddress } from '@/hooks/useChooseAddress'
import { ORDER_LIST_BTN_STYLE } from '@/config/order-view-config'
import type { TConfirmAddress } from '@/hooks/useChooseAddress'
import { useQueryLogistics } from '@/pluginPackage/order/orderList/hooks/logisticsHooks'
import { doGetInvoiceTryRequest } from '@/apis/invoice'
import { useUserStore } from '@/store/modules/user'
import { useInvoiceStore } from '@/store/modules/invoice'
import type { ApiOrder, ShopOrder, ShopOrderItem } from '../types'
import useOrderDispatcherStore from '@/store/dispatcher/useOrderDispatcher'
import QPrice from '@/components/q-price/index.vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import storage from '@/utils/storage'

const orderDispatcherStore = useOrderDispatcherStore()
// 确认订单页面地址变更
const { orderBtnAddress } = useChosseAddress()
const { offOrderBtnAddress, getOrderBtnAddress } = orderBtnAddress()
const $useOrderMore = useOrderMore()
const isCountdown = ref(true)
const $props = defineProps({
  // 付款成功的订单传入order对象
  order: { type: Object as PropType<ApiOrder>, default: () => ({}) },
  shop: { type: Object as PropType<ShopOrder>, default: () => ({}) },
  // 付款未成功的订单传入orderlist集合
  shopOrderList: { type: Array as PropType<ShopOrderItem[]>, default: () => [] },
  // 付款未成功的订单传入time倒计时 仅支持 年月日格式
  time: {
    type: [Number, String],
    default: 0,
  },
})

const { setLogisticsStorage, toLogisticsPage } = useQueryLogistics()

const emit = defineEmits(['cancelOrder'])
const moreKey = computed(() => {
  const len = $props.shopOrderList.length
  const shopOrderNoAndShopNo = len ? $props.order.no : $props.shop.orderNo + $props.shop.id
  return shopOrderNoAndShopNo
})
const { timeSet } = useCountdown($props.time, { immediate: true, immediateCallback: true }, () => {
  isCountdown.value = false
})
/**
 * 订单状态
 * @param {*} computed
 */
const orderStatus = computed(() => getOrderDetailStatusPlus($props.order, $props.shop.shopId))

onUnmounted(() => {
  // 页面卸载移除事件监听
  offOrderBtnAddress()
})

/**
 * 修改收货地地址
 * @param {*} res
 */
async function updataOrderReceiver(res: TConfirmAddress) {
  try {
    const params = res
    const { code, msg } = await doPutOrderReceiver(storage.get('setAddRessOrderNo'), params)
    if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '修改地址失败'}` })
    uni.showToast({ icon: 'none', title: `修改地址成功` })
  } catch (error) {
    console.log(error)
  } finally {
    storage.remove('setAddRessOrderNo')
  }
}

/**
 * 导航修改地址
 */
const handelAddress = (no: string) => {
  closeMore()
  storage.set('setAddRessOrderNo', no)
  uni.navigateTo({ url: `/basePackage/pages/addressManage/AddressManage?callKey=calltOrderBtn` })
}
/**
 * 取消订单
 */
const cancelOrder = (e: Event) => {
  e.preventDefault()
  uni.showModal({
    title: '请确认',
    content: '是否需要取消？',
    success: async ({ confirm }) => {
      if (!confirm) return
      const { code, data, msg } = await doPutCloseOrderByOrderNo($props.order.no)
      if (code !== 200) return uni.showToast({ icon: 'none', title: `${msg ? msg : '取消订单失败'}` })
      uni.showToast({ icon: 'none', title: `订单已取消` })
      orderDispatcherStore.updateData()
      // emit('cancelOrder')
    },
  })
}
/**
 * 去支付
 */
const handlePay = (no: string) => {
  closeMore()
  uni.navigateTo({ url: `/basePackage/pages/pay/Index?no=${no}&orderType=${PAY_TYPE.ORDER}` })
}
/**
 * 导航去物流追踪
 */
const navGoTracking = (shop: ShopOrder, info: ApiOrder) => {
  const orderNo = info.no
  const shopOrderNo = shop.no
  const shopId = shop.shopId
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
 * 更多按钮change
 * @param {*} shopOrderNoAndShopNo
 */
const handleMore = () => {
  $useOrderMore.updateMoreMap(moreKey.value)
}

function computeOrderPrice(data: ShopOrder) {
  if (data.id) {
    return data.shopOrderItems.reduce((pre, cur) => {
      return pre.add(new Decimal(cur.num).mul(cur.dealPrice).add(cur.fixPrice).add(cur.freightPrice))
    }, new Decimal(0))
  }
  return $props.shopOrderList.reduce(
    (pre, item) => pre.add(new Decimal(item.dealPrice).mul(item.num).add(item.freightPrice).add(item.fixPrice)),
    new Decimal(0),
  )
}

function closeMore() {
  $useOrderMore.updateMoreMap('')
}

async function navGoInvoice(orderNo: string, shopId: Long, shopName: string) {
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
onShow(() => {
  getOrderBtnAddress(updataOrderReceiver)
  $useOrderMore.getMoreMap.set(moreKey.value, false)
})
onUnload(() => {
  $useOrderMore.getMoreMap.set(moreKey.value, false)
})
</script>

<template>
  <view class="container__footer">
    <!-- 已关闭订单 样式展示 s-->
    <template v-if="isCloseOrder(order.status)">
      <view class="flex-justify-content-end">
        <view class="container__footer-right flex-justify-content-end">
          <view class="f12">
            应付款
            <q-price :price="computeOrderPrice(shop)" class="container__footer--price f16" font-size="32rpx" />
          </view>
        </view>
      </view>
    </template>
    <!-- 已关闭订单 样式展示 e-->

    <!-- 待支付订单样式展示 s-->
    <template v-else-if="isUnpaidOrder(order.status)">
      <view class="container__footer-top flex-space-between">
        <view v-if="order.status === 'UNPAID'" class="f12">
          还剩
          <text class="container__footer-top--time"> {{ timeSet.hours }}:{{ timeSet.minutes }}:{{ timeSet.seconds }} </text>
        </view>
        <!-- 兼容 flex -->
        <text v-else></text>
        <!-- 兼容 flex -->
        <view class="f12">
          应付款
          <q-price :price="computeOrderPrice(shop)" class="container__footer--price f16" font-size="32rpx" />
        </view>
      </view>
      <view class="container__footer-more flex-space-between">
        <view style="position: relative">
          <text class="f12" @click.stop="handleMore">更多</text>
          <view v-if="$useOrderMore.getMoreMap.get(moreKey)" class="container__footer-more-btns f12">
            <view v-if="isUnpaidOrder(order.status)" class="container__footer-more-btns--item" @click="cancelOrder"> 取消订单 </view>
          </view>
        </view>
        <view>
          <!-- 修改地址会造成运费发生变化，因此隐藏修改地址按钮 虚拟/到店自提订单不显示 -->
          <u-button
            v-if="isUnpaidOrder(order.status) && !['VIRTUAL', 'SHOP_STORE'].includes(order.extra.distributionMode)"
            :custom-style="ORDER_LIST_BTN_STYLE.LEFT"
            plain
            size="mini"
            @click="handelAddress(order.no)"
          >
            修改地址
          </u-button>
          <u-button
            v-if="isUnpaidOrder(order.status)"
            :custom-style="ORDER_LIST_BTN_STYLE.RIGHT"
            :hair-line="false"
            plain
            ripple
            size="mini"
            @click="handlePay(order.no)"
          >
            去支付
          </u-button>
        </view>
      </view>
    </template>
    <!-- 待支付订单样式展示 e-->

    <!-- 待收货 样式展示 s-->
    <template v-else-if="['待收货', '待接单', '待到店', '待取货', '配送中', '已送达'].includes(orderStatus.desc)">
      <view class="container__footer-top flex-justify-content-end">
        <view class="f12">
          实付款
          <q-price :price="computeOrderPrice(shop)" class="container__footer--price f16" font-size="32rpx" />
        </view>
      </view>
      <!-- 虚拟/到店自提订单不显示物流按钮 -->
      <view v-if="!['VIRTUAL', 'SHOP_STORE'].includes(order.extra.distributionMode)" class="container__footer-more flex-justify-content-end">
        <view>
          <u-button :custom-style="ORDER_LIST_BTN_STYLE.LEFT" plain ripple size="mini" type="info" @click.stop="navGoTracking(shop, order)">
            查看物流
          </u-button>
        </view>
      </view>
    </template>
    <!-- 待收货 样式展示 e-->

    <!-- 待发货/交易失败 样式展示 s -->
    <template v-else-if="['待发货', '交易失败'].includes(orderStatus.desc)">
      <view class="container__footer-top flex-justify-content-end">
        <view class="f12">
          实付款
          <q-price :price="computeOrderPrice(shop)" class="container__footer--price f16" font-size="32rpx" />
        </view>
      </view>
    </template>
    <!-- 待发货/已完成 样式展示e -->

    <!-- 已完成 样式展示 s -->
    <template v-else-if="orderStatus.desc === '已完成'">
      <view class="container__footer-top flex-justify-content-end">
        <view class="f12">
          实付款
          <q-price :price="computeOrderPrice(shop)" class="container__footer--price f16" font-size="32rpx" />
        </view>
      </view>
      <view class="container__footer-more flex-space-between">
        <view style="position: relative">
          <text class="f12" @click.stop="handleMore">更多</text>
          <view
            v-if="$useOrderMore.getMoreMap.get(moreKey)"
            class="container__footer-more-btns f12"
            @click.stop="navGoInvoice(order.no, shop.shopId, shop.shopName)"
          >
            <view class="container__footer-more-btns--item"> 申请开票</view>
          </view>
        </view>
        <view>
          <u-button
            :custom-style="ORDER_LIST_BTN_STYLE.LEFT"
            style="width: 116px"
            plain
            ripple
            size="mini"
            type="info"
            @click.stop="navGoTracking(shop, order)"
          >
            查看物流
          </u-button>
        </view>
      </view>
    </template>
    <!-- 已完成 样式展示e -->

    <!-- 待评价 样式展示 s -->
    <template v-else-if="orderStatus.desc === '待评价'">
      <view class="container__footer-top flex-justify-content-end">
        <view class="f12">
          实付款
          <q-price :price="computeOrderPrice(shop)" class="container__footer--price f16" font-size="32rpx" />
        </view>
      </view>
      <view class="container__footer-more flex-justify-content-end">
        <view>
          <u-button :custom-style="ORDER_LIST_BTN_STYLE.LEFT" plain ripple size="mini" type="info" @click.stop="navGoTracking(shop, order)">
            查看物流
          </u-button>
        </view>
      </view>
    </template>
    <!-- 待评价 样式展示e -->
    <!-- 询问弹窗 -->
    <!-- 询问弹窗 -->
  </view>
</template>

<style lang="scss" scoped>
@include b(container) {
  @include e(footer) {
    padding: 20rpx 20rpx 17rpx 20rpx;
    padding-top: 0;
    .flex-justify-content-end {
      display: flex;
      justify-content: flex-end;
    }
    .f16 {
      font-size: 32rpx;
    }
    @include m(price) {
      font-weight: 700;
      color: $qszr_red;
      &::before {
        content: '￥';
        font-size: 24rpx;
        font-weight: normal;
      }
    }
  }
  @include e(footer-top) {
    width: 100%;
    @include m(time) {
      color: $qszr-red;
      font-weight: 700;
      margin-left: 10rpx;
    }
  }
  @include e(footer-more) {
    align-items: center;
    padding: 18rpx 0;
    width: 100%;
  }
  @include e(footer-more-btns) {
    position: absolute;
    color: $qszr-red;
    background: #fff;
    border: 1px solid #ccc;
    padding: 15rpx 10rpx;
    z-index: 98;
    border-radius: 15rpx;
    left: 0;
    width: 150rpx;
    text-align: center;
  }
  @include e(footer-right) {
  }
}
</style>
