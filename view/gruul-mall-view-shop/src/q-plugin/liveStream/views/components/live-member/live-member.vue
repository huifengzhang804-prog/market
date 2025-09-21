<script setup lang="ts">
import HeadSearch from '@/q-plugin/liveStream/views/components/live-goods/head-search.vue'
import PageManage from '@/components/PageManage.vue'
import { doPostAddRole, doGetLiveMemberList, doDelDeleteLiveUser } from '@/q-plugin/liveStream/apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import { role } from '@/q-plugin/liveStream/views'
import type { ApiRole } from '@/q-plugin/liveStream/views/types'

const searchParams = ref({
    keywords: '',
})
const wxUrl = ref('')
const addMemberShow = ref(false)
const formData = ref({ username: '', role: 'LIVE_ANCHOR' })
const formRef = ref()
const memberList = ref<ApiRole[]>([])
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const chooseList = ref<ApiRole[]>([])

const validateUsername = (rule: any, value: any, callback: any) => {
    // const regex = /^\d{6,20}$/
    if (!value) {
        callback(new Error('请输入微信号'))
    } else if (value?.length < 6 || value?.length > 20) {
        callback(new Error('微信号最少6位最多20位'))
    } else {
        callback()
    }
}

const rules: { [k: string]: any[] } = reactive({
    username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
    role: [{ required: true, message: '请选择成员', trigger: ['blur', 'change'] }],
})

// 处理选择变化
function handleSelectionChange(selection: any[]) {
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
    initMemberList()
    ElMessage.success('删除成功')
}
const handleSearch = () => {
    initMemberList()
}
async function initMemberList() {
    const params = { ...pageConfig, ...searchParams.value }
    const { code, data } = await doGetLiveMemberList(params)
    if (code !== 200) return ElMessage.error('获取成员列表失败')
    memberList.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}
initMemberList()
const handleDelClick = async (row: ApiRole) => {
    try {
        const isValidate = await ElMessageBox.confirm('确定删除该角色?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        if (!isValidate) return
        const { code, data, msg } = await doDelDeleteLiveUser([row.id])
        if (code !== 200) {
            ElMessage.error(msg || '删除失败')
            return
        }
        ElMessage.success('删除成功')
        memberList.value = memberList.value.filter((item) => item.id !== row.id)
        pageConfig.total--
    } catch (error) {
        console.error(error)
    }
}
const handleSubmit = async () => {
    try {
        const isValidate = await formRef.value.validate()
        if (!isValidate) return
        const { code, data, msg } = await doPostAddRole({ ...formData.value })
        if (code === 400002) {
            wxUrl.value = msg
            return
        }
        if (code !== 200) {
            ElMessage.error(msg || '添加角色失败')
            return
        }
        ElMessage.success('添加角色成功')
        reset()
        initMemberList()
    } catch (error) {
        console.error(error)
    }
}
const handleCancel = () => {
    ElMessageBox.confirm('取消角色信息不会保存?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(() => {
            reset()
        })
        .catch(() => {})
}
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initMemberList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initMemberList()
}
const reset = () => {
    formRef.value.resetFields()
    formData.value = { username: '', role: 'LIVE_ANCHOR' }
    wxUrl.value = ''
    searchParams.value.keywords = ''
    addMemberShow.value = false
}
</script>

<template>
    <head-search v-model="searchParams" @search="handleSearch" />
    <div class="handle_container">
        <el-button round type="primary" @click="addMemberShow = true">新增成员</el-button>
        <el-button plain @click="handleBatchDel">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="memberList"
            :header-cell-style="{ background: '#F7F8FA', height: '48px', color: '#333' }"
            :cell-style="{ color: '#333333' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="30" />
            <el-table-column label="微信昵称" width="340">
                <template #default="{ row }: { row: ApiRole }">
                    <div class="name">{{ row.userName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="微信号">
                <template #default="{ row }: { row: ApiRole }">
                    <span>{{ row.wechatNumber }}</span>
                </template>
            </el-table-column>
            <el-table-column label="直播角色" width="120">
                <template #default="{ row }: { row: ApiRole }">
                    <div>{{ role[row.role] }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="date" label="创建时间">
                <template #default="{ row }: { row: ApiRole }">
                    <span>{{ row.createTime }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="address" label="操作" fixed="right" width="150" align="right">
                <template #default="{ row }: { row: ApiRole }">
                    <el-link style="padding: 0 5px" :underline="false" type="danger" size="small" @click="handleDelClick(row)">删除</el-link>
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
    <el-dialog v-model="addMemberShow" width="600px" @close="reset">
        <template #header="{ titleId }">
            <h4 :id="titleId" style="font-size: 14px">新增</h4>
        </template>
        <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
            <el-form-item label="微信号" prop="username">
                <el-input v-model.trim="formData.username" style="width: 551px" maxlength="20" placeholder="请输入微信号" />
            </el-form-item>
            <el-form-item label="角色" prop="role">
                <el-radio-group v-model="formData.role">
                    <el-radio value="LIVE_ANCHOR">主播</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item v-show="wxUrl">
                <el-image style="width: 300px; height: 300px" :src="wxUrl" />
                <h3 style="margin-top: 15px">设置主播角色时微信号需要实名认证，请先扫码进行实名认证</h3>
            </el-form-item>
        </el-form>
        <template #footer>
            <span>
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" @click="handleSubmit"> 确认 </el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(name) {
    width: 280;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
</style>
