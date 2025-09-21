<script lang="ts" setup>
import type { Ref } from 'vue'
import type { SelectOptionsProps } from './type'
import { doGetCategoryLevelByParentId } from '@/apis/shops'
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
    emitFn('selectionChange', selectionList)
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
</script>
<template>
    <div class="contract">
        <el-form :model="filterForm">
            <el-form-item label="一级类目" prop="firstCategory">
                <el-select v-model="filterForm.firstCategory" placeholder="请选择以及类目" @update:model-value="getSecondCategoryList">
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
                <template #empty><ElTableEmpty /></template>
                <el-table-column type="selection" width="55" :selectable="(row) => !props.checkedList.includes(row.id)" />
                <el-table-column label="二级类目" prop="name" />
                <el-table-column label="类目扣率" prop="deductionRatio">
                    <template #default="{ row }"> {{ row?.deductionRatio }}% </template>
                </el-table-column>
                <el-table-column label="自定义类目扣率">
                    <template #default="{ row }">
                        <!-- <el-input v-model="row.customDeductionRatio" type="number" :min="0" :max="100" placeholder="请输入自定义类目扣率">
                            <template #append>%</template>
                        </el-input> -->
                        <div class="flex-box">
                            <el-input-number
                                v-model="row.customDeductionRatio"
                                class="input-number"
                                :min="0"
                                :max="100"
                                :precision="2"
                                :controls="false"
                                placeholder="请输入0~100的正整数"
                            />
                            <el-tag type="info" size="large">%</el-tag>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-form>
    </div>
</template>

<style lang="scss" scoped>
@include b(flex-box) {
    @include flex();
    @include b(input-number) {
        flex: 1;
    }
    @include b(el-tag) {
        flex-shrink: 0;
    }
}
</style>
