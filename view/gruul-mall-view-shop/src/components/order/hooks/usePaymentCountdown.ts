import useSetInterval from './useSetInterval'

const usePaymentCountdown = (createTime: string, payTimeout?: string) => {
    const timeTable = reactive({
        day: '00',
        hours: '00',
        minutes: '00',
        seconds: '00',
    })
    if (!payTimeout) {
        return {
            ...toRefs(timeTable),
        }
    }

    const createTimeStamp = new Date(createTime.replaceAll('-', '/')).getTime()
    const expireTimeStamp = createTimeStamp + parseInt(payTimeout) * 1000
    const countDownTimeStamp = expireTimeStamp - new Date().getTime()
    const { day, hours, minutes, seconds } = getRemainTime(countDownTimeStamp)
    timeTable.day = day
    timeTable.hours = hours
    timeTable.minutes = minutes
    timeTable.seconds = seconds
    return {
        ...toRefs(timeTable),
    }
}
/**
 * 获取剩余的时间
 * @param countDownTimeStamp 剩余的倒计时时间戳
 */
const getRemainTime = (countDownTimeStamp: number) => {
    const dayTimeStamp = 60 * 24 * 60 * 1000
    const hoursTimeStamp = 60 * 60 * 1000
    const minutesTimeStamp = 60 * 1000
    let day = 0,
        hours = 0,
        minutes = 0,
        seconds = 0
    while (countDownTimeStamp > dayTimeStamp) {
        countDownTimeStamp -= dayTimeStamp
        day++
    }
    while (countDownTimeStamp > hoursTimeStamp) {
        countDownTimeStamp -= hoursTimeStamp
        hours++
    }
    while (countDownTimeStamp > minutesTimeStamp) {
        countDownTimeStamp -= minutesTimeStamp
        minutes++
    }
    while (countDownTimeStamp > 1000) {
        countDownTimeStamp -= 1000
        seconds++
    }
    return {
        day: String(day).padStart(2, '0'),
        hours: String(hours).padStart(2, '0'),
        minutes: String(minutes).padStart(2, '0'),
        seconds: String(seconds).padStart(2, '0'),
    }
}

export default usePaymentCountdown
