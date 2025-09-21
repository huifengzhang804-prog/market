<template>
    <q-plugin
        :context="{
            VueUse: { useVModel },
            ElementPlusIconsVue: { Search },
            VueRouter: { useRoute, useRouter },
            Request: { get, post, del },
            PageManageTwo,
            SchemaForm,
            ElementPlus: { ElMessage, ElMessageBox },
            ElTableEmpty,
        }"
        name="PlatformSetMeal"
        service="addon-matching-treasure"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import { Search } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post, del } from '@/apis/http'
import PageManageTwo from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<style scoped></style>
