//@ts-ignore
import Stomp from 'stompjs'
import { ref } from 'vue'
import NewMessage from '@/assets/media/new-message.mp3'
import OrderReminder from '@/assets/media/order-reminder.mp3'
import { playSound } from '@/libs/MediaPlayer'
import { ConnectStatus, StompHook, StompConfig, Channel, BaseMessage } from './typs'

const destination = '/topic/pigeon-to'

//初始化stomp客户端
const client = ref(null)
const url = `${import.meta.env.VITE_BASE_URL}${import.meta.env.VITE_STOMP_CONNECT_URI}`.replace(/^http/, 'ws')
/**
 *  配置信息 将存放至 stomp 链接头里
 */
const stompConfig = ref<StompConfig>({
    shopId: '',
    token: '',
    userId: '',
})

/**
 * 需要执行的函数列表
 */
const hooks: Map<Channel, StompHook> = new Map<Channel, StompHook>()

/**
 * 处理回调
 */
const doHooks = (status: ConnectStatus, response: any) => {
    console.log('response', response)
    if (status !== ConnectStatus.SUBSCRIBE) {
        return Promise.all(
            Array.from(hooks.values()).map(
                (hook) =>
                    new Promise((resolve) => {
                        hook[status](response)
                        resolve(null)
                    }),
            ),
        )
    }
    const data = JSON.parse(response?.body) as BaseMessage
    console.log('data', data)
    playMedia(data)
    const stompHook = hooks.get(data.channel)
    if (!stompHook) return new Promise(() => {})
    return new Promise((resolve) => {
        stompHook[status](data)
        resolve(null)
    })
}
const playMedia = (message: BaseMessage) => {
    let media = NewMessage
    if (message) {
        switch (message.channel) {
            case Channel.NEW_ORDER:
                media = OrderReminder
                break
            default:
                break
        }
    }
    playSound(media).then(() => {})
}
/**
 * 异常重连定时器
 */
let reconnectTimeOut: NodeJS.Timeout | null = null
/**
 * 重新连接
 */
const reconnect = () => {
    stopReconnect()
    if (isConnected.value) {
        return
    }
    reconnectTimeOut = setTimeout(connect, 5000)
}
/**
 * 关闭重试
 */
const stopReconnect = () => {
    if (reconnectTimeOut) {
        clearTimeout(reconnectTimeOut)
        reconnectTimeOut = null
    }
}

/**
 * 关闭链接
 */
const stopConnect = () => {
    stopReconnect()
    disconnect()
}
/**
 * 渲染头
 */
const renderHeaders = () => {
    return {
        Authorization: stompConfig.value?.token,
        'Shop-Id': stompConfig.value.shopId,
        'Client-Type': import.meta.env.VITE_CLIENT_TYPE,
    }
}

/**
 * 连接成功
 */
const connectSuccess = (successData: any) => {
    isConnected.value = true
    stopReconnect()
    console.log('successData---', successData)
    doHooks(ConnectStatus.SUCCESS, successData).then(subscribe)
}
/**
 * 订阅主题
 */
const subscribe = () => {
    //订阅主题
    const stompClient = client.value
    if (!stompClient) return
    const headers = renderHeaders()
    //接受当前客户端的所有消息
    const all = `${destination}-${headers['Client-Type']}`
    //接收当前店铺的所有消息
    const shopAll = `${all}-${headers['Shop-Id']}`
    //接受当前用户的个人消息
    const currentUser = `${shopAll}-${stompConfig.value.userId}`
    // @ts-ignore
    const destinations = [all, shopAll, currentUser]
    destinations.forEach(
        // @ts-ignore
        (dest) => stompClient.subscribe(dest, (data) => doHooks(ConnectStatus.SUBSCRIBE, data), { ...headers }),
    )
}

//是否尝试重新连接
const tryReconnect = ref<boolean>(true)
/**
 * 连接异常
 */
const connectFail = (frame: any) => {
    isConnected.value = false
    doHooks(ConnectStatus.FAIL, frame).then(() => {
        const headers = frame.headers
        if (!headers) {
            if (tryReconnect.value) {
                reconnect()
            }
            return
        }
        const message = headers.message
        if (!message) {
            reconnect()
            return
        }
        try {
            JSON.parse(message.replaceAll('\\c', ':'))
            tryReconnect.value = false
        } catch (e) {
            //Connection to broker closed.
            reconnect()
            return
        }
    })
}
/**
 * 断连disconnect
 */
const disconnect = () => {
    const stompClient = client.value
    if (!stompClient) {
        return
    }
    // @ts-ignore
    stompClient.disconnect(() => {
        const currentClient = client.value
        if (currentClient) {
            client.value = null
        }
    }, renderHeaders())
}
/**
 * 客户端连接初始化
 */
const connect = () => {
    disconnect()
    const headers = renderHeaders()

    const token = headers.Authorization
    const shopId = headers['Shop-Id']
    if (!token || !shopId) {
        return
    }
    let stompClient = client.value
    if (stompClient !== null) {
        return
    }
    stompClient = Stomp.client(url)
    // @ts-ignore
    stompClient.debug = false
    client.value = stompClient
    // @ts-ignore
    stompClient.connect(headers, connectSuccess, connectFail)
}

/**
 * 是否已连接
 */
export const isConnected = ref(false)

/**
 * 设置配置
 * @param config
 */
export const setStompConfig = (config: StompConfig) => {
    if (!config) {
        stopConnect()
    }
    const { shopId, token, userId } = config
    stompConfig.value = { shopId, token, userId }
    if (!shopId || !token || !userId) {
        stopConnect()
        return
    }
    reconnect()
}

/**
 * 观察监听接收到的消息
 * @param key
 * @param hook
 */
export const stompHookMount = (key: Channel, hook: StompHook) => {
    if (!hook) {
        return
    }
    hooks.set(key, hook)
}
