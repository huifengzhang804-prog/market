export interface ShopList {
    shopType: ShopType
    name: string
    id: string
    logo: string
    score: string
    newTips: string
}

export type ShopType = 'SELF_OWNED' | 'PREFERRED' | 'ORDINARY'

export const shopTypeMap = {
    SELF_OWNED: '自营',
    PREFERRED: '优选',
    ORDINARY: '普通',
} as const
