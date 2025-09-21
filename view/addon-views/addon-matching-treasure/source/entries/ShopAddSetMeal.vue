<script lang="ts" setup>
import { ref, reactive, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import SelectGoodsTable from '../components/select-goods-table.vue'
import { doPostSetMeal, doGetSetMealDetail } from '../apis'
import DateUtil from '@/utils/date'
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import type { DoPostSetMealQuery } from '../index'
import type { FormInstance, FormRules } from 'element-plus'
import type { ChoosedGoodCallBack } from '@/components/q-choose-goods-popup/types'
import type { ApiRetrieveComItemType } from '@/apis/good/model'
import selectMaterial from '@/views/material/selectMaterial.vue'
import { cloneDeep } from 'lodash-es'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'

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
const choosedMainGoodsRef = ref()
const choosedMatchingGoodsRef = ref()
const datePickerValue = ref<string[]>([])
const pageData = reactive<PageData>({
  form: {
    setMealId: '',
    shopId: '',
    shopName: useShopInfoStore().shopInfo.name,
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
    distributionMode: 'EXPRESS',
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
const searchConfigMain = reactive({
  maxPrice: '',
  minPrice: '',
  keyword: '',
  categoryFirstId: '',
  excludeProductIds: [] as string[],
})
const searchConfig = reactive({
  maxPrice: '',
  minPrice: '',
  keyword: '',
  categoryFirstId: '',
  excludeProductIds: [] as string[],
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
const { divTenThousand } = useConvert()

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
  isEditDisable.value = !!params.isLookUp
  if (params.shopId && params.setMealId) {
    const { code, data, msg } = await doGetSetMealDetail(params.shopId as string, params.setMealId as string)
    if (code !== 200) return ElMessage.error(msg || '获取活动详情失败')
    form.value = data
    // form.value.mainProduct = data.mainProduct
    flatMainGoodList.value = fillTable(data.mainProduct)
    // form.value.matchingProducts = data.matchingProducts
    flatMatchingGoodList.value = fillTable(data.matchingProducts)
    datePickerValue.value = [data.startTime, data.endTime]
  }
}

function fillTable(data: DoPostSetMealQuery['mainProduct']) {
  // 筛选回显
  return data.map((item) => {
    const {
      productId,
      shopId,
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
      shopId,
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

const handleSubmit = async () => {
  if (!ruleFormRef.value) return
  const isValitate = await ruleFormRef.value.validate()
  if (!isValitate) return
  if (!selectMainGoodsTableRef.value && !selectMatchingGoodsTableRef.value) return
  if (selectMainGoodsTableRef.value.validateProduct() && selectMatchingGoodsTableRef.value.validateProduct()) {
    form.value.mainProduct = selectMainGoodsTableRef.value.getProduct()
    form.value.matchingProducts = selectMatchingGoodsTableRef.value.getProduct()
    const { code, data, msg } = await doPostSetMeal(form.value)
    if (code === 200) return success()
    ElMessage.error(msg || '添加活动失败')
  }
}

function success() {
  ElMessage.success('添加活动成功')
  $router.push({
    name: 'bundlePriceIndex',
    query: {
      flag: 'true',
    },
  })
}

const handleChoosedCallback = (e: ChoosedGoodCallBack) => {
  searchConfigMain.excludeProductIds = e.tempGoods.map((item: ApiRetrieveComItemType) => {
    return item.productId
  })
  choosedMatchingGoods.value = cloneDeep(e.tempGoods)
  choosedMainGoodsRef.value.retrieveCommodity()
}
const handleChoosedCallbackMain = (e: ChoosedGoodCallBack) => {
  console.log(e.tempGoods)
  searchConfig.excludeProductIds = [e.tempGoods[0].productId]
  choosedMainGoods.value = cloneDeep(e.tempGoods)
  choosedMatchingGoodsRef.value.retrieveCommodity()
}

function filterDate(date: Date) {
  const currentDate = dateUtil.getYMD(new Date())
  const chooseDate = dateUtil.getYMD(date)
  if (currentDate === chooseDate) {
    return false
  }
  return new Date().getTime() > date.getTime()
}

const handleChooseGoodsOpen = async (type: 'main' | 'matching') => {
  if (!form.value.distributionMode) return ElMessage.error({ message: '请先选择配送方式' })
  return activityTimeHandlerByType(type)
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

const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
  dialogVisible.value = val
}
const buttonlFn = () => {
  if (isEditDisable.value) return
  dialogVisible.value = true
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: any) => {
  form.value.setMealMainPicture = val
}
const checkedFileLists = (val: any) => {
  form.value.setMealMainPicture = val?.shift() || ''
}
const handleDistributionModeChange = () => {
  // 删除表格
  flatMatchingGoodList.value = []
  choosedMatchingGoods.value = []
  flatMainGoodList.value = []
  choosedMainGoods.value = []
  // 取消全选
  choosedMainGoodsRef.value.allChecked = false
  choosedMatchingGoodsRef.value.allChecked = false
  choosedMainGoodsRef.value.handleGetAll()
  choosedMatchingGoodsRef.value.handleGetAll()
}
</script>

<template>
  <div class="add">
    <h1 class="title">{{ isEditDisable ? '基本信息' : '新增套餐' }}</h1>
    <el-form ref="ruleFormRef" :inline-message="false" :model="form" :rules="rules" label-position="left" label-width="auto">
      <el-form-item label="套餐名称" prop="setMealName">
        <el-input v-model.trim="form.setMealName" :disabled="isEditDisable" maxlength="10" placeholder="限10个字" style="width: 551px" />
      </el-form-item>
      <el-form-item label="套餐描述" prop="setMealDescription">
        <el-input
          v-model.trim="form.setMealDescription"
          :disabled="isEditDisable"
          maxlength="40"
          placeholder="套餐描述不超过40个字"
          style="width: 551px"
        />
      </el-form-item>
      <el-form-item label="活动日期" required>
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
      <el-form-item label="套餐主图" prop="setMealMainPicture">
        <!-- <q-upload v-model:src="form.setMealMainPicture" :format="{ size: 1 }" :height="100" :width="100" /> -->

        <div v-if="!form.setMealMainPicture" class="selectMaterialStyle" @click="buttonlFn">
          <span class="selectMaterialStyle__span">+</span>
        </div>
        <img v-else :src="form.setMealMainPicture" alt="" class="selectMaterialStyle" @click="buttonlFn" />
      </el-form-item>
      <el-form-item label="优惠叠加">
        <div class="use_discount">
          <el-checkbox v-model="form.stackable.vip" :disabled="isEditDisable" label="会员价"></el-checkbox>
          <el-checkbox v-model="form.stackable.coupon" :disabled="isEditDisable" label="优惠券"></el-checkbox>
          <el-checkbox v-model="form.stackable.full" :disabled="isEditDisable" label="满减"></el-checkbox>
          <div class="msg discount_msg">
            优惠叠加可能导致实付金额为 <strong style="color: red">0</strong> (实付金额 = 活动价 - 会员优惠 - 优惠券优惠 - 满减优惠)
          </div>
        </div>
      </el-form-item>
      <el-form-item label="套餐类型" prop="setMealType">
        <el-radio-group v-model="form.setMealType" :disabled="isEditDisable" class="ml-4">
          <el-radio value="OPTIONAL_PRODUCT">自选商品套餐</el-radio>
          <el-radio value="FIXED_COMBINATION">固定套餐</el-radio>
        </el-radio-group>
        <span class="msg">自选套餐：主商品+至少1种搭配商品以上 ；固定套餐：主商品+库存不为0的所有搭配商品各1件以上</span>
      </el-form-item>

      <el-form-item label="配送方式" prop="distributionMode" required>
        <el-radio-group v-model="form.distributionMode" class="ml-4" :disabled="isEditDisable" @change="handleDistributionModeChange">
          <el-radio value="EXPRESS">快递配送</el-radio>
          <el-radio value="INTRA_CITY_DISTRIBUTION">同城配送</el-radio>
          <el-radio value="SHOP_STORE">到店自提</el-radio>
          <el-radio v-if="useShopInfoStore().shopInfo?.mode !== 'O2O'" value="VIRTUAL">虚拟配送</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="主商品(限1种商品)" required>
        <el-button :disabled="isEditDisable" plain round type="primary" @click="handleChooseGoodsOpen('main')"> 选择商品 </el-button>
        <span class="msg">主商品：必买商品，此商品详情页展示搭配套餐 搭配商品：用户选择购买</span>
      </el-form-item>
    </el-form>
    <SelectGoodsTable
      ref="selectMainGoodsTableRef"
      :flatGoodList="flatMainGoodList"
      :isEdit="isEditDisable"
      :productList="choosedMainGoods"
      style="margin-bottom: 20px"
    ></SelectGoodsTable>
    <el-form-item label="搭配商品（限4种）" required>
      <el-button :disabled="isEditDisable" plain round type="primary" @click="handleChooseGoodsOpen('matching')"> 选择商品 </el-button>
      <span class="msg">是否参与：SKU的粒度设置商品是否参与活动，是(默认)则参与活动，反则否</span>
    </el-form-item>
    <SelectGoodsTable
      ref="selectMatchingGoodsTableRef"
      :flatGoodList="flatMatchingGoodList"
      :isEdit="isEditDisable"
      :productList="choosedMatchingGoods"
      productAttributes="MATCHING_PRODUCTS"
    ></SelectGoodsTable>
    <div class="position_tool">
      <el-button plain round @click="$router.back()">{{ !isEditDisable ? '取消' : '返回' }}</el-button>
      <QSafeBtn v-if="!isEditDisable" round type="primary" @click="handleSubmit">确定</QSafeBtn>
    </div>
    <!-- 选择商品弹出 s-->
    <QChooseGoodsPopup
      ref="choosedMainGoodsRef"
      v-model="choosedMainGoodsPopup"
      v-model:searchParam="searchConfigMain"
      :pointGoodsList="choosedMainGoods"
      :searchConsignmentProduct="true"
      :distributionMode="form.distributionMode"
      :quota="1"
      @onConfirm="handleChoosedCallbackMain"
    />
    <QChooseGoodsPopup
      ref="choosedMatchingGoodsRef"
      v-model="choosedMatchingGoodsPopup"
      v-model:searchParam="searchConfig"
      :pointGoodsList="choosedMatchingGoods"
      :searchConsignmentProduct="true"
      :distributionMode="form.distributionMode"
      :quota="4"
      @onConfirm="handleChoosedCallback"
    />
    <!-- 选择商品弹出 e-->
    <!-- 选择素材 e -->
    <selectMaterial
      :dialog-visible="dialogVisible"
      :upload-files="1"
      @select-material-fn="selectMaterialFn"
      @cropped-file-change="croppedFileChange"
      @checked-file-lists="checkedFileLists"
    />
    <!-- 选择素材 d -->
  </div>
</template>

<style lang="scss" scoped>
@include b(add) {
  scrollbar-width: none;
  -ms-overflow-style: none;
  height: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow-y: scroll;
  padding: 40px;
  padding-bottom: 0;
  &::-webkit-scrollbar {
    display: none;
  }
  .position_tool {
    margin-top: auto;
    width: calc(100% + 80px);
    @include flex;
    position: sticky;
    bottom: 0;
    padding: 15px 0px;
    display: flex;
    justify-content: center;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    transform: translateX(-40px);
    z-index: 100;
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
@include b(selectMaterialStyle) {
  width: 100px;
  height: 100px;
  border-radius: 5px;
  overflow: hidden;
  border: 1px dashed #ccc;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  @include e(span) {
    color: #999;
    font-size: 20px;
  }
}
</style>
