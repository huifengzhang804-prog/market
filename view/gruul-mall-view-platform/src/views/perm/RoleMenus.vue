<template>
    <div style="width: 100%; overflow-y: auto">
        <el-tree
            v-show="showMenus"
            ref="menuTree"
            :data="menus"
            show-checkbox
            node-key="id"
            :props="{
                children: 'children',
                label: 'name',
            }"
            @check="checkChanged"
        />
        <el-skeleton v-if="!showMenus" animated />
    </div>
</template>
<script lang="ts" setup>
import { getAllMenus } from '@/apis/perm'
import { MenuType } from '@/apis/perm/types'
import { ElTree } from 'element-plus'
import type { TreeKey } from 'element-plus/es/components/tree/src/tree.type.mjs'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
    modelValue: {
        type: Array,
        default() {
            return []
        },
    },
})
const menuTree = ref<InstanceType<typeof ElTree> | null>(null)
const menus = ref<MenuType[]>([])
const showMenus = computed(() => {
    const value = menus.value
    return !!value && value.length > 0
})
const checkedMenuIds = ref<TreeKey[]>([])
const checkChanged = () => {
    checkedMenuIds.value = menuTree.value!.getCheckedKeys(true)
    emits('update:modelValue', checkedMenuIds.value)
}
watch(
    () => props.modelValue,
    (value) => {
        menuTree.value!.setCheckedKeys(value as TreeKey[])
    },
)
onMounted(() => {
    getAllMenus().then((response) => {
        menus.value = response.data as unknown as MenuType[]
    })
    nextTick(() => {
        menuTree.value!.setCheckedKeys(props.modelValue as TreeKey[])
    })
})

const handleCheckAll = () => {
    // 设置所有节点为选中状态
    menuTree.value?.setCheckedNodes(menus.value as any)
    // 获取所有选中的菜单ID
    checkedMenuIds.value = menuTree.value!.getCheckedKeys(true)
}

// 取消全选方法
const handleUncheckAll = () => {
    // 清空所有选中状态
    menuTree.value?.setCheckedNodes([])
    checkedMenuIds.value = []
}

defineExpose({
    checkedMenuIds,
    handleCheckAll,
    handleUncheckAll,
})
</script>
<style>
.el-tree-node__content {
    height: 40px;
}
</style>
