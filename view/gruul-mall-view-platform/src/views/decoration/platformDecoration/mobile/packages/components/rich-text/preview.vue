<script setup lang="ts">
import defaultRichTextData from './rich-text'
import type { PropType } from 'vue'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultRichTextData>,
        default() {
            return defaultRichTextData
        },
    },
})
</script>

<template>
    <div class="rc-design-react-preview rc-design-component-default-preview">
        <div v-if="!$props.formData.text" class="rc-design-component-default-preview__text">编辑文本</div>
        <div v-else v-dompurify-html="$props.formData.text" class="cap-cube-wrap"></div>
    </div>
</template>

<style lang="scss" scoped>
@import '@/assets/css/decoration/cubeBox.scss';
.cap-cube-wrap {
    width: 100%;
    overflow: hidden;
    word-wrap: break-word;
    min-height: 200px;
}
</style>
