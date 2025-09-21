<script setup lang="ts">
import { ref } from 'vue'
import Editor from '@/components/q-editor/editor.vue'
import { doGetIntegralRulesInfo, doPostIntegralRulesInfo, doPostIntegralRulesInfoUpdate } from '../apis'
import { IntegralRulesParameter } from '../components/IntegralRules/types'
import { ElMessage } from 'element-plus'

// 编辑标识
const isEdit = ref(false)
const dataEmpty = ref(false)
const submitFrom = ref<IntegralRulesParameter>({
  indate: 0,
  useRule:
    '<div>（1）积分使用过程中不找零、不兑现、不开发票，不可转移至其他账户。</div><div>（2）使用积分进行兑换，兑换申请一经提交, 一律不能退货</div><div>（3）如因积分商品缺货等原因导致的退货退款，积分会原路返还</div><div>（4）兑换礼品涉及运费和配送费由用户自行承担。</div><div>（5）启山智软保留最终解释权。</div>',
  ruleInfo: '<div>月度滚动过期制。 每个自然月的第1天00：00分自动清零 已满一年的积分。 举例：2022年8月1日开始清除2023年7月31日</div>',
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
    {
      rulesParameter: {
        consumeJson: [
          { consumeGrowthValueType: '', isSelected: false, orderQuantityAndAmount: '', presentedGrowthValue: '' },
          { consumeGrowthValueType: '', isSelected: false, orderQuantityAndAmount: '', presentedGrowthValue: '' },
        ] as any,
      },
      gainRuleType: 'CONSUME',
      open: false,
    },
  ],
})
// 积分规则映射对象
const integralGainRuleMapping = ref({
  LOGIN: [
    {
      key: 0,
      value: 0,
    },
    {
      key: 0,
      value: 0,
    },
  ],
  SING_IN: [
    {
      key: 0,
      value: 0,
    },
    {
      key: 0,
      value: 0,
    },
  ],
  CONSUME: [
    {
      key: 0,
      value: 0,
    },
    {
      key: 0,
      value: 0,
    },
  ],
})

initRulesInfo()

const consumptionAcquisitionList = ref([
  { consumeGrowthValueType: 'ORDER_QUANTITY', isSelected: false, orderQuantityAndAmount: '', presentedGrowthValue: '' },
  { consumeGrowthValueType: 'ORDER_AMOUNT', isSelected: false, orderQuantityAndAmount: '', presentedGrowthValue: '' },
])
const consumeGrowthValueType = ref('ORDER_QUANTITY')
async function initRulesInfo() {
  const { code, data, msg } = await doGetIntegralRulesInfo()
  if (code !== 200) {
    ElMessage.error(msg || '积分规则获取失败')
    return
  }
  if (data && data.useRule) {
    formatData(data)
    submitFrom.value = data
    consumptionAcquisitionList.value = data.integralGainRule[3].rulesParameter.consumeJson
    if (data.integralGainRule[3].rulesParameter.consumeJson[0]?.isSelected === true) {
      consumeGrowthValueType.value = 'ORDER_QUANTITY'
    } else {
      consumeGrowthValueType.value = 'ORDER_AMOUNT'
    }
    return
  }
  // 如果第一次编辑可能无返回 data 字段 提交表单时需要调用接口 doPostIntegralRulesInfo
  dataEmpty.value = true
}
const handleEditIntegralRules = (flag = false) => {
  isEdit.value = !isEdit.value
  if (flag) {
    initRulesInfo()
  }
}
const handleSubmit = async () => {
  //提交前把收集到的数据赋值给提交表单 防止数据回显
  // submitFrom.value = submitFromDate
  if (!submitVilidate()) return
  const validate = validateIntegralGainRuleMapping()
  if (validate === 'COMPLETE') {
    formatSubmitFrom()
    const { code, msg } = await (dataEmpty.value ? doPostIntegralRulesInfo(submitFrom.value) : doPostIntegralRulesInfoUpdate(submitFrom.value))
    if (code !== 200) {
      ElMessage.error(msg || `${dataEmpty.value ? '保存' : '修改'}积分规则失败`)
      return
    }
    ElMessage.success(`${dataEmpty.value ? '保存' : '修改'}积分规则成功`)
    initRulesInfo()
    reset()
  } else {
    ElMessage.info(`连续${validate === 'LOGIN' ? '登录' : '签到'}天数输入重复，请修改`)
  }
}
/**
 * 整理提交表单数据
 */
