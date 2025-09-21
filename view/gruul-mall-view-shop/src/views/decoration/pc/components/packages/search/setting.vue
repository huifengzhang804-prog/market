<script setup lang="ts">
import search from './search'
import upload from '@/views/decoration/pc/components/upload/index.vue'
import { useVModel } from '@vueuse/core'
import type { FormInstance } from 'element-plus'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof search>,
        default: search,
    },
})

const formRef = ref<FormInstance>()

const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const rules = {
    logo: [{ required: true, message: 'LOGO 为必传项。', trigger: 'change' }],
}

defineExpose({
    formRef,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="90">
        <el-form-item label="平台LOGO" prop="logo">
            <div style="height: 84px">
                <upload v-model="formData.logo" :form-ref="formRef" validate-key="logo" />

                <span class="text">200*80</span>
            </div>
        </el-form-item>

        <el-form-item label="搜索">
            <el-switch v-model="formData.search" />
        </el-form-item>

        <el-form-item label="购物车">
            <el-switch v-model="formData.car" />
        </el-form-item>
    </el-form>
</template>

<style scoped lang="scss">
@include b(text) {
    color: #999;
    line-height: 24px;
}
</style>
