<script lang="ts" setup>
import { ElMessage } from 'element-plus'

const props = defineProps({
    initialLabels: {
        type: Array<{ id?: string; tagName: string; option?: boolean }>,
        default: () => [],
    },
})
const emitFn = defineEmits(['update:initialLabels'])
const labels = computed({
    get() {
        return props.initialLabels
    },
    set(value) {
        emitFn('update:initialLabels', value)
    },
})
const showAddForm = ref(false)
const formModel = reactive({
    tagName: '',
})
const handleAddTags = () => {
    if (!formModel.tagName) return ElMessage.error({ message: '请输入标签名' })
    labels.value.push({ tagName: formModel.tagName })
    resetFormModel()
}
const resetFormModel = () => {
    formModel.tagName = ''
    showAddForm.value = false
}
const handleRemove = (index: number) => {
    labels.value.splice(index, 1)
}
const checkGroupIndexs = computed({
    get() {
        const indexs: number[] = []
        props.initialLabels.forEach((item, index) => {
            if (item.option) {
                indexs.push(index)
            }
        })
        return indexs
    },
    set(value) {
        labels.value.forEach((item) => (item.option = false))
        value.forEach((item) => {
            labels.value[item].option = true
        })
    },
})
</script>
<template>
    <el-checkbox-group v-model="checkGroupIndexs">
        <el-row :gutter="8">
            <el-col v-for="(item, index) in labels" :key="index" :span="8">
                <el-checkbox :value="index">
                    <el-input v-model="item.tagName" clearable :maxlength="10" @clear="handleRemove(index)" />
                </el-checkbox>
            </el-col>
        </el-row>
    </el-checkbox-group>
    <el-row>
        <el-form v-if="showAddForm" :model="formModel" inline>
            <el-form-item label="标签名">
                <el-input v-model="formModel.tagName" placeholder="请输入标签名" :maxlength="10" />
            </el-form-item>
            <el-form-item>
                <el-button type="text" @click="handleAddTags">添加</el-button>
                <el-button type="text" @click="resetFormModel">删除</el-button>
            </el-form-item>
        </el-form>
        <el-link v-else type="primary" @click="showAddForm = true">新增标签</el-link>
    </el-row>
</template>

<style lang="scss" scoped>
:deep(.el-col) {
    padding: 5px 10px;
}
</style>
