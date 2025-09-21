/**
 * uni socket for stompjs
 * @author 张治保
 * date 2023-2-17
 */

export class IStompSocket {
  socketTask: any
  eventMap: Record<string, (arg1?: any, arg2?: any) => void> = {}
  constructor(url: string, protocols?: string | string[]) {
    this.socketTask = uni.connectSocket({
      url: url,
      protocols: typeof protocols === 'string' ? [protocols] : protocols,
      complete(result) {},
    })
    this.socketTask?.onOpen(() => {
      if (Object.prototype.hasOwnProperty.call(this.eventMap, 'open')) {
        this.eventMap['open']()
      } else {
        this.onopen()
      }
    })
    this.socketTask?.onMessage((res: any) => {
      if (Object.prototype.hasOwnProperty.call(this.eventMap, 'message')) {
        this.eventMap['message'](res)
      } else {
        this.onmessage(res)
      }
    })
    this.socketTask?.onClose(() => {
      if (Object.prototype.hasOwnProperty.call(this.eventMap, 'close')) {
        this.eventMap['close']()
        return
      }
      if (typeof this.onclose === 'function') {
        this.onclose()
      } else {
        this.socketTask.close({
          complete: (e: any) => {},
        })
      }
    })
    this.socketTask?.onError((res: any) => {
      if (Object.prototype.hasOwnProperty.call(this.eventMap, 'error')) {
        this.eventMap['error'](res)
      } else {
        this.onerror(res)
      }
    })
  }

  addEventListener(event: string, callback: (arg1?: any, arg2?: any) => void) {
    this.eventMap[event] = callback
  }
  /**
   * 连接开启
   */
  onopen(res?: any) {}

  /**
   * 连接关闭
   */
  onclose(res?: any) {}

  /**
   * 连接异常
   */
  onerror(res: any) {}

  /**
   * 接收消息
   */
  onmessage(res: any) {}
  /**
   * 发送消息
   */
  send(data: string | ArrayBuffer) {
    this.socketTask.send({
      data: data,
    })
  }

  /**
   * 关闭连接
   */
  close(closeOption: any) {
    this.socketTask.close({ reason: 'close' })
  }
}
