<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/AutoImportCustomUse/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { doGetRetrieveProduct } from '@/apis/good'
</script>
<template>
    <q-plugin
        :context="{
            Request,
            DateUtil,
            UseConvert: useConvert,
            VueRouter: { useRouter, useRoute },
            VueUse: { useVModel },
            GoodAPI: { doGetRetrieveProduct },
        }"
        name="PlatformCouponInfo"
        service="addon-coupon"
    />
</template>
