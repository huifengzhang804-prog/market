<script setup lang="ts">
import type { PropType } from 'vue'
import { useVModel } from '@vueuse/core'
import { typeNameMap } from '../linkSelectItem'
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
const tableData: LinkSelectItem[] = [
    {
        id: '9',
        type: 2,
        name: '分销中心',
        url: '/pluginPackage/distribute/DistributorBackground',
        append: '',
    },
    {
        id: '10',
        type: 2,
        name: '会员中心',
        url: '/pluginPackage/member/views/MemberCenter',
        append: '',
    },
    {
        id: '11',
        type: 2,
        name: '领券中心',
        url: '/pluginPackage/coupon/couponCenter/CouponsCenter',
        append: '',
    },
    {
        id: '12',
        type: 2,
        name: '我的优惠券',
        url: '/pluginPackage/coupon/myCoupon/MyCoupon',
        append: '',
    },
    {
        id: '13',
        type: 2,
        name: '积分商城',
        url: '/pluginPackage/integral/mall/Index',
        append: '',
    },
]
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

const handleSelect = (id: string) => {
    const currentItem = tableData.find((item) => item.id === id)
    Object.assign(linkSelectItem.value, currentItem)
}
</script>

<template>
    <el-table :data="tableData" height="369">
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column label="页面名称" prop="name"></el-table-column>
        <el-table-column label="操作" width="100px">
            <template #default="scope">
                <el-radio v-model="selectId" :value="scope.row.id" @click="handleSelect(scope.row.id)">
                    <span></span>
                </el-radio>
            </template>
        </el-table-column>
    </el-table>
</template>

<style lang="scss" scoped></style>
