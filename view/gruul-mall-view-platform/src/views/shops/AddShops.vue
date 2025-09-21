<template>
    <div ref="AddShops" class="shopForm fdc1">
        <el-steps :active="stepIndex" simple class="shopForm__steps">
            <el-step :title="`1.基本信息`" icon="none"></el-step>
            <el-step title="2.信息登记" icon="none"></el-step>
            <el-step title="3.收款账户" icon="none"></el-step>
        </el-steps>
        <keep-alive>
            <component :is="reactiveComponent[currentStep]" ref="componentRef" class="f1" :supplier-view-model="supplierViewModel" />
        </keep-alive>
        <div class="shopForm__tool">
            <el-button v-if="prevStep !== ''" type="primary" round @click="preHandle">上一步</el-button>
            <el-button v-if="nextStep !== ''" type="primary" round @click="nextHandle">下一步</el-button>
            <template v-if="$route.name !== 'previewShop'">
                <el-button v-if="nextStep === ''" type="primary" round :loading="submitLoading" @click="submitHandle">
                    {{ $route.query.shopId ? '更新' : '保存' }}
                </el-button>
                <el-button v-if="nextStep === ''" type="primary" round @click="handleCancel"> 取消 </el-button>
            </template>
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { Ref, Component } from 'vue'
import { onBeforeRouteLeave } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { doAddShops, doEditShop, doShopAudit, doGetShopSigningCategoryList, doGetShopDetail } from '@/apis/shops'
import { ShopFormType, RegisterInfo, BankAccountType } from './types'
import { cloneDeep } from 'lodash-es'
import { GeometryType } from '@/apis/afs/type'

interface AsyncComponent {
    NewShopBase: Component
    NewShopInfo: Component
    NewShopFinance: Component
}
type EnumStep = 'NewShopBase' | 'NewShopInfo' | 'NewShopFinance'
const componentRef = ref()
const $router = useRouter()
const $route = useRoute()
const firstAssignment = ref(true)
const submitLoading = ref(false)
// 供应商视图模式
const supplierViewModel = computed(() => ($route.path.includes('supplier') ? '供应商' : '店铺'))
const navBackUrl = computed(() => ($route.path.includes('supplier') ? 'supplierPage' : 'shopList'))

