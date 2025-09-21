<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'
import { ElMessage } from 'element-plus'

const props = defineProps({
    userId: {
        type: [String, Number],
        default: '',
    },
})
</script>
<template>
    <q-plugin
        :context="{ Request, ElementPlus: { ElMessage } }"
        :properties="props"
        hide-on-miss
        name="VipDistributionInformation"
        service="addon-distribute"
    />
</template>
