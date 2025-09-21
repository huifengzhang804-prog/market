<script lang="ts" setup>
import type { PropType } from 'vue'
import Decimal from 'decimal.js'
import { distributionModeStatus } from '@/composables/useOrderStatus'
import { payTypeFn } from '@/composables/usePaymentCn'
import RemarkView from '@/views/order/orderDetails/components/remark-view.vue'
import { type ApiOrder, SellTypeEnum } from '@/views/order/types/order'
import { calculate, OrderMergedItems, OrderStateStatus } from '../OrderStatusCalculate'
import { RemarkItem } from '@apis/order/types'

const $props = defineProps({
    order: {
        type: Object as PropType<ApiOrder>,
        default() {
            return {}
        },
    },
})
const orderReceiver = computed(() => {
    const order = $props.order
    if (!order || !order.shopOrders) return null
    const shopOrderReceiver = order.shopOrders[0].orderReceiver
    return shopOrderReceiver ? shopOrderReceiver : order.orderReceiver
})
const orderMergedItems = ref<OrderMergedItems>({
    state: {
        status: OrderStateStatus.UNPAID,
        activeStep: 0,
        createTime: '',
        payTime: undefined,
        deliveryTime: undefined,
        receiveTime: undefined,
    },
    total: new Decimal(0),
    actual: new Decimal(0),
    freight: new Decimal(0),
    platform: new Decimal(0),
    shop: new Decimal(0),
    items: [],
})
const { divTenThousand } = useConvert()
// 订单备注信息
const remark = ref<RemarkItem[]>([])

watch(
    () => $props.order,
    (val) => {
        if (Object.values(val).length) {
            remark.value = val.shopOrders[0]?.remark?.items || []
            orderMergedItems.value = calculate(val)
        }
    },
    { immediate: true },
)

const payText = computed(() => {
    console.log('$props.order.orderPayment.type', $props.order?.orderPayment?.type)
    return $props.order?.orderPayment?.type ? '实付款: ' : '应付款: '
})
</script>

