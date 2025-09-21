<script setup lang="ts">
import { ref, reactive, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import QInputNumber from '@/components/q-input-number/q-input-number.vue'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import SelectGoodsTable from '../components/addBargain/select-goods-table.vue'
import DateUtil from '@/utils/date'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doPostAddBargain, doGetBargainInfo } from '../apis'
import type { FormInstance, FormRules } from 'element-plus'
import type { DoPostAddBargainQuery } from '../index'
import type { ChoosedGoodCallBack } from '@/components/q-choose-goods-popup/types'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'

interface PageData {
  form: DoPostAddBargainQuery
  isEditDisable: boolean
  rules: FormRules
  fullReductionTime: string[]
  chooseGoodsPopup: boolean
}
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
const $router = useRouter()
const $route = useRoute()
const dateUtil = new DateUtil()
const selectGoodsTableRef = ref()
const datePickerValue = ref<string[]>([])
const pageData = reactive<PageData>({
  form: {
    shopId: '',
    shopName: useShopInfoStore().shopInfo.name,
    name: '',
    startTime: '',
    endTime: '',
    bargainingPeople: 0,
    bargainValidityPeriod: 5,
    isSelfBargain: false,
    // userType: 'NEW_USER',
    // activityPreheat: 0,
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
const choosedGoods = ref<ApiRetrieveComItemType[]>([])
// 回显商品列表
const flatGoodList = ref<any[]>([])
const { form, isEditDisable, rules, chooseGoodsPopup } = toRefs(pageData)
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
  79113: '当前商品不是砍价商品 ',
  /**
   * 砍价失败
   */
}

const handleChangeTime = (e: Date[] | null) => {
  if (!e || !e[0] || !e[1]) {
    return
  }
  if (e[0].getTime() >= e[1].getTime()) {
    ElMessage.warning('结束时间大于开始时间')
  }
  form.value.startTime = dateUtil.getYMDHMSs(e[0])
  form.value.endTime = dateUtil.getYMDHMSs(e[1])
}

initGetBargainInfo()
// 编辑回显
async function initGetBargainInfo(params = $route.query) {
  if (params.shopId && params.activityId) {
    isEditDisable.value = !!params.isLookUp
    const { code, data, msg } = await doGetBargainInfo(params as { shopId: string; activityId: string })
    if (code !== 200) return ElMessage.error(msg || '获取活动详情失败')
    form.value = data
    form.value.bargainProducts = data.bargainActivityProducts
    form.value.bargainValidityPeriod = +data.bargainValidityPeriod
    datePickerValue.value = [data.startTime, data.endTime]
    flatGoodList.value = fillTable(data.bargainActivityProducts)
  }
}
function fillTable(data: DoPostAddBargainQuery['bargainProducts']) {
  // 筛选回显
  return data.map((item) => {
    const { activityId, productId, skuPrice, productPic, productName, skuId, skuStock, stock, skuName, floorPrice } = item
    return {
      floorPrice,
      isJoin: true,
      productId,
      productName,
      productPic,
      stock,
      skuItem: { productId, skuId, skuName, skuPrice, skuStock },
    }
  })
}
// 加载中
const loading = ref(false)
const handleSubmit = async () => {
  if (!ruleFormRef.value) return
  const isValitate = await ruleFormRef.value.validate()
  if (!isValitate) return
  if (!selectGoodsTableRef.value) return
  if (selectGoodsTableRef.value.validateProduct()) {
    form.value.bargainProducts = selectGoodsTableRef.value.getProduct()
    form.value.productNum = form.value.bargainProducts.length
    loading.value = true
    const { code, data, msg } = await doPostAddBargain(form.value)
    if (code === 200) return success()
    const errArr = [79111, 79112, 79113]
    if (errArr.includes(code)) return ElMessage.error(AddBargainError[code as keyof typeof AddBargainError] || msg || '添加活动失败')
    ElMessage.error(msg || '添加活动失败')
  }
  loading.value = false
}
function success() {
  ElMessage.success('添加活动成功')
  $router.push({
    name: 'bargainIndex',
  })
}
const handleChoosedCallback = (e: ChoosedGoodCallBack) => {
  choosedGoods.value = JSON.parse(JSON.stringify(e.tempGoods))
}
function filterDate(date: Date) {
  const currentDate = dateUtil.getYMD(new Date())
  const chooseDate = dateUtil.getYMD(date)
  if (currentDate === chooseDate) {
    return false
  }
  return new Date().getTime() > date.getTime()
}
const handleChooseGoodsOpen = async () => {
  if (ruleFormRef.value && !!form.value.endTime && !!form.value.startTime) {
    let start = true
    let end = true
    await ruleFormRef.value.validateField('startTime', (e) => {
      start = e
    })
    await ruleFormRef.value.validateField('endTime', (e) => {
      end = e
    })
    if (start && end) {
      // 查询活动需把活动时段传入供后端筛选商品
      const { startTime, endTime } = form.value
      searchConfig.activity.startTime = `${startTime}`
      searchConfig.activity.endTime = `${endTime}`
      chooseGoodsPopup.value = true
      return
    }
    return ElMessage.warning('活动时间选择有误')
  }
  return ElMessage.warning('请选择活动时间')
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
    callback()
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
  <div style="padding: 30px" class="add">
    <h1 class="title">{{ isEditDisable ? '基本信息' : '新增砍价' }}</h1>
    <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="auto" :inline-message="false" label-position="left">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model.trim="form.name" style="width: 551px" maxlength="10" placeholder="活动名称不超过10个字" :disabled="isEditDisable" />
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
            :disabled-date="filterDate"
            @change="handleChangeTime"
          />
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
      <!-- <el-form-item label="砍价用户类型" prop="fullReductionName">
                    <el-radio-group v-model="form.userType" class="ml-4" :disabled="isEditDisable">
                        <el-radio value="UNLIMITED">不限</el-radio>
                        <el-radio value="NEW_USER">新用户</el-radio>
                    </el-radio-group>
                    <span class="msg">新用户是指未在本店铺购买过商品的用户</span>
                </el-form-item> -->

      <!-- <el-form-item label="活动预热">
                    <span>活动前</span>
                    <el-select
                        v-model="form.activityPreheat"
                        style="width: 135px; margin: 0 10px"
                        maxlength="15"
                        placeholder="请选择"
                        :disabled="isEditDisable"
                    >
                        <el-option label="请选择" :value="0" />
                        <el-option label="1小时" :value="1" />
                        <el-option label="2小时" :value="2" />
                        <el-option label="3小时" :value="3" />
                        <el-option label="4小时" :value="4" />
                        <el-option label="5小时" :value="5" />
                    </el-select>
                    <span>开启活动预热</span>
                    <span class="msg">预热阶段用户可看到砍价活动，但活动开始前无法发起砍价</span>
                </el-form-item> -->
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
        <el-button type="primary" round plain :disabled="isEditDisable" @click="handleChooseGoodsOpen"> 选择商品 </el-button>
        <span class="msg">是否参与：SKU的粒度设置商品是否参与活动，是(默认)则参与活动，反则否</span>
      </el-form-item>
    </el-form>
    <select-goods-table ref="selectGoodsTableRef" :productList="choosedGoods" :is-edit="isEditDisable" :flat-good-list="flatGoodList">
    </select-goods-table>
  </div>
  <div class="btn">
    <el-button round plain @click="$router.back()">{{ !isEditDisable ? '取消' : '返回' }}</el-button>
    <QSafeBtn v-if="!isEditDisable" type="primary" round :loading="loading" @click="handleSubmit">确定</QSafeBtn>
  </div>
  <!-- 选择商品弹出 s-->
  <QChooseGoodsPopup
    v-model="chooseGoodsPopup"
    v-model:searchParam="searchConfig"
    :pointGoodsList="choosedGoods"
    :searchConsignmentProduct="true"
    @onConfirm="handleChoosedCallback"
  />
  <!-- 选择商品弹出 e-->
</template>

<style scoped lang="scss">
@include b(add) {
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  &::-webkit-scrollbar {
    display: none;
  }
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
  color: #323233;
  font-weight: 700;
  margin-bottom: 30px;
  text-align: center;
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
@include b(btn) {
  margin-top: auto;
  width: 100%;
  padding: 15px 0;
  text-align: center;
  box-shadow: 0 0 10px rgba(46, 44, 44, 0.3);
}
</style>
