<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import useConvert from '@/AutoImportCustomUse/useConvert'
import PageManage from '@/components/PageManage.vue'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import MCard from '@/components/MCard.vue'
import { cloneDeep } from 'lodash-es'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            Request,
            PageManage,
            UseConvert: useConvert,
            ElementPlus: { ElMessageBox, ElMessage },
            QTable,
            QTableColumn,
            SchemaForm,
            ChromeTabs,
            Lodash: { cloneDeep },
            MCard,
        }"
        name="ShopAppLiveList"
        service="addon-live"
    />
</template>
