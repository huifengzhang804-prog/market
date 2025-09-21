<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { useRouter, useRoute, onBeforeRouteLeave } from 'vue-router'
import * as Request from '@/apis/http'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search, QuestionFilled, Plus, CircleClose, Delete } from '@element-plus/icons-vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { cloneDeep } from 'lodash-es'
import QUpload from '@/components/q-upload/q-upload.vue'
import { elementUploadRequest } from '@/apis/upload'
import { VueDraggableNext } from 'vue-draggable-next'
import QEdit from '@/components/q-edit/q-edit.vue'
import CityDistributionCheckbox from '@/q-plugin/cityDistribution/CityDistributionCheckbox.vue'
import FreightCheckBox from '@/q-plugin/freight/FreightCheckBox.vue'
import dragTable from '@/components/element-plus-table-drag/drag-table.vue'
import exampleImg from '@/assets/images/ex_img.png'

const VITE_BASE_URL = import.meta.env.VITE_BASE_URL
</script>

<template>
    <q-plugin
        :context="{
            QPlugin,
            VueRouter: { useRouter, useRoute, onBeforeRouteLeave },
            ShopInfoStore: { useShopInfoStore },
            Lodash: { cloneDeep },
            UseConvert,
            ElementPlus: { ElMessageBox, ElMessage },
            ElementPlusIconsVue: { Search, Plus, CircleClose, QuestionFilled, Delete },
            QUpload,
            UploadAPI: { elementUploadRequest },
            VueDraggableNext: { VueDraggableNext },
            Request,
            QEdit,
            CityDistributionCheckbox,
            FreightCheckBox,
            dragTable,
            exampleImg,
        }"
        :properties="{ VITE_BASE_URL }"
        name="ShopPurchaseOrderRelease"
        service="addon-supplier"
    />
</template>
