<script lang="ts" setup>
import { reactive } from 'vue'
import { doGetaccounts } from '@/basePackage/apis/distribute'
import Auth from '@/components/auth/auth.vue'

const withdrawFormOfAli = reactive({
  name: '',
  alipayAccount: '',
})
const withdrawInfoOfAli = reactive({
  name: '',
  alipayAccount: '',
})
const withdrawFormOfBank = reactive({
  name: '',
  bank: '',
  cardNo: '',
})
const withdrawInfoOfBank = reactive({
  name: '',
  bank: '',
  cardNo: '',
})

initaccounts()

async function initaccounts() {
  const { code, data, msg } = await doGetaccounts()
  if (code !== 200) {
    return uni.showToast({ title: `${msg ? msg : '获取提现账户失败'}`, icon: 'none' })
  }
  withdrawInfoOfBank.bank = data.BANK_CARD?.bank
  withdrawInfoOfBank.cardNo = data.BANK_CARD?.cardNo
  withdrawInfoOfBank.name = data.BANK_CARD?.name
  withdrawInfoOfAli.alipayAccount = data.ALIPAY?.alipayAccount
  withdrawInfoOfAli.name = data.ALIPAY?.name
}

/**
 * 打开银行卡弹窗
 */
const openBankPopup = () => {
  withdrawFormOfBank.bank = withdrawInfoOfBank.bank
  withdrawFormOfBank.cardNo = withdrawInfoOfBank.cardNo
  withdrawFormOfBank.name = withdrawInfoOfBank.name
  // BankType.value = true
  uni.navigateTo({
    url: '/pluginPackage/distribute/bankCardInformation/bankCardInformation',
    // url: '/pluginPackage/distribute/bankCardInformation/bankCardInformation',
  })
}
/**
 * 打开支付宝弹窗
 */
const openAliPopup = () => {
  withdrawFormOfAli.alipayAccount = withdrawInfoOfAli.alipayAccount
  withdrawFormOfAli.name = withdrawInfoOfAli.name
  // AliType.value = true
  uni.navigateTo({
    url: '/pluginPackage/distribute/alipayInformation/alipayInformation',
  })
}
</script>
<template>
  <view>
    <view class="info">
      <view class="info__title" @click="openBankPopup">
        <view class="info__title--word">银行卡信息</view>
        <view v-if="!withdrawInfoOfBank.name" class="info__item"></view>
        <view v-else class="info__item">{{ withdrawInfoOfBank.cardNo }}</view>
        <view class="info__title--edit">
          <q-icon name="icon-chevron-right" size="50rpx" />
        </view>
      </view>
    </view>
    <view class="info">
      <view class="info__title" @click="openAliPopup">
        <view class="info__title--word">支付宝信息</view>
        <view v-if="!withdrawInfoOfAli.name" class="info__item"></view>
        <view v-else class="info__item">{{ withdrawInfoOfAli.alipayAccount }}</view>
        <view class="info__title--edit">
          <q-icon name="icon-chevron-right" size="50rpx" />
        </view>
      </view>
    </view>
  </view>
  <Auth />
</template>
<style lang="scss" scoped>
@include b(tips) {
  padding: 20rpx 26rpx 10rpx;
  color: #333333;
  font-size: 24rpx;
}

@include b(but) {
  width: 462rpx;
  height: 106rpx;
  border-radius: 16rpx;
  background: #ee3729;
  color: #fff;
  font-size: 28rpx;
  line-height: 106rpx;
  text-align: center;
  margin: 60rpx auto;
}

@include b(info) {
  font-size: 32rpxrpx;
  color: #000000;
  background: #fff;
  // padding: 32rpx 0 32rpx 32rpx;
  // margin-left: 32rpx;
  height: 112rpx;
  @include e(title) {
    margin: 0 0 32rpx 32rpx;
    height: 112rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid rgb(231, 231, 231);
    @include m(word) {
      font-size: 32rpx;
      margin-right: 32rpx;
    }
    @include m(edit) {
      color: rgba(0, 0, 0, 0.4);
    }
  }
  @include e(item) {
    width: 394rpx;
    text-align: left;
    font-size: 32rpx;
  }
}

@include b(withDraw) {
  padding: 60rpx 56rpx;
  font-size: 30rpx;
  @include e(title) {
    margin-bottom: 30rpx;
    text-align: center;
    font-size: 36rpx;
  }
  @include e(form-item) {
    height: 110rpx;
    @include flex;
    font-size: 30rpx;
    @include m(text) {
      margin-right: 20rpx;
      width: 140rpx;
      &::before {
        content: '*';
        display: inline-block;
        font-size: 26rpx;
        color: red;
      }
    }
  }
  @include e(btn) {
    width: 160rpx;
    height: 60rpx;
    line-height: 60rpx;
    background: #0f40f5;
    border-radius: 16rpx;
    text-align: center;
    color: #fff;
    font-size: 28rpx;
    margin: 20rpx auto;
  }
}

.cancel {
  border: 1px solid rgba(187, 187, 187, 1);
  color: rgba(16, 16, 16, 1);
  background: #fff;
}

.border {
  border: 1px solid rgba(187, 187, 187, 1);
  border-radius: 16rpx;
  padding-left: 20rpx !important;
}
</style>
