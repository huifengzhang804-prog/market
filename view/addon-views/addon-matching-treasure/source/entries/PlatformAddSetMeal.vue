<script setup lang="ts">
import { ref, reactive, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SelectGoodsTable from '../components/select-goods-table.vue'
import type { DoPostSetMealQuery } from '../index'
import { doGetSetMealDetail } from '../apis'
import DateUtil from '@/utils/date'
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import type { FormInstance, FormRules } from 'element-plus'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
import QUpload from '@/components/q-upload/q-upload.vue'

interface PageData {
  form: DoPostSetMealQuery
  isEditDisable: boolean
  rules: FormRules
  fullReductionTime: string[]
  flatMainGoodList: any[]
  flatMatchingGoodList: any[]
  choosedMainGoods: ApiRetrieveComItemType[]
  choosedMatchingGoods: ApiRetrieveComItemType[]
  choosedMainGoodsPopup: boolean
  choosedMatchingGoodsPopup: boolean
}

const $router = useRouter()
const $route = useRoute()
const dateUtil = new DateUtil()
const selectMainGoodsTableRef = ref()
const selectMatchingGoodsTableRef = ref()
const pageData = reactive<PageData>({
  form: {
    setMealId: '',
    shopId: '',
    shopName: '',
    setMealName: '',
    setMealDescription: '',
    setMealMainPicture: '',
    setMealType: 'FIXED_COMBINATION',
    setMealStatus: 'NOT_STARTED',
    startTime: '',
    endTime: '',
    stackable: {
      coupon: false,
      vip: false,
      full: false,
    },
    mainProduct: [],
    matchingProducts: [],
    shopMode: '',
    distributionMode: '',
  },
  // 编辑状态用于判定禁用
  isEditDisable: false,
  fullReductionTime: [],
  rules: {
    setMealName: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
    setMealDescription: [{ required: true, message: '请输入套餐描述', trigger: 'blur' }],
    product: [{ required: true, message: '请选择商品', trigger: ['blur', 'change'] }],
    startTime: [
      { required: true, message: '请选择开始日期', trigger: ['blur', 'change'] },
      { validator: validateStartTime, trigger: ['blur', 'change'] },
    ],
    endTime: [
      { required: true, message: '请选择结束日期', trigger: ['blur', 'change'] },
      { validator: validateEndTime, trigger: ['blur', 'change'] },
    ],
    setMealMainPicture: [{ required: true, message: '请添加套餐主图', trigger: ['blur', 'change'] }],
    setMealType: [{ required: true, message: '请选择套餐类型', trigger: ['blur', 'change'] }],
  },
  flatMainGoodList: [],
  flatMatchingGoodList: [],
  choosedMainGoods: [],
  choosedMatchingGoods: [],
  choosedMainGoodsPopup: false,
  choosedMatchingGoodsPopup: false,
})

// 回显商品列表
const {
  form,
  isEditDisable,
  rules,
  choosedMainGoodsPopup,
  choosedMatchingGoodsPopup,
  choosedMainGoods,
  choosedMatchingGoods,
  flatMainGoodList,
  flatMatchingGoodList,
} = toRefs(pageData)
const ruleFormRef = ref<FormInstance>()
const { mulTenThousand, divTenThousand } = useConvert()

initGetBargainInfo()

// 编辑回显
async function initGetBargainInfo(params = $route.query) {
  if (params.shopId && params.setMealId) {
    isEditDisable.value = true
    const { code, data, msg } = await doGetSetMealDetail(params.shopId as string, params.setMealId as string)
    if (code !== 200) return ElMessage.error(msg || '获取活动详情失败')
    form.value = data
    flatMainGoodList.value = fillTable(data.mainProduct)
    flatMatchingGoodList.value = fillTable(data.matchingProducts)
  }
}
function fillTable(data: DoPostSetMealQuery['mainProduct']) {
  // 筛选回显
  return data.map((item) => {
    const {
      productId,
      skuPrice,
      productPic,
      productName,
      skuId,
      skuStock,
      stockType,
      skuName,
      setMealId,
      matchingPrice,
      matchingStock,
      productAttributes,
    } = item
    return {
      isJoin: true,
      productId,
      productName,
      productPic,
      setMealId,
      matchingPrice: divTenThousand(matchingPrice).toString(),
      matchingStock,
      productAttributes,
      skuItem: { productId, skuId, skuName, skuPrice, skuStock, stockType },
    }
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

function activityTimeHandlerByType(type: 'main' | 'matching') {
  if (type === 'main') {
    choosedMainGoodsPopup.value = true
    return
  }

  choosedMatchingGoodsPopup.value = true
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
    <h1 class="title">基本信息</h1>
    <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="auto" :inline-message="false" label-position="left">
      <el-form-item label="套餐名称" prop="setMealName">
        <el-input v-model.trim="form.setMealName" style="width: 551px" maxlength="10" placeholder="限10个字" :disabled="isEditDisable" />
      </el-form-item>
      <el-form-item label="套餐描述" prop="setMealDescription">
        <el-input
          v-model.trim="form.setMealDescription"
          style="width: 551px"
          maxlength="40"
          placeholder="套餐描述不超过40个字"
          :disabled="isEditDisable"
        />
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
      <el-form-item label="套餐主图" prop="setMealMainPicture">
        <q-upload v-model:src="form.setMealMainPicture" :disabled="isEditDisable" :width="100" :height="100"
      /></el-form-item>
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
      <el-form-item label="套餐类型" prop="setMealType">
        <el-radio-group v-model="form.setMealType" class="ml-4" :disabled="isEditDisable">
          <el-radio value="OPTIONAL_PRODUCT">自选商品套餐</el-radio>
          <el-radio value="FIXED_COMBINATION">固定套餐</el-radio>
        </el-radio-group>
        <span class="msg">自选套餐：主商品+至少1种搭配商品以上 ；固定套餐：主商品+库存不为0的所有搭配商品各1件以上</span>
      </el-form-item>
      <el-form-item label="配送方式" prop="distributionMode">
        <el-radio-group v-model="form.distributionMode" :disabled="isEditDisable" class="ml-4" @change="handleChangeDistributionMode">
          <el-radio v-if="form.shopMode !== 'O2O'" value="EXPRESS">快递配送</el-radio>
          <el-radio value="INTRA_CITY_DISTRIBUTION">同城配送</el-radio>
          <el-radio value="SHOP_STORE">到店自提</el-radio>
          <el-radio v-if="form.shopMode !== 'O2O'" value="VIRTUAL">虚拟配送</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="主商品(限1种商品)" required>
        <span class="msg">主商品：必买商品，此商品详情页展示搭配套餐 搭配商品：用户选择购买</span>
      </el-form-item>
    </el-form>
    <select-goods-table
      ref="selectMainGoodsTableRef"
      :product-list="choosedMainGoods"
      :is-edit="isEditDisable"
      :flat-good-list="flatMainGoodList"
      style="margin-bottom: 20px"
    ></select-goods-table>
    <el-form-item label="搭配商品（限4种）" required>
      <span class="msg">是否参与：SKU的粒度设置商品是否参与活动，是(默认)则参与活动，反则否</span>
    </el-form-item>
    <select-goods-table
      ref="selectMatchingGoodsTableRef"
      :product-list="choosedMatchingGoods"
      :is-edit="isEditDisable"
      :flat-good-list="flatMatchingGoodList"
      product-attributes="MATCHING_PRODUCTS"
    ></select-goods-table>
  </div>
  <div class="tool">
    <el-button round plain @click="$router.back()">返回</el-button>
  </div>
</template>

<style scoped lang="scss">
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
@include b(add) {
  padding: 30px 16px 10px 16px;
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
