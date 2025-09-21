<template>
    <el-config-provider :empty-values="[undefined, null]">
        <ShopListSearch v-model="searchParams" :is-add-shop="true" @search-params="searchHandle" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <ListPart ref="listPartRef" :search-params="searchParams" :current-tab-choose="tabChoose" @get-store-status="getStoreStatus" />
</template>

<script lang="ts" setup>
import ShopListSearch from './components/ShopListSearch.vue'
import ListPart from './components/ListPart.vue'
import { storeStatusList } from '@/apis/shops'
import { searchParamType, searchParamStatus } from '@/views/shops/types'

const listPartRef = ref()
const $route = useRoute()
type SearchParamType = Record<'no' | 'name' | 'status', string>
const searchParams = ref<searchParamType>({
    no: '',
    name: '',
    status: ($route.query.name as searchParamStatus | undefined) || '',
    shopType: '',
    extractionType: '',
    shopModes: '',
    settledStartTime: undefined,
    settledEndTime: undefined,
})
const storeStatusNun = ref('')

onMounted(() => {
    getStoreStatus()
})

const getStoreStatus = async () => {
    const data = await storeStatusList({ ...searchParams.value, status: '' })
    storeStatusNun.value = data.data
}
const tabChoose = ref<string>(($route.query.name as string | undefined) || '')

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
    listPartRef.value?.initList(paramsCopy)
}
</script>

<style lang="scss" scoped></style>
