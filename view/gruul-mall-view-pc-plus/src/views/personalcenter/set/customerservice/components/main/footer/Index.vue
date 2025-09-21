<template>
  <div class="footer-content">
    <el-header class="header">
      <ChatToolbar v-if="user?.chatWithShopInfo.shopId && +user.chatWithShopInfo.shopId > 0" :user="user" @contentChange="contentChange" />
    </el-header>
    <el-main class="main">
      <el-input
        ref="elInputRef"
        v-model.trim="content"
        type="textarea"
        :disabled="!user || !user.chatWithShopInfo.shopId || +user.chatWithShopInfo.shopId < 0"
        resize="none"
        maxlength="300"
        @keydown.enter="onSubmit"
      >
      </el-input>
    </el-main>
    <div class="send-btn">
      <el-button type="primary" class="sendOut" @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import ChatToolbar from './toolbar/Index.vue'
import { MessageType, MessageUser, ToolbarMessage, ToolbarMessageType, ChatMessage } from '@/views/personalcenter/set/customerservice/types'

const props = withDefaults(
  defineProps<{
    user: MessageUser
    searchFocus: boolean
  }>(),
  {
    searchFocus: false,
  },
)

const emits = defineEmits(['messageSubmit'])
const content = ref('')
const position = ref(0)
const elInputRef = ref()
const textarea = ref(null)

onMounted(() => {
  content.value = ''
  textarea.value = elInputRef.value.textarea
  position.value = content.value.length
})

watch(
  () => props.user,
  (user) => {
    if (!user || !user.chatWithShopInfo.shopId) return
    elInputRef.value.focus()
    clearMessage()
  },
)
//清空消息
const clearMessage = () => {
  content.value = ''
  position.value = 0
}
// 从ChatMessage中去除receiverId, senderId
type SubmitMessage = Omit<ChatMessage, 'receiverId' | 'senderId'>
const submitMessage = (chatMessage: SubmitMessage) => {
  emits('messageSubmit', chatMessage)
}

const onSubmit = (e: Obj) => {
  //非 enter 或者 按住了shift
  if (e.shiftKey) return
  e.preventDefault()
  sendMessage()
}
function sendMessage() {
  const value = content.value
  if (!value) return
  submitMessage({ messageType: MessageType.TEXT, content: content.value })
  clearMessage()
}
//
//内容改变
const contentChange = (toolContent: ToolbarMessage) => {
  switch (toolContent.type) {
    case ToolbarMessageType.EXPRESSION:
      changeContentValue(toolContent.content)
      return
    case ToolbarMessageType.IMAGE:
      submitMessage({ messageType: MessageType.IMAGE, content: toolContent.content })
      return
    default:
      break
  }
}

//插入新值
const changeContentValue = (newContent: string) => {
  if (!newContent) return
  const currentValue = content.value
  const cursorIndex = position.value
  content.value = currentValue.slice(0, cursorIndex) + newContent + currentValue.slice(cursorIndex)
  position.value = position.value + newContent.length
}
</script>

<style scoped lang="scss">
.footer-content {
  width: 100%;
  background-color: #fff;
  position: relative;
  resize: both;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 0 0 20px 0;
}

.sendOut {
  width: 76px;
  height: 36px;
  border-radius: 100px;
  position: absolute;
  background-color: #555cfd;
  border-color: #555cfd;
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
