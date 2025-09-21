<script lang="ts" setup>
import { ref, onMounted, reactive, watch, nextTick } from 'vue'
import QMapcity from '@/components/q-mapcity/q-mapcity.vue'
//引入获取商店详情的api 需求参数shopid
//引入shopstore
import { useShopInfoStore } from '@/store/modules/shopInfo'
//引入element-plus
import { ElMessage } from 'element-plus'
//引入上传表单的api 和 获取同城配送详情的api
import { saveNewIntraCity, getIntraCityInfo, doGetShopInfowhile, doGetShopUuptStatus } from '../apis'
import type { FormInstance, FormRules } from 'element-plus'
import QInputNumber from '@/components/q-input-number/q-input-number.vue'
import { BillMethodType, IntraCityDistributionConfig } from '../types/types'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import useConvert from '@/composables/useConvert'
import QIcon from '@/components/q-icon/q-icon.vue'

const { mulTenThousand, divTenThousand } = useConvert()
//商家信息
const shopInfoStore = useShopInfoStore()
//存储商家位置信息
const shopInfoData = ref<{ address: string; location: string[] }>({
  address: '',
  location: [],
})

const freeLimit = ref(false)
//配送范围
const ruleForm = ref<IntraCityDistributionConfig>({
  deliveryRange: 20, //配送范围
  description: '', //配送说明
  minLimit: 0, //起送金额
  baseDelivery: 0, //基础配送费用
  billMethod: {
    type: BillMethodType.DISTANCE,
    free: 0, // 包邮距离【km】 | 包邮重量
    step: 0, // 每增加距离【km】 | 每增加重量
    stepPrice: 1, // 增加距离【km】 | 增加重量
  },
  freeLimit: 0, //订单实付金额大于等于【减免全部配送费】
  enableSelf: false,
  enableOpen: false,
  defaultType: void 0,
  warmBox: true,
  location: {
    coordinates: [121.583336, 29.990282],
    type: 'Point',
  },
  address: '',
  icStatus: false,
})
const handleDeliveryPartyChange = () => {
  // @ts-ignore
  rules.defaultType[0].required = ruleForm.value.enableSelf && ruleForm.value.enableOpen
}
const validateDefaultType = (rule: any, value: any, callback: FN) => {
  if (ruleForm.value.enableSelf && ruleForm.value.enableOpen) {
    if (!value) {
      callback(new Error('请选择默认的配送方'))
    } else {
      callback()
    }
  } else {
    callback()
  }
}
const validateDeliveryParty = (rule: any, value: any, callback: FN) => {
  if (!ruleForm.value.enableSelf && !ruleForm.value.enableOpen) {
    callback(new Error('请选择至少一个配送方'))
  } else {
    callback()
  }
}

//表单验证规则
const rules = reactive<FormRules>({
  defaultType: [
    {
      required: false,
      message: '请选择默认配送方',
      trigger: 'change',
    },
    {
      validator: validateDefaultType,
    },
  ],
  deliveryParty: [
    {
      required: true,
      message: '请配置配送方',
      trigger: 'blur',
    },
    {
      validator: validateDeliveryParty,
      trigger: 'blur',
    },
  ],
  deliveryRange: [
    {
      required: true,
      message: '请输入配送范围',
      trigger: 'change',
    },
  ],
  minLimit: [
    {
      required: true,
      message: '请输入起送金额',
      trigger: 'blur',
    },
  ],
  baseDelivery: [
    {
      required: true,
      message: '请输入基础配送费用',
      trigger: 'blur',
    },
  ],
  'billMethod.type': [
    {
      required: true,
    },
  ],
})

onMounted(async () => {
  // 获取店铺 uupt 账号激活状态
  handleGetShopUuptStatus()
  //获取店铺基础新信息
  await handleGetShopInfo()
  //获取同城配送详情
  await handleGetintraCityInfo()
})
const uuptChooseDisabled = ref(true)
const handleGetShopUuptStatus = async () => {
  const { data } = await doGetShopUuptStatus()
  uuptChooseDisabled.value = !data?.activated
}

