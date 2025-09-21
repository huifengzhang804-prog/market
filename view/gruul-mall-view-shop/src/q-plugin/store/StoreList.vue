<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRouter } from 'vue-router'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import * as Request from '@/apis/http'
import SliderCaptcha from '@/components/slide-captcha/SliderCaptcha.vue'
import { REGEX_MOBILE } from '@/libs/validate'
import { ElMessageBox, ElMessage } from 'element-plus'
import selectMaterial from '@/views/material/selectMaterial.vue'
import SchemaForm from '@/components/SchemaForm.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import SetPinter from '@/q-plugin/components/printer.vue'
import SetPrintTemplate from '@/q-plugin/components/printTemplate.vue'
import SetPrintingTask from '@/q-plugin/components/printingTask.vue'
import { doGetCaptchaSlider } from '@/apis/sign'
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter },
            PageManage: BetterPageManage,
            Request,
            SliderCaptcha,
            LibsValidate: { REGEX_MOBILE },
            ElementPlus: { ElMessageBox, ElMessage },
            SignAPI: { doGetCaptchaSlider },
            selectMaterial,
            SchemaForm,
            ChromeTabs,
            SetPinter,
            SetPrintTemplate,
            SetPrintingTask,
        }"
        name="ShopStoreList"
        service="addon-shop-store"
    />
</template>
