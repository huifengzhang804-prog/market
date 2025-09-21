<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useEventListener } from '@vueuse/core'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import Decimal from 'decimal.js'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            VueUse: { useEventListener },
            UseConvert,
            Request,
            ElementPlus: { ElMessageBox, ElMessage },
            DateUtil,
            Decimal,
        }"
        name="PlatformSeckillInfo"
        service="addon-seckill"
    />
</template>
