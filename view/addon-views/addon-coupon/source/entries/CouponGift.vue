<script lang="ts" setup>
import { ref, defineProps, PropType } from 'vue'
import SelectCoupponType from '../components/select-couppon-type.vue'
import DateUtil from '@/utils/date'
import { ElMessage } from 'element-plus'
import { doPostCouponShopGifts } from '../apis'
import { setRules } from '../setRules'
import { EffectType, pricePostHaoFormat, ProductType } from '../index'
import type { FormInstance, FormRules } from 'element-plus'

interface PropertiesType {
  toBack: () => void
  userIds: string[]
  productType: ProductType
}

const props = defineProps({
  properties: {
    type: Object as PropType<PropertiesType>,
    required: true,
  },
})
const dateUtil = new DateUtil()
// 禁用
// 编辑状态用于判定禁用
const ruleFormRef = ref<FormInstance>()
const ruleForm = ref({
  name: '',
  days: 1,
  endDate: '',
  type: 'PRICE_DISCOUNT',
  requiredAmount: 1,
  discount: 1,
  amount: 1,
  effectType: EffectType.PERIOD,
  startDate: '',
  num: 1,
  limit: 1,
  productType: props.properties.productType,
})
const rules: FormRules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  effectType: [{ required: true, message: '请选择有效时间', trigger: ['blur', 'change'] }],
  type: [{ required: true, message: '请选择优惠券类型', trigger: ['blur', 'change'] }],
  discount: [{ required: true, message: '请输入优惠券规则', trigger: 'blur' }],
  num: [{ required: true, message: '请输入每人发送数量', trigger: 'blur' }],
  limit: [{ required: true, message: '请输入每人发送数量', trigger: 'blur' }],
  requiredAmount: [{ required: true, message: '请输入优惠券规则', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入优惠券规则', trigger: 'blur' }],
  days: [
    { required: true, message: '请输入优惠券有效期', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (Number(value) === 0) {
          callback('最少1天')
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
  startDate: [{ required: true, message: '请选择开始日期', trigger: ['blur', 'change'] }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: ['blur', 'change'] }],
}
const handleSubmit = async () => {
  if (!ruleFormRef.value) return
  try {
    const isValidate = await ruleFormRef.value.validate()
    if (!isValidate) return
    dateTimeFormat()
    const params = paramsFormat()
    formatQuery(params)
    const { code } = await doPostCouponShopGifts({
      coupon: params,
      userIds: props.properties.userIds,
    })
    if (code !== 200) {
      ElMessage.error('保存失败')
      return
    }
    ElMessage.success('保存成功')
    props.properties.toBack && props.properties.toBack()
  } catch (error: any) {
    if (error.requiredAmount?.length) {
      ElMessage.info(error.requiredAmount[0].message)
      return
    }
    if (error.amount?.length) {
      ElMessage.info(error.amount[0].message)
      return
    }
  }
}

function formatQuery(params: any) {
  if (params.effectType === 'PERIOD') {
    params.days = null
  }
  if (params.effectType === 'IMMEDIATELY') {
    params.startDate = ''
    params.endDate = ''
  }
  if (params.type === 'PRICE_REDUCE') {
    // 无门槛现金券
    params.requiredAmount = null
    params.discount = null
  }
  if (params.type === 'PRICE_DISCOUNT') {
    // 无门槛折扣券
    params.amount = null
    params.requiredAmount = null
  }
  if (params.type === 'REQUIRED_PRICE_REDUCE') {
    // 满减券
    params.discount = null
  }
  if (params.type === 'REQUIRED_PRICE_DISCOUNT') {
    // 满折券
    params.amount = null
  }
}

/**
 * 固定时间处理
 */
const dateTimeFormat = () => {
  if (ruleForm.value.effectType === 'PERIOD') {
    ruleForm.value.days = 1
    return
  }
  ruleForm.value.startDate = ''
  ruleForm.value.endDate = ''
}

function paramsFormat() {
  const { name, days, endDate, productType, type, requiredAmount, discount, amount, effectType, startDate, num, limit, productIds } = ruleForm.value
  // 折扣
  return {
    name,
    days,
    endDate,
    productType,
    type,
    requiredAmount: pricePostHaoFormat(requiredAmount).toString(),
    discount,
    amount: pricePostHaoFormat(amount).toString(),
    effectType,
    startDate,
    num,
    limit: num,
    productIds,
  }
}

const handleChangeType = () => {
  setRules(ruleForm.value.type, rules as any, ruleForm.value)
  ruleForm.value.amount = 1
  ruleForm.value.requiredAmount = 1
  ruleForm.value.discount = 1
}

const disabledStartDate = (time: Date) => {
  const isChooseEndDate = !!ruleForm.value.endDate
  return isChooseEndDate
    ? ruleForm.value.endDate < dateUtil.getYMDs(time) || dateUtil.getYMDs(time) < dateUtil.getYMDs(new Date())
    : dateUtil.getYMDs(time) < dateUtil.getYMDs(new Date())
}
const disabledDate = (time: Date) => {
  const isChooseStartDate = !!ruleForm.value.startDate
  return isChooseStartDate ? ruleForm.value.startDate > dateUtil.getYMDs(time) || dateUtil.getYMDs(time) < dateUtil.getYMDs(new Date()) : false
}
</script>

<template>
  <div class="content_container">
    <h1 class="title">基本信息</h1>
    <el-form ref="ruleFormRef" :inline-message="false" :model="ruleForm" :rules="rules" label-width="95px">
      <el-form-item label="优惠券名称" prop="name">
        <el-input v-model.trim="ruleForm.name" maxlength="5" placeholder="请输入优惠券名称" style="width: 551px" />
        <span class="msg">优惠券名称不超过5个字</span>
      </el-form-item>
      <el-form-item label="有效时间" prop="effectType">
        <el-row style="width: 100%">
          <el-radio-group v-model="ruleForm.effectType" class="ml-4">
            <el-radio value="PERIOD">固定时间</el-radio>
            <el-radio value="IMMEDIATELY">领券立即生效</el-radio>
          </el-radio-group>
        </el-row>
        <el-row style="margin-top: 20px">
          <template v-if="ruleForm.effectType === 'PERIOD'">
            <div class="ruleform-date">
              <el-form-item :inline-message="false" prop="startDate">
                <el-date-picker
                  v-model="ruleForm.startDate"
                  :disabled-date="disabledStartDate"
                  format="YYYY/MM/DD"
                  placeholder="请选择开始时间"
                  type="date"
                  value-format="YYYY-MM-DD"
                />
                <span style="margin: 0 10px">至</span>
              </el-form-item>
              <el-form-item :inline-message="false" prop="endDate">
                <el-date-picker
                  v-model="ruleForm.endDate"
                  :disabled-date="disabledDate"
                  format="YYYY/MM/DD"
                  placeholder="请选择结束时间"
                  type="date"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </div>
          </template>
          <el-form-item v-else prop="days">
            <div class="period-validity text">
              领券当日起
              <el-input-number
                v-model.number="ruleForm.days"
                :controls="false"
                :max="99999"
                :min="ruleForm.days ? 1 : 0"
                style="width: 20%; margin: 0 5px"
              />
              天内可用
            </div>
          </el-form-item>
        </el-row>
      </el-form-item>
      <el-form-item label="活动规则">
        <el-table
          :cell-style="{ height: '60px' }"
          :data="[{}]"
          :header-cell-style="{ textAlign: 'center', fontSize: '14px', color: '#606266' }"
          border
          style="width: 90%"
        >
          <el-table-column label="选择优惠券类型" width="170">
            <template #default>
              <el-form-item prop="type">
                <select-couppon-type v-model="ruleForm.type" placeholder="全部类型" @change="handleChangeType" />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="优惠券规则">
            <template #default>
              <div class="coupon-rules">
                <el-form-item v-if="ruleForm.type === 'PRICE_DISCOUNT'" prop="discount">
                  <el-input-number v-model.number="ruleForm.discount" :controls="false" :max="9.9" :min="0.1" :precision="1" style="width: 100px" />
                  <span style="margin-left: 10px">折，无门槛使用</span>
                </el-form-item>
                <el-form-item v-if="ruleForm.type === 'PRICE_REDUCE'" prop="amount">
                  <el-input-number v-model.number="ruleForm.amount" :controls="false" :max="999999" :min="0" style="width: 100px" />
                  <span style="margin-left: 5px">元，无门槛使用</span>
                </el-form-item>
                <template v-if="ruleForm.type === 'REQUIRED_PRICE_DISCOUNT'">
                  <div class="df">
                    <el-form-item label-width="0%" prop="requiredAmount">
                      <span>满</span>
                      <el-input-number
                        v-model.number="ruleForm.requiredAmount"
                        :controls="false"
                        :max="99999"
                        :min="0"
                        style="width: 100px; margin: 0 5px"
                      />
                      <span>元,打</span>
                    </el-form-item>
                    <el-form-item label-width="0%" prop="discount">
                      <el-input-number
                        v-model.number="ruleForm.discount"
                        :controls="false"
                        :max="9.9"
                        :min="0.1"
                        :precision="1"
                        style="width: 100px; margin-left: 5px"
                      />
                      <span style="margin-left: 5px">折</span>
                    </el-form-item>
                  </div>
                </template>
                <template v-if="ruleForm.type === 'REQUIRED_PRICE_REDUCE'">
                  <div class="df">
                    <el-form-item label-width="0%" prop="requiredAmount">
                      <span>满</span>
                      <el-input-number
                        v-model.number="ruleForm.requiredAmount"
                        :controls="false"
                        :max="999999"
                        :min="0"
                        style="width: 100px; margin-left: 5px"
                      />
                    </el-form-item>
                    <el-form-item label-width="0%" prop="amount">
                      <span>元,减</span>
                      <el-input-number
                        v-model.number="ruleForm.amount"
                        :controls="false"
                        :max="999999"
                        :min="0"
                        style="width: 100px; margin-left: 5px"
                      />
                      <span style="margin-left: 5px">元</span>
                    </el-form-item>
                  </div>
                </template>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="每人发送（张）">
            <template #default>
              <el-form-item prop="num">
                <el-input-number v-model.number="ruleForm.num" :controls="false" :max="99999" :min="1" />
              </el-form-item>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
  </div>
  <div class="nav-button">
    <el-button plain round @click="props.properties.toBack && props.properties.toBack()">返回</el-button>
    <el-button round type="primary" @click="handleSubmit">保存</el-button>
  </div>
</template>

<style lang="scss" scoped>
@include b(title) {
  font-size: 14px;
  color: #606266;
  font-weight: 700;
  margin-bottom: 40px;
}

@include b(msg) {
  font-size: 12px;
  margin-left: 12px;
  color: #c4c4c4;
}

@include b(rules) {
  display: flex;
  margin-top: 10px;
  height: 50px;
}

@include b(period-validity) {
  width: 300px;
  display: flex;
}

@include b(text) {
  font-size: 14px;
  color: #333333;
}

@include b(goodsData) {
  border: 1px solid #ccc;
}

@include b(goods-list) {
  width: 90%;
  margin-top: 20px;
  height: 300px;
  border: 1px solid transparent;
  @include e(info) {
    display: flex;
  }
  @include e(goods-list__info-name) {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    padding: 0 16px;
    @include m(name) {
      width: 462px;
      font-size: 14px;
      color: #2e99f3;
      @include utils-ellipsis(1);
    }
    @include m(price) {
      font-size: 14px;
      text-align: LEFT;
      color: #f12f22;
      &::before {
        content: '￥';
        font-size: 12px;
        text-align: LEFT;
        color: #f12f22;
      }
    }
  }
}

@include b(my-header) {
  font-size: 16px;
}

@include b(ruleform-date) {
  width: 100%;
  display: flex;
  align-items: center;
}

@include b(flex) {
  height: 70px;
}

@include b(flex-item) {
  width: 40%;
}

@include b(coupon-rules) {
  height: 70px;
  @include flex;
}

@include b(nav-button) {
  margin-top: auto;
  width: 100%;
  padding: 15px 0;
  display: flex;
  justify-content: center;
  background-color: #fff;
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.05);
}
</style>
