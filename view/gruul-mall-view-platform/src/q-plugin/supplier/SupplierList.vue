<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { ElMessageBox, ElMessage } from 'element-plus'

import SchemaForm from '@/components/SchemaForm.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import { useRoute, useRouter } from 'vue-router'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { Shop } from '@element-plus/icons-vue'
import { doChangeStatus, doDelShop, doShopAudit, doGetShopList, storeStatusList, doReAudit, doGetShopRejectReason } from '@/apis/shops'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import QTooltip from '@/components/q-tooltip/q-tooltip.vue'
import Storage from '@/utils/Storage'
import ChromeTab from '@/components/chrome-tab/index.vue'
</script>
<template>
    <q-plugin
        :context="{
            SchemaForm,
            PageManage,
            QTable,
            QTableColumn,
            VueRouter: { useRoute, useRouter },
            UseConvert,
            ShopAPI: { doChangeStatus, doDelShop, doShopAudit, doGetShopList, storeStatusList, doReAudit, doGetShopRejectReason },
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { Shop },
            QDropdownBtn,
            QTooltip,
            Storage,
            ChromeTab,
        }"
        name="PlatformSupplierList"
        service="addon-supplier"
    />
</template>
