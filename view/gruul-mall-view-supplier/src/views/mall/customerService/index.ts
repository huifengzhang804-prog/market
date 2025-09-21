import { ref, watch, computed, reactive } from 'vue'
import { ChatMessage, IPage, MessageAndShopAdmin, MessageUser, CustomerServiceMessage } from '@/views/mall/customerService/types'
import { stompHookMount } from '@/composables/stomp/StompHandler'
import { Channel } from '@/composables/stomp/typs'
import { getMessages, getMessageUsers, sendMessages } from '@/apis/message'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { Page } from '@/components/pageManage'
import DateUtil from '@utils/date'

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

const searchKeyword = ref('')
export const messageUsersPage = reactive<IPage<MessageUser>>(
    new IPage<MessageUser>((page: Page) => getMessageUsers(searchKeyword.value, { ...page, chatWithType: 'PLAT_FORM' }), 30),
)

export const currentSelectUser = ref<null | MessageUser>(null)
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

export const adminMessagesPage = ref<IPage<MessageAndShopAdmin>>(
    new IPage<MessageAndShopAdmin>((page: Page) => {
        if (!currentSelectUser.value) return Promise.reject()
        console.log('currentSelectUser.value', currentSelectUser.value)
        if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false
        const { chatWithShopInfo } = currentSelectUser.value

        return getMessages(
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
    sendMessages({
        receiverShopId: chatWithShopInfo.shopId,
        messageType: chatMessage.messageType,
        content: chatMessage.message,
    }).then(() => {
        if (!currentSelectUser.value) return
        adminMessagesPage.value.initLoad()
        let lastMessage: any = currentSelectUser.value.lastMessage
        lastMessage = !lastMessage ? { message: '', createTime: '' } : lastMessage
        lastMessage.message = chatMessage.message
        lastMessage.createTime = new DateUtil().getYMDHMSs()
        currentSelectUser.value.lastMessage = lastMessage
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
    await messageUsersPage
        .initLoad()
        .then((res) => {
            adminMessagesPage.value = new IPage<MessageAndShopAdmin>((page: Page) => {
                if (!currentSelectUser.value) return Promise.reject()
                if (currentSelectUser.value.lastMessage) currentSelectUser.value.lastMessage.show = false

                const { chatWithShopInfo } = currentSelectUser.value

                return getMessages(
                    {
                        senderShopId: chatWithShopInfo.shopId,
                    },
                    page,
                )
            }, 20)
        })
        .catch((err) => {
            console.log('err', err)
        })
    //监听客服消息
    stompHookMount(Channel.SUPPLIER_SHOP, {
        success: pageInit,
        fail: () => {},
        subscribe: (message) => {
            messageUsersOnNewMessage(message as CustomerServiceMessage)
        },
    })
}
/**
 * 新的消息处理
 * @param message
 */
const messageUsersOnNewMessage = (messageObj: CustomerServiceMessage) => {
    messageUsersPage.initLoad()
    if (messageObj.sender.senderShopInfo.shopId === currentSelectUser.value?.chatWithShopInfo.shopId) {
        const { sender, receiver, messageType, message } = messageObj
        const messageAdmin: MessageAndShopAdmin = {
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
