<template>
    <ul class="messages-list">
        <li v-for="message in currentMessages" :key="message.sendTime" class="message-item">
            <p class="message-timer">{{ renderTime(message.sendTime) }}</p>
            <div class="message-item-box" :style="isMine(message) ? 'flex-direction: row-reverse' : ''">
                <div class="message-avatar">
                    <el-avatar shape="square" :size="36" :src="isMine(message) ? logo : props.user.chatWithUserInfo?.avatar" />
                </div>
                <component :is="dynamicComponent[message.messageType]" :message="message" :is-mine="isMine(message)" />
            </div>
        </li>
    </ul>
</template>

<script setup lang="ts">
import type { Component, PropType } from 'vue'
import { MessageAndShopAdmin, MessageTypeKeys, MessageUser } from '@/views/customerService/types'
import DateUtil from '@/utils/date'
import { configurePlatform } from '@/store/modules/configurePlatform'

const configure = configurePlatform()
const logo = configure.getPlatformLogo
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
console.log(props.user)
// 组件
const dynamicComponent: Record<MessageTypeKeys, Component> = {
    IMAGE: defineAsyncComponent(() => import('./ContentImage.vue')),
    PRODUCT: defineAsyncComponent(() => import('./ContentProduct.vue')),
    ORDER: defineAsyncComponent(() => import('./ContentOrder.vue')),
    TEXT: defineAsyncComponent(() => import('./ContentText.vue')),
}

// 处理时间
const renderTime = (time: any) => {
    if (!time) return ''
    const lastTime = new Date(+time)
    const dateUtil = new DateUtil(lastTime)
    const isToday = new Date().getDay() === lastTime.getDay()
    return isToday ? dateUtil.getH() + ':' + dateUtil.getMin() : dateUtil.getYMD()
}

// 处理聊天信息
const currentMessages = computed<Array<MessageAndShopAdmin>>(() => {
    const msgs: Array<MessageAndShopAdmin> = []
    props.messages.forEach((item) => msgs.unshift(item))
    return msgs
})

// 是否是自己
const isMine = (message: MessageAndShopAdmin) => {
    return message.sender.senderType === 'PLATFORM_ADMIN'
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
