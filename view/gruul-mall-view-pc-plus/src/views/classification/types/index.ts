export enum LEVELTYPE {
  LEVEL_1,
  LEVEL_2,
  LEVEL_3,
}
/**
 * @description: 排序规则
 * @param salePrice 价格排序
 * @param createTime 新品排序
 * @param salesVolume 销量排序
 * @param comprehensive 综合排序 desc降序 asc升序
 */
enum ENUMSORT {
  salePrice_desc,
  salePrice_asc,
  createTime_desc,
  createTime_asc,
  salesVolume_desc,
  salesVolume_asc,
  comprehensive_desc,
  comprehensive_asc,
}
export type SortType = keyof typeof ENUMSORT
export interface ClassItemType {
  id: string
  level: keyof typeof LEVELTYPE
  name: string
  parentId: string
}
export interface RetrieveComItemType {
  categoryFirstId: string
  categorySecondId: string
  categoryThirdId: string
  id: string
  productId: string
  productName: string
  pic: string
  platformCategoryFirstId: string
  platformCategorySecondId: string
  platformCategoryThirdId: string
  salePrices: string[]
  prices: string[]
  salesVolume: string
  shopId: string
  shopName: string
  sort: SortType
  couponList?: any[]
  memberInfo?: any
}
