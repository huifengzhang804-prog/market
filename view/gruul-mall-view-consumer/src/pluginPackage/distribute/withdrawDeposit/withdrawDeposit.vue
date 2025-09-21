<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { doGetDistributeCenter, doPostWithdraw } from '../apis'
import Auth from '@/components/auth/auth.vue'
import { Decimal } from 'decimal.js-light'

const columns = [
  { text: '微信', name: 'WECHAT' },
  { text: '支付宝', name: 'ALIPAY' },
  { text: '银行卡', name: 'BANK_CARD' },
]
const checkFn = () => {
  show.value = true
}
const text = ref('微信')
const name = ref('WECHAT')
const clickItem = (index: number) => {
  text.value = columns[index].text
  name.value = columns[index].name
}
// 数字键盘
const show = ref(false)
const show1 = ref(false)
const num = ref('')

//是否可点击提交按钮
const canSubmit = computed(() => {
  const amount = num.value
  if (!amount) {
    return false
  }
  if (Number(num.value) > 500) {
    return false
  }
  const decimalAmount = mulTenThousand(amount)
  if (decimalAmount.lte(0)) {
    return false
  }
  const undrawnAmount = new Decimal(undrawn.value)
  return undrawnAmount.gte(decimalAmount)
})

// 按键被点击
const valChange = (val: number | string) => {
  const pointIndex = num.value.indexOf('.')
  if (val === '.' && pointIndex > -1) {
    return
  }
  if (val !== '.' && pointIndex > -1 && pointIndex === num.value?.length - 3) {
    return
  }
  if (val === '') return
  num.value += val
}
// 退格键被点击
const backspace = (val: number) => {
  if (num.value.length) num.value = num.value.substr(0, num.value.length - 1)
}

const { divTenThousand, mulTenThousand } = useConvert()

// 全部提现
const undrawn = ref('0')
const handleWithdrawAll = () => {
  num.value = divTenThousand(undrawn.value).toString()
}

// 总金额
async function initMemerInfo() {
  const { code, data } = await doGetDistributeCenter()
  if (code && code === 200) {
    undrawn.value = data.undrawn
  }
}

// 确认
const handleWithdrawSubmit = async () => {
  if (!canSubmit.value) {
    return
  }
  const tempObj = {
    type: name.value,
    amount: mulTenThousand(num.value),
  }
  //提交提现工单
  const { code, msg } = await doPostWithdraw(tempObj)
  if (code === 200) {
    undrawn.value = new Decimal(undrawn.value).minus(tempObj.amount).toString()
    uni.$emit('distributeUndrawn', undrawn.value)
    num.value = ''
    uni.showToast({
      title: '提现申请成功',
      icon: 'none',
      duration: 2000,
    })
    setTimeout(function () {
      uni.navigateBack()
    }, 200)
  } else {
    uni.showToast({
      icon: 'none',
      title: `${msg ? msg : '提现申请失败'}`,
    })
  }
}
onMounted(() => {
  initMemerInfo()
})
</script>
<template>
  <view class="hear">
    <u-action-sheet v-model="show" :list="columns" @click="clickItem"></u-action-sheet>
    <view
      style="display: flex; justify-content: space-between; font-size: 26rpx; padding: 24rpx 32rpx; background-color: #fff; height: 84rpx"
      @click="checkFn"
    >
      <text style="font-weight: bold; width: 690rpx"
        >提现到
        <text style="color: #606266; font-weight: normal; float: right; margin-right: 10rpx">{{ text }}</text>
      </text>
      <q-icon color="#909193" name="icon-chevron-right" size="40rpx" />
    </view>
  </view>
  <view style="width: 100%; background-color: #fff">
    <view class="num" @click="show1 = true">
      <text>￥</text>
      <text style="width: 730rpx; display: block; height: 114rpx; line-height: 114rpx">
        {{ num }}
      </text>
    </view>
    <view style="display: flex; justify-content: space-between">
      <text style="font-size: 28rpx; color: #999; margin: 10rpx 0 10rpx 30rpx; max-width: 70%"
        >可提现余额 ￥{{ divTenThousand(undrawn)?.toFixed(2) || 0 }}
      </text>
      <text style="font-size: 28rpx; color: #005cf4; margin: 10rpx 30rpx" @click="handleWithdrawAll">全部提现</text>
    </view>
    <view class="prompt">
      <text> 提现说明：</text>
      <view class="content">
        1、请确保提现账号信息真实有效 <br />
        2、申请提交成功后请耐心等待审核结果<br />
        3、微信单笔提现范围0.1~500，单日最高2万元 <br />
        4、支付宝单笔提现范围0.1~10万元，单日最高500万元
      </view>
    </view>
    <u-keyboard ref="uKeyboard" v-model="show1" :mask="false" mode="number" @backspace="backspace" @change="valChange"></u-keyboard>
  </view>
  <view :class="canSubmit ? 'btn' : 'btn1'" @click="handleWithdrawSubmit">立即申请</view>
  <Auth />
</template>

<style lang="scss" scoped>
.hear {
  margin: 20rpx 0;
}

::v-deep uni-page-wrapper {
  background-color: #fff;
  height: 100%;
}

::v-deep #van-field-1-input {
  text-align: right;
  color: #606266;
}

::v-deep .van-cell .van-cell--clickable .van-field {
  width: 500rpx;
}

::v-deep #van-field-2-input {
  height: 64rpx;
  line-height: 64rpx;
  font-size: 64rpx;
  text-align: left;
}

@include b(num) {
  display: flex;
  font-size: 48rpx;
  height: 114rpx;
  line-height: 114rpx;
  background-color: #fff;
  margin: 0 30rpx;
  border-bottom: 1rpx solid rgba(0, 0, 0, 0.15);
}

@include b(btn) {
  position: fixed;
  bottom: 0;
  left: 0;
  height: 98rpx;
  width: 100%;
  line-height: 98rpx;
  background-color: #fa3534;
  color: #fff;
  text-align: center;
  font-size: 36rpx;
}

@include b(btn1) {
  position: fixed;
  bottom: 0;
  left: 0;
  height: 98rpx;
  width: 100%;
  line-height: 98rpx;
  background-color: #e7e7e7;
  color: #999;
  text-align: center;
  font-size: 36rpx;
}
@include b(prompt) {
  padding: 0 30rpx;
  margin-top: 50%;
  color: #7f7878;
  .content {
    margin-top: 16rpx;
    line-height: 50rpx;
    margin-left: 30rpx;
  }
}
</style>
