<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
    disable: { type: Boolean, default: false },
    templateId: { type: [String], default: '0' },
    templateChange: {
        type: Function,
        default: (template: string) => {},
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
        service="gruul-mall-freight"
    />
</template>
