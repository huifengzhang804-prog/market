<script setup lang="ts">
import { ref, computed } from 'vue'
import QNav from '@/components/q-nav/q-nav.vue'
import { doGetSavingManage } from '@/apis/paymentDetail'
import storage from '@/utils/storage'
import { onLoad } from '@dcloudio/uni-app'
import type { ApiOnlineTopUp } from '@/basePackage/pages/onlineTopUp/types'

const { divTenThousand } = useConvert()
const currentBalance = ref('0')
const currentBalanceComputed = computed(() => divTenThousand(currentBalance.value).toString().split('.'))
const topUpParam = ref<ApiOnlineTopUp>({
  switching: false,
  discountsState: true,
  ruleJson: [],
  id: '',
})

onLoad(({ balance }: any) => {
  uni.$emit('updateTitle')
  if (balance) {
    currentBalance.value = balance
  }
})
initSavingManage()

async function initSavingManage() {
  const { code, data, msg } = await doGetSavingManage()
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取储值管理信息失败'}`, icon: 'none' })
  topUpParam.value = data
  storage.set('topUpParam', data)
}
// // 可视区域滑动高度计算
// const detailHeight = computed(() => {
//     if (!storedCardNode.value) {
//         return '1100rpx'
//     }
//     // 6是  margin-top: 12rpx ， 1rpx = 0.5px = 1物理像素 discountsState
//     return `${systermInfo.screenHeight - (storedCardNode.value.height + 6 + statusBarHeight.value)}px`
// })
const handleTheDetail = () => {
  uni.navigateTo({ url: '/basePackage/pages/billingDetails/BillingDetails' })
}
const handleTopUp = () => {
  uni.navigateTo({ url: '/basePackage/pages/onlineTopUp/OnlineTopUp' })
}
const handleNavToWithdrawalMoney = () => {
  uni.navigateTo({ url: '/basePackage/pages/withdrawalMoney/WithdrawalMoney' })
}
</script>

<template>
  <q-nav title="我的储值" @right-click="handleTheDetail" />
  <view class="stored-card">
    <view class="stored-card__title">可用储值(元)</view>
    <view class="stored-card__body">
      <view>
        <text class="stored-card__body--num">{{ currentBalanceComputed[0] }}.</text>
        <text style="color: red; font-size: 34rpx; font-weight: 700">{{ (currentBalanceComputed[1] || '00').slice(0, 2) }}</text>
      </view>
      <view v-if="topUpParam.switching" class="flex" style="transform: translateY(-2rpx)">
        <view class="stored-card__body--add" @click="handleTopUp">充值</view>
      </view>
    </view>
    <view class="detail">
      <text class="detail__text" @click="handleTheDetail">查看明细</text>
    </view>
    <Auth />
  </view>
</template>

<style scoped lang="scss">
@include b(stored-card) {
  padding: 30rpx;
  background: #fff;
  @include e(title) {
    font-size: 24rpx;
    font-weight: normal;
    color: #9a9a9a;
    margin-bottom: 30rpx;
  }
  @include e(body) {
    @include flex(space-between);
    @include m(num) {
      font-size: 50rpx;
      font-weight: Bold;
      color: red;
      &::before {
        content: '￥';
        font-size: 30rpx;
      }
    }
    @include m(add) {
      padding: 10rpx 40rpx;
      border: 1rpx solid #9a9a9a;
      border-radius: 10rpx;
      font-weight: Bold;
    }
  }
}
@include b(detail) {
  margin-top: 12rpx;
  background: #fff;
  padding: 50rpx 0 0 0;
  @include e(text) {
    margin-left: 5rpx;
  }
}
</style>
