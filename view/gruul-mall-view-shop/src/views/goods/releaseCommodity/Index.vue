<template>
    <div class="release">
        <div class="release__step">
            <el-steps :active="stepIndex" align-center>
                <el-step title="编辑基本信息" />
                <el-step title="编辑销售信息" />
                <el-step title="编辑商品信息" />
            </el-steps>
        </div>
        <!-- 排除富文本组件 否则富文本组件将报错 -->
        <div class="tab_container">
            <keep-alive :exclude="['NewProductInfo']">
                <component
                    :is="reactiveComponent[currentStep as keyof typeof reactiveComponent]"
                    ref="componentRef"
                    @change-instance="changeInstance"
                    @changeSpecificationType="changeSpecificationType"
                ></component>
            </keep-alive>
        </div>
        <div class="release__tool">
            <el-button v-if="prevStep !== ''" type="primary" round @click="preHandle">上一步</el-button>
            <el-button v-if="nextStep !== ''" type="primary" round @click="nextHandle">下一步</el-button>
            <el-button v-if="nextStep === '' && submitForm?.status === 'PLATFORM_SELL_OFF'" type="primary" round @click.once="navToList">
                返回
            </el-button>
            <QSafeBtn v-if="nextStep === '' && submitForm?.status !== 'PLATFORM_SELL_OFF'" round type="primary" @click="submitHandle">
                {{ $route.query.id ? '发布' : '上架' }}
            </QSafeBtn>
        </div>
    </div>
</template>
<script lang="ts" name="ReleaseCommodity" setup>
import { useRoute, onBeforeRouteLeave, useRouter } from 'vue-router'
import { cloneDeep } from 'lodash-es'
import { doReleaseGood, doUpdateCommodity } from '@/apis/good'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { SubmitFormInterface } from './types'
import Storage from '@/utils/Storage'

