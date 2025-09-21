<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            UtilsHttp: { http },
            GoodAPI: {
                doGetRetrieveProduct,
            },
            UseConvert,
            ElementPlus: {
                ElMessage,
            },
        }"
        name="PlatformGroupDetail"
        service="addon-team"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/utils/http'
import { doGetRetrieveProduct } from '@/apis/good'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { ElMessage } from 'element-plus'
</script>

<style scoped></style>
