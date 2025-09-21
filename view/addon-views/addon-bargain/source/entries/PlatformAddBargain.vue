<script setup lang="ts">
import { ref, reactive, toRefs } from 'vue'
import type { DoPostAddBargainQuery } from '../index'
import { doPostAddBargain, doGetBargainInfo } from '../apis'
import SelectGoodsTable from '../components/select-goods-table.vue'
import { useRoute, useRouter } from 'vue-router'
import QInputNumber from '@/components/q-input-number/q-input-number.vue'
import DateUtil from '@/utils/date'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { ApiRetrieveComItemType } from '@/apis/good/model'

interface PageData {
  form: DoPostAddBargainQuery
  isEditDisable: boolean
  rules: FormRules
  fullReductionTime: string[]
  chooseGoodsPopup: boolean
}
const $router = useRouter()
const $route = useRoute()
const dateUtil = new DateUtil()
const selectGoodsTableRef = ref()
const pageData = reactive<PageData>({
  form: {
    shopId: '',
    shopName: '',
    name: '',
    startTime: '',
    endTime: '',
    bargainingPeople: 0,
    bargainValidityPeriod: 5,
    isSelfBargain: false,
    stackable: {
      coupon: false,
      vip: false,
      full: false,
    },
    status: 'ILLEGAL_SELL_OFF',
    helpCutAmount: 'FIX_BARGAIN',
    bargainProducts: [],
    productNum: 0,
  },
  // 编辑状态用于判定禁用
  isEditDisable: false,
  fullReductionTime: [],
  rules: {
    name: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
    product: [{ required: true, message: '请选择商品', trigger: ['blur', 'change'] }],
    startTime: [
      { required: true, message: '请选择开始日期', trigger: ['blur', 'change'] },
      { validator: validateStartTime, trigger: ['blur', 'change'] },
    ],
    endTime: [
      { required: true, message: '请选择结束日期', trigger: ['blur', 'change'] },
      { validator: validateEndTime, trigger: ['blur', 'change'] },
    ],
  },
  chooseGoodsPopup: false,
})
const choosedGoods = ref<ApiRetrieveComItemType[]>([])
// 回显商品列表
const flatGoodList = ref<any[]>([])
const { form, isEditDisable, rules } = toRefs(pageData)
const ruleFormRef = ref<FormInstance>()
// 砍价错误码
const AddBargainError = {
  /**
   * 砍价商品在相同时间段内已存在
   */
  79111: '砍价商品在相同时间段内已存在',
  /**
   * 砍价活动不存在
   */
  79112: '砍价活动不存在',
  /**
   * 当前商品不是砍价商品
   */
  79113: '当前商品不是砍价商品',
  /**
   * 砍价失败
   */
}

initGetBargainInfo()

// 编辑回显
async function initGetBargainInfo(params = $route.query) {
  if (params.shopId && params.activityId) {
    isEditDisable.value = true
    const { code, data, msg } = await doGetBargainInfo(params as { shopId: string; activityId: string })
    if (code !== 200) return ElMessage.error(msg || '获取活动详情失败')
    form.value = { ...data, shopName: '', bargainProducts: data.bargainActivityProducts, productNum: data.bargainValidityPeriod }
    form.value.bargainProducts = data.bargainActivityProducts
    flatGoodList.value = fillTable(data.bargainActivityProducts)
  }
}
function fillTable(data: DoPostAddBargainQuery['bargainProducts']) {
  // 筛选回显
  return data.map((item) => {
    const { productId, skuPrice, productPic, productName, skuId, skuStock, stock, skuName, floorPrice, stockType } = item
    return {
      floorPrice,
      isJoin: true,
      productId,
      productName,
      productPic,
      stock,
      skuItem: { productId, skuId, skuName, skuPrice, skuStock, stockType },
    }
  })
}
const handleSubmit = async () => {
  if (!ruleFormRef.value) return
  const isValitate = await ruleFormRef.value.validate()
  if (!isValitate) return
  if (!selectGoodsTableRef.value) return
  if (selectGoodsTableRef.value.validateProduct()) {
    form.value.bargainProducts = selectGoodsTableRef.value.getProduct()
    form.value.productNum = form.value.bargainProducts.length
    const { code, data, msg } = await doPostAddBargain(form.value)
    if (code === 200) return success()
    const errArr = [79111, 79112, 79113]
    if (errArr.includes(code)) return ElMessage.error(AddBargainError[code as keyof typeof AddBargainError] || '添加活动失败')
    ElMessage.error(msg || '添加活动失败')
  }
}
function success() {
  ElMessage.success('添加活动成功')
  $router.push({
    name: 'bargainIndex',
  })
}
function filterDate(date: Date) {
  const currentDate = dateUtil.getYMD(new Date())
  const chooseDate = dateUtil.getYMD(date)
  if (currentDate === chooseDate) {
    return false
  }
  return new Date().getTime() > date.getTime()
}
function validateStartTime(rule: any, value: any, callback: any) {
  if (!value) {
    callback(new Error('请选择活动开始日期'))
  } else if (value && form.value.endTime) {
    intervalOfFiveMinutes(
      new Date(form.value.endTime).getTime(),
      callback,
      '开始日期和结束日期最少间隔5分钟',
      new Date(value).getTime(),
      1000 * 60 * 5,
    )
  } else {
    intervalOfFiveMinutes(new Date(value).getTime(), callback, '开始日期必须是一个将来的时间', new Date().getTime(), 1000)
  }
}
function validateEndTime(rule: any, value: any, callback: any) {
  if (!value) {
    callback(new Error('请选择活动结束日期'))
  } else if (value && form.value.startTime) {
    // 间隔5分钟
    intervalOfFiveMinutes(
      new Date(value).getTime(),
      callback,
      '开始日期和结束日期最少间隔5分钟',
      new Date(form.value.startTime).getTime(),
      1000 * 60 * 5,
    )
  } else {
    intervalOfFiveMinutes(new Date(value).getTime(), callback, '结束日期最少大当前时间5分钟', new Date().getTime() + 1000, 1000 * 60 * 5)
  }
}
/**
 * 开始时间大于当前时间
 */
