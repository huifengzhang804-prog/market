<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'
import { ElMessage } from 'element-plus'
import PageManageTwo from '@/components/PageManage.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'

const props = defineProps({
    userId: {
        type: [String, Number],
        default: '',
    },
})
</script>
<template>
    <q-plugin
        :context="{ UseConvert, Request, PageManageTwo, ElementPlus: { ElMessage } }"
        :properties="props"
        hide-on-miss
        name="VipIntegralSubsidiary"
        service="addon-integral"
    />
</template>
