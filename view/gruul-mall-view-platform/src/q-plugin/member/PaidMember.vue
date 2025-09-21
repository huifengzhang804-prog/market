<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import { cloneDeep } from 'lodash-es'
</script>
<template>
    <q-plugin
        :context="{
            Request,
            UseConvert: useConvert,
            ElementPlus: { ElMessageBox, ElMessage },
            Lodash: { cloneDeep },
        }"
        hide-on-miss
        name="PaidMember"
        service="addon-member"
    />
</template>
