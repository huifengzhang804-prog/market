<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import useConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

const userIdsStr = useRoute().query.ids
const router = useRouter()
if (!userIdsStr) {
    nextTick(() => router.back())
}
let userIds: Long[] = []
if (typeof userIdsStr === 'string') {
    userIds = JSON.parse(userIdsStr)
} else {
    userIds = userIdsStr as Long[]
}
</script>
<template>
    <q-plugin
        :context="{
            VueUse: { useVModel },
            Request,
            DateUtil,
            UseConvert: useConvert,
            ElementPlus: { ElMessageBox, ElMessage },
        }"
        :properties="{ userIds, productType: 'ALL', toBack: () => router.back() }"
        name="CouponGift"
        service="addon-coupon"
    />
</template>
