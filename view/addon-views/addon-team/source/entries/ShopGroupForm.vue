<script setup lang="ts">
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import SelectGoodsTable from '../components/select-good-table.vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import DateUtils from '@/utils/date'
import { doPostGroupForm, doGetGroupForm } from '../apis'
import { doGetRetrieveProduct } from '@/apis/good'
import type { ApiGroupForm, ApiGroupMode, FlatGoodRetrieve } from '../index'
import type { ChoosedGoodCallBack } from '@/components/q-choose-goods-popup/types'
import type { RetrieveParam, ApiRetrieveComItemType } from '@/apis/good/model'
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { cloneDeep } from 'lodash-es'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'

type SearchConfig = {
  maxPrice: string
  minPrice: string
  activity: {
    endTime: string
    startTime: string
  }
  keyword: string
  categoryFirstId: string
}

const submitForm = ref<ApiGroupForm>({
  name: '',
  startTime: '',
  endTime: '',
  effectTimeout: 0,
  mode: 'COMMON',
  users: [],
  payTimeout: 0,
  simulate: false,
  huddle: true,
  stackable: {
    vip: false,
    coupon: false,
    full: false,
  },
  products: [],
})
const $route = useRoute()
const $router = useRouter()
const isEdit = !!$route.query.id && !!$route.query.isLookUp
const dateUtils = new DateUtils()
const ruleFormRef = ref<FormInstance>()
const datePickerValue = ref<string[]>([])
const showSelectGoodsPop = ref(false)
const choosedGoods = ref<ApiRetrieveComItemType[]>([])
// 回显商品列表
const flatGoodList = ref<FlatGoodRetrieve[]>([])
const selectGoodsTableRef = ref()
const searchConfig = reactive<SearchConfig>({
  maxPrice: '',
  minPrice: '',
  activity: {
    endTime: '',
    startTime: '',
  },
  keyword: '',
  categoryFirstId: '',
})
const rules = reactive<FormRules>({
  name: [
    {
      required: true,
      message: '请输入活动名称',
      trigger: 'blur',
    },
    {
      min: 1,
      max: 10,
      message: '活动名称字数为1-10',
      trigger: 'blur',
    },
  ],
  startTime: [
    {
      required: true,
      message: '请选择日期',
    },
    {
      trigger: 'blur',
      validator: validateActTime,
    },
  ],
  endTime: [
    {
      required: true,
      message: '请选择日期',
    },
    {
      trigger: 'blur',
      validator: validateActTime,
    },
  ],
  effectTimeout: [
    {
      required: true,
      message: '拼团有效时间必须大于等于15分钟',
    },
    {
      validator: validateEffectTime,
      trigger: 'blur',
    },
  ],
  mode: [
    {
      required: true,
      message: '请选择拼团模式',
      trigger: 'blur',
    },
  ],
  users: [
    {
      validator: validateUsers,
      trigger: 'blur',
    },
  ],
  payTimeout: [
    {
      validator: validatePayTimeout,
      trigger: 'blur',
    },
  ],
  simulate: [
    {
      required: true,
      message: '请选择是否模拟成团',
      trigger: 'blur',
    },
  ],
  huddle: [
    {
      required: true,
      message: '请选择是否凑团',
      trigger: 'blur',
    },
  ],
  preheat: [
    {
      required: true,
      message: '请选择是否预热',
      trigger: 'blur',
    },
  ],
})

// 编辑回显
fillForm()

const handleAddStairs = () => {
  if (submitForm.value.users.length < 3) {
    submitForm.value.users.push(0)
  } else {
    ElMessage.warning('最多添加三个梯队')
  }
}
const handleDelStairs = () => {
  submitForm.value.users.pop()
}
const handleChoosedCallback = (e: ChoosedGoodCallBack) => {
  choosedGoods.value = cloneDeep(e.tempGoods)
}
const handleSubmit = () => {
  // 校验选中商品填写信息
  submitForm.value.effectTimeout = +submitForm.value.effectTimeout >= 1440 ? 1440 : submitForm.value.effectTimeout
  console.log('测试新增')
  ruleFormRef.value?.validate(async (valid) => {
    if (valid) {
      if (selectGoodsTableRef.value.validateProduct()) {
        submitForm.value.products = selectGoodsTableRef.value.getProduct()
        const { code, msg } = await doPostGroupForm(submitForm.value)
        if (code === 200) {
          ElMessage.success('创建成功')
          handleBack()
        } else {
          ElMessage.error(msg ? msg : '创建失败')
        }
      }
    }
  })
}
const handleChangeTime = (e: Date[]) => {
  if (e[0].getTime() >= e[1].getTime()) {
    ElMessage.warning('结束时间大于开始时间')
  }
  submitForm.value.startTime = dateUtils.getYMDHMSs(e[0])
  submitForm.value.endTime = dateUtils.getYMDHMSs(e[1])
  searchConfig.activity.startTime = dateUtils.getYMDHMSs(e[0])
  searchConfig.activity.endTime = dateUtils.getYMDHMSs(e[1])
}
const handleBack = () => {
  $router.push({
    name: 'marketingAppGroup',
    query: {
      flag: true as any,
    },
  })
}
/**
 * 切换拼团模式
 */
