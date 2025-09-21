import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'
import { ApiOrder } from '../types/order'
import { AFSSTATUS } from '@/views/afs/types'
const { toClipboard } = useClipboard()

const useOrderHeader = () => {
    const copyOrderNo = async (orderNo: string) => {
        try {
            await toClipboard(orderNo)
            ElMessage.success('复制成功')
        } catch (e) {
            ElMessage.error('复制失败')
        }
    }
    const getPaymentType = (row: ApiOrder) => {
        const payMap = { ALIPAY: '支付宝支付', BALANCE: '余额支付', WECHAT: '微信支付' }
        const orderPaymentType = row?.orderPayment?.type
        return payMap[orderPaymentType] || ''
    }
    /**
     * @description 获取当前订单是否处于售后状态
     * @param row 订单数据
     * @returns { boolean } 是否处于售后状态
     */
    const getOrderAfterSaleStatus = (row: ApiOrder) => {
        const shopOrderItems = row.shopOrders?.[0]?.shopOrderItems || []
        for (const shopOrderItem of shopOrderItems) {
            const { ing } = afsStatusHandler[shopOrderItem.afsStatus]
            if (ing) return true
        }
        return false
    }
    /**
     * @description 获取配送方式
     * @param row 当前订单信息
     * @returns 配送方式
     */
    const getDeliveryMode = (row: ApiOrder) => {
        const distributionModeMap = {
            EXPRESS: '快递配送',
            INTRA_CITY_DISTRIBUTION: '同城配送',
            SHOP_STORE: '门店自提',
            VIRTUAL: '无需物流',
            MERCHANT: '快递配送',
        }
        const distributionMode = row?.extra?.distributionMode
        return distributionModeMap[distributionMode] || ''
    }
    return {
        copyOrderNo,
        getPaymentType,
        getOrderAfterSaleStatus,
        getDeliveryMode,
    }
}

export default useOrderHeader

export const afsStatusHandler: Record<keyof typeof AFSSTATUS, { closed: boolean; ing: boolean; finished: boolean }> = {
    NONE: { closed: true, ing: false, finished: false },
    REFUND_REQUEST: { closed: false, ing: true, finished: false },
    SYSTEM_REFUND_AGREE: { closed: false, ing: true, finished: false },
    REFUND_AGREE: { closed: false, ing: true, finished: false },
    REFUND_REJECT: { closed: true, ing: false, finished: false },
    REFUNDED: { closed: false, ing: false, finished: true },
    RETURN_REFUND_REQUEST: { closed: false, ing: true, finished: false },
    RETURN_REFUND_AGREE: { closed: false, ing: true, finished: false },
    SYSTEM_RETURN_REFUND_AGREE: { closed: false, ing: true, finished: false },
    RETURN_REFUND_REJECT: { closed: true, ing: false, finished: false },
    RETURNED_REFUND: { closed: false, ing: true, finished: false },
    SYSTEM_RETURNED_REFUND_CONFIRM: { closed: false, ing: true, finished: false },
    RETURNED_REFUND_CONFIRM: { closed: false, ing: true, finished: false },
    RETURNED_REFUND_REJECT: { closed: true, ing: false, finished: false },
    RETURNED_REFUNDED: { closed: false, ing: false, finished: true },
    SYSTEM_CLOSED: { closed: true, ing: false, finished: false },
    BUYER_CLOSED: { closed: true, ing: false, finished: false },
}
