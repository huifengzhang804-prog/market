import { IStompSocket } from '@/utils/StompSock'
import Stomp from 'stompjs'
import { ref } from 'vue'
import { ConnectStatus, type StompHook, type StompConfig, Channel, type BaseMessage } from './typs'

const destination = '/topic/pigeon-to'

//可以使用websocket http直接替换为ws 使用websocket连接
// @ts-ignore
const originalUrl = `${import.meta.env.VITE_BASE_URL}${import.meta.env.VITE_STOMP_CONNECT_URI}`
// const url = uni.canIUse('WebSocket') ? originalUrl.replace(/^http/, 'ws') : originalUrl
const url = originalUrl.replace(/^http/, 'ws')
//初始化stomp客户端
const client = ref(null) as any

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
  const stompHook = hooks.get(data.channel)
  if (!stompHook) return new Promise(() => {})
  return new Promise((resolve) => {
    stompHook[status](data)
    resolve(null)
  })
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
    'Client-Type': 'CONSUMER',
  }
}

/**
 * 连接成功
 */
const connectSuccess = (successData: any) => {
  console.log('-----------------------------------------\n----------Stomp Connect Success----------\n-----------------------------------------')
  isConnected.value = true
  stopReconnect()
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
  const all = `${destination}-${headers['Client-Type']}-${headers['Shop-Id']}-${stompConfig.value.userId}`
  const destinations = [all]
  destinations.forEach((destination) => stompClient.subscribe(destination, (data: any) => doHooks(ConnectStatus.SUBSCRIBE, data), { ...headers }))
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
      JSON.parse(message.replace(/(\\c)/g, ':'))
      tryReconnect.value = false
    } catch (e) {
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
  // @ts-ignore
  Stomp.WebSocketClass = IStompSocket
  stompClient = Stomp.client(url)
  stompClient.debug = false
  client.value = stompClient
  stompClient.connect(headers, connectSuccess, connectFail)
}

/**
 * 是否已连接
 */
const isConnected = ref(false)

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
