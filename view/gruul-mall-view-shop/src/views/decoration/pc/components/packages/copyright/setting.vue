<script setup lang="ts">
import copyright from './copyright'
import { Plus } from '@element-plus/icons-vue'
import { copyrightDefault } from './copyright'
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof copyright>,
        default: copyright,
    },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const titleRules = () => [{ required: true, message: '标题为必填项。', trigger: 'blur' }]

const rules: { [key: string]: any } = {
    '0.title': titleRules(),
}

/**
 * 添加标题
 */
const addTitle = () => {
    // 数据添加
    if (formData.value.length >= 10) return ElMessage.error('最多添加10个标题！')

    formData.value.push(copyrightDefault())
    // 校验添加
}

watch(
    () => formData.value.length,
    () => {
        rules[`${formData.value.length - 1}.title`] = titleRules()
    },
)

onBeforeMount(() => {
    formData.value.forEach((item, index) => {
        rules[`${index}.title`] = titleRules()
    })
})

/**
 * 删除标题
 */
const delTitle = (index: number) => {
    formData.value.splice(index, 1)
}

const formRef = ref()

defineExpose({
    formRef,
})
</script>

<template>
    <el-form ref="formRef" :model="formData" :rules="rules">
        <el-scrollbar height="432px">
            <div class="item">
                <!-- 标题 -->
                <div v-for="(item, index) in formData" :key="index" class="border item__title">
                    <el-form-item :prop="`${index}.title`" label="标题" label-width="90px">
                        <el-input v-model="item.title" placeholder="请输入标题" style="width: 283px" show-word-limit maxlength="10" />
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model="item.link" placeholder="请输入链接地址" style="width: 283px" />
                    </el-form-item>

                    <!-- 添加图标 -->
                    <el-icon
                        v-show="index === formData.length - 1 && formData.length < 10"
                        color="#409EFF"
                        class="item__title--plus cp"
                        size="21px"
                        @click="addTitle"
                    >
                        <Plus />
                    </el-icon>

                    <!-- 删除图标 -->
                    <q-icon
                        v-show="formData.length > 1"
                        name="icon-shanchu2"
                        color="#E90000"
                        class="item__title--del cp"
                        size="20px"
                        @click="delTitle(index)"
                    />
                </div>
            </div>
        </el-scrollbar>
    </el-form>
</template>

<style lang="scss" scoped>
@include b(item) {
    border: 1px solid rgba(0, 0, 0, 0.06);
    border-bottom: none;
    margin-bottom: 12px;

    @include e(title) {
        display: flex;
        position: relative;

        @include m(del) {
            position: absolute;
            right: 12px;
            top: 12px;
        }

        @include m(plus) {
            position: absolute;
            right: 48px;
            top: 12px;
            font-weight: 700;
        }
    }

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
}

@include b(border) {
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}
</style>
