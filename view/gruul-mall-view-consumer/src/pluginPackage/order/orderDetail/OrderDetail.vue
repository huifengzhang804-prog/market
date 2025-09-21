<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app'
import QIcon from '@/components/q-icon/q-icon.vue'
import ShopNav from '@/components/order/shop-nav.vue'
import RemarkView from './components/remark-view.vue'
import OrderGoodsSpec, { type ButtonController } from '@/components/order/order-goods-spec.vue'
import OrderDetailStatus from './components/order-detail-status.vue'
import DetailBar from '@/pluginPackage/order/orderDetail/components/detail-bar.vue'
import AddressInfo from '@/pluginPackage/order/orderDetail/components/address-info.vue'
import OrderPayInfo from '@/pluginPackage/order/orderDetail/components/order-pay-info.vue'
import OrderDetailCodeComponent from '@/pluginPackage/shopStore/components/orderDetailCodeComponent.vue'
import MovableMask from '@/pluginPackage/order/orderDetail/movable-mask.vue'
import { usePaymentCn } from '@/hooks'
import { getOrderDetailsConfig, getOrderDetailStatusPlus, orderDetailIconConfig, OrderStatusZh } from '@/hooks/useOrderStatus'
import { useSettingStore } from '@/store/modules/setting'
import { doGetFirstDeliveryPage, doGetLogisticsTrajectoryByWaybillNo, doGetOrderInfo } from '@/apis/order'
import { useNavBack } from '@/hooks/useNavBack'
import { countDownHandler, orderMapComputed } from '@/pluginPackage/order/orderList/hooks/orderHooks'
import { type ReturnTypeMap, useOrderDetailToAfs } from '@/pluginPackage/order/orderList/hooks/afsHooks'
import type { ApiOrder, OrderDiscount, OrderInfo } from '@/pluginPackage/order/orderList/types'
import { type ApiLogistics01, discountTypeConf, type OrderDiscountDetail } from './types'
import { Decimal } from 'decimal.js-light'
import QPrice from '@/components/q-price/index.vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import { computed, ref } from 'vue'

const { setStorageSync, toAfsPage } = useOrderDetailToAfs()
const info = ref<ApiOrder>()
const detailBarRef = ref()
// const isLoad = ref(false)
// 第一个包裹的订单快递信息（快递公司，快递单号等）
const logistics = ref<ApiLogistics01>()
//  物流轨迹
const currentDeliveryLocation = ref({
  area: [] as string[],
  areaName: '',
  context: '',
  ftime: '',
  status: '',
  time: '',
})
const show = ref(false)
const pathOrderInfo = ref({
  orderNo: '',
  shopId: '',
  packageId: '',
})
const orderDetailsConfig = ref({
  isShowAfsBtn: false,
  isShowFooterBtn: false,
  isShowFooterRightBtnText: '',
  isShowModifyAddressBtn: false,
})
//运费
const freightPrice = ref(new Decimal(0))
//是否有使用了返利支付
const isUseRebate = ref(false)
//折扣
const discounts = ref<OrderDiscountDetail[]>([])
//计算店铺总折扣
const shopDiscount = computed(() => {
  return discounts.value.filter((item) => item.isShopDiscount).reduce((pre, item) => pre.add(item.price), new Decimal(0))
})
const $settingStore = useSettingStore()
// 是否为无需物流发货 true 是 false 不是
const logisticsTypeIsWithout = computed(() => {
  return !!(logistics.value && logistics.value.type === 'WITHOUT')
})
// 商品总个数
const totalNum = computed(() =>
  info.value
    ? info.value.shopOrders.reduce(
        (pre, item) => pre + item.shopOrderItems.reduce((accumulator, currentValue) => accumulator + currentValue.num, 0),
        0,
      )
    : 0,
)

onLoad(({ orderNo, shopId, packageId }: any) => {
  uni.$emit('updateTitle')
  if (!orderNo) return uni.navigateBack({ delta: 1 })
  pathOrderInfo.value = {
    orderNo,
    shopId: shopId || '',
    packageId: packageId || '',
  }
  initOrderData(orderNo, shopId)
})

const handleUpdateAddress = () => {
  const { orderNo, shopId } = pathOrderInfo.value
  initOrderData(orderNo, shopId)
}
const handleUpdateOrder = () => {
  const { orderNo, shopId } = pathOrderInfo.value
  initOrderData(orderNo, shopId)
}

/**
 * 订单的数据
 */
