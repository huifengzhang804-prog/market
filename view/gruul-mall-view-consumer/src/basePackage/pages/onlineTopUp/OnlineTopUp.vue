<script lang="ts" setup>
import { ref } from 'vue'
import TopupCard from '@/basePackage/pages/onlineTopUp/components/topup-card.vue'
import QRoundDefault from '@/components/q-btns/q-round-default.vue'
import storage from '@/utils/storage'
import { Decimal } from 'decimal.js-light'
import type { ApiOnlineTopUp, OnlineTopUpItem } from '@/basePackage/pages/onlineTopUp/types'
import { PAY_TYPE } from '@/apis/paymentDetail/model'

const { divTenThousand, mulTenThousand } = useConvert()
const activeIndex = ref(0)
const priceVal = ref()
const topUpParam = ref<ApiOnlineTopUp>({
  switching: true,
  discountsState: true,
  ruleJson: [],
  id: '',
})
const currentTopUpItem = ref({
  id: '',
  ladderMoney: '',
  presentedGrowthValue: '',
  presentedMoney: '',
})

initSavingManage()

async function initSavingManage() {
  topUpParam.value = storage.get('topUpParam')
  // 后台未配置规则 ruleJson 为 undeinfed
  if (topUpParam.value.ruleJson) return (currentTopUpItem.value = topUpParam.value.ruleJson[0])
}

const handleTopup = () => {
  let price
  let ruleId = ''
  if (priceVal.value) {
    price = mulTenThousand(priceVal.value)
  } else {
    price = currentTopUpItem.value.ladderMoney
    ruleId = currentTopUpItem.value.id
  }
  if (Number(price) <= 0) {
    return uni.showToast({
      icon: 'none',
      title: '请填写数值',
    })
  }
  const extra = encodeURIComponent(JSON.stringify({ ruleId }))
  uni.navigateTo({ url: `/basePackage/pages/pay/Index?price=${price}&orderType=${PAY_TYPE.BALANCE}&extra=${extra}` })
}
const handleLadderMoneyClick = (index: number, topUpItem: OnlineTopUpItem) => {
  activeIndex.value = index
  currentTopUpItem.value = topUpItem
}
const handleInputBlur = (e: number | string) => {
  if (e === '') return
  const priceArr = String(new Decimal(e).absoluteValue()).split('.')
  if (priceArr.length > 1) {
    priceVal.value = `${priceArr[0]}.${priceArr[1].slice(0, 2)}`
    return
  } else {
    priceVal.value = priceArr[0]
  }
}
</script>

<template>
  <view class="Topup">
    <TopupCard v-if="topUpParam.ruleJson" :discounts-state="topUpParam.discountsState" :line-top-up-item="currentTopUpItem" />
    <template v-if="topUpParam.switching">
      <view class="Topup-title">充值</view>
      <scroll-view scroll-y style="max-height: 240px">
        <view class="price-container">
          <view
            v-for="(item, index) in topUpParam.ruleJson"
            :key="item.id"
            :class="{ activeIndex: index === activeIndex }"
            class="price-container--item"
            @click="handleLadderMoneyClick(index, item)"
            >充值{{ item.ladderMoney && divTenThousand(item.ladderMoney) }}元
          </view>
        </view>
      </scroll-view>
    </template>
    <view class="Topup-title mt30">自定义金额(元)</view>
    <view class="input-container">
      <view class="input-container--icon">￥</view>
      <u-input v-model="priceVal" height="100" placeholder="输入金额" type="digit" @blur="handleInputBlur" />
    </view>
    <u-line color="#ccc" />
    <view class="msg mt30">自定义金额不享受充值优惠</view>
  </view>
  <q-round-default class="round-btn" text="立即充值" @click="handleTopup" />
</template>

<style lang="scss" scoped>
@include b(Topup) {
  background: #fff;
  padding: 24rpx 30rpx;
}

@include b(Topup-title) {
  font-size: 28rpx;
  font-weight: bold;
  margin-top: 28rpx;
}

@include b(price-container) {
  @include flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  @include m(item) {
    width: 46%;
    border-radius: 15rpx;
    background-color: #fafafa;
    color: rgba(51, 51, 51, 1);
    font-size: 28rpx;
    text-align: center;
    padding: 20rpx;
    margin: 20rpx 10rpx;
    font-weight: bold;
    border: 1px solid transparent;
  }
}

@include b(activeIndex) {
  color: #f54319;
  border: 1px solid #f54319;
}

.mt30 {
  margin-top: 30rpx;
}

@include b(input-container) {
  margin-top: 20rpx;
  // #ifndef MP-WEIXIN
  @include flex();
  // #endif
  // #ifdef MP-WEIXIN
  @include flex(flex-start);
  // #endif
  @include m(icon) {
    font-size: 30rpx;
    font-weight: 700;
    margin-right: 10rpx;
  }
}

@include b(msg) {
  color: #ccc;
  font-size: 24rpx;
}
</style>
