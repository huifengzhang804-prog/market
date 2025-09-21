<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import useConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
import DateUtil from '@/utils/date'
import { Search, Check } from '@element-plus/icons-vue'
import { useVModel } from '@vueuse/core'
import * as Request from '@/apis/http'
import { doGetHighestCategoryLevel } from '@/apis/good'
import { cloneDeep } from 'lodash-es'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetRetrieveCommodity } from '@/apis/decoration'
import { ElMessageBox, ElMessage } from 'element-plus'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
</script>

<template>
    <q-plugin
        :context="{
            QSafeBtn,
            UseConvert: useConvert,
            VueRouter: { useRouter, useRoute },
            DateUtil,
            VueUse: { useVModel },
            Request,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { Search, Check },
            GoodAPI: { doGetHighestCategoryLevel },
            Lodash: { cloneDeep },
            ShopInfoStore: { useShopInfoStore },
            DecorationAPI: { doGetRetrieveCommodity },
            PageManage: BetterPageManage,
        }"
        name="ShopCouponInfo"
        service="addon-coupon"
    />
</template>