async function initOrderData(orderNo: string, shopId?: Long) {
  const { code, data } = await doGetOrderInfo(orderNo, shopId)
  if (code !== 200) return uni.showToast({ title: '获取订单详情失败', icon: 'none' })
  if (!data) {
    useNavBack()
    return
  }
  const order = data
  discounts.value = []
  order.orderDiscounts.forEach((item: OrderDiscount) => {
    if (item.sourceType === 'CONSUMPTION_REBATE') {
      isUseRebate.value = true
    }
    discounts.value.push({
      ...discountTypeConf[item.sourceType],
      price: new Decimal(item.sourceAmount),
    })
  })
  freightPrice.value = order.shopOrders
    .flatMap((shopOrder) => shopOrder.shopOrderItems)
    .map((shopOrderItem) => new Decimal(shopOrderItem.freightPrice))
    .reduce((pre, current) => current.add(pre))
  info.value = order
  orderDetailsConfig.value = getOrderDetailsConfig(order)
  if (whetherTheDelivery(order)) {
    initFirstDeliveryPage(data)
  }
}

/**
 * 是否已经发货
 * @param {ApiOrder} order
 * @returns {boolean}boolean
 */
function whetherTheDelivery(order: ApiOrder) {
  return (
    order.status === 'PAID' &&
    order.shopOrders[0].status === 'OK' &&
    order.shopOrders[0].shopOrderItems.some((item) => item.packageStatus !== 'WAITING_FOR_DELIVER')
  )
}

/**
 *  查询第一个已发货的包裹收货地址并查询物流轨迹
 * @param {*} params
 */
async function initFirstDeliveryPage(params: OrderInfo) {
  const { code, data } = await doGetFirstDeliveryPage(params.no, params.shopOrders[0].no, params.shopOrders[0].shopOrderItems[0].packageId as string)
  if (code !== 200) return uni.showToast({ title: '获取订单详情失败', icon: 'none' })
  logistics.value = data
  if (!logistics.value) return
  const { expressCompanyCode, expressNo, type } = logistics.value
  if (type !== 'EXPRESS') return // 无需物流发货 不需要调接口
  const { code: status, data: res } = await doGetLogisticsTrajectoryByWaybillNo(expressCompanyCode, expressNo, pathOrderInfo.value.shopId)
  if (status !== 200) return
  if (!res.status || res.status !== '200') {
    currentDeliveryLocation.value.context = res.message
    return
  }
  currentDeliveryLocation.value = res.data[res.data.length - 1]
}

/**
 * 导航去首页
 */
const handleGoHome = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
/**
 * 订单状态
 * @param {*} computed
 */
const orderStatus = computed(() => getOrderDetailStatusPlus(info.value))
/**
 * 优惠后总价格计算
 * @param {*} info
 * @param {*} fixPrice 除不尽的时候展示的价格
 */
const payAmountComputed = computed(() => {
  if (!info.value) {
    return new Decimal(0)
  }
  const shopOrder = info.value.shopOrders.flatMap((shopOrder) => shopOrder.shopOrderItems)
  return shopOrder.reduce((p, item) => {
    return p.add(new Decimal(item.dealPrice).mul(new Decimal(item.num)).add(item.fixPrice))
  }, new Decimal(0))
})
/**
 * 总价格计算
 */
const paysalePriceAmountComputed = computed(() => {
  if (!info.value) {
    return new Decimal(0)
  }
  const shopOrder = info.value.shopOrders.flatMap((shopOrder) => shopOrder.shopOrderItems)
  return shopOrder.reduce((p, item) => {
    return p.add(new Decimal(item.salePrice).mul(new Decimal(item.num)))
  }, new Decimal(0))
})
/**
 * 优惠后的实付金额
 */
const amountRealPay = computed(() => {
  if (info.value) {
    const amount = payAmountComputed.value
    const totalPrice = new Decimal(amount)
    const totalPrice_ = totalPrice.toNumber() < 0 ? new Decimal(0) : totalPrice // 运费不参与优惠计算 最后相加
    return totalPrice_.add(freightPrice.value)
  }
  return new Decimal(0)
})
const handleCopyOrderNo = (no: string) => {
  uni.setClipboardData({
    data: no,
    showToast: false,
    success: function () {
      uni.showToast({ title: '复制成功', icon: 'none' })
    },
  })
}
const countDownConfig = computed(() => {
  const countDownReturn = { isShow: false, updateTime: '0', payTimeOut: '0', text: '' }
  console.log(info)

  if (info.value?.status) {
    const countdownWhiteList = [OrderStatusZh.WAITING_FOR_RECEIVE, OrderStatusZh.WAITING_FOR_COMMENT, 'UNPAID', 'DELIVERED']
    const { status, icStatus } = info.value
    let key = countdownWhiteList.includes(status) ? status : orderStatus.value.desc
    if (icStatus) {
      key = countdownWhiteList.includes(icStatus) ? icStatus : orderStatus.value.desc
    }
    if (countdownWhiteList.includes(key)) return countDownHandler[key as keyof typeof countDownHandler](info.value)
    return countDownReturn
  }

  return countDownReturn
})

