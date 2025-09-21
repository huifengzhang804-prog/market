<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { ApiOrder } from '../types/order'

const $props = defineProps({
    isShow: {
        type: Boolean,
        default() {
            return false
        },
    },
    currentNo: {
        required: true,
        type: String,
    },
    currentShopOrderNo: { required: true, type: String },
    currentDeliver: {
        type: Object as PropType<ApiOrder>,
        default: () => {},
    },
})
const $emit = defineEmits(['update:isShow'])
const _isShow = useVModel($props, 'isShow', $emit)
const reactiveComponent = reactive({
    notShipment: defineAsyncComponent(() => import('./components/notShipment.vue')),
})
const activeName = ref<'notShipment' | 'hasShipment'>('notShipment')
const handleDeliverDialogClose = () => {
    _isShow.value = false
}
</script>
<template>
    <el-dialog
        v-model="_isShow"
        :width="currentDeliver?.extra?.distributionMode === 'EXPRESS' ? '893px' : '810px'"
        :before-close="handleDeliverDialogClose"
        destroy-on-close
        top="7vh"
    >
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <p :id="titleId" style="font-size: 16px; font-weight: bold" :class="titleClass">
                    {{ currentDeliver?.extra?.distributionMode === 'VIRTUAL' ? '虚拟发货' : '快递发货' }}
                </p>
            </div>
        </template>
        <component
            :is="reactiveComponent[activeName as keyof typeof reactiveComponent]"
            v-model:is-show="_isShow"
            :current-no="$props.currentNo"
            :current-shop-order-no="$props.currentShopOrderNo"
        />
    </el-dialog>
</template>

<style lang="scss" scoped>
.demo-tabs > .el-tabs__content {
    padding: 32px;
    color: #6b778c;
    font-size: 32px;
    font-weight: 600;
}
</style>
