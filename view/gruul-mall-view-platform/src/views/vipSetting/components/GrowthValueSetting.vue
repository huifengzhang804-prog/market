<script lang="ts" setup>
import { doGetGrowthValueSettings, doPostGrowthValueSettings } from '@/apis/vip/index'
import { validateForm } from '@/utils/util'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import type { Ref } from 'vue'
interface GrowthValueFormInterface {
    id?: number | string
    registerEnabled: boolean
    registerGrowthValue: number
    consumeEnabled: boolean
    consumeJson: {
        id?: number
        isSelected: boolean
        consumeGrowthValueType: 'ORDER_QUANTITY' | 'ORDER_AMOUNT'
        orderQuantityAndAmount?: number
        presentedGrowthValue?: number
    }[]
}
const isLoading = ref(false)
const growthValueFormRef: Ref<FormInstance | null> = ref(null)
const growthValueFormModel: GrowthValueFormInterface = reactive({
    id: '',
    registerEnabled: true,
    registerGrowthValue: 0,
    consumeEnabled: true,
    consumeJson: [],
})
const getGrowthValueInfo = async () => {
    const result = await doGetGrowthValueSettings()
    if (result?.success) {
        growthValueFormModel.registerEnabled = result.data?.registerEnabled || false
        growthValueFormModel.id = result.data?.id || ''
        growthValueFormModel.registerGrowthValue = result?.data?.registerGrowthValue
        growthValueFormModel.consumeEnabled = result?.data?.consumeEnabled
        if (!result?.data?.consumeJson?.length) {
            growthValueFormModel.consumeJson.push({ isSelected: false, consumeGrowthValueType: 'ORDER_QUANTITY' })
            growthValueFormModel.consumeJson.push({ isSelected: false, consumeGrowthValueType: 'ORDER_AMOUNT' })
        } else {
            const consumeJson = result.data.consumeJson
            growthValueType.value = consumeJson.find((item: any) => item.isSelected)?.consumeGrowthValueType
            growthValueFormModel.consumeJson = result.data.consumeJson
        }
    }
}
const handleSaveGrowthValueSettings = async () => {
    isLoading.value = true
    try {
        await validateForm(growthValueFormRef.value)
        const consumeJson = growthValueFormModel.consumeJson
        const idx = consumeJson.findIndex((item) => item.consumeGrowthValueType === growthValueType.value)
        growthValueFormModel.consumeJson.forEach((item) => (item.isSelected = false))
        if (idx > -1) {
            growthValueFormModel.consumeJson[idx].isSelected = true
        }
        const result = await doPostGrowthValueSettings(growthValueFormModel)
        if (result.success) {
            ElMessage.success({ message: result.msg || '修改成功' })
        } else {
            ElMessage.error({ message: result.msg || '修改失败' })
        }
        getGrowthValueInfo()
    } finally {
        isLoading.value = false
    }
}
const numberExp = /^[0-9]+$/
const growthValueFormRules: FormRules = {
    registerGrowthValue: [
        {
            validator: (_, val) => {
                if (val && !numberExp.test(val)) {
                    return new Error('请输入大于等于零的整数')
                }
                if (growthValueFormModel.registerEnabled) {
                    if (!val) {
                        return new Error('请输入注册获得的成长值')
                    }
                }

                return true
            },
            trigger: 'blur',
        },
    ],
    'consumeJson[0][orderQuantityAndAmount]': [
        {
            validator: (_, val) => {
                if (val && !numberExp.test(val)) {
                    return new Error('请输入大于等于零的整数')
                }
                if (growthValueType.value === 'ORDER_QUANTITY' && !val) {
                    return new Error('请输入订单数')
                }
                return true
            },
            trigger: 'blur',
        },
    ],
    'consumeJson[0][presentedGrowthValue]': [
        {
            validator: (_, val) => {
                if (val && !numberExp.test(val)) {
                    return new Error('请输入大于等于零的整数')
                }
                if (growthValueType.value === 'ORDER_QUANTITY' && !val) {
                    return new Error('请输入奖励成长值')
                }
                return true
            },
            trigger: 'blur',
        },
    ],
    'consumeJson[1][orderQuantityAndAmount]': [
        {
            validator: (_, val) => {
                if (val && !numberExp.test(val)) {
                    return new Error('请输入大于等于零的整数')
                }
                if (growthValueType.value === 'ORDER_AMOUNT' && !val) {
                    return new Error('请输入订单数')
                }
                return true
            },
            trigger: 'blur',
        },
    ],
    'consumeJson[1][presentedGrowthValue]': [
        {
            validator: (_, val) => {
                if (val && !numberExp.test(val)) {
                    return new Error('请输入大于等于零的整数')
                }
                if (growthValueType.value === 'ORDER_AMOUNT' && !val) {
                    return new Error('请输入奖励成长值')
                }
                return true
            },
            trigger: 'blur',
        },
    ],
}
const growthValueType = ref(undefined)
onMounted(() => getGrowthValueInfo())
</script>

