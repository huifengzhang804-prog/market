<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { useRoute, useRouter } from 'vue-router'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { cloneDeep } from 'lodash-es'
import VueClipboard3 from 'vue-clipboard3'
import Decimal from 'decimal.js'
import { AddressFn } from '@/components/q-address'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { ArrowDown } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import QIcon from '@/components/q-icon/q-icon.vue'

const regionData = useGDRegionDataStore().getterGDRegionData
</script>
<template>
    <q-plugin
        :context="{
            PageManage: BetterPageManage,
            UseConvert,
            VueRouter: { useRoute, useRouter },
            Lodash: { cloneDeep },
            Decimal,
            ElementPlus: { ElMessageBox, ElMessage },
            VueClipboard3,
            GDRegionData: { regionData },
            QAddressIndex: { AddressFn },
            Request,
            DateUtil,
            ElementPlusIconsVue: { ArrowDown },
            SchemaForm,
            ElTableEmpty,
            QIcon,
        }"
        name="PlatformPurchaseList"
        service="addon-supplier"
    />
</template>
