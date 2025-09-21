<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import Decimal from 'decimal.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useConversionPrice } from '@/utils/useConversionPrice'
import DateUtils from '@/utils/date'

const props = defineProps({
  otherData: { type: Object, default: () => ({}) },
})
</script>

<template>
  <q-plugin
    :context="{
      Request,
      DateUtils,
      UseConvert: useConvert,
      VueRouter: { useRouter, useRoute },
      useVModel,
      Decimal,
      ElementPlus: {
        ElMessage,
        ElMessageBox,
      },
      useConversionPrice,
    }"
    :properties="{ otherData: props?.otherData }"
    hide-on-miss
    name="PcSeckillList"
    service="addon-seckill"
  />
</template>
