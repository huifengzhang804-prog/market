<script lang="ts" setup>
import price from '@/views/decoration/platformDecoration/pc/components/price/index.vue'
import { ElCarousel } from 'element-plus'
import { SeckillRoundProductVO } from '@/apis/decoration/type'
import { PropType } from 'vue'

const props = defineProps({
    productPage: {
        type: Object as PropType<Pagination<SeckillRoundProductVO>>,
        required: true,
    },
})
const emits = defineEmits(['currentChange'])

const { divTenThousand } = useConvert()
</script>

<template>
    <div v-if="productPage.total" class="parent">
        <div class="more cp">
            查看更多
            <q-icon name="icon-chevron-right" style="top: 1.3px"></q-icon>
        </div>

        <el-carousel
            ref="carouselRef"
            :autoplay="false"
            :loop="false"
            height="260px"
            indicator-position="none"
            style="min-width: 1010px"
            @change="(current) => emits('currentChange', current + 1)"
        >
            <el-carousel-item v-for="page in productPage.pages" :key="page">
                <div v-if="productPage.current === page" class="item">
                    <div v-for="(product, ind) in productPage.records" :key="ind" class="goods">
                        <img :src="product.productImage" class="goods__img" />

                        <div class="goods__name">{{ product.productName }}</div>

                        <div class="goods__price">
                            <price :price="divTenThousand(product.price)" :sale-price="divTenThousand(product.minPrice)" />
                        </div>

                        <!-- 分割线 -->
                        <div class="goods__line"></div>
                    </div>
                </div>
                <div v-else class="parent un-seckill">暂无商品中~</div>
            </el-carousel-item>
        </el-carousel>
    </div>

    <div v-else class="parent un-seckill">暂无活动~</div>
</template>

<style lang="scss" scoped>
@include b(goods) {
    position: relative;
    width: 202px;
    height: 100%;
    padding: 30px 16px 16px;
    background-color: #fff;
    text-align: center;
    @include e(img) {
        width: 146px;
        height: 140px;
        margin-bottom: 8px;
    }

    @include e(name) {
        font-size: 12px;
        text-align: justify;
        height: 34px;
        margin-bottom: 10px;

        @include utils-ellipsis(2);
    }

    @include e(price) {
        width: 130px;
        margin: 0 auto;
    }

    @include e(line) {
        position: absolute;
        top: 50%;
        right: 0;
        transform: translateY(-50%);
        height: 101px;
        width: 1px;
        background-color: #000000;
        opacity: 0.05;
    }
}

@include b(parent) {
    position: relative;
    background-color: #fff;
    height: 260px;
    width: 1010px;
}

@include b(arrow) {
    top: 50%;
    transform: translateY(-50%);
    position: absolute;
    z-index: 4;
    cursor: pointer;
    font-size: 40px;
    opacity: 0.5;
}

@include b(pre) {
    left: -9px;
}

@include b(next) {
    right: -8px;
}

@include b(more) {
    position: absolute;
    color: #8c8c8c;
    font-size: 12px;
    z-index: 2;
    right: 16px;
    top: 9px;
}

@include b(item) {
    display: flex;
}

@include b(un-seckill) {
    background-image: url('@/assets/image/decoration/un-seckill.png');
    background-position: center 30px;
    background-repeat: no-repeat;
    line-height: 420px;
    font-size: 16px;
    color: #999999;
    text-align: center;
}
</style>
