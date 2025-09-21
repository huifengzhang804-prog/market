<script lang="ts" setup>
import useMaterialCategoryList from './hooks/useMaterialCategoryList'
import handleCategory from './components/handle-category.vue'

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
const emit = defineEmits(['classificationIdVal'])
const classificationId = ref('')
const nodeClickFn = (item: { id: string }) => {
    classificationId.value = item.id
}
watch(
    () => classificationId.value,
    (val) => {
        emit('classificationIdVal', val)
    },
)
const defaultProps = {
    children: 'children',
    label: 'name',
}
</script>
<template>
    <div class="category">
        <div class="category-head">
            <span>素材分类</span>
            <el-button type="primary" link @click="handleAddCategory()">新增分类</el-button>
        </div>
        <div class="tree_list">
            <el-tree
                :data="materialCategoryList"
                :highlight-current="true"
                :check-on-click-node="true"
                :expand-on-click-node="false"
                :props="defaultProps"
                node-key="id"
                default-expand-all
                @node-click="nodeClickFn"
            >
                <template #default="{ node }">
                    <i class="iconfont icon-wenjianjia" style="padding: 0 10px; order: -3; font-size: 18px; color: #ffca28"></i>
                    <span class="treeText"> {{ node.label }}</span>
                    <el-dropdown v-if="node.label !== '全部 '" style="position: absolute; right: 5%; top: 10px">
                        <span style="font-weight: 600; font-size: 18px; order: 1"> ... </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="handleAddCategory(node.data)">重命名</el-dropdown-item>
                                <el-dropdown-item @click="delMaterialInfo(node.data.id)">删除</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </template>
            </el-tree>
        </div>
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
    </div>
</template>
<style lang="scss" scoped>
@include b(category) {
    height: calc(100% - 30px);
    margin: 15px 23px 0 15px;
    width: 210px;
    border: 1px solid #f3f3f3;
    border-radius: 5px;
    box-shadow: border-box;
    @include b(category-head) {
        @include flex(space-between, center);
        font-weight: bold;
        height: 65px;
        padding: 0 15px;
        border-bottom: 1px solid #f3f3f3;
    }
}
@include b(tree_list) {
    height: calc(-280px + 100vh);
    overflow-y: auto;
}
@include b(treeText) {
    max-width: 86px;
    @include utils-ellipsis;
    display: inline-block;
    order: -2;
}
:deep(.el-tree-node) {
    position: relative;
    align-items: center;
    font-size: 14px;
}
:deep(.el-tree-node__content > .el-tree-node__expand-icon) {
    order: -1;
    font-size: 16px;
}
:deep(.el-tree-node__content) {
    height: 50px;
    display: flex;
    align-items: center;
}
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
    background-color: rgb(85, 92, 253, 0.1);
}
</style>