function formatSubmitFrom() {
  submitFrom.value.integralGainRule.forEach((item) => {
    let obj: any = {}
    const integralArray = integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN']
    if (integralArray) {
      for (let i = 0; i < integralArray.length; i++) {
        const integralItmeKey = integralArray[i].key
        const integralItmeValue = integralArray[i].value
        obj[integralItmeKey] = integralItmeValue
      }
      item.rulesParameter.extendValue = obj
    }
    if (item.gainRuleType === 'CONSUME') {
      if (!item.open) {
        consumptionAcquisitionList.value.forEach((item) => {
          item.isSelected = false
        })
      } else {
        consumptionAcquisitionList.value.forEach((item) => {
          item.isSelected = item.consumeGrowthValueType === consumeGrowthValueType.value
        })
      }
      item.rulesParameter.consumeJson = consumptionAcquisitionList.value as any
    }
  })
}
/**
 * 后端返回数据整理
 */
function formatData(data: IntegralRulesParameter) {
  data.integralGainRule.forEach((item) => {
    let arr: any = []
    let integralArray = integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN']
    if (integralArray) {
      for (const key in item.rulesParameter.extendValue) {
        arr.push({
          key,
          value: (item.rulesParameter.extendValue as any)[key],
        })
      }
      integralGainRuleMapping.value[item.gainRuleType as 'LOGIN' | 'SING_IN'] = arr
    }
  })
}
/**
 * 检验连续登录天数重复
 */
function validateIntegralGainRuleMapping() {
  const map = new Map()
  const loginLen = integralGainRuleMapping.value.LOGIN.filter((a) => !map.has(a.key) && map.set(a.key, a.value)).length
  if (loginLen !== integralGainRuleMapping.value.LOGIN.length) {
    return 'LOGIN'
  }
  map.clear()
  const singInLen = integralGainRuleMapping.value.SING_IN.filter((a) => !map.has(a.key) && map.set(a.key, a.value)).length
  if (singInLen !== integralGainRuleMapping.value.SING_IN.length) {
    console.log('singInLen', singInLen)
    return 'SING_IN'
  }
  return 'COMPLETE'
}
// 校验提交表单数据
function submitVilidate() {
  const { indate, useRule, ruleInfo, integralGainRule } = submitFrom.value
  if (!indate) {
    ElMessage.info(`请输入积分有效期`)
    return false
  }
  if (!useRule.trim().length || useRule === '<p><br></p>') {
    ElMessage.info(`请输入积分规则`)
    return false
  }
  if (!ruleInfo.trim().length || ruleInfo === '<p><br></p>') {
    ElMessage.info(`请输入积分值信息`)
    return false
  }
  const basicsValueValidate = integralGainRule.every((item) => !!item.rulesParameter.basicsValue)
  if (basicsValueValidate) {
    ElMessage.info(`请输入首次赠送积分值信息`)
    return false
  }
  const errorMsg = errorMsgValidate()
  console.log(errorMsg)

  if (errorMsg) {
    ElMessage.info(errorMsg)
    return false
  }
  return true
}
// 校验登录天数和对应积分值
function errorMsgValidate() {
  let errorMsg = ''
  const loginArr = integralGainRuleMapping.value.LOGIN
  const singInArr = integralGainRuleMapping.value.SING_IN
  const InArr = integralGainRuleMapping.value.SING_IN
  loginArr.forEach((item) => {
    if (!item.key) {
      errorMsg = '请输入连续登录天数'
      return errorMsg
    }
    if (!item.value) {
      errorMsg = '请输入连续登录赠送积分值'
      return errorMsg
    }
  })
  if (errorMsg) return errorMsg
  singInArr.forEach((item) => {
    if (!item.key) {
      errorMsg = '请输入连续签到天数'
      return errorMsg
    }
    if (!item.value) {
      errorMsg = '请输入连续登签到赠送积分值'
      return errorMsg
    }
  })
  if (errorMsg) return errorMsg
  InArr.forEach((item) => {
    if (!item.key) {
      return errorMsg
    }
    if (!item.value) {
      errorMsg = '请输入赠送的积分值'
      return errorMsg
    }
  })
  if (errorMsg) return errorMsg
}
function reset() {
  isEdit.value = false
  dataEmpty.value = false
}
// 积分
const integralChangeFn = (val: string | number | boolean) => {
  console.log(val)
  if (val === 'ORDER_QUANTITY') {
    consumptionAcquisitionList.value[0].isSelected = true
    consumptionAcquisitionList.value[1].isSelected = false
  }
  if (val === 'ORDER_AMOUNT') {
    consumptionAcquisitionList.value[1].isSelected = true
    consumptionAcquisitionList.value[0].isSelected = false
  }
}
</script>

