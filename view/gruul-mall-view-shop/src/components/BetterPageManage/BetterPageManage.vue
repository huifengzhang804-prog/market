<template>
    <div class="pagination">
        <el-pagination
            :background="props.background"
            :page-size="+props.modelValue.size"
            :current-page="+props.modelValue.current"
            :page-sizes="props.sizes"
            :total="+props.total"
            layout="total, slot,prev, pager, next, sizes"
            @size-change="sizeChange"
            @current-change="currentChange"
        >
            <template #default>
                <el-link :icon="Refresh" :underline="false" title="刷新" @click="reload" />
            </template>
        </el-pagination>
    </div>
</template>
<script setup lang="ts">
import { Refresh } from '@element-plus/icons-vue'
import { defaultConfig, Page } from './index'
import { useVModel } from '@vueuse/core'

const emits = defineEmits(['reload', 'update:modelValue'])
const props = defineProps({
    //是否立马请求数据
    loadInit: Boolean,
    modelValue: {
        type: Object as () => Page,
        required: false,
        default(): Page {
            return {
                current: defaultConfig.current,
                size: defaultConfig.size,
            }
        },
    },
    sizes: {
        type: Array as () => Array<number>,
        default: defaultConfig.sizes,
    },
    total: {
        type: [Number, String],
        default: defaultConfig.total,
    },
    background: {
        type: Boolean,
        default: false,
    },
})
const _modelValue = useVModel(props, 'modelValue', emits)

const updateValue = (page: Page) => {
    _modelValue.value.current = page.current
    _modelValue.value.size = page.size
    nextTick(reload)
}
const sizeChange = (size: number) => {
    updateValue({ size, current: 1 })
}
const currentChange = (current: number) => {
    updateValue({ size: props.modelValue.size, current })
}
const reload = () => {
    emits('reload')
}

//直接重置初始数据
onMounted(() => {
    if (props.loadInit) {
        reload()
    }
})
</script>

<style lang="scss" scoped>
.pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: auto;
}

:deep(.el-pagination) {
    padding: 0;
    position: relative;
}
</style>
