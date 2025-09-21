import DateUtil from '@/utils/date'
import Decimal from 'decimal.js'
const dateUtil = new DateUtil()
// 开始时间日期限制
export const disabledStartDate = (time: Date) => {
    const YMDs = dateUtil.getYMDs(time)
    const currentYMDs = dateUtil.getYMDs(new Date())
    return (
        new Decimal(new Date(YMDs).getTime()).lessThan(new Date(currentYMDs).getTime()) ||
        new Decimal(new Date(time).getTime()).greaterThanOrEqualTo(new Date(diffMonth(6)).getTime())
    )
}

const makeRange = (start: number, end: number) => {
    const result: number[] = []
    for (let i = start; i <= end; i++) {
        result.push(i)
    }
    return result
}
// 前 / 后 一个月 根据传入正负值
export function diffMonth(n: number) {
    let dt = new Date()
    dt.setMonth(dt.getMonth() + Number(n))
    return dt.toLocaleString().replace(/\//g, '-')
}

export const disabledEndDate = (time: Date) => {
    const YMDs = dateUtil.getYMDs(time)
    const currentYMDs = dateUtil.getYMDs(new Date())
    return (
        new Decimal(new Date(YMDs).getTime()).lessThan(new Date(currentYMDs).getTime()) ||
        new Decimal(new Date(time).getTime()).greaterThanOrEqualTo(new Date(diffMonth(6)).getTime())
    )
}
