<script lang="ts" setup>
import { ref, reactive, watch, onMounted, onActivated, inject, computed, defineProps } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CascaderProps, CheckboxValueType, ElMessage, UploadProps } from 'element-plus'
import { QuestionFilled, CircleClose } from '@element-plus/icons-vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import CityDistributionCheckbox from '@/q-plugin/cityDistribution/CityDistributionCheckbox.vue'
import FreightCheckBox from '@/q-plugin/freight/FreightCheckBox.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ApiSupplierType, ProductLabel } from '../../../../../index'
import {
  getToBeReleaseDetails,
  doGetLogisticsTemplateList,
  doGetSupList,
  doGetPlatformCategory,
  doGetCategory,
  doGetLabelList,
} from '../../../../../apis'
import { elementUploadRequest } from '@/apis/upload'
//引入api获取同城配送的数据
import type { ApiFreightTemplate } from '@/views/freight/components/types'
import type { Ref } from 'vue'
import { FormInject } from '../types'
import ReleaseTitle from './ReleaseTitle.vue'
import useServiceAssuranceList from './hooks/useServiceAssuranceList'

type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>

interface ShopCategoryList extends ShopCategoryItem {
  disabled?: boolean
  children: ShopCategoryList[]
}

const props = defineProps({
  // eslint-disable-next-line vue/prop-name-casing
  VITE_BASE_URL: {
    type: String,
    default: '',
  },
})

const $route = useRoute()
const $router = useRouter()
const currentFormRef = ref()
const uploadUrl = props.VITE_BASE_URL + 'gruul-mall-carrier-pigeon/oss/upload'
const $parent: any = inject('form') as FormInject
const submitForm = $parent.submitForm
const logisticsData = ref<ApiFreightTemplate[]>([])
const commodityImgList = $parent.commodityImgList
const supplierList = ref<ApiSupplierType[]>([])
const LabelList = ref<ProductLabel[]>([])
const platformCategoryList = ref<ShopCategoryList[]>([])
const shopCategoryList = ref<ShopCategoryList[]>([])
/**
 * 级联选择器 ref
 */
const platformCategoryRef = ref(null)
const shopCategoryRef = ref(null)

const shopCascaderProps: CascaderProps = {
  expandTrigger: 'hover',
  label: 'name',
  value: 'id',
}
const basicRules: Partial<Record<string, any>> = reactive({
  name: [
    {
      required: true,
      message: '请填写商品名称',
      trigger: 'blur',
    },
  ],
  categoryId: [
    {
      required: true,
      message: '请选择店铺分类',
      trigger: 'blur',
    },
  ],
  albumPics: [
    {
      required: true,
      message: '请添加商品主图',
      trigger: 'blur',
    },
  ],
  platformCategoryId: [{ required: true, message: '请选择平台分类', trigger: 'change' }],
  distributionMode: [{ required: true, message: '请选择配送方式', trigger: 'change', type: 'array' }],
})

const shopInfo = useShopInfoStore()
const notO2O = computed(() => shopInfo.shopInfo.mode !== 'O2O')

onActivated(async () => {
  const forwardPath: string = $router.options.history.state.forward as string
  if (forwardPath !== '/freight/logistics?from=releaseGoods') {
    // 如果是从设置页面回来的不需要再次进行回显
    await dataDisplay()
  }
  Promise.all([getPlatformCategory(), getShopCategory()]).then(() => {
    categoryChange('platformCategory').then(() => {})
    categoryChange('shopCategory').then(() => {})
  })
  getSupplier()
  getLabelList()
})
onMounted(() => {
  initLogisticsTemplateList()
})
// 商品主图字符串转数组处理
watch(
  submitForm.value,
  (newVal, oldVal) => {
    imageConversion()
  },
  {
    immediate: true,
  },
)
const { serviceAssuranceList, updateserviceIds, resetServiceAssuranceList, initServiceAssuranceList } = useServiceAssuranceList(submitForm)

async function initLogisticsTemplateList() {
  const { code, data } = await doGetLogisticsTemplateList(1, 1000)
  if (code !== 200) return ElMessage.error('获取物流列表失败')
  logisticsData.value = data.records
}

