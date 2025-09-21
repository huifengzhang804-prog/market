export { IntegralOrderStatusCn }
/**
 * 订单状态
 * @param UNPAID: '未支付',
 * @param PAID: '已支付',
 * @param ON_DELIVERY: '发货中',
 * @param ACCOMPLISH: '已完成',
 * @param SYSTEM_CLOSED: '系统关闭',
 */
const IntegralOrderStatusCn = {
  UNPAID: '未支付',
  PAID: '待发货',
  ON_DELIVERY: '已发货',
  ACCOMPLISH: '已完成',
  SYSTEM_CLOSED: '已关闭',
} as const
