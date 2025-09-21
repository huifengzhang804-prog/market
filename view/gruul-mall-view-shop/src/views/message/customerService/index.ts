import { MessageType, IPage, MessageAndShopAdmin, MessageUser, CustomerServiceMessage } from '@/views/message/customerService/types'
import { stompHookMount } from '@/composables/stomp/StompHandler'
import { Channel } from '@/composables/stomp/typs'
import { getMessages, getMessageUsers, sendMessages } from '@/apis/message'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { Page } from '@/components/BetterPageManage'

export const shopInfo = ref(useShopInfoStore().getterShopInfo)
useShopInfoStore().$subscribe((mutation, state) => {
    const info = state.shopInfo
    shopInfo.value = info
    if (!info || !info.id || !info.token) return
    currentSelectUser.value = void 0
    messageUsersPage.initLoad()
})
/***** constant ******/

/***** user list ******/
const searchKeyword = ref('')
export const onKeywordsChange = (keyword: string) => {
    searchKeyword.value = keyword
    messageUsersPage.initLoad()
}
export const messageUsersPage = reactive<IPage<MessageUser>>(
    new IPage<MessageUser>((page: Page) => getMessageUsers(searchKeyword.value, { ...page, chatWithType: 'PLAT_FORM' }, shopInfo.value.id), 30),
)
export const currentSelectUser = ref<undefined | MessageUser>(void 0)
/**
 * 切换客户
 * @param selectUser 选择的客户
 */
export const onChange = (selectUser: MessageUser) => {
    currentSelectUser.value = selectUser
    console.log(selectUser, 'selectUser')
    nextTick(async () => {
        if (selectUser.chatWithUserInfo?.userId) {
            await adminMessagesPage.value.initLoad()
        }
    })
}

export const searchFocus = ref(false)
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
    new IPage<MessageAndShopAdmin>((page: Page) => getMessages(userId.value() ? userId.value() : '-1', { ...page, shopId: shopInfo.value.id }), 20),
)
export const messageSubmit = ({ messageType, content }: { messageType: MessageType; content: string }) => {
    if (!currentSelectUser.value) return
    if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
    const data = {
        receiverId: userId.value(),
        senderId: shopInfo.value.id,
        messageType,
        content,
    }
    sendMessages(data).then(() => {
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
            (page: Page) => getMessages(userId.value() ? userId.value() : res[0].chatWithUserInfo?.userId, { ...page, shopId: shopInfo.value.id }),
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
    const messageShopId = messageObj.sender.senderUserInfo?.userId || messageObj.sender.senderShopInfo?.shopId
    console.log('shopInfo.value.id', shopInfo.value.id)
    if (messageShopId === currentSelectUser.value?.chatWithUserInfo?.userId || messageShopId === shopInfo.value.id) {
        adminMessagesPage.value.concatData(messageObj)
    }
}

export const contentLoadMore = () => {
    adminMessagesPage.value.loadMore()
}
