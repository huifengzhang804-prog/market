<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
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
})
const $emit = defineEmits(['update:link'])
const linkSelectItem = useVModel($props, 'link', $emit)
const selectId = ref('')
const tableData: LinkSelectItem[] = []
watch(
    linkSelectItem,
    (newVal) => {
        selectId.value = newVal.id
    },
    {
        immediate: true,
    },
)
defineExpose({
    selectId,
})

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
                    <span></span>
                </el-radio>
            </template>
        </el-table-column>
    </el-table>
</template>

<style lang="scss" scoped></style>
