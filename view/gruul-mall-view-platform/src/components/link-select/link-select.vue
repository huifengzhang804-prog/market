<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import WebView from './components/web-link.vue'
import AppView from './components/app-link.vue'
import { ElMessage } from 'element-plus'
import { isEqual } from 'lodash-es'
import { typeNameMap } from './linkSelectItem'
import type { CurrentComponent, LinkSelectItem } from './linkSelectItem'
import type { PropType } from 'vue'
import { useDecorationStore } from '@/store/modules/decoration'
const asyncComponents = {
    FunctionPage: defineAsyncComponent(() => import('./components/function-page.vue')),
    Goods: defineAsyncComponent(() => import('./components/goods.vue')),
    classification: defineAsyncComponent(() => import('./components/classification.vue')),
    ActivityMarket: defineAsyncComponent(() => import('./components/activity-market.vue')),
    CustomPage: defineAsyncComponent(() => import('./components/cus-page.vue')),
}

const $props = defineProps({
    inner: {
        type: Boolean,
        default: false,
    },
    noProTab: {
        type: Boolean,
        default: false,
    },
    spellTab: {
        type: Boolean,
        default: true,
    },
    limitProTab: {
        type: Boolean,
        default: true,
    },
    customizedProTab: {
        type: Boolean,
        default: true,
    },
    isH5: {
        type: Boolean,
        default: true,
    },
    showClear: {
        type: Boolean,
        default: false,
    },
    selectType: {
        type: Number,
        default: 1,
    },
    link: {
        type: Object as PropType<LinkSelectItem>,
        default() {
            return {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            }
        },
    },
})
const $decorationStore = useDecorationStore()
const $emit = defineEmits(['update:link', 'update-value'])
const linkSelectItem = useVModel($props, 'link', $emit)
// 临时存储link
const tempSelectedLink = ref<LinkSelectItem>({
    id: '',
    type: 0,
    name: '',
    url: '',
})
const radio = ref(1)
// 当前显示组件
const currentComponent = ref<CurrentComponent>('FunctionPage')
const dialogVisible = ref(false)
// 当前组件引用
const componentRef = ref()
watch(dialogVisible, (newVal) => {
    if (newVal) {
        // 以选中链接 进入当前tab
        if (typeNameMap[linkSelectItem.value.type as keyof typeof typeNameMap]) {
            currentComponent.value = typeNameMap[linkSelectItem.value.type as keyof typeof typeNameMap].name
        }
    }
})
watch(
    () => $props.link.id,
    (newVal) => {
        if ($props.link.type === 7) {
            radio.value = 3
        } else {
            radio.value = $props.link.type === 6 ? 2 : 1
        }
    },
)

onMounted(() => {
    if (linkSelectItem.value.type === 7) {
        radio.value = 3
    } else {
        radio.value = linkSelectItem.value.type === 6 ? 2 : 1
    }
})

const handleRadioChange = () => {
    $emit('update-value')
}

const handleConfirm = () => {
    if (!tempSelectedLink.value.id) {
        ElMessage.warning('请选择需要链接的页面')
        return
    }
    if (!isEqual(linkSelectItem.value, tempSelectedLink.value)) {
        Object.assign(linkSelectItem.value, tempSelectedLink.value)
    }
    handleCloseDialog()
}
const handleShow = () => {
    dialogVisible.value = true
    tempSelectedLink.value = JSON.parse(JSON.stringify(linkSelectItem.value))
}
function handleCloseDialog() {
    dialogVisible.value = false
    tempSelectedLink.value = { id: '', type: 0, name: '', url: '', append: '' }
}
</script>

<template>
    <div class="w100">
        <div class="mb18">
            <div>
                <el-radio-group v-if="$props.selectType === 1" v-model="radio" class="line" @change="handleRadioChange">
                    <el-radio :value="1">系统链接</el-radio>
                    <el-radio :value="2">自定义链接</el-radio>
                    <el-radio v-if="customizedProTab" :value="3">小程序</el-radio>
                </el-radio-group>
                <el-select v-if="$props.selectType === 2" v-model="radio">
                    <el-option label="系统链接" :value="1"></el-option>
                    <el-option label="自定义链接" :value="2"></el-option>
                    <el-option label="小程序" :value="3"></el-option>
                </el-select>
            </div>
        </div>
        <div>
            <div @click="handleShow">
                <el-button v-if="radio === 1 && !$props.link.name" type="primary">添加链接</el-button>
                <el-button v-if="radio === 1 && $props.link.name" link type="primary" style="max-width: 100%">{{ $props.link.name }}</el-button>
            </div>
        </div>
        <web-view v-if="radio === 2" :link="linkSelectItem" />
        <app-view v-if="radio === 3" :link="linkSelectItem" />
        <el-dialog v-model="dialogVisible" title="链接选择器" width="886px" :append-to-body="true" :modal-append-to-body="true">
            <div style="margin: 0 20px">
                <el-tabs v-model="currentComponent">
                    <el-tab-pane label="功能页面" name="FunctionPage"></el-tab-pane>
                    <el-tab-pane v-if="!$props.noProTab" label="商品" name="Goods"></el-tab-pane>
                    <el-tab-pane v-if="$props.limitProTab" label="活动营销" name="ActivityMarket"></el-tab-pane>
                    <el-tab-pane v-if="$props.isH5" label="自定义页面" name="CustomPage"></el-tab-pane>
                </el-tabs>
                <component
                    :is="asyncComponents[currentComponent]"
                    ref="componentRef"
                    :link="tempSelectedLink"
                    :visible="dialogVisible"
                    :no-pro-tab="$props.noProTab"
                    :limit-pro-tab="$props.limitProTab"
                ></component>
            </div>
            <template #footer>
                <el-button @click="handleCloseDialog">取 消</el-button>
                <el-button type="primary" @click="handleConfirm">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
.w100 {
    width: 100%;
}

.mb18 {
    margin-bottom: 12px;
}

.line {
    flex-wrap: nowrap;
}

:deep(.el-radio) {
    margin-right: 10px;
}

:deep(.el-button) {
    span {
        display: inline-block;
        overflow: hidden;
        text-overflow: ellipsis;
        text-align: left;
    }
}
</style>
