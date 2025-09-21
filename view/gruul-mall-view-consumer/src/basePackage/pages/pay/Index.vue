<script lang="ts" setup>
import { computed, reactive, ref, toRefs, unref, watch } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import QRoundDefault from '@/components/q-btns/q-round-default.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import storage from '@/utils/storage'
import { doGetSavingPay } from '@/apis/paymentDetail'
import { doGetUserBalance } from '@/apis/consumer/footprint'
import { doGetOrderIsPaySuccess, doGetOrderPayment, doPostActiveMember, doPostOrderPayPage } from '@/apis/order'
import { doGetIntegralOrderIsPaySuccess, doPostIntegralOrderPay } from '@/apis/plugin/integral'
import { ORDERPAYMENT, PAY_TYPE, type PayType } from '@/apis/paymentDetail/model'
import { doGetRebatePayBalance } from '@/basePackage/apis/rebate'
import { doGetUserIntegralSystemtotal } from '@/basePackage/apis/integral'
import Auth from '@/components/auth/auth.vue'
import QPrice from '@/components/q-price/index.vue'
import { Decimal } from 'decimal.js-light'

const orderCacheKey = 'payOrderInfo'

interface OnLoadQuery {
  orderType: PayType
  no: string
  price: Long
  extra: string
  integral?: string
}

const currentPayType = ref<ORDERPAYMENT>(ORDERPAYMENT.BALANCE)
const totalPrice = ref<Long>('0')
const orderNumber = ref('')
const payBtnLoading = ref(false)
const rebate = reactive({
  checked: false,
  disabled: true,
  balance: '0',
})
const isModalShow = ref(false)
const flag = ref(false)
// 商品支付 ORDER BALANCE MEMBER
const payFrom = ref<PayType>('ORDER')
/**
 * 路由额外参数 （后面的营销活动参数都可以丰富这个对象）
 * @params teamNo 拼团单号
 * @params memberId 会员id
 * @params memberPrice 会员支付金额
 * @params ruleId 积分规则id / 会员规则id
 * @params activeType 活动类型 OrderType
 */
const onLoadExtra = ref<Record<'teamNo' | 'memberId' | 'memberPrice' | 'ruleId' | 'activeType', string>>({
  teamNo: '',
  memberId: '',
  memberPrice: '',
  ruleId: '',
  activeType: '',
})
// 跳转充值成功通过此字段判断是否是充值余额
// 余额支付询问
const balancePayModal = reactive({
  isPayModal: false,
  modalContent: '是否使用余额支付',
  balance: '0',
})
const { isPayModal, modalContent, balance } = toRefs(balancePayModal)
//是否余额不可用
const balanceInvalid = computed(() => {
  return (
    // 1. 余额充值
    payFrom.value === 'BALANCE' ||
    // 2. 没有余额
    !balance.value ||
    // 3. 余额不足
    new Decimal(totalPrice.value).cmp(new Decimal(balance.value)) > 0
  )
})
// 余额不可用 切换支付方式
watch(
  () => balanceInvalid.value,
  (val) => {
    if (val) {
      // #ifdef MP-WEIXIN
      currentPayType.value = ORDERPAYMENT.WECHAT
      // #endif
      // #ifndef MP-WEIXIN
      currentPayType.value = ORDERPAYMENT.ALIPAY
      // #endif
      return
    }
    currentPayType.value = ORDERPAYMENT.BALANCE
  },
)

let time: NodeJS.Timeout
/**
 * @param no 订单号
 * @param price 金额
 * @param extra 额外参数
 * @param orderType 订单类型
 */
onLoad(async (res) => {
  const params = res as OnLoadQuery
  // 如果路由未传参 默认是第三方（支付宝丶微信）等重定向 开启轮询 openPolling
  if (!params || !params.orderType) {
    flag.value = true
    initLoopCheckPayStatus()
    return
  }
  initOnLoadQuery(params)
})
// 用户余额
initUserBalance()
onUnload(() => {
  if (time) {
    clearTimeout(time)
  }
})

// #ifndef H5
// 微信支付相关 APP WEIXIN 环境适用
// no=SS1646406278165680128&orderType=ORDER
// #endif
/**
 * 路由参数初始化处理
 * @param {*} params
 */
