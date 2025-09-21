<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { InfoFilled, Microphone, Mute, Wallet } from '@element-plus/icons-vue'
</script>
<template>
    <q-plugin
        :context="{
            ElementPlusIconsVue: { InfoFilled, Microphone, Mute, Wallet },
        }"
        name="ShopAppLiveOperation"
        service="addon-live"
    />
</template>