/**
 * 获取店铺详情
 * @returns {*}
 */
const handleGetShopInfo = async () => {
  const shopId = shopInfoStore.shopInfo.shopId
  const res = await doGetShopInfowhile(shopId as string)
  if (res.code === 200) {
    shopInfoData.value.address = res.data.address
    shopInfoData.value.location = res.data.location.coordinates
  }
}

/**
 * 计算配送范围 算数回调
 */
const handleCount = (e: number) => {
  //限定配送范围 1-50
  if (e < 1) {
    ElMessage.warning('配送范围不能小于1km')
    return
  }
  if (e > 50) {
    ElMessage.warning('限50km(含)以内')
    return
  }
}

/**
 * 保存表单
 */
const ruleFormRef = ref<FormInstance>()
const handleOnSubmit = async (formEl: FormInstance | undefined) => {
  //表单效验
  if (!formEl) return
  await formEl.validate(async (valid: any, fields: any) => {
    if (valid) {
      const copyRuleForm: IntraCityDistributionConfig = JSON.parse(JSON.stringify(ruleForm.value))
      // 价格乘以10000
      copyRuleForm.billMethod!.stepPrice = mulTenThousand(copyRuleForm.billMethod!.stepPrice).toNumber()
      copyRuleForm.freeLimit = mulTenThousand(copyRuleForm.freeLimit).toNumber()
      copyRuleForm.minLimit = mulTenThousand(copyRuleForm.minLimit).toNumber()
      copyRuleForm.baseDelivery = mulTenThousand(copyRuleForm.baseDelivery).toNumber()
      if (!freeLimit.value) {
        // 配送费减免处理
        Reflect.deleteProperty(copyRuleForm, 'freeLimit')
      }
      // 判断是编辑还是新增
      await handleSave(copyRuleForm)
      handleGetintraCityInfo()
    } else {
      console.log('error submit!', fields)
      Object.keys(fields).forEach((key) => {
        ElMessage.error(fields[key][0].message)
      })
    }
  })
}
/**
 * 发送保存请求
 */
const handleSave = async (res: any) => {
  const data = await saveNewIntraCity(res)
  if (data.code === 200) return ElMessage.success('保存成功')
  ElMessage.error(res.msg || '保存失败')
}
/**
 * 进入页面获取同城配送信息
 */
const handleGetintraCityInfo = async () => {
  const res = await getIntraCityInfo()
  if (res.code === 200) {
    if (res.data.deliveryRange === void 0) {
      res.data.deliveryRange = 20
    }
    if (!res.data.billMethod) {
      res.data.billMethod = {
        type: BillMethodType.DISTANCE,
        free: 0, // 包邮距离【km】 | 包邮重量
        step: 0, // 每增加距离【km】 | 每增加重量
        stepPrice: 1, // 增加距离【km】 | 增加重量
      }
    }
    freeLimit.value = !!res.data.freeLimit
    res.data.billMethod!.stepPrice = divTenThousand(res.data.billMethod!.stepPrice).toNumber()
    res.data.freeLimit = divTenThousand(res.data.freeLimit).toNumber()
    res.data.minLimit = divTenThousand(res.data.minLimit).toNumber()
    res.data.baseDelivery = divTenThousand(res.data.baseDelivery).toNumber()
    ruleForm.value = { ...ruleForm.value, ...res.data }
  }
}
const options = [
  {
    value: 'SELF',
    label: '商家自行配送',
  },
  {
    value: 'UUPT',
    label: 'UU跑腿配送',
  },
]

