<script lang="ts" name="ReleaseCommodity" setup>
import { type Component, ref, defineAsyncComponent, provide, computed, defineProps } from 'vue'
import { useRoute, onBeforeRouteLeave, useRouter } from 'vue-router'
import { cloneDeep } from 'lodash-es'
import { doReleaseGood } from '../apis'
import useConvert from '@/composables/useConvert'
import { ElMessage, type FormInstance } from 'element-plus'
import { SubmitFormInterface } from '../components/page-components/waitingPublish/releaseCommodity/types'

const props = defineProps({
  properties: {
    type: Object,
    default: () => ({}),
  },
})
const VITE_BASE_URL = computed(() => {
  return props.properties.VITE_BASE_URL
})

const currentStep = ref('NewBasicInfo')
const $route = useRoute()
const $router = useRouter()
const { mulTenThousand } = useConvert()
const submitForm = ref<SubmitFormInterface>({
  name: '',
  saleDescribe: '',
  platformCategoryId: '',
  categoryId: '',
  providerId: '',
  labelId: '',
  widePic: '',
  distributionMode: [],
  videoUrl: '',
  albumPics: '',
  productType: 'REAL_PRODUCT',
  specGroups: [],
  //平台类目 1 2 3级id
  platformCategory: {
    one: null,
    two: null,
    three: null,
  },
  //店铺分类 1 2 3级id
  shopCategory: {
    one: null,
    two: null,
    three: null,
  },
  skus: [
    {
      id: '',
      image: '',
      initSalesVolume: 0,
      limitNum: 0,
      limitType: 'UNLIMITED',
      price: 0,
      productId: '',
      initStock: 0,
      salePrice: 0,
      shopId: '',
      stockType: 'UNLIMITED',
      weight: 0,
      specs: [],
    },
  ],
  productParameters: [],
  productAttributes: [],
  serviceIds: [],
  detail: '',
  freightTemplateId: '0',
  status: 'SELL_ON',
  brandId: '',
  sellType: 'PURCHASE',
})
const commodityImgList = ref([])
const copyGoodsTB = ref('')
const copyGoodsAL = ref('')
const copyGoodsJD = ref('')
provide('form', {
  submitForm,
  commodityImgList,
  copyGoodsJD,
  copyGoodsAL,
  copyGoodsTB,
})
const componentRef = ref<(typeof reactiveComponent)[keyof typeof reactiveComponent]>()
// 动态组件列表 MERCHANT
const reactiveComponent: { [x: string]: Component } = {
  NewBasicInfo: defineAsyncComponent(() => import('../components/page-components/waitingPublish/releaseCommodity/components/NewBasicInfo.vue')),
  NewSaleInfo: defineAsyncComponent(() => import('../components/page-components/waitingPublish/releaseCommodity/components/NewSaleInfo.vue')),
  NewProductInfo: defineAsyncComponent(() => import('../components/page-components/waitingPublish/releaseCommodity/components/NewProductInfo.vue')),
}
// 商铺添加步骤
const stepIndicator = {
  NewBasicInfo: {
    prev: '',
    next: 'NewSaleInfo',
    stepIndex: 0,
  },
  NewSaleInfo: {
    prev: 'NewBasicInfo',
    next: 'NewProductInfo',
    stepIndex: 1,
  },
  NewProductInfo: {
    prev: 'NewSaleInfo',
    next: '',
    stepIndex: 2,
  },
}
const tempArr: Array<(typeof reactiveComponent)[keyof typeof reactiveComponent]> = []
onBeforeRouteLeave((to, from, next) => {
  const cacheWhiteList = ['/goods/category', '/goods/supplier', '/goods/attribute', '/freight/logistics', '/goods/purchase']
  if (!cacheWhiteList.includes(to.path)) {
    submitForm.value = {
      name: '',
      saleDescribe: '',
      platformCategoryId: '',
      categoryId: '',
      labelId: '',
      providerId: '',
      distributionMode: [],
      productType: 'REAL_PRODUCT',
      widePic: '',
      videoUrl: '',
      albumPics: '',
      specGroups: [],
      //平台类目 1 2 3级id
      platformCategory: {
        one: null,
        two: null,
        three: null,
      },

      //店铺分类 1 2 3级id
      shopCategory: {
        one: null,
        two: null,
        three: null,
      },
      skus: [
        {
          id: '',
          image: '',
          initSalesVolume: 0,
          limitNum: 0,
          limitType: 'UNLIMITED',
          price: 0,
          productId: '',
          initStock: 0,
          salePrice: 0,
          shopId: '',
          stockType: 'UNLIMITED',
          weight: 0,
          specs: [],
        },
      ],
      productParameters: [],
      productAttributes: [],
      serviceIds: [],
      detail: '',
      freightTemplateId: '0',
      status: 'SELL_ON',
      brandId: '',
      sellType: 'PURCHASE',
    }
    // 初始化商品主图
    commodityImgList.value = []
    // 初始化一件获取
    copyGoodsJD.value = ''
    copyGoodsAL.value = ''
    copyGoodsTB.value = ''
    // 初始化切换下标
    currentStep.value = 'NewBasicInfo'
    // 初始化编辑标识
  }
  next()
})
// 当前添加步骤
const currentStepIndicator = computed(() => {
  return stepIndicator[currentStep.value as keyof typeof stepIndicator]
})

