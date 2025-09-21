<script setup lang="ts">
import mainTitle from '../../title/index.vue'
import goodsItem from './components/goods-item.vue'
import { ElMessage } from 'element-plus'
import { doGetRetrieveProduct } from '@/apis/good'
import type { ApiRetrieveComItemType } from '@/apis/good/model/index'
import goods from './goods'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<typeof goods>,
        default: goods,
    },
})

const goodsId = computed(() => {
    return props.formData.list.map((item) => item.id)
})

const goodsList = ref<ApiRetrieveComItemType[]>([])

// 分页配置
const pageConfig = reactive({
    current: 1,
    size: 20,
    total: 0,
})

const getGoodList = async () => {
    try {
        const { data, code } = await doGetRetrieveProduct({
            ...pageConfig,
            ids: goodsId.value,
            searchTotalStockGtZero: true,
            showCoupon: true,
        })

        if (code !== 200) {
            return ElMessage.error('获取商品失败')
        }

        const { list, total, pageNum, pageSize } = data
        goodsList.value = list
        pageConfig.total = total
        pageConfig.current = pageNum
        pageConfig.size = pageSize
    } catch (error) {
        console.log('error', error)
    }
}

watch(goodsId, getGoodList, { deep: true, immediate: true })

/**
 * 条数发生变化
 */
const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    getGoodList()
}

/**
 * 当前页发生变化
 */
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    getGoodList()
}
</script>

<template>
    <div class="main goods-main">
        <main-title :main-title="formData.mainTitle" :subtitle="formData.subtitle" />

        <div class="goods m-t-16">
            <goods-item v-for="item in goodsList" :key="item.id" :item="item" />
        </div>

        <div class="page">
            <page-manage
                :page-size="pageConfig.size"
                :page-num="pageConfig.current"
                :total="pageConfig.total"
                @handle-size-change="handleSizeChange"
                @handle-current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(page) {
    display: flex;
    justify-content: center;
}

@include b(goods) {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    grid-gap: 24px;
}

@include b(goods-main) {
    background: #f6f6f6;
}
</style>
