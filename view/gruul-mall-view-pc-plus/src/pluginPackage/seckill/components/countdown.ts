/**
 * @description: 59 分 59 秒 用于计算 秒杀结束时间
 */
export const FIXED_POINT_TIME = 60 * 60 * 1000 - 1000
/**
 * @description: 时间格式化工具（倒计时转化）
 * @param {number} data
 * @returns {*}
 */
export function toHHmmss(data: number) {
  let time
  let hours = parseInt(((data % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toString())
  let minutes = parseInt(((data % (1000 * 60 * 60)) / (1000 * 60)).toString())
  let seconds = parseInt(((data % (1000 * 60)) / 1000).toString())
  time = (hours < 10 ? '0' + hours : hours) + ':' + (minutes < 10 ? '0' + minutes : minutes) + ':' + (seconds < 10 ? '0' + seconds : seconds)
  return time
}
