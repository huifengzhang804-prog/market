<script lang="ts" setup>
import { ElMessage, FormInstance } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { doGetAvailableMemberConfig, doPostPaidMember, doPutPaidMember, doGetPaidMember } from '../apis'
import type { MEMBERBENEFITSTATUS } from '../index'
import { defineProps, ref, reactive } from 'vue'
import useConvert from '@/composables/useConvert'

type MemberBenefitItemType = {
  id: string
  rightsName: string
  rightsType: string
}

const props = defineProps({
  memberId: {
    type: String,
    default: '',
  },
  memberLevel: {
    type: Number,
    default: undefined,
  },
})
type BenefitType = {
  name: string
  rightsType: keyof typeof MEMBERBENEFITSTATUS
  memberRightsId: string
  extendValue?: number
}
type FormDataType = {
  id: string
  paidMemberName: string
  relevancyRightsList: RelevancyRightsType[]
  paidRuleJson: PaidRuleType[]
}
type PaidRuleType = {
  price: number
  effectiveDurationType: keyof typeof MEMBERBENEFITSTATUS
}
type RelevancyRightsType = {
  memberRightsId: string
  extendValue: number
  rightsType: string
}
type MemberBenefit = keyof typeof MEMBERBENEFITSTATUS | string

const { mulHundred, divHundred, divTenThousand, mulTenThousand } = useConvert()
const loading = ref(false)
const ruleFormRef = ref<FormInstance>()
const checkedMap = ref<Map<MemberBenefit, BenefitType>>(new Map())
const memberBenefitList = ref<MemberBenefitItemType[]>([])
const goodsDiscount = ref(0)
const multiIntegral = ref(0)
const formData = reactive<FormDataType>({
  id: '',
  paidMemberName: '',
  paidRuleJson: [],
  relevancyRightsList: [],
})
const validateRules: Record<MemberBenefit, (val: string | number) => boolean> = {
  INTEGRAL_MULTIPLE: verifyIntegral,
  GOODS_DISCOUNT: verifypointDiscount,
}
const validateTips: Record<MemberBenefit, string> = {
  INTEGRAL_MULTIPLE: '积分值为2-9.9倍保留一位小数',
  GOODS_DISCOUNT: '商品折扣值为0.1-9.9折保留一位小数',
}
const paymentRules = [
  {
    name: '1个月',
    label: 'ONE_MONTH',
  },
  {
    name: '3个月',
    label: 'THREE_MONTH',
  },
  {
    name: '12个月',
    label: 'TWELVE_MONTH',
  },
  {
    name: '3年',
    label: 'THREE_YEAR',
  },
  {
    name: '5年',
    label: 'FIVE_YEAR',
  },
]
const rules = reactive({
  paidMemberName: [{ required: true, message: '请输入会员名称', trigger: 'blur' }],
  paidRuleJson: [
    {
      validator: paidRuleValidate,
      trigger: 'blur',
    },
  ],
})
function paidRuleValidate(rule: any, value: any, callback: any) {
  for (let i = 0; i < value.length; i++) {
    if (!value[i].effectiveDurationType) {
      return callback('请完善付费规则')
    }
  }
  callback()
}

initMemberConfig()

const handleSubmit = async () => {
  if (!formData.paidRuleJson.length) {
    ElMessage.error('请填写付费规则')
    return Promise.reject('请填写付费规则')
  }
  const formEl = ruleFormRef.value
  await validateForm(formEl)
  if (validateSubmit()) {
    const cloneForm = cloneDeep(formData)
    cloneForm.relevancyRightsList = Array.from(checkedMap.value.values()).map((item) => {
      if (item.rightsType === 'GOODS_DISCOUNT') {
        item.extendValue = Number(mulHundred(goodsDiscount.value))
      } else if (item.rightsType === 'INTEGRAL_MULTIPLE') {
        item.extendValue = Number(mulHundred(multiIntegral.value))
      } else {
        delete item.extendValue
      }
      return item
    }) as RelevancyRightsType[]
    cloneForm.paidRuleJson = cloneForm.paidRuleJson.map((item) => {
      item.price = Number(mulTenThousand(item.price))
      return item
    })
    const { code, msg } = props.memberId ? await doPutPaidMember(cloneForm) : await doPostPaidMember(cloneForm)
    if (code === 200) {
      ElMessage.success('保存成功')
      return Promise.resolve('保存成功')
    } else {
      ElMessage.error(msg ? msg : '保存失败')
      return Promise.reject(msg ? msg : '保存失败')
    }
  } else {
    return Promise.reject('校验失败')
  }
}
const handleAddRules = () => {
  formData.paidRuleJson.push({
    price: 0,
    effectiveDurationType: '',
  })
}
const handleDelRules = (index: number) => {
  formData.paidRuleJson.splice(index, 1)
}
const handleCheckList = (e: BenefitType) => {
  if (checkedMap.value.has(e.memberRightsId)) {
    checkedMap.value.delete(e.memberRightsId)
  } else {
    checkedMap.value.set(e.memberRightsId, e)
  }
}

function validateForm(formRefs?: FormInstance | null) {
  if (!formRefs) {
    return Promise.reject(new Error('no form instance input'))
  }
  return new Promise((resolve, reject) => {
    formRefs?.validate((isValid, invalidFields) => {
      if (isValid) {
        resolve('success valid')
      } else {
        reject(invalidFields)
      }
    })
  })
}
/**
 * 校验提交
 */
