<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
</script>

<template>
    <q-plugin
        :context="{
            VueUse: { useVModel },
            Request,
            ElementPlus: { ElMessageBox, ElMessage },
            SchemaForm,
            PageManage,
            QIcon,
        }"
        name="VoucherRecord"
        service="addon-coupon"
    />
</template>
