<template>
    <q-plugin
        :context="{
            UseConvert,
            Request: { post, get, del, put },
            VueRouter: { useRouter, useRoute },
            ElementPlusIconsVue,
            DateUtil,
            PageManageTwo,
            QChooseGoodsPopup,
            ElementPlus: { ElMessage, ElMessageBox, ElIcon },
            QIcon,
            MCard,
            VueClipboard3,
            Decimal,
            QTable,
            QTableColumn,
            SchemaForm,
            ChromeTab,
            GoodAPI: {
                doGetRetrieveProduct,
            },
        }"
        name="ShopDistribute"
        service="addon-distribute"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { post, get, del, put } from '@/apis/http'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import DateUtil from '@/utils/date'
import PageManageTwo from '@/components/PageManage.vue'
import QChooseGoodsPopup from '@/components/q-choose-goods-popup/q-choose-goods-popup.vue'
import { ElMessage, ElMessageBox, ElIcon } from 'element-plus'
import QIcon from '@/components/q-icon/q-icon.vue'
import MCard from '@/components/MCard.vue'
import VueClipboard3 from 'vue-clipboard3'
import Decimal from 'decimal.js'
import QTable from '@components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@components/qszr-core/packages/q-table/q-table-column.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTab from '@/components/ChromeTab.vue'
import { useRouter, useRoute } from 'vue-router'
import { doGetRetrieveProduct } from '@/apis/good'
</script>