const stepIndex = computed(() => {
  return currentStepIndicator.value.stepIndex
})

const prevStep = computed(() => {
  return currentStepIndicator.value.prev
})

const nextStep = computed(() => {
  return currentStepIndicator.value.next
})
/**
 * 存储组件实例
 * @param {*} component
 */
const handleSaveComponent = () => {
  if (!tempArr[stepIndex.value]) {
    tempArr.push(componentRef.value as (typeof reactiveComponent)[keyof typeof reactiveComponent])
  }
}
const handleGetComponent = () => {
  if (tempArr[stepIndex.value]) {
    return tempArr[stepIndex.value]
  } else {
    return componentRef.value
  }
}
// 上一步
const preHandle = () => {
  handleSaveComponent()
  currentStep.value = prevStep.value
}
// 下一步
const nextHandle = async () => {
  let passStatus = true
  // 编辑销售信息的多规格校验
  if (nextStep.value === 'NewProductInfo') {
    const specObj = handleGetComponent()?.GoodOnlyRef.handleSpacObj()
    if (specObj) {
      const { productAttribute, productParameter } = specObj
      submitForm.value.productAttributes = productAttribute
      submitForm.value.productParameters = productParameter
    }
    if (submitForm.value.specGroups.length !== 0) {
      passStatus = validateTableFn()
    }
  }

  if (passStatus && handleGetComponent()?.currentFormRef) {
    const currentFormRef: FormInstance = handleGetComponent()?.currentFormRef
    currentFormRef.validate((valid) => {
      if (valid) {
        handleSaveComponent()
        currentStep.value = nextStep.value
      }
    })
  }
}
/**
 * 校验table
 */
