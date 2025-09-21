<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import type { LinkSelectItem } from '../linkSelectItem'
import type { PropType } from 'vue'

const $props = defineProps({
    link: {
        type: Object as PropType<LinkSelectItem>,
        default() {
            return {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            }
        },
    },
})
const $emit = defineEmits(['update:link'])
const linkSelectItem = useVModel($props, 'link', $emit)
const webview = ref('')
watch(webview, (newVal) => {
    const newWebview = newVal.replace(/https:\/\//, '')
    const currentItem = {
        id: 999,
        type: 6,
        name: '自定义链接',
        url: `https://${newWebview}`,
        append: '',
    }
    Object.assign(linkSelectItem.value, currentItem)
})
watch(
    linkSelectItem,
    (newVal) => {
        webviewChange()
    },
    {
        immediate: true,
    },
)

function webviewChange() {
    if (linkSelectItem.value.type === 6) {
        const newWebview = linkSelectItem.value.url.replace(/https:\/\//, '')
        webview.value = newWebview
    }
}
</script>

<template>
    <div>
        <el-input v-model="webview" placeholder="请输入内容" class="input-with-select">
            <template #prepend>https://</template>
        </el-input>
        <div class="link-tips">链接必须支持<text style="color: red">https</text>协议</div>
    </div>
</template>

<style lang="scss" scoped>
.link-tips {
    margin-top: 5px;
}
</style>
