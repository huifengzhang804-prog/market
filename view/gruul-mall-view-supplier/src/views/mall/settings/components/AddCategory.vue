<script lang="ts" setup>
import type { Ref } from 'vue'
import type { SelectOptionsProps } from './type'
import { doGetCategoryLevelByParentId } from '@/apis/store/index'
import { ElMessage } from 'element-plus'
const props = defineProps({
    checkedList: {
        type: Array<string>,
        default: () => [],
    },
})
const emitFn = defineEmits(['selectionChange'])
const filterForm = reactive({
    firstCategory: '',
})
const firstCategoryList: Ref<SelectOptionsProps[]> = ref([])
const secondCategoryList: Ref<any[]> = ref([])

const handleSelectionChange = (selectionList: any[]) => {
    const emitSelectionList = selectionList.map((item) => ({
        parentId: item.parentId,
        parentName: item.firstName,
        currentCategoryId: item.id,
        currentCategoryName: item.name,
        customDeductionRatio: 0,
        deductionRatio: item.deductionRatio,
    }))
    emitFn('selectionChange', emitSelectionList)
}
const getFirstCategoryList = async () => {
    const { data, success } = await doGetCategoryLevelByParentId('LEVEL_1', 0, 1000, 1)
    if (success) {
        firstCategoryList.value = data.records?.map((item: any) => ({ value: item.id, label: item.name }))
    } else {
        ElMessage.error('获取一级类目失败')
    }
}
const getSecondCategoryList = async () => {
    const currentFirstCategory = firstCategoryList.value?.find((item: any) => item.value === filterForm.firstCategory)?.label || ''
    const { data, success } = await doGetCategoryLevelByParentId('LEVEL_2', filterForm.firstCategory as string, 1000, 1)
    if (success) {
        secondCategoryList.value = data.records.map((item: any) => ({ ...item, firstName: currentFirstCategory }))
    } else {
        secondCategoryList.value = []
        ElMessage.error('获取二级类目失败')
    }
}
onMounted(() => {
    getFirstCategoryList()
})

/**
 * @: 渲染类目
 */
const renderRatio = (row: any) => {
    const ratio = row.customDeductionRatio ? row.customDeductionRatio : row.deductionRatio
    return ratio ? ratio + '%' : '-'
}
</script>
<template>
    <div class="contract">
        <el-form :model="filterForm">
            <el-form-item label="一级类目" prop="firstCategory">
                <el-select
                    v-model="filterForm.firstCategory"
                    placeholder="请选择以及类目"
                    style="width: 214px"
                    @update:model-value="getSecondCategoryList"
                >
                    <el-option
                        v-for="(firstCategory, index) in firstCategoryList"
                        :key="index"
                        :label="firstCategory.label"
                        :value="firstCategory.value"
                    />
                </el-select>
            </el-form-item>
        </el-form>
        <el-form ref="tableFormRef">
            <el-table :data="secondCategoryList" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" :selectable="(row) => !props.checkedList.includes(row.id)" />
                <el-table-column label="二级类目" prop="name" />
                <el-table-column label="类目扣率">
                    <template #default="{ row }">{{ renderRatio(row) }}</template>
                </el-table-column>
            </el-table>
        </el-form>
    </div>
</template>
