<script lang="ts" setup>
import type { Ref, WritableComputedRef } from 'vue'
import AddContractCategory from './AddContractCategory.vue'
import EditContractCategory from './EditContractCategory.vue'
import { ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

const route = useRoute()

const props = defineProps({
    list: {
        type: Array,
        default: () => [],
    },
})

const emit = defineEmits(['update:list'])

const tableData: WritableComputedRef<any[]> = computed({
    get() {
        return props.list
    },
    set(value: any[]) {
        emit('update:list', value)
    },
})
const showAddDialog = ref(false)
const selectionSubList: Ref<any[]> = ref([])
const checkedSubCategoryIds = computed(() => tableData.value.map((item: any) => item.currentCategoryId))
const showEditDialog = ref(false)
const current: Ref<any> = ref({})
const editCategoryCompRef: Ref<InstanceType<typeof EditContractCategory> | null> = ref(null)
const handleEditClick = (row: any) => {
    current.value = cloneDeep(row)
    showEditDialog.value = true
}
const handleRemoveClick = (index: number) => {
    ElMessageBox.confirm('请确认是否移出？？？', '请确认')
        .then(() => {
            tableData.value.splice(index, 1)
        })
        .catch(() => {})
}
const handleAddClick = () => {
    showAddDialog.value = true
}
const selectionChange = (selectionList: any[]) => {
    selectionSubList.value = selectionList
}
const confirmAddCategory = () => {
    console.log(selectionSubList.value)
    selectionSubList.value.forEach((item: any) => {
        const isExistId = tableData.value.findIndex((tableDataItem) => tableDataItem.currentCategoryId === item.id)
        if (isExistId === -1) {
            tableData.value.push({
                ...item,
                customDeductionRatio: item.customDeductionRatio || null,
                id: void 0,
                parentId: item.parentId,
                currentCategoryId: item.id,
            })
        }
        console.log(tableData.value)
    })
    showAddDialog.value = false
}
const confirmEditCategory = async () => {
    try {
        await editCategoryCompRef.value?.editContratCategoryRef?.validate()
        const currentIndexInList = tableData.value.findIndex((item: any) => item.currentCategoryId === current.value.currentCategoryId)
        if (currentIndexInList > -1) {
            tableData.value[currentIndexInList].customDeductionRatio = current.value.customDeductionRatio
        }
        showEditDialog.value = false
    } catch (error) {
        console.log('error', error)
    }
}
const cancelEditCategory = () => {
    current.value = {}
    showEditDialog.value = false
}
</script>
<template>
    <div class="operator">
        <el-button type="primary" @click="handleAddClick">添加类目</el-button>
    </div>
    <el-table :data="tableData">
        <template #empty><ElTableEmpty /></template>
        <el-table-column prop="firstName" label="一级类目" />
        <el-table-column prop="name" label="二级类目" />
        <el-table-column prop="deductionRatio" label="类目扣率">
            <template #default="{ row }"> {{ row.deductionRatio }}% </template>
        </el-table-column>
        <el-table-column prop="customDeductionRatio" label="自定义类目扣率">
            <template #default="{ row }">
                <span v-if="row.customDeductionRatio">{{ row.customDeductionRatio }}</span>
                {{ row.customDeductionRatio ? '%' : '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="!(route.name === 'previewShop' || route.name === 'previewSupplier')" label="操作" width="100px">
            <template #default="{ row, $index }">
                <el-link style="margin-right: 8px" :underline="false" type="primary" size="small" @click="handleEditClick(row)">编辑</el-link>
                <el-link :underline="false" type="danger" size="small" @click="handleRemoveClick($index)">移除</el-link>
            </template>
        </el-table-column>
    </el-table>
    <el-dialog v-model="showAddDialog" title="添加类目" destroy-on-close>
        <AddContractCategory :checked-list="checkedSubCategoryIds" @selection-change="selectionChange" />
        <template #footer>
            <el-button @click="showAddDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmAddCategory">确定</el-button>
        </template>
    </el-dialog>
    <el-dialog v-model="showEditDialog" title="编辑" destroy-on-close width="30%">
        <EditContractCategory ref="editCategoryCompRef" v-model:current="current" />
        <template #footer>
            <el-button @click="cancelEditCategory">取消</el-button>
            <el-button type="primary" @click="confirmEditCategory">确定</el-button>
        </template>
    </el-dialog>
</template>

<style scoped>
.operator {
    display: flex;
    justify-content: flex-end;
    width: 100%;
    margin-bottom: 8px;
}
</style>
