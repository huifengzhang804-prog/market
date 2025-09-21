<script setup lang="ts">
import HeadSearch from '@/q-plugin/liveStream/views/components/live-goods/head-search.vue'
import { doGetRoomGoods, doPostAddRoomGoods } from '@/q-plugin/liveStream/apis/goods'
import ChoosedGoodsPopup from '@/q-plugin/liveStream/views/components/live-goods/choosed-goods-popup.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { ElMessage } from 'element-plus'
import { LIVE_GOODS_STATUS } from '@/q-plugin/liveStream/views/components/live-goods'
import type { ApiGoods, ChooseGoodsPopup, LiveGoodsType } from '@/q-plugin/liveStream/views/components/live-goods/types'

const searchParams = ref({
    keywords: '',
})
const chooseGoodsPopupRef = ref()
const dialogVisible = ref(false)
const $route = useRoute()
const roomId = $route.query.id
const roomStatus = $route.query.roomStatus
const $router = useRouter()
const goodsList = ref([])
const pageConfig = reactive({
    size: 10,
    current: 1,
    total: 0,
})

const handleClose = () => {
    dialogVisible.value = false
}
/**
 * 新增直播商品
 */
const handleAddGoods = () => {
    dialogVisible.value = true
}
async function initGetRoomGoods() {
    if (!roomId) {
        ElMessage.error('未获取到直播间')
        $router.back()
        return
    }
    const { code, data } = await doGetRoomGoods({ roomId, ...pageConfig })
    if (code !== 200) {
        ElMessage.error('获取商品列表失败')
        return
    }
    goodsList.value = data.records
    pageConfig.current = data.current
    pageConfig.size = data.size
    pageConfig.total = data.total
}

const handleSearch = () => {}
/**
 * 获取到选择的商品
 */
const handleConfirmChoose = async () => {
    if (chooseGoodsPopupRef.value.tempGoods.length) {
        const ids = chooseGoodsPopupRef.value.tempGoods.map((item: ChooseGoodsPopup) => item.goodsId)
        const { code, data, msg } = await doPostAddRoomGoods(roomId as string, ids)
        if (code !== 200) {
            ElMessage.error(msg ? msg : '添加直播间商品失败')
            return
        }
        ElMessage.success('添加直播间商品成功')
        dialogVisible.value = false
        initGetRoomGoods()
    }
}
/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    pageConfig.size = value
    initGetRoomGoods()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initGetRoomGoods()
}
const handleSeckill = () => {
    $router.push({
        name: 'seckill',
    })
}
</script>

<template>
    <head-search
        v-model="searchParams"
        :right-btn-hidden="roomStatus !== 'CLOSED'"
        left-btn-text="返回直播间"
        right-btn-text="新增直播商品"
        @batch-del="handleAddGoods"
        @search="handleSearch"
        @add="$router.back()"
    />
    <el-table
        ref="multipleTableRef"
        :data="goodsList"
        stripe
        height="calc(100vh - 280px)"
        :header-row-style="{ fontSize: '12px', color: '#909399' }"
        :header-cell-style="{ background: '#f6f8fa' }"
        :cell-style="{ fontSize: '12px', color: '#333333' }"
    >
        <template #empty> <ElTableEmpty /> </template>
        <el-table-column label="商品名称" width="250">
            <template #default="{ row }">
                <div class="goods__info--name">{{ row.productName }}</div>
            </template>
        </el-table-column>
        <el-table-column label="商品图片" align="center">
            <template #default="{ row }">
                <el-image style="width: 40px; height: 40px" :src="row.ossImgUrl" />
            </template>
        </el-table-column>
        <el-table-column label="状态" width="200">
            <template #default="{ row }: { row: LiveGoodsType }">
                <span>{{ LIVE_GOODS_STATUS[row.auditStatus] }}</span>
            </template>
        </el-table-column>
    </el-table>
    <el-row justify="end" align="middle">
        <!-- 好用的分页器 -->
        <BetterPageManage
            v-model="pageConfig"
            :load-init="true"
            :page-size="pageConfig.size"
            :total="pageConfig.total"
            @reload="initGetRoomGoods"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </el-row>
    <el-dialog v-model="dialogVisible" title="" width="45%" :before-close="handleClose">
        <choosed-goods-popup ref="chooseGoodsPopupRef" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleConfirmChoose">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<style scoped></style>
