<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { AddressFn } from '@/components/q-address'
import * as Request from '@/apis/http'
import { ElMessage } from 'element-plus'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData

const props = defineProps({
    deliverDialogFormData: { type: Object, default: () => {} },
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
        default: () => {},
    },
})
</script>
<template>
    <div>
        <q-plugin
            :context="{
                Request,
                QAddressIndex: { AddressFn },
                GDRegionData: { regionData },
                ElementPlus: { ElMessage },
            }"
            :properties="props"
            hide-on-miss
            name="ShopDeliveryListSelect"
            service="gruul-mall-freight"
        />
    </div>
</template>
