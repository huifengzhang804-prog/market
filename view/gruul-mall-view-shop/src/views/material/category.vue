<script lang="ts" setup>
import useMaterialCategoryList from './hooks/useMaterialCategoryList'
import handleCategory from './components/handle-category.vue'
import { useVModel } from '@vueuse/core'

const $props = defineProps({
    categoryId: {
        type: String as PropType<Long>,
        default: ' ',
    },
})
const emit = defineEmits(['update:categoryId', 'nodeClick'])

const categoryId = useVModel($props, 'categoryId', emit)

const {
    materialCategoryList,
    getMaterialCategoryList,
    handleAddCategory,
    handleCategoryRef,
    currentFormModel,
    showDialog,
    handleCloseDialog,
    handleConfirm,
    delMaterialInfo,
} = useMaterialCategoryList()
getMaterialCategoryList()
const nodeClickFn = (item: { id: string }) => {
    categoryId.value = item.id
    emit('nodeClick')
}
const defaultProps = {
    children: 'children',
    label: 'name',
}
const TreeRef = ref()

watch(
    () => categoryId.value,
    (val) => {
        // 选中树
        TreeRef.value?.setCurrentKey(val)
    },
    { immediate: true },
)
// 只执行一次
const stopWatcher = watch(
    () => materialCategoryList.value,
    async (val) => {
        if (val?.length) {
            await nextTick()
            TreeRef.value?.setCurrentKey('')
            // 取消监听
            stopWatcher()
        }
    },
)
</script>
<template>
    <el-card shadow="never">
        <template #header>
            <div class="ccenter fwb">
                <div>素材分类</div>
                <div class="mla">
                    <el-button type="primary" link @click="handleAddCategory()">新增分类</el-button>
                </div>
            </div>
        </template>
        <el-tree
            ref="TreeRef"
            :data="materialCategoryList"
            :highlight-current="true"
            :check-on-click-node="true"
            :expand-on-click-node="false"
            :props="defaultProps"
            node-key="id"
            default-expand-all
            :default-checked-keys="['']"
            @node-click="nodeClickFn"
        >
            <template #default="{ node }">
                <q-icon svg name="icon-wenjianjia" style="order: -3; margin: 0 10px"></q-icon>
                <span class="treeText"> {{ node.label }}</span>
                <el-dropdown v-if="node.label !== '全部 '" style="position: absolute; right: 5%">
                    <span style="font-weight: 600; font-size: 18px; order: 1; height: 20px"> ... </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="handleAddCategory(node.data)">重命名</el-dropdown-item>
                            <el-dropdown-item @click="delMaterialInfo(node.data.id)">删除</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </template>
        </el-tree>
        <el-dialog
            v-model="showDialog"
            :title="currentFormModel?.id ? '编辑分类' : '添加分类'"
            :width="650"
            destroy-on-close
            @close="handleCloseDialog"
        >
            <handle-category ref="handleCategoryRef" v-model:form-model="currentFormModel" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button type="primary" @click="handleConfirm"> 确定 </el-button>
                </span>
            </template>
        </el-dialog>
    </el-card>
</template>
<style lang="scss" scoped>
.fwb {
    font-weight: bold;
    height: 65px;
    padding: 0 15px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
@include b(treeText) {
    max-width: 86px;
    display: inline-block;
    @include utils-ellipsis;
    order: -2;
}

:deep(.el-tree-node__content) {
    height: 50px;
    display: flex;
    align-items: center;
}
:deep(.el-card__body) {
    padding: 0;
}
:deep(.el-tree-node__expand-icon) {
    font-size: 18px;
}
:deep(.el-card__header) {
    padding: 0;
}
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
    background-color: rgb(85, 92, 253, 0.1);
}
</style>
