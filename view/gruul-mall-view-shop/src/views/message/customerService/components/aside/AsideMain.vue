<template>
    <el-scrollbar height="calc(100% - 60px)">
        <div class="customer-service-users">
            <div
                v-for="messageUser in messageUsers"
                :key="messageUser.chatWithUserInfo?.userId"
                :index="messageUser.chatWithUserInfo?.userId"
                class="user_msg_menu_item"
                :class="{
                    'is-active': messageUser.chatWithUserInfo?.userId === currentSelectUser?.chatWithUserInfo?.userId,
                }"
                @click="onChange(messageUser)"
            >
                <div v-if="messageUser.chatWithUserInfo.includeRights" class="special_tag">
                    <q-icon svg name="icon-a-zuhe12851" style="width: 100%; height: 100%"></q-icon>
                </div>
                <el-badge :hidden="messageUser?.lastMessage?.read" is-dot class="badge">
                    <el-avatar class="avatar" :size="45" shape="square" :src="global.getUserAvatar(messageUser.chatWithUserInfo.avatar)" />
                </el-badge>
                <div class="user-desc">
                    <div class="user-msg">
                        <div class="nickname">
                            {{ global.getUserNickname(messageUser.chatWithUserInfo?.userId, messageUser.chatWithUserInfo.nickname) }}
                        </div>
                        <div v-if="messageUser.lastMessage" class="last-time">
                            {{ renderTime(messageUser.lastMessage.sendTime) }}
                        </div>
                    </div>
                    <div v-if="messageUser.lastMessage" class="last-message">
                        {{ renderMessage(messageUser.lastMessage) }}
                    </div>
                </div>
            </div>
        </div>
    </el-scrollbar>
</template>
<script setup lang="ts">
import type { PropType } from 'vue'
import DateUtil from '@/utils/date'
import { MessageType, MessageUser, Message } from '../../types'
import { currentSelectUser } from '@/views/message/customerService'
import { useRoute } from 'vue-router'
import type { GlobalProperties } from '@/libs/global'

const route = useRoute()

const props = defineProps({
    messageUsers: {
        type: Array as PropType<Array<MessageUser>>,
        default: () => [],
    },
})

const changeUser = ref(false)

watch(
    () => props.messageUsers,
    (val) => {
        if (!route.query.id || changeUser.value) {
            return
        }
        if (val[0]?.chatWithUserInfo) {
            const selectUser = val.find((item) => item.chatWithUserInfo?.userId === route.query.id)
            if (selectUser) {
                onChange(selectUser)
            }
        }
    },
)
const global: GlobalProperties = inject('global')!
const emits = defineEmits(['change'])

const onChange = (selectUser: MessageUser) => {
    if (selectUser.lastMessage) {
        selectUser.lastMessage.read = true
    }
    changeUser.value = true
    emits('change', { ...selectUser })
}

const renderTime = (time: any) => {
    if (!time) return ''
    const lastTime = new Date(+time)
    const dateUtil = new DateUtil(lastTime)
    const isToday = new Date().getDay() === lastTime.getDay()
    return isToday ? dateUtil.getH() + ':' + dateUtil.getMin() : dateUtil.getYMD()
}

const renderMessage = (message: Message) => {
    if (!message) return ''
    switch (message.messageType) {
        case MessageType.PRODUCT:
            return '[商品]'
        case MessageType.IMAGE:
            return '[图片]'
        case MessageType.ORDER:
            return '[订单]'
        default:
            return message.message
    }
}
</script>
<style scoped lang="scss">
.customer-service-users {
    padding: 0;
    border-right: none;
    .user_msg_menu_item {
        cursor: pointer;
        font-size: 14px;
        color: #333333;
        height: 80px;
        user-select: none;
        margin: 0 !important;
        padding: 17px 14px 16px 14px !important;
        border-bottom: 2px solid $cust_service_border_color;
        overflow: hidden;
        display: flex;
        align-items: center;
        position: relative;
        .special_tag {
            position: absolute;
            width: 26px;
            height: 26px;
            right: 0;
            top: 0;
        }
        .badge {
            height: 45px;
            margin-right: 10px;
        }
        .avatar {
            border-radius: 5px;
        }
        .user-desc {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-around;
            height: 100%;
            .user-msg {
                display: flex;
                align-items: center;
                .nickname {
                    font-weight: bold;
                    width: 136px;
                    // 超出省略
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
                .last-time {
                    margin-left: auto;
                    color: rgb(153, 153, 153);
                    font-size: 12px;
                }
            }
            .last-message {
                width: 142px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }
        }
    }
    .user_msg_menu_item.is-active {
        background-color: $rows-bg-color-hover;
    }
}
</style>