const handleChangeMode = (e: ApiGroupMode) => {
  // 切换模式为了table校验表单
  if (e === 'COMMON') {
    submitForm.value.users = [submitForm.value.users[0]]
  }
}
const handleSelectGood = () => {
  if (!submitForm.value.endTime || !submitForm.value.startTime) {
    ElMessage.warning('请先选择时间段')
    return
  }
  showSelectGoodsPop.value = true
}
function validateUsers(rule: any, value: any, callback: any) {
  if (submitForm.value.mode === 'COMMON') {
    submitForm.value.users.length && submitForm.value.users[0] >= 2 ? callback() : callback(new Error('参团人数应大于等于两人'))
  }
  if (submitForm.value.mode === 'STAIRS') {
    if (submitForm.value.users.length) {
      const userNumLessTwo = submitForm.value.users.find((item) => item < 2)
      if (userNumLessTwo) {
        callback(new Error('阶梯团人数最低为2人'))
      }
      if (!isIncreasingValues(submitForm.value.users)) {
        callback(new Error('阶梯团人数应为递增人数'))
      }
      callback()
    } else {
      callback(new Error('请至少添加一项阶梯团人数'))
    }
  }
}
function validateEffectTime(rule: any, value: any, callback: any) {
  value >= 15 ? callback() : callback(new Error('拼团有效时间必须大于等于15分钟'))
}
function validatePayTimeout(rule: any, value: any, callback: any) {
  value >= 3 && value <= 369 ? callback() : callback(new Error('订单关闭时间3-360分钟'))
}
function isIncreasingValues(arr: number[]) {
  const isIncreasing = arr.every((current, index) => {
    if (index === 0) return true // 第一个元素总是比前一个元素大
    return +current > +arr[index - 1]
  })
  return isIncreasing
}
function validateActTime(rule: any, value: any, callback: any) {
  if (new Date(submitForm.value.startTime).getTime() >= new Date(submitForm.value.endTime).getTime()) {
    callback(new Error('开始时间应小于结束时间'))
  } else {
    callback()
  }
}
async function fillForm() {
  const id = $route.query.id as string
  if (id) {
    const { data } = await doGetGroupForm(id)
    submitForm.value = data
    submitForm.value.payTimeout = +data.payTimeout
    datePickerValue.value = [data.startTime, data.endTime]
    fillTable(data)
  }
}
async function fillTable(data: ApiGroupForm) {
  const productId = data.products.map((item) => item.productId)
  const { code, data: productData } = await doGetRetrieveProduct({ productId })
  if (code !== 200) return ElMessage.error('获取活动商品信息失败')
  let tempArr: FlatGoodRetrieve[] = []
  data.products.forEach((item) => {
    const findProduct = productData.list.find((it) => it.productId === item.productId)
    if (findProduct) {
      tempArr = [
        ...tempArr,
        ...item.skus.map((it, index) => ({
          productName: findProduct.productName,
          productPic: findProduct.pic,
          productId: findProduct.productId,
          skuItem: {
            productId: findProduct.productId,
            skuId: it.skuId,
            skuName: findProduct.specs[index],
            skuPrice: findProduct.salePrices[index],
            skuStock: findProduct.stocks[index],
            stockType: findProduct.stockTypes[index],
          },
          rowTag: 0,
          stock: it.stock,
          prices: it.prices,
          isJoin: true,
        })),
      ]
    }
  })

  flatGoodList.value = tempArr
}

function filterDate(date: Date) {
  const currentDate = dateUtils.getYMD(new Date())
  const chooseDate = dateUtils.getYMD(date)
  if (currentDate === chooseDate) {
    return false
  }
  return new Date().getTime() > date.getTime()
}

const effectTimeoutFormatter = (value: string) => {
  const num = Number(`${value}`.replace(/[^\d]/g, ''))
  return num < 1440 ? num : 1440
}
</script>

