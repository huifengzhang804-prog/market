<template>
    <div>
        <QIcon name="icon-dingdan3" class="sendOut" size="20px" @click="dialogTableVisible = true"></QIcon>
        <el-dialog
            v-model="dialogTableVisible"
            title=""
            width="522"
            center
            :style="{ backgroundColor: searchType.total * 1 > 0 ? '#f3f3f3' : ' #fff', height: ' 765px' }"
            top="8vh"
        >
            <template #title>
                <span class="title">请选择要发送的订单</span>
            </template>
            <div class="order_con">
                <div class="search">
                    <el-input
                        v-model="searchType.orderNo"
                        style="width: 100%; border: none"
                        size="large"
                        placeholder="请输入订单号搜索"
                        @keydown.enter="loadMoreItems"
                    >
                        <template #prepend>
                            <QIcon name="icon-sousuo3" size="16px" @click="loadMoreItems" />
                        </template>
                    </el-input>
                </div>
                <div v-if="searchType.total * 1 > 0" ref="scrollContainer" class="order_list" @scroll="handleScroll">
                    <div v-for="(item, index) in orderList" :key="index" class="order_item">
                        <div class="order_item_head">
                            <span style="color: #999">订单号：{{ item.no }}</span>
                            <span style="color: #333; font-weight: 500">{{ calculate(item as any).state.status }}</span>
                        </div>
                        <div v-for="(text, key) in item.shopOrders[0].shopOrderItems" :key="key">
                            <div class="order_item_cont">
                                <img :src="text.image" alt="" />
                                <div class="order_item_info">
                                    <div class="order_item_info_left">
                                        <p class="order_item_info_name">{{ text.productName }}</p>
                                        <div class="order_item_info_price">
                                            <span> {{ text.specs.join('、') }} </span>
                                        </div>
                                        <div
                                            v-if="afsStatus[text.afsStatus as keyof typeof afsStatus].list"
                                            style="position: absolute; bottom: -30px; left: 0; color: #f54319"
                                        >
                                            <div class="order-status_text">
                                                <span>{{ afsStatus[text.afsStatus as keyof typeof afsStatus].list }}</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="order_item_info_right">
                                        <span>￥{{ divTenThousand(text.dealPrice)?.toFixed(2) }}</span>
                                        <span style="color: #838383">x{{ text.num }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="order_item_foot">
                            <p @click="SendAnOrder(item)">发送订单</p>
                        </div>
                    </div>
                    <div v-if="loading" class="footer"><span>加载中...</span></div>
                    <div v-else class="footer">
                        <span>已经到底</span>
                    </div>
                </div>
            </div>
            <div v-if="searchType.total * 1 === 0" class="order_noOrder">
                <div class="noOrder">
                    <img src="@/assets/images/no_shop_examine.png" alt="" />
                    <p>没有关联的订单</p>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { ref, reactive, computed } from 'vue'
import { doGetOrderList } from '@/apis/order'
import { calculate } from '@/views/order/orderDetails/OrderStatusCalculate'
import { afsStatus } from '@/composables/useAfsStatus'
import { MessageType } from '@/views/message/components/types'
import { MessageUser } from '@/views/message/customerService/types'
import type { Order } from '@/apis/order/types.ts'

const props = defineProps({
    user: {
        type: Object as PropType<MessageUser>,
        default: () => {},
    },
})
const { divTenThousand } = useConvert()
const dialogTableVisible = ref(false)
const loading = ref(false)
const orderList = ref<Order[]>([])
const scrollContainer = ref()
console.log(props.user.chatWithUserInfo, 'props.user.chatWithUserInfo')
const searchType = reactive({
    buyerId: props.user && props.user.chatWithUserInfo.userId,
    current: 1,
    size: 10,
    total: 0,
    orderNo: '',
    isMiniAppTip: true,
})
const emits = defineEmits(['orderHandle'])

watch(
    () => dialogTableVisible.value,
    (val) => {
        if (val) {
            orderList.value = []
            loadMoreItems()
        }
    },
)
watch(
    () => props.user,
    (val) => {
        if (val) {
            searchType.buyerId = props.user.chatWithUserInfo.userId
            orderList.value = []
        }
    },
)

const loadMoreItems = async (str?: string) => {
    loading.value = true
    const { data, code } = await doGetOrderList({ ...searchType })
    if (code === 200) {
        searchType.total = data.total
        loading.value = false
        if (str) {
            orderList.value = data.records
        } else {
            orderList.value = [...orderList.value, ...data.records]
        }
    }
}

const initialization = () => {
    orderList.value = []
    searchType.current = 1
    searchType.total = 0
    searchType.orderNo = ''
}

const handleScroll = () => {
    const container = scrollContainer.value
    if (container.scrollTop + container.clientHeight >= container.scrollHeight) {
        searchType.current++
        loadMoreItems()
    }
}

const SendAnOrder = (item: Order) => {
    const { totalAmount, shopOrderItems } = item.shopOrders[0]
    const { productName, image, productId } = item.shopOrders[0].shopOrderItems[0]
    emits('orderHandle', {
        messageType: MessageType.ORDER,
        content: JSON.stringify({
            id: productId,
            name: productName,
            salePrices: totalAmount,
            pic: image,
            amountRealPay: divTenThousand(
                shopOrderItems.reduce((sum: any, text: any) => {
                    return sum + text.dealPrice
                }, 0),
            ).toFixed(2),
            no: item.no,
            createTime: item.createTime,
            afsStatu: afsStatus[item.shopOrders[0].shopOrderItems[0].afsStatus as keyof typeof afsStatus].list || calculate(item).state.status,
            commodityList: item.shopOrders[0].shopOrderItems.map((text: any) => {
                return {
                    ...text,
                    pic: text.image,
                    name: text.productName,
                    amountRealPay: divTenThousand(text.dealPrice)?.toFixed(2),
                    dealPrice: divTenThousand(text.dealPrice)?.toFixed(2),
                }
            }),
            my: true,
        }),
    })
    dialogTableVisible.value = false
    initialization()
}
</script>

<style lang="scss" scoped>
.title {
    color: #333333;
    font-weight: bold;
    font-size: 16px;
}
:deep(.el-dialog) {
    padding: 16px 5px;
}
:deep(.el-dialog__headerbtn) {
    position: absolute;
    right: 0;
    top: 0;
}
:deep(.el-dialog__header) {
    padding-right: 0;
}
:deep(.el-dialog__close) {
    font-size: 24px;
    color: #666;
    position: absolute;
    top: 8px;
    right: 10px;
}
:deep(.el-input-group__prepend) {
    border: none;
    padding: 0 0 0 14px;
    box-shadow: none;
    background-color: #fff;
    border-radius: 20px 0 0 20px;
}
:deep(.el-input__wrapper) {
    box-shadow: none;
    border-radius: 0 20px 20px 0;
}
.order_con {
    display: flex;
    flex-direction: column;
    align-items: center;

    .search {
        width: 100%;
        padding: 0 10px;
    }
    .order_list::-webkit-scrollbar {
        width: 5px; /* 设置滚动条的宽度 */
        height: 10px;
    }
    .order_list {
        width: 100%;
        height: 670px;
        overflow-y: auto;
        padding: 0 6px 0 12px;

        .order_item {
            background-color: #fff;
            margin-top: 20px;
            border-radius: 5px;
            display: flex;
            flex-direction: column;
            padding: 18px 16px;

            .order_item_head {
                display: flex;
                width: 100%;
                align-items: center;
                justify-content: space-between;
            }

            .order_item_cont {
                display: flex;
                height: 70px;
                margin-top: 18px;

                img {
                    width: 70px;
                    height: 70px;
                }

                .order_item_info {
                    display: flex;
                    justify-content: space-between;
                    margin-left: 16px;
                    flex: 1;
                    padding: 3px 0;

                    .order_item_info_left {
                        position: relative;
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;

                        .order_item_info_name {
                            width: 258px;
                            font-size: 14px;
                            color: #333;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                        }

                        .order_item_info_price {
                            width: 248px;
                            font-size: 13px;
                            color: #999;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            display: -webkit-box;
                            -webkit-line-clamp: 1;
                            -webkit-box-orient: vertical;

                            span {
                                margin-right: 12px;
                            }
                        }
                    }

                    .order_item_info_right {
                        color: #333;
                        height: 37px;
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;
                        align-items: end;
                    }
                }
            }
        }

        .order_item_foot {
            width: 100%;
            display: flex;
            justify-content: end;
            p {
                background-color: rgba(85, 92, 253, 0.1);
                color: #555cfd;
                padding: 5px 14px;
                border-radius: 20px;
                cursor: pointer;
            }
        }
    }
    .footer {
        text-align: center;
        font-size: 12px;
        color: #333;
        padding: 15px 0;
    }
}
.order_noOrder {
    height: 500px;
    display: flex;
    align-items: center;
    justify-content: center;
    .noOrder {
        display: flex;
        flex-direction: column;
        align-items: center;
        color: #999;
        img {
            width: 206px;
            height: 144px;
        }
        p {
            margin-top: 30px;
        }
    }
}
</style>