const toolTipsMap: Record<'intraCity' | 'deliveryFee' | string, string[]> = {
  // 同城配送
  intraCity: [
    `1. 谨慎关闭同城配送，关闭后同城配送商品无法提交订单`,
    `2. 商家自行配送的配送员请在【权限设置--管理员列表】中添加用户，已添加的用户可登录商家移动端配送同城订单`,
    `3. 如需开启第三方配送，请在【第三方配送】中授权开通配送服务`,
  ],
  // 配送费用
  deliveryFee: [
    `1. 起送金额是指订单金额大于等于该数值时才能提交同城配送订单`,
    `2. 配送计费可按距离或计重收费，选择计重收费请确保商品重量已设置`,
    `距离收费说明：`,
    `     1. 配送费 = 基础配送费 + 超距收费 （小于配送费减免金额）`,
    `     2. 以店铺定位地址为中心，因考虑实际送货路况，配送费计算按骑行距离，非地图直线距离`,
    `     3. 不足 1 km 按1 km计算`,
    `计重收费说明：`,
    `     1. 配送费 = 基础配送费 + 续重收费 （小于配送费减免金额）`,
    `     2. 计重收费需设置商品重量`,
    `     3. 不足 1 kg 按1 kg计算`,
  ],
}

const refShopLocation = ref()
watch(
  () => ruleForm.value.icStatus,
  (value) => value && nextTick(() => refShopLocation.value.initMap()),
)
</script>

