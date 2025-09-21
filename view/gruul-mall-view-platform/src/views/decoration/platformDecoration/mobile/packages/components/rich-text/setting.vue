<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import QEdit from '@/components/q-editor/q-edit.vue'
import defaultRichTextData from './rich-text'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultRichTextData>,
        default: defaultRichTextData,
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
</script>

<template>
    <div class="editor">
        <q-edit v-model:content="formData.text" height="400px" />
    </div>
</template>

<style lang="scss" scoped>
.editor {
    width: 100%;
    border: 1px solid #ccc;
    top: 62px !important;
}
</style>
