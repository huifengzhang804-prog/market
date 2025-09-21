<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import Decimal from 'decimal.js'
import VueClipboard3 from 'vue-clipboard3'
import QAddress from '@/components/q-address/q-address.vue'
import { AddressFn } from '@/components/q-address'
import DateUtil from '@/utils/date'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            Decimal,
            ElementPlus: { ElMessageBox, ElMessage },
            VueClipboard3,
            GDRegionData: { regionData },
            QAddressIndex: { AddressFn },
            Request,
            DateUtil,
            QAddress,
            UseConvert,
        }"
        name="PlatformPurchaseInfo"
        service="addon-supplier"
    />
</template>