function initOnLoadQuery(params: OnLoadQuery) {
  if (params.extra && JSON.stringify(params.extra).length) {
    //记录额外参数
    onLoadExtra.value = JSON.parse(decodeURIComponent(params.extra))
  }
  if (params?.orderType) {
    initMethod[params.orderType](params)
  }
}

const initMethod: { [x: string]: ({ no, price, orderType }: OnLoadQuery) => void } = {
  INTEGRAL: initFromIntegral,
  ORDER: initFromOrder,
  BALANCE: initFromBalance,
  MEMBER: initFromMember,
}
const payMethod: {
  [x: string]: Record<keyof typeof ORDERPAYMENT | 'BALANCE_CONFIRM' | 'error' | 'success', () => void>
} = {
  INTEGRAL: {
    ALIPAY: integralPay,
    WECHAT: integralPay,
    BALANCE: () => {
      // 余额支付开启弹窗 支付方法在弹窗确认回调
      isPayModal.value = true
    },
    BALANCE_CONFIRM: integralBalanceConfig,
    error: () => {
      uni.showToast({
        icon: 'none',
        title: '积分订单支付失败',
      })
    },
    success: () => {
      generalOrderPaymentStatusInquiry(
        doGetIntegralOrderIsPaySuccess,
        `/pluginPackage/integral/mall/components/commodityInfo/InfoEntrance?goodId=${onLoadExtra.value.ruleId}&shopId=0`,
        true,
      )
    },
  },
  ORDER: {
    ALIPAY: orderPay,
    BALANCE: () => {
      isPayModal.value = true
    },
    WECHAT: orderPay,
    BALANCE_CONFIRM: orderBalanceConfig,
    error: modalClose,
    success: paySuccess,
  },
  BALANCE: {
    ALIPAY: balancePay,
    BALANCE: () => {
      uni.showToast({ title: '不可使用余额充值余额', icon: 'none' })
    },
    WECHAT: balancePay,
    BALANCE_CONFIRM: () => {
      uni.showToast({ title: '不可使用余额充值余额', icon: 'none' })
    },
    error: () => {
      uni.showToast({
        icon: 'none',
        title: '余额充值失败',
      })
    },
    success: paySuccess,
  },
  MEMBER: {
    ALIPAY: memberPay,
    BALANCE: () => {
      isPayModal.value = true
    },
    WECHAT: memberPay,
    BALANCE_CONFIRM: memberBalanceConfig,
    error: () => {
      uni.showToast({
        icon: 'none',
        title: '开通会员失败',
      })
    },
    success: paySuccess,
  },
}

/**
 * 支付成功
 */
function paySuccess() {
  console.log('轮询检查是否支付成功', 'paySuccess')
  // 拼团余额支付失败跳转
  let url = `/basePackage/pages/paySuccess/Index?orderNo=${orderNumber.value}&payFrom=${payFrom.value}`
  if (onLoadExtra.value.activeType === 'TEAM' && onLoadExtra.value.teamNo.length) {
    url = `/pluginPackage/group/views/OwnGroup?teamNo=${onLoadExtra.value.teamNo}&payType=${true}`
  }
  generalOrderPaymentStatusInquiry(doGetOrderIsPaySuccess, url, 'ACCOMPLISH')
}

/**
 * 余额冲会员
 */
async function memberBalanceConfig() {
  // 余额冲会员
  const paySuccess = await memberPay()
  if (paySuccess) {
    loopCheckPayStatus()
  }
}

/**
 * 积分订单余额支付
 */
async function integralBalanceConfig() {
  uni.showLoading({
    title: '支付中',
  })
  const { code, data, msg } = await doPostIntegralOrderPay(unref(orderNumber), unref(currentPayType))
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '余额支付失败'}`, icon: 'none' })
    payBtnLoading.value = false
    uni.redirectTo({ url: '/pluginPackage/integral/mall/Index' })
    return
  }
  uni.hideLoading()
  orderNumber.value = data.outTradeNo
  loopCheckPayStatus()
}

/**
 * 订单余额支付
 */
async function orderBalanceConfig() {
  uni.showLoading({
    title: '支付中',
  })
  const { code, data, msg } = await doPostOrderPayPage({
    orderNo: unref(orderNumber),
    payType: unref(currentPayType),
    rebate: rebate.checked,
  }).catch((e) => {
    payBtnLoading.value = false
    return e
  })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '余额支付失败'}`, icon: 'none' })
    payBtnLoading.value = false
    uni.redirectTo({ url: '/pluginPackage/order/orderList/orderList' })
    return
  }
  uni.hideLoading()
  orderNumber.value = data.outTradeNo
  loopCheckPayStatus()
}