<template>
  <div class="overh fdc1">
    <div class="main">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" class="ruleForm" label-position="right" label-width="120px" size="default">
        <TitleBar name="同城配送">
          <template #right>
            <el-tooltip effect="dark" placement="top-end">
              <template #content>
                <div v-for="str in toolTipsMap.intraCity" :key="str">{{ str }}</div>
              </template>
              <q-icon class="cup" name="icon-wenhao"></q-icon>
            </el-tooltip>
          </template>
        </TitleBar>
        <el-form-item label="是否开启" prop="icStatus">
          <el-switch v-model="ruleForm.icStatus" />
        </el-form-item>

        <template v-if="ruleForm.icStatus">
          <div class="two_side_container df">
            <div class="left_form_container">
              <el-form-item label="配送方" prop="enableSelf" :rules="rules.deliveryParty">
                <el-checkbox
                  v-model="ruleForm.enableSelf"
                  class="block_checkbox"
                  label="商家自行配送"
                  size="large"
                  @change="handleDeliveryPartyChange"
                />
                <el-checkbox
                  v-model="ruleForm.enableOpen"
                  :disabled="uuptChooseDisabled"
                  class="block_checkbox"
                  label="第三方配送"
                  size="large"
                  @change="handleDeliveryPartyChange"
                />
              </el-form-item>
              <el-form-item label="默认配送方" prop="defaultType">
                <el-select v-model="ruleForm.defaultType" placeholder="请选择默认配送方" style="width: 210px">
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                    :disabled="item.value === 'UUPT' && uuptChooseDisabled"
                  />
                </el-select>
              </el-form-item>
              <el-form-item v-if="ruleForm.enableOpen" label="保温箱" prop="warmBox">
                <el-radio-group v-model="ruleForm.warmBox">
                  <el-radio :value="true">需要</el-radio>
                  <el-radio :value="false">不需要</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </div>

          <TitleBar name="配送范围"></TitleBar>
          <el-form-item label="配送范围(km)" prop="deliveryRange">
            <div class="form-control">
              <!-- <div class="form-count" @click="() => handleCount('-')">-</div> -->
              <el-input-number v-model="ruleForm.deliveryRange" size="small" style="width: 110px" :min="1" :max="50" @change="handleCount" />
              <!-- <div class="form-count" @click="() => handleCount('+')">+</div> -->
              <span class="tips_label">以店铺为中心，配送 50 km(含)以内</span>
            </div>
          </el-form-item>
          <el-form-item label="店铺定位地址">
            {{ ruleForm.address }}
          </el-form-item>
          <!-- 地图组件 -->
          <el-form-item>
            <div>
              <!-- 拟合出来的函数 zoom = 14.19 + -1.43 * ln(area) -->
              <q-mapcity
                ref="refShopLocation"
                :zoom="14.19 + (-1.43 * Math.log(ruleForm.deliveryRange!))"
                :area="ruleForm.deliveryRange"
                :coordinates="ruleForm.location.coordinates"
              ></q-mapcity>
            </div>
          </el-form-item>
          <el-form-item label="配送说明" prop="description">
            <el-input
              v-model="ruleForm.description"
              :placeholder="'限100字以内'"
              :rows="4"
              class="ruleForm__desc"
              maxlength="100"
              resize="none"
              type="textarea"
            />
          </el-form-item>

          <TitleBar name="配送费用">
            <template #right>
              <el-tooltip effect="dark" placement="top-end">
                <template #content>
                  <div v-for="str in toolTipsMap.deliveryFee" :key="str">{{ str.replaceAll(' ', '&nbsp;') }}</div>
                </template>
                <q-icon class="cup" name="icon-wenhao"></q-icon>
              </el-tooltip>
            </template>
          </TitleBar>
          <el-form-item label="起送金额(元)" prop="minLimit">
            <el-input v-model.number="ruleForm.minLimit" class="ruleForm__item" maxlength="6" />
          </el-form-item>
          <el-form-item label="基础配送费(元)" prop="baseDelivery">
            <el-input v-model.number="ruleForm.baseDelivery" class="ruleForm__item" maxlength="6" />
          </el-form-item>
          <el-form-item v-if="ruleForm.billMethod" label="计费方式" prop="billMethod.type" class="ruleForm__rates">
            <el-radio-group v-model="ruleForm.billMethod.type">
              <el-radio value="DISTANCE" label="按距离收费" />
              <el-radio value="WEIGHT" label="按重量收费" />
            </el-radio-group>
            <div style="margin-left: 10px">
              <div v-if="ruleForm.billMethod.type === 'DISTANCE'" class="rates">
                <q-input-number v-model="ruleForm.billMethod.free" :controls="false" :min="1" class="ruleForm__item" maxlength="6">
                  <template #append>
                    <p class="ruleForm__item--span">km</p>
                  </template>
                </q-input-number>
                <span class="rates__span">内不额外收费，每超出</span>
                <q-input-number v-model="ruleForm.billMethod.step" :controls="false" :min="1" class="ruleForm__item">
                  <template #append>
                    <p class="ruleForm__item--span">km</p>
                  </template>
                </q-input-number>
                <span class="rates__span">，增加</span>
                <q-input-number v-model="ruleForm.billMethod.stepPrice" :controls="false" :min="1" class="ruleForm__item" maxlength="6">
                  <template #append>
                    <p class="ruleForm__item--span">元</p>
                  </template>
                </q-input-number>
              </div>

              <div v-else class="rates">
                <q-input-number v-model="ruleForm.billMethod.free" :controls="false" class="ruleForm__item" :min="1" maxlength="6">
                  <template #append>
                    <p class="ruleForm__item--span">kg</p>
                  </template>
                </q-input-number>
                <p class="rates__span">内不额外收费，每超出</p>
                <q-input-number v-model="ruleForm.billMethod.step" :controls="false" :min="1" class="ruleForm__item">
                  <template #append>
                    <p class="ruleForm__item--span">kg</p>
                  </template>
                </q-input-number>
                <p class="rates__span">，增加</p>
                <q-input-number v-model="ruleForm.billMethod.stepPrice" :min="1" :controls="false" class="ruleForm__item" maxlength="6">
                  <template #append>
                    <p class="ruleForm__item--span">元</p>
                  </template>
                </q-input-number>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="配送费减免" prop="freeLimit">
            <div class="fdc">
              <el-switch v-model="freeLimit" style="width: 48px" inline-prompt active-text="开启" inactive-text="关闭" />
              <el-form-item v-if="freeLimit" prop="freeLimit">
                <span class="rates__span">订单实付金额大于等于</span>
                <q-input-number v-model="ruleForm.freeLimit" :controls="false" :max="999999" :min="0" class="ruleForm__item">
                  <template #append>
                    <span class="ruleForm__item--span">元</span>
                  </template>
                </q-input-number>
                <span class="rates__span">时，</span>
                <span class="red-text">减免全部配送费</span>
              </el-form-item>
              <template v-else>
                <div style="height: 50px"></div>
              </template>
            </div>
          </el-form-item>
        </template>
      </el-form>
    </div>
    <div class="save">
      <el-button class="save__btn" type="primary" @click="() => handleOnSubmit(ruleFormRef)">保存</el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(main) {
  padding: 0 16px;
  overflow-y: auto;
  .two_side_container {
    .left_form_container {
      width: 350px;
    }
    .right_form_container {
      margin-left: auto;
      .tips_text {
        color: rgb(102, 102, 102);
        font-size: 14px;
        font-weight: 500;
        margin-top: 34px;
      }
    }
  }
}
@include b(save) {
  background-color: #ffffff;
  height: 60px;
  box-shadow: 0px 0px 2px 0px rgba(0, 0, 0, 0.07);
  display: flex;
  justify-content: center;
  align-items: center;
  @include e(btn) {
    height: 32px;
    font-size: 14px;
    color: #ffffff;
  }
}

