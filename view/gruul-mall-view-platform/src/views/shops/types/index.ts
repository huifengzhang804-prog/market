import { Geometry } from '@/apis/afs/type'
import { ShopMode } from '@/apis/overview'
import { LinkSelectItem } from '@/components/link-select/linkSelectItem'

export type UserType = Record<'userId' | 'username' | 'mobile' | 'email' | 'nickname' | 'avatar' | 'gender', string>
export { ShopMode }

export interface ShopFormType {
    [key: string]: any
    companyName: string
    address: string
    bankAcc: string
    bankAccount: BankAccountType
    bankName: string
    briefing: string
    contractNumber: string
    legalPersonIdBack: string
    legalPersonIdFront: string
    license: string
    location: Geometry
    logo: string
    name: string
    openAccountBank: string
    payee: string
    registerInfo: RegisterInfo
    userId: string
    shopType: string
    signingCategory: any[]
    shopMode: keyof typeof ShopMode
    area: [string, string, string?]
    fakeAddress: string // 假的地址，用于展示门牌号等信息(不可包含字符串: '~' )
}
export type BankAccountType = {
    payee: string
    bankName: string
    openAccountBank: string
    bankAcc?: string
    bankAccount?: string
}
export type LocationType = {
    type: string
    coordinates: string[]
}
export type RegisterInfo = {
    license: string
    legalPersonIdFront: string
    legalPersonIdBack: string
}

type SelectItemType = Record<'id' | 'name', string>
export type SelectLevelType = Record<'firstArr' | 'secondArr', SelectItemType[]>

// *---------------
// test
export interface Tree {
    categoryId: string
    id: number
    name: string
    parentId: number
    secondCategoryVos?: TreeSecondary[]
    sort: number
    categoryImg?: string
    deductionRatio?: number | null
    children?: Tree[]
    ads?: { img: string; link: LinkSelectItem }[]
}
export interface TreeSecondary {
    categoryId: string
    categoryThirdlyVos?: TreeThree[]
    id: string
    name: string
    parentId: string
    sort: number
}
export interface TreeThree {
    categoryImg: string
    id: string
    name: string
    parentId: string
    productNumber: number
    sort: number
}

export type searchParamStatus = 'NORMAL' | 'REJECT' | 'UNDER_REVIEW' | ''
export interface searchParamType {
    no?: string
    name?: string
    status?: searchParamStatus
    shopType?: string
    extractionType?: string
    shopModes?: string
    applyUserPhone?: string
    auditUserPhone?: string
    settledStartTime?: Date | undefined
    settledEndTime?: Date | undefined
    applyStartTime?: Date | undefined
    applyEndTime?: Date | undefined
    data?: Date[]
    applyOfData?: Date[]
    queryAuditInfo?: boolean
}
