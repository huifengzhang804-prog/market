<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBrandList, doDelBrand, doPutBrand } from '@apis/brand'

const $router = useRouter()
const brandName = ref('')
const searchForm = reactive({
    brandName: '',
})
const BrandList = ref([])
const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})
const columns = [
    {
        label: '品牌名称',
        labelWidth: 75,
        prop: 'brandName',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入品牌名称',
        },
    },
]

initBrandList()

async function initBrandList() {
    const { code, data } = await getBrandList({
        ...pageConfig,
        brandName: searchForm.brandName,
    })
    if (code !== 200) {
        ElMessage.error('获取品牌列表失败')
        return
    }
    BrandList.value = data.records
    pageConfig.total = data.total
}
const handleSizeChange = (val: number) => {
    pageConfig.current = 1
    pageConfig.size = val
    initBrandList()
}
const handleCurrentChange = (val: number) => {
    pageConfig.current = val
    initBrandList()
}
const Searchlist = () => {
    pageConfig.current = 1
    initBrandList()
}
const handleReset = () => {
    Object.keys(searchForm).forEach((key) => {
        if (key in searchForm) {
            ;(searchForm as Record<string, string>)[key] = ''
        }
    })
    Searchlist()
}
const DelBrand = async (brandId: string) => {
    ElMessageBox.confirm('确定要删除该品牌吗，此操作不可逆?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        const { code } = await doDelBrand(brandId)
        if (code !== 200) {
            ElMessage.error('删除失败')
            return
        }
        ElMessage.success('删除成功')
        initBrandList()
    })
}
const PutBrandStatus = async (brandId: string, status: string) => {
    const paramsstatus = status === 'SELL_ON' ? 'SELL_OFF' : 'SELL_ON'
    const { code } = await doPutBrand(brandId, paramsstatus)
    if (code !== 200) {
        ElMessage.error(`${status === 'SELL_ON' ? '下架' : '上架'}失败`)
        return
    }
    ElMessage.success(`${status === 'SELL_ON' ? '下架' : '上架'}成功`)
    initBrandList()
}
const EditeBrand = async (brandId: string) => {
    $router.push({
        path: '/brandManage/AddBrand',
        query: {
            brandId,
        },
    })
}
</script>

<template>
    <div>
        <SchemaForm v-model="searchForm" :columns="columns" @searchHandle="Searchlist" @handleReset="handleReset"> </SchemaForm>
        <div class="handle_container" style="padding-top: 16px">
            <div style="display: flex; justify-content: space-between">
                <el-button type="primary" round @click="$router.push('/brandManage/AddBrand')">添加品牌</el-button>
            </div>
        </div>
        <el-table
            class="table_container"
            empty-text="暂无数据~"
            :data="BrandList"
            style="width: 100%; margin-bottom: 60px"
            :header-cell-style="{
                'background-color': '#F6F8FA',
                color: '#333',
            }"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column label="品牌LOGO" width="170px" align="left">
                <template #default="{ row }">
                    <el-image :src="row.brandLogo" style="width: 49px; height: 49px"></el-image>
                </template>
            </el-table-column>
            <el-table-column label="品牌名称" width="130px">
                <template #default="{ row }">
                    <div class="ellipsis">{{ row.brandName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="品牌描述" width="270px">
                <template #default="{ row }">
                    <div class="ellipsis">{{ row.brandDesc }}</div>
                </template>
            </el-table-column>
            <el-table-column label="检索首字母" width="100px">
                <template #default="{ row }">
                    <div>{{ row.searchInitials }}</div>
                </template>
            </el-table-column>
            <el-table-column label="关注人数" width="120px" align="center">
                <template #default="{ row }">
                    <div>{{ row.followers }}</div>
                </template>
            </el-table-column>
            <el-table-column label="状态" width="80px">
                <template #default="{ row }">
                    <div>{{ row.status === 'SELL_ON' ? '已上架' : '已下架' }}</div>
                </template>
            </el-table-column>
            <el-table-column label="创建时间" width="160px">
                <template #default="{ row }">
                    <div>{{ row.createTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="排序" width="60px" align="center">
                <template #default="{ row }">
                    <div>{{ row.sort }}</div>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150px" align="right" fixed="right">
                <template #default="{ row }">
                    <span style="color: #2e99f3; cursor: pointer" @click="EditeBrand(row.id)">编辑</span>
                    <span style="color: #2e99f3; cursor: pointer; margin: 0 10px" @click="PutBrandStatus(row.id, row.status)">{{
                        row.status === 'SELL_ON' ? '下架' : '上架'
                    }}</span>
                    <span style="color: #2e99f3; cursor: pointer" @click="DelBrand(row.id)">删除</span>
                </template>
            </el-table-column>
        </el-table>
        <div style="position: fixed; bottom: 0; background: #fff; width: 980px; height: 70px; z-index: 1000">
            <page-manage
                :page-size="pageConfig.size"
                :page-num="pageConfig.current"
                :total="pageConfig.total"
                @handle-size-change="handleSizeChange"
                @handle-current-change="handleCurrentChange"
            />
        </div>
    </div>
</template>

<style lang="scss" scoped>
.ellipsis {
    @include utils-ellipsis();
}
</style>