const beforeVideoUpload: UploadProps['beforeUpload'] = (rawfile) => {
  const whiteList = ['video/mp4']
  const isLt = rawfile.size < 10 * 1024 * 1024
  if (!whiteList.includes(rawfile.type)) {
    ElMessage.error('上传视频只能是 mp4 格式!')
    return false
  }
  if (!isLt) {
    ElMessage.error('上传视频大小不超过10M!')
    return false
  }
  return true
}

const uploadVideoSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  submitForm.value.videoUrl = response.data
}
/**
 * 新增商品主图
 */
const addNewMainSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  commodityImgList.value.push(response)
  submitForm.value.albumPics = arrToString(commodityImgList)
}
/**
 * 删除商品主图
 */
const delImgHandle = (index: number) => {
  commodityImgList.value.splice(index, 1)
  submitForm.value.albumPics = arrToString(commodityImgList)
}
/**
 * 数组转字符串
 */
function arrToString(arr: Ref<string[]>) {
  return arr.value.join(',')
}
/**
 * 获取供应商
 */
async function getSupplier() {
  const { code, data } = await doGetSupList({
    current: 1,
    size: 1000,
    status: 'REVIEW',
  })
  if (code !== 200) {
    ElMessage.error('获取供应商失败')
    return
  }
  supplierList.value = data.records
}

/**
 * 获取商品标签
 */
async function getLabelList() {
  const { code, data } = await doGetLabelList(shopInfo.shopInfo.shopType)
  if (code !== 200) {
    ElMessage.error('获取商品标签失败')
    return
  }
  LabelList.value = data
}

/**
 * 获取平台类目
 */
async function getPlatformCategory() {
  const { code, data, success } = await doGetPlatformCategory()
  if (code !== 200) {
    ElMessage.error('获取平台分类失败')
    return
  }
  platformCategoryList.value = checkCategoryEnable(1, data)
}

/**
 * 获取店铺分类
 */
async function getShopCategory() {
  const { code, data } = await doGetCategory({
    current: 1,
    size: 500,
  })
  if (code !== 200) {
    ElMessage.error('获取店铺分类失败')
    return
  }
  shopCategoryList.value = checkCategoryEnable(1, data.records)
}
/**
 * 数据回显
 */
async function dataDisplay() {
  if ($route.query.id) {
    const { code, data } = await getToBeReleaseDetails($route.query.id as string, $route.query.supplierId as string)
    if (code !== 200) {
      ElMessage.error('获取商品信息失败')
      return
    }
    // 商品回显图片字符转数组
    commodityImgList.value = data?.product?.albumPics.split(',')
    const productInfo = data?.product || {}
    productInfo.freightTemplateId = '0'
    submitForm.value = { ...submitForm.value, ...productInfo, categoryId: submitForm.value.categoryId }
    initServiceAssuranceList()
    if (submitForm.value.distributionMode.length === 0) {
      if (data?.product?.distributionMode?.includes('VIRTUAL')) {
        submitForm.value.distributionMode = ['VIRTUAL']
      }
    }
    isDisabledDistributionWay.value = submitForm.value.distributionMode?.includes('VIRTUAL')
  }
}
/**
 * 检查是分类否可用
 */
function checkCategoryEnable(currentLevel: number, records: any[]) {
  const isLastLevel = currentLevel === 3
  for (let index = 0; index < records.length; ) {
    const record = records[index]
    if (isLastLevel) {
      record.disabled = false
      index++
      continue
    }
    const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
    delete record.secondCategoryVos
    delete record.categoryThirdlyVos
    const disable = !children || children.length === 0
    record.disabled = disable
    if (disable) {
      records.splice(index, 1)
      continue
    }
    checkCategoryEnable(currentLevel + 1, children)
    if (children.length === 0) {
      records.splice(index, 1)
      continue
    }
    record.children = children
    index++
  }

  return records
}

function imageConversion() {
  let tmp = []
  if (submitForm.value.albumPics) {
    tmp = submitForm.value.albumPics.split(',')
  }
  commodityImgList.value = tmp
}

