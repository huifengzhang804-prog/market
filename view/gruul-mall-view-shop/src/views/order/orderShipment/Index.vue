<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { PropType } from 'vue'
import { ApiOrder } from '../types/order'
// 非同城配送发货
import NotShipment from '@/views/order/orderShipment/components/notShipment.vue'
// 同城配送发货
import SameCityDelivery from '@/views/order/orderShipment/components/SameCityDelivery.vue'

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
const deliverType = ref($props.currentDeliver.extra.distributionMode)

const $emit = defineEmits(['update:isShow'])
const _isShow = useVModel($props, 'isShow', $emit)

const handleDeliverDialogClose = () => {
    _isShow.value = false
}
</script>
<template>
    <el-dialog v-model="_isShow" :width="deliverType === 'EXPRESS' ? '893px' : '810px'" :before-close="handleDeliverDialogClose" destroy-on-close>
        <template #header="{ titleId, titleClass }">
            <div class="my-header">
                <p :id="titleId" style="font-size: 16px; font-weight: bold" :class="titleClass">
                    {{ deliverType === 'INTRA_CITY_DISTRIBUTION' ? '同城配送' : deliverType === 'EXPRESS' ? '快递发货' : '虚拟发货' }}
                </p>
            </div>
        </template>
        <NotShipment
            v-if="deliverType !== 'INTRA_CITY_DISTRIBUTION'"
            v-model:isShow="_isShow"
            :currentNo="$props.currentNo"
            :currentShopOrderNo="$props.currentShopOrderNo"
        />
        <SameCityDelivery v-else v-model:isShow="_isShow" :currentNo="$props.currentNo" :currentShopOrderNo="$props.currentShopOrderNo" />
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
