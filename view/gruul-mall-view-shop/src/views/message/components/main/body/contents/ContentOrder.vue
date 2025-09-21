<script setup lang="ts">
import type { PropType } from 'vue'
import { Message } from '@/views/message/components/types'

/**
 * msg 消息内容
 * isMine 是否是我的消息
 */
const $router = useRouter()
const props = defineProps({
    message: {
        type: Object as PropType<Message>,
        required: true,
    },
    isMine: {
        type: Boolean,
        default: false,
    },
})
const product = computed(() => {
    const productMsg = props.message.message
    if (!productMsg) {
        return { id: '', name: '未正确获取商品信息', salePrices: [], pic: '' }
    }
    return JSON.parse(productMsg)
})
const handleNavToOrderList = (item: any) => {
    if (item) {
        if (item.path) {
            $router.push({
                name: item.path,
                query: {
                    orderNo: item.no,
                },
            })
        } else {
            $router.push({
                name: 'orderIndex',
                query: {
                    orderNo: item.orderNo,
                },
            })
        }
    }
}
</script>
<template>
    <el-link v-if="!product.my" type="info" :underline="false" @click="handleNavToOrderList(product)">
        <div class="message-content-product product-box">
            <div style="height: 130px"><el-image :src="product.pic" style="height: 100%" fit="scale-down" /></div>
            <div class="product-box__right">
                <span>订单号：{{ product.no }}</span>
                <div class="product-box__right--footer">
                    <span> {{ product.name ? product.name : '未正确获取商品信息' }} </span>
                    <div>实付￥{{ product.amountRealPay }}</div>
                </div>
            </div>
        </div>
    </el-link>
    <div v-else class="order">
        <div class="order_head">
            <span>订单号：{{ product.no }}</span>
            <span :style="{ color: product.afsStatu?.length > 6 ? '#F54319' : product.afsStatu === '待支付' ? '#FD9224' : '#333' }">{{
                product.afsStatu
            }}</span>
        </div>
        <div v-for="(item, index) in product.commodityList" :key="index" class="order_info">
            <img :src="item.pic" alt="" />
            <div class="order_info_right">
                <p class="order_info_right_name">{{ item.name ? item.name : '未正确获取商品信息' }}</p>
                <div>
                    <span
                        >￥<span style="font-weight: bold">{{ item.amountRealPay }}</span></span
                    >
                    <span style="margin-left: 16px">共{{ item.num }}件商品</span>
                </div>
            </div>
        </div>
        <div class="order_btn">
            <p @click="handleNavToOrderList(product)">查看订单</p>
        </div>
    </div>
</template>
<style scoped lang="scss">
.message-content-product {
    margin: $rows-spacing-row-sm;
    height: 200px;
}
.product-box {
    background: $rows-text-color-inverse;
    border-radius: $rows-border-radius-sm;
    padding: $rows-spacing-row-sm;
    border: 1px solid var(--el-border-color);
    height: 100%;
    display: flex;
}
.product-box__right {
    margin-left: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}
.product-box__right--footer {
    display: flex;
    justify-content: space-between;
}
.product-info {
    color: $rows-text-color-grey;
    flex: 1;
    display: flex;
    padding-left: $rows-spacing-col-lg;
    flex-direction: column;
    justify-content: space-between;
    min-width: 0;
}
.product-info .product-name {
    height: 40px;
    font-size: 15px;
    width: 100%;
    @include utils-ellipsis(2);
}
.product-info .product-prices {
    height: 60px;
    font-size: 20px;
    color: $rows-color-error;
    @include utils-ellipsis(2);
}

.order {
    width: 490px;
    padding: 20px 16px;
    background-color: #fff;
    border: 1px solid rgb(85, 92, 253);
    border-radius: 8px 0 8px 8px;
    margin-right: 10px;
    .order_head {
        width: 100%;
        padding-bottom: 24px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #999;
    }
    .order_info {
        width: 100%;
        display: flex;
        padding-bottom: 20px;
        img {
            width: 70px;
            height: 70px;
        }
        .order_info_right {
            height: 70px;
            margin-left: 10px;
            padding: 2px 0;
            width: 258px;
            font-size: 14px;
            color: #333;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            .order_info_right_name {
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
            }
        }
    }
    .order_btn {
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
</style>
