<template>
    <div class="message-content" :class="clazz">
        {{ props.message.message }}
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { MessageAndShopAdmin } from '@/views/customerService/types'

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
const clazz = computed(() => (props.isMine ? 'msg-mine' : 'msg-other'))
</script>

<style scoped lang="scss">
$not-mine-bg: #555cfd;
$mine-bg: #fff;
.message-content {
    font-size: 14px;
    font-weight: 500;
    &.msg-mine {
        margin-right: $rows-spacing-row-sm;
        background-color: $mine-bg !important;
        color: #333333;
        border-radius: 8px 0px 8px 8px;
    }
    &.msg-other {
        margin-left: $rows-spacing-row-sm;
        background-color: $not-mine-bg !important;
        color: #fff;
        border-radius: 0px 8px 8px 8px;
    }
}
</style>