const handleCloseMoreControl = () => {
  // 手指开始触摸关闭底部更多按钮
  detailBarRef.value?.closeMoreControl()
}
const orderItemsMap = computed(() => {
  if (!info.value?.shopOrders) return new Map<string, ReturnTypeMap>()
  return info.value.shopOrders.reduce((map, item) => {
    map.set(`${item.shopId}`, orderMapComputed(item.shopOrderItems))
    return map
  }, new Map<string, ReturnTypeMap>())
})
const handleApplyRefund = (shopId: Long, productId: Long, skuId: Long, specs: string[]) => {
  if (info.value) {
    const itemsMap = orderItemsMap.value.get(`${shopId}`)?.get(`${productId}${skuId}${specs}`) || new Map()
    const { status, no } = info.value
    // 携带额外参数 供申请售后页面使用
    const extra = {
      payStatus: status,
      orderNo: no,
      distributionMode: info.value.extra.distributionMode,
    }
    setStorageSync(itemsMap, extra)
    toAfsPage()
  }
}

function initPackages(skuId: Long, shopId: Long) {
  const shopOrder = info.value?.shopOrders.find((item) => item.shopId === shopId)
  const shopOrderPackages = shopOrder?.shopOrderItems.filter((item) => item.skuId === skuId && item.packageId)
  return shopOrderPackages?.length ? shopOrderPackages.reduce((pre, item) => pre + item.num, 0) : 0
}

const isPackages = (id: Long) => {
  if (!info.value?.shopOrders) return false
  const shop = info.value?.shopOrders.find((item) => item.shopId === id)
  if (!shop) return false
  const shopOrderItems = shop.shopOrderItems
  if (shopOrderItems.length === 1) return false

  const packageId = shopOrderItems[0]?.packageId
  return shopOrderItems.some((item) => item.packageId !== packageId)
}

/**
 * 评价按钮是否正在展示(因为res.show为true的时候才会被抛出来 所以只要有一个评价按钮展示了就算是展示)
 */
const isEvaluateShowing = ref(false)
const evaluateButtonController = (res: ButtonController) => {
  isEvaluateShowing.value = res.show
}
const isShowPopup = ref(false)
//是否同时显示了多个店铺订单
const multiShop = computed(() => {
  const order = info.value
  if (!order || !order.shopOrders || order.shopOrders.length === 0) {
    return false
  }
  return order.shopOrders.length > 1
})
</script>

