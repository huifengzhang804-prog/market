<script lang="ts" setup>
import { defineProps } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'

const props = defineProps({
  disable: { type: Boolean, default: false },
  templateId: { type: String, default: '0' },
  templateChange: {
    type: Function,
    default: () => {},
  },
})
</script>
<template>
  <q-plugin
    :context="{
      VueRouter: { useRouter, useRoute },
      Request,
    }"
    :properties="props"
    hide-on-miss
    name="FreightCheckBox"
    service="addon-freight"
  />
</template>
