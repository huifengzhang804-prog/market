<template>
    <div class="goods">
        <div class="goods__img">
            <img v-if="goodsItem.logo" :src="goodsItem?.logo" style="width: 100%; height: 100%" />
            <el-empty v-else description="请选择商品" style="width: 100%; height: 100%" />
        </div>
        <template v-if="goodsItem.name">
            <h3 class="goods__name">{{ goodsItem?.name }}</h3>
            <div class="goods__price">{{ goodsItem?.price }}</div>
        </template>
        <h3 v-else class="goods__name">请选择商品</h3>
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import shopGoods from '../shopGoods'
const goodsItemDefult = shopGoods.shopInfo[0].goods[0]
defineProps({
    goodsItem: {
        type: Object as PropType<Partial<typeof goodsItemDefult>>,
        default: shopGoods.shopInfo[0].goods[0],
    },
})
</script>

<style scoped lang="scss">
$color-red: #fa3534;
@include b(goods) {
    width: 111px;
    @include e(name) {
        color: rgba(16, 16, 16, 1);
        font-size: 13px;
        @include utils-ellipsis(1);
        margin: 10px 0;
    }
    @include e(img) {
        width: 100%;
        height: 120px;
        border-radius: 8px;
        overflow: hidden;
    }
    @include e(price) {
        color: $color-red;
        font-size: 14px;
        margin-bottom: 10px;
        font-weight: 600;
        &::before {
            content: '￥';
            display: inline-block;
            font-size: 12px;
            transform: scale(0.9);
        }
    }
}
</style>
