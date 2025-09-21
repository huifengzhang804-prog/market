<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
// import DateUtil from '@/utils/date'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManage from '@/components/PageManage.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            VueUse: { useVModel },
            UseConvert,
            Request,
            PageManage: PageManage,
            ChromeTab,
            SchemaForm,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { Search },
            ElTableEmpty,
        }"
        name="PlatformSeckillList"
        service="addon-seckill"
    />
</template>
