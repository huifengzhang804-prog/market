<script setup lang="ts">
import { logo } from '../footer-info'
import upload from '@/views/decoration/platformDecoration/pc/components/upload/index.vue'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof logo>,
        default: logo,
    },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const rules = {
    img: [{ required: true, message: 'LOGO 为必传项。', trigger: 'change' }],
    'logoInfo.0.title': [{ required: true, message: '标题为必传项。', trigger: 'blur' }],
    'logoInfo.1.title': [{ required: true, message: '标题为必传项。', trigger: 'blur' }],
    'logoInfo.2.title': [{ required: true, message: '标题为必传项。', trigger: 'blur' }],
}

const formRef = ref()

defineExpose({
    formRef,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules">
        <el-form-item label="平台LOGO" class="left-logo" prop="img">
            <div style="height: 130px">
                <upload v-model="formData.img" width="108px" height="108px" :form-ref="formRef" />

                <span class="left-logo__text">230*92</span>
            </div>
        </el-form-item>

        <div class="info">
            <div v-for="(item, ind) in formData.logoInfo" :key="ind" class="item">
                <el-form-item label="标题" label-width="90px" class="item__border" :prop="`logoInfo.${ind}.title`">
                    <el-input v-model="item.title" placeholder="请输入标题" maxlength="20" show-word-limit />
                </el-form-item>
                <el-form-item label="链接地址" label-width="90px">
                    <el-input v-model="item.link" placeholder="请输入链接地址" />
                </el-form-item>
            </div>
        </div>
    </el-form>
</template>

<style lang="scss" scoped>
@include b(el-form) {
    display: flex;
}

@include b(left-logo) {
    align-items: flex-start;
    margin-right: 22px;

    @include e(text) {
        color: #999999;
    }
}

@include b(info) {
    flex: 1;

    @include b(el-form-item) {
        height: 48px;
        margin-bottom: 0;
        padding-right: 10px;

        :deep(.el-form-item__label) {
            height: 48px;
            line-height: 48px;
            padding-right: 18px;
        }

        :deep(.el-form-item__error) {
            top: 86%;
        }
    }

    @include b(item) {
        border: 1px solid rgba(0, 0, 0, 0.06);
        margin-bottom: 12px;

        @include e(border) {
            border-bottom: 1px solid rgba(0, 0, 0, 0.06);
        }
    }
}
</style>
