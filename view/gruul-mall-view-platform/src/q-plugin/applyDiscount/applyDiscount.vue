<template>
    <q-plugin
        :context="{
            VueUse: { useVModel },
            ElementPlusIconsVue: { Search },
            VueRouter: { useRouter },
            UseConvert,
            PageManage: BetterPageManage,
            Request: { post, get, put, del },
            ElementPlus: { ElMessageBox, ElMessage },
            SchemaForm,
            ElTableEmpty,
        }"
        name="PlatformAppluDiscount"
        service="addon-full-reduction"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import { Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { post, get, put, del } from '@/apis/http'
import SchemaForm from '@/components/SchemaForm.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<style scoped></style>
