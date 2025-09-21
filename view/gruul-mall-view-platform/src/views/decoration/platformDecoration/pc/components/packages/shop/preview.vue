<script setup lang="ts">
import shop from './shop'
import mainTitle from '../../title/index.vue'
import shopItem from './components/shop-item.vue'
import { handleGetGoodList } from './shop'
import type { ShopList } from '@/components/q-select-shop/type'
import type { Shop } from './shop'
import type { PropType } from 'vue'

const props = defineProps({
    formData: {
        type: Object as PropType<Shop>,
        default: shop,
    },
})

watch(
    () => props.formData.list,
    () => {
        tableData.value = props.formData.list
    },
    { deep: true },
)

/**
 * 获取店铺列表
 */
const tableData = ref<ShopList[]>([])
async function getGoodList() {
    tableData.value = await handleGetGoodList(props.formData.list)
}

onBeforeMount(() => {
    getGoodList()
})
</script>

<template>
    <div class="main shop">
        <main-title :main-title="formData.mainTitle" :subtitle="formData.subtitle" />
        <div class="shop__card m-t-16">
            <shopItem v-for="item in tableData" :key="item.id" :item="item" />
        </div>
    </div>
</template>

<style lang="scss" scoped>
@include b(shop) {
    padding-bottom: 24px;
    background-color: #f6f6f6;
    @include e(card) {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        grid-gap: 24px;
    }
}
</style>
