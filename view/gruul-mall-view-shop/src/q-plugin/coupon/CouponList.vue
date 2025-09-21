<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import { useVModel } from '@vueuse/core'
import useConvert from '@/AutoImportCustomUse/useConvert'
import PageManage from '@/components/PageManage.vue'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            VueUse: { useVModel },
            PageManage: PageManage,
            Request,
            DateUtil,
            SchemaForm,
            UseConvert: useConvert,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { Search },
        }"
        name="ShopCouponList"
        service="addon-coupon"
    />
</template>
