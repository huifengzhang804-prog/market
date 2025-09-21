<script setup lang="ts">
import { ref } from 'vue'
import { CaretRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useConversionPrice, useConversionHaoPrice } from '@/utils/useConversionPrice'
import { doGetUserBalance } from '@/apis/consumer'
import { doGetOrderPayment, doGetOrderPayPage, doGetOrderIsPaySuccess, doGetOrderInfo } from '@/apis/order'
import { doPostIntegralOrderPay, doGetUserIntegralSystemtotal } from '@/apis/integral'
import { doGetSavingPay } from '@/apis/paymentDetail'
import { ORDERPAYMENT } from '@/views/personalcenter/order/myorder/types'
import type { Decimal } from 'decimal.js'
import useConvert from '@/composables/useConvert'
import Storage from '@/libs/storage'
import { usePropertiesListStore } from '@/store/modules/propertiesList'

import { storeToRefs } from 'pinia'

// 定义API响应类型
interface ApiResponse<T = any> {
  code: number
  msg?: string
  data: T
}

interface PayPageResponse {
  data: string
  outTradeNo: string
}

const { getterPropertiesList } = storeToRefs(usePropertiesListStore())

const storage = new Storage()
type List = {
  type: keyof typeof ORDERPAYMENT
  icon?: string
  color?: string
}[]

const deposit = ref(false)
const wx = ref(true)
const zfb = ref(false)
const $route = useRoute()
const $router = useRouter()
const topComponents = getterPropertiesList.value
const payTypeList = ref<List>([
  {
    type: 'BALANCE',
    icon: 'icon-yuezhifu',
    color: '#ffc000',
  },
  {
    type: 'WECHAT',
    icon: 'icon-weixinzhifu',
    color: '#5ab830',
  },
  {
    type: 'ALIPAY',
    icon: 'icon-alipay',
    color: '#06b4fd',
  },
])
const { divTenThousand } = useConvert()
const currentPayType = ref<keyof typeof ORDERPAYMENT>('WECHAT')
const totalPrice = ref<Decimal | string>('0')
const orderNumber = ref('')
// const payBtnLoading = ref(false)
const currentRuleId = ref('')
// const isModalShow = ref(false)
const flag = ref(false)
// 余额支付询问
const balancePayModal = reactive({
  isPayModal: false,
  modalContent: '是否使用储值支付',
  balance: '0',
})
const { balance } = toRefs(balancePayModal)
// 余额是否不足
const balanceInsufficient = computed(() => {
  if (!balance.value) return true
  const max = Math.max(parseFloat(Number(totalPrice.value).toFixed(2)), parseFloat(useConversionPrice(balance.value).toFixed(2)))
  const isBalanceInsufficient = parseFloat(Number(totalPrice.value).toFixed(2)) === max
  return isBalanceInsufficient
})
const haveBALANCE = ref(true)
const integral = ref('')

// let time: NodeJS.Timeout

initUserBalance()
init()

const handledepositChange = () => {
  wx.value = false
  zfb.value = false
  deposit.value = true
  currentPayType.value = 'BALANCE'
  console.log('currentPayType.value', currentPayType.value)
}
const handlewxChange = () => {
  deposit.value = false
  zfb.value = false
  wx.value = true
  currentPayType.value = 'WECHAT'
  console.log('currentPayType.value', currentPayType.value)
}
const handlezfbChange = () => {
  wx.value = false
  deposit.value = false
  zfb.value = true
  currentPayType.value = 'ALIPAY'
  console.log('currentPayType.value', currentPayType.value)
}
async function initUserBalance() {
  const { code, msg, data } = await doGetUserBalance()
  if (code !== 200) ElMessage.error(`${msg ? msg : '获取余额失败'}`)
  if (data) {
    balance.value = data
  }
}

