<template>
    <q-plugin
        :context="{
            QSafeBtn,
            VueRouter: { useRouter, useRoute },
            DateUtil,
            ShopInfoStore: { useShopInfoStore },
            UseConvert,
            Request: { get, post },
            QChooseGoodsPopup,
            QInputNumber,
            VueUse: { useVModel },
            ElementPlus: { ElMessage },
        }"
        name="ShopAddBargain"
        service="addon-bargain"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import DateUtil from '@/utils/date'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { get, post } from '@/apis/http'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import QInputNumber from '@/components/q-input-number/q-input-number.vue'
import { ElMessage } from 'element-plus'
import { useVModel } from '@vueuse/core'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
</script>

<style scoped></style>
