<script setup lang="ts">
import type { PropType } from 'vue'
import type { ApiLogisticCompany } from '@/views/platformDelivery/pages/logistics-service/types'
import type { ApiOrder, ShopOrderItem } from '@/views/order/types/order'
import type { ExpressCompany } from '@/views/order/orderShipment/types'
import { OrderReceiver } from '@/views/order/types/order'
import SplitTable from '@/components/order/order-split-table/SplitTable'
import SplitTableColumn from '@/components/order/order-split-table/split-table-column.vue'

interface ApiOrderExpressCompany extends ApiOrder {
    expressCompany: ExpressCompany
}

const $props = defineProps({
    tableData: { type: Array as PropType<ApiOrderExpressCompany[]>, default: () => [] },
    companySelectList: { type: Array as PropType<ApiLogisticCompany[]>, default: () => [] },
})
const $emit = defineEmits(['filterOrderList', 'update:tableData'])
const tableData = computed(() => {
    return JSON.parse(JSON.stringify($props.tableData))
})

const { divTenThousand } = useConvert()
/**
 * 默认展示第一个商品的单价
 */
const unitPrice = computed(() => (shopOrderItems: ShopOrderItem[]) => divTenThousand(shopOrderItems[0].dealPrice))

/**
 * 商品总数量展示
 */
const num = computed(() => (shopOrderItems: ShopOrderItem[]) => shopOrderItems.reduce((pre, item) => item.num + pre, 0))
const getOrderReceiver = (order: ApiOrder): OrderReceiver => {
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
}
const handleSpecs = (shopOrderItem: ShopOrderItem) => {
    if (shopOrderItem.specs) {
        return shopOrderItem.specs.join('、').replace(':-', '-').replace(':', '+')
    } else {
        return ''
    }
}
</script>

<template>
    <SplitTable :data="tableData" class="orderIndex-table">
        <template #header="{ row }">
            <div class="fcenter f1">
                <div>订单号:{{ row.no }}</div>
                <div style="margin-left: 5px; margin-right: 5px">|</div>
                <div>下单时间:{{ row.createTime }}</div>
                <div style="margin-left: 5px; margin-right: 5px">|</div>
                <div>店铺名称:{{ row.shopOrders[0].shopName }}</div>
            </div>
        </template>
        <SplitTableColumn prop="name" label="商品" width="450" align="center">
            <template #default="{ shopOrderItems }">
                <!-- 已拆分数据展示 -->
                <div class="orderIndex-table__commodity">
                    <div class="orderIndex-table__img-box">
                        <el-image
                            v-for="item in shopOrderItems?.slice(0, 1)"
                            :key="item.id"
                            fits="cover"
                            style="width: 68px; height: 68px"
                            shape="square"
                            size="large"
                            :src="item.image"
                            :title="item.productName"
                        />
                        <span class="order-info">
                            <el-tooltip
                                v-if="shopOrderItems?.[0]?.productName.length >= 44"
                                effect="dark"
                                :content="shopOrderItems?.[0]?.productName"
                                placement="top-start"
                            >
                                <p class="order-info__name">
                                    {{ shopOrderItems?.[0]?.productName }}
                                </p>
                            </el-tooltip>
                            <p v-else class="order-info__name">
                                {{ shopOrderItems?.[0]?.productName }}
                            </p>
                            <el-tooltip
                                v-if="handleSpecs(shopOrderItems?.[0]).length >= 20"
                                effect="dark"
                                :content="handleSpecs(shopOrderItems?.[0])"
                                placement="top-start"
                            >
                                <p class="order-info__spec">{{ handleSpecs(shopOrderItems?.[0]) }}</p></el-tooltip
                            >
                            <p v-else class="order-info__spec">{{ handleSpecs(shopOrderItems?.[0]) }}</p>
                        </span>
                    </div>
                    <div class="orderIndex-table__img-mask">
                        <span>￥{{ unitPrice(shopOrderItems)?.toFixed(2) }}</span>
                        <span style="color: #838383; font-size: 10px">x{{ num(shopOrderItems) }}</span>
                    </div>
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn label="待发货数" :is-mixed="true" align="center" width="90">
            <template #default="{ row }">
                <div>{{ num(row.shopOrders[0].shopOrderItems) }}</div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn label="收件人信息" :is-mixed="true" width="300">
            <template #default="{ row }">
                <div class="fdc" style="line-height: 18px">
                    <div>{{ getOrderReceiver(row).name }}</div>
                    <div>{{ getOrderReceiver(row).mobile }}</div>
                    <div>{{ getOrderReceiver(row).area?.join('') }}{{ getOrderReceiver(row).address?.replaceAll('~', '') }}</div>
                </div>
            </template>
        </SplitTableColumn>
        <SplitTableColumn label="操作" fixed="right" :is-mixed="true" width="230">
            <template #default="{ row }">
                <div class="fdc">
                    <el-form-item label="物流服务" style="margin-bottom: 5px">
                        <el-select
                            v-model="row.expressCompany.expressCompanyName"
                            placeholder="请选择物流服务"
                            @change="
                                (e) => {
                                    $emit('update:tableData', e, row, 'select')
                                }
                            "
                        >
                            <el-option
                                v-for="item in $props.companySelectList"
                                :key="item.id"
                                :label="item.logisticsCompanyName"
                                :value="item.logisticsCompanyName"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="运单号码">
                        <el-input
                            v-model="row.expressCompany.expressNo"
                            style="height: 28px"
                            maxlength="40"
                            @change="
                                (e) => {
                                    $emit('update:tableData', e, row, 'input')
                                }
                            "
                        />
                    </el-form-item>
                    <el-button type="danger" text size="small" @click="$emit('filterOrderList', row)">移除</el-button>
                </div>
            </template>
        </SplitTableColumn>
    </SplitTable>
</template>

<style scoped lang="scss">
.fcenter {
    padding-left: 20px;
    padding-right: 10px;
    .mla {
        position: sticky;
        right: 10px;
    }
}

@include b(orderIndex-table) {
    position: relative;
    overflow-x: auto;
    transition: height 0.5s;
    @include e(top) {
        @include flex(space-between);
        width: 100%;
    }
    @include e(top-time) {
        @include flex;
        & > div:nth-child(2) {
            padding: 0 60px;
        }
    }
    @include e(commodity) {
        width: 100%;
        display: flex;
    }
    @include e(img-box) {
        width: 390px;
        display: flex;
        font-size: 14px;
        align-items: center;
        .el-image {
            flex-shrink: 0;
            margin-right: 10px;
        }
        .order-info {
            display: flex;
            flex-direction: column;
            justify-content: space-evenly;
            height: 100%;
        }
        .order-info__name {
            line-height: 20px;
        }
        .order-info__spec {
            line-height: 20px;
            color: rgb(153, 153, 153);
            font-size: 14px;
            font-weight: 400;
        }
    }

    @include e(img) {
        flex-shrink: 0;
        border-radius: 5px;
        position: relative;
    }

    @include e(img-mask) {
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: end;
        font-size: 12px;
        color: #666666;
        margin-left: 20px;
    }
}
</style>
