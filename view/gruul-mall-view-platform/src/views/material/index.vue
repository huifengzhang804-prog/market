<script lang="ts" setup>
import Category from './category.vue'
import MaterialList from './material-list.vue'
import Search from './components/search.vue'

const classificationId = ref('')
const classificationIdVal = (val: string) => {
    classificationId.value = val
}
const freshKey = ref(0)
const categoryListFn = () => {
    freshKey.value = Date.now()
}
const material = ref()
</script>
<template>
    <Search :classification-id="classificationId" @search="material?.handleSearch" @changeShow="material?.changeTableHeight" />
    <div class="grey_bar"></div>
    <div class="material f1">
        <Category :key="freshKey" class="material__category" @classification-id-val="classificationIdVal" />
        <MaterialList ref="material" :classification-id="classificationId" @category-list-fn="categoryListFn" />
    </div>
</template>

<style lang="scss" scoped>
@include b(material) {
    display: flex;
    overflow-y: scroll;
    @include e(category) {
        flex-shrink: 0;
        overflow: hidden;
    }
}
</style>
