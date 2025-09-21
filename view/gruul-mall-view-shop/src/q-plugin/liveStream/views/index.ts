export const liveIndexStatus = {
    LIVE_BROADCAST: '进行中',
    NOT_STARTED: '未开始',
    CLOSED: '已结束',
}
export const role = {
    LIVE_ADMIN: '管理员',
    LIVE_ANCHOR: '主播',
    LIVE_OPERATOR: '运营者',
}
/**
 * 格式化时间秒
 * @param {string} time
 */
export const formatTime_S = (time: number) => {
    if (!time) {
        return ''
    }
    //将时间戳格式转换成年月日时分秒
    let date = new Date(time)
    let Y = date.getFullYear() + '-'
    let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-'
    let D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' '

    let h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':'
    let m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':'
    let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
    let strDate = Y + M + D + h + m + s
    return strDate
}
