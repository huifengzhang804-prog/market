import { StompMessage } from './message'
import { stompHookMount } from '@/composables/stomp/StompHandler'
import { messagePage } from '@/apis/message'
import { Channel } from '@/composables/stomp/typs'
import { configurePlatform } from '@/store/modules/configurePlatform'

//消息列表
const messages = ref<Array<StompMessage>>([])
//滚动播放的下标
const rollIndex = ref<number>(0)
//当前滚动下标的消息
export const message = ref<StompMessage | null>(null)
//是否展示当前消息 用于控制动画效果
const show = ref<boolean>(true)

//滚动消息的计时器
let messageTimer: NodeJS.Timer | null = null
/**
 *动画
 */
const animationControl = () => {
    return new Promise<void>((resolve) => {
        const timer = 600
        show.value = false
        let showTimer = setTimeout(() => {
            show.value = true
            if (showTimer) {
                clearTimeout(showTimer)
            }
        }, timer)
        let switchNextTimer = setTimeout(() => {
            if (switchNextTimer) {
                clearTimeout(switchNextTimer)
            }
            resolve()
        }, timer / 2)
    })
}
//滚动标题
const rollMessage = () => {
    const msgList = messages.value
    const length = msgList.length
    if (length <= 1) {
        return
    }
    animationControl().then(() => {
        const current = rollIndex.value
        const index = current % length
        message.value = msgList[index]
        rollIndex.value = current + 1
    })
}
/**
 * 监听并取消或开始定时器
 */
const timerHandler = (current: number, pre: number) => {
    if (current <= 1) {
        if (messageTimer) {
            clearInterval(messageTimer as NodeJS.Timeout)
        }
        messageTimer = null
    }
    if (current === 0) {
        message.value = null
        return
    }
    if (current === 1) {
        message.value = messages.value[0]
    }
    //重置滚动下标
    rollIndex.value = 0
    if (pre > 1) {
        return
    }
    messageTimer = setInterval(rollMessage, 4000)
}
/**
 * 监听数组长度变化 启用或终止定时任务
 */
watch(() => messages.value.length, timerHandler)

//
const getMessages = () => {
    return messagePage({
        read: false,
    })
        .then((response) => {
            const data = response.data
            if (!data) {
                return
            }
            const records = data.records
            if (!records) {
                return
            }
            messages.value = records
            configurePlatform().SET_NEWS(data.total)
        })
        .catch((err) => {
            console.debug(err)
        })
}
export const messageInit = () => {
    getMessages().then(acceptMessage)
}

/**
 * 监听接收消息
 */
const acceptMessage = () => {
    stompHookMount(Channel.NOTICE, {
        success: messageInit,
        fail: () => {},
        subscribe: (message) => {
            const messageId = message.messageId
            if (!messageId) {
                console.error('没有消息id的消息')
            }
            messageInit()
        },
    })
}
