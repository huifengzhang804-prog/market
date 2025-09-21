<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageManage from '@/components/PageManage.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import * as Request from '@/apis/http'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { Search } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            VueUse: { useVModel },
            ElementPlus: { ElMessage, ElMessageBox },
            ElementPlusIconsVue: { Search },
            UseConvert,
            PageManage: PageManage,
            ShopInfoStore: { useShopInfoStore },
            Request,
            SchemaForm,
        }"
        name="ShopSeckillList"
        service="addon-seckill"
    />
</template>
