<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import type { LinkSelectItem } from '../linkSelectItem'

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
    const webview = newVal.replace(/https:\/\//, '')
    const currentItem = {
        id: 999,
        type: 6,
        name: '自定义链接',
        url: `https://${webview}`,
        append: '',
    }
    Object.assign(linkSelectItem.value, currentItem)
})

onMounted(() => {
    if (linkSelectItem.value.type === 6) {
        webview.value = linkSelectItem.value.url
    }
})
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
