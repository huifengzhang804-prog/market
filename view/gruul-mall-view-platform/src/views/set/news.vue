<script setup lang="ts">
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetNewsList, doPostNews, doPutNews, doPatchNews, doDelNews } from '@/apis/setting'
import { ElMessage, ElMessageBox } from 'element-plus'
import QEdit from '@/components/q-editor/q-edit.vue'
import type { FormRules } from 'element-plus'
import SchemaForm from '@/components/SchemaForm.vue'

type SearchValueType = {
    keywords: string
}

const searchValue = ref<SearchValueType>({
    keywords: '',
})
const tableData = ref([])
const dialogFlag = ref(false)
const isEdit = ref(false)
const ruleFormRef = ref()
const formRule = reactive<FormRules>({
    title: [
        {
            required: true,
            message: '请输入标题',
            trigger: 'blur',
        },
        { min: 2, max: 64, message: '标题请输入2~64之间', trigger: 'blur' },
    ],
    content: [
        {
            required: true,
            message: '请输入内容',
            trigger: 'blur',
        },
    ],
})
const submitForm = ref({
    id: '',
    title: '',
    content: '',
})
const pageConfig = reactive({
    pageSize: 10,
    pageNum: 1,
    total: 0,
})
const columns = [
    {
        label: '公告标题',
        labelWidth: 75,
        prop: 'keywords',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入公告标题',
            maxlength: 150,
        },
    },
]

onMounted(() => {
    GetNewsList()
})

/**
 * 初始化列表方法
 */
const GetNewsList = async () => {
    const params = {
        keywords: searchValue.value.keywords,
        type: 'ANNOUNCEMENT',
        current: pageConfig.pageNum,
        size: pageConfig.pageSize,
    }
    const { data } = await doGetNewsList(params)
    tableData.value = data.records
    pageConfig.total = data.total
}

const handleReset = () => {
    Object.keys(searchValue.value).forEach((key) => (searchValue.value[key as keyof typeof searchValue.value] = ''))
    GetNewsList()
}
/**
 * 搜索
 */
const Searchlist = () => {
    GetNewsList()
}
/**
 * 关闭弹窗
 */
const dialogCloseHandle = () => {
    isEdit.value = false
    dialogFlag.value = false
    submitForm.value = {
        id: '',
        title: '',
        content: '',
    }
}
/**
 * 提交表单
 *
 * */
const submitHandle = async () => {
    const flag = await ruleFormRef.value.validate()
    console.log('submitForm.value.content.trim()', submitForm.value.content.trim())
    if (!submitForm.value.content.trim() || submitForm.value.content === '<p><br></p>') {
        ElMessage.error('请输入内容')
        return
    }
    if (flag) {
        const { code, success } = isEdit.value
            ? await doPutNews(submitForm.value.id, { ...submitForm.value })
            : await doPostNews({ ...submitForm.value })
        if (code === 200 && success) {
            ElMessage.success(`${isEdit.value ? '更新成功' : '添加成功'}`)
            GetNewsList()
            dialogFlag.value = false
        }
    }
}
/**
 * 编辑表单
 */
const editHandle = (val: any) => {
    isEdit.value = true
    submitForm.value = val
    dialogFlag.value = true
}
/**
 * 推送消息
 */
const pushMessage = async (id: any) => {
    const { code, success } = await doPatchNews(id)
    if (code === 200 && success) {
        ElMessage.success(`推送成功`)
        GetNewsList()
    }
}
/**
 * 删除消息
 */
const removalMessage = (id: any) => {
    ElMessageBox.confirm('确认删除当前公告？', '提示').then(async () => {
        const { code, success } = await doDelNews(id)
        if (code === 200 && success) {
            ElMessage.success(`删除成功`)
            GetNewsList()
        }
    })
}
const handleSizeChange = (val: number) => {
    pageConfig.pageNum = 1
    pageConfig.pageSize = val
    GetNewsList()
}
const handleCurrentChange = (val: number) => {
    pageConfig.pageNum = val
    GetNewsList()
}
</script>

<template>
    <SchemaForm v-model="searchValue" :columns="columns" @searchHandle="Searchlist" @handleReset="handleReset" />
    <div class="grey_bar"></div>
    <div class="news__head">
        <el-button type="primary" round @click="dialogFlag = true">新增消息</el-button>
    </div>
    <div class="table_container" style="border-top: 0">
        <el-table
            :data="tableData"
            style="width: 100%"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#f6f8fa', height: '48px' }"
            :row-style="{ height: '60px' }"
        >
            <template #empty>
                <ElTableEmpty imageIndex="6" />
            </template>
            <el-table-column type="index" label="序号" :index="1" align="center" width="70" fixed="left" />
            <el-table-column prop="title" label="公告标题" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" align="right" width="210" fixed="right">
                <template #default="scope">
                    <el-link type="primary" class="news__head--tablecell" @click="editHandle(scope.row)"> 编辑</el-link>
                    <el-link v-if="!scope.row.pushed" type="primary" class="news__head--tablecell" @click="pushMessage(scope.row.id)">
                        推送消息</el-link
                    >
                    <el-link type="danger" class="news__head--tablecell" @click="removalMessage(scope.row.id)"> 删除</el-link>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <BetterPageManage
        :page-size="pageConfig.pageSize"
        :page-num="pageConfig.pageNum"
        :total="pageConfig.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
        @reload="GetNewsList"
    />
    <el-dialog v-model="dialogFlag" :title="isEdit ? '编辑消息' : '新增消息'" destroy-on-close @close="dialogCloseHandle">
        <el-form ref="ruleFormRef" :model="submitForm" :rules="formRule" label-width="100px">
            <el-form-item label="标题" prop="title">
                <el-input v-model="submitForm.title" placeholder="请填写通知标题" maxlength="20"></el-input>
            </el-form-item>
            <el-form-item label="通知内容" prop="content">
                <div style="border: 1px solid #ccc">
                    <QEdit v-model:content="submitForm.content" />
                </div>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogFlag = false">取消</el-button>
                <el-button type="primary" @click="submitHandle">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(news) {
    @include e(head) {
        @include flex(space-between);
        padding: 16px;
        @include m(tablecell) {
            margin-left: 22px;
        }
    }
}
:deep(.el-dialog__headerbtn) {
    z-index: -100;
}
@include b(ellipsis) {
    width: 280px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    height: 60px;
    line-height: 60px;
    display: flex;
    justify-content: flex-start;
}
.content::after {
    content: '...';
    position: absolute;
    right: 45px;
    bottom: 25px;
}
:deep(.el-table__inner-wrapper::before) {
    background-color: #fff;
}
:deep(.el-table__inner-wrapper::before) {
    background-color: #fff;
}
</style>
