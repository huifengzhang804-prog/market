import { Matrix } from './createMatrix'

/**
 * 创建商品详情sku
 * @param commoditySpecs 商品规格数组
 * @param normGroup 商品规格组合数组
 * @function initMatrix 初始化规格矩阵
 * @function initSimilarVertex 创建可选点位
 * @function querySpecsOptions 筛选可选择规格
 * @function applyCommodity 根据商品规格点位添加
 */
interface GoodNormType {
  commoditySpecs: commoditySpecsType[]
  normGroup: normGroupType[]
  initMatrix(): void
  initSimilarVertex(): void
  querySpecsOptions(params: string[]): string[]
  applyCommodity(params: string[]): void
}
type commoditySpecsType = {
  title: string
  list: string[]
}
type normGroupType = {
  id: Long
  specs: string[]
}
export default class GoodNorm extends Matrix implements GoodNormType {
  commoditySpecs: commoditySpecsType[] = []
  normGroup: normGroupType[] = []
  constructor(commoditySpecs: commoditySpecsType[], normGroup: normGroupType[]) {
    super(commoditySpecs.reduce((total: string[], current) => [...total, ...current.list], []))
    this.commoditySpecs = commoditySpecs
    this.normGroup = normGroup
    // 单规格矩阵创建
    this.initMatrix()
    // 同类顶点创建
    this.initSimilarVertex()
  }
  initMatrix() {
    this.normGroup.forEach((item) => {
      this.applyCommodity(item.specs)
    })
  }
  initSimilarVertex() {
    // 获得所有可选项
    const specsOption = this.getUnion(this.vertex)
    this.commoditySpecs.forEach((item) => {
      const params: string[] = []
      item.list.forEach((value) => {
        if (specsOption.indexOf(value) > -1) params.push(value)
      })
      // 同级点位创建
      this.applyCommodity(params)
    })
  }
  querySpecsOptions(params: string[]): string[] {
    // 判断是否存在选项填一个
    if (params?.some(Boolean)) {
      params = this.getIntersection(params.filter(Boolean))
    } else {
      params = this.getUnion(this.vertex)
    }
    return params
  }
  applyCommodity(params: string[]) {
    params.forEach((param) => {
      this.setVertexRange(param, params)
    })
  }
}
