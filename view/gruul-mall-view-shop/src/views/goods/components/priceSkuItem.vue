<template>
    <div class="item">
        <el-row
            :gutter="20"
            justify="space-around"
            style="background-color: #eee; height: 40px; line-height: 40px; margin-left: 0px; margin-right: 0px"
        >
            <el-col :span="12">规格</el-col>
            <el-col :span="5">划线价</el-col>
            <el-col :span="5">销售价</el-col>
        </el-row>
        <el-scrollbar height="400px">
            <el-row v-for="item in itemSku" :key="item.id" :gutter="20" justify="space-around" style="margin-left: 0px; margin-right: 0px">
                <el-col :span="12">{{ item.specs?.join(' ') || '单规格商品' }}</el-col>
                <el-col :span="5"
                    ><el-input-number v-model="item.price" :max="999999" :min="0.01" :precision="2" type="text" :controls="false"
                /></el-col>
                <el-col :span="5"
                    ><el-input-number v-model="item.salePrice" :max="999999" :min="0.01" :precision="2" type="text" :controls="false"
                /></el-col>
            </el-row>
        </el-scrollbar>
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type { CommoditySpecTable } from '@/views/goods/types/index'
const props = defineProps({
    skus: {
        type: Array as PropType<CommoditySpecTable[]>,
        default: () => [],
    },
})
const itemSku = ref(props.skus)
const setALLskyPrice = (key: 'price' | 'salePrice', price: string | number) => {
    itemSku.value.forEach((item: CommoditySpecTable) => {
        item[key] = price
    })
}
defineExpose({
    setALLskyPrice,
    itemSku,
})
</script>

<style lang="scss" scoped>
.item {
    margin-top: 25px;
    .el-col {
        text-align: center;
    }
}
.name {
    width: 80px;
    text-align: center;
}
.el-row {
    margin-bottom: 8px;
}
</style>
