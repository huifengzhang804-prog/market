import { tim, TIM, options } from './hooks'
import { doCreateGroup, doLoginIM, doJoinGroup } from './apis/index'
// 自己或好友的资料发生变更时触发，event.data 是包含 Profile 对象的数组
let onProfileUpdated = function (event: Event) {
  console.log(event, '自己或好友的资料发生变更时触发，event') // 包含 Profile 对象的数组
}
tim.on(TIM.EVENT.PROFILE_UPDATED, onProfileUpdated)
// 用户被踢下线时触发
let onKickedOut = function (event: Event) {
  console.log('用户被踢下线时触发')
  // TIM.TYPES.KICKED_OUT_MULT_ACCOUNT(Web端，同一帐号，多页面登录被踢)
  // TIM.TYPES.KICKED_OUT_MULT_DEVICE(同一帐号，多端登录被踢)
  // TIM.TYPES.KICKED_OUT_USERSIG_EXPIRED(签名过期)
  // TIM.TYPES.KICKED_OUT_REST_API(REST API kick 接口踢出。v2.20.0起支持)
}
tim.on(TIM.EVENT.KICKED_OUT, onKickedOut)
// 网络状态发生改变
let onNetStateChange = function (event: Event) {
  console.log('网络状态发生改变')
  // v2.5.0 起支持
  // event.data.state 当前网络状态，枚举值及说明如下：
  // TIM.TYPES.NET_STATE_CONNECTED - 已接入网络
  // TIM.TYPES.NET_STATE_CONNECTING - 连接中。很可能遇到网络抖动，SDK 在重试。接入侧可根据此状态提示“当前网络不稳定”或“连接中”
  // TIM.TYPES.NET_STATE_DISCONNECTED - 未接入网络。接入侧可根据此状态提示“当前网络不可用”。SDK 仍会继续重试，若用户网络恢复，SDK 会自动同步消息
}
tim.on(TIM.EVENT.NET_STATE_CHANGE, onNetStateChange)
// SDK 进入 ready 状态时触发，接入侧监听此事件，然后可调用 SDK 发送消息等 API，使用 SDK 的各项功能
// 注意：login 成功才会驱动 SDK 触发 SDK_READY 事件
let onSdkReady = function () {
  let promise = tim.getMyProfile()
  promise
    .then(function (imResponse: any) {
      console.log('个人资料 - Profile 实例', imResponse.data) // 个人资料 - Profile 实例
    })
    .catch(function (imError: string) {
      console.warn('getMyProfile error:', imError) // 获取个人资料失败的相关信息
    })
  // 修改个人标配资料
  let promise_ = tim.updateMyProfile({
    // @ts-ignore
    nick: getApp().globalData.userInfo.nickname,
    // @ts-ignore
    avatar: getApp().globalData.userInfo.avatar,
    gender: TIM.TYPES.GENDER_MALE,
    selfSignature: '我的个性签名',
    allowType: TIM.TYPES.ALLOW_TYPE_ALLOW_ANY,
  })
  promise_
    .then(function (imResponse: any) {
      console.log(imResponse.data) // 更新资料成功
    })
    .catch(function (imError: string) {
      console.warn('updateMyProfile error:', imError) // 更新资料失败的相关信息
    })

  // @ts-ignore
  const res = getApp().globalData.identity as 'HOST' | 'USER'
  if (res === 'HOST') {
    createGroup()
  } else {
    joinGroup()
  }
}

tim.on(TIM.EVENT.SDK_READY, onSdkReady)

function createGroup() {
  doCreateGroup({
    success: () => {
      console.log('创建群组')
      joinGroup()
    },
    error: () => {
      console.log('创建失败')
    },
  })
}
function joinGroup() {
  doJoinGroup({
    success: () => {
      console.log('加入群组成功')
    },
    error: () => {
      console.log('加入群组失败')
    },
  })
}
function loginIM() {
  doLoginIM({
    success: () => {
      console.log('登陆成功')
    },
    error: () => {
      console.log('登陆失败')
    },
  })
}
export { loginIM }