function validateTableFn() {
  const skus = submitForm.value.skus
  if (skus.length > 0) {
    const skuError = skus
      .map((item) => {
        if (item.price <= 0) {
          // return `请修改规格${item.specs}中指导价且大于0`
          return `请修改指导价且大于0`
        } else if (Number(item.salePrice) <= 0) {
          // return `请修改规格${item.specs}中实售价且大于0`
          return `请修改实售价且大于0`
        } else if (Number(item.salePrice) > Number(item.price)) {
          return `商品实售价大于指导价`
        } else {
          return ''
        }
      })
      .filter(Boolean)
    if (skuError.length > 0) {
      ElMessage.error(skuError[0])
      return false
    }
  } else {
    ElMessage.error('请完善商品规格信息')
    return false
  }
  return true
}
const navToList = () => {
  $router.replace('/goods/purchase')
}
const radio = ref<'SINGLE_SPEC' | 'MUTI_SPEC'>('SINGLE_SPEC')
const changeSpecificationType = (e: 'SINGLE_SPEC' | 'MUTI_SPEC') => {
  radio.value = e
}
const submitHandle = async () => {
  const rawForm = cloneDeep(submitForm.value)
  // 如果是单规格
  if (radio.value === 'SINGLE_SPEC') {
    rawForm.specGroups = []
    rawForm.skus = rawForm.skus.filter((item, index) => {
      item.specs = []
      return index === 0
    })
  }
  // 店铺分类id为数组转为string
  if (Array.isArray(rawForm.categoryId)) {
    rawForm.categoryId = rawForm.categoryId[2]
  }
  // 平台类目id为数组转为string
  if (Array.isArray(rawForm.platformCategoryId)) {
    rawForm.platformCategoryId = rawForm.platformCategoryId[2]
  }
  // 处理价格以厘作为单位
  rawForm.skus = rawForm.skus.map((item) => {
    item.price = mulTenThousand(item.price).toNumber()
    item.salePrice = mulTenThousand(item.salePrice).toNumber()
    return item
  })
  // 处理单规格商品图片
  if (rawForm.specGroups.length === 0) {
    rawForm.skus[0].image = rawForm.albumPics.split(',')[0]
  }
  rawForm.status = 'SELL_ON'
  rawForm.supplierId = $route.query.supplierId as string
  rawForm.sellType = 'PURCHASE'
  rawForm.id = $route.query.id
  if (rawForm.productType === 'VIRTUAL_PRODUCT') {
    rawForm.distributionMode = ['VIRTUAL']
  }
  const { code } = await doReleaseGood(rawForm)
  if (code !== 200) return ElMessage.error('上传商品失败')
  ElMessage.success('上传成功')
  tempArr[0].resetServiceAssuranceList()
  $router.replace('/goods/purchase')
}
</script>

<template>
  <div class="release fdc1 overh">
    <div class="release__step">
      <el-steps :active="stepIndex" align-center>
        <el-step title="1.编辑基本信息"></el-step>
        <el-step title="2.编辑销售信息"></el-step>
        <el-step title="3.编辑商品信息"></el-step>
      </el-steps>
    </div>
    <div class="fdc1 overh scroll">
      <!-- 排除富文本组件 否则富文本组件将报错 -->
      <keep-alive :exclude="['NewProductInfoAddon']">
        <component
          :is="reactiveComponent[currentStep]"
          ref="componentRef"
          :VITE_BASE_URL="VITE_BASE_URL"
          @changeSpecificationType="changeSpecificationType"
        ></component>
      </keep-alive>
    </div>
    <div class="release__tool">
      <el-button v-if="prevStep !== ''" type="primary" round @click="preHandle">上一步</el-button>
      <el-button v-if="nextStep !== ''" type="primary" round @click="nextHandle">下一步</el-button>
      <el-button v-if="nextStep === '' && submitForm?.status === 'PLATFORM_SELL_OFF'" type="primary" round @click.once="navToList"> 返回 </el-button>
      <el-button v-if="nextStep === '' && submitForm?.status !== 'PLATFORM_SELL_OFF'" type="primary" round @click="submitHandle">
        {{ $route.query.id ? '更新' : '上架' }}
      </el-button>
    </div>
  </div>
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
@include b(release) {
  .scroll {
    overflow-y: scroll;
    padding-left: 16px;
    padding-right: 16px;
    padding-top: 30px;
    padding-bottom: 10px;
  }
  @include e(step) {
    width: 100%;
    margin: 0 auto;
    position: sticky;
    top: 0;
    padding-top: 16px;
    background: white;
    z-index: 100;
    padding-bottom: 10px;
  }
  @include e(tool) {
    @include flex;
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
}

// @Override css
:deep(.el-step__head.is-process) {
  color: #fff;
  .el-step__icon.is-text {
    background-color: #4b80ff;
    border-color: #4b80ff;
  }
}
:deep(.el-step__title.is-process) {
  font-weight: 400;
}
:deep(.el-step__title.is-finish) {
  color: var(--el-text-color-primary);
}
:deep(.el-step__head.is-finish) {
  color: #fff;
  .el-step__icon.is-text {
    background-color: #4b80ff;
    border-color: #4b80ff;
  }
}
:deep(.el-step__head.is-wait) {
  color: #fff;
  .el-step__icon.is-text {
    background-color: #d7dce4;
    border-color: #d7dce4;
  }
}
:deep(.el-step.is-center .el-step__line) {
  left: 65%;
  right: -30%;
}
</style>
