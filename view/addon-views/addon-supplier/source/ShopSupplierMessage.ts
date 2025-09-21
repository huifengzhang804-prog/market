import { ref, reactive, nextTick } from 'vue'
import { type Page, ChatMessage, IPage, MessageUser, CustomerServiceMessage, Message } from '@/views/message/components/types'
import { stompHookMount } from '@/composables/stomp/StompHandler'
import { Channel } from '@/composables/stomp/typs'
import { getSupplierMessages, getSupplierMessageUsers, sendSupplierMessages } from './apis'
import { useShopInfoStore } from '@/store/modules/shopInfo'

export const shopInfo = ref(useShopInfoStore().getterShopInfo)
useShopInfoStore().$subscribe((mutation, state) => {
  const info = state.shopInfo
  shopInfo.value = info
  if (!info || !info.id || !info.token) return
  currentSelectUser.value = null
  messageUsersPage.initLoad()
})
/***** constant ******/

/***** user list ******/
export const messageUsersPage = reactive<IPage<MessageUser>>(
  new IPage<MessageUser>((page: Page) => getSupplierMessageUsers(searchKeyword.value, page, 4), 30),
)
export const currentSelectUser = ref<null | MessageUser>(null)
const searchKeyword = ref('')
export const searchFocus = ref(false)
export const onChange = (selectUser: MessageUser) => {
  currentSelectUser.value = selectUser
  nextTick(() => {
    if (selectUser.chatWithShopInfo.shopId) {
      adminMessagesPage.value.initLoad()
    }
  })
}
export const onKeywordsChange = (keyword: string) => {
  searchKeyword.value = keyword
  messageUsersPage.initLoad()
}
export const onSearchFocus = (val: boolean) => {
  searchFocus.value = val
}

/***** user messages ******/
/**
 * 加载用户消息
 */
export const adminMessagesPage = ref<IPage<Message>>(
  new IPage<Message>((page: Page) => {
    if (!currentSelectUser.value) return Promise.reject()
    if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
    const { chatWithShopInfo } = currentSelectUser.value
    return getSupplierMessages(
      {
        senderShopId: chatWithShopInfo.shopId,
      },
      page,
    )
  }, 20),
)
export const messageSubmit = (chatMessage: ChatMessage) => {
  if (!currentSelectUser.value) return
  if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
  const { chatWithShopInfo } = currentSelectUser.value
  sendSupplierMessages({
    receiverShopId: chatWithShopInfo.shopId,
    messageType: chatMessage.messageType,
    content: chatMessage.message,
  })
}
export const pageInit = () => {
  messageUsersPage.initLoad()
}
/**
 * 初始化 用户列表 并且 监听  客服消息
 */
export const initCustomerService = async () => {
  //初始化用户列表
  await messageUsersPage.initLoad().then((res) => {
    adminMessagesPage.value = new IPage<Message>((page: Page) => {
      if (!currentSelectUser.value) return Promise.reject()
      if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
      const { chatWithShopInfo } = currentSelectUser.value

      return getSupplierMessages(
        {
          senderShopId: chatWithShopInfo.shopId,
        },
        page,
      )
    }, 20)
  })
  //监听客服消息
  stompHookMount(Channel.SUPPLIER_SHOP, {
    success: pageInit,
    fail: () => {},
    subscribe: (message: any) => {
      const currentMsg = message as CustomerServiceMessage
      messageUsersOnNewMessage(currentMsg)
    },
  })
}
/**
 * 新的消息处理
 */

const messageUsersOnNewMessage = async (messageObj: CustomerServiceMessage) => {
  await messageUsersPage.initLoad()
  const messageShopId = messageObj.sender.senderShopInfo.shopId
  if (messageShopId === currentSelectUser.value?.chatWithShopInfo.shopId || messageShopId === shopInfo.value.id) {
    const { sender, receiver, messageType, message } = messageObj
    const messageAdmin: Message = {
      sender,
      receiver,
      messageType,
      message,
      read: false,
      handled: false,
      show: true,
      sendTime: String(+new Date()),
    }

    adminMessagesPage.value.concatData(messageAdmin)
  }
}

export const contentLoadMore = () => {
  adminMessagesPage.value.loadMore()
}
