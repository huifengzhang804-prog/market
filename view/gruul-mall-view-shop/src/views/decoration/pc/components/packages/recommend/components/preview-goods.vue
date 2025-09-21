<script setup lang="ts">
import { ElMessage } from 'element-plus'
import price from '@/views/decoration/pc/components/price/index.vue'
import { doGetRetrieveProduct } from '@/apis/good'
import type { ApiRetrieveComItemType } from '@/apis/good/model/index'
import type { itemType } from '@/components/q-select-goods/type'

const props = defineProps<{
    style: string
    goods?: itemType[]
}>()

const { divTenThousand } = useConvert()

const tableData = ref<ApiRetrieveComItemType[]>([])

/**
 * 获取商品数据
 */
const getData = async (ids: string[]) => {
    try {
        const { data, code } = await doGetRetrieveProduct({
            current: 1,
            size: 10,
            searchTotalStockGtZero: true,
            ids,
        })

        if (code !== 200) {
            return ElMessage.error('获取商品失败')
        }
        tableData.value = data.list
    } catch (error) {
        console.log('error', error)
    }
}

/**
 * 数据发生变化调用接口
 */
watch(
    () => props,
    () => {
        const { style, goods } = props
        if (goods) {
            const allArr = style === 'row' ? goods.slice(0, 9) : goods.slice(0, 6)
            getData(allArr.map((item) => item.id))
        }
    },
    { immediate: true, deep: true },
)
</script>

<template>
    <div v-if="style === 'row'" class="row">
        <div v-if="tableData.length" class="left">
            <img class="left__img" :src="tableData[0].pic" />

            <div class="left__name">{{ tableData[0].productName }}</div>

            <div class="left__price">
                <price :price="divTenThousand(tableData[0].prices?.[0] ?? '0')" :sale-price="divTenThousand(tableData[0].salePrices[0])" />
            </div>
        </div>
        <div class="right">
            <div v-for="(item, ind) in tableData.slice(1, 9)" :key="item.id" class="right__item">
                <img class="right__item--img" :src="item.pic" />
                <div class="right__item-info">
                    <div class="right__item-info--name">
                        {{ item.productName }}
                    </div>
                    <div class="right__item-info--price">
                        <price :price="divTenThousand(item.prices?.[0] ?? '0')" :sale-price="divTenThousand(item.salePrices[0])" />
                    </div>
                </div>

                <div v-if="ind > 4" class="right__item--line"></div>
            </div>
        </div>
    </div>

    <div v-else class="column">
        <div v-for="item in tableData.slice(0, 6)" :key="item.id" class="left column__item">
            <img class="column__item--img" :src="item.pic" />

            <div class="left__name">{{ item.productName }}</div>

            <div class="column__item--price">
                <price :price="divTenThousand(item.prices?.[0] ?? 0)" :sale-price="divTenThousand(item.salePrices[0])" />
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(row) {
    display: flex;
    background-color: #fff;
    color: #333;
}

@include b(left) {
    width: 202px;
    height: 260px;
    display: flex;
    padding: 0 16px;
    flex-direction: column;
    justify-content: center;
    background-color: #fff;
    text-align: center;
    box-shadow: 1px 0px 4px 0px rgba(0, 0, 0, 0.1);
    margin-right: 1px;

    @include e(img) {
        width: 164px;
        height: 156px;
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
        width: 160px;
        margin: 0 auto;
    }
}

@include b(right) {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    background-color: #fff;
    @include e(item) {
        display: flex;
        position: relative;
        padding: 24px 16px;
        width: 249px;
        height: 130px;

        @include m(img) {
            width: 72px;
            height: 74px;
        }

        @include m(line) {
            position: absolute;
            width: 101px;
            height: 1px;
            background-color: #000000;
            opacity: 0.05;
            left: 50%;
            transform: translateX(-50%);
            top: 0px;
        }
    }
    @include e(item-info) {
        margin-left: 16px;
        @include m(name) {
            height: 38px;
            width: 127.5px;
            margin-bottom: 18px;
            @include utils-ellipsis(2);
        }
    }
}

@include b(column) {
    color: #333;
    display: grid;
    grid-template-columns: repeat(6, 180px);
    grid-column-gap: 24px;
    @include e(item) {
        width: 180px;
        box-shadow: unset;
        @include m(img) {
            width: 148px;
            height: 148px;
            margin-bottom: 12px;
        }

        @include m(price) {
            margin-top: 12px;
            width: 140px;
        }
    }
}
</style>
