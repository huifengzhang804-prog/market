<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import * as Request from '@/apis/http'
import { ElMessage } from 'element-plus'
import supplierToBeAudited from '@/assets/image/supplier_to_be_audited.png'
const prop = defineProps({
    loadSupplierData: {
        type: Function,
        default: () => {},
    },
})
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            Request,
            supplierToBeAudited,
            ElementPlus: { ElMessage },
        }"
        :properties="prop"
        hide-on-miss
        name="PlatformOverviewSchedule"
        service="addon-supplier"
    />
</template>
