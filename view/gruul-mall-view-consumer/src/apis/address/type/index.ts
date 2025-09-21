export interface Address<T extends keyof typeof GeometryType = GeometryType.Point> {
  id?: Long
  name: string
  mobile: string
  location: Geometry<T>
  address: string // 详细地址 由三部分组成 xx0~xx1~xx2 xx0:详细街道地址(不包含省市区的) xx1:地点名称(通用叫法,如:'宁波启山科技有限公司') xx2:门牌号(如:2单元302室 保证xx2不包含~字符 xx0和xx1是高德地图给的数据 不存在~的可能性)
  isDefault: boolean
  area: [string, string, string?]
}

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

/**
 * 几何类型
 */
export const enum GeometryType {
  // 点
  Point = 'Point',
  // 多边形
  Polygon = 'Polygon',
}

/**
 * 单个坐标类型 （地图上可以理解为经纬度和和海拔）
 * 0: 经度 x
 * 1: 纬度 y
 * 2: 海拔 z
 */
export type Coordinate = [number, number, number?]

export type Checked = {
  longitude: Long
  latitude: Long
  address: string
  name: string
}
