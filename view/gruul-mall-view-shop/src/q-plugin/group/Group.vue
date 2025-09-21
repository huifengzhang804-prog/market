<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            ShopInfoStore: { useShopInfoStore },
            UtilsHttp: { http },
            VueUse: { useVModel },
            UseConvert,
            PageManageTwo,
            SchemaForm,
            ChromeTabs,
            Storage,
            ElementPlus: { ElMessage, ElMessageBox },
            ElementPlusIconsVue: { Search },
        }"
        name="ShopGroup"
        service="addon-team"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { http } from '@/utils/http'
import { useVModel } from '@vueuse/core'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import PageManageTwo from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import Storage from '@/utils/Storage'
</script>

<style scoped></style>
