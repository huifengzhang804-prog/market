import { MessageType, IPage, MessageAndShopAdmin, MessageUser, CustomerServiceMessage } from '@/views/customerService/types'
import { stompHookMount } from '@/libs/stomp/StompHandler'
import { Channel } from '@/libs/stomp/typs'
import { getMessages, getMessageUsers, sendMessages } from '@/apis/message'
import type { Page } from '@/apis/message'
import { useAdminInfo } from '@/store/modules/admin'

export const shopInfo = ref(useAdminInfo().getterAdminInfo)
useAdminInfo().$subscribe((mutation, state) => {
    const info = state.adminInfo
    shopInfo.value = info
    if (!info || !info.value) return
    currentSelectUser.value = null
    messageUsersPage.initLoad()
})
/***** constant ******/

/***** user list ******/
export const messageUsersPage = reactive<IPage<MessageUser>>(
    new IPage<MessageUser>(
        (page: Page) => getMessageUsers(searchKeyword.value, { ...page, chatWithType: 'PLAT_FORM', shopId: shopInfo.value.open.shopId }),
        30,
    ),
)
export const currentSelectUser = ref<null | MessageUser>(null)
const searchKeyword = ref('')
export const searchFocus = ref(false)
export const onChange = (selectUser: MessageUser) => {
    currentSelectUser.value = selectUser
    nextTick(() => {
        if (selectUser.chatWithUserInfo?.userId) {
            adminMessagesPage.value.initLoad()
        }
    })
}
export const onKeywordsChange = (keyword: string) => {
    if (keyword !== searchKeyword.value) {
        searchKeyword.value = keyword
        messageUsersPage.initLoad()
    }
}
export const onSearchFocus = (val: boolean) => {
    searchFocus.value = val
}

const userId = computed(() => (userId?: string) => {
    if (userId) {
        return userId
    }
    return currentSelectUser.value?.chatWithUserInfo?.userId || ''
})

/***** user messages ******/
/**
 * 加载用户消息
 */
export const adminMessagesPage = ref<IPage<MessageAndShopAdmin>>(
    new IPage<MessageAndShopAdmin>(
        (page: Page) => getMessages(userId.value() ? userId.value() : '-1', { ...page, shopId: shopInfo.value.open.shopId as Long }),
        20,
    ),
)
export const messageSubmit = ({ messageType, content }: { messageType: MessageType; content: string }) => {
    if (!currentSelectUser.value) return
    if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
    const data = {
        receiverId: userId.value(),
        senderId: shopInfo.value.open.shopId,
        messageType,
        content,
    }
    sendMessages(data as { receiverId: string; senderId: string; messageType: MessageType; content: string }).then(() => {
        if (!currentSelectUser.value) return
        adminMessagesPage.value.initLoad()
        messageUsersPage.initLoad()
    })
}
const pageInit = () => {
    messageUsersPage.initLoad()
}
/**
 * 初始化 用户列表 并且 监听  客服消息
 */
export const initCustomerService = async () => {
    //初始化用户列表
    await messageUsersPage.initLoad().then((res) => {
        adminMessagesPage.value = new IPage<MessageAndShopAdmin>(
            (page: Page) =>
                getMessages(userId.value() ? userId.value() : res[0].chatWithUserInfo.userId, { ...page, shopId: shopInfo.value.open.shopId }),
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
const messageUsersOnNewMessage = (message: MessageAndShopAdmin) => {
    messageUsersPage.initLoad()
    const messageShopId = message.sender.senderUserInfo?.userId || message.sender.senderShopInfo?.shopId

    if (messageShopId === currentSelectUser.value?.chatWithUserInfo.userId || messageShopId === shopInfo.value.open.shopId) {
        adminMessagesPage.value.concatData(message)
    }
}

export const contentLoadMore = () => {
    adminMessagesPage.value.loadMore()
}
