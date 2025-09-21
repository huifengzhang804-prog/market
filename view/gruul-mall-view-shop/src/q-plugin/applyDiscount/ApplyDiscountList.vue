<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import { useVModel } from '@vueuse/core'
import PageManageTwo from '@/components/PageManage.vue'
import { post, get, del, put } from '@/apis/http'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import SchemaForm from '@/components/SchemaForm.vue'
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            VueUse: { useVModel },
            PageManageTwo,
            ElementPlusIconsVue: { Search },
            Request: { post, get, del, put },
            ShopInfoStore: { useShopInfoStore },
            UseConvert,
            SchemaForm,
            ElementPlus: { ElMessage, ElMessageBox },
        }"
        name="ShopApplyDiscount"
        service="addon-full-reduction"
    />
</template>

<style lang="scss" scoped></style>
