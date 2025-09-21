<script setup lang="ts">
const $props = defineProps({
    tagShow: {
        type: Boolean,
        default: false,
    },
    tagStyle: {
        type: Number,
        default: 1,
    },
})
const getGoodsCornerMark = computed(() => {
    // 商品角标 1新品 2热卖 3抢购
    const styles = ['goods-item__coner1', 'goods-item__coner2', 'goods-item__coner3']
    return {
        class: styles[+$props.tagStyle - 1],
    }
})
</script>

<template>
    <!-- TODO:隐藏角标 -->
    <div v-if="false" :class="['goods-item__coner', getGoodsCornerMark.class]">
        <!-- <img :src="getGoodsCornerMark.url" /> -->
        <div v-if="tagStyle === 1" class="goods-item__coner1--item"></div>
        <div v-if="tagStyle === 2" class="goods-item__coner2--item"></div>
        <div v-if="tagStyle === 3" class="goods-item__coner3--item"></div>
    </div>
</template>

<style scoped lang="scss">
.goods-item__coner1--item {
    width: 50px;
    height: 21px;
    background: linear-gradient(90deg, #ff164b 0%, #ffa694 103.92%);
    border-radius: 10px 0px;
    &::before {
        content: '新品';
        color: #fff;
        font-size: 12px;
        display: block;
        line-height: 21px;
        text-align: center;
    }
}
.goods-item__coner2--item {
    width: 50px;
    height: 50px;
    background: #000;
    border-top-left-radius: 10px;
    clip-path: polygon(0% 0%, 100% 0, 0% 100%, 0% 100%, 0 81.595%);
    background: linear-gradient(10deg, #ff164b 20%, #ffa694 90%);
    &::before {
        content: 'H O T';
        display: inline-block;
        color: #fff;
        font-size: 12px;
        transform: matrix(0.7, -0.7, 0.71, 0.7, 1, 8);
    }
}

.goods-item__coner3--item {
    width: 50px;
    height: 21px;
    background: linear-gradient(90deg, #ff164b 0%, #ffa694 103.92%);
    border-radius: 10px 10px 10px 0px;
    &::before {
        content: '抢购';
        color: #fff;
        font-size: 12px;
        display: block;
        line-height: 21px;
        text-align: center;
    }
}
</style>
