<template>
    <q-plugin
        :context="{
            VueRouter: { useRoute, useRouter },
            UseConvert,
            Request: { get, del, post, put },
            ElementPlusIconsVue: { CircleClose },
            VueDraggableNext: { VueDraggableNext },
            QUpload,
            ElementPlus: { ElMessageBox, ElMessage },
            QEdit,
            selectMaterial,
            ExampleImg,
        }"
        name="AddIntegralGoods"
        service="addon-integral"
    />
</template>

<script lang="ts" setup>
import QPlugin from '@/q-plugin/index.vue'
import { useRoute, useRouter } from 'vue-router'
import UseConvert from '@/AutoImportCustomUse/useConvert'
import { get, del, post, put } from '@/apis/http'
import { CircleClose } from '@element-plus/icons-vue'
import { VueDraggableNext } from 'vue-draggable-next'
import QUpload from '@/components/q-upload/q-upload.vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import QEdit from '@/components/q-editor/q-edit.vue'
import selectMaterial from '@/views/material/selectMaterial.vue'
import ExampleImg from '@/assets/image/ex_img.png'
</script>

<style scoped></style>
