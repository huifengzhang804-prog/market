<script setup lang="ts">
import Decimal from 'decimal.js'

const props = defineProps({
    salePrice: {
        type: [String, Decimal],
        default: '',
    },
    price: {
        type: [String, Decimal],
        default: '',
    },
})

const sale = computed(() => {
    const num = +props.salePrice
    return num.toFixed(2).split('.')
})
</script>

<template>
    <div class="line">
        <div class="sale-price">
            <span class="top-4"> ￥ </span>
            <div class="sale-price__number">{{ sale[0] }}<span v-if="sale[1]">.</span></div>

            <div v-if="sale[1]" class="top-6">{{ sale[1] }}</div>
        </div>
        <div class="price">￥{{ price }}</div>
    </div>
</template>

<style lang="scss" scoped>
@include b(line) {
    display: flex;
    font-size: 12px;
    align-items: center;
    flex-wrap: wrap;
}

@include b(sale-price) {
    color: #e00500;
    display: flex;
    @include e(number) {
        font-size: 20px;
        line-height: 20px;
    }
}

@include b(top-4) {
    position: relative;
    top: 4px;
}

@include b(top-6) {
    position: relative;
    top: 6px;
}

@include b(price) {
    color: #8c8c8c;
    text-decoration-line: line-through;
    margin-left: 8px;
    margin-top: 5px;
}
</style>
