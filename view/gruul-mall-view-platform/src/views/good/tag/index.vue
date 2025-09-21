<template>
    <div style="padding-top: 15px" class="handle_container">
        <el-button type="primary" @click="handleAddTag">新增标签</el-button>
    </div>
    <q-table class="table_container" :data="tagTableList" no-border>
        <q-table-column label="商品标签" align="left" width="180">
            <template #default="{ row }">
                <div class="tag" :style="{ backgroundColor: row?.backgroundColor, color: row?.fontColor, padding: '0 13px', lineHeight: '30px' }">
                    {{ row?.name }}
                </div>
            </template>
        </q-table-column>
        <q-table-column label="商家类型" align="left" style="color: #333" width="180">
            <template #default="{ row }">{{ computedShopType(row) }}</template>
        </q-table-column>
        <q-table-column label="添加时间" align="left" prop="createTime" width="250" style="color: #333" />
        <q-table-column label="操作" align="right" width="150" fixed="right">
            <template #default="{ row }">
                <el-link type="primary" @click="handleEditTag(row)">编辑</el-link>
                <el-link type="danger" @click="handleDeleteTag(row)">删除</el-link>
            </template>
        </q-table-column>
    </q-table>
    <BetterPageManage
        :page-size="pageConfig.pageSize"
        :page-num="pageConfig.pageNum"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="showTagDialog" :width="680" :title="currentTag?.id ? '编辑标签' : '新增标签'" destroy-on-close @close="currentTag = {}">
        <handle-tag ref="handleTagRef" :tag-info="currentTag" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showTagDialog = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmTag">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import useTagListHooks from './hooks/useTagListHooks'
import HandleTag from './components/HandleTag.vue'

const {
    tagTableList,
    pageConfig,
    handleSizeChange,
    handleCurrentChange,
    handleEditTag,
    handleDeleteTag,
    computedShopType,
    handleAddTag,
    currentTag,
    showTagDialog,
    handleConfirmTag,
    handleTagRef,
} = useTagListHooks()
</script>

<style lang="scss" scoped>
* {
    font-size: 14px;
}
@include b(table) {
    height: calc(100vh - 224px);
}
@include b(el-link) {
    margin-right: 8px;
}
</style>
