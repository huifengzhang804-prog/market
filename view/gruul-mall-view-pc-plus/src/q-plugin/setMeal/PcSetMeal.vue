<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import * as Request from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'
import { ElMessage } from 'element-plus'
import storage from '@/libs/storage'
import Decimal from 'decimal.js'
import { cloneDeep, debounce } from 'lodash-es'
import QIcon from '@/components/q-icon/q-icon.vue'

const props = defineProps({
  goodInfo: {
    type: Object,
    default: () => {},
  },
  shopInfo: {
    type: Object,
    default: () => {},
  },
})
</script>

<template>
  <q-plugin
    name="PcSetMeal"
    service="addon-matching-treasure"
    :context="{
      Request,
      DateUtil,
      cloneDeep,
      debounce,
      Decimal,
      UseConvert: useConvert,
      VueRouter: { useRouter, useRoute },
      Lodash: { cloneDeep, debounce },
      useVModel,
      ElementPlus: {
        ElMessage,
      },
      storage,
      QIcon,
    }"
    hide-on-miss
    :properties="{ goodInfo: props.goodInfo, shopInfo: props.shopInfo }"
  />
</template>

<style scoped></style>
