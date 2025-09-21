<template>
    <div class="message-content" :class="clazz">
        <div class="message-content-direction" :class="clazz"></div>
        {{ props.message.message }}
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { Message } from '@/views/message/components/types'

/**
 * msg 消息内容
 * isMine 是否是我的消息
 */
const props = defineProps({
    message: {
        type: Object as PropType<Message>,
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
$not-mine-bg: $rows-text-color-inverse;
$mine-bg: #94eb6e;

.message-content.msg-other {
    margin-left: $rows-spacing-row-sm;
    background-color: $not-mine-bg !important;
}
.message-content.msg-mine {
    margin-right: $rows-spacing-row-sm;
    background-color: $mine-bg !important;
}
.message-content-direction {
    position: absolute;
    top: 5px;
    font-size: 0;
    line-height: 0;
    border-width: 10px;
    border-style: dashed;
}
.message-content-direction.msg-other {
    left: -5px;
    border-color: $not-mine-bg;
    border-left-width: 0;
    border-right-style: solid;
    border-top-color: transparent;
    border-bottom-color: transparent;
}
.message-content-direction.msg-mine {
    right: -5px;
    border-color: $mine-bg;
    border-right-width: 0;
    border-left-style: solid;
    border-top-color: transparent;
    border-bottom-color: transparent;
}
</style>
