<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'

import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage.vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import { CircleClose } from '@element-plus/icons-vue'
import { cloneDeep } from 'lodash-es'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'
</script>
<template>
    <q-plugin
        :context="{
            Request,
            PageManage,
            QTable,
            QTableColumn,
            QUpload,
            SchemaForm,
            ChromeTab,
            ElementPlusIconsVue: { CircleClose },
            Lodash: { cloneDeep },
        }"
        name="PlatformAppLiveList"
        service="addon-live"
    />
</template>
