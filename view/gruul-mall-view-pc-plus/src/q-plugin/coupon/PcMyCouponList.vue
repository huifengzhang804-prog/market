<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
</script>

<template>
  <q-plugin
    name="PcMyCouponList"
    service="addon-coupon"
    :context="{ Request, DateUtil, UseConvert: useConvert, VueRouter: { useRouter, useRoute }, useVModel, ToTopGoCar }"
    hide-on-miss
    dev-url="http://172.24.176.1:5175/"
  />
</template>

<style scoped></style>
