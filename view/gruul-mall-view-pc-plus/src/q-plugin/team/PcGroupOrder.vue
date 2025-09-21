<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import useConvert from '@/composables/useConvert'
import { get } from '@/apis/http'
import DateUtil from '@/utils/date'
import { useRouter, useRoute } from 'vue-router'
import { useVModel } from '@vueuse/core'

const props = defineProps({
  shop: {
    type: Object,
    default: () => {},
  },
  order: {
    type: Object,
    default: () => {},
  },
})
</script>

<template>
  <q-plugin
    name="PcGroupOrder"
    service="addon-team"
    :context="{ Request: { get }, DateUtil, UseConvert: useConvert, VueRouter: { useRouter, useRoute }, useVModel }"
    :properties="{ shop: props.shop, order: props.order }"
    hide-on-miss
  />
  <!-- dev-url="http://192.168.1.207:5173" -->
</template>

<style scoped></style>