<template>
  <div style="padding: 16px; overflow: auto">
    <el-button v-show="!isEdit" round :type="'primary'" @click="handleEditIntegralRules(false)">编辑积分规则</el-button>
    <div class="title">积分使用规则</div>
    <div class="use_rules p20">
      <div v-if="isEdit" style="border: 1px solid #ccc">
        <Editor v-model="submitFrom.useRule" :height="200"></Editor>
      </div>
      <div v-else v-dompurify-html="submitFrom.useRule"></div>
    </div>
    <div class="title">积分有效期</div>
    <div class="use_rules p20">
      <div>
        积分有效期为
        <el-input-number v-if="isEdit" v-model="submitFrom.indate" :controls="false" :min="1" :max="12" style="width: 50px" />
        <span v-else>{{ submitFrom.indate }}</span>
        个月
      </div>
    </div>
    <div class="title">积分值规则</div>
    <div class="use_rules p20">
      <div v-if="isEdit" style="border: 1px solid #ccc">
        <Editor v-model="submitFrom.ruleInfo"></Editor>
      </div>
      <div v-else v-dompurify-html="submitFrom.ruleInfo"></div>
    </div>
    <div class="title">积分获取规则</div>
    <div class="use_rules p20">
      <template v-if="isEdit && submitFrom?.integralGainRule">
        <el-checkbox v-model="submitFrom.integralGainRule[0].open">分享</el-checkbox>
        <span class="use_rules__msg">（限制用户首次分享才可获得积分）</span>
        <div class="get_rules">
          每日首次分享获得
          <el-input-number v-model="submitFrom.integralGainRule[0].rulesParameter.basicsValue" :controls="false" :min="1" style="width: 100px" />
          点积分
        </div>
        <el-checkbox v-model="submitFrom.integralGainRule[1].open">登录</el-checkbox>
        <span class="use_rules__msg">（每日首次登录获得积分，7天中有2个节点可以额外赠送积分，第8天00:00数据清空）</span>
        <div class="get_rules">
          每日首次登录获得
          <el-input-number v-model="submitFrom.integralGainRule[1].rulesParameter.basicsValue" :controls="false" :min="1" style="width: 100px" />
          点积分, 第
          <el-input-number v-model="integralGainRuleMapping.LOGIN[0].key" :controls="false" :min="1" :max="6" style="width: 100px" />
          天连续登录将额外获赠
          <el-input-number v-model="integralGainRuleMapping.LOGIN[0].value" :controls="false" :min="1" style="width: 100px" />
          点积分； 第

          <el-input-number v-model="integralGainRuleMapping.LOGIN[1].key" :controls="false" :min="1" :max="7" style="width: 100px" />
          天连续登录将额外获得
          <el-input-number v-model="integralGainRuleMapping.LOGIN[1].value" :controls="false" :min="1" style="width: 100px" />
          点积分。
        </div>
        <el-checkbox v-model="submitFrom.integralGainRule[2].open" style="margin-right: 30px">签到</el-checkbox>
        <span class="use_rules__msg">（每日签到获得积分，7天中有2个节点可以额外赠送积分，第8天00:00数据清空）</span>
        <div class="get_rules">
          每日首次签到获得
          <el-input-number v-model="submitFrom.integralGainRule[2].rulesParameter.basicsValue" :controls="false" :min="1" style="width: 100px" />
          点积分, 第
          <el-input-number v-model="integralGainRuleMapping.SING_IN[0].key" :max="6" :controls="false" :min="1" style="width: 100px" />
          天连续签到将额外获赠
          <el-input-number v-model="integralGainRuleMapping.SING_IN[0].value" :controls="false" :min="1" style="width: 100px" />点积分； 第
          <el-input-number v-model="integralGainRuleMapping.SING_IN[1].key" :controls="false" :min="1" style="width: 100px" :max="7" />
          天连续签到将额外获得
          <el-input-number v-model="integralGainRuleMapping.SING_IN[1].value" :controls="false" :min="1" style="width: 100px" />
          点积分。
        </div>

        <el-checkbox v-model="submitFrom.integralGainRule[3].open" style="margin-right: 30px">消费获得</el-checkbox>
        <span class="use_rules__msg">（成功交易笔数及实付金额只统计订单状态为【已完成】的数值，且将【已结算订单、积分订单、储值充值】剔除）</span>
        <el-radio-group
          v-model="consumeGrowthValueType"
          class="ml-4"
          style="margin-top: 5px; display: flex; flex-direction: column; align-items: start; margin-left: 20px"
          @change="integralChangeFn"
        >
          <template v-for="(item, index) in consumptionAcquisitionList" :key="index">
            <el-form-item v-if="item.consumeGrowthValueType === 'ORDER_QUANTITY'">
              <el-radio v-model="item.isSelected" label="ORDER_QUANTITY">
                每成功交易(已完成)
                <el-input v-model="item.orderQuantityAndAmount" style="width: 100px" />
                笔订单，奖励
                <el-input v-model="item.presentedGrowthValue" style="width: 100px" />
                点积分
              </el-radio>
            </el-form-item>
            <el-form-item v-else-if="item.consumeGrowthValueType === 'ORDER_AMOUNT'">
              <el-radio v-model="item.isSelected" label="ORDER_AMOUNT">
                实付金额(不含运费)，每满
                <el-input v-model="item.orderQuantityAndAmount" style="width: 100px" />
                元，奖励
                <el-input v-model="item.presentedGrowthValue" style="width: 100px" />
                点积分
              </el-radio>
            </el-form-item>
            <!-- 
                    <el-radio value="ORDER_QUANTITY" size="large">
                        <div class="get_rules">
                            每成功交易(已完成)
                            <el-input-number
                                v-model="consumptionAcquisitionList[0].orderQuantityAndAmount"
                                :max="6"
                                :controls="false"
                                :min="1"
                                style="width: 100px"
                            />
                            笔订单，奖励
                            <el-input-number
                                v-model="consumptionAcquisitionList[0].presentedGrowthValue"
                                :max="6"
                                :controls="false"
                                :min="1"
                                style="width: 100px"
                            />
                            点积分
                        </div>
                    </el-radio>
                    <el-radio value="ORDER_AMOUNT" size="large"
                        ><div class="get_rules">
                            实付金额(不含运费)，每满
                            <el-input-number
                                v-model="consumptionAcquisitionList[1].orderQuantityAndAmount"
                                :max="6"
                                :controls="false"
                                :min="1"
                                style="width: 100px"
                            />
                            笔订单，奖励
                            <el-input-number
                                v-model="consumptionAcquisitionList[1].presentedGrowthValue"
                                :max="6"
                                :controls="false"
                                :min="1"
                                style="width: 100px"
                            />
                            点积分
                        </div>
                    </el-radio> -->
          </template>
        </el-radio-group>
        <div class="save">
          <el-button round @click="handleEditIntegralRules(true)">返回</el-button>
          <el-button round type="primary" @click="handleSubmit">保存</el-button>
        </div>
      </template>
      <template v-else>
        <div v-if="submitFrom?.integralGainRule?.[0]?.open">
          每日首次分享获得 {{ submitFrom?.integralGainRule?.[0]?.rulesParameter?.basicsValue ?? 0 }} 点积分
        </div>
        <div v-show="submitFrom?.integralGainRule?.[1]?.open">
          每日首次登录获得 {{ submitFrom?.integralGainRule?.[1]?.rulesParameter?.basicsValue ?? 0 }} 点积分, 第
          {{ integralGainRuleMapping.LOGIN?.[0].key }} 天连续登录将额外获赠
          {{ integralGainRuleMapping.LOGIN?.[0].value }}
          点积分； 第
          {{ integralGainRuleMapping.LOGIN?.[1].key }}
          天连续登录将额外获得
          {{ integralGainRuleMapping.LOGIN?.[1].value }}
          点积分。
        </div>
        <div v-show="submitFrom?.integralGainRule?.[2]?.open">
          每日首次签到获得 {{ submitFrom?.integralGainRule?.[2]?.rulesParameter?.basicsValue ?? 0 }} 点积分, 第
          {{ integralGainRuleMapping.SING_IN?.[0].key }} 天连续签到将额外获赠 {{ integralGainRuleMapping.SING_IN?.[0].value }} 点积分； 第
          {{ integralGainRuleMapping.SING_IN?.[1].key }} 天连续签到将额外获得 {{ integralGainRuleMapping.SING_IN?.[1].value }} 点积分。
        </div>
        <div v-show="submitFrom?.integralGainRule?.[3]?.open">
          <p v-if="consumptionAcquisitionList?.[0].isSelected">
            每成功交易（已完成），{{ consumptionAcquisitionList?.[0].orderQuantityAndAmount }} 笔订单，奖励
            {{ consumptionAcquisitionList?.[0].presentedGrowthValue }} 点积分
          </p>
          <p v-else-if="consumptionAcquisitionList?.[1]?.isSelected">
            实付金额（不含运费），每满 {{ consumptionAcquisitionList?.[1]?.orderQuantityAndAmount }} 元，奖励
            {{ consumptionAcquisitionList?.[1]?.presentedGrowthValue }} 点积分
          </p>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped lang="scss">
@include b(title) {
  margin: 15px 10px;
  @include flex;
  justify-content: flex-start;
  font-size: 14px;
  color: #555cfd;
  font-weight: 700;
  &::before {
    content: '';
    display: inline-block;
    height: 14px;
    width: 2.5px;
    background: #555cfd;
    margin-right: 9px;
  }
}
@include b(use_rules) {
  font-size: 14px;
  font-family: sans-serif, sans-serif-Normal;
  font-weight: normal;
  text-align: LEFT;
  color: #333333;
  line-height: 30px;
  @include e(msg) {
    font-size: 14px;
    text-align: LEFT;
    color: #d3d3d3;
  }
}
@include b(save) {
  margin: 30px 0;
  text-align: center;
}
@include b(get_rules) {
  text-indent: 1em;
  padding: 10px 0;
}
.p20 {
  padding: 0 0 0 20px;
}
</style>
