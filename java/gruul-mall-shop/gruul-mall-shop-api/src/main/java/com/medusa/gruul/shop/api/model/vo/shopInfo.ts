//店铺ts类型定义
type Long = number | string

/**
 * 店铺运营模式
 */
export enum ShopMode {
    //普通店铺
    COMMON = 'COMMON',
    //供应商
    SUPPLIER = 'SUPPLIER',
    //o2o 店铺
    O2O = 'O2O'
}

/**
 * 店铺类型
 */
export enum ShopType {
    //自营
    SELF_OWNED = 'SELF_OWNED',
    //优选
    PREFERRED = 'PREFERRED',
    //普通
    ORDINARY = 'ORDINARY'
}

/**
 * 店铺状态
 */
export enum ShopStatus {
    //审核拒绝
    REJECT = 'REJECT',
    //禁用
    FORBIDDEN = 'FORBIDDEN',
    //审核中
    UNDER_REVIEW = 'UNDER_REVIEW',
    //正常
    NORMAL = 'NORMAL'
}

/**
 * 平台抽佣类型
 */
export enum ExtractionType {
    //类目抽佣
    CATEGORY_EXTRACTION = 'CATEGORY_EXTRACTION',
    //订单销售额抽佣
    ORDER_SALES_EXTRACTION = 'ORDER_SALES_EXTRACTION'
}

/**
 * 几何类型
 */
export enum GeometryType {
    //点
    Point = 'Point',
    //多边形
    Polygon = 'Polygon'
}

/**
 * 单个坐标类型 （地图上可以理解为经纬度和和海拔）
 * 0: 经度 x
 * 1: 纬度 y
 * 2: 海拔 z
 */
export type Coordinate = [number, number, number?]

/**
 * 物理几何描述
 */
export interface Geometry {
    //类型
    type: GeometryType,
    //坐标 Point=》Coordinate ｜ Polygon=》Coordinate[]
    coordinates: Coordinate | Coordinate[]
}

export interface ShopInfoVO {
    //店铺 id
    id: Long
    //店铺运营模式
    shopMode: ShopMode
    //店铺类型
    shopType: ShopType
    //店铺名称
    name: string
    //联系方式
    contractNumber: string
    //店铺 logo
    logo: string
    // 上新提醒
    newTips: string
    //店铺状态
    status: ShopStatus
    //平台抽佣类型
    extractionType: ExtractionType
    //抽佣比率
    drawPercentage: number
    //定位 位置
    location: Geometry
    //头部背景图
    headBackground: string,
}
