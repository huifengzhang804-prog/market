<template>
    <q-plugin
        :context="{
            UseConvert,
            VueRouter: { useRoute, useRouter },
            VueUse: { useVModel },
            ElementPlusIconsVue: { Search },
            PageManageTwo,
            SchemaForm,
            Request: { get, post, put, del, patch },
            ElementPlus: { ElMessage, ElMessageBox },
            ElTableEmpty,
        }"
        name="PlatformBargain"
        service="addon-bargain"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRoute, useRouter } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { Search } from '@element-plus/icons-vue'
import PageManageTwo from '@/components/PageManage.vue'
import { get, post, put, del, patch } from '@/apis/http'
import { ElMessage, ElMessageBox } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<style lang="scss" scoped></style>
