import { ShopMode } from '@/apis/overview'

/**
 * 抽佣类型
 */
export enum ExtractionType {
    CATEGORY_EXTRACTION = 'CATEGORY_EXTRACTION',
    ORDER_SALES_EXTRACTION = 'ORDER_SALES_EXTRACTION',
}
/**
 * 店铺类型
 */
export enum ShopType {
    SELF_OWNED = 'SELF_OWNED',
    PREFERRED = 'PREFERRED',
    ORDINARY = 'ORDINARY',
}
/**
 * 状态 0.审核中,  1.正常, -1.禁用, -2审核拒绝
 */
export enum ShopStatus {
    REJECT = 'REJECT',
    FORBIDDEN = 'FORBIDDEN',
    UNDER_REVIEW = 'UNDER_REVIEW',
    NORMAL = 'NORMAL',
}

/**
 * ShopListVO
 */
export interface ShopListVO {
    /**
     * 联系地址
     */
    address?: string
    /**
     * 联系电话
     */
    contractNumber?: string
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 抽佣类型
     */
    extractionType?: keyof typeof ExtractionType
    /**
     * No comments found.
     */
    id?: number
    /**
     * logo url
     */
    logo?: string
    /**
     * 店铺名称
     */
    name?: string
    /**
     * 店铺编号
     */
    no?: string
    /**
     * 店铺评分
     */
    score?: string
    /**
     * 店铺余额
     * (object)
     */
    shopBalance?: ShopBalanceVO
    /**
     * 店铺类型
     */
    shopType?: keyof typeof ShopType
    /**
     * 状态 0.审核中,  1.正常, -1.禁用, -2审核拒绝
     */
    status?: keyof typeof ShopStatus
    /**
     * 管理员用户id
     */
    userId?: number
    /**
     * 店铺管理员手机号
     */
    userMobile?: string
    /**
     * 经营模式
     */
    mode: keyof typeof ShopMode
}

/**
 * 店铺余额
 * ShopBalanceVO
 */
export interface ShopBalanceVO {
    /**
     * 总余额 不包含待结算
     */
    total?: number
    /**
     * 待结算余额
     */
    uncompleted?: number
    /**
     * 待提现余额
     */
    undrawn?: number
}

export interface UserRecord {
    email?: string
    mobile?: string
    userId?: number | string
    username?: string
    nickname?: string
    gender?: string
    avatar?: string
}

export interface UserListResponse {
    current: number
    keywords: string
    pages: number
    records: UserRecord[]
    size: number
    total: number
}
