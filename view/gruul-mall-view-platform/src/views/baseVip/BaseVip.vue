<script setup lang="ts">
import SearchForm from '@/views/baseVip/components/search-form.vue'
import BaseVipTable from '@/views/baseVip/components/base-vip-table.vue'
import VipImport from './components/import.vue'
import type { ParamsSearchVipBase } from '@/views/baseVip/types'
import type { Ref } from 'vue'
import request from '@/apis/request'
import { cloneDeep } from 'lodash-es'

let searchParams: ParamsSearchVipBase = {} as ParamsSearchVipBase
const sortType = ref('3')
const searchFromChangeVal = ref(false)
const tableRef: Ref<InstanceType<typeof BaseVipTable> | null> = ref(null)
const importDialogShow = ref(false)
const vipImportRef: Ref<InstanceType<typeof VipImport> | null> = ref(null)

const handleDropdownLeft = () => {
    tableRef.value?.batchGiftsCoupons()
}
const handleConfirmImport = async () => {
    vipImportRef.value?.uploadExcelRef?.submit()
}
const handleDropdownRight = (e: string) => {
    switch (e) {
        case 'Tags':
            openTagePopUp()
            break
        case 'import':
            // TODO 导入逻辑
            importDialogShow.value = true
            break
        case 'export':
            // eslint-disable-next-line no-case-declarations
            const serviceSearchParams: any = cloneDeep(searchParams)
            Object.keys(serviceSearchParams).forEach((key) => {
                if (!serviceSearchParams[key]) delete serviceSearchParams[key]
            })
            request
                .post(
                    'gruul-mall-user/user/export',
                    {
                        ...serviceSearchParams,
                        total: tableRef.value?.pageConfig.total,
                        sortType: 3,
                        ids: tableRef.value?.tableSelectedArr.map((item) => item.id),
                    },
                    {
                        responseType: 'arraybuffer',
                    },
                )
                .then((res) => {
                    const blob = new Blob([res.data], { type: 'application/vnd.ms-excel;charset=utf-8' })
                    const elink = document.createElement('a')
                    elink.href = URL.createObjectURL(blob)
                    elink.download = '批量导出客户.xls'
                    elink.click()
                })
            // TODO 导出逻辑
            break
        case 'Blacklist':
            tableRef.value?.batchBlack()
            break
        default:
            break
    }
}
const openTagePopUp = () => {
    tableRef.value?.openLabelView()
}
const handleSearchData = (e: ParamsSearchVipBase) => {
    searchParams = e
    tableRef.value!.tableSelectedArr = []
    tableRef.value?.initBaseVipList(e)
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SearchForm @search-data="handleSearchData" @change-show="searchFromChangeVal = $event" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="handle_container" style="padding-top: 16px">
        <div style="display: flex; justify-content: space-between">
            <div style="display: flex; justify-content: space-between">
                <div>
                    <el-button @click="handleDropdownLeft">送优惠券</el-button>
                    <el-button @click="handleDropdownRight('Tags')">设置标签</el-button>
                    <el-button @click="handleDropdownRight('Blacklist')">加入黑名单</el-button>
                </div>
            </div>
            <div>
                <el-button @click="handleDropdownRight('import')">导入</el-button>
                <el-button @click="handleDropdownRight('export')">导出</el-button>
            </div>
        </div>
    </div>
    <BaseVipTable ref="tableRef" :sort-type="sortType" :search-from-change-val="searchFromChangeVal" />
    <el-dialog v-model="importDialogShow" title="导入" destroy-on-close>
        <VipImport ref="vipImportRef" @import-success="importDialogShow = false" />
        <template #footer>
            <el-button @click="importDialogShow = false">取 消</el-button>
            <el-button type="primary" @click="handleConfirmImport">确 定</el-button>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(filterSkipVal-box) {
    @include flex(space-between);
    margin: 15px 0;
}
</style>
