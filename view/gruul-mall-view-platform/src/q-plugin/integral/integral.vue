<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { get, post, del, put } from '@/apis/http'
import { Search, ArrowDown, WarningFilled } from '@element-plus/icons-vue'
import PageManageTwo from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { usePlatformGoodStatus } from '@/composables/usePlatformGoodStatus'
import VueClipboard3 from 'vue-clipboard3'
import * as Lodash from 'lodash-es'
import { useVModel } from '@vueuse/core'
import DateUtil from '@/utils/date'
import RemarkFlag from '@/components/remark/remark-flag.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import QAddress from '@/components/q-address/q-address.vue'

import Editor from '@/components/q-editor/editor.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import PlatformComp from '@/views/order/platform.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'
import Storage from '@/utils/Storage'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
</script>

<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            UseConvert,
            Request: { get, post, del, put },
            ElementPlusIconsVue: { Search, ArrowDown, WarningFilled },
            PageManageTwo,
            QTable,
            QTableColumn,
            PlatformGoodStatus: { usePlatformGoodStatus },
            VueClipboard3,
            Lodash,
            VueUse: { useVModel },
            DateUtil,
            RemarkFlag,
            RemarkPopup,
            QAddress,
            Editor,
            PlatformComp,
            SchemaForm,
            ChromeTab,
            ElementPlus: { ElMessageBox, ElMessage },
            GDRegionData: { regionData },
            Storage,
        }"
        name="Integral"
        service="addon-integral"
    />
</template>

<style scoped></style>
