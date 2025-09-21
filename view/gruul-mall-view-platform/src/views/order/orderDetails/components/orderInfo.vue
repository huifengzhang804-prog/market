<script lang="ts" setup>
import type { PropType } from 'vue'
import Decimal from 'decimal.js'
import { distributionModeStatus } from '@/composables/useOrderStatus'
import { payTypeFn } from '@/composables/usePaymentCn'
import RemarkView from '@/views/order/orderDetails/components/remark-view.vue'
import { calculate, MergedItem, OrderMergedItems, OrderStateStatus } from '../OrderStatusCalculate'

import subtotalDetail from './subtotal-detail.vue'
import { SellTypeEnum } from '@/views/good/types'
import type { OrderDetail } from '@/apis/order/type/order'
import { FormType, RemarkItem } from '@apis/order/types'
import { ApiOrder } from '../../types/order'

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
    return order.orderReceiver
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
const SubtotalOpenType = ref(false)
const currentrow = ref<MergedItem>()

watch(
    () => $props.order,
    (val: ApiOrder) => {
        if (Object.values(val).length) {
            if (val.shopOrders[0].remark) {
                remark.value = val.shopOrders[0].remark.items || []
            }
            orderMergedItems.value = calculate(val)
        }
    },
    { immediate: true },
)

const openSubtotal = (row: MergedItem) => {
    SubtotalOpenType.value = true
    currentrow.value = row
}
</script>

<template>
    <div v-if="orderReceiver" class="orderInfo">
        <div style="display: flex; align-items: center">
            <div class="orderInfo__title">
                {{ orderMergedItems.state.status }}
                <div style="font-size: 13px; text-align: left; margin-top: 30px; margin-left: 10px">店铺：{{ order.shopOrders[0].shopName }}</div>
            </div>
            <div class="orderInfo__steps">
                <el-steps :active="orderMergedItems.state.activeStep" align-center process-status="finish">
                    <el-step :description="orderMergedItems.state.createTime" title="提交订单" />
                    <el-step :description="orderMergedItems.state.payTime" title="买家已付款" />
                    <el-step :description="orderMergedItems.state.deliveryTime" title="商家发货" />
                    <el-step :description="orderMergedItems.state.receiveTime" title="买家收货" />
                </el-steps>
            </div>
        </div>
        <div class="orderInfo__userInfo">
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">订单信息</div>
                <div>订单编号：{{ $props.order.no }}</div>
                <div>下单时间：{{ $props.order.createTime }}</div>
                <div v-show="orderMergedItems.state.payTime">付款时间：{{ orderMergedItems.state.payTime }}</div>
                <div>支付方式：{{ payTypeFn($props.order!.orderPayment!.type) || '未' }}支付</div>
                <div>配送方式：{{ distributionModeStatus[$props.order.extra.distributionMode] }}</div>
            </div>
            <div class="orderInfo__userInfo--left">
                <div class="orderInfo__userInfo--title">收货人信息</div>
                <div>收货人姓名：{{ orderReceiver.name }}</div>
                <div>联系人电话：{{ orderReceiver.mobile }}</div>
                <div v-if="$props.order.extra.distributionMode !== 'SHOP_STORE'">
                    收货地址：{{ orderReceiver.area?.join('') }} {{ orderReceiver.address }}
                </div>
                <remark-view :remark="remark" class="detail__item" />
            </div>
            <div class="orderInfo__userInfo--right">
                <div class="orderInfo__userInfo--title">买家信息</div>
                <div>用户昵称：{{ $props.order.buyerNickname || '无' }}</div>
                <remark-view
                    :remark="$props.order?.remark ? [{ key: '备注', value: $props.order?.remark, type: FormType.TEXT }] : []"
                    class="detail__item"
                />
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
                                <div>{{ row.merged.specs?.join(',') }}</div>
                                <div>{{ SellTypeEnum[row?.merged?.sellType as keyof typeof SellTypeEnum] }}</div>
                            </div>
                            <div class="orderInfo__tab--goods-last">
                                <div>￥{{ divTenThousand(row.merged.salePrice)?.toFixed(2) }}</div>
                                <div>{{ row.merged.num }}件</div>
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
                        {{ divTenThousand(row.subtotal)?.toFixed(2) }}
                        <el-popover :width="650" placement="top" title="小计明细">
                            <template #reference>
                                <div style="color: #1890ff; position: absolute; bottom: 8px; right: 8px">明细</div>
                            </template>
                            <subtotal-detail :currentrow="row" />
                        </el-popover>
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
                >商品总价:<span style="color: #f70707"> {{ divTenThousand(orderMergedItems.total).toFixed(2) }}</span></span
            >
            <span
                >运费:<span style="color: #f70707">{{ divTenThousand(orderMergedItems.freight).toFixed(2) }}</span></span
            >
            <span
                >平台总优惠:<span style="color: #f70707">{{ divTenThousand(orderMergedItems.platform).toFixed(2) }}</span></span
            >
            <span
                >店铺总优惠:<span style="color: #f70707">{{ divTenThousand(orderMergedItems.shop).toFixed(2) }}</span></span
            >
            <span style="font-size: 20px"
                >{{ ['待支付', '已关闭'].includes(orderMergedItems.state.status) ? '应付款：' : '实付款：'
                }}<span style="color: #f70707">{{ divTenThousand(orderMergedItems.actual).toFixed(2) }} 元</span></span
            >
        </div>
    </div>
    <el-empty v-else description="暂无数据~" />
</template>

<style lang="scss" scoped>
@include b(orderInfo) {
    padding: 16px;
    overflow: scroll;
    @include e(steps) {
        padding: 20px;
        margin: 20px;
        width: 700px;
        border-left: 1px solid #d5d5d5;
    }
    @include e(title) {
        font-size: 28px;
        font-weight: Bold;
        color: #515151;
        width: 223px;
        text-align: center;
    }
    @include e(priceInfo) {
        text-align: center;
        height: 40px;
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
            margin: 17px 0;
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
            width: 300px;
            margin-left: 10px;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-direction: column;
        }
        @include m(goods-last) {
            font-size: 12px;
            color: #586884;
            width: 60px;
            margin-left: 10px;
            display: flex;
            // justify-content: space-between;
            align-items: flex-start;
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