const currentStep = ref('NewBasicInfo')
const $route = useRoute()
const $router = useRouter()
const storageLocal = () => new Storage()

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
            stockType: 'LIMITED',
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
    sellType: 'OWN',
})
const commodityImgList = ref([])
const copyGoodsTB = ref('')
const copyGoodsAL = ref('')
const copyGoodsJD = ref('')
const editProductPreviousStep = ref(false)
const productLabel = ref('')
provide('form', {
    submitForm,
    commodityImgList,
    copyGoodsJD,
    copyGoodsAL,
    copyGoodsTB,
    editProductPreviousStep,
    productLabel,
})
const componentRef = ref<(typeof reactiveComponent)[keyof typeof reactiveComponent]>()
// 动态组件列表 MERCHANT
const reactiveComponent = {
    NewBasicInfo: defineAsyncComponent(() => import('./components/NewBasicInfo.vue')),
    NewSaleInfo: defineAsyncComponent(() => import('./components/NewSaleInfo.vue')),
    NewProductInfo: defineAsyncComponent(() => import('./components/NewProductInfo.vue')),
}
const changeInstance = (instance: any) => {
    componentRef.value = instance
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
const radio = ref<'SINGLE_SPEC' | 'MUTI_SPEC'>('SINGLE_SPEC')
const changeSpecificationType = (e: 'SINGLE_SPEC' | 'MUTI_SPEC') => {
    radio.value = e
}

// 获取去别的页面之前保存的数据
onMounted(() => {
    // 获取来源页面的URL
    const previousPage = window.history.state?.forward || ''
    if (previousPage.includes('freight/logistics?from=releaseGoods')) {
        submitForm.value = storageLocal().getItem('releaseCommodity')
    }
})
onBeforeRouteLeave((to, from, next) => {
    const cacheWhiteList = ['/goods/category', '/goods/supplier', '/goods/attribute', '/freight/logistics']

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
            collectionUrl: '',
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
                    stockType: 'LIMITED',
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
            sellType: 'OWN',
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
    if (to.path === '/freight/logistics') {
        storageLocal().setItem(`releaseCommodity`, { ...submitForm.value })
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
    editProductPreviousStep.value = true
    productLabel.value = submitForm.value.labelId
}
// 下一步
const nextHandle = async () => {
    let passStatus = true
    // 编辑销售信息的多规格校验
    if (nextStep.value === 'NewProductInfo') {
        const spacObj = handleGetComponent()?.GoodOnlyRef.handleSpacObj()
        if (radio.value === 'MUTI_SPEC' && submitForm.value.skus?.length === 0) {
            return ElMessage.error({ message: '请至少添加一组规格信息' })
        } else if (
            radio.value === 'MUTI_SPEC' &&
            submitForm.value.specGroups.some((v) => v.children?.length) &&
            !submitForm.value.specGroups.some((v) => v.name)
        ) {
            return ElMessage.error({ message: '请填写规格名称' })
        }
        if (spacObj) {
            const { productAttribute, productParameter } = spacObj
            if (productAttribute.find((item: any) => !item.id)) {
                return ElMessage.error({ message: '存在未选择的属性名称' })
            }
            if (productParameter.find((item: any) => !item.id)) {
                return ElMessage.error({ message: '存在未选择的商品参数' })
            }
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
                    // return `请修改规格${item.specs}中划线价且大于0`
                    return `请修改划线价且大于0`
                } else if (Number(item.salePrice) <= 0) {
                    // return `请修改规格${item.specs}中销售价且大于0`
                    return `请修改销售价且大于0`
                } else if (Number(item.salePrice) > Number(item.price)) {
                    return `划线价应大于等于销售价`
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
    $router.replace('/goods/list')
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
    // 处理虚拟商品
    if (rawForm.productType === 'VIRTUAL_PRODUCT' && rawForm.distributionMode.length === 0) {
        rawForm.distributionMode = ['VIRTUAL']
    }
    const isCopy = $route.name === 'releaseCommodityCopy'
    const isEdit = !!$route.query.id
    rawForm.status = 'SELL_ON'
    if (isCopy) {
        // 发布新商品
        rawForm.id = ''
        rawForm.specGroups.forEach((item) => {
            item.id = ''
            if (item.children?.length) {
                item.children?.forEach((chil) => {
                    chil.id = ''
                })
            }
        })
        rawForm.skus.forEach((item) => {
            item.id = ''
            item.productId = ''
        })
        rawForm.sellType = 'OWN'
        const { code, data } = await doReleaseGood(rawForm)
        if (code !== 200) return ElMessage.error('上传商品失败')
        ElMessage.success(data || '上传成功')
        tempArr[0].resetServiceAssuranceList?.()
        $router.replace('/goods/list')
        submitForm.value.labelId = ''
        console.log('发布新商品结束', submitForm.value)
    } else if (isEdit) {
        // 编辑商品
        if (rawForm['extra']) {
            delete rawForm.extra
            console.log('rawForm', rawForm)
        }
        const { code, msg } = await doUpdateCommodity(rawForm)
        if (code !== 200) return ElMessage.error(msg || '更新商品失败')
        ElMessage.success('更新成功')
        tempArr[0].resetServiceAssuranceList?.()
        $router.replace({ path: '/goods/list', query: { current: $route.query.current } })
        submitForm.value.labelId = ''
        console.log('编辑商品结束', submitForm.value)
    } else {
        rawForm.sellType = 'OWN'
        const { code, data } = await doReleaseGood(rawForm)
        if (code !== 200) return ElMessage.error('上传商品失败')
        ElMessage.success(data || '上传成功')
        tempArr[0].resetServiceAssuranceList?.()
        $router.replace('/goods/list')
        submitForm.value.labelId = ''
        console.log('else商品结束', submitForm.value)
    }
    submitForm.value.labelId = ''
    console.log('all商品结束', submitForm.value)
}
</script>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';

@include b(navLine) {
    margin: 25px 0;
    height: 40px;
    line-height: 40px;
    background-color: #f8f8f8;
    padding-left: 15px;
    font-weight: 700;
}
@include b(release) {
    width: 100%;
    box-sizing: border-box;
    padding-bottom: 62px;
    overflow: hidden;
    overflow-y: auto;
    scrollbar-width: none;
    @include e(step) {
        width: 400px;
        margin: 0 auto;
    }
    @include e(tool) {
        width: 100%;
        @include flex;
        position: absolute;
        bottom: 0px;
        padding: 15px 0px;
        display: flex;
        justify-content: center;
        box-shadow: 0 0px 10px 0px #d5d5d5;
        background-color: white;
        z-index: 100;
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
