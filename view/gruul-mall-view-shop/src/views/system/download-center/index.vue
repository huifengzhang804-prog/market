<template>
    <DownloadCenterSearch @on-search-params="handleSearch" />
    <div class="grey_bar"></div>
    <div class="handle_container pt15">
        <el-button :disabled="selectionDownloadCenterData.length === 0" @click="batchDelete">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="downloadCenterData"
            size="large"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#F7F8FA', height: '48px' }"
            :cell-style="{ color: '#333333', height: '60px' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="30" fixed="left" />
            <el-table-column prop="dataType" label="数据类型" width="200">
                <template #default="{ row }">
                    {{ row.exportDataTypeStr }}
                </template>
            </el-table-column>
            <el-table-column prop="dataCount" label="数据量(条)" />
            <el-table-column prop="fileSize" label="文件大小">
                <template #default="{ row }">
                    {{ getFileSize(row?.fileSize) }}
                </template>
            </el-table-column>
            <el-table-column prop="userPhone" label="导出用户" />
            <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                    <span :class="row?.status">
                        {{ EXPORT_STATUS_ENUM[row?.status as keyof typeof EXPORT_STATUS_ENUM] }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="导出时间" width="180" />
            <el-table-column label="操作" align="right">
                <template #default="{ row }">
                    <el-link
                        v-if="row?.status === 'SUCCESS'"
                        type="primary"
                        @click="
                            (e) => {
                                debounceDownload(row, e)
                            }
                        "
                        >下载</el-link
                    >
                    <el-link v-if="row?.status !== 'PROCESSING'" type="danger" @click="handleDeleteFile(row?.id)">删除</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <PageManage
        :page-size="paginationOptions.size"
        :total="paginationOptions.total"
        :page-num="paginationOptions.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<script lang="ts" setup>
import { doDeleteBatchDownloadCenterFiles, doDeleteDownloadCenterFile, doGetDownloadCenterList } from '@/apis/overview'
import DownloadCenterSearch from './DownloadCenterSearch.vue'
import { DATA_TYPE_ENUM, EXPORT_STATUS_ENUM } from './types'
import Decimal from 'decimal.js'
import PageManage from '@/components/PageManage.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dayjs } from 'element-plus'
import downloadRequest from '@/apis/download-request'
import { debounce } from 'lodash-es'
import { downloadAnimation } from '@/utils/useDownloadAnimation'

const searchOptions = reactive({
    dataType: '' as keyof typeof DATA_TYPE_ENUM | '',
    status: '' as keyof typeof EXPORT_STATUS_ENUM | '',
    exportStartDate: '',
    exportEndDate: '',
    userPhone: '',
})

const paginationOptions = reactive({
    current: 1,
    size: 10,
    total: 0,
})

const handleSearch = (searchCondition: any) => {
    Object.keys(searchOptions).forEach((key) => {
        // @ts-ignore
        searchOptions[key] = searchCondition?.[key] || ''
    })
    if (Array.isArray(searchCondition.exportDate) && searchCondition.exportDate.length) {
        searchOptions.exportStartDate = searchCondition.exportDate[0]
        searchOptions.exportEndDate = searchCondition.exportDate[1]
    }
    initialData()
}
const downloadCenterData = ref<any[]>([])
const selectionDownloadCenterData = ref<any[]>([])
const initialData = async () => {
    let centerData: any[] = [],
        total = 0
    try {
        const { data } = await doGetDownloadCenterList({ ...searchOptions, current: paginationOptions.current, size: paginationOptions.size })
        total = +data?.total
        centerData = data?.records || []
    } finally {
        paginationOptions.total = total
        downloadCenterData.value = centerData
    }
}
initialData()

const getFileSize = (fileSize = 0) => {
    let returnSize = new Decimal(fileSize),
        changeCount = 0
    if (returnSize.toNumber() < 1024) return `${returnSize.toFixed(2)}${getFileSizeUnit(changeCount)}`
    // eslint-disable-next-line no-constant-condition
    while (true) {
        if (returnSize.toNumber() < 1024) break
        changeCount++
        returnSize = returnSize.div(1024)
    }
    return `${returnSize.toFixed(2)}${getFileSizeUnit(changeCount)}`
}
const getFileSizeUnit = (changeCount: number) => {
    const unitStack = ['B', 'KB', 'MB', 'GB', 'TB']
    return unitStack[changeCount]
}

const handleSizeChange = (value: number) => {
    paginationOptions.current = 1
    paginationOptions.size = value
    initialData()
}
const handleCurrentChange = (value: number) => {
    paginationOptions.current = value
    initialData()
}

// 处理选择变化
function handleSelectionChange(selection: any[]) {
    selectionDownloadCenterData.value = selection
}
const handleDownloadFile = async (row: any, e: MouseEvent) => {
    const fileUrl = row?.filePath
    // if (!fileUrl) return
    let fileExtension = ''
    if (!fileUrl) {
        fileExtension = 'xlsx'
    } else {
        const urlObj = new URL(fileUrl)
        const filePath = urlObj.pathname
        // 分割文件路径并获取最后一部分作为文件名
        const fileNameParts = filePath.split('/')
        const fileName = fileNameParts[fileNameParts.length - 1]
        fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1)
    }
    downloadRequest.get(`gruul-mall-overview/export/${row?.id}/download`, { responseType: 'blob' }).then((res) => {
        if (res.data?.type === 'application/json') {
            let reader = new FileReader()
            reader.addEventListener('loadend', () => {
                const { msg } = JSON.parse(reader.result as any)
                ElMessage.error({ message: msg || '下载失败' })
            })
            reader.readAsText(res.data, 'utf-8')
            return
        }
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(new Blob([res.data], { type: 'application/octet-stream' }))
        link.download = `${DATA_TYPE_ENUM[row?.dataType as keyof typeof DATA_TYPE_ENUM]}_${dayjs().format('YYYYMMDD')}.${fileExtension}`
        link.click()
        downloadAnimation(e)
        // 销毁链接元素
        link.parentNode?.removeChild(link)
    })
}

const debounceDownload = debounce(handleDownloadFile, 1000)
const handleDeleteFile = async (id: string) => {
    if (!id) return
    ElMessageBox.confirm('确认删除选中的记录信息').then(async () => {
        const { success, msg } = await doDeleteDownloadCenterFile(id)
        if (success) {
            ElMessage.success({ message: '删除成功' })
            initialData()
        } else {
            ElMessage.error({ message: msg || '删除失败' })
        }
    })
}
const batchDelete = async () => {
    if (!selectionDownloadCenterData.value.length) return ElMessage.error({ message: '请选择记录' })
    ElMessageBox.confirm('确认删除选中的记录信息').then(async () => {
        const { success, msg } = await doDeleteBatchDownloadCenterFiles({ ids: selectionDownloadCenterData.value?.map((item) => item.id) })
        if (success) {
            ElMessage.success({ message: '删除成功' })
            initialData()
        } else {
            ElMessage.error({ message: msg || '删除失败' })
        }
    })
}
</script>

<style lang="scss" scoped>
@include b(download-center) {
    @include e(operator) {
        margin: 8px 0;
    }
}
.el-link + .el-link {
    margin-left: 8px;
}
.PROCESSING {
    color: #fd9224;
}
.SUCCESS {
    color: #00bb2c;
}
.FAILED {
    color: #f54319;
}
</style>
