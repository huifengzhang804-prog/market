<script lang="ts" setup>
import { computed, ref } from 'vue'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import { doPostWithdrawRebate } from '../apis'
import { useRebatePrices } from '../hooks'
import Auth from '@/components/auth/auth.vue'
import useRebateDispatcher from '@/store/dispatcher/useRebateDispatcher'

const $rebateDispatcherStore = useRebateDispatcher()

const { init, formatPrice } = useRebatePrices()
const initRebateBalance = () => {
  init().then((res) => {
    canBeWithdrawalAmount.value = formatPrice(res.rebateBalance)
  })
}
initRebateBalance()
const { mulTenThousand } = useConvert()
const rebateDetailsPrice = ref('')
const showkeyboard = ref(false)
const isShowPopup = ref(false)
const canBeWithdrawalAmount = ref('0')
const bgColor = computed(() => (!rebateDetailsPrice.value.length || +rebateDetailsPrice.value > 500 ? '#e7e7e7' : '#fa3534'))
const footerColor = computed(() => (!rebateDetailsPrice.value.length || +rebateDetailsPrice.value > 500 ? '#999' : '#fff'))

interface PayTypeItem {
  text: string
  type: string
}

const payTypeArr: PayTypeItem[] = [
  {
    text: '微信',
    type: 'WECHAT',
  },
  {
    text: '支付宝',
    type: 'ALIPAY',
  },
  {
    text: '银行卡',
    type: 'BANK_CARD',
  },
]
const currentPayObject = ref<PayTypeItem>({
  text: '微信',
  type: 'WECHAT',
})

const handleKeyboardChange = (value: string) => {
  const pointIndex = rebateDetailsPrice.value.indexOf('.')
  if (value === '.' && pointIndex > -1) {
    return
  }
  if (value !== '.' && pointIndex > -1 && pointIndex === rebateDetailsPrice.value?.length - 3) {
    return
  }
  rebateDetailsPrice.value += value
}
const backspace = () => {
  // 删除value的最后一个字符
  if (rebateDetailsPrice.value.length) rebateDetailsPrice.value = rebateDetailsPrice.value.substring(0, rebateDetailsPrice.value.length - 1)
}
const handleSheetClick = (index: number) => {
  currentPayObject.value = payTypeArr[index]
}
const handleAllWithdrawal = () => {
  rebateDetailsPrice.value = canBeWithdrawalAmount.value
}
const handleSubmitWithdraw = async () => {
  const rebateDetailsPricePlus = mulTenThousand(rebateDetailsPrice.value)
  const canWithDrawAmoutPlus = mulTenThousand(canBeWithdrawalAmount.value)
  const isGreaterThan = rebateDetailsPricePlus.greaterThan(canWithDrawAmoutPlus)
  if (isGreaterThan) {
    return uni.showToast({ title: '提现金额不能大于可提现金额', icon: 'none' })
  }
  const { code, msg } = await doPostWithdrawRebate({ type: currentPayObject.value.type, amount: rebateDetailsPricePlus })
  if (code === 200) {
    initRebateBalance()
    $rebateDispatcherStore.updateData()
    // 返回上一页
    uni.navigateBack({
      success: function () {
        uni.showToast({
          title: '提现申请成功',
          icon: 'none',
          duration: 2000,
        })
      },
    })
  } else {
    uni.showToast({ title: msg || '提现申请失败', icon: 'none' })
  }
}
</script>
<template>
  <view class="head">
    <view class="head__main">
      <text>提现到</text>
      <view class="head__main--right" @click="isShowPopup = true">
        <text class="head__main--type">{{ currentPayObject.text }}</text>
        <u-icon color="#606266" name="arrow-right"></u-icon>
      </view>
    </view>
  </view>
  <view class="main">
    <view class="main__price" @click="showkeyboard = true">
      <text>￥</text>
      {{ rebateDetailsPrice || '0.00' }}
      <view v-if="showkeyboard" class="main__focus"></view>
    </view>
    <view class="main__msg">
      <view>
        <text>可提现￥</text>
        <text style="margin-left: 5rpx">{{ canBeWithdrawalAmount }}</text>
      </view>
      <text v-if="+canBeWithdrawalAmount > 0" style="color: #005cf4" @click="handleAllWithdrawal">全部提现</text>
    </view>
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
  <!-- 说明弹窗 -->
  <u-action-sheet
    v-model="isShowPopup"
    :border-radius="20"
    :cancel-btn="true"
    :list="payTypeArr"
    :tips="{ text: '提现到', color: '#909399', fontSize: 24 }"
    @click="handleSheetClick"
  ></u-action-sheet>
  <!-- 说明弹窗 -->
  <u-keyboard ref="uKeyboard1" v-model="showkeyboard" :mask="false" mode="number" @backspace="backspace" @change="handleKeyboardChange" />
  <q-footer-default
    :bg-color="bgColor"
    :color="footerColor"
    :disable="!rebateDetailsPrice.length || +rebateDetailsPrice > 500"
    is-bold
    text="立即申请"
    @click="handleSubmitWithdraw"
  ></q-footer-default>
  <Auth />
</template>

<style lang="scss" scoped>
@keyframes showText {
  0% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

page {
  background: #fff;
}

@include b(head) {
  height: 123rpx;
  @include flex;
  background: #f2f2f2;
  @include e(main) {
    @include flex;
    color: #000;
    justify-content: space-between;
    padding: 0 30rpx;
    width: 100%;
    height: 84rpx;
    background: #fff;
    @include m(type) {
      color: #606266;
      margin-right: 15rpx;
    }
    @include m(right) {
      @include flex;
    }
  }
}

@include b(main) {
  padding: 30rpx;
  @include e(price) {
    @include flex;
    justify-content: flex-start;
    padding: 0 0 20rpx 0;
    font-size: 40rpx;
    font-weight: 400;
    border-bottom: 1px solid #ccc;
  }
  @include e(focus) {
    margin-left: 10rpx;
    height: 35rpx;
    width: 2rpx;
    background: #303133;
    animation: showText 1.5s linear infinite;
  }
  @include e(msg) {
    @include flex;
    justify-content: flex-start;
    margin-top: 20rpx;
    color: #999999;
    @include flex;
    justify-content: space-between;
    font-size: 28rpx;
  }
}

@include b(popup-title) {
  color: rgba(0, 0, 0, 0.4);
  /* 自动布局 */
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 24rpx 32rpx;
}

@include b(popup-item) {
  @include flex;
  height: 112rpx;
  font-size: 32rpx;
  color: #000;
  font-weight: 400;
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
