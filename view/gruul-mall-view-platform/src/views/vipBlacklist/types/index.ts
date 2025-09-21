enum ROLE {
    FORBIDDEN_COMMENT,
    FORBIDDEN_ORDER,
}
export type SearchParamsType = {
    userNickname: string
    roles: keyof typeof ROLE | string | null
}
export type MemberType = {
    memberType: string
    rankCode: number
}
export interface TableListType {
    balance: string
    createTime: string
    dealTotalMoney: string
    explain: string
    gender: string
    growthValue: string
    id: string
    member: MemberType
    userAuthority: string[]
    userHeadPortrait: string
    userId: string
    userNickname: string
    userPhone: string
    userTagVOList: any[]
    checked?: boolean
}
