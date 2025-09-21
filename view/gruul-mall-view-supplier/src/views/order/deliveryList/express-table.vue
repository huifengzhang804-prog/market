<script setup lang="ts">
import { ref, PropType, computed, toRaw, watch } from 'vue'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import type { ApiLogisticCompany } from '@/views/freight/components/types'
import type { ApiOrder } from '@/views/order/types/order'
import type { ExpressCompany } from '@/views/order/orderShipment/types'
import { OrderReceiver } from '@/views/order/types/order'
interface ApiOrderExpressCompany extends ApiOrder {
    expressCompany: ExpressCompany
}

const $props = defineProps({
    tableData: { type: Array as PropType<ApiOrderExpressCompany[]>, default: () => [] },
    modelValue: { type: Map as PropType<Map<string, ExpressCompany>>, default: () => new Map() },
    companySelectList: { type: Array as PropType<ApiLogisticCompany[]>, default: () => [] },
    logisticsCompanyCode: { type: String, default: '' },
})
const $emit = defineEmits(['filterOrderList', 'update:modelValue'])
const { divTenThousand } = useConvert()
const currentExpressCompanyMap = ref($props.modelValue || new Map())

watch(
    () => $props.modelValue.size,
    (val) => {
        currentExpressCompanyMap.value = $props.modelValue
    },
)
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

watch(
    () => $props.logisticsCompanyCode,
    (val) => {
        if (val) {
            for (const key in $props.tableData) {
                const orderNo = $props.tableData[key].no
                currentExpressCompanyMap.value.get(orderNo).logisticsCompanyCode = val
            }
        }
    },
)

const getOrderReceiver = (order: ApiOrder): OrderReceiver => {
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
}
const changeExpress = () => {
    console.log('currentExpressCompanyMap.value', currentExpressCompanyMap.value)
    $emit('update:modelValue', currentExpressCompanyMap.value)
}
</script>

<template>
    <q-table :data="$props.tableData" class="orderIndex-table">
        <template #header="{ row }">
            <div style="margin-right: 36px">订单号:{{ row.no }}</div>
            <div>创建时间:{{ row.createTime }}</div>
            <el-row style="flex: 1" justify="end">
                <el-button type="primary" link size="small" @click="$emit('filterOrderList', row)">移除</el-button>
            </el-row>
        </template>
        <q-table-column prop="name" label="商品">
            <template #default="{ row }">
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
        </q-table-column>
        <q-table-column prop="age" label="客户">
            <template #default="{ row }">
                <div class="avatar_text avatar_text__bottom money_text">
                    <span style="color: #2e99f3; margin-right: 10px">买家昵称 : {{ row.buyerNickname }}</span>
                    <div style="padding: 0 10px 0" class="money_text">
                        (收货人：{{ getOrderReceiver(row).name }},{{ getOrderReceiver(row).mobile }})
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column prop="sex" label="操作" align="right">
            <template #default="{ row }">
                <el-row>
                    <el-form-item label="物流服务" style="width: 230px">
                        <el-select
                            v-model="currentExpressCompanyMap.get(row.no)!.logisticsCompanyCode"
                            placeholder="请选择物流服务"
                            @change="changeExpress"
                        >
                            <el-option
                                v-for="item in $props.companySelectList"
                                :key="item.logisticsCompanyName"
                                :label="item.logisticsCompanyName"
                                :value="item.logisticsCompanyCode"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="运单号码" style="width: 230px">
                        <el-input
                            v-model="currentExpressCompanyMap.get(row.no)!.expressNo"
                            style="height: 28px"
                            maxlength="40"
                            @input="changeExpress"
                        />
                    </el-form-item>
                </el-row>
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
