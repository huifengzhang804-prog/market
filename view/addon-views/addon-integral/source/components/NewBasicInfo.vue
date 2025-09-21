<script lang="ts" setup>
import { reactive, ref, inject, Ref, defineExpose, watch, onMounted } from 'vue'
import { UploadProps } from 'element-plus'
import type { AddonIntegralGoods } from './types/add-goods'
import { CircleClose } from '@element-plus/icons-vue'
import { VueDraggableNext } from 'vue-draggable-next'
import { useRoute } from 'vue-router'

// 选择素材 e
import selectMaterial from '@/views/material/selectMaterial.vue'
const dialogVisible = ref(false)
const selectMaterialFn = (val: boolean) => {
  dialogVisible.value = val
}
const $route = useRoute()
const parameterId = ref<number>()
let num = 0
const buttonlFn = (val: number) => {
  dialogVisible.value = true
  parameterId.value = val
  num++
}
// @cropped-file-change="" 裁剪后返回的单个素材
// @checked-file-lists=""  选中素材返回的素材合集
const croppedFileChange = (val: any) => {
  if (num === 1) commodityImgList.value = val
  else commodityImgList.value[parameterId.value] = val
  submitForm.value.albumPics = arrToString(commodityImgList)
}
const checkedFileLists = (val: any) => {
  if (num === 1) commodityImgList.value = val
  else commodityImgList.value[parameterId.value] = val
  submitForm.value.albumPics = arrToString(commodityImgList)
}
// 选择素材 d

const handleSortCommodityList = () => {
  submitForm.value.albumPics = arrToString(commodityImgList)
}

type AttributeItemType = Record<'id' | 'content' | 'name', string>
interface AttributeListType extends AttributeItemType {
  attributeTemplates: AttributeItemType[]
}
type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>
interface ShopCategoryList extends ShopCategoryItem {
  disabled?: boolean
  children: ShopCategoryList[]
}
const currentFormRef = ref()
const $parent = inject('form')
const submitForm = $parent.submitForm as Ref<AddonIntegralGoods>
const commodityImgList = $parent.commodityImgList as Ref<string[]>
const basicRules = reactive({
  name: [
    {
      required: true,
      message: '请填写商品名称',
      trigger: 'blur',
    },
  ],
  stock: [
    {
      required: true,
      message: '请填写商品库存',
      trigger: 'blur',
    },
  ],
  integralPrice: [
    {
      required: true,
      message: '请填写积分价',
      trigger: 'blur',
    },
  ],
  albumPics: [
    {
      required: true,
      message: '请选择商品主图',
      trigger: 'change',
    },
  ],
  productType: [
    {
      required: true,
      message: '请选择商品类型',
      trigger: 'change',
    },
  ],
})
/** 服务保障 */
const serviceAssuranceList = ref([
  {
    name: '7天退换',
    state: false,
    text: '商家承诺7天无理由退换货',
    enum: 'SEVEN_END_BACK',
  },
  {
    name: '小时发货',
    state: false,
    text: '商家承诺订单发货时间',
    enum: 'DAY_SEND',
  },
  {
    name: '假一赔十',
    state: false,
    text: '若收到商品是假冒品牌，可获得十倍赔偿',
    enum: 'FAKE_COMPENSATE',
  },
  {
    name: '正品保证',
    state: false,
    text: '商家承诺商品正品质量',
    enum: 'ALL_ENSURE',
  },
])

onMounted(() => {
  imageConversion()
})
// 商品主图字符串转数组处理
watch(
  submitForm.value,
  (val) => {
    imageConversion()
  },
  {
    immediate: true,
  },
)

