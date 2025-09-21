<template>
    <ChromeTab :tab-list="tabList" :value="titleStatus" @handle-tabs="handleTabClick" />
    <div class="search_container">
        <Search :titleStatus="titleStatus" @on-search-params="getSearch"></Search>
    </div>
    <div class="tab_container">
        <el-button type="primary" @click="handleAdd">新增</el-button>
    </div>
    <q-table v-if="!tableList.loading" :data="tableList.goods" no-border class="table">
        <q-table-column :label="`${titleStatus === 'ATTRIBUTE' ? '属性' : '参数'}名称`" prop="featureName" width="250">
            <template #default="{ row }">
                <el-tooltip
                    v-if="row.featuresValue.featureName.length > 15"
                    class="box-item"
                    effect="dark"
                    :content="row.featuresValue.featureName"
                    placement="top-start"
                >
                    <span class="featureName">{{ row.featuresValue.featureName }}</span>
                </el-tooltip>
                <span v-else class="featureName">{{ row.featuresValue.featureName }}</span>
            </template>
        </q-table-column>
        <q-table-column :label="`${titleStatus === 'ATTRIBUTE' ? '属性' : '参数'}值`" width="200" prop="address">
            <template #default="{ row }">
                <el-tooltip
                    v-if="row.featuresValue.featureValues.map((item: FeatureValue) => item.firstValue).join(',').length > 30"
                    class="box-item"
                    effect="dark"
                    :content="row.featuresValue.featureValues.map((item: FeatureValue) => item.firstValue).join(',')"
                    placement="top-start"
                >
                    <div class="attributeValues">
                        {{ row.featuresValue.featureValues.map((item: FeatureValue) => item.firstValue).join(',') }}
                    </div>
                </el-tooltip>
                <div v-else class="attributeValues">
                    {{ row.featuresValue.featureValues.map((item: FeatureValue) => item.firstValue).join(',') }}
                </div>
            </template>
        </q-table-column>
        <q-table-column v-if="titleStatus === 'ATTRIBUTE'" label="是否必选" width="150">
            <template #default="{ row }">
                <div>{{ row.featuresValue.isRequired ? '是' : '否' }}</div>
            </template>
        </q-table-column>
        <q-table-column v-if="titleStatus === 'ATTRIBUTE'" label="是否多选" width="150">
            <template #default="{ row }">
                {{ row.featuresValue.isMultiSelect ? '是' : '否' }}
            </template>
        </q-table-column>
        <q-table-column label="操作" align="right" width="120" fixed="right">
            <template #default="{ row }">
                <div style="height: 40px"></div>
                <el-link type="primary" style="margin-right: 10px" @click="handleEdit(row)">编辑</el-link>
                <el-link type="danger" @click="handleDel(row)">删除</el-link>
            </template>
        </q-table-column>
    </q-table>
    <PageManage
        :page-size="tableList.page.size"
        :page-num="tableList.page.current"
        :total="tableList.total"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="dialogVisible" :title="title" width="700px" center>
        <para-dialog v-if="dialogVisible" ref="paraDialogRef" :row-info="rowInfo" :title-status="titleStatus === 'ATTRIBUTE' ? '属性' : '参数'" />
        <template #footer>
            <div>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" style="margin-left: 100px" @click="handleConfirm">确 定</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { onBeforeMount } from 'vue'
import Search, { SearchType } from './components/search.vue'
import paraDialog from './components/paraDialog.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import PageManage from '@/components/PageManage/index.vue'
import { doGetfeatureList, dofeatureSave, dofeatureUpdate, dofeatureDel } from '@/apis/good'
import { ElMessage, ElMessageBox } from 'element-plus'
import ChromeTab from '@/components/ChromeTab.vue'
import type { FeatureValue, Goods, Record } from './type'
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

const tableList = reactive<Goods>({
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
const handleDel = async (row: Record) => {
    const featuresType = titleStatus.value
    const data = [row.id]
    ElMessageBox.confirm(`确认删除当前${titleStatus.value === 'ATTRIBUTE' ? '属性' : '参数'}？`, '提示').then(async () => {
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
        data.records.forEach((element: Record) => {
            element.featuresValue.featureValues.forEach((item: FeatureValue) => {
                item.secondValue = divTenThousand(item.secondValue) as any
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
const handleEdit = (row: Record) => {
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

const paraDialogRef = ref<InstanceType<typeof paraDialog>>()
// 点击确定
const handleConfirm = async () => {
    const params = { ...paraDialogRef.value?.params }
    if (!params) return
    if (!params.featuresValue.featureName) {
        return ElMessage.error({ message: '请输入名称' })
    }
    const featureValues = params.featuresValue.featureValues.map((item: FeatureValue) => ({
        ...item,
        secondValue: mulTenThousand(item.secondValue),
    }))
    if (params?.id) {
        // 编辑
        await dofeatureUpdate({ ...params, featuresValue: { ...params.featuresValue, featureValues } })
    } else {
        // 新增
        await dofeatureSave({ ...params, featuresValue: { ...params.featuresValue, featureValues } })
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

<style scoped lang="scss">
@include b(table) {
    overflow: auto;
    transition: height 0.5s;
}
@include b(attributeValues) {
    @include utils-ellipsis(1);
    max-width: 423px;
}
@include b(featureName) {
    width: 210px;
    @include utils-ellipsis(1);
}
</style>
