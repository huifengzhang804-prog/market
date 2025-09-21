<script setup lang="ts">
import { ref, reactive, defineAsyncComponent } from 'vue'
import { useVModel } from '@vueuse/core'

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
})
const $emit = defineEmits(['update:isShow'])
const _isShow = useVModel($props, 'isShow', $emit)
const reactiveComponent = reactive({
    notShipment: defineAsyncComponent(() => import('./components/notShipment.vue')),
    hasShipment: defineAsyncComponent(() => import('./components/hasShipment.vue')),
})
const activeName = ref<'notShipment' | 'hasShipment'>('notShipment')

const handleDeliverDialogClose = () => {
    _isShow.value = false
}
</script>
<template>
    <el-dialog v-model="_isShow" width="35%" :before-close="handleDeliverDialogClose" destroy-on-close>
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass">商品发货</h4>
            </div>
        </template>
        <component
            :is="reactiveComponent[activeName]"
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
