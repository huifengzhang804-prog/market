<script setup lang="ts">
import type { PropType } from 'vue'
import { ElMessage } from 'element-plus'
import NavigationItem from './navigation-item.vue'
import { useVModel } from '@vueuse/core'
import defaultNavData, { defaultNavItem } from './navigation'

const $props = defineProps({
    formData: {
        type: Object as PropType<typeof defaultNavData>,
        default() {
            return defaultNavData
        },
    },
})
const $emit = defineEmits(['update:formData'])
const formData = useVModel($props, 'formData', $emit)
const dragStartIndex = ref(-1)

const handelAddNav = () => {
    if (formData.value.navigationList.length >= 10) {
        ElMessage.warning('最多可添加10个')
        return
    }
    const cloneDefaultNavItem = JSON.parse(JSON.stringify(defaultNavItem))
    formData.value.navigationList.push(cloneDefaultNavItem)
}
const handleDelNavItem = (index: number) => {
    formData.value.navigationList.splice(index, 1)
}
const handleDragstart = (i: number) => {
    dragStartIndex.value = i
}
const handleDragover = (e: DragEvent) => {
    e.preventDefault()
}
const handleDrop = (i: number) => {
    const dragStartIndexVal = dragStartIndex.value
    if (dragStartIndexVal === i) {
        return false
    }
    const temp = formData.value.navigationList.splice(dragStartIndexVal, 1, formData.value.navigationList[i])
    formData.value.navigationList.splice(i, 1, ...temp)
}
</script>

<template>
    <div>
        <el-form :model="formData" label-width="80px">
            <el-form-item label="金刚区">最多可添加10个，最少添加4个</el-form-item>
        </el-form>
        <div
            v-for="(storeNavigationItem, index) in formData.navigationList"
            :key="storeNavigationItem.id"
            :draggable="true"
            @dragstart="handleDragstart(index)"
            @dragover="handleDragover"
            @drop="handleDrop(index)"
        >
            <navigation-item :key="index" :item-index="index" :form-data="storeNavigationItem" @on-delect="handleDelNavItem(index)" />
        </div>

        <el-row v-if="formData.navigationList.length < 10" type="flex" align="middle" justify="space-between">
            <el-button style="margin-bottom: 40px" @click="handelAddNav">添加导航</el-button>
            <!-- <div>上下拖动可排序</div> -->
        </el-row>
    </div>
</template>

<style lang="scss" scoped></style>
