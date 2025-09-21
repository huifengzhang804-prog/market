<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { post, get } from '@/apis/http'
import DateUtil from '@/utils/date'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import Decimal from 'decimal.js'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { doGetRetrieveCommodity } from '@/apis/decoration'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
</script>

<template>
    <q-plugin
        :context="{
            QSafeBtn,
            VueRouter: { useRouter, useRoute },
            Request: { post, get },
            DateUtil,
            ShopInfoStore: {
                useShopInfoStore,
            },
            UseConvert,
            Decimal,
            QChooseGoodsPopup,
            ElementPlus: {
                ElMessageBox,
                ElMessage,
            },
            DecorationAPI: {
                doGetRetrieveCommodity,
            },
        }"
        name="ShopAddDiscountActive"
        service="addon-full-reduction"
    />
</template>

<style lang="scss" scoped></style>
