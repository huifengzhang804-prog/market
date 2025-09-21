<template>
    <div ref="AddShops" class="shopForm">
        <el-steps :active="stepIndex" simple class="shopForm_steps">
            <el-step title="1.基本信息" icon="none"></el-step>
            <el-step title="2.信息登记" icon="none"></el-step>
            <el-step title="3.收款账户" icon="none"></el-step>
        </el-steps>
        <keep-alive>
            <component
                :is="reactiveComponent[currentStep]"
                ref="componentRef"
                :supplier-view-model="supplierViewModel"
                @map-change="handleMapChange"
            ></component>
        </keep-alive>
    </div>
    <div class="shopForm__tool">
        <el-button v-if="prevStep !== ''" type="primary" round @click="preHandle">上一步</el-button>
        <el-button v-if="nextStep !== ''" type="primary" round @click="nextHandle">下一步</el-button>
        <el-button v-else type="primary" round @click="$router.back()"> 返回 </el-button>
    </div>
</template>

<script lang="ts" setup>
import type { Ref, Component } from 'vue'
import type { FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { RegisterInfo } from './types'
import { doGetShopDetail, doGetShopSigningCategoryList } from '@/apis/shops'

interface AsyncComponent {
    NewShopBase: Component
    NewShopInfo: Component
    NewShopFinance: Component
}
type EnumStep = 'NewShopBase' | 'NewShopInfo' | 'NewShopFinance'
const componentRef = ref()
const $route = useRoute()
const $router = useRouter()
const firstAssignment = ref(true)
const currentStep = ref<EnumStep>('NewShopBase')

// 供应商视图模式
const supplierViewModel = computed(() => ($route.path.includes('supplier') ? '供应商' : '店铺'))
// 当前步骤
// 动态组件列表
const reactiveComponent: AsyncComponent = {
    NewShopBase: defineAsyncComponent(() => import('./components/NewShopBase.vue')),
    NewShopInfo: defineAsyncComponent(() => import('./components/NewShopInfo.vue')),
    NewShopFinance: defineAsyncComponent(() => import('./components/NewShopFinance.vue')),
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

onMounted(() => {
    initShopForm()
})
// 测试数据
const submitForm = ref<any>({
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
    location: { type: 'Point', coordinates: ['121.583336', '29.990282'] },
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
    shopType: '',
    signingCategory: [],
    mode: 'COMMON',
    shopMode: 'COMMON',
})

const handleMapChange = (e: { address: string; position: string[] }) => {
    // 编辑状态下初始化不对详细地址进行重写
    if ($route.query.shopId && firstAssignment.value) {
        firstAssignment.value = false
        return
    }
    submitForm.value.address = e.address
    submitForm.value.location.coordinates = e.position
}
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
async function initShopForm() {
    const res = await doGetShopDetail($route.query.shopId)
    if (res.code === 200) {
        submitForm.value = { ...submitForm.value, ...JSON.parse(JSON.stringify(res.data)) }
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
        const { code, success, data } = await doGetShopSigningCategoryList({ shopId: submitForm.value.id })
        if (success) {
            const signingCategoryList = data?.map((item: any) => ({
                id: item.id,
                firstName: item.parentName,
                parentId: item.parentId,
                customDeductionRatio: item.customDeductionRatio,
                name: item.currentCategoryName,
                currentCategoryId: item.currentCategoryId,
                deductionRatio: item?.deductionRatio,
            }))
            submitForm.value.signingCategory = signingCategoryList
        }
    }
}
// 暴露属性
provide('addShops', {
    submitForm,
})
</script>
<style lang="scss">
@include b(shopForm) {
    overflow: scroll;
    // min-height: 800px;

    .shopForm_steps {
        position: sticky;
        top: 0;
        z-index: 1000;
    }
}
@include b(shopForm__tool) {
    width: 100%;
    padding: 15px 0px;
    display: flex;
    justify-content: center;
    box-shadow: 0 0px 10px 0px #d5d5d5;
    background-color: white;
    margin-top: auto;
}
</style>
