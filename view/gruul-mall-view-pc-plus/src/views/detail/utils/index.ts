import type { ServicesType } from '@/views/detail/types'
interface ServiceHandle {
  name: string
  sendTime: () => string
  isSendTimeService: boolean
}
export const serviceHandler: Record<ServicesType, ServiceHandle> = {
  ALL_ENSURE: {
    name: '正品保证',
    sendTime: () => '',
    isSendTimeService: false,
  },
  FAKE_COMPENSATE: {
    name: '假一赔十',
    sendTime: () => '',
    isSendTimeService: false,
  },
  NO_FREIGHT: {
    name: '全场包邮',
    sendTime: () => '',
    isSendTimeService: false,
  },
  SEVEN_END_BACK: {
    name: '7天无理由退货',
    sendTime: () => '',
    isSendTimeService: false,
  },
  FIFTEEN_DAY_SEND: {
    name: '15天内发货',
    sendTime: () => getEstimateDate(15),
    isSendTimeService: true,
  },
  SEVEN_DAY_SEND: {
    name: '7天内发货',
    sendTime: () => getEstimateDate(7),
    isSendTimeService: true,
  },
  THREE_DAY_SEND: {
    name: '3天内发货',
    sendTime: () => getEstimateDate(3),
    isSendTimeService: true,
  },
  TWO_DAY_SEND: {
    name: '2天内发货',
    sendTime: () => getEstimateDate(2),
    isSendTimeService: true,
  },
}
const getEstimateDate = (deliverDay: number) => {
  const dateUtil = new Date(new Date().getTime() + (deliverDay + 3) * 24 * 60 * 60 * 1000)
  return dateUtil.getMonth() + 1 + '月' + dateUtil.getDate() + '日'
}
