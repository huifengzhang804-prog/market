<template>
    <div class="message-content message-content-image">
        <el-image :src="props.message?.message" :preview-src-list="[props.message?.message]" fit="scale-down" hide-on-click-modal />
    </div>
</template>

<script setup lang="ts">
import { PropType } from 'vue'
import { MessageAndShopAdmin } from '@/views/mall/customerService/types'

/**
 * msg 消息内容
 * isMine 是否是我的消息
 */
const props = defineProps({
    message: {
        type: Object as PropType<MessageAndShopAdmin>,
        required: true,
    },
    isMine: {
        type: Boolean,
        default: false,
    },
})
</script>

<style scoped lang="scss">
.message-content-image {
    padding: 0 $rows-spacing-row-sm !important;
}
.message-content-image .el-image {
    max-width: 350px;
    max-height: 450px;
    border-radius: $rows-border-radius-sm;
}
</style>
