import { IPage, MessageAndShopAdmin, MessageUser } from './types'
import { MessageType } from '@/apis/consumerSever/types'
import { stompHookMount } from '@/composables/stomp/StompHandler'
import { Channel } from '@/composables/stomp/typs'
import { getMessages, getMessageUsers, sendMessages } from '@/apis/consumerSever'
import { useUserStore } from '@/store/modules/user'

// 分页参数
export interface Page {
  size: number
  current: number
}
export const userInfo = ref(useUserStore().getterUserInfo)
useUserStore().$subscribe((mutation, state) => {
  userInfo.value = state.userInfo.info
  if (!state.userInfo.info || !state.userInfo.info.id || !state.userInfo.info.token) return
  currentSelectUser.value = void 0
  messageUsersPage.initLoad()
})
/***** constant ******/

/***** user list ******/
export const messageUsersPage = reactive<IPage<MessageUser>>(
  new IPage<MessageUser>(
    (page: Page) => getMessageUsers(searchKeyword.value, { ...page, chatWithType: 'CONSUMER', userId: userInfo.value.userId }),
    30,
  ),
)
export const currentSelectUser = ref<undefined | MessageUser>(void 0)
const searchKeyword = ref('')

/**
 * 切换客户
 * @param selectUser 选择的客户
 */
export const onChange = (selectUser: MessageUser) => {
  currentSelectUser.value = selectUser
  nextTick(async () => {
    if (selectUser.chatWithShopInfo?.shopId) {
      await adminMessagesPage.value.initLoad()
    }
  })
}

export const onKeywordsChange = (keyword: string) => {
  if (keyword !== searchKeyword.value) {
    searchKeyword.value = keyword
    messageUsersPage.initLoad()
  }
}

const pageInit = () => {
  messageUsersPage.initLoad()
}

export const searchFocus = ref(false)
export const onSearchFocus = (val: boolean) => {
  searchFocus.value = val
}

const shopId = computed(() => (shopId?: string) => {
  if (shopId) {
    return shopId
  }
  return currentSelectUser.value?.chatWithShopInfo?.shopId || ''
})

/***** user messages ******/
/**
 * 加载用户消息
 */
export const adminMessagesPage = ref<IPage<MessageAndShopAdmin>>(
  new IPage<MessageAndShopAdmin>((page: Page) => getMessages(shopId.value() ? shopId.value() : '', { ...page, userId: userInfo.value.userId }), 20),
)
export const messageSubmit = ({ messageType, content }: { messageType: MessageType; content: string }) => {
  if (!currentSelectUser.value) return
  if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
  const data = {
    receiverId: shopId.value(),
    senderId: userInfo.value.userId,
    messageType,
    content,
  }
  sendMessages(data).then(() => {
    if (!currentSelectUser.value) return
    adminMessagesPage.value.initLoad()
    messageUsersPage.initLoad()
  })
}

/**
 * 初始化 用户列表 并且 监听  客服消息
 */
export const initCustomerService = async () => {
  //初始化用户列表
  await messageUsersPage.initLoad().then((res) => {
    adminMessagesPage.value = new IPage<MessageAndShopAdmin>(
      (page: Page) => getMessages(shopId.value() ? shopId.value() : res[0].chatWithShopInfo?.shopId, { ...page, userId: userInfo.value.userId }),
      20,
    )
  })
  //监听客服消息
  stompHookMount(Channel.PLATFORM_SHOP_AND_USER, {
    success: pageInit,
    fail: () => {},
    subscribe: (message) => {
      messageUsersOnNewMessage(message as unknown as MessageAndShopAdmin)
    },
  })
}
/**
 * 新的消息处理
 * @param message
 */
const messageUsersOnNewMessage = (messageObj: MessageAndShopAdmin) => {
  messageUsersPage.initLoad()
  adminMessagesPage.value.initLoad()
  const messageShopId = messageObj.sender.senderShopInfo?.shopId || messageObj.sender.senderUserInfo?.userId
  if (messageShopId === currentSelectUser.value?.chatWithUserInfo?.userId || messageShopId === userInfo.value.shopId) {
    adminMessagesPage.value.concatData(messageObj)
  }
}

export const contentLoadMore = () => {
  adminMessagesPage.value.loadMore()
}
