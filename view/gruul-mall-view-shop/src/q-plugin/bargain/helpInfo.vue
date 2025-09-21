<template>
    <q-plugin
        :context="{ VueRouter: { useRoute, useRouter }, Request: { get, post }, UseConvert, PageManageTwo }"
        name="ShopHelpInfo"
        service="addon-bargain"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import { get, post } from '@/apis/http'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import PageManageTwo from '@/components/PageManage.vue'
</script>

<style scoped></style>