function startTimeItsGreaterThanTheCurrentTime(ms: number, lastMs = new Date().getTime()) {
  const s = ms - lastMs
  return (time = 1000) => s >= time
}

function intervalOfFiveMinutes(ms: number, fn: any, msg: string, lastMs?: number, time?: number) {
  startTimeItsGreaterThanTheCurrentTime(ms, lastMs)(time) ? fn() : fn(new Error(msg))
}
</script>

<template>
  <div class="add">
    <h1 class="title p16">基本信息</h1>
    <el-form ref="ruleFormRef" class="p16" :model="form" :rules="rules" label-width="auto" :inline-message="false" label-position="left">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model.trim="form.name" style="width: 551px" maxlength="15" placeholder="活动名称不超过15个字" :disabled="isEditDisable" />
      </el-form-item>
      <el-form-item label="活动日期" required>
        <div class="ruleform-date">
          <el-form-item prop="startTime" :inline-message="false">
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              :disabled="isEditDisable"
              placeholder="请选择开始时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY/MM/DD HH:mm:ss"
              :disabled-date="filterDate"
            />
            <span style="margin: 0 10px">至</span>
          </el-form-item>
          <el-form-item prop="endTime" :inline-message="false">
            <el-date-picker
              v-model="form.endTime"
              :disabled="isEditDisable"
              type="datetime"
              placeholder="请选择结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY/MM/DD HH:mm:ss"
              :disabled-date="filterDate"
            />
          </el-form-item>
        </div>
      </el-form-item>
      <el-form-item label="砍价到底的人数" prop="bargainingPeople" required>
        <el-input-number v-model="form.bargainingPeople" style="width: 135px" :disabled="isEditDisable" :precision="0" :controls="false" :min="2" />
        <span class="msg">砍到底价所需人数</span>
      </el-form-item>
      <el-form-item label="砍价有效期" prop="bargainValidityPeriod" required>
        <q-input-number v-model="form.bargainValidityPeriod" :controls="false" :precision="0" :min="5" :disabled="isEditDisable">
          <template #append> 分钟 </template>
        </q-input-number>
        <span class="msg">砍价有效期是指从用户发起砍价到砍价截止的时间</span>
      </el-form-item>
      <el-form-item label="是否自我砍价" prop="fullReductionName">
        <el-radio-group v-model="form.isSelfBargain" class="ml-4" :disabled="isEditDisable">
          <el-radio :value="false">否</el-radio>
          <el-radio :value="true">是</el-radio>
        </el-radio-group>
        <span class="msg">是否用户发起砍价的同时为自己砍1次价</span>
      </el-form-item>
      <el-form-item label="优惠叠加">
        <div class="use_discount">
          <el-checkbox v-model="form.stackable.vip" label="会员价" :disabled="isEditDisable"> </el-checkbox>
          <el-checkbox v-model="form.stackable.coupon" label="优惠券" :disabled="isEditDisable"></el-checkbox>
          <el-checkbox v-model="form.stackable.full" label="满减" :disabled="isEditDisable"></el-checkbox>
          <div class="msg discount_msg">
            优惠叠加可能导致实付金额为 <strong style="color: red">0</strong> (实付金额 = 活动价 - 会员优惠 - 优惠券优惠 - 满减优惠)
          </div>
        </div>
      </el-form-item>

      <el-form-item label="单次砍价金额范围" prop="fullReductionName">
        <div class="bargaining_amount">
          <el-radio-group v-model="form.helpCutAmount" class="ml-4" :disabled="isEditDisable">
            <el-radio value="RANDOM_BARGAIN">随机砍价</el-radio>
            <el-radio value="FIX_BARGAIN">固定砍价</el-radio>
          </el-radio-group>
        </div>
        <div>
          <p class="msg">a.固定砍价 =（原价 - 砍价底价）/砍价人数</p>
          <p class="msg">b.随机砍价：最低砍价金额 = 1 ，最高砍价金额 = (原价 - 砍价底价) * 100 / 砍价到底人数 * 2 单位：分，最后一人砍完剩余价格</p>
        </div>
      </el-form-item>
      <el-form-item label="活动商品" required>
        <span class="msg">是否参与：SKU的粒度设置商品是否参与活动，是(默认)则参与活动，反则否</span>
      </el-form-item>
    </el-form>
    <SelectGoodsTable
      ref="selectGoodsTableRef"
      class="f1 p16"
      :product-list="choosedGoods"
      :is-edit="isEditDisable"
      :flat-good-list="flatGoodList"
    ></SelectGoodsTable>
  </div>
  <div class="tool">
    <el-button round plain @click="$router.back()">返回</el-button>
    <el-button v-if="!isEditDisable" type="primary" round @click="handleSubmit">保存</el-button>
  </div>
</template>

<style scoped lang="scss">
.add {
  margin-top: 30px;
  overflow: scroll;
  .p16 {
    padding-left: 16px;
    padding-right: 16px;
  }
}
.tool {
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
@include b(bargaining_amount) {
  position: relative;
  width: 100%;
  @include e(description) {
    position: absolute;
    top: -35px;
    right: 0;
    width: 480px;
  }
}
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
@include b(use_discount) {
  display: flex;
  width: 100%;
}
@include b(discount_msg) {
  display: inline-block;
  width: 400px;
  flex: 1;
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
</style>
