<template>
    <q-plugin
        :context="{
            UseConvert,
            VueRouter: { useRoute, useRouter },
            UtilsHttp: { http },
            PageManageTwo,
        }"
        name="ShopGroupOrderDetail"
        service="addon-team"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRoute, useRouter } from 'vue-router'
import { http } from '@/utils/http'
import PageManageTwo from '@/components/PageManage.vue'
</script>

<style scoped></style>
