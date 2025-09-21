<script setup lang="ts">
import guarantee from './guarantee'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import { useVModel } from '@vueuse/core'
import type { FormInstance } from 'element-plus'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof guarantee>,
        default: guarantee,
    },
})

const formRef = ref<FormInstance>()

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const rules = {
    img: [{ required: true, message: 'LOGO 为必传项。', trigger: 'change' }],
}

defineExpose({
    formRef,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules">
        <el-form-item label="服务保障" prop="img">
            <div style="height: 84px">
                <upload v-model="formData.img" width="376px" :form-ref="formRef" />

                <span class="text">1920*96</span>
            </div>
        </el-form-item>
    </el-form>
</template>

<style lang="scss" scoped>
@include b(text) {
    color: #999;
    line-height: 24px;
}
</style>
