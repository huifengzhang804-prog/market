<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            DateUtil,
            UseConvert,
            QChooseGoodsPopupFun: { formatGoodsPrice },
            Request: { post },
            GoodAPI: { doGetRetrieveProduct },
            Decimal,
        }"
        name="PlatformAddDiscountActive"
        service="addon-full-reduction"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import DateUtil from '@/utils/date'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { formatGoodsPrice } from '@/components/q-choose-goods-popup'
import { post } from '@/apis/http'
import Decimal from 'decimal.js'
import { doGetRetrieveProduct } from '@/apis/good'
</script>

<style scoped></style>
