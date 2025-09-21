<template>
    <div class="footer-content">
        <el-header class="header">
            <ChatToolbar
                v-if="props.user?.chatWithShopInfo.shopId && +props.user.chatWithShopInfo.shopId > 0"
                :user="user"
                @contentChange="contentChange"
                @submitMessage="submitMessage"
            />
        </el-header>
        <el-main class="main">
            <el-input
                ref="elInputRef"
                v-model.trim="content"
                type="textarea"
                :disabled="!props.user || !props.user.chatWithShopInfo.shopId || +props.user.chatWithShopInfo.shopId < 0"
                resize="none"
                maxlength="300"
                @blur="onBlur"
                @keydown.enter="onSubmit"
            >
            </el-input>
        </el-main>
        <div class="send-btn">
            <QIcon svg name="icon-a-zuhe1282" class="sendOut" @click="sendMessage"></QIcon>
        </div>
    </div>
</template>

<script setup lang="ts">
import QIcon from '@/components/q-icon/q-icon.vue'
import { onMounted, PropType, ref, watch } from 'vue'
import ChatToolbar from './toolbar/Index.vue'
import { Search } from '@element-plus/icons-vue'
import { MessageType, MessageUser, ToolbarMessage, ToolbarMessageType, ChatMessage } from '../../../types'

const props = defineProps({
    user: {
        type: Object as PropType<MessageUser>,
        default: () => {},
    },
    searchFocus: {
        type: Boolean,
        default: false,
    },
})
const emits = defineEmits(['messageSubmit'])
const content = ref('')
const position = ref(0)
const elInputRef = ref(null)
const textarea = ref(null)

onMounted(() => {
    content.value = ''
    textarea.value = elInputRef.value?.textarea
    position.value = content.value.length
})

watch(
    () => props.user,
    (user) => {
        if (!user || !user.chatWithShopInfo.shopId) return
        elInputRef.value?.focus()
        clearMessage()
    },
)
//清空消息
const clearMessage = () => {
    content.value = ''
    position.value = 0
}
//
const submitMessage = (chatMessage: ChatMessage) => {
    emits('messageSubmit', chatMessage)
}
const onSubmit = (e: KeyboardEvent | Event) => {
    //非 enter 或者 按住了shift
    if (e.shiftKey) return
    e.preventDefault()
    sendMessage()
}
function sendMessage() {
    const value = content.value
    if (!value) return
    submitMessage({ messageType: MessageType.TEXT, message: content.value })
    clearMessage()
}
//
//获取失去焦点前的焦点
const onBlur = (e) => {
    //console.log('textarea', props.searchFocus)
    /*console.log(props.searchFocus)
    if (props.searchFocus) return*/
    // position.value = e.target.selectionStart
    // e.target.focus()
}
//内容改变
const contentChange = (toolContent: ToolbarMessage) => {
    switch (toolContent.type) {
        case ToolbarMessageType.EXPRESSION:
            changeContentValue(toolContent.content)
            return
        case ToolbarMessageType.IMAGE:
            submitMessage({ messageType: MessageType.IMAGE, message: toolContent.content })
            return
        default:
            break
    }
}

//插入新值
const changeContentValue = (newContent) => {
    if (!newContent) return
    const currentValue = content.value
    const cursorIndex = position.value
    content.value = currentValue.slice(0, cursorIndex) + newContent + currentValue.slice(cursorIndex)
    position.value = position.value + newContent.length
}
</script>

<style scoped lang="scss">
.footer-content {
    width: 95%;
    border: 1px solid #555cfd;
    border-radius: 10px;
    background-color: #fff;
    position: relative;
    resize: both;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.sendOut {
    width: 54px;
    height: 54px;
    cursor: pointer;
    position: absolute;
    right: 20px;
    z-index: 999;
    bottom: 16px;
}

.header {
    padding: 0;
    margin-top: 12px;
    height: 22px;
}

.main {
    padding: 0;
    width: 89%;
    height: auto;
}

.el-main .el-textarea {
    height: 100%;
    --el-input-hover-border: $rows-bg-color-grey;
    --el-input-focus-border: $rows-bg-color-grey;
    --el-input-border-color: $rows-bg-color-grey;
    --el-input-bg-color: $rows-bg-color-grey;
    --el-input-icon-color: $rows-bg-color-grey;
    --el-input-placeholder-color: $rows-bg-color-grey;
    --el-input-hover-border-color: $rows-bg-color-grey;
    --el-input-clear-hover-color: $rows-bg-color-grey;
    --el-input-focus-border-color: $rows-bg-color-grey;
}
:deep(.el-main .el-textarea .el-textarea__inner) {
    width: 100%;
    height: 100%;
}
</style>
