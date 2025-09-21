<script setup lang="ts">
import { ShopMode } from '@/apis/afs/type'
import { useShopInfoStore } from '@/store/modules/shopInfo'

const { shopMode } = useShopInfoStore().getterShopInfo
console.log('店铺信息', useShopInfoStore().getterShopInfo)
const activeName = ref<keyof typeof ShopMode>(shopMode)
const asyncComponentReactive = {
    COMMON: defineAsyncComponent(() => import('@/views/decoration/chooseType/onLine.vue')),
    SUPPLIER: defineAsyncComponent(() => import('@/views/decoration/chooseType/onLine.vue')),
    O2O: defineAsyncComponent(() => import('@/views/decoration/chooseType/o2o.vue')),
}
</script>

<template>
    <!-- <el-tabs v-model="activeName" class="tab_container">
        <el-tab-pane v-if="shopMode === 'COMMON'" label="线上店铺装修" name="COMMON"></el-tab-pane>
        <el-tab-pane v-else-if="shopMode === 'SUPPLIER'" label="线上店铺装修" name="SUPPLIER"></el-tab-pane>
        <el-tab-pane v-else label="O2O店铺装修" name="O2O"></el-tab-pane>
    </el-tabs> -->
    <component :is="asyncComponentReactive[activeName]"></component>
</template>

<style lang="scss" scoped></style>
