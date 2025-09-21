import { useDevListenerStore } from '@/store/modules/devListener'

// 监听组件
export function watchPluginPort() {
    const devListenerStoreHook = useDevListenerStore()

    // 初始化WebSocket连接
    let socket: WebSocket | null = null
    let heartbeatInterval: number | null = null
    let reconnectTimeout: number | null = null

    // 心跳间隔时间(毫秒)
    const HEARTBEAT_INTERVAL = 30000
    // 重连间隔时间(毫秒)
    const RECONNECT_TIMEOUT = 5000
    // 最大重连次数
    const MAX_RECONNECT_ATTEMPTS = 5
    let reconnectAttempts = 0

    // 客户端信息
    const clientInfo = {
        type: '平台端', // 客户端类型
    }

    // 美化日志
    const log = {
        info: (message: string) => console.log(`%c 🔌 ${message}`, 'color: #3498db'),
        success: (message: string) => console.log(`%c ✅ ${message}`, 'color: #2ecc71'),
        warn: (message: string) => console.log(`%c ⚠️ ${message}`, 'color: #f39c12'),
        error: (message: string) => console.log(`%c ❌ ${message}`, 'color: #e74c3c'),
        heart: (message: string) => console.log(`%c 💓 ${message}`, 'color: #9b59b6'),
        reload: (message: string) => console.log(`%c 🔄 ${message}`, 'color: #1abc9c'),
    }

    function connect() {
        // 清除先前的重连定时器
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout)
            reconnectTimeout = null
        }

        // 使用URL查询参数传递客户端类型
        const wsUrl = `ws://localhost:9998?clientType=${clientInfo.type}`

        // 创建新的WebSocket连接
        socket = new WebSocket(wsUrl)

        // 连接打开时的处理
        socket.addEventListener('open', () => {
            log.success(`插件热更新服务 WS 连接已建立 (${clientInfo.type})`)
            reconnectAttempts = 0 // 重置重连计数
            startHeartbeat() // 启动心跳
        })

        // 消息处理
        socket.addEventListener('message', (event) => {
            const message = JSON.parse(event.data)

            // 如果是心跳响应，则不进行其他处理
            if (message.type === 'pong') {
                log.heart('收到心跳响应')
                return
            }

            if (message.type === 'file-change') {
                log.reload(`文件变更: ${message.service}/${message.name}`)
                // 重新加载页面
                devListenerStoreHook.SET_RELOAD_STATE(!devListenerStoreHook.getReloadState)
                devListenerStoreHook.SET_RELOAD_PATH(`${message.service}/${message.name}`)
            }
        })

        // 错误和关闭处理
        socket.addEventListener('error', (error) => {
            log.error('插件热更新服务 WS 连接错误')
            stopHeartbeat()
            reconnect()
        })

        socket.addEventListener('close', () => {
            log.warn('插件热更新服务 WS 连接已关闭')
            stopHeartbeat()
            reconnect()
        })
    }

    // 启动心跳
    function startHeartbeat() {
        stopHeartbeat() // 先停止可能存在的心跳

        heartbeatInterval = window.setInterval(() => {
            if (socket && socket.readyState === WebSocket.OPEN) {
                // 发送心跳消息，携带客户端信息
                socket.send(
                    JSON.stringify({
                        type: 'ping',
                        clientInfo,
                    }),
                )
                log.heart('发送心跳')
            }
        }, HEARTBEAT_INTERVAL)
    }

    // 停止心跳
    function stopHeartbeat() {
        if (heartbeatInterval) {
            clearInterval(heartbeatInterval)
            heartbeatInterval = null
        }
    }

    // 重连逻辑
    function reconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            log.error(`已重连${MAX_RECONNECT_ATTEMPTS}次，停止重连`)
            return
        }

        reconnectAttempts++
        log.info(`第${reconnectAttempts}次重连，${RECONNECT_TIMEOUT / 1000}秒后...`)

        reconnectTimeout = window.setTimeout(() => {
            log.info('重新连接中...')
            connect()
        }, RECONNECT_TIMEOUT)
    }

    // 初始连接
    connect()

    // 确保在组件销毁时清理资源
    window.addEventListener('beforeunload', () => {
        stopHeartbeat()
        if (reconnectTimeout) {
            clearTimeout(reconnectTimeout)
        }
        if (socket) {
            socket.close()
        }
    })
}