async function orderPay() {
  const { code, data, msg } = await doPostOrderPayPage({
    orderNo: unref(orderNumber),
    payType: unref(currentPayType),
    rebate: rebate.checked,
  })
  // orderNumber.value = data.outTradeNo
  // setPayOrderInfo()
  if (code !== 200) {
    uni.showToast({ title: `${msg || '调取支付页面失败'}`, icon: 'none' })
    payBtnLoading.value = false
    return
  }
  orderNumber.value = data.outTradeNo
  setPayOrderInfo()
  //  #ifdef H5
  document.write(data.data)
  //  #endif
  //  #ifndef H5
  requestPayment(data.data)
  //  #endif
}

async function balancePay() {
  if (currentPayType.value === ORDERPAYMENT.BALANCE) return uni.showToast({ title: '不可使用余额充值余额', icon: 'none' })
  const price = totalPrice.value
  const { code, msg, data } = await doGetSavingPay(currentPayType.value, price, onLoadExtra.value.ruleId)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '余额充值失败'}`, icon: 'none' })
  orderNumber.value = data.outTradeNo
  setPayOrderInfo()
  payBtnLoading.value = true
  //  #ifdef H5
  document.write(data.data)
  //  #endif
  //  #ifndef H5
  requestPayment(data.data)
  //  #endif
}

/**
 * 订单信息存储本地供轮询处理
 */
function setPayOrderInfo() {
  //todo 可优化 只有h5支付时才需要缓存数据
  storage.set(orderCacheKey, {
    payFrom: payFrom.value,
    orderNumber: orderNumber.value,
    pirce: totalPrice.value,
    type: currentPayType.value,
    activeExtra: JSON.stringify(onLoadExtra.value),
  })
}

/**
 * 积分订单 支付宝微信通用支付
 */
async function integralPay() {
  const { code, data } = await doPostIntegralOrderPay(unref(orderNumber), unref(currentPayType))
  if (code !== 200) {
    uni.showToast({ title: '调取支付页面失败', icon: 'none' })
    payBtnLoading.value = false
    return
  }
  orderNumber.value = data.outTradeNo
  setPayOrderInfo()
  //  #ifdef H5
  document.write(data.data)
  //  #endif
  //  #ifndef H5
  requestPayment(data.data)
  //  #endif
}

/**
 * uni requestPayment 支付
 * @param {*} data
 */
function requestPayment(data: any) {
  uni.requestPayment({
    provider: currentPayType.value === ORDERPAYMENT.WECHAT ? 'wxpay' : 'alipay',
    orderInfo: data,
    ...data,
    success: payMethod[payFrom.value].success,
    fail: payMethod[payFrom.value].error,
  })
}

const integral = ref('0')

/**
 * 积分订单
 * @param {*} params
 */
function initFromIntegral(params: OnLoadQuery) {
  payFrom.value = PAY_TYPE.INTEGRAL
  orderNumber.value = params.no
  totalPrice.value = params.price
  if (params.integral) {
    integral.value = params.integral
    initUserIntegralSystemtotal()
  }
}

const integralTotal = ref('0')
const integralResidue = ref('0')

// 获取用户全部积分
async function initUserIntegralSystemtotal() {
  const { code, data, msg } = await doGetUserIntegralSystemtotal()
  if (code !== 200 || (!data && data !== 0)) {
    uni.showToast({ title: `${msg || '获取用户积分失败'}`, icon: 'none' })
    return
  }
  integralResidue.value = data
  // 积分在支付金额前已经扣除 所以在这边要加上所需积分才是当前积分
  integralTotal.value = String(+data + +integral.value)
}

async function initFromOrder(params: OnLoadQuery) {
  payFrom.value = PAY_TYPE.ORDER
  orderNumber.value = params.no
  const { data, code } = await doGetOrderPayment(orderNumber.value)
  console.log('code跳转', code)
  switch (code) {
    case 30012:
      uni.redirectTo({ url: `/basePackage/pages/paySuccess/Index?orderNo=${orderNumber.value}&payFrom=${PAY_TYPE.ORDER}` })
      flag.value = false
      break
    case 30011: //订单不存在
    case 30013: // 订单已无法支付
      //重定向到订单列表
      uni.redirectTo({ url: '/pluginPackage/order/orderList/orderList' })
      flag.value = false
      break
    case 200:
      totalPrice.value = data.payAmount
      initRebateBalance(data.payAmount)
      break
    default:
      uni.showToast({ icon: 'none', title: '获取支付金额失败' })
      initRebateBalance(data.payAmount)
      break
  }
}

function initRebateBalance(payAmount: string) {
  if (payFrom.value !== 'ORDER') {
    return
  }
  rebate.disabled = true
  rebate.checked = false
  doGetRebatePayBalance({ payAmount, orderNo: orderNumber.value }).then(({ code, data }) => {
    if (code !== 200) {
      return
    }
    const { disabled, balance } = data
    rebate.balance = balance || '0'
    rebate.disabled = disabled
  })
}

function initFromBalance(params: OnLoadQuery) {
  payFrom.value = PAY_TYPE.BALANCE
  totalPrice.value = params.price
}

function initFromMember() {
  payFrom.value = PAY_TYPE.MEMBER
  totalPrice.value = onLoadExtra.value.memberPrice
  flag.value = false
}

async function initUserBalance() {
  const { code, msg, data } = await doGetUserBalance()
  if (code !== 200)
    uni.showToast({
      title: `${msg ? msg : '获取余额失败'}`,
      icon: 'none',
    })
  balance.value = (code === 200 && data) || '0'
}

/**
 * 初始轮询
 */
function initLoopCheckPayStatus() {
  if (!flag.value) return
  const res = storage.get(orderCacheKey)
  const { orderNumber: orderN, pirce, type, activeExtra: extra, payFrom: form } = res
  payFrom.value = form
  orderNumber.value = orderN
  totalPrice.value = pirce
  currentPayType.value = type
  if (extra) {
    onLoadExtra.value = JSON.parse(extra)
  }
  isModalShow.value = true
  loopCheckPayStatus()
}

/**
 * 循环检查支付状态
 */
function loopCheckPayStatus() {
  let count = 1
  checkPayStatus(false)
  try {
    //先清除一次定时器
    if (time) {
      clearInterval(time)
    }
    // 开启轮训
    time = setInterval(() => checkPayStatus(++count > 10), 800)
  } catch (error) {
    if (time) {
      clearInterval(time)
    }
  }
  payBtnLoading.value = false
}

/**
 * 定时器循环体
 */
function checkPayStatus(skipLoop: boolean) {
  if (skipLoop) {
    return closeLoopCheck()
  }
  payMethod[payFrom.value].success()
}

/**
 * 取消轮训检查
 */
const closeLoopCheck = () => {
  if (time) {
    clearInterval(time)
  }
  isModalShow.value = false
  payMethod[payFrom.value].error()
}

/**
 * 订单付款状态查询
 */
async function generalOrderPaymentStatusInquiry(isPaySuccessFn: (a: string) => Promise<any>, url: string, type: boolean | 'ACCOMPLISH') {
  const { code, data } = await isPaySuccessFn(unref(orderNumber))
  if (code === 200 && data === type) {
    if (time) {
      clearInterval(time)
    }
    isModalShow.value = false
    if (payFrom.value === 'INTEGRAL') {
      uni.showToast({
        icon: 'none',
        title: '支付成功',
      })
    }
    uni.redirectTo({
      url,
    })
    return
  }
}

/**
 * 支付调取支付页面
 */
function handelPaySubmit() {
  payMethod[payFrom.value][currentPayType.value]()
}

/**
 * 余额支付
 */
const handleBalancePayConfirm = async () => {
  payBtnLoading.value = true
  payMethod[payFrom.value]['BALANCE_CONFIRM']()
}
const handleCancelBalancePay = () => {
  payBtnLoading.value = false
}

function modalClose() {
  if (time) {
    clearTimeout(time)
  }
  isModalShow.value = false
  uni.redirectTo({ url: '/pluginPackage/order/orderList/orderList' })
}

/**
 * 购买会员支付
 * @return boolean 用户判断是否能继续处理 循环检查支付状态
 */
async function memberPay() {
  uni.showLoading({
    title: '支付中',
  })
  try {
    const { code, data } = await doPostActiveMember({
      id: onLoadExtra.value.memberId,
      payType: currentPayType.value,
      payAmount: totalPrice.value,
      paidRuleId: onLoadExtra.value.ruleId,
    })
    if (code !== 200) {
      uni.showToast({
        icon: 'none',
        title: '支付调用失败',
      })
      return false
    }
    uni.hideLoading()
    handlePay(data)
    return true
  } catch (e) {
    uni.showToast({
      icon: 'none',
      title: '支付调用失败',
    })
    return false
  }
}

function handlePay(data: { data: string; outTradeNo: string }) {
  orderNumber.value = data.outTradeNo
  setPayOrderInfo()
  if (currentPayType.value === ORDERPAYMENT.BALANCE) {
    return
  }
  // #ifdef H5
  document.write(data.data)
  //  #endif
  //  #ifndef H5
  requestPayment(data.data)
  //  #endif
}
</script>
<template>
  <view>
    <view class="pay">
      <!-- <view class="pay__price">{{ totalPrice }}</view> -->
      <view v-if="payFrom === 'INTEGRAL'" class="base-box">
        <view style="font-size: 30rpx; font-weight: 700; margin-bottom: 20rpx"> 支付积分</view>
        <view class="pay__pay-item">
          <view class="pay__pay-item--icon">
            <text>所需积分:</text>
            <text class="pay__pay-item--intergal">{{ integral }}</text>
          </view>
        </view>
        <view class="pay__pay-item">
          <view class="pay__pay-item--icon">
            <text>剩余积分:</text>
            <text class="pay__pay-item--intergal">{{ integralResidue }}</text>
          </view>
        </view>
      </view>
      <view class="base-box">
        <view style="font-size: 30rpx; font-weight: 700; margin-bottom: 20rpx"> 支付方式</view>
        <u-radio-group v-model="currentPayType">
          <view v-if="payFrom !== 'BALANCE'" class="pay__pay-item">
            <view class="pay__pay-item--icon">
              <q-icon color="#FA3534" name="icon-a-path1" size="40rpx" style="margin-right: 8rpx" />
              <text>&nbsp;储值</text>
              <q-price :price="balance" class="pay__pay-item--balance" font-size="28rpx" unit="¥" />
              <text v-if="balanceInvalid" class="pay__pay-item--insufficient">可用余额不足</text>
            </view>
            <u-radio :disabled="balanceInvalid" :name="ORDERPAYMENT.BALANCE" active-color="#fa3534" />
          </view>
          <view class="pay__pay-item">
            <view class="pay__pay-item--icon">
              <q-icon color="#50B674" name="icon-weixin2" size="44rpx" style="margin-right: 8rpx" />
              <text>&nbsp;微信</text>
            </view>
            <u-radio :name="ORDERPAYMENT.WECHAT" active-color="#fa3534" />
          </view>
          <!-- #ifndef MP-WEIXIN -->
          <view class="pay__pay-item">
            <view class="pay__pay-item--icon">
              <q-icon color="#06b4fd" name="icon-zhifubao1" size="44rpx" style="margin-right: 8rpx" />
              <text>&nbsp;支付宝</text>
            </view>
            <u-radio :name="ORDERPAYMENT.ALIPAY" active-color="#fa3534" />
          </view>
          <!-- #endif -->
        </u-radio-group>
      </view>
      <view class="pay__main">
        <view v-if="payFrom === 'ORDER' && !rebate.disabled" class="pay__pay-base pay__rebate pay__pay-item">
          <view class="pay__pay-item--icon">
            <q-icon color="#5ab830" name="icon-weixinzhifu" size="44rpx" style="margin-right: 8rpx" />
            <text>消费返利</text>
          </view>
          <view class="pay__rebate-right">
            <q-price :price="rebate.balance" class="pay__rebate--price" font-size="30rpx" unit="￥" />
            <u-switch v-model="rebate.checked" :disabled="new Decimal(rebate.balance).lte(0)" :size="35" active-color="#FA3534" />
          </view>
        </view>
      </view>
    </view>
    <u-modal
      v-model="isPayModal"
      :content="modalContent"
      :content-style="{ fontWeight: 'bold', color: '#000' }"
      :show-cancel-button="true"
      :show-title="false"
      @cancel="handleCancelBalancePay"
      @confirm="handleBalancePayConfirm"
    />

    <q-round-default :box-height="98" style="border-radius: 0">
      <view class="footer">
        <view class="footer__price">
          合计：
          <text v-show="payFrom === 'INTEGRAL'">
            <text style="color: #fa3534">{{ integral }}积分</text>
            +
          </text>
          <q-price :price="totalPrice" font-size="40rpx" style="color: #fa3534" unit="￥" />
        </view>
        <view class="footer__pay" @click="handelPaySubmit"> 确认支付</view>
      </view>
    </q-round-default>
    <u-modal v-model="isModalShow" :show-cancel-button="true" :show-confirm-button="false" :show-title="false" @cancel="closeLoopCheck">
      <view class="paySuccessText">
        <u-loading mode="flower" size="60" />
        <text>支付结果检查中</text>
      </view>
    </u-modal>
    <Auth />
  </view>
</template>

<style lang="scss" scoped>
@include b(paySuccessText) {
  @include flex;
  height: 100rpx;
  color: rgba(0, 0, 0, 0.411);
}

.base-box {
  margin-top: 20rpx;
  width: 100%;
  background: #ffffff;
  padding: 20rpx 10rpx 20rpx 30rpx;
}

@include b(pay) {
  @include e(view) {
    height: 202rpx;
    line-height: 202rpx;
    text-align: center;
    @include m(price) {
      font-weight: bold;
      @include flex;
      font-size: 52rpx;
      color: #000;
      font-family: YuanQuanYuan, YuanQuanYuan-Bold;
      &::before {
        content: '￥';
        font-size: 32rpx;
        color: #000;
      }
    }
  }
  @include e(main) {
    padding: 0 20rpx;
    @include m(msg) {
      /* 支付方式 */
      color: #333333;
      font-family: PingFang SC;
      font-size: 32rpx;
      font-weight: 400;
    }
  }
  @include e(pay-box) {
    margin-top: 20rpx;
    width: 100%;
  }
  @include e(pay-base) {
    border-radius: 20rpx;
    background: #ffffff;
  }
  @include e(pay-item) {
    height: 90rpx;
    line-height: 80rpx;
    padding: 0 30rpx 0 45rpx;
    width: 100%;
    font-size: 30rpx;
    @include flex(space-between);
    @include m(icon) {
      @include flex();
      align-items: center;
    }
    @include m(balance) {
      margin-left: 20rpx;
      color: #fa3534;
    }
    @include m(intergal) {
      margin-left: 20rpx;
      color: #fa3534;
    }
    @include m(insufficient) {
      margin-left: 20rpx;
      color: #909399;
    }
  }
  @include e(rebate) {
    margin: 20rpx 0;
    @include m(price) {
      color: #333333;
      margin-right: 10rpx;
    }
  }
  @include e(rebate-right) {
    @include flex;
    margin-right: 30rpx;
  }
  @include e(balance) {
    height: 134rpx;
    width: 750rpx;
    background: #ffffff;
    border-radius: 20rpx 20rpx 0 0;
    @include flex;
    position: fixed;
    bottom: 0;
    @include m(text) {
      width: 668rpx;
      height: 78rpx;
      font-size: 28rpx;
      text-align: CENTER;
      color: #ffffff;
      border-radius: 50rpx;
      line-height: 78rpx;
    }
  }
}

@include b(footer) {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  padding: 0 20rpx 0 30rpx;
  justify-content: space-between;
  @include e(price) {
    font-size: 30rpx;
    font-weight: 700;
  }
  @include e(pay) {
    background: #f54319;
    border-radius: 6rpx;
    width: 198rpx;
    height: 70rpx;
    font-size: 28rpx;
    line-height: 70rpx;
    text-align: center;
    color: #fff;
  }
}
</style>
