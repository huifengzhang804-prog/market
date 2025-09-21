import { setStompConfig } from '@/composables/stomp/StompHandler'
import { useUserStore } from '@/store/modules/user'

export class StompStarter {
  /**
   * 设置stomp配置参数
   * @param userInfo 店铺信息 token 店铺id 用户id
   */
  static setConfig = (userInfo: any) => {
    if (!userInfo.info) {
      setStompConfig({ shopId: '', token: '', userId: '' })
      return
    } else {
      const { userId } = userInfo.info
      setStompConfig({ shopId: '0', token: useUserStore().getterToken, userId })
    }
  }
  /**
   * 启动 stomp 连接
   */
  static start = () => {
    useUserStore().$subscribe((mutation, state) => {
      console.log(state, 'state')
      StompStarter.setConfig(state)
    })
    setStompConfig({ ...useUserStore().getterUserInfo, shopId: '0', token: useUserStore().getterToken })
  }
}
