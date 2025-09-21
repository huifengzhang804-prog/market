/**
 * @param vertex 矩阵顶点数组(顶点=规格值)
 * @param quantity 顶点个数
 * @param vertexRange 二维顶点数组变为一维占位对应关系的一维数组
 * @function init 初始化矩阵
 * @function setVertexRange 设置一维顶点数组中元素占位符
 * @function getVertexColumn 根据传入顶点获取顶点当前行占位数组
 * @function getColumnTotal 根据传入顶点组获取矩阵列所有顶点次数统计
 * @function getIntersection 获取交集
 * @function getUnion 获取并集
 */
export interface MatrixType {
  vertex: string[]
  quantity: number
  vertexRange: (number | undefined)[]
  init(): void
  setVertexRange(vertex: string, vertexGroup: string[]): void
  getVertexColumn(vertex: string): (number | undefined)[]
  getColumnTotal(vertexGroup: string[]): number[]
  getIntersection(vertexGroup: string[]): string[]
  getUnion(vertexGroup: string[]): string[]
}
export class Matrix implements MatrixType {
  vertex: string[] = []

  quantity = 0

  vertexRange: (undefined | number)[] = []

  constructor(vertex: string[]) {
    this.vertex = vertex
    this.quantity = vertex.length
    this.init()
  }

  init() {
    this.vertexRange = Array.from({
      length: this.quantity * this.quantity,
    })
  }
  /**
   * 设置矩阵一维对应占位符
   * @param {string} vertex 当前规格 红色
   * @param {string} vertexGroup 当前规格组 [红色,L]
   */
  setVertexRange(vertex: string, vertexGroup: string[]) {
    // 当前列数
    const pIndex = this.vertex.indexOf(vertex)
    vertexGroup.forEach((item) => {
      const index = this.vertex.indexOf(item)
      this.vertexRange[pIndex * this.quantity + index] = 1
    })
  }

  getVertexColumn(vertex: string) {
    const index = this.vertex.indexOf(vertex)
    const col: (number | undefined)[] = []
    this.vertex.forEach((item, pIndex) => {
      col.push(this.vertexRange[index + this.quantity * pIndex])
    })
    return col
  }
  /**
   * 获取column计数数组
   * @param {Array} specs 规格组
   */
  getColumnTotal(vertexGroup: string[]) {
    // 根据顶点获取矩阵二维占位数组
    const vertexColumn = vertexGroup.map((spec) => this.getVertexColumn(spec))
    const vertexColumnNames: number[] = []
    this.vertex.forEach((item, index) => {
      const columnTotal = vertexColumn
        .map((value) => value[index])
        .reduce((total: number, current) => {
          total += current || 0
          return total
        }, 0)
      vertexColumnNames.push(columnTotal)
    })
    return vertexColumnNames
  }
  getIntersection(vertexGroup: string[]) {
    const columnArr = this.getColumnTotal(vertexGroup)
    return columnArr.map((item, index) => item >= vertexGroup.length && this.vertex[index]).filter(Boolean) as string[]
  }
  getUnion(vertexGroup: string[]) {
    const columnArr = this.getColumnTotal(vertexGroup)
    return columnArr.map((item, index) => item && this.vertex[index]).filter(Boolean) as string[]
  }
}