const oneHourSecond = 60 * 60
const oneDaySecond = 24 * oneHourSecond
const secondToPayTimeout = (seconds: string) => {
  const nums = parseInt(seconds)
  let dd = Math.floor(nums / oneDaySecond)
  let hh = Math.floor((nums % oneDaySecond) / oneHourSecond)
  let mm = Math.floor((nums % oneHourSecond) / 60)
  return [dd, hh, mm]
}
// 订单过期时间
const timeout = reactive({
  d: 0,
  h: 0,
  m: 0,
})
// 获取订单过期时间
async function getExpirationTime(no: string) {
  const { data, code } = await doGetOrderInfo(no)
  if (code === 200) {
    const timeSplit = secondToPayTimeout(data.timeout.payTimeout)
    timeout.d = timeSplit[0]
    timeout.h = timeSplit[1]
    timeout.m = timeSplit[2]
  }
}

async function init() {
  const no = $route.query.no as string
  const price = $route.query.price as string
  const ruleId = $route.query.ruleId as string
  const integrals = $route.query.integral as string

  if (integrals) {
    const no = $route.query.no as string
    orderNumber.value = no
    totalPrice.value = price
    integral.value = integrals
    if (ruleId) {
      currentRuleId.value = ruleId
    }
    return
  }

  if (price) {
    payTypeList.value = payTypeList.value.filter((item) => item.type !== 'BALANCE')
    currentPayType.value = 'WECHAT'
    totalPrice.value = price
    if (ruleId) {
      currentRuleId.value = ruleId
    }
    haveBALANCE.value = false
    return
  }
  if (no) {
    const no = $route.query.no as string
    getExpirationTime(no)
    orderNumber.value = no
    const { data, code, msg } = await doGetOrderPayment(no)
    if (code === 31005) {
      $router.replace({
        path: '/personalcenter/order/myorder',
        query: {},
      })
      flag.value = false
      return
    }
    if (code !== 200) {
      if (msg === '订单已支付') {
        ElMessage.success(msg)
      } else {
        ElMessage.error(msg || '获取支付金额失败')
      }
      $router.replace('/personalcenter/order/myorder')
      return
    }
    if (data) {
      totalPrice.value = useConversionPrice(data.payAmount)
    }
    return
  }
  flag.value = true
}
/**
 * @description: 初始轮询
 * @returns {*}
 */
function initLoopCheckPayStatus() {
  if (!flag.value) return
  if ($route.query.no) {
    orderNumber.value = $route.query.no as string
  } else {
    const payOrderNo = storage.getItem(`payOrderNo`)
    const { orderNumber: orderNo, pirce, type } = payOrderNo
    orderNumber.value = orderNo
    totalPrice.value = pirce
    currentPayType.value = type
  }

  WXloopCheckPayStatus()
}

let timer: ReturnType<typeof setInterval>
/**
 * @: 微信轮询查询支付结果
 */
const WXloopCheckPayStatusBol = ref(true)
function WXloopCheckPayStatus() {
  let isModalShow = true
  try {
    if (!isModalShow) return
    clearInterval(timer as NodeJS.Timeout)
    timer = setInterval(async () => {
      if (!WXloopCheckPayStatusBol.value) return
      if (isModalShow) {
        const { code, data, msg } = await doGetOrderIsPaySuccess(orderNumber.value)
        // ACCOMPLISH UNFINISHED
        if (code === 200 && data === 'ACCOMPLISH') {
          isModalShow = false
          flag.value = true
          payStatusInt(data)
          payLoading.value = false
          return
        }
      }
    }, 3000)
  } catch (error) {
    isModalShow = false
    payLoading.value = false
  }
}
// 触发Esc键盘事件 关闭弹窗 退回订单页
const payStatusInt = (data: 'ACCOMPLISH' | 'UNFINISHED') => {
  document.dispatchEvent(
    new KeyboardEvent('keydown', {
      key: 'Escape',
      keyCode: 27,
      which: 27,
    }),
  )
  if (data === 'ACCOMPLISH') {
    ElMessage.success('支付成功')
    $router.replace({
      path: '/personalcenter/order/myorder',
    })
  } else {
    ElMessage.error('请重新支付')
  }
}
/**
 * @description:支付调取支付页面
 * @returns {*}
 */
const payLoading = ref(false)
const visible = ref(false)
const payQRcode = ref('')

