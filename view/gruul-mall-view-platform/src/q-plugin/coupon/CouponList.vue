<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueUse: { useVModel },
            VueRouter: { useRoute, useRouter },
            Request,
            DateUtil,
            UseConvert,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { Search },
            PageManage: BetterPageManage,
            SchemaForm,
            ElTableEmpty,
        }"
        service="addon-coupon"
        name="PlatformCouponList"
    />
</template>
