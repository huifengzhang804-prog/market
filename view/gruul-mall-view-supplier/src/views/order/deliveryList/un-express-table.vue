<script setup lang="ts">
import { ref, PropType, computed, defineExpose } from 'vue'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import type { ApiLogisticCompany } from '@/views/freight/components/types'
import { ApiOrder, OrderReceiver } from '@/views/order/types/order'

const $emit = defineEmits(['filterOrderList'])
const $props = defineProps({
    tabData: { type: Array as PropType<any[]>, default: () => [] },
    companySelectList: { type: Array as PropType<ApiLogisticCompany[]>, default: () => [] },
})
const { divTenThousand } = useConvert()
/**
 * @description:默认展示第一个商品的单价
 * @param {*} prc
 * @returns {*}
 */
const unitPrice = computed(() => (row) => divTenThousand(row.shopOrders[0].shopOrderItems[0].dealPrice))

/**
 * @description: 商品总数量展示
 * @param {*} computed
 * @param {*} order
 * @returns {*}
 */
const num = computed(() => (row) => row.shopOrders[0].shopOrderItems.reduce((pre, order) => order.num + pre, 0))

const getOrderReceiver = (order: ApiOrder): OrderReceiver => {
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
}
</script>

<template>
    <q-table :data="$props.tabData">
        <template #header="{ row }">
            <div style="margin-right: 36px">订单号:{{ row.no }}</div>
            <div style="margin-right: 36px">创建时间:{{ row.createTime }}</div>
            <el-row style="flex: 1" justify="end">
                <el-button type="primary" link size="small" @click="$emit('filterOrderList', row)">移除</el-button>
            </el-row>
        </template>
        <q-table-column label="商品">
            <template #default="{ row }">
                <template v-if="row.shopOrders">
                    <div class="orderIndex-table__img-box">
                        <el-image
                            v-for="shopOrderItem in row.shopOrders[0].shopOrderItems.slice(0, 2)"
                            :key="shopOrderItem.id"
                            fits="cover"
                            style="width: 63px; height: 63px"
                            shape="square"
                            size="large"
                            :src="shopOrderItem.image"
                            :title="shopOrderItem.productName"
                        />
                        <div class="orderIndex-table__img-mask">
                            <span>￥{{ unitPrice(row) }}</span>
                            <span style="color: #838383; font-size: 10px">共{{ num(row) }}件</span>
                        </div>
                    </div>
                </template>
            </template>
        </q-table-column>
        <q-table-column label="客户">
            <template #default="{ row }">
                <div class="avatar_text avatar_text__bottom money_text">
                    <span style="color: #2e99f3; margin-right: 10px">买家昵称 : {{ row.buyerNickname }}</span>
                    <div class="money_text">(收货人：{{ getOrderReceiver(row).name }},{{ getOrderReceiver(row).mobile }})</div>
                </div>
            </template>
        </q-table-column>
    </q-table>
</template>

<style scoped lang="scss">
@include b(orderIndex-table) {
    @include e(img-box) {
        width: 200px;
        display: flex;
        justify-content: space-between;
    }
    @include e(img) {
        flex-shrink: 0;
        border-radius: 5px;
        position: relative;
    }
    @include e(img-mask) {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        font-size: 12px;
        color: #000000;
    }
}
@include b(is-complete) {
    background: #eef1f6;
}
@include b(header-table) {
    width: 100%;
    @include flex(space-between);
}
@include b(money_text) {
    font-size: 12px;
    color: #000000;
}
</style>
