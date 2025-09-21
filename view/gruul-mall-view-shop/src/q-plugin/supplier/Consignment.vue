<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
import MCard from '@/components/MCard.vue'
import PageManageTwo from '@/components/PageManage.vue'
import * as Request from '@/apis/http'
import { cloneDeep, debounce } from 'lodash-es'
import { ElMessage, ElMessageBox } from 'element-plus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import Decimal from 'decimal.js'
import information from '@/assets/images/icon/information.png'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            MCard,
            Lodash: { cloneDeep, debounce },
            PageManageTwo,
            QTable,
            QTableColumn,
            Decimal,
            SchemaForm,
            ChromeTabs,
            information,
            ElementPlus: { ElMessage, ElMessageBox },
            ShopInfoStore: { useShopInfoStore },
            UseConvert,
            Request,
            QIcon,
        }"
        name="ShopConsignment"
        service="addon-supplier"
    />
</template>
