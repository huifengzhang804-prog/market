import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'
import { ApiOrder } from '@/views/order/types/order'

export interface SpanMethodProps {
    row: ApiOrder
    column: TableColumnCtx<ApiOrder>
    rowIndex: number
    columnIndex: number
}
let spanArr: Array<number> = [],
    pos = 0
/**
 * @description: 初始化标记
 * @param {any} data 后端请求的数据
 * @param {any} stata 指定字段名
 * @returns {*}
 */
export const initSpanTag = (data: any[], stata: string) => {
    for (let i = 0; i < data.length; i++) {
        if (i === 0) {
            // 如果是第一条记录（即索引是0的时候），向数组中加入1
            spanArr.push(1)
            pos = 0
        } else if (data[i][stata] === data[i - 1][stata]) {
            // 如果stata相等就累加，并且push 0
            spanArr[pos] += 1
            spanArr.push(0)
        } else {
            // 不相等push 1
            spanArr.push(1)
            pos = i
        }
    }
}
/**
 * @description: 指定第几个单元格合并
 * @param {SpanMethodProps} param1
 * @returns {*}
 */
export const useMergeCells = ({ row, column, rowIndex, columnIndex }: SpanMethodProps) => {
    if (columnIndex === 0 || columnIndex === 3 || columnIndex === 4) {
        const _row = spanArr[rowIndex]
        const _col = _row > 0 ? 1 : 0
        return {
            rowspan: _row,
            colspan: _col,
        }
    }
}
