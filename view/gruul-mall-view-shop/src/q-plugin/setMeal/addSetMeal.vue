<template>
    <q-plugin
        :context="{
            QSafeBtn,
            VueRouter: { useRouter, useRoute },
            DateUtil,
            ShopInfoStore: { useShopInfoStore },
            ElementPlusIconsVue: { Search },
            UseConvert,
            QUpload,
            QChooseGoodsPopup,
            Request: { get, post, del },
            ElementPlus: { ElMessage },
            selectMaterial,
            Lodash: {
                cloneDeep,
            },
        }"
        name="ShopAddSetMeal"
        service="addon-matching-treasure"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter, useRoute } from 'vue-router'
import DateUtil from '@/utils/date'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import QUpload from '@/components/q-upload/q-upload.vue'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import { get, post, del } from '@/apis/http'
import { ElMessage } from 'element-plus'
import selectMaterial from '@/views/material/selectMaterial.vue'
import { Search } from '@element-plus/icons-vue'
import { cloneDeep } from 'lodash-es'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
</script>

<style scoped></style>
