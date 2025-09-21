<script lang="ts" setup>
import seckillInfo from './components/seckill-info.vue'
import seckillGoods from './components/seckill-goods.vue'
import seckill from './seckill'
import { doGetSeckillRounds, doGetSeckillProductsOfRound } from '@/apis/decoration'
import { SeckillRoundVO, SeckillRoundProductVO } from '@/apis/decoration/type'
import type { PropType } from 'vue'

defineProps({
    formData: {
        type: Object as PropType<typeof seckill>,
        default: seckill,
    },
})
const productPage = ref<Pagination<SeckillRoundProductVO>>({
    pages: 1,
    current: 1,
    size: 5,
    total: 0,
    records: [],
})
//当前活动场次
const round = ref<SeckillRoundVO>()
//当前场次商品列表 前五个
/**
 * 获取秒杀数据
 */

onBeforeMount(initSeckill)

/**
 * 初始化秒杀信息 和商品
 */
async function initSeckill() {
    const { code, data } = await doGetSeckillRounds({ current: 1, size: 1 })
    if (code !== 200 || !data?.records?.length) {
        return
    }
    round.value = data.records[0]
    getSeckillProducts(1)
}

async function getSeckillProducts(current: number) {
    const start = round.value?.time.start
    if (!start) {
        return
    }
    const { code, data } = await doGetSeckillProductsOfRound({
        start,
        current: current,
        size: 5,
    })
    if (code !== 200 || !data?.records?.length) {
        return
    }
    productPage.value = data
}
</script>

<template>
    <div class="seckill">
        <div class="main">
            <!--     秒杀场次   -->
            <seckill-info :name="formData.name" :round="round" />
            <!--   秒杀商品   -->
            <seckill-goods :product-page="productPage" @current-change="getSeckillProducts" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(seckill) {
    height: 260px;
    .main {
        display: flex;
    }
}
</style>
