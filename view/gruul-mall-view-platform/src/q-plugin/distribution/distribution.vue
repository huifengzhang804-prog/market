<template>
    <q-plugin
        :context="{
            UseConvert,
            Request: { post, get, put },
            VueRouter: { useRouter, useRoute },
            PageManageTwo: PageManage,
            DateUtil,
            ElementPlusIconsVue: { ArrowDownBold },
            VueClipboard3,
            Decimal,
            QTable,
            QTableColumn,
            QEditor,
            SchemaForm,
            ChromeTab,
            QIcon,
            ElementPlus: { ElMessageBox, ElMessage },
            ElTableEmpty,
        }"
        name="PlatformDistribute"
        service="addon-distribute"
    />
</template>

<script lang="ts" setup>
import { post, get, put } from '@/apis/http'
import { ArrowDownBold } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import DateUtil from '@/utils/date'
import Decimal from 'decimal.js'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import VueClipboard3 from 'vue-clipboard3'
import QPlugin from '@/q-plugin/index.vue'
import PageManage from '@/components/PageManage.vue'

import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import QEditor from '@/components/q-editor/q-edit.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<style scoped></style>
