<script setup lang="ts">
import { ElMessage } from 'element-plus'
import useConvert from '@/composables/useConvert'
import { doPostAddIntegralProduct, doGetIntegralProductInfo, doPutIntegralProductInfoUpdate } from '../apis'
import type { Ref } from 'vue'
import type { FormInstance } from 'element-plus'
import type { AddonIntegralGoods } from '../components/types/add-goods'
import { useRouter, useRoute } from 'vue-router'
import { ref, computed, onBeforeMount, provide, defineAsyncComponent } from 'vue'

// 商铺添加步骤
const stepIndicator = {
  NewBasicInfo: {
    prev: '',
    next: 'NewProductInfo',
    stepIndex: 0,
  },
  NewProductInfo: {
    prev: 'NewBasicInfo',
    next: '',
    stepIndex: 1,
  },
}
const $router = useRouter()
const $route = useRoute()
const { mulTenThousand, divTenThousand } = useConvert()
const componentRef = ref()
const tempArr: Ref<FormInstance>[] = []
const reactiveComponent = {
  NewBasicInfo: defineAsyncComponent(() => import('../components/NewBasicInfo.vue')),
  NewProductInfo: defineAsyncComponent(() => import('../components/NewProductInfo.vue')),
}
const currentStep = ref<'NewBasicInfo' | 'NewProductInfo'>('NewBasicInfo')
const stepIndex = computed(() => {
  return currentStepIndicator.value.stepIndex
})
const submitForm = ref<AddonIntegralGoods>({
  id: '',
  name: '',
  price: 1,
  integralPrice: 0,
  stock: 0,
  virtualSalesVolume: 0,
  detail: '',
  pic: '',
  albumPics: '',
  freightPrice: 0,
  integralProductAttributes: [
    {
      attributeName: '',
      attributeValue: '',
    },
  ],
  salePrice: 0,
  productType: 'REAL_PRODUCT',
})
const commodityImgList = ref<string[]>([])
// 当前添加步骤
const currentStepIndicator = computed(() => {
  return stepIndicator[currentStep.value]
})
const prevStep = computed(() => {
  return currentStepIndicator.value.prev
})
const nextStep = computed(() => {
  return currentStepIndicator.value.next
})

onBeforeMount(initInfo)

async function initInfo() {
  if ($route.query.id) {
    const { code, msg, data } = await doGetIntegralProductInfo($route.query.id)
    if (code !== 200) {
      ElMessage.error(msg || '获取积分商品信息失败')
      return
    }
    data.salePrice = divTenThousand(data.salePrice)
    submitForm.value = data
    imageConversion()
  }
}
// 上一步
const preHandle = () => {
  handleSaveComponent(componentRef.value)
  currentStep.value = prevStep.value as 'NewBasicInfo'
}
/**
 * 存储组件实例
 */
const handleSaveComponent = (component: Ref<FormInstance>) => {
  if (!tempArr[stepIndex.value]) {
    tempArr.push(componentRef.value)
  }
}
const handleGetComponent = () => {
  if (tempArr[stepIndex.value]) {
    return tempArr[stepIndex.value]
  } else {
    return componentRef.value
  }
}
// 下一步
const nextHandle = async () => {
  let passStatus = true
  // 编辑销售信息的多规格校验
  if (nextStep.value === 'NewProductInfo') {
    passStatus = validateTableFn()
  }
  passStatus &&
    handleGetComponent().currentFormRef &&
    handleGetComponent().currentFormRef.validate((valid: boolean) => {
      if (valid) {
        handleSaveComponent(componentRef.value)
        currentStep.value = nextStep.value as 'NewProductInfo'
      }
    })
}
/**
 * 校验table
 */
function validateTableFn() {
  return true
}
const submitHandle = async () => {
  const { id } = submitForm.value
  submitForm.value.salePrice = +mulTenThousand(submitForm.value.salePrice)
  const params = { ...submitForm.value }
  const { code, data, msg } = await (id ? doPutIntegralProductInfoUpdate(params) : doPostAddIntegralProduct(params))
  if (code !== 200) {
    ElMessage.error(msg || `${id ? '更新' : '提交'}失败`)
    return
  }
  commodityImgList.value = []
  ElMessage.success(`${id ? '更新' : '提交'}成功`)
  $router.push({ name: 'integralMall' })
}
function imageConversion() {
  let tmp: string[] = []
  if (submitForm.value.albumPics) {
    tmp = submitForm.value.albumPics.split(',')
  }
  commodityImgList.value = tmp
}
const goBack = () => {
  $router.back()
}
provide('form', {
  submitForm,
  commodityImgList,
})
</script>

<template>
  <div class="commodityForm fdc1">
    <el-steps :active="stepIndex" simple class="commodityForm__steps">
      <el-step title="1.编辑基本信息" icon="none"></el-step>
      <el-step title="2.编辑商品信息" icon="none"></el-step>
    </el-steps>
    <keep-alive :exclude="['NewProductInfo']">
      <component :is="reactiveComponent[currentStep]" ref="componentRef" class="f1"></component>
    </keep-alive>
    <div class="commodityForm__tool">
      <el-button v-if="prevStep !== ''" type="primary" round @click="preHandle">上一步</el-button>
      <el-button v-if="nextStep !== ''" type="primary" round @click="nextHandle">下一步</el-button>
      <el-button v-if="prevStep !== ''" type="primary" round @click="submitHandle">
        {{ $route.query.id ? '更新' : '上架' }}
      </el-button>
      <el-button v-if="prevStep !== ''" type="" round @click="goBack"> 取消 </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@include b(commodityForm) {
  box-sizing: border-box;
  overflow-y: scroll;
  @include e(tool) {
    width: 100%;
    @include flex();
    position: sticky;
    bottom: 0;
    padding: 15px 0px;
    display: flex;
    justify-content: center;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    z-index: 100;
    margin-top: auto;
  }
  .commodityForm__steps {
    position: sticky;
    top: 0;
    z-index: 100;
  }
}
</style>
