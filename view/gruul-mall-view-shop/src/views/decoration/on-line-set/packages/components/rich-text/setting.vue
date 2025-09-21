<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import QEdit from '@/components/q-edit/q-edit.vue'
import defaultRichTextData from './rich-text'

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
