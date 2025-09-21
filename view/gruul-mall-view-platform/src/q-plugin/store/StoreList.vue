<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import PageManage from '@/components/PageManage.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import * as Request from '@/apis/http'
import { REGEX_MOBILE } from '@/libs/validate'
import { ElMessageBox, ElMessage } from 'element-plus'

import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            PageManage,
            Request,
            LibsValidate: { REGEX_MOBILE },
            ElementPlus: { ElMessageBox, ElMessage },
            SchemaForm,
            ElTableEmpty,
        }"
        name="PlatformStoreList"
        service="addon-shop-store"
    />
</template>
