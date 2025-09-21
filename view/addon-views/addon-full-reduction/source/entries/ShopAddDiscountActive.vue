<script lang="ts" setup>
import { ref, reactive, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fullReductionRuleCn, AddDiscountActiveParams, FullReductionRules } from '../index'
import { doPostDiscountActive, doGetApplyDiscountById } from '../apis'
import DateUtil from '@/utils/date'
import Decimal from 'decimal.js'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import useConvert from '@/composables/useConvert'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
import type { ChoosedGoodCallBack } from '@/components/q-choose-goods-popup/types'
import type { FormInstance, FormRules } from 'element-plus'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'

interface PageData {
  form: AddDiscountActiveParams
  isEditDisable: boolean
  rules: FormRules
  fullReductionTime: string[]
  chooseGoodsPopup: boolean
  chooseGoodsList: ChoosedGoodCallBack['tempGoods']
  goodsTotal: number
}
const $router = useRouter()
const $route = useRoute()
const dateUtil = new DateUtil()
const datePickerValue = ref<string[]>([])
const pageData = reactive<PageData>({
  form: {
    id: null,
    name: '',
    time: {
      start: '',
      end: '',
    },
    rules: [],
    productType: 'ALL_PRODUCT',
    productIds: [],
  },
  // 编辑状态用于判定禁用
  isEditDisable: false,
  goodsTotal: 0,
  fullReductionTime: [],
  rules: {
    name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
    'time.start': [{ required: true, message: '请选择开始日期', trigger: ['blur', 'change'] }],
    'time.end': [{ required: true, message: '请选择结束日期', trigger: ['blur', 'change'] }],
    productType: [{ required: true, message: '请输入满减规则', trigger: ['blur', 'change'] }],
    rules: [{ required: true, message: '请添加活动规则', trigger: ['blur', 'change'] }],
  },
  chooseGoodsPopup: false,
  chooseGoodsList: [],
})
const { form, isEditDisable, rules, chooseGoodsPopup, chooseGoodsList, goodsTotal } = toRefs<PageData>(pageData)
const ruleFormRef = ref<FormInstance>()
const { mulTenThousand, divTenThousand } = useConvert()

const handleChangeTime = (e: Date[] | null) => {
  if (!e || !e[0] || !e[1]) {
    return
  }
  if (e[0].getTime() >= e[1].getTime()) {
    ElMessage.warning('结束时间大于开始时间')
  }
  form.value.time.start = dateUtil.getYMDHMSs(e[0])
  form.value.time.end = dateUtil.getYMDHMSs(e[1])
}

initActive()

/**
 * 编辑获取活动信息
 */
async function initActive() {
  const id = $route.query.id as string
  if (!id) return
  form.value.id = id
  const { code, data, msg } = await doGetApplyDiscountById({ id })
  if (code !== 200) {
    ElMessage.error(msg || '获取活动信息失败')
    return
  }
  isEditDisable.value = 'true' === $route.query.isLookUp
  data.rules = paramsFormatRules(data.rules, priceApiFormat)
  datePickerValue.value = [data.time.start, data.time.end]
  chooseGoodsList.value = data.products.map((product: any) => ({
    productId: product.id,
    productName: product.name,
    pic: product.image,
  }))
  delete data.products
  pageData.form = data
}

const handleSubmit = async () => {
  if (isEditDisable.value) {
    // 禁用状态仅查看点击保存返回活动列表
    returnsAListActivities()
    return
  }
  if (!ruleFormRef.value) return
  const isValidate = await ruleFormRef.value.validate()
  if (!isValidate) return
  if (!vilidatefullReductionRules(form.value.rules)) {
    ElMessage.error('满减规则输入有误')
    return
  }
  if (['SPECIFIED_PRODUCT_PARTICIPATE', 'SPECIFIED_PRODUCT_NOT_PARTICIPATE'].includes(form.value.productType) && !chooseGoodsList.value.length) {
    ElMessage.error('请选择商品')
    return
  }
  if (form.value.rules.length === 0) {
    ElMessage.error('请添加活动规则')
    return
  }
  form.value.productIds = chooseGoodsList.value.map((item) => item.productId)
  // 没有直接赋值给 form.fullReductionRules 是为了防止创建失败页面回显问题
  const fullReductionRules = paramsFormatRules(form.value.rules, pricePostHaoFormat)
  const { code, msg } = await doPostDiscountActive({ ...pageData.form, rules: fullReductionRules })
  if (code !== 200) {
    ElMessage.error(msg || '活动创建失败')
    return
  }
  ElMessage.success('活动创建成功')
  reset()
  returnsAListActivities()
}

function returnsAListActivities() {
  $router.push({ name: 'applyDiscountIndex' })
}

function vilidatefullReductionRules(arr: FullReductionRules[]) {
  return arr.every((item) => {
    // 检验规则类型
    const isCheckRule = ['FULL_DISCOUNT', 'FULL_REDUCTION'].includes(item.type as string)
    if (!isCheckRule) return false
    // 检验折扣
    if (item.type === 'FULL_DISCOUNT') {
      return item.conditionAmount && item.discountRatio && item.discountRatio > 0 && item.discountRatio <= 9.9
    }
    // 检验折扣
    return item.conditionAmount && item.discountAmount && item.conditionAmount >= item.discountAmount
  })
}

