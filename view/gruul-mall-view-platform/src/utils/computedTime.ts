export default function computedTime(seconds: number) {
    let days = Math.floor(seconds / (24 * 3600))
    seconds %= 24 * 3600
    let hours = Math.floor(seconds / 3600)
    seconds %= 3600
    let minutes = Math.floor(seconds / 60)
    seconds %= 60
    let result = ''
    if (days > 0) {
        result += days + '天'
    }
    if (hours > 0) {
        result += hours + '小时'
    }
    if (minutes > 0) {
        result += minutes + '分'
    }
    if (seconds > 0 || result === '') {
        result += seconds + '秒'
    }
    return result
}
