/**
 * 普通会员列表
 */
export interface ApiBaseVipListItem {
    [key: string]: any
    balance: string
    consumeCount: number
    createTime: string
    dealTotalMoney: string
    distributionCount: number
    id: string
    userId: string
    integralTotal: string
    remark: string
    userHeadPortrait: string
    userPhone: string
    userNickname: string
    checked?: boolean
    userTagVOList: [{ tagId: string; tagName: string }]
}
/**
 * 会员标签
 */
export interface ApiTagItem {
    id: string
    tagName: string
    option: false
}

/**
 *下拉选择状态初始数据
 */
export interface SearchFromDataType {
    userCardNum: string // 会员卡号
    userNickname: string // 用户名称
    tagId: string //标签id
    userPhone: string // 用户手机
    memberType: string // 用户类型
    clinchTime: any
    rankCode: string
    consigneeName: string // 收货人姓名
}

/**
 * 会员搜索参数
 */
export interface ParamsSearchVipBase {
    userCardNum: string
    userNickname: string
    consigneeName: string
    registrationStartTime: string
    registrationEndTime: string
    tagId: string
    sortType: string
    userId: number
}
export interface ApiMemberInfoType {
    [key: string]: any
    balance: string
    createTime: string
    dealTotalMoney: string
    gender: string
    growthValue: string
    id: string
    userHeadPortrait: string
    userId: string
    userNickname: string
    userPhone: string
    userTagVOList: { tagId: string; tagName: string }[]
}
export enum PAYTYPE {
    SYSTEM_GIVE,
    PERSONAL_CHARGING,
    SYSTEM_CHARGING,
    SHOPPING_PURCHASE,
    PURCHASE_MEMBER,
    REFUND_SUCCEED,
    WITHDRAW,
    GIVE,
}

export interface TopUpBalanceType {
    isShowTopUpBalance: boolean
    currentBalance: ApiBaseVipListItem
}