let isManual = false
// 当前步骤
const currentStep = ref<EnumStep>('NewShopBase')
// 动态组件列表
const reactiveComponent: AsyncComponent = {
    NewShopBase: defineAsyncComponent(() => import('./components/NewShopBase.vue')),
    NewShopInfo: defineAsyncComponent(() => import('./components/NewShopInfo.vue')),
    NewShopFinance: defineAsyncComponent(() => import('./components/NewShopFinance.vue')),
}
onMounted(() => {
    initShopForm()
})
// 商铺添加步骤
const stepIndicator = {
    NewShopBase: {
        prev: '',
        next: 'NewShopInfo',
        stepIndex: 0,
    },
    NewShopInfo: {
        prev: 'NewShopBase',
        next: 'NewShopFinance',
        stepIndex: 1,
    },
    NewShopFinance: {
        prev: 'NewShopInfo',
        next: '',
        stepIndex: 2,
    },
}
// 当前添加步骤
const currentStepIndicator = computed(() => {
    return stepIndicator[currentStep.value]
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
// 测试数据
const submitForm = ref<ShopFormType>({
    area: [] as unknown as [string, string, string?],
    companyName: '',
    address: '',
    bankAcc: '',
    bankAccount: {
        payee: '',
        bankName: '',
        openAccountBank: '',
        bankAcc: '',
    },
    bankName: '',
    briefing: '',
    contractNumber: '',
    legalPersonIdBack: '',
    legalPersonIdFront: '',
    license: '',
    location: { type: GeometryType.Point, coordinates: [] as unknown as [number, number] },
    logo: '',
    name: '',
    openAccountBank: '',
    payee: '',
    registerInfo: {
        license: '',
        legalPersonIdFront: '',
        legalPersonIdBack: '',
    },
    userId: '',
    shopType: 'ORDINARY',
    signingCategory: [],
    extractionType: 'CATEGORY_EXTRACTION',
    drawPercentage: '',
    shopMode: 'COMMON',
    fakeAddress: '',
})

const tempArr: Ref<FormInstance>[] = []
/**
 * 存储组件实例
 * @param {*} component
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

// 上一步
const preHandle = () => {
    handleSaveComponent(componentRef.value)
    currentStep.value = prevStep.value as EnumStep
}
// 下一步
const nextHandle = () => {
    handleGetComponent().currentFormRef.validate((valid: any) => {
        if (valid) {
            handleSaveComponent(componentRef.value)
            currentStep.value = nextStep.value as EnumStep
        }
    })
}
const handleCancel = () => {
    $router.push({
        name: navBackUrl.value,
    })
}
const submitHandle = async () => {
    submitLoading.value = true
    const isEdit = !!$route.query.shopId
    const { license, legalPersonIdFront, legalPersonIdBack, payee, bankName, openAccountBank, bankAcc } = submitForm.value
    submitForm.value.registerInfo = {
        license,
        legalPersonIdFront,
        legalPersonIdBack,
    }
    submitForm.value.bankAccount = {
        payee,
        bankName,
        openAccountBank,
        bankAccount: bankAcc,
    }
    if (supplierViewModel.value === '供应商') {
        submitForm.value.shopMode = 'SUPPLIER'
    }
    try {
        await handleGetComponent().currentFormRef.validate(async (valid: any) => {
            if (valid) {
                if (isEdit) {
                    switch ($route.query.type) {
                        case 'EDIT':
                            await handleEditShop(submitForm.value)
                            break
                        default:
                            await throughApplication(submitForm.value, $route.query.type as 'EDIT' | 'through')
                            break
                    }
                    return
                }
                const cloneParams = cloneDeep(submitForm.value)
                cloneParams.address = `${cloneParams.address}~${cloneParams.fakeAddress}`
                cloneParams.signingCategory = cloneParams.signingCategory?.map((item: any) => ({
                    id: item.id,
                    parentId: item.parentId,
                    currentCategoryId: item.currentCategoryId,
                    customDeductionRatio: item.customDeductionRatio,
                }))
                const { code, msg } = await doAddShops(cloneParams).catch((err) => {
                    submitLoading.value = false
                    return err
                })
                if (code !== 200) {
                    submitLoading.value = false
                    if (code === 100003) {
                        return ElMessage.error(msg || '添加失败')
                    }
                    return ElMessage.error(msg || '添加失败')
                }
                let time = setTimeout(() => {
                    submitLoading.value = false
                    isManual = true
                    $router.push({
                        name: navBackUrl.value,
                    })
                    clearTimeout(time)
                }, 2000)
            } else {
                submitLoading.value = false
            }
        })
    } finally {
        submitLoading.value = false
    }
}
/**
 * 处理编辑 等待接收
 * @param {*} params
 */
async function handleEditShop(params: any) {
    const cloneParams = cloneDeep(params)
    cloneParams.address = `${cloneParams.address}~${cloneParams.fakeAddress}`
    cloneParams.signingCategory = cloneParams.signingCategory?.map((item: any) => ({
        id: item.id,
        parentId: item.parentId,
        currentCategoryId: item.currentCategoryId,
        customDeductionRatio: item.customDeductionRatio,
    }))
    const { code, success, msg } = await doEditShop(cloneParams)
    if (code === 200 && success === true) {
        ElMessage.success('更新成功')
        isManual = true
        $router.push({
            name: navBackUrl.value,
        })
    } else {
        ElMessage.error(msg)
    }
}
/**
 * 通过审核
 */
async function throughApplication(params: any, type: 'EDIT' | 'through') {
    if (type === 'through') {
        const cloneParams = cloneDeep(params)
        cloneParams.address = `${cloneParams.address}~${cloneParams.fakeAddress}`
        cloneParams.signingCategory = cloneParams.signingCategory?.map((item: any) => ({
            id: item.id,
            parentId: item.parentId,
            currentCategoryId: item.currentCategoryId,
            customDeductionRatio: item.customDeductionRatio,
        }))
        const { code: c } = await doEditShop(cloneParams)
        if (c !== 200) return ElMessage.error('通过申请失败')
        const query = { shopId: params, passFlag: true }
        const { code, success } = await doShopAudit(query)
        if (code !== 200) return ElMessage.error('通过申请失败')
        isManual = true
        $router.push({
            name: navBackUrl.value,
        })
    }
}
async function initShopForm() {
    if (!$route.query.shopId) {
        return
    }
    const res = await doGetShopDetail($route.query.shopId)
    if (res.code === 200) {
        submitForm.value = JSON.parse(JSON.stringify(res.data))
        submitForm.value.fakeAddress = res.data.address.split('~')[2] || ''
        submitForm.value.address = `${res.data.address.split('~')[0] || ''}~${res.data.address.split('~')[1] || ''}`
        const { registerInfo, bankAccount, address } = submitForm.value
        for (let item in registerInfo) {
            submitForm.value[item as keyof RegisterInfo] = registerInfo[item as keyof typeof registerInfo]
        }
        for (let item in bankAccount) {
            if (item !== 'bankAccount') {
                submitForm.value[item as keyof typeof submitForm.value] = bankAccount[item as keyof typeof bankAccount]
            }
        }
        submitForm.value['address'] = address
        // 结局bankAccount重名问题 重新赋值
        submitForm.value['bankAcc' as keyof typeof submitForm.value] = bankAccount?.bankAccount
        // 获取对应的类目列表
        const { code, success, data } = await doGetShopSigningCategoryList({ shopId: submitForm.value.id })
        if (success) {
            const signingCategoryList = data?.map((item: any) => ({
                id: item.id,
                firstName: item.parentName,
                parentId: item.parentId,
                customDeductionRatio: item.customDeductionRatio,
                name: item.currentCategoryName,
                currentCategoryId: item.currentCategoryId,
                deductionRatio: item.deductionRatio,
            }))
            submitForm.value.signingCategory = signingCategoryList
        }
    }
}
// 暴露属性
provide('addShops', {
    submitForm,
})

// FIXME 操作浏览器回退无法生成Message组件
// 离开页面提示
onBeforeRouteLeave((to, from, next) => {
    if (!isManual) {
        ElMessageBox.confirm('确定退出新增商铺页面?退出后，未保存的信息将不会保留!', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
            .then(() => {
                next()
            })
            .catch(() => {
                next(false)
            })
    } else {
        next()
    }
})
</script>
<style lang="scss">
@include b(shopForm) {
    overflow-y: auto;
    overflow-x: hidden;
    // min-height: 800px;
    @include e(tool) {
        width: 100%;
        @include flex();
        bottom: 10px;
        padding: 15px 0px;
        display: flex;
        justify-content: center;
        box-shadow: 0 0px 10px 0px #d5d5d5;
        background-color: white;
        z-index: 999;
        position: sticky;
        bottom: 0;
    }
    .shopForm__steps {
        position: sticky;
        top: 0;
        z-index: 1000;
    }
}
</style>
