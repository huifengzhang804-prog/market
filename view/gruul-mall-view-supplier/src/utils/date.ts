type UnionDate = number | Date
export default class DateUtil {
    ms: Date

    constructor(ms = new Date()) {
        this.ms = ms
    }
    /**
     * @description: 获取年
     * @param {Date} ms
     */
    getY(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        return GMT.getFullYear()
    }
    /**
     * @description: 获取月
     * @param {Date} ms
     */
    getM(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const m = GMT.getMonth() + 1
        return this.formatLength(m)
    }
    /**
     * @description: 获取日
     * @param {Date} ms
     */
    getD(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const d = GMT.getDate()
        return this.formatLength(d)
    }
    /**
     * @description: 获取时
     * @param {Date} ms
     */
    getH(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const H = GMT.getHours()
        return this.formatLength(H)
    }
    /**
     * @description: 获取分
     * @param {Date} ms
     */
    getMin(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const M = GMT.getMinutes()
        return this.formatLength(M)
    }
    /**
     * @description: 获取秒
     * @param {Date} ms
     */
    getS(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const S = GMT.getSeconds()
        return this.formatLength(S)
    }
    /**
     * @description: 获取年/月/日
     * @param {Date} ms
     */
    getYMD(ms: Date | number = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const y = GMT.getFullYear()
        const m = GMT.getMonth() + 1
        const d = GMT.getDate()
        return [y, m, d].map(this.formatLength).join('/')
    }
    /**
     * @description: 获取年-月-日
     * @param {Date} ms
     */
    getYMDs(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const y = GMT.getFullYear()
        const m = GMT.getMonth() + 1
        const d = GMT.getDate()
        return [y, m, d].map(this.formatLength).join('-')
    }
    /**
     * @description: 获取年-月
     * @param {Date} ms
     */
    getYMs(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const y = GMT.getFullYear()
        const m = GMT.getMonth() + 1
        return [y, m].map(this.formatLength).join('-')
    }
    /**
     * @description: 获取月-日
     * @param {Date} ms
     */
    getMDs(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const m = GMT.getMonth() + 1
        const d = GMT.getDate()
        return [m, d].map(this.formatLength).join('-')
    }
    /**
     * @description: 获取时: 分: 秒
     * @param {Date} ms
     */
    getHMS(ms: UnionDate = this.ms) {
        const GMT = this.unitReturnDate(ms)
        const h = GMT.getHours()
        const m = GMT.getMinutes()
        const s = GMT.getSeconds()
        return [h, m, s].map(this.formatLength).join(':')
    }
    /**
     * @description: 获取年/月/日 时: 分: 秒
     * @param {Date} ms
     */
    getYMDHMS(ms: UnionDate = this.ms) {
        ms = this.unitReturnDate(ms)
        return this.getYMD(ms) + ' ' + this.getHMS(ms)
    }
    /**
     * @description: 获取年-月-日 时: 分: 秒
     * @param {Date} ms
     */
    getYMDHMSs(ms: UnionDate = this.ms) {
        return this.getYMDs(ms) + ' ' + this.getHMS(ms)
    }
    /**
     * @description: 获取上个月 格式年-月-日 时: 分: 秒
     * @param {Date} ms
     * @param {number} day 天数
     */

    getLastMonth(ms: UnionDate = this.ms, day = 30) {
        let GMT = this.getTime(this.unitReturnDate(ms))
        GMT = GMT - 3600 * 1000 * 24 * day
        // return this.getYMDs(GMT) + ' ' + this.getHMS(GMT)
        return this.getYMDs(GMT)
    }
    /**
     * @description: 获取上个季度 格式年-月-日 // 时: 分: 秒
     * @param {Date} ms
     * @param {number} day 天数
     */
    getLastThreeMonth(ms: Date = this.ms, day = 90) {
        let GMT = this.getTime(ms)
        GMT = GMT - 3600 * 1000 * 24 * day
        // return this.getYMDs(GMT) + ' ' + this.getHMS(GMT)
        return this.getYMDs(GMT)
    }
    /**
     * @description: 年月日加天数
     * @param {Date} ms
     * @param {number} day 天数
     */
    getAddDays(ms: Date | number = this.ms, day = 0) {
        let GMT = this.getTime(this.unitReturnDate(ms))
        GMT = GMT + day * 24 * 60 * 60 * 1000
        const Y = this.getY(GMT)
        const M = this.getM(GMT)
        const D = this.getD(GMT)
        return [Y, M, D].map(this.formatLength).join('-')
    }
    getSubtracteDays(ms: Date | number = this.ms, day = 0) {
        let GMT = this.getTime(this.unitReturnDate(ms))
        GMT = GMT - day * 24 * 60 * 60 * 1000
        const Y = this.getY(GMT)
        const M = this.getM(GMT)
        const D = this.getD(GMT)
        return [Y, M, D].map(this.formatLength).join('-')
    }
    /**
     * @description: 获取毫秒数
     * @param {Date} ms
     */
    getTime(ms: Date = this.ms) {
        return ms.getTime()
    }

    getObj(ms = this.ms) {
        const GMT = ms
        const Y = GMT.getFullYear()
        const M = GMT.getMonth() + 1
        const D = GMT.getDate()
        const h = GMT.getHours()
        const m = GMT.getMinutes()
        const s = GMT.getSeconds()
        return [Y, M, D, h, m, s].map(this.formatLength)
    }
    /**
     * @description: 格式化补零
     * @param {Date} ms
     */
    formatLength(ms: number | string) {
        return String(ms)[1] ? String(ms) : '0' + ms
    }
    /**
     * @description: 统一返回日期类型
     */
    unitReturnDate(ms: Date | number) {
        if (!(ms instanceof Date)) {
            return new Date(ms)
        }
        return ms
    }
    /**
     * @description: 闰年判断
     * @param {string | number} year
     */
    isLeapYear(year: string | number) {
        const d = new Date(Number(year), 1, 29)
        return d.getDate() === 29
    }
    /**
     * @description: 判断当月对应天数
     * @param {string} year
     * @param {string} month
     */
    MonthToDay(year: string | number, month: string | number) {
        const d = new Date(Number(year), Number(month), 1, 0, 0, 0)
        const lastDay = new Date(d.getTime() - 1000)
        return lastDay.getDate()
    }
}