const categoryChange = async (categoryType: 'platformCategory' | 'shopCategory') => {
  const iscategoryType = categoryType === 'platformCategory'
  const value = (iscategoryType ? platformCategoryRef.value : shopCategoryRef.value) as any
  if (!value) {
    return
  }
  const checkedNodes = await value.getCheckedNodes()
  let category = submitForm.value[categoryType]
  if (!category) {
    const defaultCategory = {
      one: null,
      two: null,
      three: null,
    }
    submitForm.value[categoryType] = defaultCategory
    category = defaultCategory
  }
  if (!checkedNodes || checkedNodes.length === 0) {
    category.one = null
    category.two = null
    category.three = null
    return
  }
  const categoryIds = checkedNodes[0].pathValues
  const isEmpty = !categoryIds || categoryIds.length === 0
  if (iscategoryType) {
    const categoryId = checkedNodes[0].pathNodes[0].data.categoryId
    category.one = isEmpty ? null : categoryId
  } else {
    category.one = isEmpty ? null : categoryIds[0]
  }
  category.two = isEmpty ? null : categoryIds[1]
  category.three = isEmpty ? null : categoryIds[2]
}

const isDisabledDistributionWay = ref(false)
const changeDistributionMode = (distributionMode: CheckboxValueType[]) => {
  if (distributionMode.includes('VIRTUAL')) {
    submitForm.value.distributionMode = ['VIRTUAL']
  }
  isDisabledDistributionWay.value = distributionMode.includes('VIRTUAL')
}

