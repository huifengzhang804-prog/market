<script lang="ts" setup>
import { defineProps } from 'vue'
import QPlugin from '@/q-plugin/index.vue'

const props = defineProps({
  disable: {
    type: Boolean,
    default: false,
  },
})
</script>
<template>
  <q-plugin :properties="props" hide-on-miss name="CityDistributionCheckBox" service="addon-ic" />
</template>
