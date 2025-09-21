<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            VueUse: { useVModel },
            PageManageTwo,
            ShopInfoStore: { useShopInfoStore },
            Request: { get, post, put, del, patch },
            UseConvert,
            SchemaForm,
            ChromeTabs,
            Storage,
            ElementPlusIconsVue: { Search },
            ElementPlus: { ElMessageBox, ElMessage },
        }"
        name="ShopBargain"
        service="addon-bargain"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import { useVModel } from '@vueuse/core'
import PageManageTwo from '@/components/PageManage.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { get, post, put, del, patch } from '@/apis/http'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { Search } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import Storage from '@/utils/Storage'
</script>

<style scoped></style>
