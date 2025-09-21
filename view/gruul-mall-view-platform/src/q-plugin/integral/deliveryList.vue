<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            Storage,
            UseConvert,
            handleGetCompanyName,
            QTableColumn,
            QTable,
            Request: { get, post, del, put },
            ElementPlus: { ElMessage },
        }"
        name="DeliveryList"
        service="addon-integral"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import Storage from '@/utils/Storage'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import handleGetCompanyName from '@/assets/json/data.json'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import { get, post, del, put } from '@/apis/http'
import { ElMessage } from 'element-plus'
</script>

<style scoped></style>
