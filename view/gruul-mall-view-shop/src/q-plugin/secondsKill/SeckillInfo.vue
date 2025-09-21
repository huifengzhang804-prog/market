<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import { useEventListener, useVModel } from '@vueuse/core'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import * as Request from '@/apis/http'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import DateUtil from '@/utils/date'
import Decimal from 'decimal.js'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
</script>
<template>
    <q-plugin
        :context="{
            QSafeBtn,
            VueRouter: { useRouter, useRoute },
            VueUse: { useVModel, useEventListener },
            ElementPlus: { ElMessage, ElMessageBox },
            UseConvert,
            ShopInfoStore: { useShopInfoStore },
            Request,
            QChooseGoodsPopup,
            DateUtil,
            Decimal,
        }"
        name="ShopSeckillInfo"
        service="addon-seckill"
    />
</template>
