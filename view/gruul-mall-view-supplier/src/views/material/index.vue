<script lang="ts" setup>
import Category from './category.vue'
import MaterialList from './material-list.vue'
import Search from './components/search.vue'
import { SearchType } from './hooks/useMaterialListHooks'

const categoryId = ref<string>(' ')
const freshKey = ref(0)
const categoryListFn = () => {
    freshKey.value = Date.now()
}

const MaterialListRef = ref()
const handleSearch = (e: SearchType) => {
    categoryId.value = e.categoryId
    MaterialListRef.value.searchCondition = e
    MaterialListRef.value.initialList()
}
const nodeClick = () => {
    MaterialListRef.value.searchCondition.categoryId = categoryId.value
    MaterialListRef.value.currentFormModel.parentId = categoryId.value
    MaterialListRef.value.initialList()
}
</script>
<template>
    <Search @onSearchParams="handleSearch" />
    <div class="grey_bar"></div>
    <div class="material f1">
        <Category :key="freshKey" v-model:categoryId="categoryId" class="material__category" @nodeClick="nodeClick" />
        <MaterialList ref="MaterialListRef" @categoryListFn="categoryListFn" />
    </div>
</template>

<style lang="scss" scoped>
@include b(material) {
    display: flex;
    overflow-y: scroll;
    @include e(category) {
        flex-shrink: 0;
        width: 210px;
        height: calc(100% - 30px);
        overflow: auto;
        margin: 15px 23px 0 15px;
        border-radius: 5px;
    }
}
</style>
