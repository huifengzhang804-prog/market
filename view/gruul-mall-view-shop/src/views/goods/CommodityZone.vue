<template>
    <el-table ref="multipleTableRef" :data="tableList.records" style="width: 100%" @selection-change="handleSelectionChange">
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column type="selection" width="55" />
        <el-table-column label="Date" width="120">
            <template #default="scope">{{ scope.row.createTime }}</template>
        </el-table-column>
        <el-table-column property="name" label="Name" width="120" />
        <el-table-column label="图片" width="120">
            <template #default="scope">
                <el-image style="width: 100px; height: 100px" :src="scope.row.albumPics" fit="fit" />
            </template>
        </el-table-column>
    </el-table>
</template>

<script lang="ts" setup>
import type { ElTable } from 'element-plus'
import { get } from '../../apis/http'

interface User {
    date: string
    name: string
    address: string
}
let tableList = shallowReactive({ records: [] })

const multipleTableRef = ref<InstanceType<typeof ElTable>>()
const multipleSelection = ref<User[]>([])
const toggleSelection = (rows?: User[]) => {
    if (rows) {
        rows.forEach((row) => {
            // TODO: improvement typing when refactor table
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            multipleTableRef.value?.toggleRowSelection(row, undefined)
        })
    } else {
        multipleTableRef.value?.clearSelection()
    }
}
const handleSelectionChange = (val: User[]) => {
    multipleSelection.value = val
}

/**
 * 初始化列表方法
 */

const initList = () => {
    get({
        url: '/gruul-mall-goods/manager/product/list',
        params: {
            saleMode: 1,
            place: 0,
        },
    }).then((res) => {
        // tableList=res.data.records
        // tableList=reactive(res.data.records)
        // console.log('tableList',tableList)
        // tableList.concat(res.data.records)
        tableList.records = res.data.records
    })
}

initList()
</script>

<style lang="scss" scoped>
@import '@/assets/css/goods/goods.scss';
</style>