/**
 * 删除商品主图
 * @param {number} index
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

function imageConversion() {
  let tmp: string[] = []
  if (submitForm.value.albumPics) {
    tmp = submitForm.value.albumPics.split(',')
  }
  commodityImgList.value = tmp
}

const selectTypeFn = (type: 'REAL_PRODUCT' | 'VIRTUAL_PRODUCT') => {
  if (submitForm.value?.id !== '') return
  submitForm.value.productType = type
}

defineExpose({
  currentFormRef,
})
</script>
<template>
  <div class="navLine">基本信息</div>
  <el-form ref="currentFormRef" :model="submitForm" :rules="basicRules">
    <el-form-item label="商品名称" prop="name" label-width="120px">
      <el-input v-model="submitForm.name" class="inputWidth" placeholder="请填写商品名称" maxlength="35"> </el-input>
    </el-form-item>
    <el-form-item :label="'商品类型' + ($route.query.id ? '(单选)' : '')" prop="productType" label-width="120px">
      <template v-if="$route.query.id">
        <span v-if="submitForm.productType === 'REAL_PRODUCT'">实物商品(快递配送)</span>
        <span v-else-if="submitForm.productType === 'VIRTUAL_PRODUCT'">虚拟商品(无需物流)</span>
      </template>
      <div v-else class="shopType">
        <div class="shopType__tag">不同的商品类型可编辑的字段内容不同，商品类型一旦发布后将不可更改</div>
        <div class="shopType__select">
          <div
            class="shopType__select--type"
            :class="submitForm.productType === 'REAL_PRODUCT' ? 'border' : ''"
            @click="selectTypeFn('REAL_PRODUCT')"
          >
            <p style="font-size: 16px">实物商品</p>
            <p style="color: #8c939d">快递配送</p>
          </div>
          <div
            class="shopType__select--type"
            :class="submitForm.productType === 'VIRTUAL_PRODUCT' ? 'border' : ''"
            @click="selectTypeFn('VIRTUAL_PRODUCT')"
          >
            <p style="font-size: 16px">虚拟商品</p>
            <p style="color: #8c939d">无需物流</p>
          </div>
        </div>
      </div>
    </el-form-item>
    <el-form-item label="划线价" prop="price" label-width="120px">
      <el-input-number
        v-model="submitForm.price"
        class="input_number inputWidth"
        placeholder="请填写划线价"
        :min="0"
        :max="999999"
        :precision="2"
        :controls="false"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="积分价" prop="integralPrice" label-width="120px">
      <el-input-number
        :model-value="+submitForm.integralPrice"
        class="inputWidth input_number"
        :max="999999"
        :min="1"
        placeholder="请填写积分价"
        :controls="false"
        @update:model-value="(e) => (submitForm.integralPrice = e)"
      ></el-input-number>
    </el-form-item>
    <!-- <el-form-item label="销售价" prop="salePrice" label-width="100px">
            <el-input-number
                v-model="submitForm.salePrice"
                class="inputWidth input_number"
                :max="999999"
                :min="0"
                placeholder="请填写销售价"
                :precision="0"
                :controls="false"
            ></el-input-number>
        </el-form-item> -->
    <el-form-item label="销售价" prop="salePrice" label-width="120px">
      <el-input-number
        :model-value="+submitForm.salePrice"
        :max="999999"
        :min="0"
        class="inputWidth input_number"
        :precision="2"
        :controls="false"
        @update:model-value="(e) => (submitForm.salePrice = e)"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="库存" prop="stock" label-width="120px">
      <el-input-number
        v-model="submitForm.stock"
        class="input_number inputWidth"
        :max="999999"
        placeholder="请填写库存"
        :min="0"
        :precision="0"
        :controls="false"
      ></el-input-number>
    </el-form-item>
    <el-form-item label="销量(注水)" prop="initSalesVolume" label-width="120px">
      <el-input-number
        v-model="submitForm.virtualSalesVolume"
        :max="999999"
        :min="0"
        class="inputWidth input_number"
        placeholder="请填写销量(注水)"
        :precision="0"
        :controls="false"
      ></el-input-number>
    </el-form-item>
    <el-form-item v-if="submitForm.productType !== 'VIRTUAL_PRODUCT'" label="运费" prop="freightPrice" label-width="120px">
      <el-input-number
        v-model="submitForm.freightPrice"
        class="inputWidth input_number"
        placeholder="请填写重量（KG）"
        :max="999999"
        :min="0"
        :precision="0"
        :controls="false"
      ></el-input-number>
    </el-form-item>
    <!-- <el-form-item label="商品属性" label-width="100px" required>
            <div v-if="submitForm.integralProductAttributes.length" class="com__attr">
                <div class="com__attr-header">
                    <div>属性名称</div>
                    <div>属性值</div>
                    <div>操作</div>
                </div>
                <div v-for="(item, index) in submitForm.integralProductAttributes" :key="index" class="com__attr-content">
                    <el-input v-model="item.attributeName" placeholder="" class="com__attr-input" maxlength="10"></el-input>
                    <el-input v-model="item.attributeValue" placeholder="" class="com__attr-input" maxlength="20"></el-input>
                    <el-link
                        :underline="false"
                        :disabled="submitForm.integralProductAttributes.length === 1"
                        type="danger"
                        @click="delAttrHandle(index)"
                        >删除</el-link
                    >
                </div>
            </div>
        </el-form-item> -->
    <el-form-item label-width="120px">
      <!-- <el-button type="primary" round plain @click="addNewAttrHandle">添加属性</el-button> -->
    </el-form-item>
    <el-form-item label="商品主图" prop="albumPics" label-width="120px">
      <el-row style="width: 100%">
        <!-- <VueDraggableNext v-if="commodityImgList.length" v-model="commodityImgList" style="display: flex">
                    <div
                        v-for="(item, index) in commodityImgList"
                        :key="item"
                        style="position: relative; margin-right: 20px; width: 100px; height: 100px"
                    >
                        <img :src="commodityImgList[index]" width="100" height="100" style="border-radius: 7px" />
                        <el-icon
                            v-if="item"
                            style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%"
                            color="#7f7f7f"
                            size="20px"
                            @click="delImgHandle(index)"
                        >
                            <CircleClose />
                        </el-icon>
                        <div v-if="index === 0" class="com__imgText">封面图</div>
                    </div>
                </VueDraggableNext> -->
        <VueDraggableNext v-if="commodityImgList.length" v-model="commodityImgList" style="display: flex" @sort="handleSortCommodityList">
          <div v-for="(item, index) in commodityImgList" :key="index" style="position: relative; margin-right: 20px">
            <img :src="commodityImgList[index]" alt="" class="selectMaterialStyle" style="width: 105px; height: 105px" @click="buttonlFn(index)" />
            <el-icon
              v-if="item"
              color="#7f7f7f"
              size="20px"
              style="position: absolute; right: -5px; top: -5px; background: #fff; border-radius: 50%; cursor: pointer"
              @click="delImgHandle(index)"
            >
              <CircleClose />
            </el-icon>
            <div v-if="index === 0" class="com__imgText">封面图</div>
          </div>
        </VueDraggableNext>
        <div
          v-if="commodityImgList.length !== 6"
          class="selectMaterialStyle"
          style="width: 100px; height: 100px"
          @click="buttonlFn(commodityImgList.length)"
        >
          <span class="selectMaterialStyle__span">+</span>
        </div>
      </el-row>
      <div style="color: rgba(69, 64, 60, 0.6); font-size: 12px">尺寸建议 750x750、750x1125 像素以上，大小1M以下，最多6张 (可拖拽图片调整顺序)1</div>
    </el-form-item>
  </el-form>
  <!-- 选择素材 e -->
  <selectMaterial
    :dialog-visible="dialogVisible"
    :upload-files="6 - commodityImgList.length"
    @select-material-fn="selectMaterialFn"
    @cropped-file-change="croppedFileChange"
    @checked-file-lists="checkedFileLists"
  />
  <!-- 选择素材 d -->
</template>

<style lang="scss" scoped>
@include b(navLine) {
  margin: 25px 0;
  height: 40px;
  line-height: 40px;
  background-color: #f8f8f8;
  padding-left: 15px;
  font-weight: 700;
}
@include b(serveMsg_hours) {
  @include flex;
}
@include b(inputWidth) {
  width: 550px;
}
.serveMsg {
  width: 400px;
  display: flex;
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
</style>

<style lang="scss">
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
.input_number {
  .el-input__inner {
    text-align: left;
  }
}
@include b(selectMaterialStyle) {
  width: 60px;
  height: 60px;
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
@include b(shopType) {
  display: block !important;
  @include e(tag) {
    color: #8c939d;
  }
  @include e(select) {
    display: flex;
    @include m(type) {
      padding: 15px;
      text-align: center;
      border: 1px solid #ccc;
      margin-right: 20px;
      width: 150px;
    }
  }
}
@include b(border) {
  border: 1px solid #fd0505 !important;
}
</style>
