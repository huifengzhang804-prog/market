<script setup lang="ts">
import price from '@/views/decoration/pc/components/price/index.vue'
import couponList from './coupon-list.vue'
import type { ApiRetrieveComItemType } from '@/apis/good/model/index'

defineProps<{
    item: ApiRetrieveComItemType
}>()
const { divTenThousand } = useConvert()
</script>

<template>
    <div class="goods-item">
        <img class="goods-item__img" :src="item.pic" />

        <!-- 价格 -->
        <price :price="divTenThousand(item.prices?.[0] ?? '0')" :sale-price="divTenThousand(item.salePrices[0])" class="m-t-16" />

        <div class="goods-item__name">{{ item.productName }}</div>

        <!-- 优惠列表 -->
        <coupon-list :coupon-list="item.couponList ?? []" class="m-t-8" />

        <div class="goods-item__info m-t-8">{{ item.salesVolume }}人付款</div>
    </div>
</template>

<style lang="scss" scoped>
@include b(goods-item) {
    width: 221px;
    height: 352px;
    padding: 16px 16px 12px;
    background-color: #fff;
    @include e(img) {
        width: 189px;
        height: 189px;
        border-radius: 4px;
    }

    @include e(name) {
        margin-top: 10px;
        color: #141414;
        text-align: justify;
        font-size: 14px;
        height: 38px;

        @include utils-ellipsis(2);
    }

    @include e(info) {
        font-size: 12px;
        color: #999999;
        text-align: end;
    }
}
</style>
