<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'
import QIcon from '@/components/q-icon/q-icon.vue'
import { ElMessage } from 'element-plus'

const prop = defineProps({
    supplierData: {
        type: Object,
        default: () => {},
    },
})
</script>
<template>
    <q-plugin
        :context="{
            Request,
            ElementPlus: { ElMessage },
            QIcon,
        }"
        :properties="prop.supplierData"
        hide-on-miss
        name="PlatformOverviewBasic"
        service="addon-supplier"
    />
</template>
