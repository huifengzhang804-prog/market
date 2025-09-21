<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useVModel } from '@vueuse/core'
import useConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

const userIdsStr = useRoute().query.userIds
const router = useRouter()
if (!userIdsStr) {
    router.back()
}
const userIds = JSON.parse(userIdsStr as string)
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
        :properties="{ userIds, productType: 'SHOP_ALL', toBack: () => router.back() }"
        name="CouponGift"
        service="addon-coupon"
    />
</template>
