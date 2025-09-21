<template>
    <q-plagin
        :context="{
            UseConvert,
            VueRouter: { useRouter, useRoute },
            VueUse: { useVModel },
            ElementPlusIconsVue: { Search },
            PageManageTwo,
            SchemaForm,
            UtilsHttp: { http },
            ElementPlus: { ElMessageBox, ElMessage },
            ElTableEmpty,
        }"
        name="PlatformGroup"
        service="addon-team"
    />
</template>

<script lang="ts" setup>
import QPlagin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { Search } from '@element-plus/icons-vue'
import PageManageTwo from '@/components/PageManage.vue'
import { http } from '@/utils/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<style scoped></style>