@include b(red-text) {
  color: red;
  font-size: 14px;
  font-weight: 700;
}

@include b(form-area) {
  width: 50px;
  height: 20px;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  text-align: center;
  line-height: 20px;
}

@include b(form-control) {
  display: flex;
  align-items: center;
}

@include b(form-count) {
  //转化为rem
  width: 20px;
  height: 20px;
  border: 1px solid #e2e2e2;
  border-radius: 4px;
  text-align: center;
  line-height: 20px;
  margin: 0 5px;
  cursor: pointer;
  user-select: none;
}

@include b(ruleForm) {
  @include e(name) {
    width: 466px;
  }
  @include e(desc) {
    width: 667px;
  }
  @include e(center) {
    width: 1007px;
    height: 31px;
    line-height: 31px;
    background: #fcf8f8;
    font-size: 13px;
    font-family: -apple-system, -apple-system-Bold;
    font-weight: 700;
    text-align: LEFT;
    color: #0c0b0b;
    margin-bottom: 22px;
  }
  @include e(item) {
    width: 141px;
    height: 30px;
    line-height: 30px;
    display: flex;
    padding-right: 0;
    :deep(.el-input__wrapper) {
      padding-right: 0;
    }
    @include m(span) {
      display: block;
      width: 38px;
      line-height: 30px;
      color: #333333;
      background: #f7f7f7;
      border-radius: 0 4px 4px 0;
    }
  }
  @include e(tip) {
    width: 390px;
    height: 20px;
    font-size: 14px;
    color: #d0caca;
    line-height: 20px;
    margin-left: 25px;
  }
}

@include b(rates) {
  display: flex;
  align-items: center;
  justify-content: start;
  margin-left: -10px;
  @include e(span) {
    padding: 5px;
  }
}
.radio_box {
  // display: flex;
  flex-direction: column;
  > div {
    display: flex;
    align-items: center;
  }
}

@include b(explain) {
  display: flex;
  flex-direction: column;
  padding-left: 100px;
  font-size: 13px;
  color: #bcbbbb;
  line-height: 25px;
  margin-bottom: 20px;
  @include e(span) {
    margin-left: 20px;
  }
}
:deep(.mo-input--number) {
  border: none;
}
:deep(.ruleForm__rates .el-form-item__content) {
  flex-direction: column;
  align-items: start;
}
@include b(block_checkbox) {
  width: 100%;
}

@include b(tips_label) {
  color: #bebebe;
  margin-left: 10px;
}
</style>
