<template>
    <div class="pagination">
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
    </div>
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

const handleSizeChange = (val: number) => {
    $emit('handleSizeChange', val)
}
const handleCurrentChange = (val: number) => {
    $emit('handleCurrentChange', val)
}
</script>

<style lang="scss" scoped>
.pagination {
    display: flex;
    justify-content: center;
    margin-top: auto;
    width: 100%;
    background-color: white;
    padding: 12.23px 13px;
}

:deep(.el-pagination) {
    padding: 0px;
    position: relative;
}
</style>
