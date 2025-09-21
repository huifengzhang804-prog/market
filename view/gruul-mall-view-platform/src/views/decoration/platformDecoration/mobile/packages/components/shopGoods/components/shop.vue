<script setup lang="ts">
import shopGoods from '../shopGoods'

defineProps({
    shopItem: {
        type: Object as PropType<(typeof shopGoods.shopInfo)[0]>,
        default: shopGoods.shopInfo[0],
    },
})
</script>

<template>
    <div class="shop-item">
        <div class="shop-item-advertisement">
            <van-image :src="shopItem.shop.advertisement" width="139px" height="139px" />
            <div class="shop-item-goods-info-promotion">{{ shopItem.shop.promotion }}</div>
        </div>
        <div class="shop-item-goods">
            <div v-for="good in shopItem.goods.slice(0, 3)" :key="good.id" class="shop-item-goods-item">
                <van-image :src="good.logo" width="65px" height="85px" />
                <div class="shop-item-goods-info">
                    <div class="shop-item-goods-info-name">{{ good.name }}</div>
                    <div class="shop-item-goods-info-price" :style="{ textAlign: 'left' }">
                        <text>￥</text>
                        <text v-if="String(good.price)?.includes('.')">{{ String(good.price).split('.')[0] }}.</text>
                        <text v-if="String(good.price)?.includes('.')" style="font-size: 12px">{{ String(good.price).split('.')[1] }}</text>
                        <text v-else>{{ good.price }}</text>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.shop-item {
    display: flex;
    margin-bottom: 10px;

    :deep(.van-image) {
        .van-image__img {
            border-radius: 12px;
        }
    }
    .shop-item-advertisement {
        position: relative;
        .shop-item-goods-info-promotion {
            position: absolute;
            bottom: 12px;
            left: 7px;
            font-size: 12px;
            color: #fff;
            margin: 5px 0;
            width: 126px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
        }
    }

    .shop-item-goods {
        flex: 1;
        display: flex;
        justify-content: start;

        .shop-item-goods-item {
            background-color: #fafafa;
            border-radius: 12px;
            margin-left: 8px;

            .shop-item-goods-info {
                width: 65px;
                display: flex;
                flex-direction: column;
                justify-content: space-between;

                .shop-item-goods-info-name {
                    padding: 0 2px;
                    font-size: 13px;
                    color: #000;
                    font-family: Arial, sans-serif;
                    font-weight: 400; // 正常字重
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    -webkit-line-clamp: 1;
                    -webkit-box-orient: vertical;
                    margin: 5px 0;
                }

                .shop-item-goods-info-price {
                    font-size: 16px;
                    color: #000;
                    font-family: Arial, sans-serif;
                    font-weight: 500; // 正常字重
                }
            }
        }
    }
}
</style>
