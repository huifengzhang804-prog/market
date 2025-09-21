<template>
    <q-plugin
        :context="{
            UseConvert,
            VueClipboard3,
            GoodAPI: { doGetSeachSupplierSearchList, doGetSupplierCommodityDetails },
            ShopAPI: { doGetCategory },
            VueRouter: { useRouter, useRoute },
            PageManage: BetterPageManage,
            QTable,
            QTableColumn,
            SchemaForm,
            noShopExamine,
            Request: { get, post, put, del },
            ElTableEmpty,
            QIcon,
            auditGoodsVue,
            ElementPlus: { ElMessage },
        }"
        name="PlatformProductReviewList"
        service="addon-supplier"
    />
</template>

<script lang="ts" setup>
import VueClipboard3 from 'vue-clipboard3'
import QPlugin from '@/q-plugin/index.vue'
import { get, post, put, del } from '@/apis/http'
import { useRouter, useRoute } from 'vue-router'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import { doGetSeachSupplierSearchList } from '@/apis/good'
import { doGetCategory } from '@/apis/shops'
import noShopExamine from '@/assets/image/no_shop_examine.png'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { doGetSupplierCommodityDetails } from '@/apis/good'
import QIcon from '@/components/q-icon/q-icon.vue'
import auditGoodsVue from '@/views/good/examine/shop/components/audit-goods.vue'
import { ElMessage } from 'element-plus'
</script>