const handelPaySubmit = async () => {
  // 储值支付
  if (currentPayType.value === 'BALANCE') {
    ElMessageBox.confirm('是否使用储值支付？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        payLoading.value = true
        if (integral.value) {
          integralBalanceConfig()
          return
        }
        handleBalancePayConfirm()
      })
      .catch(() => {})
    return
  }
  const price = Number($route.query.price)
  const ruleId = $route.query.ruleId as string
  // 微信 或 支付宝支付
  const response = !haveBALANCE.value
    ? await doGetSavingPay(currentPayType.value, totalPrice.value, currentRuleId.value)
    : integral.value
    ? await doPostIntegralOrderPay(unref(orderNumber), unref(currentPayType))
    : unref(orderNumber)
    ? await doGetOrderPayPage({ orderNo: unref(orderNumber), payType: unref(currentPayType) })
    : await doGetSavingPay(currentPayType.value, price, ruleId)

  const { code, data, msg } = response as ApiResponse<PayPageResponse>

  if (code !== 200) {
    if (code !== 60013) handleNavToOrderDetail()
    return ElMessage.error(msg || '调取支付金额失败')
  }

  if (!data?.data) return ElMessage(msg || '获取支付二维码失败')

  payQRcode.value = data.data
  visible.value = true
  if (unref(orderNumber)) {
    flag.value = true
    initLoopCheckPayStatus()
  } else {
    orderNumber.value = data.outTradeNo
    WXloopCheckPayStatus()
  }
}
/**
 * @description: 余额支付
 * @returns {*}
 */
const handleBalancePayConfirm = async () => {
  const response = await doGetOrderPayPage({
    orderNo: unref(orderNumber),
    payType: unref(currentPayType),
  })
  const { code, data, msg } = response as ApiResponse<PayPageResponse>

  if (code !== 200) {
    ElMessage.error(`${msg ? msg : '储值支付失败'}`)
    $router.replace({
      path: '/personalcenter/order/myorder',
      query: {},
    })
    payLoading.value = false
    return
  }
  orderNumber.value = data.outTradeNo
  flag.value = true
  initLoopCheckPayStatus()
}

/**
 * @description: 积分订单余额支付
 * @returns {*}
 */
async function integralBalanceConfig() {
  const response = await doPostIntegralOrderPay(unref(orderNumber), unref(currentPayType))
  const { code, data, msg } = response as ApiResponse<PayPageResponse>

  if (code !== 200) {
    ElMessage.error(`${msg ? msg : '储值支付失败'}`)
    $router.replace({
      path: '/personalcenter/assets/integral',
      query: {},
    })
    return
  }
  orderNumber.value = data.outTradeNo
  $router.replace({
    path: '/personalcenter/assets/integral',
    query: {},
  })
}

/**
 * @: 跳转到积分订单 或者 订单
 */
const handleNavToOrderDetail = () => {
  if (integral.value)
    $router.replace({
      path: '/personalcenter/assets/integral',
      query: {},
    })
  else
    $router.replace({
      path: '/personalcenter/order/orderdetail',
      query: {
        orderNo: orderNumber.value,
      },
    })
}

const integralSystemtotal = ref('')
// 获取积分总额 doGetUserIntegralSystemtotal
const userIntegralSystemtotal = async () => {
  const { data, msg, code } = await doGetUserIntegralSystemtotal()
  if (code !== 200) ElMessage.error(msg || '获取积分总额失败')
  else integralSystemtotal.value = data
}
userIntegralSystemtotal()

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

/**
 * @: 支付弹框关闭后
 */
const closedDialog = () => {
  if (timer) {
    clearInterval(timer)
  }
}
</script>

