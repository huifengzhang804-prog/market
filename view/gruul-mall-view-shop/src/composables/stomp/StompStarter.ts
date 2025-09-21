import { setStompConfig } from '@/composables/stomp/StompHandler'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { ShopInfoStore } from '@/store/modules/shopInfo/state'

export class StompStarter {
    /**
     * 设置stomp配置参数
     * @param shopInfo 店铺信息 token 店铺id 用户id
     */
    static setConfig = (shopInfo: ShopInfoStore) => {
        if (!shopInfo) {
            setStompConfig({ shopId: '', token: '', userId: '' })
            return
        }
        const { id, token, userId } = shopInfo
        setStompConfig({ shopId: id.toString(), token: token || '', userId: userId || '' })
    }
    /**
     * 启动 stomp 连接
     */
    static start = () => {
        useShopInfoStore().$subscribe((mutation, state) => {
            StompStarter.setConfig(state.shopInfo)
        })
        setStompConfig({
            shopId: useShopInfoStore().getterShopInfo.id.toString(),
            token: useShopInfoStore().getterShopInfo.token || '',
            userId: useShopInfoStore().getterShopInfo.userId || '',
        })
    }
}