defineExpose({
  currentFormRef,
  resetServiceAssuranceList,
})
const handleSortCommodityList = () => {
  submitForm.value.albumPics = arrToString(commodityImgList)
}
const templateIdChange = (templateId: string) => {
  submitForm.value.templateId = templateId
}
</script>
<template>
  <release-title title="基本信息" />
  <el-form ref="currentFormRef" :model="submitForm" :rules="basicRules">
    <el-row :gutter="8">
      <el-col :span="12">
        <el-form-item label="商品类型" label-width="100px">
          <el-select v-model="submitForm.productType" :disabled="$route.query.type === 'ProcurementRelease'" class="inputWidth">
            <el-option label="实物商品（快递/同城/自提）" value="REAL_PRODUCT" />
            <el-option v-if="notO2O" label="虚拟商品（无需物流）" value="VIRTUAL_PRODUCT" />
          </el-select>
          <el-tooltip content="不同的商品类型可编辑的字段内容不同，商品类型一旦发布后将不可更改！" effect="effect-tooltip" placement="top">
            <el-icon color="#999" size="14px" style="margin-left: 24px"><QuestionFilled /></el-icon>
          </el-tooltip>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="商品名称" label-width="100px" prop="name">
          <el-input v-model="submitForm.name" class="inputWidth" maxlength="35" placeholder="请填写商品名称"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="平台类目" label-width="100px" prop="platformCategoryId">
          <template #default>
            <el-cascader
              ref="platformCategoryRef"
              v-model="submitForm.platformCategoryId"
              :disabled="$route.query.type === 'ProcurementRelease'"
              :options="platformCategoryList"
              :props="shopCascaderProps"
              class="inputWidth"
              clearable
              placeholder="请选择平台类目"
              show-all-levels
              style="width: calc(100% - 120px)"
              @change="() => categoryChange('platformCategory')"
            />
          </template>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="卖点描述" label-width="100px" prop="saleDescribe">
          <el-input v-model="submitForm.saleDescribe" class="inputWidth" maxlength="13" placeholder="请填写卖点描述"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="店铺类目" label-width="100px" prop="categoryId">
          <template #default>
            <el-cascader
              ref="shopCategoryRef"
              v-model="submitForm.categoryId"
              :options="shopCategoryList"
              :props="shopCascaderProps"
              class="inputWidth"
              clearable
              placeholder="请选择店铺类目"
              show-all-levels
              style="width: calc(100% - 120px)"
              @change="() => categoryChange('shopCategory')"
            />
            <el-link
              :underline="false"
              style="margin-left: 15px"
              type="primary"
              @click="
                $router.push({
                  name: 'goodsCategory',
                  query: {
                    from: 'releaseGoods',
                  },
                })
              "
            >
              前往设置
            </el-link>
          </template>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="自有货源" label-width="100px" prop="providerId">
          <el-select v-model="submitForm.providerId" class="inputWidth" placeholder="请选择供应商" style="">
            <el-option v-for="item in supplierList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
          <el-link
            :underline="false"
            style="margin-left: 15px"
            type="primary"
            @click="
              $router.push({
                name: 'goodsSupplier',
                query: {
                  from: 'releaseGoods',
                },
              })
            "
          >
            前往设置
          </el-link>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="商品标签" label-width="100px">
          <el-select v-model="submitForm.labelId" class="inputWidth" placeholder="请选择商品标签">
            <el-option v-for="item in LabelList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="8">
      <el-col :span="12">
        <el-form-item label="商品大图" label-width="100px">
          <el-row style="width: 100%">
            <q-upload v-model:src="submitForm.widePic" :format="{ size: 1 }" :height="120" :width="250" />
          </el-row>
          <div style="color: #9d9d9d; font-size: 14px">尺寸建议710x460（长方形模式）像素以上，大小1M以下的JPG、PNG格式图片</div>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="商品视频" label-width="100px">
          <el-row style="width: 100%">
            <el-upload
              :action="uploadUrl"
              :before-upload="beforeVideoUpload"
              :http-request="elementUploadRequest"
              :on-success="uploadVideoSuccess"
              :show-file-list="false"
              class="avatar-uploader"
            >
              <video v-if="submitForm.videoUrl !== ''" :src="submitForm.videoUrl" controls style="width: 250px; height: 120px"></video>
              <el-icon v-else class="avatar-uploader-icon"><i-ep-plus /></el-icon>
            </el-upload>
          </el-row>
          <div style="color: #9d9d9d; font-size: 14px">大小为10M以下</div>
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item label="商品主图" label-width="100px" prop="albumPics">
      <el-row style="width: 100%">
        <VueDraggableNext v-if="commodityImgList.length" v-model="commodityImgList" style="display: flex" @sort="handleSortCommodityList">
          <div v-for="(item, index) in commodityImgList" :key="item" style="position: relative; margin-right: 20px">
            <q-upload v-model:src="commodityImgList[index]" :format="{ size: 1 }" :height="100" :width="100" />
            <el-icon
              v-if="item"
              color="#7f7f7f"
              size="20px"
              style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%"
              @click="delImgHandle(index)"
              ><CircleClose />
            </el-icon>
            <div v-if="index === 0" class="com__imgText">封面图</div>
          </div>
        </VueDraggableNext>
        <q-upload v-show="commodityImgList.length <= 5" :format="{ size: 1 }" :height="100" :width="100" @change="addNewMainSuccess" />
      </el-row>
      <div style="color: rgba(69, 64, 60, 0.6); font-size: 12px">
        尺寸建议750x750（正方形模式）像素以上，大小1M以下，最多6张 (可拖拽图片调整顺序 )
      </div>
    </el-form-item>
    <div v-if="submitForm.productType === 'REAL_PRODUCT'">
      <!-- 虚拟商品只能选择无需物流 -->
      <release-title title="物流信息" />
      <el-form-item label="配送方式（至少选一种）" label-width="200px" required style="margin: 0"> </el-form-item>
      <el-form-item label="" label-width="100px" prop="distributionMode">
        <el-checkbox-group v-model="submitForm.distributionMode" @change="changeDistributionMode">
          <CityDistributionCheckbox />
          <br />
          <el-checkbox value="SHOP_STORE">到店自提</el-checkbox>
          <br />
          <freight-check-box
            v-if="notO2O"
            :disable="isDisabledDistributionWay"
            :template-change="templateIdChange"
            :template-id="submitForm.freightTemplateId"
          />
        </el-checkbox-group>

        <logistics-setting v-if="submitForm.freightTemplateId !== '0'" :id="submitForm.freightTemplateId" :data="logisticsData" />
      </el-form-item>
    </div>
    <release-title title="服务保障" />
    <el-form-item class="mleft-form-item">
      <div v-for="item in serviceAssuranceList" :key="item.name" style="display: flex; flex-direction: column">
        <el-checkbox v-model="item.state" @change="updateserviceIds">
          <div class="serveMsg">
            <span style="width: 120px">{{ item.name }}</span>
            <span style="color: #c6c6c6">{{ item.text }}</span>
          </div>
        </el-checkbox>
      </div>
    </el-form-item>
  </el-form>
</template>

