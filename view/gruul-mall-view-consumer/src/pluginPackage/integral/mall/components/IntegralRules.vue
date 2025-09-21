<script setup lang="ts">
import { ref } from 'vue'
import formatRichText from '@/pluginPackage/utils/formatRichText'
import { doGetIntegralRulesInfo } from '@pluginPackage/integral/api'
import type { IntegralRulesParameter } from '@/apis/plugin/integral/model'

const submitFrom = ref<IntegralRulesParameter>({
  indate: 12,
  useRule: '',
  ruleInfo: '',
  integralGainRule: [
    {
      rulesParameter: {
        basicsValue: 1,
        extendValue: {},
      },
      gainRuleType: 'SHARE',
      open: false,
    },
    {
      rulesParameter: {
        basicsValue: 1,
        extendValue: {},
      },
      gainRuleType: 'LOGIN',
      open: false,
    },
    {
      rulesParameter: {
        basicsValue: 1,
        extendValue: {},
      },
      gainRuleType: 'SING_IN',
      open: false,
    },
  ],
})
// 积分规则映射对象
const integralGainRuleMapping = ref({
  LOGIN: [
    {
      key: 2,
      value: 30,
    },
    {
      key: 7,
      value: 50,
    },
  ],
  SING_IN: [
    {
      key: 5,
      value: 60,
    },
    {
      key: 7,
      value: 20,
    },
  ],
})
const dataEmpty = ref(false)

initRulesInfo()

async function initRulesInfo() {
  const { code, data, msg } = await doGetIntegralRulesInfo()
  if (code !== 200) {
    uni.showToast({ title: `${msg || '获取积分规则信息失败'}`, icon: 'none' })
    return
  }
  if (data && data.useRule) {
    formatData(data)
    submitFrom.value = data
    return
  }
  dataEmpty.value = true
}
/**
 * 后端返回数据整理
 * @param {*} data
 */
function formatData(data: IntegralRulesParameter) {
  data.integralGainRule.forEach((item) => {
    let arr: any = []
    let integralArray = integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN']
    if (integralArray) {
      for (const key in item.rulesParameter.extendValue) {
        arr.push({
          key,
          value: item.rulesParameter.extendValue[key],
        })
      }
      integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN'] = arr
    }
  })
}
</script>

<template>
  <view class="line">
    <view class="integral_rules">积分获取规则</view>
    <view class="text">
      <view style="padding-left: 20rpx"> 每日首次分享获得{{ submitFrom.integralGainRule[0].rulesParameter.basicsValue }}点积分 </view>
      <view style="padding: 20rpx">
        每日首次登录获得{{ submitFrom.integralGainRule[1].rulesParameter.basicsValue }}点积分, 第{{
          integralGainRuleMapping.LOGIN[0].key
        }}天连续登录将额外获赠{{ integralGainRuleMapping.LOGIN[0].value }}点积分； 第{{ integralGainRuleMapping.LOGIN[1].key }}天连续登录将额外获得{{
          integralGainRuleMapping.LOGIN[1].value
        }}点积分。
      </view>
      <view style="padding-left: 20rpx">
        每日首次签到获得{{ submitFrom.integralGainRule[2].rulesParameter.basicsValue }}点积分, 第{{
          integralGainRuleMapping.SING_IN[0].key
        }}天连续签到将额外获赠{{ integralGainRuleMapping.SING_IN[0].value }}点积分； 第{{
          integralGainRuleMapping.SING_IN[1].key
        }}天连续签到将额外获得{{ integralGainRuleMapping.SING_IN[1].value }}点积分。
      </view>
    </view>
  </view>
  <view class="line">
    <view class="integral_rules">积分抵扣规则</view>
    <view class="text">
      <rich-text :nodes="formatRichText(submitFrom.useRule)"></rich-text>
    </view>
  </view>
  <view class="line">
    <view class="integral_rules">积分使用规则</view>
    <view class="text">
      <rich-text :nodes="formatRichText(submitFrom.ruleInfo)"></rich-text>
    </view>
  </view>
  <view class="line">
    <view class="integral_rules">积分有效期</view>
    <view class="text">
      <text>积分有效期为{{ submitFrom.indate }}个月</text>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(integral_rules) {
  color: #333;
  font-size: 30rpx;
  text-indent: 10rpx;
  line-height: 30rpx;
  border-left: 5rpx solid #333;
}
.line {
  padding: 40rpx 40rpx 0 40rpx;
}
.text {
  padding: 20rpx 0rpx 0 20rpx;
  color: #666;
}
</style>
