<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
import { module, moduleDefault, moduleTitleDefault } from '../footer-info'
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof module>,
        default: module,
    },
})
const emit = defineEmits(['update:formData'])
const formData = useVModel(props, 'formData', emit)

const moduleTitleRules = () => [{ required: true, message: '标题为必填项。', trigger: 'blur' }]

const moduleNameRules = () => [{ required: true, message: '模块名称为必填项。', trigger: 'blur' }]

const rules: { [key: string]: any } = {
    '0.moduleName': moduleNameRules(),
    '0.moduleTitle.0.title': moduleTitleRules(),
}

/**
 * 添加模块
 */
const addModule = () => {
    // 数据添加
    if (formData.value.length >= 4) return ElMessage.error('最多添加4个模块！')
    formData.value.push(moduleDefault())

    // 校验添加
    const index = formData.value.length - 1
    rules[`${index}.moduleName`] = moduleNameRules()
    rules[`${index}.moduleTitle.0.title`] = moduleTitleRules()
}

/**
 * 删除模块
 */
const delModule = (index: number) => {
    formData.value.splice(index, 1)
}

/**
 * 添加标题
 */
const addTitle = (ind: number) => {
    // 数据添加
    if (formData.value[ind].moduleTitle.length >= 4) return ElMessage.error('最多添加4个标题！')
    formData.value[ind].moduleTitle.push(moduleTitleDefault())

    // 校验添加
    const index = formData.value[ind].moduleTitle.length - 1
    rules[`${ind}.moduleTitle.${index}.title`] = moduleTitleRules()
}

onBeforeMount(() => {
    formData.value.forEach((item, ind) => {
        rules[`${ind}.moduleName`] = moduleNameRules()
        item.moduleTitle.forEach((_, index) => {
            rules[`${ind}.moduleTitle.${index}.title`] = moduleTitleRules()
        })
    })
})

/**
 * 删除标题
 */
const delTitle = (ind: number, index: number) => {
    formData.value[ind].moduleTitle.splice(index, 1)
}

const formRef = ref()

defineExpose({
    formRef,
})
</script>
<template>
    <div class="module">
        <div class="module__text">
            <el-button :icon="Plus" type="primary" class="m-r-16" :disabled="formData.length >= 4" @click="addModule">添加</el-button>
            最多4个模块
        </div>
        <el-scrollbar height="346px" class="m-t-12">
            <el-form ref="formRef" :model="formData" :rules="rules">
                <div v-for="(item, ind) in formData" :key="ind" class="item">
                    <!-- 名称 -->
                    <el-form-item label="模块名称" label-width="90px" class="border" :prop="`${ind}.moduleName`">
                        <el-input v-model="item.moduleName" placeholder="请输入模块名称" style="width: 577px" maxlength="10" show-word-limit />

                        <!-- 删除图标 -->
                        <q-icon name="icon-shanchu2" color="#E90000" class="item__del cp" size="20px" @click="delModule(ind)" />
                    </el-form-item>

                    <!-- 标题 -->
                    <div v-for="(ele, index) in item.moduleTitle" :key="index" class="border item__title">
                        <el-form-item :prop="`${ind}.moduleTitle.${index}.title`" label="标题" label-width="90px">
                            <el-input v-model="ele.title" placeholder="请输入标题" maxlength="10" style="width: 283px" show-word-limit />
                        </el-form-item>
                        <el-form-item>
                            <el-input v-model="ele.link" placeholder="请输入链接地址" style="width: 283px" />
                        </el-form-item>

                        <!-- 添加图标 -->
                        <el-icon
                            v-show="index === item.moduleTitle.length - 1 && item.moduleTitle.length < 4"
                            color="#409EFF"
                            class="item__title--plus cp"
                            size="21px"
                            @click="addTitle(ind)"
                        >
                            <Plus />
                        </el-icon>

                        <!-- 删除图标 -->
                        <q-icon
                            v-show="item.moduleTitle.length > 1"
                            name="icon-shanchu2"
                            color="#E90000"
                            class="item__title--del cp"
                            size="20px"
                            @click="delTitle(ind, index)"
                        />
                    </div>
                </div>
            </el-form>
        </el-scrollbar>
    </div>
</template>

<style lang="scss" scoped>
@include b(module) {
    @include e(text) {
        font-size: 12px;
        color: #666666;
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

    @include b(item) {
        border: 1px solid rgba(0, 0, 0, 0.06);
        border-bottom: none;
        margin-bottom: 12px;

        @include e(del) {
            position: absolute;
            right: 3px;
            top: 12px;
        }

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
    }

    @include b(border) {
        border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    }
}
</style>
