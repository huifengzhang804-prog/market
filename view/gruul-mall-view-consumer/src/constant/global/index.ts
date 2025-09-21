//
/**
 * 大数字 类型 当后端返回的数字过大时 会以字符串格式返回 否则返回数字
 * todo 应该作为全局类型使用 会有比较多的地方会用到 比如 价格 各种 id等
 */
export type Long = number | string

/**
 * 可以为空的数据类型
 */
export type Nullable<T> = null | T | undefined

/**
 * 范围时间
 */
export interface RageTime<T> {
  //开始时间
  start: T
  //结束时间
  end: T
}

/**
 * 店铺运营模式
 */
export enum ShopMode {
  /**
   * 普通店铺
   */
  COMMON = 'COMMON',

  /**
   * 供应商
   */
  SUPPLIER = 'SUPPLIER',

  /**
   * O2O店铺
   */
  O2O = 'O2O',
}
