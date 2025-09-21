<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as Request from '@/apis/http'
import { REGEX } from '@/constant'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManageTwo from '@/components/PageManage.vue'
</script>

<template>
    <q-plugin
        :context="{
            ShopInfoStore: { useShopInfoStore },
            ElementPlus: { ElMessage, ElMessageBox },
            Request,
            ElTableEmpty,
            PageManageTwo,
            Constant: { REGEX },
        }"
        name="ShopInvoiceHeader"
        service="addon-supplier"
        hide-on-miss
    />
</template>
