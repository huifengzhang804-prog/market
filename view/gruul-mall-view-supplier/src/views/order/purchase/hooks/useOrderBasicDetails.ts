import Decimal from 'decimal.js'
import { ElMessage } from 'element-plus'
import useClipboard from 'vue-clipboard3'
import { AddressFn } from '@/components/q-address'
import { ApiPurchaseOrder } from '../components/split-table/order'
import useCombineDelivery from './delivery/useCombineDelivery'
import { useGDRegionDataStore } from '@/store/modules/GDRegionData'

const regionData = useGDRegionDataStore().getterGDRegionData
const stepMap = {
    待支付: 1,
    待审核: 2,
    待发货: 2,
    部分发货: 2,
    待入库: 3,
    已完成: 4,
    已关闭: 1,
}

const payTypeMap = {
    OFFLINE: '线下支付',
    BALANCE: '余额支付',
}
const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const useOrderBasicDetails = (orderDetail: ApiPurchaseOrder, initOrderList?: any) => {
    const { showDeliveryDialog, deliveryProps, setDeliveryProps, deliveryRef } = useCombineDelivery()
    const orderDetails = ref<ApiPurchaseOrder>()
    orderDetails.value = orderDetail
    const stepInfo = reactive({
        statusText: '',
        activeStep: 0,
    })
    const orderStatusText = (stepInfo.statusText = filterMainOrderStatus(orderDetail))
    stepInfo.activeStep = stepMap[orderStatusText]
    const copyOrderNo = async (orderNo: string) => {
        try {
            await toClipboard(orderNo)
            ElMessage.success('复制成功')
        } catch (e) {
            ElMessage.error('复制失败')
        }
    }
    const handleCopyReceiver = async () => {
        const str = `
            收货人姓名：${orderDetails?.value?.extra?.receiver?.name}\n
            联系人电话：${orderDetails?.value?.extra?.receiver?.mobile}\n
            收货地址：${orderDetails?.value?.extra?.receiver?.area?.join('')} ${orderDetails?.value?.extra?.receiver?.address}\n
            采购备注：${orderDetails?.value?.extra?.remark}
        `
        try {
            await toClipboard(str)
            ElMessage.success('复制成功')
        } catch (e) {
            ElMessage.error('复制失败')
        }
    }
    const handleConfirmDelivery = async () => {
        const result = await deliveryRef.value?.handleeSubmit()
        if (result) {
            showDeliveryDialog.value = false
            initOrderList?.()
        }
    }
    const handleDelivery = () => {
        setDeliveryProps({
            currentNo: orderDetails?.value?.no || '',
            listOrderItems: orderDetails?.value?.orderItems || [],
            receiver: orderDetails?.value?.extra?.receiver,
            createTime: orderDetails?.value?.createTime || '',
        })
    }
    const computedCalculateFreight = computed(() => (orderItems?: ApiPurchaseOrder['orderItems']) => calculateFreight(orderItems))
    const computedCalculateCommodityPrice = computed(() => (orderItems?: ApiPurchaseOrder['orderItems']) => calculateCommodityPrice(orderItems))
    return {
        orderDetails,
        stepInfo,
        payTypeMap,
        divTenThousand,
        computedCalculateFreight,
        computedCalculateCommodityPrice,
        copyOrderNo,
        handleCopyReceiver,
        showDeliveryDialog,
        deliveryProps,
        setDeliveryProps,
        deliveryRef,
        handleConfirmDelivery,
        handleDelivery,
    }
}

export default useOrderBasicDetails

export const filterMainOrderStatus = (orderInfo?: ApiPurchaseOrder) => {
    if (!orderInfo) return ''
    if (orderInfo.status === 'UNPAID') return '待支付'
    if (orderInfo.status === 'PAYMENT_AUDIT') return '待审核'
    const paidOrderStatusStack = ['WAITING_FOR_DELIVER', 'WAITING_FOR_RECEIVE', 'COMPLETED']
    const paidOrderStatusMap = {
        WAITING_FOR_DELIVER: '待发货',
        WAITING_FOR_RECEIVE: '待入库',
        COMPLETED: '已完成',
    }
    if (orderInfo.status === 'PAID') {
        let statusText = 'COMPLETED'
        for (const item of orderInfo.orderItems) {
            const currentIndex = paidOrderStatusStack.findIndex((stat) => stat === statusText)
            if (item.packageStatus === 'WAITING_FOR_DELIVER' && currentIndex > 0) {
                statusText = 'WAITING_FOR_DELIVER'
                continue
            }
            if (item.packageStatus === 'WAITING_FOR_RECEIVE' && currentIndex > 1) {
                statusText = 'WAITING_FOR_RECEIVE'
                continue
            }
        }
        let returnStatus = paidOrderStatusMap[statusText]
        if (returnStatus === '待发货') {
            const deliveryedItem = orderInfo.orderItems.find((item) => item.packageStatus !== 'WAITING_FOR_DELIVER')
            if (deliveryedItem) {
                returnStatus = '部分发货'
            }
        }
        return returnStatus
    }
    return '已关闭'
}

/**
 * @description 计算订单运费
 * @param orderItems 订单项
 * @returns
 */
const calculateFreight = (orderItems: ApiPurchaseOrder['orderItems'] = []) => {
    return orderItems.reduce((pre, item) => {
        return pre.plus(new Decimal(divTenThousand(item.freightPrice)))
    }, new Decimal(0))
}

const calculateCommodityPrice = (orderItems: ApiPurchaseOrder['orderItems'] = []) => {
    return orderItems.reduce((pre, item) => {
        return pre.plus(new Decimal(divTenThousand(item.salePrice).mul(new Decimal(item.num))))
    }, new Decimal(0))
}
