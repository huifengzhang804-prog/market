/**
 * PageMemberRights
 */
export interface PageMemberRights {
    /**
     * countId
     */
    countId?: string
    /**
     * 当前页
     */
    current: number
    /**
     * 单页分页条数限制
     */
    maxLimit?: number
    /**
     * 自动优化 COUNT SQL
     */
    optimizeCountSql?: boolean
    /**
     * {@link #optimizeJoinOfCountSql()}
     */
    optimizeJoinOfCountSql?: boolean
    /**
     * 排序字段信息
     */
    orders?: OrderItem[]
    /**
     * 当前分页总页数
     */
    pages: number
    /**
     * 查询数据列表
     */
    records: MemberRights[]
    /**
     * 是否进行 count 查询
     */
    searchCount?: boolean
    /**
     * 每页显示条数，默认 10
     */
    size: number
    /**
     * 总数
     */
    total: number
}

/**
 * OrderItem
 */
export interface OrderItem {
    /**
     * 是否正序排列，默认 true
     */
    asc?: boolean
    /**
     * 需要进行排序的字段
     */
    column?: string
}

/**
 * MemberRights
 */
export interface MemberRights {
    /**
     * 创建时间
     */
    createTime?: string
    /**
     * 逻辑删除标记
     */
    deleted?: boolean
    /**
     * id
     * {@link com.medusa.gruul.common.mp.config.MybatisPlusConfig#identifierGenerator()}
     */
    id: number
    /**
     * 权益说明
     */
    rightsExplain?: string
    /**
     * 权益图标
     */
    rightsIcon?: string
    /**
     * 权益名称
     */
    rightsName?: string
    /**
     * 权益开关
     */
    rightsSwitch?: boolean
    /**
     * 权益类型
     */
    rightsType: keyof typeof RightsType
    /**
     * 更新时间
     */
    updateTime?: string
    /**
     * 乐观锁版本号
     */
    version?: number
}

/**
 * 权益类型
 * GOODS_DISCOUNT: '商品抵扣',
 * INTEGRAL_MULTIPLE: '积分加倍',
 * LOGISTICS_DISCOUNT: '物流优惠',
 * PRIORITY_SHIPMENTS: '优先发货',
 * QUICKNESS_AFS: '极速售后',
 * EXCLUSIVE_SERVICE: '专属客服',
 * USER_DEFINED: '自定义权益',
 */
export enum RightsType {
    GOODS_DISCOUNT = 'GOODS_DISCOUNT',
    INTEGRAL_MULTIPLE = 'INTEGRAL_MULTIPLE',
    LOGISTICS_DISCOUNT = 'LOGISTICS_DISCOUNT',
    PRIORITY_SHIPMENTS = 'PRIORITY_SHIPMENTS',
    QUICKNESS_AFS = 'QUICKNESS_AFS',
    EXCLUSIVE_SERVICE = 'EXCLUSIVE_SERVICE',
    GROWTH_VALUE_MULTIPLE = 'GROWTH_VALUE_MULTIPLE',
    USER_DEFINED = 'USER_DEFINED',
}

export interface MemberConfig {
    id: string
    rightsName: string
    rightsType: keyof typeof RightsType
}

export interface LabelJson {
    fontColor: string
    id: string
    labelColor: string
    name: string
    priceFontColor: string
    priceLabelColor: string
    priceLabelName: string
}

export interface RelevancyRights {
    name: string
    rightsType: keyof typeof MEMBERBENEFITSTATUS
    memberRightsId: string
    extendValue?: string | number
}

export interface FreeMemberInfo {
    freeMemberName: string
    id: string
    labelJson: LabelJson
    needValue: number
    relevancyRightsList: RelevancyRights[]
}
/**
 * 会员权益状态
 * @param GOODS_DISCOUNT 商品抵扣
 * @param INTEGRAL_MULTIPLE 积分加倍
 * @param LOGISTICS_DISCOUNT 物流优惠
 * @param PRIORITY_SHIPMENTS 优先发货
 * @param QUICKNESS_AFS 极速售后
 * @param EXCLUSIVE_SERVICE 专属客服
 * @param USER_DEFINED 自定义
 */
export enum MEMBERBENEFITSTATUS {
    GOODS_DISCOUNT,
    INTEGRAL_MULTIPLE,
    LOGISTICS_DISCOUNT,
    PRIORITY_SHIPMENTS,
    QUICKNESS_AFS,
    EXCLUSIVE_SERVICE,
    USER_DEFINED,
}
