import storage from '@/utils/Storage'
export const orderListState = {
    orderList: new storage().getItem('deliveryOrderList') || [],
}
