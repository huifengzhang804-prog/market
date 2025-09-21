<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { AddressFn } from '@/components/q-address'
import * as Request from '@/apis/http'
import { ElMessage } from 'element-plus'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData

const props = defineProps({
    deliverDialogFormData: { type: Object, default: () => {} },
    expressCompanyMap: { type: Object, default: () => {} },
    loadDeliverDialogFormData: {
        type: Function,
        default: () => {},
    },
    loadCompanySelectListData: {
        type: Function,
        default: () => {},
    },
    loadDeliveryAddressData: {
        type: Function,
        default: () => {},
    },
    loadexpressCompanyMapData: {
        type: Function,
        default: () => () => {},
    },
})
const QPluginRef = ref()
defineExpose({
    QPluginRef,
})
</script>
<template>
    <q-plugin
        ref="QPluginRef"
        :context="{
            Request,
            QAddressIndex: { AddressFn },
            GDRegionData: { regionData },
            ElementPlus: { ElMessage },
        }"
        :properties="props"
        hide-on-miss
        name="ShopShipmentSelect"
        service="gruul-mall-freight"
    />
</template>
