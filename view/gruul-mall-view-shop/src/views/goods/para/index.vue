<script setup lang="ts">
import Search, { SearchType } from './components/search.vue'
import ParaDialog from './components/paraDialog.vue'
import PageManage from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { doGetfeatureList, dofeatureSave, dofeatureUpdate, dofeatureDel } from '@/apis/good'
import { ElMessage, ElMessageBox } from 'element-plus'
import ChromeTab from '@/components/ChromeTab.vue'

const { mulTenThousand, divTenThousand } = useConvert()

const searchParams = ref({
    featuresType: 'ATTRIBUTE',
    name: '',
})

// 获取搜索参数
const getSearch = (e: SearchType) => {
    searchParams.value = { ...searchParams.value, ...e }
    initList()
}

const tableList = reactive({
    page: { size: 10, current: 1 },
    loading: false,
    goods: [],
    total: 0,
})

onBeforeMount(() => {
    initList()
})

const dialogVisible = ref(false)

// 删除
const handleDel = (row: any) => {
    const featuresType = titleStatus.value
    const data = [row.id]
    ElMessageBox.confirm(`确认删除当前${titleStatus.value === 'ATTRIBUTE' ? '属性' : '参数'}吗？`, '提示').then(async () => {
        try {
            await dofeatureDel(featuresType, data)
            initList()
        } catch (error) {
            return
        }
    })
}

const tabList = [
    {
        name: 'ATTRIBUTE',
        label: '属性',
    },
    {
        name: 'ARGUMENTS',
        label: '参数',
    },
]
const titleStatus = ref('ATTRIBUTE')
// 切换tab
const handleTabClick = (status: string) => {
    searchParams.value.featuresType = status
    titleStatus.value = status
    initList()
}
// 初始化list
const initList = async () => {
    tableList.loading = true
    try {
        const params = {
            ...tableList.page,
            ...searchParams.value,
        }
        const { data } = await doGetfeatureList(params)
        tableList.page = {
            size: data.size,
            current: data.current,
        }
        tableList.total = data.total
        data.records.forEach((element: any) => {
            element.featuresValue.featureValues.forEach((item: any) => {
                item.secondValue = divTenThousand(item.secondValue).toNumber()
            })
        })
        tableList.goods = data.records
    } catch (error) {
        console.log(error)
    }
    tableList.loading = false
}

// title
const title = ref('新增属性')

const initInfo = () => ({
    featuresType: titleStatus.value,
    featuresValue: {
        featureName: '',
        isRequired: true,
        isMultiSelect: true,
        featureValues: [{ firstValue: '', secondValue: 0 }],
    },
})

// 子组件默认参数
const rowInfo = ref(initInfo())

// 修改
const handleEdit = (row: any) => {
    title.value = `编辑${titleStatus.value === 'ATTRIBUTE' ? '属性' : '参数'}`
    rowInfo.value = row
    dialogVisible.value = true
}
// 新增
const handleAdd = () => {
    title.value = `新增${titleStatus.value === 'ATTRIBUTE' ? '属性' : '参数'}`
    rowInfo.value = initInfo()
    dialogVisible.value = true
}

const paraDialogRef = ref<InstanceType<typeof ParaDialog>>()
// 点击确定
const handleConfirm = async () => {
    const params = { ...paraDialogRef.value?.params }
    if (!params) return
    if (!params.featuresValue.featureName) {
        return ElMessage.error({ message: '请输入名称' })
    }
    const featureValues = params.featuresValue.featureValues.map((item: any) => ({
        ...item,
        secondValue: mulTenThousand(item.secondValue),
    }))
    if (
        featureValues.some((item: any) => {
            return item.firstValue === ''
        })
    ) {
        return ElMessage.error('请输入值')
    }
    if (params?.id) {
        // 编辑
        const { code, msg } = await dofeatureUpdate({ ...params, featuresValue: { ...params.featuresValue, featureValues } })
        if (code !== 200) return ElMessage.error(msg)
    } else {
        // 新增
        const { code, msg } = await dofeatureSave({ ...params, featuresValue: { ...params.featuresValue, featureValues } })
        if (code !== 200) return ElMessage.error(msg)
    }
    await initList()
    dialogVisible.value = false
    rowInfo.value = initInfo()
}
// 分页方法
const handleSizeChange = (val: number) => {
    tableList.page.current = 1
    tableList.page.size = val
    initList()
}
const handleCurrentChange = (val: number) => {
    tableList.page.current = val
    initList()
}
</script>

<template>
    <ChromeTab :tab-list="tabList" :value="titleStatus" @handle-tabs="handleTabClick" />
    <Search :titleStatus="titleStatus" @onSearchParams="getSearch"></Search>
    <div class="handle_container">
        <el-button type="primary" @click="handleAdd">新增</el-button>
    </div>
    <QTable v-if="!tableList.loading" :data="tableList.goods" class="table" no-border>
        <QTableColumn :label="`${titleStatus === 'ATTRIBUTE' ? '属性' : '参数'}名称`" prop="featureName" width="320">
            <template #default="{ row }">
                {{ row.featuresValue.featureName }}
            </template>
        </QTableColumn>
        <QTableColumn :label="`${titleStatus === 'ATTRIBUTE' ? '属性' : '参数'}值`" prop="address">
            <template #default="{ row }">
                <div :title="row.featuresValue.featureValues.map((item: any) => item.firstValue).join(',')" class="attributeValues">
                    {{ row.featuresValue.featureValues.map((item: any) => item.firstValue).join(',') }}
                </div>
            </template>
        </QTableColumn>
        <QTableColumn v-if="titleStatus === 'ATTRIBUTE'" label="是否必选" width="140">
            <template #default="{ row }">
                <div>{{ row.featuresValue.isRequired ? '是' : '否' }}</div>
            </template>
        </QTableColumn>
        <QTableColumn v-if="titleStatus === 'ATTRIBUTE'" label="是否多选" width="140">
            <template #default="{ row }">
                {{ row.featuresValue.isMultiSelect ? '是' : '否' }}
            </template>
        </QTableColumn>
        <QTableColumn label="操作" align="right" width="130">
            <template #default="{ row }">
                <div style="height: 50px"></div>
                <el-link type="primary" style="margin-right: 10px" @click="handleEdit(row)">编辑</el-link>
                <el-link type="danger" @click="handleDel(row)">删除</el-link>
            </template>
        </QTableColumn>
    </QTable>
    <PageManage
        :page-size="tableList.page.size"
        :total="tableList.total"
        :page-num="tableList.page.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="dialogVisible" :title="title" width="700px" center>
        <ParaDialog v-if="dialogVisible" ref="paraDialogRef" :row-info="rowInfo" :title-status="titleStatus === 'ATTRIBUTE' ? '属性' : '参数'" />
        <template #footer>
            <div>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" style="margin-left: 100px" @click="handleConfirm">确 定</el-button>
            </div>
        </template>
    </el-dialog>
</template>
<style scoped lang="scss">
@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}
@include b(attributeValues) {
    @include utils-ellipsis(1);
}
</style>
