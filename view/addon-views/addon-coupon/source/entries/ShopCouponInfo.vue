<script lang="ts" setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SelectCoupponType from '../components/select-couppon-type.vue'
import ChooseGoodsPopup from '../components/choose-goods-popup.vue'
import DateUtil from '@/utils/date'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { doAddonCoupon, doGetCouponInfoById, doPostonCouponEditByCouponId, doPostCouponWorkingEditByCouponId } from '../apis'
import { setRules } from '../setRules'
import useConvert from '@/composables/useConvert'
import { EffectType, pricePostHaoFormat, dataFormat, couponStatusComputed, ApiGoodsRetrieve } from '../index'
import { doGetRetrieveCommodity } from '@/apis/decoration'
import { cloneDeep } from 'lodash-es'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
import DateUtils from '@/utils/date'

const $router = useRouter()
const $route = useRoute()
const dateUtil = new DateUtil()
// 禁用
const isEditDisable = ref(false)
// 编辑状态用于判定禁用
type Status = '' | '违规下架' | '未开始' | '进行中' | '已结束'
const editStatus = ref<Status>('')
const goodsData = ref([])
const editEndTime = ref('')
const ruleFormRef = ref<FormInstance>()
const chooseGoodsPopupShow = ref(false)
const datePickerValue = ref<string[]>([])
const chooseGoodsPopupRef = ref()
const ruleForm = ref({
  name: '',
  days: 1,
  endDate: '',
  productType: 'SHOP_ALL',
  type: 'PRICE_DISCOUNT',
  requiredAmount: 1,
  discount: 1,
  amount: 1,
  effectType: EffectType.PERIOD,
  startDate: '',
  num: 1,
  limit: 1,
  shopId: '',
  productIds: [],
  createTime: '',
  usedCount: '',
})
const $useConvert = useConvert()
const rules: FormRules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  effectType: [{ required: true, message: '请选择有效时间', trigger: ['blur', 'change'] }],
  type: [{ required: true, message: '请选择优惠券类型', trigger: ['blur', 'change'] }],
  discount: [{ required: true, message: '请输入优惠券规则', trigger: 'blur' }],
  num: [{ required: true, message: '请输入发行量', trigger: 'blur' }],
  limit: [{ required: true, message: '请输入每人发送数量', trigger: 'blur' }],
  requiredAmount: [{ required: true, message: '请输入优惠券规则', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入优惠券规则', trigger: 'blur' }],
  productType: [{ required: true, message: '请选择优惠券类型', trigger: ['blur', 'change'] }],
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
// 是否是列表[查看]点击进入的
const isLookUp = ref(false)
const dateUtils = new DateUtils()

const handleChangeTime = (e: Date[] | null) => {
  if (!e || !e[0] || !e[1]) {
    return
  }
  if (e[0].getTime() >= e[1].getTime()) {
    ElMessage.warning('结束时间大于开始时间')
  }
  ruleForm.value.startDate = dateUtils.getYMDHMSs(e[0])
  ruleForm.value.endDate = dateUtils.getYMDHMSs(e[1])
}

initIsEdit()
watch(
  () => $route,
  async (val) => {
    isLookUp.value = !!val.query.isLookUp
    if (!val.query.id || !val.query.shopId) {
      return
    }
    const { code, data } = await doGetCouponInfoById(val.query.shopId.toString(), val.query.id.toString())
    if (code !== 200) {
      ElMessage.error('获取优惠券信息失败')
      return
    }
    //@ts-ignore
    ruleForm.value = dataFormat(data)
    editStatus.value = couponStatusComputed(data)
    editEndTime.value = data.endDate
    isEditDisable.value = true
    datePickerValue.value = [data.startDate, data.endDate]
    // 获取关联商品
    if (data.productIds?.length) {
      const { code: codeStatus, data: res } = await doGetRetrieveCommodity({
        productId: data.productIds,
        searchTotalStockGtZero: true,
      })
      if (codeStatus !== 200) {
        ElMessage.error('获取商品信息失败')
        return
      }
      goodsData.value = res.list.map((item: ApiGoodsRetrieve) => ({ ...item, isCheck: true }))
    }
  },
  {
    immediate: true,
  },
)
const IsEdit = ref(false)
function initIsEdit() {
  console.log('$route.query.isEdit', $route.query.isEdit)
  if ($route.query.isEdit) {
    IsEdit.value = $route.query.isEdit === 'true'
  }
}
/**
 * 提交
 */
const handleSubmit = async () => {
  if (!ruleFormRef.value) return
  const { num, limit } = ruleForm.value
  if (!num || !limit) {
    ElMessage.error('发行量或每人发送数量只能是正整数和数字')
    return
  }
  try {
    const isValidate = await ruleFormRef.value.validate()
    if (!isValidate) return
    if (['ASSIGNED', 'ASSIGNED_NOT'].includes(ruleForm.value.productType) && !goodsData.value.length) {
      goodsData.value.length
      ElMessage.error('请选择指定商品')
      return
    }
    dateTimeFormat()
    const params = paramsFormat()
    // 如果是全部商品无需传 productIds
    if (ruleForm.value.productType === 'SHOP_ALL') {
      params.productIds = null
    }
    formatQuery(params)
    const editCouponId = ruleForm.value.id
    const { code } = await (editCouponId
      ? editStatus.value === '进行中'
        ? doPostCouponWorkingEditByCouponId(editCouponId, {
            name: params.name,
            days: params.days,
            endDate: params.endDate,
            productType: params.productType,
            productIds: params.productIds,
          })
        : doPostonCouponEditByCouponId(editCouponId, params)
      : doAddonCoupon(params))
    if (code !== 200) {
      ElMessage.error('保存失败')
      return
    }
    ElMessage.success('保存成功')
    reset()
    $router.push({ name: 'coupons' })
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

function formatQuery(params) {
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
  const { name, days, endDate, productType, type, requiredAmount, discount, amount, effectType, startDate, num, limit, shopId } = ruleForm.value
  // 折扣
  const params = {
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
    shopId,
    limit,
    productIds: goodsData.value.length ? goodsData.value.map((item: any) => item.productId) : null,
  }
  return params
}

const handleClose = () => {
  chooseGoodsPopupShow.value = false
}
const handleConfirm = () => {
  // 拷贝
  goodsData.value = cloneDeep(chooseGoodsPopupRef.value.tempGoods)
  chooseGoodsPopupShow.value = false
}
const handleDelGoods = async (productId: string) => {
  const isValidate = await ElMessageBox.confirm('确定移除该商品?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  if (isValidate) {
    goodsData.value = goodsData.value.filter((item: any) => item.productId !== productId)
  }
}

/**
 * 表单重置
 */
function reset() {
  if (!ruleFormRef.value) return
  ruleFormRef.value.resetFields()
  ruleForm.value = {
    name: '',
    days: 1,
    endDate: '',
    productType: 'SHOP_ALL',
    type: 'PRICE_DISCOUNT',
    requiredAmount: 1,
    discount: 1,
    amount: 1,
    effectType: EffectType.PERIOD,
    startDate: '',
    num: 1,
    shopId: '',
    limit: 1,
    productIds: [],
    createTime: '',
    usedCount: '',
  }
  goodsData.value = []
}

const handleChangeType = () => {
  setRules(ruleForm.value.type, rules, ruleForm.value)
  ruleForm.value.amount = 1
  ruleForm.value.requiredAmount = 1
  ruleForm.value.discount = 1
}

const disabledStartDate = (time: Date) => {
  if (!isEditDisable.value) {
    const isChooseEndDate = !!ruleForm.value.endDate
    return isChooseEndDate
      ? ruleForm.value.endDate < dateUtil.getYMDs(time) || dateUtil.getYMDs(time) < dateUtil.getYMDs(new Date())
      : dateUtil.getYMDs(time) < dateUtil.getYMDs(new Date())
  }
  return editEndTime.value < dateUtil.getYMDs(time)
}
const disabledDate = (time: Date) => {
  if (!isEditDisable.value) {
    const isChooseStartDate = !!ruleForm.value.startDate
    return isChooseStartDate ? ruleForm.value.startDate > dateUtil.getYMDs(time) || dateUtil.getYMDs(time) < dateUtil.getYMDs(new Date()) : false
  }
  return editEndTime.value > dateUtil.getYMDs(time)
}

const isDisable = (excludeStatus: Array<Status>) => {
  if (isLookUp.value) {
    return true
  }
  return isEditDisable.value && !excludeStatus.includes(editStatus.value)
}
</script>

<template>
  <div class="q_plugin_container overh">
    <div class="coupon-info">
      <h1 class="title">{{ isLookUp ? '基本信息' : '新增优惠券' }}</h1>
      <el-form ref="ruleFormRef" :inline-message="false" :model="ruleForm" :rules="rules" label-width="auto" class="coupon-info__form">
        <el-form-item label="优惠券名称" prop="name">
          <el-input
            v-model.trim="ruleForm.name"
            :disabled="isDisable(['未开始', '进行中'])"
            maxlength="10"
            placeholder="请输入优惠券名称"
            style="width: 551px"
          />
          <span class="msg">优惠券名称不超过10个字</span>
        </el-form-item>
        <el-form-item label="有效时间" prop="effectType">
          <el-row style="width: 100%">
            <el-radio-group v-model="ruleForm.effectType" :disabled="isDisable(['未开始'])" class="ml-4">
              <el-radio value="PERIOD">固定时间</el-radio>
              <el-radio value="IMMEDIATELY">领券立即生效</el-radio>
            </el-radio-group>
          </el-row>
          <el-row style="margin-top: 20px">
            <template v-if="ruleForm.effectType === 'PERIOD'">
              <div class="ruleform-date">
                <el-date-picker
                  v-model="datePickerValue"
                  style="width: 550px"
                  type="datetimerange"
                  range-separator="-"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  :disabled="isDisable(['未开始', '进行中'])"
                  :disabled-date="disabledDate"
                  @change="handleChangeTime"
                />
              </div>
            </template>
            <el-form-item v-else prop="days">
              <div class="period-validity text">
                领券当日起
                <el-input-number
                  v-model.number="ruleForm.days"
                  :controls="false"
                  :disabled="isDisable(['未开始', '进行中'])"
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
                  <select-couppon-type v-model="ruleForm.type" :disabled="isDisable(['未开始'])" placeholder="全部类型" @change="handleChangeType" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="优惠券规则" width="350">
              <template #default>
                <div class="coupon-rules">
                  <el-form-item v-if="ruleForm.type === 'PRICE_DISCOUNT'" prop="discount">
                    <el-input-number
                      v-model.number="ruleForm.discount"
                      :controls="false"
                      :disabled="isDisable(['未开始'])"
                      :max="9.9"
                      :min="0.1"
                      :precision="1"
                      style="width: 40%"
                    />
                    <span style="margin-left: 10px">折，无门槛使用</span>
                  </el-form-item>
                  <el-form-item v-if="ruleForm.type === 'PRICE_REDUCE'" prop="amount">
                    <el-input-number
                      v-model.number="ruleForm.amount"
                      :controls="false"
                      :disabled="isDisable(['未开始'])"
                      :max="999999"
                      :min="0"
                      style="width: 40%"
                    />
                    <span style="margin-left: 5px">元，无门槛使用</span>
                  </el-form-item>
                  <template v-if="ruleForm.type === 'REQUIRED_PRICE_DISCOUNT'">
                    <div class="df" style="width: 100%; flex-wrap: nowrap">
                      <el-form-item label-width="0%" prop="requiredAmount">
                        <span>满</span>
                        <el-input-number
                          v-model.number="ruleForm.requiredAmount"
                          :controls="false"
                          :disabled="isDisable(['未开始'])"
                          :max="99999"
                          :min="0"
                          style="width: 120px; margin: 0 5px"
                        />
                        <span>元,打</span>
                      </el-form-item>
                      <el-form-item label-width="0%" prop="discount">
                        <el-input-number
                          v-model.number="ruleForm.discount"
                          :controls="false"
                          :disabled="isDisable(['未开始'])"
                          :max="9.9"
                          :min="0.1"
                          :precision="1"
                          style="width: 60px; margin-left: 5px"
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
                          :disabled="isDisable(['未开始'])"
                          :max="999999"
                          :min="0"
                          style="width: 80%; margin-left: 5px"
                        />
                      </el-form-item>
                      <el-form-item label-width="0%" prop="amount">
                        <span>元,减</span>
                        <el-input-number
                          v-model.number="ruleForm.amount"
                          :controls="false"
                          :disabled="isDisable(['未开始'])"
                          :max="999999"
                          :min="0"
                          style="width: 60%; margin-left: 5px"
                        />
                        <span style="margin-left: 5px">元</span>
                      </el-form-item>
                    </div>
                  </template>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="发行量（张）">
              <template #default>
                <el-form-item prop="num">
                  <el-input-number v-model.number="ruleForm.num" :controls="false" :disabled="isDisable(['未开始'])" :max="99999" :min="1" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="每人发送（张）">
              <template #default>
                <el-form-item prop="limit">
                  <el-input-number v-model.number="ruleForm.limit" :controls="false" :disabled="isDisable(['未开始'])" :max="99999" :min="1" />
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="商品选择" prop="productType">
          <el-radio-group v-model="ruleForm.productType" :disabled="isDisable(['未开始', '进行中'])">
            <el-radio value="SHOP_ALL">全部商品参与</el-radio>
            <el-radio value="ASSIGNED">指定商品参与</el-radio>
            <el-radio value="ASSIGNED_NOT">指定商品不参与</el-radio>
          </el-radio-group>
          <div v-if="ruleForm.productType !== 'SHOP_ALL'" :class="goodsData.length && 'goodsData'" class="goods-list">
            <el-table
              :data="goodsData"
              :header-cell-style="{ fontSize: '14px', color: '#606266', background: '#f2f2f2', height: '54px', fontWeight: 'normal' }"
              height="400px"
              style="width: 100%"
            >
              <el-table-column :label="`商品信息${goodsData.length ? `(已选择${goodsData.length}款商品)` : ''}`">
                <template #default="{ row }">
                  <div class="goods-list__info">
                    <el-image :preview-src-list="[row.pic]" :preview-teleported="true" :src="row.pic" fit="" style="width: 60px; height: 60px" />
                    <div class="goods-list__goods-list__info-name">
                      <div class="goods-list__goods-list__info-name--name">{{ row.productName }}</div>
                      <div class="goods-list__goods-list__info-name--price">
                        {{ $useConvert.divTenThousand(row.salePrices[0]) }}
                      </div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column width="100px">
                <template #header>
                  <el-button :disabled="isDisable(['未开始', '进行中'])" plain round type="primary" size="small" @click="chooseGoodsPopupShow = true"
                    >添加商品
                  </el-button>
                </template>
                <template #default="{ row }">
                  <el-link :disabled="isDisable(['未开始'])" :underline="false" type="primary" @click="handleDelGoods(row.productId)">删除 </el-link>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <!-- 选择商品弹出 s-->
    <el-dialog v-model="chooseGoodsPopupShow" :before-close="handleClose" width="800px">
      <ChooseGoodsPopup ref="chooseGoodsPopupRef" :pointGoodsList="goodsData" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="chooseGoodsPopupShow = false">取消</el-button>
          <el-button type="primary" @click="handleConfirm"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <div class="commodityForm__tool">
    <el-button plain round @click="$router.back()">{{ !isLookUp ? '取消' : '返回' }}</el-button>
    <QSafeBtn v-if="!isLookUp" style="margin-left: 40px" :disabled="isDisable(['', '未开始', '进行中'])" round type="primary" @click="handleSubmit"
      >确定</QSafeBtn
    >
  </div>
</template>
<style lang="scss" scoped>
@include b(coupon-info) {
  padding: 16px;
  position: relative;
  height: 100%;
  overflow-y: scroll;
  @include e(form) {
    overflow-y: auto;
  }
}
@include b(title) {
  text-align: center;
  font-size: 14px;
  color: #323233;
  font-weight: 700;
  margin-bottom: 30px;
  line-height: 25px;
}

@include b(msg) {
  font-size: 12px;
  margin-left: 12px;
  color: #c4c4c4;
}

@include b(rules) {
  display: flex;
  margin-top: 10px;
  height: 70px;
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
  width: 100%;
  display: flex;
  align-items: center;
}

@include b(flex) {
  margin-top: 10px;
  height: 70px;
}

@include b(flex-item) {
  width: 40%;
}

@include b(coupon-rules) {
  height: 74px;
  @include flex;
}

.commodityForm__tool {
  margin-top: auto;
  align-items: center;
  position: sticky;
  bottom: 0;
  padding: 15px 0;
  display: flex;
  justify-content: center;
  box-shadow: 0 0 10px #d5d5d5;
  background-color: #fff;
  z-index: 9;
}
</style>
