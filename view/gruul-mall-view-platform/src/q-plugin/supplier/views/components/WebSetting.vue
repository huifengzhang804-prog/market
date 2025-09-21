<script setup lang="ts">
import QPlugin from '@/q-plugin/index.vue'
import QUpload from '@/components/q-upload/q-upload.vue'
import TitleBar from '@/components/TitleBar/TitleBar.vue'
import { PropType } from 'vue'
import { SaveOrUpdateData } from '@/apis/set/websiteSettings/types'

defineProps({
    websiteForm: {
        type: Object,
        default: () => ({}),
    },
    fieldsList: {
        type: Object as PropType<Record<string, Obj[]>>,
        default: () => ({}),
    },
    changeKey: {
        type: Function as PropType<(key: string) => void>,
        default: () => () => {},
    },
    handleChange: {
        type: Function as PropType<(key: string) => void>,
        default: () => () => {},
    },
    websiteFormChange: {
        type: Function as PropType<(key: SaveOrUpdateData) => void>,
        default: () => () => {},
    },
    viewImg: {
        type: Function as PropType<(key: string) => void>,
        default: () => () => {},
    },
})
</script>

<template>
    <q-plugin
        :context="{
            QUpload,
            TitleBar,
        }"
        :properties="{
            websiteForm,
            fieldsList,
            changeKey,
            handleChange,
            websiteFormChange,
            viewImg,
        }"
        hideOnMiss
        name="PlatformWebSetting"
        service="addon-supplier"
    />
</template>

<style lang="scss" scoped></style>