<template>
    <div v-if="orderReceiver" class="orderInfo">
        <div style="display: flex; align-items: center">
            <div class="orderInfo__title">
                {{ orderMergedItems.state.status }}
            </div>
            <div class="orderInfo__steps">
                <el-steps :active="orderMergedItems.state.activeStep" align-center process-status="finish">
                    <el-step :description="orderMergedItems.state.createTime" title="买家下单" />
                    <el-step :description="orderMergedItems.state.payTime" title="买家已付款" />
                    <el-step :description="orderMergedItems.state.deliveryTime" title="商家发货" />
                    <el-step :description="orderMergedItems.state.receiveTime" title="买家收货" />
                </el-steps>
            </div>
        </div>
        <div class="orderInfo__userInfo">
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">订单信息</div>
                <div>订单号：{{ $props.order.no }}</div>
                <div>下单时间：{{ $props.order.createTime }}</div>
                <div v-show="orderMergedItems.state.payTime">支付时间：{{ orderMergedItems.state.payTime }}</div>
                <div>支付方式：{{ $props.order?.orderPayment?.type ? payTypeFn($props.order?.orderPayment?.type) : '未' }}支付</div>
                <div>配送方式：{{ distributionModeStatus[$props.order.extra?.distributionMode] }}</div>
            </div>
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">收货人信息</div>
                <div>收货人姓名：{{ orderReceiver.name }}</div>
                <div>联系人电话：{{ orderReceiver.mobile }}</div>
                <div v-if="$props.order.extra?.distributionMode !== 'SHOP_STORE'">
                    收货地址：
                    {{ orderReceiver.area?.join('') }}
                    {{ orderReceiver.address }}
                </div>
                <remark-view :remark="remark" class="detail__item" />
            </div>
            <div class="orderInfo__userInfo--right">
                <div class="orderInfo__userInfo--title">买家信息</div>
                <div>用户昵称：{{ $props.order.buyerNickname || '无' }}</div>
            </div>
        </div>
        <div v-if="order?.shopOrders">
            <el-table :data="orderMergedItems.items" border calss="orderInfo__tab" style="width: 100%">
                <template #empty>
                    <ElTableEmpty />
                </template>
                <el-table-column align="center" label="商品" width="460">
                    <template #default="{ row }">
                        <div class="orderInfo__tab--goods">
                            <el-image
                                :src="row.merged.image"
                                :title="row.merged.productName"
                                fits="cover"
                                shape="square"
                                size="large"
                                style="width: 70px; height: 70px"
                            />
                            <div class="orderInfo__tab--goods-right">
                                <div class="orderInfo__tab--goods-right-show">{{ row.merged.productName }}</div>
                                <div>{{ row?.merged?.specs?.join(', ') }}</div>
                                <div>{{ SellTypeEnum[row?.merged?.sellType as keyof typeof SellTypeEnum] }}</div>
                            </div>
                            <div class="orderInfo__tab--goods-last">
                                <div>￥{{ divTenThousand(row.merged.salePrice) }}</div>
                                <div>&nbsp;{{ row.merged.num }} 件</div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="优惠">
                    <template #default="{ row }">
                        {{ divTenThousand(row.discountAmount)?.toFixed(2) }}
                    </template>
                </el-table-column>
                <el-table-column align="center" label="小计">
                    <template #default="{ row }">
                        <div style="position: relative">
                            <el-popover :width="650" placement="top" title="小计明细">
                                <template #reference>
                                    <span style="color: #1890ff; text-align: right; position: absolute; bottom: -6px; font-size: 12px; right: 0"
                                        >明细</span
                                    >
                                </template>
                                <div class="dialog__top">
                                    <div>
                                        商品金额：<span style="color: #f70707">
                                            {{
                                                divTenThousand(row?.merged.salePrice)
                                                    .mul(row?.merged.num || 0)
                                                    ?.toFixed(2)
                                            }}</span
                                        >
                                    </div>
                                    <div>
                                        优惠金额：<span style="color: #f70707"> {{ divTenThousand(row?.discountAmount)?.toFixed(2) }}</span>
                                    </div>
                                    <div>
                                        小计： <span style="color: #f70707"> {{ divTenThousand(row?.subtotal)?.toFixed(2) }}</span>
                                    </div>
                                </div>
                                <el-table :data="row?.discounts" border style="width: 100%">
                                    <template #empty>
                                        <ElTableEmpty />
                                    </template>
                                    <el-table-column align="center" label="层级">
                                        <template #default="{ row: tableRow }">{{ tableRow.level }}</template>
                                    </el-table-column>
                                    <el-table-column label="优惠项" width="300px">
                                        <template #default="{ row: tableRow }">{{ tableRow.name }}</template>
                                    </el-table-column>
                                    <el-table-column align="center" label="金额">
                                        <template #default="{ row: tableRow }">{{ divTenThousand(tableRow.amount)?.toFixed(2) }}</template>
                                    </el-table-column>
                                </el-table>
                            </el-popover>
                            <!-- <div
