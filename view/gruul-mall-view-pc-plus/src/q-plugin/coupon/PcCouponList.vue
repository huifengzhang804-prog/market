<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'

const props = defineProps({
  otherData: { type: Object, default: () => ({}) },
})
</script>

<template>
  <q-plugin
    name="PcCouponList"
    service="addon-coupon"
    :context="{
      Request,
      DateUtil,
      ElementPlus: {
        ElMessage,
      },
      UseConvert: useConvert,
      VueRouter: { useRouter, useRoute },
      useVModel,
      ToTopGoCar,
    }"
    hide-on-miss
    :properties="{ otherData: props?.otherData }"
    dev-url="http://192.168.1.99:5180/"
  />
</template>

<style scoped></style>
