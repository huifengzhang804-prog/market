<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
import DateUtil from '@/utils/date'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import Decimal from 'decimal.js'
import QAddress from '@/components/q-address/q-address.vue'
import VueClipboard3 from 'vue-clipboard3'
import { AddressFn } from '@/components/q-address'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import QUpload from '@/components/q-upload/q-upload.vue'

const regionData = useGDRegionDataStore().getterGDRegionData
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            Decimal,
            QAddress,
            QUpload,
            UseConvert,
            ElementPlus: { ElMessageBox, ElMessage },
            VueClipboard3,
            GDRegionData: { regionData },
            QAddressIndex: { AddressFn },
            Request,
            DateUtil,
        }"
        name="ShopPurchaseOrderInfo"
        service="addon-supplier"
    />
</template>
