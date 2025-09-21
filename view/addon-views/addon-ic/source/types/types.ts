import { Long } from '@/apis/good'
import { Obj, PageParams } from '@/utils/types'

export interface IntraCityDistributionConfig {
  deliveryRange?: number // 配送范围 在这个范围内 才可以同城配送
  description?: string // 配送说明

  enableSelf?: boolean // 是否启用商家自配
  enableOpen?: boolean // 是否启用第三方开放平台配送 目前只有 UU 跑腿
  defaultType?: keyof typeof DeliveryMethod // 默认的配送方 当enableSelf 和 enableOpen都为 true时不为空
  warmBox?: boolean // 是否需要保温箱
  minLimit?: number // 起送金额
  baseDelivery?: number // 基础配送费
  billMethod?: BillMethod // 计费方式 按重量或距离计算配送费 在基础配送费的基础上额外增加配送费
  freeLimit?: number // 免配送费限制，订单支付金额大于这个值时免配送费
  location: IntraCityDistributionLoacation // 定位
  address: string // 详细地址
  icStatus: boolean // 是否开通了同城配送
}
const enum DeliveryMethod {
  // 商家自配
  SELF = 'SELF',
  // uupt 配送
  UUPT = 'UUPT',
}
export const enum BillMethodType {
  // 按距离
  DISTANCE = 'DISTANCE',
  // 按重量
  WEIGHT = 'WEIGHT',
}
interface BillMethod {
  type: keyof typeof BillMethodType // 计费方式
  /**
   * 免配送费额度
   * 以距离计算 单位公里（km）
   * 以重量计算 单位公斤（kg）
   */
  free: number
  /**
   * 步长 超出免费额度 每 step 增加配送费 stepPrice
   * 以距离计算 单位公里（km）
   * 以重量计算 单位公斤（kg）
   */
  step: number
  stepPrice: number // 步长对应的配送费
}

export interface IntraCityDistributionLoacation {
  coordinates: Coordinate | Coordinate[] // 经纬度|海拔
  type: keyof typeof GeometryType // 几何类型
  envelope?: {
    minx: number
    maxx: number
    miny: number
    maxy: number
  }
  SRID?: number // 坐标系
  userData?: Obj // 自定义数据
  factory?: {
    SRID: number
    coordinateSequenceFactory: Obj
    precisionModel: {
      scale: number // 精度
      modelType: {
        name: string
      }
    }
  }
}

/**
 * 几何类型
 */
export enum GeometryType {
  //点
  Point = 'Point',
  //多边形
  Polygon = 'Polygon',
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
export interface Geometry<T extends keyof typeof GeometryType = GeometryType.Point> {
  //类型
  type: T
  //坐标 Point=》Coordinate ｜ Polygon=》Coordinate[]
  coordinates: Coordinates[T]
}

type Coordinates = {
  Point: Coordinate
  Polygon: Coordinate[]
}