<template>
    <div class="content_container">
        <el-form ref="growthValueFormRef" :model="growthValueFormModel" :rules="growthValueFormRules">
            <el-row :gutter="16">
                <el-col :span="24">
                    <el-form-item>
                        <!-- <el-checkbox v-model="growthValueFormModel.registerEnabled"> -->
                        <span>注册获得</span>
                        <span class="sub-title">客户注册成功即可获得对应成长值</span>
                        <!-- </el-checkbox> -->
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="22" style="margin-left: 70px">
                    <el-form-item>
                        <span>注册成功获得 </span>
                        <el-form-item prop="registerGrowthValue" class="inline-form-item">
                            <el-input-number v-model="growthValueFormModel.registerGrowthValue" :min="1" :max="99999999" :controls="false" />
                        </el-form-item>
                        <span> 成长值</span>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="16">
                <el-col :span="24">
                    <el-form-item>
                        <!-- <el-checkbox v-model="growthValueFormModel.consumeEnabled"> -->
                        <span>消费获得</span>
                        <span class="sub-title"
                            >成功交易笔数及实付金额只统计订单状态为【已完成】的数值，且将【已结算订单、积分订单、储值充值】剔除</span
                        >
                        <!-- </el-checkbox> -->
                    </el-form-item>
                </el-col>
            </el-row>
            <el-radio-group v-model="growthValueType" style="flex-direction: column">
                <div v-for="(item, index) in growthValueFormModel.consumeJson" :key="index">
                    <el-row v-if="item.consumeGrowthValueType === 'ORDER_AMOUNT'">
                        <el-col :span="22" style="margin-left: 70px">
                            <el-form-item>
                                <el-radio v-model="item.isSelected" value="ORDER_AMOUNT">
                                    <span>每消费</span>
                                    <el-form-item prop="consumeJson[0][orderQuantityAndAmount]" class="inline-form-item">
                                        <el-input v-model="item.orderQuantityAndAmount" />
                                    </el-form-item>
                                    <span>元金额，获得</span>
                                    <el-form-item prop="consumeJson[0][presentedGrowthValue]" class="inline-form-item">
                                        <el-input v-model="item.presentedGrowthValue" />
                                    </el-form-item>
                                    <span>成长值</span>
                                </el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row v-if="item.consumeGrowthValueType === 'ORDER_QUANTITY'">
                        <el-col :span="22">
                            <el-form-item style="margin-left: 70px">
                                <el-radio v-model="item.isSelected" value="ORDER_QUANTITY">
                                    <span>每完成</span>
                                    <el-form-item prop="consumeJson[1][orderQuantityAndAmount]" class="inline-form-item">
                                        <el-input v-model="item.orderQuantityAndAmount" />
                                    </el-form-item>
                                    <span>笔订单，获得</span>
                                    <el-form-item prop="consumeJson[1][presentedGrowthValue]" class="inline-form-item">
                                        <el-input v-model="item.presentedGrowthValue" />
                                    </el-form-item>
                                    <span>成长值</span>
                                </el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
            </el-radio-group>
        </el-form>
    </div>
    <div class="operation">
        <el-button type="primary" :loading="isLoading" @click="handleSaveGrowthValueSettings">保 存</el-button>
    </div>
</template>

<style scoped>
:deep(.el-input) {
    width: 150px;
}
span {
    color: #333;
}
.sub-title {
    color: #999;
    font-size: 12px;
    margin-left: 15px;
}
.el-input {
}
.inline-form-item {
    display: inline-block;
    margin: 0 10px;
}
.operation {
    height: 52px;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 -3px 8px rgba(0, 0, 0, 0.1);
    margin-top: auto;
}
</style>
