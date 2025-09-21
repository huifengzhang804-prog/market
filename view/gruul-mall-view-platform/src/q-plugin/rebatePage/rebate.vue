<template>
    <q-plugin
        :context="{
            UseConvert,
            ElementPlusIconsVue: { QuestionFilled, ArrowDown },
            VueClipboard3,
            DateUtil,
            Lodash,
            Request: { get, post, put },
            PageManageTwo,
            Decimal,
            RemarkPopup,
            VueUse: { useVModel },
            ElementPlus: { ElMessage },
            SchemaForm,
            ChromeTab,
            ElTableEmpty,
            QIcon,
        }"
        name="Rebate"
        service="addon-rebate"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { QuestionFilled, ArrowDown } from '@element-plus/icons-vue'
import VueClipboard3 from 'vue-clipboard3'
import DateUtil from '@/utils/date'
import * as Lodash from 'lodash-es'
import { get, post, put } from '@/apis/http'
import PageManageTwo from '@/components/PageManage.vue'
import RemarkPopup from '@/components/remark/remark-popup.vue'

import Decimal from 'decimal.js'
import { useVModel } from '@vueuse/core'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTab from '@/components/chrome-tab/index.vue'
import { ElMessage } from 'element-plus'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
</script>

<style scoped></style>
