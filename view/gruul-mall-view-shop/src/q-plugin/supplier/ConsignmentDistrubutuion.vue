<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
import * as Request from '@/apis/http'
import { ElMessage, ElMessageBox } from 'element-plus'
import Decimal from 'decimal.js'
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            Decimal,
            ElementPlus: { ElMessage, ElMessageBox },
            UseConvert,
            Request,
        }"
        name="ShopConsignmentDistrubutuion"
        service="addon-supplier"
    />
</template>
