<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { ElMessage } from 'element-plus'
import { doGetSupplierBasicSet, doPutSupplierBasicSet } from '@/apis/setting'
const qPlugin = ref<InstanceType<typeof QPlugin> | null>(null)
defineExpose({ qPlugin })
</script>
<template>
    <q-plugin
        ref="qPlugin"
        :context="{
            SettingAPI: { doGetSupplierBasicSet, doPutSupplierBasicSet },
            ElementPlus: { ElMessage },
        }"
        name="PlatformPurchaseOrderConfig"
        service="addon-supplier"
        :hide-on-miss="true"
    />
</template>
