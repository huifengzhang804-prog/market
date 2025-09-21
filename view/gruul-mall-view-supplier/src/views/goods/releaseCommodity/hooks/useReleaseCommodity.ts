import { ElMessage, FormInstance } from 'element-plus'
import { SubmitFormInterface } from '../types'
import { cloneDeep } from 'lodash-es'
import { doReleaseGood, doUpdateCommodity } from '@/apis'
/**
 * @description 基础表单提交数据信息
 * @type { SubmitFormInterface }
 */
const basicSubmitForm: SubmitFormInterface = {
    name: '',
    saleDescribe: '',
    platformCategoryId: '',
    sellType: 'PURCHASE',
    widePic: '',
    distributionMode: [],
    videoUrl: '',
    albumPics: '',
    productType: 'REAL_PRODUCT',
    specGroups: [],
    collectionUrl: '',
    //平台类目 1 2 3级id
    platformCategory: {
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
            minimumPurchase: 1,
            specs: [],
        },
    ],
    productParameters: [],
    productAttributes: [],
    serviceIds: [],
    detail: '',
    freightTemplateId: 0,
    status: 'SELL_ON',
    brandId: '',
}
const { mulTenThousand } = useConvert()
/**
 * @description 校验表单中的sku信息
 * @param skus sku信息
 * @returns { boolean }
 */
function validateTableFn(skus: SubmitFormInterface['skus'], submitForm: SubmitFormInterface) {
    if (skus.length > 0) {
        console.log('skus', skus)
        const skuError = skus
            .map((item) => {
                if (item.price <= 0) {
                    return `请修改划线价且大于0`
                } else if (Number(item.salePrice) <= 0) {
                    return `请修改供货价且大于0`
                } else if (Number(item.salePrice) > Number(item.price)) {
                    return `划线价应大于等于供货价`
                } else if (Number(item.minimumPurchase) <= 0 && submitForm.sellType !== 'CONSIGNMENT') {
                    return `请修改起批数且大于0`
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
const useReleaseCommodity = () => {
    const commodityImgList = ref([])
    const copyGoodsTB = ref('')
    const copyGoodsAL = ref('')
    const copyGoodsJD = ref('')

    const $router = useRouter()
    const $route = useRoute()
    const submitForm = ref<SubmitFormInterface>(cloneDeep(basicSubmitForm))
    /**
     * @description 动态组件列表
     */
    const reactiveComponent = {
        NewBasicInfo: defineAsyncComponent(() => import('../components/NewBasicInfo.vue')),
        NewSaleInfo: defineAsyncComponent(() => import('../components/NewSaleInfo.vue')),
        NewProductInfo: defineAsyncComponent(() => import('../components/NewProductInfo.vue')),
    }
    /**
     * @description 当前步骤的Tab的key字段，选项值为动态加载组件对象的key值
     */
    const currentStep = ref<keyof typeof reactiveComponent>('NewBasicInfo')
    const currentStepIndicator = computed(() => {
        return stepIndicator[currentStep.value]
    })
    // 异步组件的组件实例
    const componentRef = ref<(typeof reactiveComponent)[keyof typeof reactiveComponent]>()
    const stepIndex = computed(() => {
        return currentStepIndicator.value.stepIndex
    })
    // 用于储存组件实例信息
    const tempArr: Array<(typeof reactiveComponent)[keyof typeof reactiveComponent]> = []
    /**
     * @description 存储组件实例
     */
    const handleSaveComponent = () => {
        if (!tempArr[stepIndex.value]) {
            tempArr.push(componentRef.value as (typeof reactiveComponent)[keyof typeof reactiveComponent])
        }
    }
    const prevStep = computed(() => {
        return currentStepIndicator.value.prev as keyof typeof reactiveComponent | ''
    })
    const nextStep = computed(() => {
        return currentStepIndicator.value.next as keyof typeof reactiveComponent | ''
    })

    const handleGetComponent = () => {
        if (tempArr[stepIndex.value]) {
            return tempArr[stepIndex.value]
        } else {
            return componentRef.value
        }
    }
    // 点击执行上一步
    const preHandle = () => {
        handleSaveComponent()
        if (prevStep.value) {
            currentStep.value = prevStep.value
        }
    }
    const radio = ref<'SINGLE_SPEC' | 'MUTI_SPEC'>('SINGLE_SPEC')
    const changeSpecificationType = (e: 'SINGLE_SPEC' | 'MUTI_SPEC') => {
        radio.value = e
    }
    // 下一步
    const nextHandle = async () => {
        let passStatus = true
        // 编辑销售信息的多规格校验
        if (nextStep.value === 'NewProductInfo') {
            const specReslt = handleGetComponent()?.GoodOnlyRef.handleSpacObj()
            if (radio.value === 'MUTI_SPEC' && submitForm.value.skus?.length === 0) {
                return ElMessage.error({ message: '请至少添加一组规格信息' })
            } else if (
                radio.value === 'MUTI_SPEC' &&
                submitForm.value.specGroups.some((v) => v.children?.length) &&
                !submitForm.value.specGroups.some((v) => v.name)
            ) {
                return ElMessage.error({ message: '请填写规格名称' })
            }
            if (specReslt) {
                const { productAttribute, productParameter } = specReslt
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
                passStatus = validateTableFn(submitForm.value.skus, submitForm.value)
            }
        }

        if (passStatus && handleGetComponent()?.currentFormRef) {
            const currentFormRef: FormInstance = handleGetComponent()?.currentFormRef
            currentFormRef.validate((valid) => {
                if (valid) {
                    handleSaveComponent()
                    if (nextStep.value) {
                        currentStep.value = nextStep.value
                    }
                }
            })
        }
    }
    /**
     * @description 取消/返回列表页面
     */
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
        if (rawForm.productType === 'REAL_PRODUCT') {
            rawForm.distributionMode.push('EXPRESS')
        }
        if (rawForm.productType === 'VIRTUAL_PRODUCT') {
            rawForm.distributionMode.push('VIRTUAL')
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
        const isEdit = !!$route.query.id
        rawForm.status = 'SELL_ON'
        if (isEdit && !$route.query.isCopy) {
            if (rawForm['extra']) {
                delete rawForm.extra
                console.log('rawForm', rawForm)
            }
            const { code, msg } = await doUpdateCommodity(rawForm)
            if (code !== 200) return ElMessage.error(msg || '更新商品失败')
            ElMessage.success('更新成功')
            tempArr[0].resetServiceAssuranceList?.()
            navToList()
        } else {
            const { code, data } = await doReleaseGood(rawForm)
            if (code !== 200) return ElMessage.error('上传商品失败')
            ElMessage.success(data || '上传成功')
            tempArr[0].resetServiceAssuranceList?.()
            navToList()
        }
    }

    provide('form', {
        submitForm,
        commodityImgList,
        copyGoodsJD,
        copyGoodsAL,
        copyGoodsTB,
    })
    onBeforeRouteLeave((to, _, next) => {
        const cacheWhiteList = ['/goods/category', '/goods/supplier', '/goods/attribute', '/freight/logistics']
        if (!cacheWhiteList.includes(to.path)) {
            submitForm.value = cloneDeep(basicSubmitForm)
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
    return {
        submitForm,
        reactiveComponent,
        currentStep,
        componentRef,
        preHandle,
        nextHandle,
        nextStep,
        prevStep,
        stepIndex,
        navToList,
        submitHandle,
        radio,
        changeSpecificationType,
    }
}

export default useReleaseCommodity
