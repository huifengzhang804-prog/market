/**
 * UserTagVo
 */
export interface UserTagVo {
    /**
     * 会员标签id
     */
    id: number
    /**
     * 是否选中true选中,false未选中
     */
    option: boolean
    /**
     * 会员标签名称
     */
    tagName: string
}

/**
 * 储值记录
 */
export interface PurchaseType {
    // 创建时间
    createTime: string
    // 有效期类型
    effectiveDurationType: string
    // 有效期
    effectiveStr: string
    // 过期时间
    expireTime: string
    // 储值记录id
    id: string
    // 会员id
    memberId: string
    // 会员储值类型
    memberPurchaseTypeStr: string
    // 储值单号
    no: string
    // 支付金额
    payAmount: number
    // 支付方式
    payType: string
    // 等级
    rankCode: number
    // 类型
    type: string
    // 用户id
    userId: string
    // 用户昵称
    userNickName: string
    // 用户手机号
    userPhone: string
}