style="color: #1890ff; text-align: right; font-size: 8px; width: 100%; position: absolute; top: 23px; right: 0"
@click="openSubtotal(row)"
>
明细
</div> -->
                            <span style="line-height: 61px">{{ divTenThousand(row.subtotal)?.toFixed(2) }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="已发货数">
                    <template #default="{ row }">{{ row.status.deliveredCount }}</template>
                </el-table-column>
                <el-table-column align="center" label="待发货数">
                    <template #default="{ row }">{{ row.status.waitingDeliveryCount }}</template>
                </el-table-column>
                <el-table-column align="center" label="发货状态">
                    <template #default="{ row }">{{ row.status.status }}</template>
                </el-table-column>
                <el-table-column align="center" label="售后状态">
                    <template #default="{ row }">{{ row.status.afsStatus }}</template>
                </el-table-column>
            </el-table>
        </div>
        <!-- </el-scrollbar> -->
        <div class="orderInfo__priceInfo">
            <span
                >运费: <span style="font-size: 20px">{{ divTenThousand(orderMergedItems.freight).toFixed(2) }}</span>
            </span>
            <br />
            <span>
                商品总价: <span style="font-size: 20px"> {{ divTenThousand(orderMergedItems.total).toFixed(2) }}</span>
            </span>
            <br />
            <span>
                平台总优惠: <span style="color: #f70707; font-size: 20px">-{{ divTenThousand(orderMergedItems.platform).toFixed(2) }}</span>
            </span>
            <br />
            <span>
                店铺总优惠: <span style="color: #f70707; font-size: 20px">-{{ divTenThousand(orderMergedItems.shop).toFixed(2) }}</span>
            </span>
            <br />
            <br />
            <span style="font-size: 20px">
                {{ payText }} <span style="color: #f70707">￥</span
                ><span style="color: #f70707; font-size: 38px">{{ divTenThousand(orderMergedItems.actual).toFixed(2) }} </span>
            </span>
        </div>
    </div>
    <el-empty v-else description="暂无数据~" />
</template>

<style lang="scss" scoped>
@include b(orderInfo) {
    @include e(steps) {
        padding: 20px 0;
        margin: 20px;
        width: 700px;
        border-left: 1px solid #d5d5d5;
    }
    @include e(title) {
        font-size: 26px;
        font-weight: Bold;
        color: #515151;
        width: 223px;
        text-align: center;
    }
    @include e(priceInfo) {
        text-align: center;
        color: #101010;
        @include flex(center, flex-end);
        flex-direction: column;
        margin: 50px 50px 20px 0;
    }

    @include e(footer) {
        text-align: right;
        & span:only-child {
            font-size: 22px;
            font-weight: bold;
            color: #2e99f3;
        }
    }
    @include e(userInfo) {
        display: flex;
        // background: #f7f8fa;
        margin-bottom: 22px;
        padding: 0 30px;
        @include m(left) {
            padding-left: 20px;
            flex: 0.5;
            & div:nth-of-type(n + 2) {
                margin-bottom: 11px;
            }
        }
        @include m(right) {
            margin-left: 30px;
            flex: 0.5;
            & div:nth-of-type(n + 2) {
                margin-bottom: 11px;
            }
        }
        @include m(title) {
            font-size: 14px;
            color: #333333;
            font-weight: Bold;
            margin: 10px 0;
            margin-left: -26px;
        }
    }
    @include e(tab) {
        @include m(goods) {
            display: flex;
            justify-content: space-between;
        }
        @include m(goods-right) {
            font-size: 12px;
            color: #586884;
            width: 290px;
            margin-left: 10px;
            display: flex;
            align-items: flex-start;
            flex-direction: column;
        }
        @include m(goods-last) {
            font-size: 12px;
            color: #586884;
            margin-left: 10px;
            display: flex;
            align-items: flex-end;
            flex-direction: column;
        }
        @include m(goods-right-show) {
            @include utils-ellipsis(2);
            font-weight: 600;
        }
        @include m(price) {
            display: flex;
            font-size: 12px;
            color: #50596d;
            justify-content: center;
            align-items: flex-start;
            flex-direction: column;
        }
    }
}

@include b(dialog) {
    @include e(top) {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 20px;
    }
}

.el-step {
    :deep(.el-step__description) {
        padding: 0;
    }
}

//去除table的每一条数据的下边框
.el-table td {
    border-bottom: none;
}

//去除表格的最下面一行的边框
.tableStyle::before {
    width: 0;
}

//设置表的外边框
.el-table {
    border-radius: 9px;
    border: 1px solid #d5d5d5;
}

.el-table th.is-leaf {
    border-bottom: none;
}
</style>
