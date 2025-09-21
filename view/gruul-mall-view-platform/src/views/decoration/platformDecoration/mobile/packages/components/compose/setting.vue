<template>
    <div>
        <el-tabs v-model="activeCompName" type="card">
            <el-tab-pane label="搜索" name="search" />
            <el-tab-pane label="轮播图" name="swiper" />
            <el-tab-pane label="定位" name="positionStyle" />
        </el-tabs>
        <component :is="reactiveComponent[activeCompName]" v-model:form-data="formData[activeCompName]" />
    </div>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import composeData, { ComposeDataType } from './compose'
import { useVModel } from '@vueuse/core'

type ActiveNameType = 'search' | 'positionStyle' | 'swiper'
const activeCompName = ref<ActiveNameType>('search')
const $props = defineProps({
    formData: {
        type: Object as PropType<ComposeDataType>,
        default() {
            return composeData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const reactiveComponent = {
    search: defineAsyncComponent(() => import('./compose/search/setting.vue')),
    swiper: defineAsyncComponent(() => import('./compose/swiper/setting.vue')),
    positionStyle: defineAsyncComponent(() => import('./compose/positioningStyle/setting.vue')),
}
</script>
