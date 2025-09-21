<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'

const props = defineProps({
    disable: {
        type: Boolean,
        default: false,
    },
})
</script>
<template>
    <q-plugin
        :context="{
            Request,
        }"
        :properties="props"
        hide-on-miss
        name="CityDistributionCheckBox"
        service="addon-ic"
    />
</template>
