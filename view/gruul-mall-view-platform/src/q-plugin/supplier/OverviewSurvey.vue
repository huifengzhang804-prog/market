<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'

const prop = defineProps({
    supplierData: {
        type: Object,
        default: () => {},
    },
})
</script>
<template>
    <q-plugin :properties="prop.supplierData" hide-on-miss name="PlatformOverviewSurvey" service="addon-supplier" />
</template>
