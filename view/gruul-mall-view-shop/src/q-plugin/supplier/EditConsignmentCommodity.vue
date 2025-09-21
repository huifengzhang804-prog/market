<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'
import { ElMessage } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import Decimal from 'decimal.js'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
</script>

<template>
    <q-plugin
        :context="{
            QPlugin,
            Request,
            ShopInfoStore: { useShopInfoStore },
            ElementPlus: { ElMessage },
            Decimal,
            UseConvert,
            VueRouter: { useRouter, useRoute },
        }"
        name="ShopEditConsignmentCommodity"
        service="addon-supplier"
    />
</template>
