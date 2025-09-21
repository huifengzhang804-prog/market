<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import DescStep from '@/views/set/components/DescStep.vue'
import StepDialog from '@/views/set/components/StepDialog.vue'
import { doUuptOpenConfigUpdateAndEdit, doGetUuptOpenConfigInfo } from '@/apis/set/thirdPartyDelivery'
import { ElMessage } from 'element-plus'
</script>
<template>
    <q-plugin
        :context="{
            GoodAPI: { doUuptOpenConfigUpdateAndEdit, doGetUuptOpenConfigInfo },
            TitleBar,
            DescStep,
            StepDialog,
            ElementPlus: { ElMessage },
        }"
        :properties="{
            doUuptOpenConfigUpdateAndEdit,
            doGetUuptOpenConfigInfo,
        }"
        name="PlatformThirdPartyDelivery"
        service="addon-ic"
    />
</template>

<style scoped></style>
