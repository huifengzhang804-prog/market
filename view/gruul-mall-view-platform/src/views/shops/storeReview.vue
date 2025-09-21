<template>
    <el-config-provider :empty-values="[undefined, null]">
        <ShopListSearch @search-params="searchHandle" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="tab_container">
        <el-tabs v-model="tabChoose" class="tabs" @tab-change="tabChangeHandle">
            <el-tab-pane :label="`待审核(${storeStatusNun})`" name="UNDER_REVIEW"></el-tab-pane>
            <el-tab-pane label="已通过" name=""></el-tab-pane>
            <el-tab-pane label="已拒绝" name="REJECT"></el-tab-pane>
        </el-tabs>
    </div>
    <ListPart
        ref="listPartRef"
        :tab-radio="tabRadio"
        :search-params="searchParams"
        :current-tab-choose="tabChoose"
        @get-store-status="getStoreStatus"
    />
</template>

<script lang="ts" setup>
import ShopListSearch from './components/ShopListSearch.vue'
import ListPart from './components/ListPart.vue'
import { storeStatusList } from '@/apis/shops'
import { searchParamType, searchParamStatus } from '@/views/shops/types'

defineProps({
    tabRadio: {
        type: String,
        default: () => 'storeList',
    },
})
const status = ref()
const listPartRef = ref()
const $route = useRoute()
type SearchParamType = Record<'no' | 'name' | 'status', string>
const searchParams = ref<searchParamType>({
    no: '',
    name: '',
    status: 'UNDER_REVIEW',
    shopModes: '',
    applyUserPhone: '',
    auditUserPhone: '',
    settledStartTime: undefined,
    settledEndTime: undefined,
    applyStartTime: undefined,
    applyEndTime: undefined,
    queryAuditInfo: true,
})
const storeStatusNun = ref('')

onMounted(() => {
    getStoreStatus()
})

const getStoreStatus = async () => {
    const data = await storeStatusList({ ...searchParams.value, status: 'UNDER_REVIEW' })
    storeStatusNun.value = data.data
}
const tabChoose = ref<string>(($route.query.name as string | undefined) || 'UNDER_REVIEW')
const tabChangeHandle = () => {
    status.value = tabChoose.value
    getStoreStatus()
    listPartRef.value.initList({ status: tabChoose.value, queryAuditInfo: true })
}

/**
 * 改变tabs切换并重新请求对应数据
 * @param {*} status
 */
const changeTabsEvent = (status: string) => {
    if (status.trim()) {
        tabChoose.value = status
    }
    listPartRef.value.initList({ status })
}
provide('parentTabChangeHandle', changeTabsEvent)
provide('parentTabChoose', tabChoose)
const searchHandle = async (params: SearchParamType) => {
    let paramsCopy = JSON.parse(JSON.stringify(params))
    if (paramsCopy.shopModes === '') {
        paramsCopy.shopModes = 'COMMON,O2O'
    }
    if (tabChoose.value !== '') {
        listPartRef.value.initList({ ...paramsCopy, status: tabChoose.value })
        return
    }
    listPartRef.value.initList(paramsCopy)
}
</script>

<style lang="scss" scoped></style>
