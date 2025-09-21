<template>
    <Search @search="handleSearch" @export="handleExport" />
    <div class="grey_bar"></div>
    <div class="table_container overh">
        <el-table
            :data="memberRecordsData"
            :header-cell-style="{
                'background-color': '#F6F8FA',
                'font-weight': 'normal',
                color: '#515151',
                fontSize: '14px',
            }"
            size="large"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="55" fixed="left" />
            <el-table-column label="订单号" prop="no" width="200" />
            <el-table-column label="用户昵称" prop="userNickName" width="100" />
            <el-table-column label="手机号" prop="userPhone" width="120" />
            <el-table-column label="会员等级" width="100">
                <template #default="{ row }"> SVIP{{ row?.rankCode }} </template>
            </el-table-column>
            <el-table-column label="有效期" prop="effectiveStr" />
            <el-table-column label="支付金额" width="100">
                <template #default="{ row }">{{ divTenThousand(row?.payAmount || 0) }}</template>
            </el-table-column>
            <el-table-column label="支付方式" width="100">
                <template #default="{ row }">{{ payTypeFn(row?.payType) }}</template>
            </el-table-column>
            <el-table-column label="类型" width="130" prop="memberPurchaseTypeStr" />
            <el-table-column label="购买时间" prop="createTime" width="110">
                <template #header>
                    <span style="margin-right: 5px">购买时间</span>
                </template>
                <template #default="{ row }">
                    {{ dayjs(row?.createTime).format('YYYY-MM-DD') }}
                </template>
            </el-table-column>
            <el-table-column label="到期时间" prop="expireTime" width="110">
                <template #default="{ row }">
                    {{ dayjs(row?.expireTime).format('YYYY-MM-DD') }}
                </template>
            </el-table-column>
        </el-table>
    </div>
    <PageManage
        :page-size="pagination.pageSize"
        :page-num="pagination.current"
        :total="pagination.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<script lang="ts" setup>
import { doGetMemberPurchaseList, doPostExportMemberPurchaseList } from '@/apis/vip'
import Search from './search.vue'
import PageManage from '@/components/PageManage.vue'

import { payTypeFn } from '@/composables/usePaymentCn'
import { ElMessage, dayjs } from 'element-plus'

const { divTenThousand } = useConvert()
const memberRecordsData = ref<any[]>([])
const selectedRecordsData = ref<any[]>([])
const searchData = reactive({
    no: '',
    nickName: '',
    userPhone: '',
    level: '',
    buyStartTime: '',
    buyEndTime: '',
    expireStartTime: '',
    expireEndTime: '',
})
const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
})
const handleCurrentChange = (current: number) => {
    pagination.current = current
    initialData()
}
const handleSizeChange = (pageSize: number) => {
    pagination.pageSize = pageSize
    pagination.current = 1
    initialData()
}
const handleSelectionChange = (selectedData: any[]) => {
    selectedRecordsData.value = selectedData
}
const handleSearch = (searchOptions: typeof searchData) => {
    Object.keys(searchData).forEach((key) => {
        // @ts-ignore
        searchData[key] = searchOptions[key] || ''
    })
    initialData()
}
const initialData = async () => {
    let listData: any[] = [],
        total = 0
    try {
        const { data } = await doGetMemberPurchaseList({ ...searchData, current: pagination.current, size: pagination.pageSize })
        listData = data?.records || []
        total = +data?.total
    } finally {
        memberRecordsData.value = listData
        pagination.total = total
    }
}

const handleExport = async () => {
    let params: any = {}
    if (selectedRecordsData.value?.length) {
        params.exportIds = selectedRecordsData.value?.map((item) => item.id)
    } else {
        params = searchData
    }
    const { code, msg } = await doPostExportMemberPurchaseList(params)
    if (code === 200) {
        ElMessage.success({ message: msg || '导出成功' })
    } else {
        ElMessage.error({ message: msg || '导出失败' })
    }
}

initialData()
</script>
