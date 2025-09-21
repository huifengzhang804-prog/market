<script setup lang="ts">
import { ref } from 'vue'
import { Message, MessageType, MessageUser } from '../types'
import DateUtil from '@/utils/date'
import type { GlobalProperties } from '@/libs/global'
import { currentSelectUser } from '@/views/personalcenter/set/customerservice'
import { useRoute } from 'vue-router'

const props = defineProps({
  messageUsers: {
    type: Array as PropType<Array<MessageUser>>,
    default: () => [],
  },
})

const route = useRoute()
// 搜索联系人
const searchKeyword = ref('')
const global: GlobalProperties = inject('global')!
const emits = defineEmits(['change', 'keywordChange', 'searchFocus'])
const isFirst = ref(false)
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

const onInput = (value: string) => {
  if (!value) {
    inputChange()
  }
}

const inputBlur = () => {
  emits('searchFocus', false)
}
const inputFocus = () => {
  emits('searchFocus', true)
}

const inputChange = () => {
  const keyword = searchKeyword.value
  emits('keywordChange', keyword)
}

watch(
  () => props.messageUsers,
  (val) => {
    if (!route.query.shopId) {
      return
    }
    if (val[0]?.chatWithShopInfo) {
      const selectUser = val.find((item) => item.chatWithShopInfo?.shopId === route.query.shopId)
      if (selectUser && !isFirst.value) {
        onChange(selectUser)
        isFirst.value = true
      }
    }
  },
)

// 切换聊天对象
const onChange = (selectUser: MessageUser) => {
  if (selectUser?.lastMessage) {
    selectUser.lastMessage.read = true
  }
  emits('change', { ...selectUser })
}
</script>

<template>
  <div class="contacts">
    <div class="search">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索最近联系人"
        clearable
        @keyup.enter="inputChange"
        @clear="inputChange"
        @input="onInput"
        @blur="inputBlur"
        @focus="inputFocus"
      >
        <template #prefix>
          <QIcon name="icon-sousuo1" style="cursor: pointer" @click="inputChange" />
        </template>
      </el-input>
    </div>
    <div v-if="messageUsers.length" class="customer-service-users">
      <div
        v-for="messageUser in messageUsers"
        :key="messageUser.chatWithShopInfo?.shopId"
        class="user_msg_menu_item"
        :class="{
          'is-active': messageUser.chatWithShopInfo?.shopId === currentSelectUser?.chatWithShopInfo?.shopId,
          'is-unread': messageUser?.lastMessage && !messageUser?.lastMessage?.read,
        }"
        @click="onChange(messageUser)"
      >
        <el-badge v-if="messageUser?.lastMessage" :hidden="messageUser?.lastMessage?.read" is-dot class="badge">
          <el-avatar class="avatar" :size="45" shape="square" :src="global.getUserAvatar(messageUser.chatWithShopInfo.shopLogo)" />
        </el-badge>
        <el-avatar v-else class="avatar badge" :size="45" shape="square" :src="global.getUserAvatar(messageUser.chatWithShopInfo.shopLogo)" />
        <div class="user-desc">
          <div class="user-msg">
            <div class="nickname">
              {{ global.getUserNickname(messageUser.chatWithShopInfo?.shopId, messageUser.chatWithShopInfo.shopName) }}
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
    <div v-else class="noShopUser">店铺不存在</div>
  </div>
</template>

<style lang="scss" scoped>
.contacts {
  width: 20%;
  border-right: 2px solid #f7f7f7;
  display: flex;
  flex-direction: column;
  .search {
    width: 100%;
    display: flex;
    justify-content: center;
    padding: 22px 12px;
    border-bottom: 2px solid #f7f7f7;
  }
  .noShopUser {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999999;
  }
}
:deep(.el-input) {
  border: none;
  .el-input__wrapper {
    background-color: #f7f7f7;
    border-radius: 20px;
  }
}

.customer-service-users {
  padding: 0;
  border-right: none;
  flex: 1;
  border-radius: 0 0 0 20px;
  overflow-y: scroll;
  // 隐藏滚动条
  &::-webkit-scrollbar {
    display: none;
  }
  .user_msg_menu_item {
    box-sizing: border-box;
    cursor: pointer;
    font-size: 14px;
    color: #333333;
    user-select: none;
    margin: 0 !important;
    padding: 13px 12px !important;
    height: 70px;
    border-bottom: 1px solid #f7f7f7;
    overflow: hidden;
    display: flex;
    align-items: center;
    position: relative;
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
          width: 105px;
          font-weight: bold;
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
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        text-align: left;
      }
    }
  }
  .user_msg_menu_item.is-active {
    background-color: #555cfd26;
    border: 1px solid rgb(85, 92, 253);
  }
  .user_msg_menu_item.is-unread {
    background-color: #fd922426;
  }
}
</style>
