<template>
    <ul class="messages-list">
        <li v-for="message in currentMessages" :key="message.sendTime" class="message-item">
            <p class="message-timer">{{ renderTime(message.sendTime) }}</p>
            <div class="message-item-box" :style="isMine(message) ? 'flex-direction: row-reverse' : ''">
                <div class="message-avatar">
                    <el-avatar shape="square" :size="36" :src="isMine(message) ? props.shopInfo?.logo : props.user?.chatWithUserInfo.avatar" />
                </div>
                <component :is="dynamicComponent[message.messageType]" :message="message" :is-mine="isMine(message)" />
            </div>
        </li>
    </ul>
</template>

<script setup lang="ts">
import type { PropType, Component } from 'vue'
import type { MessageAndShopAdmin, MessageTypeKeys, MessageUser } from '@/views/message/customerService/types'
import { dayjs } from 'element-plus'

const dynamicComponent: Record<MessageTypeKeys, Component> = {
    IMAGE: defineAsyncComponent(() => import('./ContentImage.vue')),
    PRODUCT: defineAsyncComponent(() => import('./ContentProduct.vue')),
    ORDER: defineAsyncComponent(() => import('./ContentOrder.vue')),
    TEXT: defineAsyncComponent(() => import('./ContentText.vue')),
}
const props = defineProps({
    user: {
        type: Object as PropType<MessageUser>,
        default: () => {},
    },
    messages: {
        type: Array as PropType<Array<MessageAndShopAdmin>>,
        default: () => [],
    },
    shopInfo: {
        type: Object,
        default: () => {},
    },
})
const currentMessages = computed(() => {
    const msgs: MessageAndShopAdmin[] = []
    props.messages.forEach((item) => msgs.unshift(item))
    return msgs
})
const renderTime = (time: Long) => {
    if (!time) return ''
    return dayjs(+time).format('YYYY/MM/DD HH:mm')
}
const isMine = (message: MessageAndShopAdmin) => {
    return message.sender.senderType === 'SHOP_ADMIN'
}
</script>

<style scoped lang="scss">
.message-timer {
    text-align: center;
    // font-size: 5px;
    color: $rows-text-color-grey;
}
.message-item-box {
    display: flex;
    padding: 16px 0;
}

.message-content {
    position: relative;
    max-width: 60%;
    padding: $rows-spacing-row-sm;
    border-radius: $rows-border-radius-sm;
}
</style>
