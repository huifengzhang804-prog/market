<template>
    <q-plugin
        :context="{ VueRouter: { useRoute, useRouter }, DateUtil, UseConvert, Request: { get, post, del }, QUpload }"
        name="PlatformAddSetMeal"
        service="addon-matching-treasure"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import DateUtil from '@/utils/date'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { get, post, del } from '@/apis/http'
import QUpload from '@/components/q-upload/q-upload.vue'
</script>

<style scoped></style>
