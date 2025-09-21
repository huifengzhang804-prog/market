<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'

defineProps<{
  couponList: any[]
  cartCoupon: boolean
}>()
</script>

<template>
  <q-plugin
    name="PcCouponGoodDetails"
    service="addon-coupon"
    :context="{ Request, DateUtil, UseConvert: useConvert, VueRouter: { useRouter, useRoute }, useVModel, ElementPlus: { ElMessage } }"
    :properties="{ couponList, cartCoupon }"
    hide-on-miss
  />
</template>

<style scoped></style>
