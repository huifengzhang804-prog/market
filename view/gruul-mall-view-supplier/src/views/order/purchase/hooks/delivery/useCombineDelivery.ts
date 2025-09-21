import { ApiPurchaseOrder, OrderItems } from '../../components/split-table/order'
import delivery from '../../components/delivery.vue'

const useCombineDelivery = () => {
    const showDeliveryDialog = ref(false)
    const deliveryRef = ref<InstanceType<typeof delivery> | null>(null)
    const deliveryProps = reactive({
        currentNo: '',
        listOrderItems: [] as OrderItems[],
        receiver: void 0 as undefined | ApiPurchaseOrder['extra']['receiver'],
        createTime: '',
    })
    const setDeliveryProps = (data: {
        currentNo: string
        listOrderItems: OrderItems[]
        receiver?: ApiPurchaseOrder['extra']['receiver']
        createTime: string
    }) => {
        deliveryProps.currentNo = data.currentNo
        deliveryProps.listOrderItems = data.listOrderItems
            .filter((item) => item.packageStatus === 'WAITING_FOR_DELIVER')
            ?.map((item) => ({ ...item, deliveryNum: item?.num }))
        deliveryProps.receiver = data.receiver
        deliveryProps.createTime = data.createTime
        showDeliveryDialog.value = true
    }
    return {
        showDeliveryDialog,
        deliveryProps,
        setDeliveryProps,
        deliveryRef,
    }
}

export default useCombineDelivery
