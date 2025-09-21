export interface ClassListType {
  guidePrice: number
  realPrice: number
  stock: number
  weight: number
  skuUrl: string
  limitPrice: number
  nameArr: string[]
}

/**
 * @description: 商品规格结构
 * @param id 规格id
 * @param name 规格名称
 * @param order 排序方式
 * @param parentId 父规格id
 * @param updateTime 更新时间
 * @param version 版本号
 */
export interface NormType {
  id?: number
  name: string
  order?: number
  parentId?: number
  updateTime?: string
  children: string[]
  version: string
  inputValue: string
  inputVisble: boolean
}