<template>
  <q-nav bgColor="linear-gradient(to bottom, #fed0bf, #fff)" color="#333333" title="订单详情" />
  <view>
    <view class="order-detail" @tap="handleCloseMoreControl">
      <OrderDetailStatus
        :countDownRight="countDownConfig.text"
        :countDownShow="countDownConfig.isShow"
        :info="info"
        :payTimeOut="countDownConfig.payTimeOut"
        :status="orderStatus.desc"
        :updateTime="countDownConfig.updateTime"
        height="327"
      />
      <div class="transform_container">
        <OrderDetailCodeComponent v-if="logisticsTypeIsWithout && info?.extra && info?.extra.distributionMode === 'SHOP_STORE'" :info="info" />
        <!-- 物流/配送方式 s -->
        <AddressInfo v-else-if="info" :info="info" />
        <!-- <SelfPickup v-if="info" :info="info" /> -->
        <!-- 物流/配送方式 e -->

        <!-- 商品展示 s-->
        <view v-for="item in info?.shopOrders || []" :key="item.no" class="good base-box">
          <view>
            <ShopNav :info="item" :is-detail="true" />
            <view v-for="(ite, index) in Array.from(orderItemsMap.get(`${item.shopId}`)?.values() || [])" :key="index" class="good__pack">
              <OrderGoodsSpec
                :deliveryNum="initPackages(ite.merged.skuId, ite.merged.shopId)"
                :distributionMode="info?.extra.distributionMode"
                :icStatus="info?.icStatus || ''"
                :info="ite.merged"
                :isPackage="isPackages(item.shopId)"
                :isShowEvaluationBtn="!orderStatus.isClosed"
                :orderNo="info?.no"
                :payStatus="info?.status"
                isFooter
                jump
                @evaluateButtonController="evaluateButtonController"
                @apply-refund="handleApplyRefund(ite.merged.shopId, ite.merged.productId, ite.merged.skuId, ite.merged.specs)"
              />
            </view>
            <view v-if="shopDiscount" class="flex-space-between f13" style="padding: 20rpx">
              <text>店铺优惠</text>
              <q-price :price="shopDiscount" font-size="26rpx" style="color: #fa3534" symbol="-" />
            </view>
            <view v-if="multiShop" style="padding-bottom: 26rpx">
              <RemarkView :remark="item.remark?.items || []" />
            </view>
          </view>
        </view>
        <!-- 商品展示 e-->
        <!-- 支付信息 s -->
        <view class="base-box pay-info">
          <OrderPayInfo
            :amountRealPay="amountRealPay"
            :discounts="discounts"
            :freightPrice="freightPrice"
            :payAmountComputed="paysalePriceAmountComputed"
            :ruleStr="info?.status"
            :totalNum="totalNum"
          />
        </view>
        <!-- 支付信息 e -->
        <!-- 订单概述 s -->
        <view v-if="info?.orderPayment" class="base-box f13" style="padding-bottom: 15rpx">
          <view class="detail-item fcenter">
            订单号：{{ info.no }}
            <text class="detail-item__copy" @click="handleCopyOrderNo(info.no)">复制</text>
            <u-icon
              v-if="info.extra.distributionMode === 'INTRA_CITY_DISTRIBUTION'"
              color="#999"
              name="question-circle"
              size="32"
              style="margin-left: 10rpx"
              @click="isShowPopup = true"
            />
          </view>
          <view class="detail-item">下单时间：{{ info?.createTime }}</view>
          <view v-if="info.status !== 'UNPAID'">
            <view v-show="orderStatus.isClosed && orderStatus.closeTime" class="detail-item"> 关闭时间：{{ orderStatus.closeTime }} </view>
            <template v-if="info?.orderPayment?.payTime">
              <view class="detail-item"> 支付方式：{{ usePaymentCn(info?.orderPayment?.type) + (isUseRebate ? '+返利' : '') }} </view>
              <view class="detail-item">支付时间：{{ info?.orderPayment?.payTime }}</view>
            </template>
          </view>
          <RemarkView v-if="!multiShop" :remark="info.shopOrders[0]?.remark?.items || []" />
        </view>
        <!-- 订单概述 e -->
      </div>

      <!-- 返回首页浮层 s -->
      <MovableMask>
        <view class="home-mask">
          <view class="home-mask__btn" @click="handleGoHome">
            <q-icon color="#FA3534" name="icon-shouye1" size="50rpx"></q-icon>
          </view>
        </view>
      </MovableMask>
      <!-- 返回首页浮层 e -->

      <!-- 操作 tabr s -->
      <DetailBar
        v-if="info"
        ref="detailBarRef"
        :amount-real-pay="amountRealPay"
        :info="info"
        :isEvaluateShowing="isEvaluateShowing"
        :orderStatusZh="orderStatus.desc"
        :status="orderStatus.desc"
        @update-order="handleUpdateOrder"
        @update-order-address="handleUpdateAddress"
      ></DetailBar>
      <!-- 操作 tabr e -->
    </view>
    <u-modal
      v-model="show"
      :content-style="{ fontWeight: 700, color: '#000' }"
      :show-title="false"
      content="该订单中存在退款宝贝，等待商家确认收货"
    />
    <Auth />
    <u-popup v-model="isShowPopup" border-radius="20" height="300" mode="bottom">
      <popup-title title="售后申请说明" @close="isShowPopup = false" />
      <view style="padding: 20rpx">同城订单：待发货、待评价可申请售后，待收货（配送期间）暂停售后服务</view>
    </u-popup>
  </view>
</template>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

.order-detail {
  position: relative;

  .transform_container {
    position: absolute;
    top: 155rpx;
    width: 100%;
    padding-bottom: 108rpx;
  }
}

@include b(base-box) {
  background: #fff;
  border-radius: 15rpx;
  margin: 10rpx;
  overflow: hidden;
}

@include b(detail-item) {
  height: 60rpx;
  padding: 20rpx 40rpx 0 20rpx;
  &.fcenter {
    display: flex;
    align-items: center;
  }
  @include e(copy) {
    color: #005cf4;
    margin-left: auto;
  }
}

@include b(good) {
  @include e(discount) {
    height: 60rpx;
    border-top: 1px solid #ccc;
    align-items: center;
    padding: 0 20rpx;
    @include m(price) {
      color: $qszr-red;
    }
  }
  @include e(nav) {
    padding: 0 20rpx;
  }
  @include e(packs) {
    margin-bottom: 12rpx;
  }
  @include e(packNum) {
    padding: 0 20rpx;
    height: 80rpx;
    font-weight: bold;
    line-height: 80rpx;
    border-bottom: 1px solid #f5f5f5;
  }
}

@include b(home-mask) {
  width: 50rpx;
  height: 50rpx;
  @include e(btn) {
    display: flex;
    width: 96rpx;
    height: 96rpx;
    justify-content: center;
    align-items: center;
    border-radius: 48rpx;
    background: #fff;
    box-shadow:
      0px 10rpx 10rpx -6rpx rgba(0, 0, 0, 0.1),
      0px 16rpx 20rpx 2rpx rgba(0, 0, 0, 0.06),
      0px 6rpx 28rpx 4rpx rgba(0, 0, 0, 0.05);
  }
}
</style>
