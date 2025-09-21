<script setup lang="ts">
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import type { PropType } from 'vue'
import SearchFormItem from '@/components/SearchFormItem.vue'
import Grid from '@/components/Grid/index.vue'
import GridItem from '@/components/Grid/components/GridItem.vue'
import { BreakPoint } from './Grid/interface'
import { SearchProps } from './types'
import { useRoute } from 'vue-router'
import Storage from '@/utils/Storage'

type LabelPositionType = 'right' | 'left' | 'top' | undefined
const storageLocal = () => new Storage()
const route = useRoute()
const props = defineProps({
    // 搜索表单绑定值
    modelValue: {
        type: Object,
        default() {
            return {}
        },
    },
    // 每行默认显示个数
    showNumber: {
        type: Number,
        default: 3,
    },
    // 标签宽度
    labelWidth: {
        type: String,
        default() {
            return '80'
        },
    },
    // 搜索配置信息
    columns: {
        type: Array as PropType<any[]>,
        default() {
            return []
        },
    },
    // 标签位置
    labelPosition: {
        type: String as PropType<LabelPositionType>,
        default() {
            return 'right'
        },
    },
    searchCol: {
        type: [Number, Object] as PropType<number | Record<BreakPoint, number>>,
        default: () => ({ xs: 1, sm: 2, md: 3, lg: 3, xl: 4 }),
    },
})

const $emit = defineEmits(['update:modelValue', 'searchHandle', 'handleReset'])
const _modelValue = useVModel(props, 'modelValue', $emit)
// 获取当前页面路径信息
const getCacheKey = () => `search_cache_${route.path}`

const handleQuerySearch = () => {
    $emit('searchHandle', _modelValue.value)
    // 缓存搜索条件
    storageLocal().setItem(getCacheKey(), _modelValue.value)
}

// 监听路由变化
watch(
    () => route.path,
    () => {
        const cached = storageLocal().getItem(getCacheKey())
        if (cached) {
            // 如果缓存存在，则将缓存中的数据赋值给搜索条件
            Object.keys(cached).forEach((key) => {
                if (cached[key] !== null && cached[key] !== undefined && cached[key] !== '') {
                    _modelValue.value[key] = cached[key]
                }
            })
        }
    },
    { immediate: true },
)

// 离开页面时，清除缓存
onBeforeUnmount(() => {
    storageLocal().removeItem(getCacheKey())
})

// 获取响应式设置
const getResponsive = (item: SearchProps) => {
    return {
        span: item?.span || 1,
        offset: item?.offset ?? 0,
        xs: item?.xs,
        sm: item?.sm,
        md: item?.md,
        lg: item?.lg,
        xl: item?.xl,
    }
}

// 是否默认折叠搜索项
const collapsed = ref(true)

// 获取响应式断点
const gridRef = ref()
const breakPoint = computed<BreakPoint>(() => gridRef.value?.breakPoint)
// 判断是否显示 展开/合并 按钮
const showCollapse = computed(() => {
    let show = false
    props.columns.reduce((prev, current) => {
        prev += (current![breakPoint.value]?.span ?? current?.span ?? 1) + (current![breakPoint.value]?.offset ?? current?.offset ?? 0)
        if (typeof props.searchCol !== 'number') {
            if (prev >= props.searchCol[breakPoint.value]) show = true
        } else if (prev >= props.searchCol) show = true
        return prev
    }, 0)
    return show
})
</script>

<template>
    <div v-if="columns.length" class="search_container">
        <el-form :model="_modelValue" :label-position="labelPosition">
            <Grid ref="gridRef" :collapsed="collapsed" :gap="[10, 12]" :cols="searchCol">
                <GridItem
                    v-for="(item, index) in columns"
                    :key="item.prop"
                    v-bind="getResponsive(item)"
                    :index="index"
                    @keyup.enter="handleQuerySearch"
                >
                    <el-form-item :label="item.label" :label-width="90">
                        <SearchFormItem v-if="!item.renderField" :key="item.prop" v-model="_modelValue[item.prop]" :column="item" />
                        <template v-else>
                            <component :is="item.renderField(_modelValue[item.prop])"></component>
                        </template>
                    </el-form-item>
                </GridItem>
                <GridItem suffix>
                    <div class="operation">
                        <el-button type="primary" @click="handleQuerySearch">搜索</el-button>
                        <el-button @click="$emit('handleReset')">重置</el-button>
                        <slot name="otherOperations"></slot>
                        <el-button v-if="showCollapse" type="primary" link class="search-isOpen" @click="collapsed = !collapsed">
                            {{ collapsed ? '展开' : '收起' }}
                            <el-icon class="el-icon--right">
                                <component :is="collapsed ? ArrowDown : ArrowUp"></component>
                            </el-icon>
                        </el-button>
                        <slot name="promptIcon"></slot>
                    </div>
                </GridItem>
            </Grid>
        </el-form>
    </div>
</template>
<style lang="scss" scoped>
.search_container {
    width: 100%;
    background: rgb(255, 255, 255);
    padding-top: 17px;
    padding-bottom: 15px;
    padding-left: 16px;
    padding-right: 16px;
    .el-form {
        .el-form-item {
            margin-bottom: 0;
        }
        .el-form-item__content > * {
            width: 100%;
        }

        // 去除时间选择器上下 padding
        .el-range-editor.el-input__wrapper {
            padding: 0 10px;
        }
    }
    .operation {
        position: relative;
        top: 0;
        display: flex;
        align-items: center;
        justify-content: flex-end;
    }
}

:deep(.el-form .el-row .el-col) {
    max-width: 370px;
}
</style>
