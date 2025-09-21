<script setup lang="ts">
import { useVModel } from '@vueuse/core'
import { navBarDefaultData } from '@/components/link-select/linkSelectItem'
import type { LinkSelectItem } from '../linkSelectItem'
import type { PropType } from 'vue'

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
let tableData = shallowReactive([
    ...navBarDefaultData,
    {
        id: '7',
        type: 0,
        name: '地址管理',
        url: '/basePackage/pages/addressManage/AddressManage',
        append: '',
    },
    {
        id: '8',
        type: 0,
        name: '设置',
        url: '/basePackage/pages/editUserInfo/EditUserInfo',
        append: '',
    },
    {
        id: '9',
        type: 0,
        name: '关于',
        url: '/basePackage/pages/aboutUs/AboutUs',
        append: '',
    },
    {
        id: '10',
        type: 0,
        name: '商户申请',
        url: '/basePackage/pages/applyMerchant/ApplyMerchant',
        append: '',
    },
    {
        id: '10_',
        type: 0,
        name: '供应商申请',
        url: '/basePackage/pages/applyFournisseurs/applyFournisseurs',
        append: '',
    },
    {
        id: '11',
        type: 0,
        name: '拼团活动',
        url: '/pluginPackage/group/views/GroupList',
        append: '',
    },
    {
        id: '12',
        type: 0,
        name: '评价中心',
        url: '/pluginPackage/order/appraise/Appraise',
        append: '',
    },
    {
        id: '13',
        type: 0,
        name: '平台客服',
        url: '/basePackage/pages/customerService/CustomerService?shopName=平台客服',
        append: '',
    },
    {
        id: '14',
        type: 0,
        name: '金刚区分类',
        url: '/basePackage/pages/diamondClassification/diamondClassification',
        append: '',
    },
    {
        id: '15',
        type: 0,
        name: 'APP直播用户端(小程序、APP端生效)',
        url: '/pluginPackage/liveModule/views/FeaturedLive/Index',
        append: '',
    },
    {
        id: '16',
        type: 0,
        name: 'APP直播主播端(仅在APP端生效)',
        url: '/pluginPackage/liveModule/views/CreateLive/Index',
        append: '',
    },
    {
        id: '17',
        type: 0,
        name: '提现账号',
        url: '/basePackage/pages/accountManagement/accountManagement',
        append: '',
    },
])

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
                <el-radio v-model="selectId" :value="scope.row.id" @change="handleSelect">
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