function validateSubmit() {
  const tempArr: { type: boolean; tips: string }[] = []
  checkedMap.value.forEach((item) => {
    if (validateRules[item.rightsType]) {
      console.log('tempArr', item.rightsType === 'GOODS_DISCOUNT' ? goodsDiscount.value : multiIntegral.value)
      tempArr.push({
        type: validateRules[item.rightsType](item.rightsType === 'GOODS_DISCOUNT' ? goodsDiscount.value : multiIntegral.value),
        tips: validateTips[item.rightsType] || '',
      })
    }
  })

  const tipsArr = tempArr.filter((item) => {
    return !item.type
  })
  tipsArr.length && ElMessage.warning(tipsArr[0].tips)
  return !tipsArr.length
}

/**
 * 校验积分输入值
 */
function verifyIntegral(val: string | number) {
  // 2-9.9
  const NumberValue = Number(val)
  return NumberValue >= 2 && NumberValue <= 9.9 && verifyRoundToOneDecimal(String(val), 1)
}
/**
 * 校验保留一位小数
 * @param {string} e 值
 * @param {number} num 保留位数
 */
function verifyRoundToOneDecimal(e: string, num: number) {
  const regu = /^\d+(\.\d?)?$/
  if (e !== '') {
    return regu.test(e)
  } else {
    return false
  }
}
/**
 * 校验商品折扣
 */
function verifypointDiscount(val: string | number) {
  console.log(val)
  // 0.1-9.9
  const NumberValue = Number(val)
  return NumberValue >= 0.1 && NumberValue <= 9.9 && verifyRoundToOneDecimal(String(val), 1)
}
async function initMemberConfig() {
  const { code, data } = await doGetAvailableMemberConfig()
  if (code !== 200) return ElMessage.error('获取会员权益失败')
  memberBenefitList.value = data
  initMember()
}
async function initMember() {
  if (props.memberId) {
    const { code, data, msg } = await doGetPaidMember(String(props.memberId))
    if (code === 200 && data) {
      const { id, paidMemberName, paidRuleJson, relevancyRightsList } = data
      formData.id = id
      formData.paidMemberName = paidMemberName
      formData.paidRuleJson = paidRuleJson.map((item: PaidRuleType) => {
        item.price = Number(divTenThousand(item.price))
        return item
      })
      formData.relevancyRightsList = relevancyRightsList
      relevancyRightsList.forEach((item: RelevancyRightsType) => {
        if (item.rightsType === 'GOODS_DISCOUNT') {
          goodsDiscount.value = Number(divHundred(item.extendValue))
        } else if (item.rightsType === 'INTEGRAL_MULTIPLE') {
          multiIntegral.value = Number(divHundred(item.extendValue))
        }
        checkedMap.value.set(item.memberRightsId, item)
      })
    } else {
      ElMessage.error(msg ? msg : '获取失败')
    }
  }
}
defineExpose({ handleSubmit })
</script>

<template>
  <div style="padding: 0 40px">
    <h1 class="title">基本信息</h1>
    <el-form ref="ruleFormRef" :model="formData" :rules="rules" label-width="auto">
      <el-form-item label="会员等级"> SVIP{{ props.memberLevel }} </el-form-item>
      <el-form-item label="等级名称" prop="paidMemberName">
        <el-input v-model.trim="formData.paidMemberName" :maxlength="8" :minlength="3" placeholder="请输入等级名称" style="width: 226px" />
      </el-form-item>
      <el-form-item label="付费规则" prop="paidRuleJson" required>
        <div v-show="formData.paidRuleJson.length" style="width: 450px">
          <el-table :data="formData.paidRuleJson" :height="formData.paidRuleJson.length > 4 ? '220px' : undef" size="small" style="width: 100%">
            <el-table-column label="有效期">
              <!-- paymentRules -->
              <template #default="{ row }">
                <el-select v-model="row.effectiveDurationType" class="m-2" placeholder="请选择">
                  <el-option v-for="item in paymentRules" :key="item.name" :label="item.name" :value="item.label">{{ item.name }}</el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="价格">
              <template #default="{ row }">
                <el-input-number v-model="row.price" :min="0.01" :precision="2" controls-position="right" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="50px">
              <template #default="{ $index }">
                <el-link :underline="false" type="primary" @click="handleDelRules($index)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div style="width: 100%">
          <el-link :underline="false" type="primary" @click="handleAddRules">添加规则</el-link>
        </div>
      </el-form-item>
      <h1 class="title">权益礼包</h1>
      <el-form-item>
        <el-row v-for="item in memberBenefitList" :key="item.id" style="width: 100%">
          <el-form-item label-width="0" style="margin: 10px 0">
            <el-checkbox
              :key="item.id + checkedMap.has(item.id)"
              :checked="checkedMap.has(item.id)"
              :label="{ name: item.rightsName, memberRightsId: item.id, extendValue: 0 }"
              @change="handleCheckList({ name: item.rightsName, rightsType: item.rightsType, memberRightsId: item.id, extendValue: 0 })"
              >{{ item.rightsName }}</el-checkbox
            >
            <el-input v-if="item.rightsType === 'GOODS_DISCOUNT'" v-model="goodsDiscount" style="width: 130px; margin: 0 20px">
              <template #append>折</template>
            </el-input>
            <el-input v-else-if="item.rightsType === 'INTEGRAL_MULTIPLE'" v-model="multiIntegral" style="width: 130px; margin: 0 20px">
              <template #append>倍</template>
            </el-input>
          </el-form-item>
        </el-row>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
@include b(title) {
  font-size: 14px;
  color: #323233;
  font-weight: 700;
  margin-bottom: 20px;
}
@include b(msg) {
  font-size: 12px;
  color: #c4c4c4;
}

@include b(nav-button) {
  width: 1010px;
  position: fixed;
  bottom: 10px;
  padding: 15px 0px;
  display: flex;
  justify-content: center;
  box-shadow: 0 0px 10px 0px #d5d5d5;
  background-color: white;
  z-index: 999;
  margin: 0 auto;
  margin-left: -55px;
}
</style>