<style lang="scss" scoped>
// 规格
@include b(spec) {
  // 类型
  @include e(type) {
  }
}
:deep(.el-tag__content) {
  max-width: 214px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@include b(norm) {
  font-size: 14px;
  margin: 20px 0;
  @include flex('flex-start', 'flex-start');
  @include e(title) {
    margin: 0 20px 0 30px;
  }
  @include e(item) {
    color: #606266;
  }
  @include e(item__name) {
    display: flex;
    @include m(del) {
      margin-left: 10px;
    }
  }
  @include e(item__value) {
    @include flex('flex-start');
    @include m(mg34) {
      margin-right: 34px;
    }
  }
  @include e(item__value__content) {
    @include flex();
    margin: 20px 0;
    cursor: move;
  }
}

@include b(specDetail) {
  font-size: 14px;
  text-align: center;
  @include e(table) {
    width: 800px;
    display: flex;
    flex-direction: column;
    overflow-x: auto;
    background: #fcfcfc;
  }
  @include e(title) {
    margin: 0 30px;
  }
  @include flex('flex-start', 'flex-start');
  @include e(table__title) {
    line-height: 50px;
    @include flex('flex-start', 'flex-start');
    @include m(primary) {
      flex-shrink: 0;
      width: 150px;
    }
    @include m(custom) {
      flex-shrink: 0;
      width: 100px;
    }
    @include m(header) {
      @include flex();
      flex-shrink: 0;
    }
  }
  @include e(table__item) {
    @include flex('flex-start', 'flex-start');
    @include m(font) {
      flex-shrink: 0;
      width: 100px;
      line-height: 70px;
      color: #606266;
    }
    @include m(input) {
      flex-shrink: 0;
      width: 150px;
      @include flex();
      & > .el-input {
        width: 80%;
      }
      & > .el-input__inner {
        width: 80%;
      }
    }
    @include m(upload) {
      width: 150px;
      @include flex();
    }
    & .el-form-item--small.el-form-item {
      margin: 0 !important;
    }
  }
}
@include b(batch) {
  margin: 30px;
  @include flex(flex-start, center);
  @include e(title) {
  }
  @include e(list) {
    @include flex(flex-start);
    cursor: pointer;
    color: #409eff;
    margin-left: 20px;
  }
  @include e(list-item) {
    @include flex();
    margin-right: 10px;
  }
}

@include b(com) {
  @include e(input) {
    @include m(width) {
      width: 500px;
    }
  }
}
.serveMsg {
  width: 400px;
  display: flex;
}
.FreightTemplateChoose {
  position: absolute;
}
@include b(inputWidth) {
  width: calc(100% - 120px);
}
@include b(com) {
  @include e(attr) {
    margin-top: 10px;
    width: 620px;
    padding: 20px 20px;
    border: 1px solid #d7d7d7;
  }
  @include e(attr-header) {
    @include flex(space-between);
  }
  @include e(attr-content) {
    @include flex(space-between);
    margin: 10px 0;
  }
  @include e(attr-input) {
    width: 230px;
  }
  @include e(attr-del) {
    color: red;
  }
  @include e(imgText) {
    position: absolute;
    right: 0;
    bottom: 0;
    font-size: 12px;
    text-align: center;
    width: 100%;
    background-color: rgba(0, 0, 0, 0.3);
    border-radius: 0 0 6px 6px;
    color: #fff;
  }
}

.goodsType {
  .title {
    font-weight: 400;
    font-size: 16px;
    margin-bottom: 20px;
  }
  .is-active {
    .title {
      font-weight: 700;
    }
  }
}
// @Override css
.el-link.is-disabled {
  color: #999;
}
</style>

<style>
.mleft-form-item .el-form-item__content {
  padding-left: 100px;
}
.el-popper.is-effect-tooltip {
  background-color: #fff;
  color: #9d9d9d;
  box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.15);
}
.el-popper.is-effect-tooltip .el-popper__arrow::before {
  background: #fff;
  right: 0;
}
.goodsType .el-radio-button__original-radio:checked + .el-radio-button__inner {
  color: #333;
  background-color: unset;
}
.goodsType .el-radio-button__inner {
  padding: 20px 40px;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.main-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.main-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 250px;
  height: 120px;
  text-align: center;
}
.el-icon.main-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
}
</style>
