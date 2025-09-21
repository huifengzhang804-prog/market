<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { doGetLiveMemberList, doDelDeleteLiveUser } from '@/q-plugin/liveStream/apis'
import { role } from '@/q-plugin/liveStream/views'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ApiRole } from '@/q-plugin/liveStream/views/components/live-member/types'

const searchParams = ref({
    keywords: '',
})
const roleList = ref<ApiRole[]>([])
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const chooseList = ref<ApiRole[]>([])
const columns = [
    {
        label: '关键字',
        labelWidth: 60,
        prop: 'keywords',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关键字',
        },
    },
]

/**
 * 批量删除
 */

// 处理选择变化
function handleSelectionChange(selection: ApiRole[]) {
    chooseList.value = selection
}
const handleBatchDel = async () => {
    if (!chooseList.value.length) {
        ElMessage.info('请选择需要删除的角色')
        return
    }
    const isValidate = await ElMessageBox.confirm('确定进行删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    if (!isValidate) return
    const ids = chooseList.value.map((item) => item.id)
    const { code, data } = await doDelDeleteLiveUser(ids)
    if (code !== 200) {
        ElMessage.error('删除失败')
        return
    }
    ElMessage.success('删除成功')
    initMemberList()
}
/**
 * 搜索
 */
const handleSearch = () => {
    initMemberList()
}

const handleReset = () => {
    searchParams.value = {
        keywords: '',
    }
    handleSearch()
}
async function initMemberList() {
    const { keywords } = searchParams.value
    const params = { ...pageConfig, keywords }
    const { code, data } = await doGetLiveMemberList(params)
    if (code !== 200) return ElMessage.error('获取角色列表失败')
    roleList.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}
initMemberList()
/**
 * 单个删除
 * @param {*} id
 */
const handleDelClick = async (row: ApiRole) => {
    const isValidate = await ElMessageBox.confirm('确定删除该角色?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    if (!isValidate) return
    const { code, data } = await doDelDeleteLiveUser([row.id])
    if (code !== 200) {
        ElMessage.error('删除失败')
        return
    }
    ElMessage.success('删除成功')
    roleList.value = roleList.value.filter((item) => item.id !== row.id)
    pageConfig.total--
}
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
    pageConfig.size = value
    initMemberList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initMemberList()
}
</script>

<template>
    <SchemaForm v-model="searchParams" :columns="columns" label-width="80" @searchHandle="handleSearch" @handleReset="handleReset"> </SchemaForm>
    <div class="grey_bar"></div>
    <div class="handle_container" style="padding-top: 16px">
        <el-button @click="handleBatchDel">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="roleList"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#F7F8FA', height: '50px' }"
            :cell-style="{ color: '#333333', height: '60px' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="35" />
            <el-table-column label="店铺名称" width="230">
                <template #default="{ row }: { row: ApiRole }">
                    <div class="name">{{ row.shopName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="用户昵称" width="250">
                <template #default="{ row }: { row: ApiRole }">
                    <span class="name">{{ row.userName }}</span>
                </template>
            </el-table-column>
            <el-table-column label="微信号" width="230">
                <template #default="{ row }: { row: ApiRole }">
                    <el-tooltip v-if="row.wechatNumber.length >= 10" class="box-item" effect="dark" :content="row.wechatNumber" placement="top">
                        <div class="name">{{ row.wechatNumber }}</div>
                    </el-tooltip>
                    <div v-else class="name">{{ row.wechatNumber }}</div>
                </template>
            </el-table-column>
            <el-table-column label="直播角色" width="140">
                <template #default="{ row }: { row: ApiRole }">
                    <span>{{ role[row.role] }}</span>
                </template>
            </el-table-column>
            <el-table-column label="创建时间">
                <template #default="{ row }: { row: ApiRole }">
                    <span>{{ row.createTime }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="address" label="操作" fixed="right" width="150" align="right">
                <template #default="{ row }: { row: ApiRole }">
                    <el-link style="padding: 0 5px" :underline="false" type="primary" size="small" @click="handleDelClick(row)">删除</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <!-- 好用的分页器 -->
    <PageManage
        :page-size="pageConfig.size"
        :total="pageConfig.total"
        :page-num="pageConfig.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
</template>

<style scoped lang="scss">
@include b(name) {
    width: 120px;
    @include utils-ellipsis;
}
</style>
