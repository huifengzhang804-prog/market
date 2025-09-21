<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            VueUse: { useVModel },
            ElementPlusIconsVue: { Search },
            PageManageTwo,
            Storage,
            Request: { get, post, del },
            ElementPlus: { ElMessageBox, ElMessage },
            selectMaterial,
            SchemaForm,
        }"
        name="ShopSetMeal"
        service="addon-matching-treasure"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { Search } from '@element-plus/icons-vue'
import PageManageTwo from '@/components/PageManage.vue'
import { get, post, del } from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import selectMaterial from '@/views/material/selectMaterial.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import Storage from '@/utils/Storage'
</script>

<style scoped></style>
