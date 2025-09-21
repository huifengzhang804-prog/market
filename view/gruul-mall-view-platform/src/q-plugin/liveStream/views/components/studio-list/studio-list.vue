<script setup lang="ts">
import PageManage from '@/components/PageManage.vue'
import { doGetLiveList, doDelDeleteRoom, doGetShareLiveRoom } from '@/q-plugin/liveStream/apis'
import { ElMessage, ElMessageBox } from 'element-plus'
import { liveIndexStatus, formatTime_S } from '@/q-plugin/liveStream/views'
import Decimal from 'decimal.js'
import type { ApiRoomItem } from '@/q-plugin/liveStream/views/types'

const searchParams = ref({
    type: '',
    keywords: '',
    status: '',
})
const dataSrc = ref('')
const studioList = ref<ApiRoomItem[]>([])
// 分享弹窗
const sharePopupShow = ref(false)
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})
const chooseList = ref<ApiRoomItem[]>([])
const columns = [
    {
        label: '状态',
        labelWidth: 45,
        prop: 'status',
        valueType: 'select',
        options: [
            {
                value: '',
                label: '全部状态',
            },
            {
                value: 'LIVE_BROADCAST',
                label: '进行中',
            },
            {
                value: 'NOT_STARTED',
                label: '未开始',
            },
            {
                value: 'CLOSED',
                label: '已结束',
            },
        ],
        fieldProps: {
            placeholder: '请选择',
        },
    },
    {
        label: '关键字',
        prop: 'keywords',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请输入关键字',
        },
    },
]

// 处理选择变化
function handleSelectionChange(selection: ApiRoomItem[]) {
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
    initStodioList()
    ElMessage.success('删除成功')
}
/**
 * 搜索
 */
const handleSearch = () => {
    initStodioList()
}

const handleReset = () => {
    searchParams.value = {
        type: '',
        keywords: '',
        status: '',
    }
    handleSearch()
}
async function initStodioList() {
    const { keywords, status: roomStatus } = searchParams.value
    const params = { ...pageConfig, keywords, roomStatus }
    const { code, data } = await doGetLiveList(params)
    if (code !== 200) return ElMessage.error('获取直播间列表失败')
    if (data) {
        studioList.value = data.records
        pageConfig.current = data.current
        pageConfig.size = data.size
        pageConfig.total = data.total
    }
}
initStodioList()
/**
 * 单个删除
 * @param {*} id
 */
const handleDelClick = async (row: any) => {
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
/**
 * 分享直播间
 * @param {*} row
 */
const handleShare = async (row: ApiRoomItem) => {
    const { code, data, msg } = await doGetShareLiveRoom(row.wechatRoomId)
    if (code !== 200) {
        ElMessage.error(msg)
        return
    }
    dataSrc.value = data
    sharePopupShow.value = true
}
const handleClose = () => {
    dataSrc.value = ''
    sharePopupShow.value = false
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
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    console.log('size', value)
    pageConfig.current = 1
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
        <SchemaForm v-model="searchParams" :columns="columns" label-width="80" @searchHandle="handleSearch" @handleReset="handleReset"> </SchemaForm>
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="handle_container" style="padding-top: 16px">
        <el-button @click="handleBatchDel">批量删除</el-button>
    </div>
    <div class="table_container">
        <el-table
            :data="studioList"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#F7F8FA', height: '50px' }"
            :cell-style="{ color: '#333333' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column type="selection" width="35" />
            <el-table-column label="店铺名称" width="180">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <el-tooltip v-if="row.shopName.length >= 10" class="box-item" effect="dark" :content="row.shopName" placement="top">
                        <div class="name">{{ row.shopName }}</div>
                    </el-tooltip>
                    <div v-else class="name">{{ row.shopName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="直播名称" width="180">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <div class="name">
                        {{ row.roomName }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="活动时间" width="200">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <div>起：{{ fromatTimeM(row.startTime) }}</div>
                    <div>止：{{ fromatTimeM(row.endTime) }}</div>
                </template>
            </el-table-column>
            <el-table-column label="主播昵称" width="180">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <el-tooltip v-if="row.anchorName.length >= 10" class="box-item" effect="dark" :content="row.anchorName" placement="top">
                        <div class="name">{{ row.anchorName }}</div>
                    </el-tooltip>
                    <div v-else class="name">{{ row.anchorName }}</div>
                </template>
            </el-table-column>
            <el-table-column label="主播微信号" width="180">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <el-tooltip v-if="row.wechatNumber.length >= 10" class="box-item" effect="dark" :content="row.wechatNumber" placement="top">
                        <span>{{ row.wechatNumber }}</span>
                    </el-tooltip>
                    <span v-else>{{ row.wechatNumber }}</span>
                </template>
            </el-table-column>
            <el-table-column label="活动状态">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <span :style="{ color: liveIndexStatus[row.status] === 'CLOSED' ? '#999' : '' }">{{ liveIndexStatus[row.status] }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" align="right">
                <template #default="{ row }: { row: ApiRoomItem }">
                    <el-link
                        v-if="row.status !== 'CLOSED'"
                        style="padding: 0 5px"
                        :underline="false"
                        type="primary"
                        size="small"
                        @click="handleShare(row)"
                        >分享
                    </el-link>
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
    <el-dialog v-model="sharePopupShow" width="30%" title="分享" @close="handleClose">
        <div class="sharePopupShow-box">
            <div class="sharePopupShow-box__title">直播间小程序码</div>
            <el-image style="width: 187px; height: 187px" :src="dataSrc" referrerpolicy="no-referrer" />
        </div>
    </el-dialog>
</template>

<style scoped lang="scss">
@include b(name) {
    @include utils-ellipsis;
}
@include b(sharePopupShow-box) {
    padding: 30px 80px;
    background: #f7f7f7;
    border-radius: 2px;
    text-align: center;
    @include e(title) {
        font-size: 14px;
        color: #333333;
        margin-bottom: 24px;
    }
}
</style>
