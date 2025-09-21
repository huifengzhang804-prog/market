<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import { useVModel } from '@vueuse/core'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import QAreaChoose from '@/components/q-area-choose/area-choose.vue'
import UtilsUuid from '@/utils/uuid'
import PageManageTwo from '@/components/PageManage.vue'
import { QuestionFilled } from '@element-plus/icons-vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import ChromeTabs from '@/components/ChromeTab.vue'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'
import { cloneDeep } from 'lodash-es'
import DeliveryOfficialKey from '@/assets/images/delivery_key.png'

const GD_regionData = useGDRegionDataStore().getterGDRegionData
</script>
<template>
    <q-plugin
        :context="{
            VueRouter: { useRouter, useRoute },
            VueUse: { useVModel },
            Request,
            QAreaChoose,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { QuestionFilled },
            UtilsUuid,
            PageManage: BetterPageManage,
            PageManageTwo,
            QIcon,
            ChromeTabs,
            ElTableEmpty,
            GD_regionData,
            Lodash: { cloneDeep },
            DeliveryOfficialKey,
        }"
        name="ShopFreight"
        service="gruul-mall-freight"
    />
</template>
