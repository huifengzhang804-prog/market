/**
 * 订单待支付倒计时
 * @param {string} createTime 创建时间
 * @param {string} payTimeOut 倒计时长
 */
import DateUtils from '@/utils/date'
const dateTool = new DateUtils()
export function useCountdownTime(startTime: string, timeOut: number | string) {
  if (!timeOut) return 0
  const createDate = dateTool.getTime(startTime)
  const timeDifference = createDate + Number(timeOut) * 1000
  return dateTool.getYMDHMSs(new Date(timeDifference))
}
