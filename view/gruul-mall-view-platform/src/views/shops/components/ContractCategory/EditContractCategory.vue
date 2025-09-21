<script lang="ts" setup>
import type { FormInstance, FormItemRule } from 'element-plus'
import type { Arrayable } from 'element-plus/es/utils'
import type { Ref } from 'vue'

const props = defineProps({
    current: {
        type: Object,
        default: () => ({}),
    },
})
const emitFn = defineEmits(['update:current'])
const editContratCategory = computed({
    get() {
        return props.current
    },
    set(newVal: any) {
        emitFn('update:current', newVal)
    },
})
const rules: Partial<Record<string, Arrayable<FormItemRule>>> = {
    customDeductionRatio: [
        {
            validator: (_, value) => {
                if (!/^[0-9]+$/.test(value)) {
                    return new Error('请输入0~100的正整数')
                } else if (value > 100 || value < 0) {
                    return new Error('请输入0~100的正整数')
                }
                return true
            },
            trigger: 'blur',
        },
    ],
}
const editContratCategoryRef: Ref<FormInstance | null> = ref(null)
defineExpose({ editContratCategoryRef })
</script>
<template>
    <el-form ref="editContratCategoryRef" :model="editContratCategory" :rules="rules" style="width: 400px">
        <el-form-item label="自定义类目扣率" prop="customDeductionRatio">
            <el-input v-model="editContratCategory.customDeductionRatio" placeholder="请输入0~100的正整数">
                <template #append>%</template>
            </el-input>
        </el-form-item>
    </el-form>
</template>
