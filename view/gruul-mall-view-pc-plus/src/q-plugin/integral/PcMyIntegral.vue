<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { ElMessage, ElMessageBox } from 'element-plus'
import formatRichText from '@/utils/formatRichText'
import ToTopGoCar from '@/views/toTopGoCar/index.vue'
import OrderTrace from '@/views/personalcenter/assets/integral/componets/orderTrace.vue'
import Countdown from '@/components/countdown.vue'
import Teps from '@/components/teps.vue'
</script>

<template>
  <q-plugin
    name="PcMyIntegral"
    service="addon-integral"
    :context="{
      Request,
      DateUtil,
      UseConvert: useConvert,
      VueRouter: { useRouter, useRoute },
      useVModel,
      ElementPlus: {
        ElMessage,
        ElMessageBox,
      },
      formatRichText,
      ToTopGoCar,
      OrderTrace,
      Countdown,
      Teps,
    }"
    hide-on-miss
    dev-url="http://192.168.1.99:5178"
  />
</template>

<style scoped></style>