<template>
  <div c-w-1190 ma c-mb-90>
    <router-link to="/home">
      <div
        :style="{
          backgroundImage: 'url(' + topComponents?.topComponents?.[1]?.formData?.logo + ')',
        }"
        class="search_logo"
      />
    </router-link>
    <div bg-white c-h-102 flex items-center justify-between c-pl-20 c-pr-25 c-mb-6 b-1 c-bc-eee>
      <div v-if="haveBALANCE" flex items-center>
        <!-- <div c-w-88 c-h-88 bg-black b-1 c-bc-e31436></div> -->
        <div c-ml-14 text-left>
          <div e-c-3 c-fs-14>
            订单提交成功，请尽快付款！订单号：<span fw-700>{{ orderNumber }}</span>
          </div>
          <div e-c-6 c-fs-12>
            <!-- 推荐使用 扫码支付 &nbsp;&nbsp;&nbsp; -->
            请您在<span c-c-e31436
              ><span v-if="timeout.d">{{ timeout.d }}天</span><span v-if="timeout.h">{{ timeout.h }}小时</span>
              <span v-if="timeout.m">{{ timeout.m }}分钟</span></span
            >内完成支付，否则订单会被自动取消
          </div>
        </div>
      </div>
      <div v-else e-c-3 c-fs-14>订单提交成功，请尽快付款！否则订单会被自动取消</div>
      <div text-right>
        <div c-fs-12 e-c-6>
          应付金额
          <span c-fs-18 c-c-e31436 fw-700>
            <span v-if="integral">{{ integral }}积分+ </span>
            ￥{{ totalPrice }}</span
          >
        </div>
        <div
          v-if="haveBALANCE"
          c-fs-12
          c-c-67a4ff
          flex
          items-center
          justify-end
          cursor-pointer
          hover-c-c-e31436
          hover-underline
          @click="handleNavToOrderDetail"
        >
          订单详情<el-icon color="#67A4FF">
            <CaretRight />
          </el-icon>
        </div>
      </div>
    </div>
    <div v-if="$route.query?.integral" text-left c-h-120 c-pt-20 c-pl-50 c-mb-20 bg-white c-fs-16 fw-600>
      支付积分：
      <div c-fs-16 c-pt-10 c-pl-50>
        所需积分：<span c-c-e31436>{{ $route.query?.integral }}</span>
      </div>
      <div c-fs-16 c-pt-10 c-pl-50>
        <!-- 我的积分：<span c-c-e31436>{{ +integralSystemtotal + +$route.query?.integral }}</span> -->
        剩余积分：<span c-c-e31436>{{ integralSystemtotal }}</span>
      </div>
    </div>
    <el-radio-group v-model="currentPayType" style="width: 100%">
      <div bg-white b-1 c-bc-eee class="pay-type">
        <div v-if="haveBALANCE" c-h-80 flex items-center c-pl-33 b-b c-bc-eee @click="handledepositChange">
          <el-radio value="BALANCE" size="large" label="BALANCE" :disabled="balanceInsufficient" />
          <div c-h-28>
            <q-icon name="icon-yuezhifu" size="28px" color="#ffc000" />
          </div>
          <div c-fs-16 fw-700 e-c-3>储值支付（￥{{ divTenThousand(balance).toFixed(2) }}）</div>
        </div>
        <div c-h-80 flex items-center c-pl-33 b-b c-bc-eee @click="handlewxChange">
          <el-radio value="WECHAT" size="large" label="WECHAT" />
          <div c-h-28>
            <q-icon name="icon-weixinzhifu" size="28px" color="#5ab830" />
          </div>
          <div c-fs-16 fw-700 e-c-3>微信支付</div>
        </div>
        <div c-h-80 flex items-center c-pl-33 b-b c-bc-eee @click="handlezfbChange">
          <el-radio value="ALIPAY" size="large" label="ALIPAY" />
          <div c-h-28>
            <q-icon name="icon-alipay" size="28px" color="#06b4fd" />
          </div>
          <div c-fs-16 fw-700 e-c-3>支付宝支付</div>
        </div>
        <div c-h-101 flex justify-end items-center c-mr-25 cursor-pointer>
          <el-button :loading="payLoading" c-h-50 c-w-222 fw-700 c-lh-50 c-fs-18 color="#e31436" type="danger" @click="handelPaySubmit">
            {{ payLoading ? '支付中' : '立即支付' }}
          </el-button>
        </div>
      </div>
    </el-radio-group>

    <el-dialog v-model="visible" :title="`请使用${zfb ? '支付宝' : '微信'}扫码支付`" width="540" @closed="closedDialog">
      <img :src="payQRcode" alt="" width="500" height="500" />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(pay-type) {
  width: 100%;
  :deep(.el-radio__label) {
    display: none;
  }
}
.search_logo {
  width: 220px;
  height: 88px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: left center;
}
</style>
