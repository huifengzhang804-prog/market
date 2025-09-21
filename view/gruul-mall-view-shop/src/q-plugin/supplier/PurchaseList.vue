<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute } from 'vue-router'
import MCard from '@/components/MCard.vue'
import DateUtil from '@/utils/date'
import PageManageTwo from '@/components/PageManage.vue'
import { ArrowDown } from '@element-plus/icons-vue'
import * as Request from '@/apis/http'
import { cloneDeep } from 'lodash-es'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import QUpload from '@/components/q-upload/q-upload.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import information from '@/assets/images/icon/information.png'
import paymentVoucher from '@/assets/images/icon/paymentVoucher.png'
import Decimal from 'decimal.js'
import VueClipboard3 from 'vue-clipboard3'
import { useVModel } from '@vueuse/core'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            MCard,
            Lodash: { cloneDeep },
            PageManageTwo,
            QTable,
            QTableColumn,
            Decimal,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { ArrowDown },
            ShopInfoStore: { useShopInfoStore },
            DateUtil,
            VueClipboard3,
            UseConvert,
            Request,
            ChromeTabs,
            SchemaForm,
            QUpload,
            information,
            paymentVoucher,
            ElTableEmpty,
            QIcon,
            VueUse: { useVModel },
        }"
        name="ShopPurchaseList"
        service="addon-supplier"
    />
</template>
