import { defineStore } from 'pinia'
import storage from '@/utils/Storage'
import { ApiPurchaseOrder } from '@/views/order/purchase/components/split-table/order'
const useDeliveryOrderList = defineStore('deliveryOrderList', {
    state: () => ({
        orderList: new storage().getItem('purchaseDeliveryOrderList') || [],
    }),
    actions: {
        SET_ORDER_LIST(data: ApiPurchaseOrder[]) {
            new storage().setItem('purchaseDeliveryOrderList', data, 60 * 60 * 24)
            this.orderList = data
        },
    },
})

export default useDeliveryOrderList
