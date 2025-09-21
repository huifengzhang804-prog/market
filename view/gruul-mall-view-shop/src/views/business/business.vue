<script setup lang="ts">
const activeName = ref('business')
const asyncComponents = {
    business: defineAsyncComponent(() => import('./components/business.vue')),
    changeShop: defineAsyncComponent(() => import('./components/changeShop.vue')),
}
</script>

<template>
    <el-tabs v-model="activeName" class="tab_container">
        <el-tab-pane label="账户管理" name="business" />
        <el-tab-pane label="切换店铺" name="changeShop" />
    </el-tabs>
    <div class="content_container">
        <component :is="asyncComponents[activeName as keyof typeof asyncComponents]"></component>
    </div>
</template>

<style scoped lang="scss"></style>
