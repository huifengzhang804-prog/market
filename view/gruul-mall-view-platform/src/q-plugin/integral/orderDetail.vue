<template>
    <q-plugin
        :context="{
            UseConvert,
            VueRouter: { useRoute },
            Request: { get, post, del },
            RemarkView,
            QAddress,
            handleGetCompanyName,
            ApisAfs: { doGetLogisticsTrajectoryByWaybillNo },
        }"
        name="OrderDetail"
        service="addon-integral"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRoute } from 'vue-router'
import { get, post, del } from '@/apis/http'
import RemarkView from '@/views/order/orderDetails/components/remark-view.vue'
import QAddress from '@/components/q-address/q-address.vue'
import receiverCompanyList from '@/assets/json/data.json'
import { doGetLogisticsTrajectoryByWaybillNo } from '@/apis/afs'

const handleGetCompanyName = (expressCompanyName: string) => {
    return receiverCompanyList.find((item) => item.companyCode === expressCompanyName)?.companyName
}
</script>

<style scoped></style>
