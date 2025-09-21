<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { doPostbalanceChange, doPostGrowthValueChange } from '@/apis/vip'
import type { FormInstance, FormRules } from 'element-plus'
import type { ApiBaseVipListItem } from '@/views/baseVip/types'
import type { PropType } from 'vue'
import type { R } from '@/apis/http.type'

const $props = defineProps({
    topUpBalance: { type: Boolean, default: false },
    baseVipListItem: { type: Object as PropType<ApiBaseVipListItem>, required: true },
    changeType: { type: String as PropType<'BALANCE' | 'GROWTHVALUE' | 'INTEGRAL'>, default: 'BALANCE' },
})
const $emit = defineEmits(['update:topUpBalance', 'reset'])
const { divTenThousand, mulTenThousand } = useConvert()
const modelFormData = reactive({
    num: 0,
    changeType: 'INCREASE' as 'INCREASE' | 'REDUCE',
})
const integralTotol = ref(0)
const ruleFormRef = ref<FormInstance>()

const handleSubmit = async () => {
    if (!ruleFormRef.value) return
    const isValidate = await ruleFormRef.value.validate()
    if (!isValidate) return
    // 积分没有小数不做处理
    const num = $props.changeType === 'BALANCE' ? mulTenThousand(modelFormData.num).toNumber() : modelFormData.num
    let requestFn: ((...args: any[]) => Promise<R<any>>) | null = null
    if ($props.changeType === 'BALANCE') {
        requestFn = doPostbalanceChange
    } else {
        requestFn = doPostGrowthValueChange
    }
    const { data, code } = await requestFn($props.baseVipListItem.userId, num, modelFormData.changeType)
    if (code !== 200) return ElMessage.error('操作失败')
    ElMessage.success('操作成功')
    integralTotol.value = 0
    modelFormData.num = 0
    modelFormData.changeType = 'INCREASE'
    $emit('update:topUpBalance', false)
    $emit('reset')
}
const handleClose = () => {
    modelFormData.num = 0
    modelFormData.changeType = 'INCREASE'
    $emit('update:topUpBalance', false)
}
const rules = computed<FormRules>(() => ({
    num: [
        { required: true, message: '请输入正确的数额' },
        { type: 'number', message: '请输入一个正确的数字' },
        {
            validator: (rule: any, value: any, callback: any) => {
                if ($props.changeType === 'BALANCE') {
                    callback()
                } else if (value < 1) {
                    callback(new Error('请输入正确的数额'))
                } else {
                    callback()
                }
            },
            trigger: 'blur',
        },
    ],
}))

const currentDialogTitle = computed(() => {
    const titleMap = { BALANCE: '储值', INTEGRAL: '积分', GROWTHVALUE: '成长值' }
    return `${titleMap[$props.changeType]}调整`
})
const precision = computed(() => ($props.changeType === 'BALANCE' ? 2 : void 0))
const maxInputNumber = computed(() => ($props.changeType === 'BALANCE' ? 9999999 : Infinity))
const minInputNumber = computed(() => ($props.changeType === 'BALANCE' ? 0.01 : 0))
</script>

<template>
    <el-dialog
        :model-value="$props.topUpBalance"
        class="label-view-dialog"
        :title="currentDialogTitle"
        width="40%"
        destroy-on-close
        @close="handleClose"
    >
        <el-form ref="ruleFormRef" :model="modelFormData" label-width="90px" :rules="rules">
            <el-form-item label="当前数值">
                <div v-if="$props.changeType === 'BALANCE'">{{ divTenThousand($props.baseVipListItem.balance) }}</div>
                <div v-else-if="$props.changeType === 'INTEGRAL'">{{ integralTotol }}</div>
                <div v-else>{{ baseVipListItem.growthValue }}</div>
            </el-form-item>
            <el-form-item label="操作">
                <el-radio-group v-model="modelFormData.changeType" class="ml-4">
                    <el-radio value="INCREASE" size="large">充值</el-radio>
                    <el-radio value="REDUCE" size="large">扣除</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item prop="num" label="调整数值">
                <el-input-number v-model="modelFormData.num" :min="minInputNumber" :controls="false" :precision="precision" :max="maxInputNumber" />
                <span v-if="$props.changeType !== 'BALANCE'">请输入正整数</span>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="$emit('update:topUpBalance', false)">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确认</el-button>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss"></style>
