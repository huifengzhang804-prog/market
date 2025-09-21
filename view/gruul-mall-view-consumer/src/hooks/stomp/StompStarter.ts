import { setStompConfig, stompHookMount } from '@/hooks/stomp/StompHandler'
import { useUserStore } from '@/store/modules/user'
import { useCustomerServiceStore, useMsgCountStore } from '@/store/modules/message'
import type { UserInfoType } from '@/store/modules/user/state'
import { Channel, ConnectType } from '@/hooks/stomp/typs'
import { doGetPigeonMessageMyCount } from '@/apis/consumerSever'

export class StompStarter {
  /**
   * 启动stomp监听
   */
  static start = () => {
    // 用户信息更新
    useUserStore().$subscribe((mutation, state) => {
      if (state.userInfo.token) {
        StompStarter.startAll(state.userInfo)
      }
    })
    // watch(
    //     () => useUserStore().userInfo.token,
    //     () => {
    //         StompStarter.startAll(useUserStore().userInfo)
    //     },
    // )
  }

  static startAll = (userInfo: UserInfoType) => {
    StompStarter.setConfig(userInfo)
    StompStarter.customerServiceMsg(userInfo)
    if (userInfo.token) StompStarter.msgCount(userInfo).then(() => {})
  }

  /**
   * 更新消息统计
   */
  static msgCount = async ({ token }: UserInfoType) => {
    if (!token) return
    const { code, data } = await doGetPigeonMessageMyCount()
    useMsgCountStore().setCount(code === 200 ? data : '0')
  }
  static msgCountIncrement = () => {
    const msgCountStore = useMsgCountStore()
    msgCountStore.setCount(String(parseInt(msgCountStore.getCount) + 1))
  }

  /**
   * 更新stomp配置
   */
  static setConfig = ({ info: { userId }, token }: UserInfoType) => {
    if (!userId) {
      setStompConfig({ shopId: '0', token: '', userId: '' })
      return
    }
    //stomp 配置
    setStompConfig({ shopId: '0', token, userId })
  }

  /**
   * 监听客服消息
   */
  static customerServiceMsg = (userinfo: UserInfoType) => {
    //监听客服消息
    stompHookMount(Channel.PLATFORM_SHOP_AND_USER, {
      success(msg) {
        StompStarter.msgCount(userinfo)
        useCustomerServiceStore().setMsg({
          connectType: ConnectType.SUCCESS,
          msg,
        })
      },
      fail(msg) {
        useCustomerServiceStore().setMsg({
          connectType: ConnectType.FAIL,
          msg,
        })
      },
      subscribe(msg) {
        StompStarter.msgCountIncrement()
        useCustomerServiceStore().setMsg({
          connectType: ConnectType.SUBSCRIBE,
          msg,
        })
      },
    })
  }
}