<template>
  <div class="groupForm">
    <div class="groupForm__title">{{ isEdit ? '基本信息' : '新增拼团' }}</div>
    <el-form ref="ruleFormRef" :model="submitForm" label-width="110" label-position="right" :rules="rules" :disabled="isEdit">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model="submitForm.name" placeholder="限10字" style="width: 550px" maxlength="10" />
      </el-form-item>
      <el-form-item label="活动时间" prop="startTime">
        <div>
          <el-date-picker
            v-model="datePickerValue"
            style="width: 550px"
            type="datetimerange"
            range-separator="-"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            :disabled-date="filterDate"
            @change="handleChangeTime"
          />
        </div>
      </el-form-item>
      <el-form-item label="拼团有效期" prop="effectTimeout" required>
        <div>
          <el-input v-model="submitForm.effectTimeout" :formatter="(value:string) => effectTimeoutFormatter(value)" style="width: 550px">
            <template #append> 分钟 </template>
          </el-input>
          <p class="tips">用户发起拼团后开始计时，需在设置时间内邀请到规定好友人数参团，超过时效时间，则系统判定拼团失败，自动发起退款</p>
        </div>
      </el-form-item>
      <el-form-item label="拼团模式" prop="mode">
        <el-radio-group v-model="submitForm.mode" @change="handleChangeMode">
          <el-radio value="COMMON">普通拼团</el-radio>
          <el-radio value="STAIRS">阶梯拼团</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="参团人数" prop="users" required>
        <el-input
          v-if="submitForm.mode === 'COMMON'"
          v-model="submitForm.users[0]"
          :formatter="(value:string) => Number(`${value}`.replace(/[^\d]/g, ''))"
          :parser="(value:string) => `${Number(value)>=100?100:value}`"
          style="width: 550px"
          :max="100"
        >
          <template #append> 人 </template>
        </el-input>
        <div v-else>
          <el-button link type="primary" @click="handleAddStairs">添加拼团阶梯</el-button>
          <div v-for="(item, index) in submitForm.users" :key="index" class="groupForm__stairs">
            <span class="groupForm__stairs--title">第{{ index + 1 }}阶段人数</span>
            <el-input
              v-model="submitForm.users[index]"
              class="groupForm__stairs--input"
              style="width: 450px"
              :formatter="(value:string) => Number(`${value}`.replace(/[^\d]/g, ''))"
              :parser="(value:string) => `${Number(value)>=100?100:value}`"
              :max="100"
            >
              <template #append> 人 </template></el-input
            >
            <el-button v-if="index > 0" type="primary" link @click="handleDelStairs">删除</el-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="订单关闭时间" prop="payTimeout">
        <el-input-number v-model="submitForm.payTimeout" :controls="false" :max="360" :min="3" />
        <span class="tips" style="margin-left: 8px">商品按下单减库存，请设置未付款订单自动取消时间及时释放库存，可输入3-360分钟</span>
      </el-form-item>
      <el-form-item label="自动成团" prop="simulate">
        <div>
          <el-radio-group v-model="submitForm.simulate">
            <el-radio :value="false">关闭</el-radio>
            <el-radio :value="true">开启</el-radio>
          </el-radio-group>
          <div class="tips">开启后拼团有效时间内人数未满，系统将以“虚拟用户”凑满人数提高成团率。</div>
        </div>
      </el-form-item>
      <el-form-item label="优惠叠加">
        <el-checkbox v-model="submitForm.stackable.vip" label="会员价" />
        <el-checkbox v-model="submitForm.stackable.coupon" label="优惠券" />
        <el-checkbox v-model="submitForm.stackable.full" label="满减" />
        <span class="tips" style="margin-left: 8px">
          优惠叠加可能导致实付金额为 <i style="color: red; font-weight: 700; font-style: unset">0</i> (实付金额= 活动价 - 会员优惠 - 优惠券优惠 -
          满减优惠)
        </span>
      </el-form-item>
      <el-form-item label="适用商品">
        <el-button type="primary" link @click="handleSelectGood">选择商品</el-button>
      </el-form-item>
    </el-form>
    <QChooseGoodsPopup
      v-model="showSelectGoodsPop"
      v-model:searchParam="searchConfig"
      :searchConsignmentProduct="true"
      :pointGoodsList="choosedGoods"
      @onConfirm="handleChoosedCallback"
    />
    <SelectGoodsTable
      ref="selectGoodsTableRef"
      :mode="submitForm.mode"
      :users="submitForm.users"
      :productList="choosedGoods"
      :isEdit="isEdit"
      :flatGoodList="flatGoodList"
    />
  </div>

  <div class="groupForm__btn btns">
    <el-button round @click="handleBack"> {{ isEdit ? '返回' : '取消' }}</el-button>
    <QSafeBtn v-if="!isEdit" type="primary" round @click="handleSubmit">确定</QSafeBtn>
  </div>
</template>

<style lang="scss" scoped>
@include b(groupForm) {
  overflow-y: auto;
  padding: 16px;
  @include e(title) {
    font-size: 14px;
    color: #323233;
    font-weight: bold;
    margin-bottom: 30px;
    text-align: center;
  }
  @include e(stairs) {
    margin-bottom: 16px;
    @include m(title) {
      font-size: 12px;
      color: #333;
      font-weight: bold;
    }
    @include m(input) {
      margin: 0 7px;
    }
  }
  @include e(btn) {
    height: 60px;
    @include flex;
  }
}
@include b(tips) {
  font-size: 12px;
  color: #c4c4c4;
}
@include b(btns) {
  margin-top: auto;
  width: 100%;
  padding: 15px 0px;
  box-shadow: 0 0px 10px 0px #d5d5d5;
  background-color: white;
}
</style>