function paramsFormatRules(arr: FullReductionRules[], priceFormat: (val?: number) => number) {
  // 1. 遍历 fullReductionRules
  return arr.map((item) => {
    // 2. 解构出 type, conditionAmount, discountAmount, discountRatio
    const { type, conditionAmount, discountAmount, discountRatio } = item
    // 3. 判断 type 是否为 FULL_DISCOUNT
    if (item.type === 'FULL_DISCOUNT') {
      // 4. 是, 返回 { type, conditionAmount, discountRatio }

      return { type, conditionAmount: priceFormat(conditionAmount), discountRatio }
    }
    // 5. 判断 type 是否为 FULL_REDUCTION
    if (item.type === 'FULL_REDUCTION') {
      // 6. 是, 返回 { type, conditionAmount, discountAmount }
      return {
        type,
        conditionAmount: priceFormat(conditionAmount),
        discountAmount: priceFormat(discountAmount),
      }
    }
    // 7. 不是, 返回 item
    return item
  })
}

/**
 * 价格转毫上传
 */
function pricePostHaoFormat(price?: number) {
  return price ? mulTenThousand(price).toNumber() : 0
}

/**
 * 接口价格转毫
 */
function priceApiFormat(price?: number) {
  return price ? divTenThousand(price).toNumber() : 0
}

const handleConfirm = (data: ChoosedGoodCallBack) => {
  chooseGoodsList.value = data.tempGoods
  // goodsTotal.value = data.pageConfig.total
}
const handleDelGoods = async (productId: string) => {
  const isValidate = await ElMessageBox.confirm('确定移除该商品?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (isValidate) {
    chooseGoodsList.value = chooseGoodsList.value.filter((item: any) => item.productId !== productId)
  }
}

/**
 * 表单重置
 */
function reset() {
  if (!ruleFormRef.value) return
  ruleFormRef.value.resetFields()
  chooseGoodsList.value = []
}

const handleDelRules = (index: number) => {
  form.value.rules.splice(index, 1)
}
const handleAddRules = async () => {
  form.value.rules.push({ type: null, conditionAmount: 0, discountAmount: 0, discountRatio: 0 })
  if (!ruleFormRef.value) return
  await ruleFormRef.value.validate()
}
const disabledStartDate = (time: Date) => {
  const YMDs = dateUtil.getYMDs(time)
  const currentYMDs = dateUtil.getYMDs(new Date())
  return (
    new Decimal(new Date(YMDs).getTime()).lessThan(new Date(currentYMDs).getTime()) ||
    new Decimal(new Date(time).getTime()).greaterThanOrEqualTo(new Date(diffMonth(6)).getTime())
  )
}

