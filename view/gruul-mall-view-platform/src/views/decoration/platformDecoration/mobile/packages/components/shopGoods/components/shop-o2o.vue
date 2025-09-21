<template>
    <div class="shop_o2o_item">
        <!-- 店铺头部信息 -->
        <div class="shop-header">
            <div class="shop-logo">
                <van-image :src="shopItem.shop.logo" width="55" height="55" radius="4" />
            </div>
            <div class="shop-info">
                <div style="display: flex; align-items: center">
                    <div v-if="shopItem.shop.shopType === 'SELF_OWNED'" class="shop-type" style="background-color: #fb232f">自营</div>
                    <div v-else-if="shopItem.shop.shopType === 'PREFERRED'" class="shop-type" style="background-color: #8239f6">优选</div>
                    <div class="shop-name">{{ shopItem.shop.name }}</div>
                </div>
                <div class="shop-meta">
                    <span v-if="shopItem.shop.o2oInfo?.score" class="shop-score"
                        >{{ shopItem.shop.o2oInfo?.score }} <text style="font-size: 10px">分</text></span
                    >
                    <span>起送 ¥15</span>
                    <span>配送费 ¥5</span>
                    <span>距离 1.2㎞</span>
                </div>
            </div>
        </div>

        <div v-if="shopItem.shop.couponList?.length > 0" class="shop_o2o_item_content">
            <!-- 优惠券展示 -->
            <div class="coupon-banner">
                <div v-if="shopItem.shop.couponList[0]?.discount" class="coupon-amount">{{ shopItem.shop.couponList[0]?.discount }}折</div>
                <div v-if="shopItem.shop.couponList[0]?.amount" class="coupon-amount">￥{{ shopItem.shop.couponList[0]?.amount / 10000 }}</div>
                <div
                    v-if="shopItem.shop.couponList[0]?.type === 'PRICE_DISCOUNT' || shopItem.shop.couponList[0]?.type === 'PRICE_REDUCE'"
                    class="coupon-scope"
                >
                    无门槛
                </div>
                <div v-else-if="shopItem.shop.couponList[0]?.type === 'REQUIRED_PRICE_REDUCE'" class="coupon-scope">满减券</div>
                <div v-else class="coupon-scope">满折券</div>
                <div v-if="shopItem.shop.couponList[0]?.productType === 'ALL'" class="coupon-desc">全部商品</div>
                <div v-else-if="shopItem.shop.couponList[0]?.productType === 'SHOP_ALL'" class="coupon-desc">店铺全部商品</div>
                <div v-else-if="shopItem.shop.couponList[0]?.productType === 'ASSIGNED'" class="coupon-desc">指定商品可用</div>
                <div v-else class="coupon-desc">指定商品不生效</div>
                <div class="coupon-xuxian"></div>
                <div class="coupon-btn">领超值券</div>
            </div>

            <!-- 商品列表 -->
            <div class="goods-list">
                <div v-for="(item, index) in shopItem.goods" :key="index" class="goods-item">
                    <van-image :src="item.logo" class="goods-image" radius="4" width="90" height="90" />
                    <div class="goods-info">
                        <div class="goods-name">{{ item.name }}</div>
                        <div class="goods-price">
                            <span class="price-prefix">¥</span>
                            <span class="price-value">{{ item.price }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import shopGoods from '../shopGoods'

defineProps({
    shopItem: {
        type: Object as PropType<(typeof shopGoods.shopInfo)[0]>,
        default: shopGoods.shopInfo[0],
    },
})
</script>

<style scoped lang="scss">
.shop_o2o_item {
    background: #fff;
    border-radius: 8px;
    margin: 0 8px;

    .shop-header {
        display: flex;
        align-items: center;
        padding-bottom: 12px;

        .shop-logo {
            margin-right: 12px;
        }

        .shop-info {
            height: 55px;
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-around;

            .shop-type {
                padding: 2px 4px;
                font-size: 10px;
                color: #fff;
                border-radius: 2px;
                margin-right: 5px;
                position: relative;
                top: 1px;
            }

            .shop-name {
                font-size: 16px;
                font-weight: bold;
                color: #000;
            }

            .shop-meta {
                display: flex;
                align-items: center;
                font-size: 10px;
                color: #666;

                span {
                    margin-right: 12px;

                    &.shop-score {
                        color: #ff4444;
                        font-size: 12px;
                    }
                }
            }
        }
    }

    .shop_o2o_item_content {
        display: flex;
        height: 140px;

        .coupon-banner {
            display: flex;
            flex-direction: column;
            width: 90px;
            border-radius: 4px;
            color: #fb232f;
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
            background: radial-gradient(circle at left bottom, transparent 8px, #fbf1ef 0) top left / 50% 70% no-repeat,
                radial-gradient(circle at left top, transparent 8px, #fbf1ef 0) bottom left / 50% 31% no-repeat,
                radial-gradient(circle at right bottom, transparent 8px, #fbf1ef 0) top right / 50% 70% no-repeat,
                radial-gradient(circle at right top, transparent 8px, #fbf1ef 0) bottom right / 50% 31% no-repeat;

            > div {
                margin-top: 10px;
                font-family: Arial, sans-serif;
                font-weight: 550; // 正常字重
                font-size: 14px;
            }

            .coupon-scope {
                font-family: Arial, sans-serif;
                font-weight: 400; // 正常字重
                padding: 0 3px;
                font-size: 12px;
            }

            .coupon-desc {
                width: 88%;
                font-size: 12px;
            }

            .coupon-btn {
                font-size: 12px;
                padding: 2px 5px;
                background-color: #fb232f;
                color: #fff;
                border-radius: 4px;
                font-family: Arial, sans-serif;
                font-weight: 300; // 正常字重
                position: absolute;
                bottom: 10px;
            }
            .coupon-xuxian {
                width: 82%;
                height: 1px;
                border-top: 1px dashed #fb232f;
                position: absolute;
                bottom: 42px;
            }
        }

        .goods-list {
            margin-left: 6px;
            display: flex;
            overflow-x: scroll;
            padding-bottom: -5px;
            height: 100%;
            // 隐藏滚动条
            &::-webkit-scrollbar {
                display: none;
            }

            .goods-item {
                width: 90px;
                margin-right: 6px;

                .goods-info {
                    height: 46px;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-around;
                    padding: 0 3px;
                    .goods-name {
                        font-size: 12px;
                        color: #000;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        font-family: Arial, sans-serif;
                        font-weight: 400; // 正常字重
                    }

                    .goods-price {
                        font-size: 16px;
                        color: #fb232f;
                        font-family: Arial, sans-serif;
                        font-weight: 500; // 正常字重

                        .price-prefix {
                            font-size: 10px;
                            margin-right: 2px;
                        }
                    }
                }
            }
        }
    }
}
</style>
