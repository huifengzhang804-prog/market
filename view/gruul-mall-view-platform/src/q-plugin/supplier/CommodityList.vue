<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { ElMessage, ElUpload } from 'element-plus'

import { doGetCategory, doGetShopList } from '@/apis/shops'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManageTwo from '@/components/PageManage.vue'
import { useRoute } from 'vue-router'
import QUpload from '@/components/q-upload/q-upload.vue'
import { usePlatformGoodStatus } from '@/composables/usePlatformGoodStatus'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import VueClipboard3 from 'vue-clipboard3'
import {
    doGetSupplierCommodityDetails,
    doGetSeachSupplierSearchList,
    doGetSupplierList,
    doUpdateSupplierSellStatus,
    doPostSupplierRestoreSale,
} from '@/apis/good'
import { cloneDeep } from 'lodash-es'
import { CircleClose } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'

const VITE_PLATFORM_NAME = import.meta.env.VITE_PLATFORM_NAME
</script>
<template>
    <q-plugin
        :context="{
            GoodAPI: {
                doGetSupplierCommodityDetails,
                doGetSeachSupplierSearchList,
                doGetSupplierList,
                doUpdateSupplierSellStatus,
                doPostSupplierRestoreSale,
            },
            ElementPlus: { ElMessage, ElUpload },
            ShopAPI: { doGetCategory, doGetShopList },
            VueRouter: { useRoute },
            QTable,
            QTableColumn,
            UseConvert,
            QUpload,
            PageManageTwo,
            PlatformGoodStatus: { usePlatformGoodStatus },
            Lodash: { cloneDeep },
            ElementPlusIconsVue: { CircleClose },
            VueClipboard3,
            SchemaForm,
            ElTableEmpty,
        }"
        :properties="{ VITE_PLATFORM_NAME }"
        name="PlatformCommodityList"
        service="addon-supplier"
    />
</template>
