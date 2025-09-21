<template>
    <el-pagination
        :page-sizes="$props.pageSizes"
        class="pagination"
        :page-size="+$props.pageSize"
        :current-page="+$props.pageNum"
        :total="+$props.total"
        size="small"
        :pager-count="5"
        background
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    ></el-pagination>
</template>
<script setup lang="ts">
import type { PropType } from 'vue'
const $props = defineProps({
    pageSize: {
        type: [Number, String],
        default: 20,
    },
    pageNum: {
        type: [Number, String],
        default: 1,
    },
    pageSizes: {
        type: Array as PropType<number[]>,
        default() {
            return [10, 20, 50, 100]
        },
    },
    total: {
        type: [Number, String],
        default: 0,
    },
})
const $emit = defineEmits(['handleSizeChange', 'handleCurrentChange'])
// const parentConfig = inject("pageManageConfig");
const handleSizeChange = (val: number) => {
    $emit('handleSizeChange', val)
}
const handleCurrentChange = (val: number) => {
    $emit('handleCurrentChange', val)
}
</script>

<style lang="scss" scoped>
.pagination {
    :deep() {
        .el-select .el-input.is-focus .el-input__wrapper {
            box-shadow: 0 0 0 1px transparent inset !important;
        }
        .el-select .el-input__wrapper.is-focus {
            box-shadow: 0 0 0 1px transparent inset !important;
        }
        .el-pagination.is-background .el-pagination__sizes.is-last {
            margin-right: 0;
        }
        .el-pagination.is-background .el-pager li:not(.is-disabled).is-active {
            background-color: #0066ff !important;
            color: #fff !important;
        }
        .el-pagination.is-background.el-pagination--small .btn-next,
        .el-pagination.is-background.el-pagination--small .btn-prev,
        .el-pagination.is-background.el-pagination--small .el-pager li {
            background-color: #f4f4f5 !important;
        }
        .el-pagination.is-background .btn-next:hover:not([disabled]),
        .el-pagination.is-background .btn-prev:hover:not([disabled]) {
            color: #0066ff !important;
        }
    }
}
</style>
