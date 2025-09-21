import { ref } from 'vue'
enum TYPE_ENUM {
  'LIKE' = 'LIKE',
  'VIEWERSHIP' = 'VIEWERSHIP',
}
interface MessageType {
  type: keyof typeof TYPE_ENUM
  data: {
    liveId: ''
    count: 0
  }
}
// 实例
const socketTask = ref()
const isOpenSocket = ref(false)
const socketMessageData = ref({
  VIEWERSHIP: {
    data: {
      liveId: '',
      count: 0,
    },
  },
  LIKE: {
    data: {
      liveId: '',
      count: 0,
    },
  },
})
const socketUrl = (id: string) => `${import.meta.env.VITE_LIVE_WEB_SOCKET}/ws/live?liveId=${id}`
function connectSocket(url: string, time: number) {
  // 如果协议是https使用wss，http使用ws
  url = `wss://${url}`
  // 心跳检测
  const timeout = ref(time)
  let heartbeat = null as NodeJS.Timeout | null
  try {
    socketMessageData.value = {
      VIEWERSHIP: {
        data: {
          liveId: '',
          count: 0,
        },
      },
      LIKE: {
        data: {
          liveId: '',
          count: 0,
        },
      },
    }
    return connectSocketInit()
  } catch (e) {
    isOpenSocket.value = false
    reconnect()
  }
  function connectSocketInit() {
    socketTask.value = uni.connectSocket({
      url: url,
      header: {
        // Authorization: 'Bearer ' + 'token',
      },
      success: (success) => {
        console.log(success)
      },
      fail: (fail) => {
        console.log(fail)
      },
      complete: (complete) => {
        console.log(complete)
      },
    })
    // 建立websocket连接
    socketTask.value.onOpen((res: any) => {
      if (heartbeat) {
        clearTimeout(heartbeat)
      }
      isOpenSocket.value = true
      start()
      // 注：只有连接正常打开中 ，才能正常成功发送消息
      socketTask.value.onMessage((res: any) => {
        const newRes = JSON.parse(res.data) as MessageType
        socketMessageData.value[newRes.type] = newRes
      })
    })
    // 监听webscket关闭的事件
    socketTask.value.onClose(() => {
      isOpenSocket.value = false
      socketMessageData.value = {
        VIEWERSHIP: {
          data: {
            liveId: '',
            count: 0,
          },
        },
        LIKE: {
          data: {
            liveId: '',
            count: 0,
          },
        },
      }
      reconnect()
    })
  }
  // 重新连接
  function reconnect() {
    if (heartbeat) {
      clearInterval(heartbeat)
    }
    if (!isOpenSocket.value) {
      setTimeout(() => {
        connectSocketInit()
      }, 3000)
    }
  }
  //开启心跳检测
  function start() {}
  //发送消息
}
function send(data: MessageType) {
  socketTask.value.send({ data: JSON.stringify(data) })
}
export { connectSocket, socketTask, type MessageType, send, socketUrl, socketMessageData }
