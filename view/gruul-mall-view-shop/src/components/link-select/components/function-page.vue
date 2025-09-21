<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { navBarDefaultData } from '../linkSelectItem'
import type { LinkSelectItem } from '../linkSelectItem'

const $props = defineProps({
    link: {
        type: Object as PropType<LinkSelectItem>,
        default() {
            return {
                id: null,
                type: null,
                name: '',
                url: '',
                append: '',
            }
        },
    },
    visible: {
        type: Boolean,
        default: false,
    },
    noProTab: {
        type: Boolean,
        default: false,
    },
    limitProTab: {
        type: Boolean,
        default: false,
    },
})
const $emit = defineEmits(['update:link'])
const linkSelectItem = useVModel($props, 'link', $emit)
const selectId = ref()
const $shopInfoStore = useShopInfoStore()
let tableData = shallowReactive([
    ...navBarDefaultData,
    {
        id: 7,
        type: 0,
        name: '地址管理',
        url: '/basePackage/pages/addressManage/AddressManage',
        append: '',
        shopId: $shopInfoStore.getterShopInfo.id,
    },
])
defineExpose({
    selectId,
})

watch(
    linkSelectItem,
    (newVal) => {
        selectId.value = newVal.id
    },
    {
        immediate: true,
    },
)

const handleSelect = () => {
    const currentItem = tableData.find((item) => item.id === selectId.value)
    Object.assign(linkSelectItem.value, currentItem)
}
</script>

<template>
    <el-table :data="tableData" height="369">
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column label="页面名称" prop="name"></el-table-column>
        <el-table-column label="操作" width="100px">
            <template #default="scope">
                <el-radio v-model="selectId" :label="scope.row.id" @change="handleSelect">
                    <div></div>
                </el-radio>
            </template>
        </el-table-column>
    </el-table>
</template>

<style lang="scss" scoped>
.search-wrap {
    display: flex;
    justify-content: space-between;
    justify-items: center;
}
.search-wrap-input {
    width: 180px;
}
</style>
