<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            DateUtil,
            Request: { get, post, put, del },
            UseConvert,
            QInputNumber,
            ElementPlus: { ElMessage, ElMessageBox },
        }"
        name="PlatformAddBargain"
        service="addon-bargain"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { get, post, put, del } from '@/apis/http'
import DateUtil from '@/utils/date'
import QInputNumber from '@/components/q-input-number/q-input-number.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { ElMessage, ElMessageBox } from 'element-plus'
</script>

<style scoped></style>
