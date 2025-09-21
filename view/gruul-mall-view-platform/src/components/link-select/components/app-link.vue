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
const appmodel = ref('')
watch(appmodel, (newVal) => {
    const currentItem = {
        id: 999,
        type: 7,
        name: '小程序',
        url: `${newVal}`,
        append: 'appmodel',
    }
    Object.assign(linkSelectItem.value, currentItem)
})

onMounted(() => {
    if (linkSelectItem.value.type === 7) {
        appmodel.value = linkSelectItem.value.url
    }
})
</script>

<template>
    <div>
        <span style="color: #9797a1">AppId</span>
        <el-input v-model="appmodel" maxlength="40" placeholder="请输入小程序appId" class="input-with-select" style="width: 180px; margin-left: 20px">
        </el-input>
        <div class="link-tips">
            需登录<a href="https://mp.weixin.qq.com/" target="_tartget" rel="noopener noreferrer">小程序管理后台 </a>配置<text style="color: red"
                >业务域名</text
            >
        </div>
    </div>
</template>

<style lang="scss" scoped>
.link-tips {
    margin-top: 5px;
}
</style>