// 前 / 后 一个月 根据传入正负值
function diffMonth(n: number) {
  let dt = new Date()
  dt.setMonth(dt.getMonth() + Number(n))
  return dt.toLocaleString().replace(/\//g, '-')
}

//商品选择
function handleProductTypeChange() {
  chooseGoodsList.value = []
}
</script>

<template>
  <div class="add">
    <h1 class="title">{{ isEditDisable ? '基本信息' : '新增满减' }}</h1>
    <el-form ref="ruleFormRef" :inline-message="false" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model.trim="form.name" :disabled="isEditDisable" maxlength="10" placeholder="请输入活动名称" style="width: 551px" />
        <span class="msg">活动名称不超过10个字</span>
      </el-form-item>
      <el-form-item label="活动时间" required>
        <div class="ruleform-date">
          <el-date-picker
            v-model="datePickerValue"
            type="datetimerange"
            range-separator="-"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            :disabled="isEditDisable"
            :disabled-date="disabledStartDate"
            @change="handleChangeTime"
          />
        </div>
      </el-form-item>
      <el-form-item label-width="80px" style="margin-bottom: 0">
        <el-link :disabled="isEditDisable || form.rules.length > 9" :underline="false" type="primary" @click="handleAddRules">添加规则 </el-link>
        <span class="msg">(限定10条)</span>
      </el-form-item>
      <el-form-item label="活动规则" prop="rules">
        <el-table
          :cell-style="{ height: '60px' }"
          :data="form.rules"
          :header-cell-style="{ textAlign: 'center', fontSize: '14px', color: '#606266' }"
          border
          style="width: 90%"
        >
          <el-table-column label="满减条件" width="170">
            <template #default="{ row }: { row: FullReductionRules }">
              <el-form-item prop="rules">
                <el-select v-model="row.type" :disabled="isEditDisable" placeholder="全部类型">
                  <el-option v-for="item in fullReductionRuleCn" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column label="满减规则" width="300">
            <template #default="{ row }: { row: FullReductionRules }">
              <template v-if="row.type === 'FULL_DISCOUNT'">
                <div class="flex" style="width: 100%; display: flex">
                  <el-form-item label-width="0%" prop="conditionAmount">
                    <span>满</span>
                    <el-input-number
                      v-model.number="row.conditionAmount"
                      :controls="false"
                      :disabled="isEditDisable"
                      :max="99999"
                      :min="1"
                      :precision="2"
                      style="width: 60%; margin: 0 5px"
                    />
                    <span>元,打</span>
                  </el-form-item>
                  <el-form-item label-width="0%" prop="discountRatio">
                    <el-input-number
                      v-model.number="row.discountRatio"
                      :controls="false"
                      :disabled="isEditDisable"
                      :max="9.9"
                      :min="0.1"
                      :precision="1"
                      style="width: 80%"
                    />
                    <span style="margin-left: 5px">折</span>
                  </el-form-item>
                </div>
              </template>
              <template v-if="row.type === 'FULL_REDUCTION'">
                <div class="flex" style="width: 100%; display: flex">
                  <el-form-item :label-width="0" prop="requiredAmount">
                    <span>满</span>
                    <el-input-number
                      v-model.number="row.conditionAmount"
                      :controls="false"
                      :disabled="isEditDisable"
                      :max="999999"
                      :min="1"
                      :precision="2"
                      style="width: 90px; margin: 0 5px"
                    />
                  </el-form-item>
                  <el-form-item :label-width="0" prop="discountAmount">
                    <span>元,减</span>
                    <el-input-number
                      v-model.number="row.discountAmount"
                      :controls="false"
                      :disabled="isEditDisable"
                      :max="row.conditionAmount"
                      :min="1"
                      :precision="2"
                      style="width: 90px; margin: 0 5px"
                    />
                    <span style="margin-left: 5px">元</span>
                  </el-form-item>
                </div>
              </template>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" fixed="right">
            <template #default="{ $index }">
              <el-link :disabled="isEditDisable" :underline="false" type="danger" @click="handleDelRules($index)">删除 </el-link>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item label="商品选择" prop="productType">
        <el-radio-group v-model="form.productType" :disabled="isEditDisable" @change="handleProductTypeChange">
          <el-radio value="ALL_PRODUCT">全部商品参与</el-radio>
          <el-radio value="SPECIFIED_PRODUCT_PARTICIPATE">指定商品参与</el-radio>
          <el-radio value="SPECIFIED_PRODUCT_NOT_PARTICIPATE">指定商品不参与</el-radio>
        </el-radio-group>
        <div v-if="form.productType !== 'ALL_PRODUCT'" :class="chooseGoodsList.length && 'goodsData'" class="goods-list">
          <el-table
            :data="chooseGoodsList"
            :header-cell-style="{ fontSize: '14px', color: '#606266', background: '#f2f2f2', height: '54px', fontWeight: 'normal' }"
            height="500px"
            style="width: 100%"
          >
            <el-table-column :label="`商品信息${chooseGoodsList.length ? `(已选择${chooseGoodsList.length}款商品)` : ''}`">
              <template #default="{ row }: { row: ApiRetrieveComItemType }">
                <div class="goods-list__info">
                  <el-image :preview-src-list="[row.pic]" :preview-teleported="true" :src="row.pic" fit="" style="width: 60px; height: 60px" />
                  <div class="goods-list__goods-list__info-name">
                    <div class="goods-list__goods-list__info-name--name">{{ row.productName }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column width="110" fixed="right">
              <template #header>
                <el-button :disabled="isEditDisable" plain round size="small" type="primary" @click="chooseGoodsPopup = true"> 添加商品 </el-button>
              </template>
              <template #default="{ row }: { row: ApiRetrieveComItemType }">
                <el-link :disabled="isEditDisable" :underline="false" type="primary" @click="handleDelGoods(row.productId)"> 删除 </el-link>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form-item>
    </el-form>

    <QChooseGoodsPopup v-model="chooseGoodsPopup" :pointGoodsList="chooseGoodsList" :searchConsignmentProduct="true" @onConfirm="handleConfirm" />
  </div>
  <div class="commodityForm__tool">
    <el-button plain round @click="$router.back()">{{ !isEditDisable ? '取消' : '返回' }}</el-button>
    <QSafeBtn v-if="!isEditDisable" round type="primary" @click="handleSubmit">确定</QSafeBtn>
  </div>
</template>

<style lang="scss" scoped>
@include b(add) {
  height: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow-y: scroll;
  padding-left: 16px;
  padding-right: 16px;
  padding-top: 30px;
}

@include b(title) {
  text-align: center;
  font-size: 14px;
  color: #323233;
  font-weight: 700;
  margin-bottom: 30px;
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
  // height: 300px;
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
  width: 550px;
  display: flex;
  align-items: center;
}

@include b(flex) {
  margin-top: 10px;
  height: 50px;
}

@include b(flex-item) {
  width: 40%;
}

@include b(coupon-rules) {
  height: 60px;
  @include flex;
}

@include b(nav-button) {
  position: fixed;
  left: 50%;
  bottom: 30px;
}

@include b(commodityForm) {
  box-sizing: border-box;
  padding-bottom: 62px;
  @include e(tool) {
    margin-top: auto;
    @include flex;
    position: sticky;
    bottom: 0;
    padding: 15px 0px;
    display: flex;
    justify-content: center;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    z-index: 100;
  }
}
</style>
