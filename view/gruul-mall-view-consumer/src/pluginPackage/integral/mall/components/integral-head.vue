<script setup lang="ts">
import { reactive, ref } from 'vue'
import { doGetIntegralRulesInfo, doGetUserIntegralSystemtotal } from '@pluginPackage/integral/api'
import { onShow } from '@dcloudio/uni-app'

// const bgImg = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20230206/a3904cdbec9a4f5cacf0696df1f6ddb9.png'
// const iconImg = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20230206/ee5cee97671745eeb34893f3c0b14a20.png'

const behavior = reactive({
  singIn: {
    continueDays: 0,
    todayState: false,
  },
  open: false,
  integralTotal: 0,
})

onShow(() => {
  // 获取用户积分
  initUserIntegralSystemtotal()
})
// 积分规则
initRulesInfo()

async function initUserIntegralSystemtotal() {
  const { code, data, msg } = await doGetUserIntegralSystemtotal()
  if (code !== 200 || (!data && data !== 0)) {
    uni.showToast({ title: `${msg || '获取用户积分失败'}`, icon: 'none' })
    return
  }
  behavior.integralTotal = data
}

// 积分规则
async function initRulesInfo() {
  const { code, data, msg } = await doGetIntegralRulesInfo()
  if (code !== 200) {
    uni.showToast({ title: `${msg || '获取积分规则信息失败'}`, icon: 'none' })
    return
  }
  if (data && data.integralGainRule && Array.isArray(data.integralGainRule)) {
    const integralGainRule = data.integralGainRule.find((item: any) => item.gainRuleType === 'SING_IN')
    behavior.open = integralGainRule.open
  }
}

/**
 * 查看积分规则
 */
const handleCheckIntegralRules = () => {
  uni.navigateTo({
    url: '/pluginPackage/integral/mall/components/IntegralRules',
  })
}
/**
 * 查看积分规则
 */
const handleCheckIntegarlRecord = () => {
  uni.navigateTo({
    url: '/pluginPackage/integral/mall/components/IntegralRecord',
  })
}
// 跳转到每日签到
const jumpSignIn = () => {
  uni.navigateTo({
    url: '/pluginPackage/integral/mall/view/signIn/signIn',
  })
}
// 跳转到积分订单
const jumpIntegralOrderList = () => {
  uni.navigateTo({
    url: '/pluginPackage/integral/mall/view/integralOrderList/integralOrderList',
  })
}

const operate = ref([
  {
    name: '每日签到',
    icon: 'https://bgniao-small-file-1253272780.cos.ap-chengdu.myqcloud.com/gruul/%E6%AF%8F%E6%97%A5%E7%AD%BE%E5%88%B0.png',
    callBack: jumpSignIn,
  },
  {
    name: '积分明细',
    icon: 'https://bgniao-small-file-1253272780.cos.ap-chengdu.myqcloud.com/gruul/%E7%A7%AF%E5%88%86%E6%98%8E%E7%BB%86.png',
    callBack: handleCheckIntegarlRecord,
  },
  {
    name: '积分订单',
    icon: 'https://bgniao-small-file-1253272780.cos.ap-chengdu.myqcloud.com/gruul/%E7%A7%AF%E5%88%86%E8%AE%A2%E5%8D%95.png',
    callBack: jumpIntegralOrderList,
  },
])
</script>

<template>
  <view>
    <view class="integral_mall">
      <view class="integral_mall__value">
        <view style="font-size: 30rpx">所剩积分</view>
        <view>
          <text class="integral_mall__value--num">{{ behavior.integralTotal }}</text>
        </view>
        <view class="integral_mall__value--detail" @click="handleCheckIntegralRules">
          <u-icon name="question-circle" color="#fff" size="32" />
        </view>
      </view>
    </view>
    <view style="padding: 20rpx">
      <view class="main" :class="{ singIn: !behavior.open }">
        <template v-for="{ name, icon, callBack } in operate" :key="name">
          <view v-if="behavior.open || name !== '每日签到'" @click="callBack">
            <view style="text-align: center; margin-bottom: 16rpx">
              <image :src="icon" style="width: 48rpx; height: 48rpx" />
            </view>
            <view style="font-size: 28rpx">{{ name }}</view>
          </view>
        </template>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(integral_mall) {
  padding: 0 30rpx;
  background: rgb(253, 94, 55);
  @include e(value) {
    height: 167rpx;
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(num) {
      font-size: 53rpx;
    }
    @include m(detail) {
      display: flex;
      justify-content: right;
      width: 105rpx;
      margin-right: 15rpx;
    }
  }
}

@include b(main) {
  @include flex();
  justify-content: space-between;
  padding: 20rpx 40rpx;
  background-color: #fff;
  border-radius: 20rpx;
}
@include b(singIn) {
  justify-content: space-around !important;
}
</style>
