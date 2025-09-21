<script lang="ts" setup>
import QMapcity from '@/components/q-mapcity/q-mapcity.vue'
import QInputNumber from '@/components/q-input-number/q-input-number.vue'
import QPlugin from '@/q-plugin/index.vue'
import * as Request from '@/apis/http'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import DescStep from '@/views/set/components/DescStep.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import PageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { REGEX_MOBILE } from '@/libs/validate'
import { debounce, get } from 'lodash-es'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import ChromeTabs from '@/components/ChromeTab.vue'
import SetPinter from '@/q-plugin/components/printer.vue'
import SetPrintTemplate from '@/q-plugin/components/printTemplate.vue'
import SetPrintingTask from '@/q-plugin/components/printingTask.vue'
</script>
<template>
    <q-plugin
        :context="{
            Request,
            ElementPlus: { ElMessageBox, ElMessage },
            QInputNumber,
            ShopInfoStore: { useShopInfoStore },
            LibsValidate: { REGEX_MOBILE },
            QMapcity,
            TitleBar,
            DescStep,
            QIcon,
            ElTableEmpty,
            PageManage,
            UseConvert,
            Lodash: { debounce, get },
            ChromeTabs,
            SetPinter,
            SetPrintTemplate,
            SetPrintingTask,
        }"
        name="CityDistribution"
        service="addon-ic"
    />
</template>
