import { setStompConfig } from './StompHandler'
import { useAdminInfo } from '@/store/modules/admin'

export class StompStarter {
    /**
     * 设置stomp配置参数
     * @param shopInfo 店铺信息 token 店铺id 用户id
     */
    static setConfig = (shopInfo: any) => {
        if (!shopInfo) {
            setStompConfig({ shopId: '', token: '', userId: '' })
            return
        }
        const { shopId, token, userId } = shopInfo
        setStompConfig({ shopId: shopId, token, userId })
    }
    /**
     * 启动 stomp 连接
     */
    static start = () => {
        useAdminInfo().$subscribe((mutation, state) => {
            const { value: access_token, userId, shopId } = state.adminInfo
            StompStarter.setConfig({ token: access_token, userId, shopId })
        })
        const {
            value: access_token,
            open: { userId, shopId },
        } = useAdminInfo().getterAdminInfo

        setStompConfig({ token: access_token, userId, shopId })
    }
}
