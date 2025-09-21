<template>
    <div class="pagination">
        <el-pagination
            v-model:current-page="_pageNum"
            v-model:page-size="_pageSize"
            :page-sizes="pageSizes"
            layout="total,sizes, prev, pager, next, jumper"
            :total="+total"
            size="small"
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
    </div>
</template>
<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'

const $props = defineProps({
    pageSize: {
        type: Number,
        default: 20,
    },
    pageNum: {
        type: Number,
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
        default: 100,
    },
})
const $emit = defineEmits(['handleSizeChange', 'handleCurrentChange', 'update:current-page', 'update:page-size', 'update:total', 'update:pageNum'])
const _pageSize = useVModel($props, 'pageSize', $emit)
const _pageNum = useVModel($props, 'pageNum', $emit)

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
    justify-content: flex-end;
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
