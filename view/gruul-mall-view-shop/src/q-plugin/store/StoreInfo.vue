<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import QMap from '@/components/q-map/q-map.vue'
import { REGEX_MOBILE } from '@/libs/validate'
import selectMaterial from '@/views/material/selectMaterial.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            Request,
            QUpload,
            QMap,
            LibsValidate: { REGEX_MOBILE },
            ElementPlus: { ElMessageBox, ElMessage },
            selectMaterial,
        }"
        name="ShopStoreInfo"
        service="addon-shop-store"
    />
</template>
