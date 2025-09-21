import { defineStore } from 'pinia'
import { orderListState } from './state'
import storage from '@/utils/Storage'
import type { ApiOrder } from '@/views/order/types/order'
export const useDeliveryOrderList = defineStore('deliveryOrderList', {
    state: () => orderListState,
    actions: {
        SET_ORDER_LIST(data: ApiOrder[]) {
            new storage().setItem('deliveryOrderList', data, 60 * 60 * 24)
            this.orderList = data
        },
    },
})
