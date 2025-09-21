/**
 * @description:
 * @param vertex 矩阵顶点数组
 * @param quantity 顶点个数
 * @param vertexRange 矩阵排列一维数组
 * @function init 初始化矩阵
 * @function setVertexRange 设置矩阵数组
 * @function getVertexColumn 根据传入顶点获取矩阵列数据
 * @function getColumnTotal 根据传入顶点获取矩阵列所有顶点次数统计
 * @function getIntersection 获取交集
 * @function getUnion 获取并集
 */
export interface MatrixType {
  vertex: string[]
  quantity: number
  vertexRange: (number | undefined)[]
  init(): void
  setVertexRange(id: string, side: string[]): void
  getVertexColumn(id: string): (number | undefined)[]
  getColumnTotal(params: string[]): number[]
  getIntersection(params: string[]): string[]
  getUnion(params: string[]): string[]
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

  setVertexRange(id: string, side: string[]) {
    const pIndex = this.vertex.indexOf(id)
    side.forEach((item) => {
      const index = this.vertex.indexOf(item)
      this.vertexRange[pIndex * this.quantity + index] = 1
    })
  }

  getVertexColumn(id: string) {
    const index = this.vertex.indexOf(id)
    const col: (number | undefined)[] = []
    this.vertex.forEach((item, pIndex) => {
      col.push(this.vertexRange[index + this.quantity * pIndex])
    })
    return col
  }

  getColumnTotal(params: Array<string>) {
    const vertexColumn = params.map((id) => this.getVertexColumn(id))
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

  getIntersection(params: string[]) {
    const row = this.getColumnTotal(params)
    return row.map((item, index) => item >= params.length && this.vertex[index]).filter(Boolean)
  }

  getUnion(params: string[]) {
    params = this.getColumnTotal(params)
    return params.map((item, index) => item && this.vertex[index]).filter(Boolean)
  }
}
