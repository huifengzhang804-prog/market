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
    name="PcIntegralShop"
    service="addon-integral"
    :context="{
      Request,
      DateUtil,
      UseConvert: useConvert,
      VueRouter: { useRouter, useRoute },
      useVModel,
      ElementPlus: {
        ElMessage,
      },
      ToTopGoCar,
    }"
    hide-on-miss
    :properties="{ otherData: props?.otherData }"
    dev-url="http://192.168.1.99:5178"
  />
</template>

<style scoped></style>
