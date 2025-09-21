<script setup lang="ts">
import { useRouter } from 'vue-router'
import HeadSearch from '@/q-plugin/liveStream/views/components/studio-list/head-search.vue'
import PageManage from '@/components/PageManage.vue'
import { doGetLiveList, doDelDeleteRoom } from '@/q-plugin/liveStream/apis'
import { formatTime_S, liveIndexStatus } from '@/q-plugin/liveStream/views'
import { ElMessage, ElMessageBox } from 'element-plus'
import Decimal from 'decimal.js'
import type { ApiCreateRoom, RoomStatusJointType } from '@/q-plugin/liveStream/views/types'

const router = useRouter()
const searchParams = ref({
    liveRoomName: '',
    status: '',
})
const studioList = ref<ApiCreateRoom[]>([])
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})

const chooseList = ref<ApiCreateRoom[]>([])

// 处理选择变化
function handleSelectionChange(selection: ApiCreateRoom[]) {
    chooseList.value = selection
}
/**
 * 批量删除
 */
const handleBatchDel = async () => {
    if (!chooseList.value.length) {
        ElMessage.info('请选择需要删除的直播间')
        return
    }
    const isValidate = await ElMessageBox.confirm('确定进行删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
    if (!isValidate) return
    const ids = chooseList.value.map((item) => item.wechatRoomId)
    const { code, data } = await doDelDeleteRoom(ids)
    if (code !== 200) {
        ElMessage.error('删除失败')
        return
    }
    ElMessage.success('删除成功')
    initStodioList()
}
/**
 * 搜索
 */
const handleSearch = () => {
    initStodioList()
}
async function initStodioList() {
    const params = { ...pageConfig, ...searchParams.value }
    try {
        const { code, data } = await doGetLiveList(params)

        if (code !== 200) return ElMessage.error('获取直播列表失败')
        studioList.value = data.records
        pageConfig.current = data.current
        pageConfig.size = data.size
        pageConfig.total = data.total
    } catch (error) {
        ElMessage.error('获取直播列表失败')
        console.log(error)
    }
}
initStodioList()
/**
 * 单个删除
 */
const handleDelClick = async (row: ApiCreateRoom) => {
    try {
        const isValidate = await ElMessageBox.confirm('确定删除该直播间?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        if (!isValidate) return
        const { code, data } = await doDelDeleteRoom([row.wechatRoomId])
        if (code !== 200) {
            ElMessage.error('删除失败')
            return
        }
        ElMessage.success('删除成功')
        studioList.value = studioList.value.filter((item) => item.id !== row.id)
        pageConfig.total--
    } catch (error) {
        console.error(error)
    }
}
const handleNavToView = (row: ApiCreateRoom) => {
    router.push({
        name: 'lookGoods',
        query: { id: row.wechatRoomId, shopId: row.shopId, roomStatus: row.status },
    })
}
const handleNavToViewStreamBaseinfo = (row: ApiCreateRoom) => {
    router.push({
        name: 'streamBaseinfo',
        query: { id: row.id },
    })
}
const fromatTimeM = (time: string) => {
    if (!time) {
        return ''
    }
    return formatTime_S(new Decimal(time).mul(1000).toNumber())
    //将时间戳格式转换成年月日时分秒
}
/**
 * 分页器
 */
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initStodioList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initStodioList()
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <HeadSearch v-model="searchParams" @search="handleSearch" />
    </el-config-provider>
    <div class="handle_container">
        <el-button
            round
            type="primary"
            @click="
                router.push({
                    name: 'streamBaseinfo',
                })
            "
            >新增直播间</el-button
        >
        <el-button plain :disabled="chooseList.length === 0" @click="handleBatchDel">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="studioList"
            stripe
            :header-cell-style="{ background: '#F7F8FA', height: '48px', color: '#333' }"
            :cell-style="{ color: '#333333' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="30" />
            <el-table-column label="店铺名称" width="160">
                <template #default="{ row }">
                    <div class="shopNmae">{{ row.shopName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="直播名称" width="160">
                <template #default="{ row }">
                    <div class="name">{{ row.roomName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="活动时间" width="240">
                <template #default="{ row }">
                    <div>起：{{ fromatTimeM(row.startTime) }}</div>
                    <div>止：{{ fromatTimeM(row.endTime) }}</div>
                </template>
            </el-table-column>
            <el-table-column label="主播昵称" width="140">
                <template #default="{ row }">
                    <div class="anchorName">{{ row.anchorName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="主播微信号" width="160">
                <template #default="{ row }">
                    <div class="name">{{ row.wechatNumber }}</div>
                </template>
            </el-table-column>
            <el-table-column label="活动状态">
                <template #default="{ row }">
                    <div>{{ liveIndexStatus[row.status as RoomStatusJointType] }}</div>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="230" align="right">
                <template #default="{ row }">
                    <el-row justify="end" style="width: 100%">
                        <el-link style="padding: 0 5px" :underline="false" type="primary" size="small" @click="handleNavToViewStreamBaseinfo(row)">
                            查看直播信息
                        </el-link>
                        <el-link style="padding: 0 5px" :underline="false" type="primary" size="small" @click="handleNavToView(row)"
                            >查看商品</el-link
                        >
                        <el-link style="padding: 0 5px" :underline="false" type="primary" size="small" @click="handleDelClick(row)">删除</el-link>
                    </el-row>
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
    width: 152px;
    @include utils-ellipsis;
}
@include b(shopNmae) {
    width: 110px;
    @include utils-ellipsis;
}
@include b(anchorName) {
    width: 120px;
    @include utils-ellipsis;
}
</style>
