<template>
    <q-plugin
        :context="{
            QSafeBtn,
            VueRouter: { useRouter, useRoute },
            DateUtil,
            UseConvert,
            UtilsHttp: { http },
            GoodAPI: { doGetRetrieveProduct },
            QChooseGoodsPopup,
            ElementPlus: {
                ElMessage,
                ElMessageBox,
            },
            Lodash: { cloneDeep },
        }"
        name="ShopGroupForm"
        service="addon-team"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import DateUtil from '@/utils/date'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { http } from '@/utils/http'
import { doGetRetrieveProduct } from '@/apis/good'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import QSafeBtn from '@/components/QSafeBtn/QSafeBtn.vue'
</script>

<style scoped></style>
