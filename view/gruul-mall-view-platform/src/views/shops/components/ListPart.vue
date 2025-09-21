<template>
    <div class="handle_container">
        <q-dropdown-btn v-if="$prop.currentTabChoose === ' '" title="批量操作" :option="commandList" @right-click="commandChange" />
    </div>
    <ShopTable
        ref="shopTableRef"
        :tab-radio="tabRadio"
        :tableList="tableData"
        :currentTabChoose="$prop.currentTabChoose"
        @getStoreStatus="$emit('getStoreStatus')"
    />
    <BetterPageManage
        v-model:page-size="pageConfig.size"
        v-model:page-num="pageConfig.current"
        v-model:total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import ShopTable from '@/views/shops/components/ShopTable.vue'
import { doGetShopList } from '@/apis/shops'
import { ElMessage } from 'element-plus'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { searchParamType } from '@/views/shops/types'
import { ShopListVO } from '@/apis/shops/types/response'

const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})
const tableData = ref<ShopListVO[]>([])
const shopTableRef = ref()
const commandList = ref([
    {
        label: '删除',
        name: 'Delete',
    },
    {
        label: '启用',
        name: 'NORMAL',
    },
    {
        label: '禁用',
        name: 'FORBIDDEN',
    },
])
const $prop = defineProps({
    searchParams: {
        type: Object as PropType<searchParamType>,
        default: () => {},
    },
    currentTabChoose: {
        type: String,
        required: true,
    },
    tabRadio: {
        type: String,
        default: () => 'storeList',
    },
})
const $emit = defineEmits(['getStoreStatus'])
watch(
    () => $prop.currentTabChoose,
    (val) => {
        switch (val) {
            case 'REJECT':
                commandList.value = [{ label: 'Delete', name: '删除' }]
                break
            case 'UNDER_REVIEW':
                commandList.value = [{ label: 'refusedTo', name: '拒绝' }]
                break
            default:
                commandList.value = [
                    {
                        name: 'Delete',
                        label: '删除',
                    },
                    {
                        name: 'NORMAL',
                        label: '启用',
                    },
                    {
                        name: 'FORBIDDEN',
                        label: '禁用',
                    },
                ]
                break
        }
    },
    { immediate: true },
)
initList($prop.searchParams)

defineExpose({
    initList,
})
async function initList(param: searchParamType) {
    const params = Object.assign(param, pageConfig)
    delete params.data
    delete params.applyOfData
    const { data } = await doGetShopList({ ...params, settledWay: $prop.tabRadio === 'storeReview' ? 'APPLY' : undefined })
    tableData.value = data.records.sort((a, b) => {
        return a.status === 'NORMAL' ? -1 : 1
    })
    pageConfig.current = +data.current
    pageConfig.total = +data.total
    pageConfig.size = +data.size
}
const commandChange = (e: string) => {
    switch (e) {
        case 'Delete':
            batchDeleteShops()
            break
        case 'NORMAL':
            batchChangeShops('NORMAL')
            break
        case 'FORBIDDEN':
            batchChangeShops('FORBIDDEN')
            break
        default:
            batchChangeShops('refusedTo')
            break
    }
}
const batchDeleteShops = () => {
    shopTableRef.value.batchDeleteShop()
}
/**
 * 批量启用禁用
 * @param {boolean} status
 */
const batchChangeShops = async (status: string) => {
    if ($prop.currentTabChoose === 'REJECT') {
        return ElMessage.error('该商户已被拒绝')
    }
    const res = await shopTableRef.value.batchChangeStatus(status)
    if (!res) return
    initList(Object.assign($prop.searchParams, { status: $prop.currentTabChoose }))
}

const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    initList(Object.assign($prop.searchParams))
}
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    initList(Object.assign($prop.searchParams))
}
</script>

<style lang="scss" scoped>
@include b(btns) {
    @include flex;
    justify-content: flex-start;
    background: white;
    padding-left: 16px;
    padding-right: 16px;
    padding-bottom: 16px;
}
@include b(down) {
    height: 18px;
}
.mr-20 {
    margin-right: 20px;
}
.mb-15 {
    margin-bottom: 15px;
}
@include b(group) {
    position: relative;
    @include e(placeholder) {
        position: absolute;
        right: -2px;
        z-index: 999;
        &:hover {
            color: #337ecc !important;
        }
    }
}
@include b(text-center) {
    margin-bottom: 5px;
}
</style>
