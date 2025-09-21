/**
 * 获取店铺查询参数
 */
export interface ShopListParams {
    name?: string
    shopIds?: string[]
    shopType?: ShopType
    size?: number
    current?: number
    productIsNotEmpty: boolean
}

export type ShopType = 'SELF_OWNED' | 'PREFERRED' | 'ORDINARY' | ''

/**
 * 秒杀状态
 */
export enum SeckillQueryStatus {
    //未开始
    NOT_START = 'NOT_START',
    //进行中
    IN_PROGRESS = 'IN_PROGRESS',
    //已结束
    FINISHED = 'FINISHED',
    //已下架
    OFF_SHELF = 'OFF_SHELF',
    //违规下架
    VIOLATION_OFF_SHELF = 'VIOLATION_OFF_SHELF',
}

export const SeckillStatus: Record<
    SeckillQueryStatus,
    {
        //状态描述
        desc: string
        //倒计时器描述
        timerDesc: string
        //计时器计算的时间生成器
        timePick: (round: SeckillRoundVO) => string
        //购买按钮描述
        buyButtonText: string
        //购买按钮样式类
        buyButtonClass: string
    }
> = {
    [SeckillQueryStatus.NOT_START]: {
        desc: '未开始',
        timerDesc: '距本场开始',
        timePick: (round) => round.time.start,
        buyButtonText: '即将开抢',
        buyButtonClass: 'snap-up',
    },
    [SeckillQueryStatus.IN_PROGRESS]: {
        desc: '进行中',
        timerDesc: '本场还剩',
        timePick: (round) => round.time.end,
        buyButtonText: '马上抢',
        buyButtonClass: 'snapped-up-immediately',
    },
    [SeckillQueryStatus.FINISHED]: {
        desc: '已结束',
        timerDesc: '',
        timePick: () => '',
        buyButtonText: '已结束',
        buyButtonClass: '',
    },
    [SeckillQueryStatus.OFF_SHELF]: {
        desc: '已下架',
        timerDesc: '',
        timePick: () => '',
        buyButtonText: '已下架',
        buyButtonClass: '',
    },
    [SeckillQueryStatus.VIOLATION_OFF_SHELF]: {
        desc: '违规下架',
        timerDesc: '',
        timePick: () => '',
        buyButtonText: '已下架',
        buyButtonClass: '',
    },
}

export interface SeckillRoundPageDTO extends PageParam {
    //店铺 id 可选是否指定店铺 id查询
    shopId?: Nullable<Long>
}

/**
 * 活动场次信息
 */
export interface SeckillRoundVO {
    //场次时间范围
    time: IRange<string>
    //该场次状态
    status: SeckillQueryStatus
}

/**
 * 场次商品分页查询条件
 */
export interface SeckillRoundProductPageDTO extends PageParam {
    //店铺 id 可选是否指定店铺 id查询
    shopId?: Nullable<Long>
    //场次开始时间
    start: string
}

/**
 * 场次商品数据类型
 */
export interface SeckillRoundProductVO {
    //店铺 id
    shopId: Long
    //活动 id
    activityId: Long
    //商品 id
    productId: Long
    //商品名称
    productName: string
    //商品主图
    productImage: string
    //划线价
    price: Long
    //秒杀最低价
    minPrice: Long
    //活动库存
    activityStock: Long
}
export interface ImageListType {
    url: string
    showTime: number
    link?: {
        id: string
        type: number
        name: string
        url: string
        append: string
    }
}
// 首页开屏广告
export interface AdvertisementFormType {
    endPoint?: string
    endTime: string
    showFlag: boolean
    showFrequency: string
    skipSecond?: string
    skipWay: string
    startTime: string
    times?: string
    displayTime?: any
    imageList: ImageListType[]
}
// 首页弹窗广告
export interface HomeBulletFrameFormType {
    showFlag: boolean //是否展示
    startTime: string //展示开始时间
    endTime: string //展示结束时间
    showTime?: string //展示时长
    endPoint: string //终端
    displayTime?: any[]
    imageInfo: {
        url: string
        showTime: string
        link: any
    }
    url?: string
}
export interface PlatformPageVO {
    // 端点类型
    endpointType: string
    // 页面 id
    id: string
    // 页面名称
    name: string
    // 页面属性
    properties: string
    // 模板类型
    templateType: string
    // 页面类型
    type: string
}

export interface Page {
    // 页面 id
    pageId: string
    // 页面类型
    pageType: string
}

export interface TemplateType {
    // 业务类型
    businessType: string
    // 描述
    description: string
    // 是否启用
    enabled: boolean
    // 端点类型
    endpointType: string
    // 模板 id
    id: string
    // 模板名称
    name: string
    pages: Page[]
    templateType: string
}

/**
 * 分类数据类型
 */
export interface Category {
    // 分类图片
    categoryImg: string
    // 创建时间
    createTime: string
    // 折扣率
    deductionRatio: number
    // 是否删除
    deleted: boolean
    // 分类 id
    id: string
    // 分类等级
    level: string
    // 分类名称
    name: string
    // 父级 id
    parentId: string | number
    // 排序
    sort: number
    // 更新时间
    updateTime: string
    // 版本
    version: number
}

/**
 * 分类数据类型
 */
export interface CategoryData {
    // 子分类
    children: Category[]
    // 创建时间
    createTime: string
    // 是否删除
    deleted: boolean
    // 分类 id
    id: string
    // 分类等级
    level: string
    // 分类名称
    name: string
    // 父级 id
    parentId: number
    // 排序
    sort: number
    // 更新时间
    updateTime: string
    // 版本
    version: number
}
