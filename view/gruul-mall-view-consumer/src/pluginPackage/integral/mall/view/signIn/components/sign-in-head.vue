<template>
  <view style="padding: 20rpx">
    <view class="header" :class="{ signInActive: behavior.todayState }">
      <view>
        我的积分 <text class="total">{{ integralTotal }}</text>
      </view>
      <view class="main">
        <view v-for="(item, index) in rulesInfo" :key="index" class="main_item">
          <view class="main_item--round" :class="{ signIned: behavior.continueDays > index }">
            <view class="reward">+{{ item }}</view>
          </view>
          <view class="main_item--day" :class="{ activeDay: behavior.continueDays > index, activeDayLine: behavior.continueDays > index + 1 }">
            {{ index + 1 }}
          </view>
        </view>
      </view>
      <view class="header__info">
        <view>
          已连续签到 <text class="total">{{ behavior.continueDays }}天</text>
        </view>
        <button :loading="behavior.loading" class="header__info-signInBtn" @click="handleSignIn">
          <q-icon name="icon-a-zuhe32" size="41rpx" />
          <text style="padding-left: 16rpx" :style="{ color: '#fff' }">
            {{ behavior.todayState ? '已签到' : '签到' }}
          </text>
        </button>
      </view>
    </view>
  </view>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { doGetUserIntegralSystemtotal, doGetIntegralBehaviorDays, doGetIntegralRulesInfo } from '@pluginPackage/integral/api'
import { doGetIntegralBehaviorSave } from '@/apis/plugin/integral'

// 积分
const integralTotal = ref(0)
const $emit = defineEmits(['changeList'])
// 获取积分
async function initUserIntegralSystemtotal() {
  const { code, data, msg } = await doGetUserIntegralSystemtotal()
  if (code !== 200 || (!data && data !== 0)) {
    uni.showToast({ title: `${msg || '获取用户积分失败'}`, icon: 'none' })
    return
  }
  integralTotal.value = data
}
// 获取积分
initUserIntegralSystemtotal()

const behavior = ref({
  continueDays: 0,
  todayState: false,
  loading: false,
})
// 连续签到
async function initIntegralBehaviorDays() {
  behavior.value.loading = true
  const { code, data, msg } = await doGetIntegralBehaviorDays('SING_IN')
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取签到天数失败'}`, icon: 'none' })
    return
  }
  behavior.value = { ...data, loading: false }
}
// 连续签到
initIntegralBehaviorDays()

const rulesInfo = ref([0, 0, 0, 0, 0, 0, 0])

// 获取积分规则
async function initRulesInfo() {
  const { code, data, msg } = await doGetIntegralRulesInfo()
  if (code !== 200) {
    uni.showToast({ title: `${msg || '获取积分规则信息失败'}`, icon: 'none' })
    return
  }
  if (data && data.integralGainRule && Array.isArray(data.integralGainRule)) {
    const integralGainRule = data.integralGainRule.find((item: any) => item.gainRuleType === 'SING_IN')
    if (integralGainRule && integralGainRule.open) {
      const {
        rulesParameter: { basicsValue, extendValue },
      } = integralGainRule
      rulesInfo.value = rulesInfo.value.map((item) => basicsValue)
      for (const key in extendValue) {
        rulesInfo.value[Number(key) - 1] = extendValue[key] + basicsValue
      }
    }
  }
}
// 获取积分规则
initRulesInfo()

const handleSignIn = async () => {
  if (behavior.value.todayState) return
  const { code, data, msg } = await doGetIntegralBehaviorSave('SING_IN')
  if (code !== 200) {
    uni.showToast({ title: `${msg || '签到失败'}`, icon: 'none' })
    return
  }
  uni.showToast({ title: `签到成功获得${data}积分`, icon: 'none' })
  let time = setTimeout(() => {
    initUserIntegralSystemtotal()
    initIntegralBehaviorDays()
    $emit('changeList')
    if (time) {
      clearTimeout(time)
    }
  }, 500)
}
</script>

<style scoped lang="scss">
@include b(signInActive) {
  .total {
    color: #fa3534;
  }
}
.signIned {
  color: #fa3534;
}
.activeDay {
  color: #fff !important;
  background-color: #fa3534 !important;
}
@include b(header) {
  color: #505050;
  background-color: #fff;
  padding: 30rpx;
  font-size: 30rpx;
  border-radius: 20rpx;
  @include e(info) {
    display: flex;
    justify-content: space-between;
    margin-top: 40rpx;
  }
  @include e(info-signInBtn) {
    background-color: #fa3534;
    color: #fff;
    width: 178rpx;
    height: 66rpx;
    border-radius: 8rpx;
    display: flex;
    align-items: center;
    padding: 10rpx;
    font-size: 32rpx;
    box-sizing: border-box;
    margin: 0;
  }
}
@include b(main) {
  @include flex();
  justify-content: space-between;
}
@include b(main_item) {
  position: relative;
  @include flex();
  flex-direction: column;
  color: #666666;
  @include m(round) {
    @include flex();
    font-size: 28rpx;
    height: 50rpx;
    align-items: flex-end;
    width: 80rpx;
  }
  @include m(day) {
    margin-top: 15rpx;
    border-radius: 44rpx;
    width: 44rpx;
    height: 44rpx;
    background-color: #f3f3f3;
    color: #999999;
    text-align: center;
    line-height: 44rpx;
    position: relative;
  }
}
.total {
  font-size: 40rpx;
}
.main_item:not(:last-child) {
  .main_item--day {
    &:before {
      position: absolute;
      content: '';
      width: 50rpx;
      right: -51rpx;
      top: 21rpx;
      border-top: 1px solid #999999;
    }
  }
  .activeDayLine {
    &:before {
      border-top: 1px solid #fa3534;
    }
  }
}
</style>
