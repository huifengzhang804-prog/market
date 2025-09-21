import { ShopStatus, ShopMode, ShopType } from '@/apis/afs/type'

export interface ShopInfoStore {
    id: Long
    logo: string
    name: string
    newTips?: string
    status: keyof typeof ShopStatus
    shopMode: keyof typeof ShopMode
    userId: string
    shopId: Long
    shopType?: keyof typeof ShopType
    token?: string
    refresh_token?: string
    nickname?: string
}
const shopInfo: ShopInfoStore = {
    id: '',
    logo: '',
    name: '',
    newTips: '',
    status: 'NORMAL',
    token: '',
    userId: '',
    nickname: '',
    refresh_token: '',
    shopId: '',
    shopMode: 'COMMON',
    shopType: 'ORDINARY',
}
export default shopInfo
