<script lang="ts" setup>
import { reactive, onMounted } from 'vue'
import { doGetaccounts, doPutaccounts } from '../apis'
import Auth from '@/components/auth/auth.vue'

// 支付宝提现表单
const withdrawFormOfBank = reactive({
  name: '',
  alipayAccount: '',
})

function validateBank() {
  if (!withdrawFormOfBank.alipayAccount || !withdrawFormOfBank.name) {
    uni.showToast({
      icon: 'none',
      title: '请完善支付宝信息',
    })
    return false
  } else {
    return true
  }
}

async function initaccounts() {
  const { code, data, msg } = await doGetaccounts()
  if (code !== 200) {
    return uni.showToast({ title: `${msg ? msg : '获取提现账户失败'}`, icon: 'none' })
  }
  withdrawFormOfBank.alipayAccount = data.ALIPAY?.alipayAccount
  withdrawFormOfBank.name = data.ALIPAY?.name
}

const handleWithdrawSubmit = async (type: string) => {
  let detail = { ...withdrawFormOfBank }
  if (!validateBank()) return

  const { code, msg } = await doPutaccounts({ type, detail })
  if (code !== 200) {
    return uni.showToast({ title: `${msg ? msg : '提交提现账户失败'}`, icon: 'none' })
  }
  uni.showToast({ title: `保存成功`, icon: 'none' })
  uni.navigateBack()
}
onMounted(() => {
  initaccounts()
})
</script>
<template>
  <view>
    <view class="withDraw__form">
      <view style="height: 112rpx; background-color: #fff">
        <view class="withDraw__form-item" style="margin-top: 20rpx">
          <text class="withDraw__form-item--text">姓名</text>
          <u-input v-model="withdrawFormOfBank.name" height="60" placeholder="请输入您的姓名" />
        </view>
      </view>
      <view style="height: 112rpx; background-color: #fff">
        <view class="withDraw__form-item">
          <text class="withDraw__form-item--text">账号</text>
          <u-input v-model="withdrawFormOfBank.alipayAccount" height="60" placeholder="请输入您的账号" />
        </view>
      </view>
    </view>
    <view style="display: flex">
      <!-- <view class="withDraw__btn cancel" @click="handlePopupclose">取消</view> -->
      <view
        :class="withdrawFormOfBank.name === '' || withdrawFormOfBank.alipayAccount === '' ? 'withDraw__btn1' : 'withDraw__btn'"
        @click="handleWithdrawSubmit('ALIPAY')"
        >保存
      </view>
    </view>
  </view>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(withDraw) {
  // padding: 60rpx 56rpx;
  font-size: 30rpx;
  @include e(title) {
    margin-bottom: 30rpx;
    text-align: center;
    font-size: 36rpx;
  }

  @include e(form-item) {
    margin-left: 32rpx;
    height: 112rpx;
    border-bottom: 1rpx solid rgb(231, 231, 231);
    background-color: #fff;
    padding: 32rpx 32rpx 32rpx 0;
    @include flex;
    font-size: 32rpx;
    @include m(text) {
      margin-right: 32rpx;
      width: 180rpx;
      &::before {
        content: '*';
        display: inline-block;
        font-size: 32rpx;
        color: red;
      }
    }
  }
  @include e(btn) {
    // width: 160rpx;
    height: 98rpx;
    line-height: 98rpx;
    background-color: #fa3534;
    // border-radius: 36rpx;
    text-align: center;
    color: #fff;
    font-size: 36rpx;
    // margin: 20rpx auto;
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
  }
  @include e(btn1) {
    color: #999999;
    background-color: #e7e7e7;
    height: 98rpx;
    line-height: 98rpx;
    text-align: center;
    font-size: 36rpx;
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
  }
}
</style>
