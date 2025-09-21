<script setup lang="ts">
import { doGetEvaluate, doPutIsExcellentEvaluate, doPutShopReplyEvaluate } from '@/apis/order/appraise'
import { ElMessage, ElMessageBox } from 'element-plus'
import SearchForm from '@/views/order/appraise/components/search-form.vue'
import PageManage from '@/components/PageManage.vue'
import type { Evaluate, EvaluateSearchData, EvaluateSearchParams } from '@/views/order/appraise/types'

const dialogVisible = ref(false)
const PageConfig = ref({ size: 10, current: 1, total: 0 })
const EvaluateInfoList = ref<Evaluate[][]>([])
const currentId = ref('')
const shopReply = ref('')
enum SellTypeEnum {
    CONSIGNMENT = '代销商品',
    PURCHASE = '采购商品',
    OWN = '自有商品',
}
const showVideoControl = ref(false)
const videoSrc = ref('')
const myVideo = ref(null)

const showVideo = (videoUrl: string) => {
    showVideoControl.value = true
    videoSrc.value = videoUrl
    playVideo()
}
const playVideo = () => {
    if (myVideo.value) {
        myVideo.value.play()
    }
}
const videoEnd = () => {
    videoSrc.value = ''
    showVideoControl.value = false
}

initEvaluateInfo()

async function initEvaluateInfo(params?: EvaluateSearchParams) {
    const { code, data } = await doGetEvaluate(Object.assign(PageConfig.value, params))
    if (code !== 200) return ElMessage.error('获取评级列表失败')
    PageConfig.value.total = data.total
    PageConfig.value.size = data.size
    EvaluateInfoList.value = processEvaluateData(data.records)
    console.log(EvaluateInfoList.value, '1')
}
/**
 * 处理 评估 数据
 * @param {*} data
 */
const processEvaluateData = (evaluates: Evaluate[]) => {
    const arr = []
    const map = new Map()
    for (const evaluate of evaluates) {
        const mapKey = `${evaluate.orderNo}`
        const currentEvaluateIndex = map.get(mapKey)
        if (!currentEvaluateIndex) {
            map.set(mapKey, arr.length)
            arr[arr.length] = [evaluate]
            continue
        }
        arr[currentEvaluateIndex].push(evaluate)
    }
    return arr
}
// tab表格
/**
 * 精选
 * @param {*} row
 */
const handleChoiceness = async (row: Evaluate) => {
    const isExcellent = !row.isExcellent
    const { code, data } = await doPutIsExcellentEvaluate(isExcellent, [row.id])
    if (code !== 200) return ElMessage.error(`${isExcellent ? '设为' : '取消'}精选失败`)
    ElMessage.success(`${isExcellent ? '设为' : '取消'}精选成功`)
    initEvaluateInfo()
}
const handlerReply = (row: Evaluate) => {
    currentId.value = row.id
    dialogVisible.value = true
}
// 商品名称
// 买家昵称
// 成交时间
//  * 评价星级
const searchData = reactive<EvaluateSearchData>({
    name: '',
    nickname: '',
    clinchTime: '',
    rate: 0,
})

/**
 * 处理搜索
 */
const handleSearch = () => {
    const { name, nickname, clinchTime, rate } = searchData as EvaluateSearchData
    const params = { name, nickname, rate, startTime: '', endTime: '' }
    if (Array.isArray(clinchTime)) {
        params.startTime = clinchTime[0]
        params.endTime = clinchTime[1]
    }
    if (Number(params.rate) === 0) {
        params.rate = ''
    }
    initEvaluateInfo(params)
}

const handleClose = () => {
    shopReply.value = ''
    dialogVisible.value = false
}
/**
 * 回复提交
 */
const handleSubmit = async () => {
    if (!shopReply.value.length) return ElMessage.error(`请输入内容`)
    const { code, data } = await doPutShopReplyEvaluate(currentId.value, shopReply.value)
    if (code !== 200) return ElMessage.error(`回复失败`)
    ElMessage.success(`回复成功`)
    shopReply.value = ''
    dialogVisible.value = false
    initEvaluateInfo()
}
const arraySpanMethod = ({ row, column, rowIndex, columnIndex }: { row: any; column: any; rowIndex: number; columnIndex: number }) => {
    return columnIndex === 0 ? [1, 6] : [0, 0]
}

/**
 * 分页器
 * @param {*} value
 */
const handleSizeChange = (value: number) => {
    PageConfig.value.size = value
    initEvaluateInfo()
}
const handleCurrentChange = (value: number) => {
    PageConfig.value.current = value
    initEvaluateInfo()
}
</script>

<template>
    <el-config-provider :empty-values="[undefined, null]">
        <SearchForm v-model:search-data="searchData" @click="handleSearch" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="table_container" style="padding-top: 20px">
        <el-table
            :data="EvaluateInfoList"
            :span-method="arraySpanMethod"
            class="table"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ height: '50px', background: '#F7F8FA' }"
            :cell-style="{ color: '#333333', padding: 0 }"
            header-row-class-name="header-row"
            row-class-name="row_tr"
        >
            <template #empty> <ElTableEmpty /> </template>
            <el-table-column>
                <template #header>
                    <span class="label">评价内容</span>
                </template>
                <template #default="{ row }">
                    <div style="text-align: left; position: relative" class="theHead">
                        <span>订单号：{{ row[0].orderNo }}</span
                        ><span>评论时间：{{ row[0].createTime }}</span>
                        <QIcon
                            v-if="row[0].isExcellent"
                            name="icon-jinrongicon_jingxuan"
                            svg
                            style="width: 23px; height: 23px; position: absolute; right: 20px; top: 50%; margin-top: -10px"
                        />
                    </div>
                    <el-table
                        :data="row"
                        style="width: 100%"
                        row-class-name="rowstyle"
                        :show-header="false"
                        :cell-style="{ color: '#333333', verticalAlign: 'top' }"
                    >
                        <template #empty> <ElTableEmpty /> </template>
                        <!-- <el-table-column type="selection" width="55" /> -->
                        <el-table-column>
                            <template #default="{ row }">
                                <div class="user">
                                    <div class="user__info"></div>
                                    <div>
                                        <p class="user__comment">{{ row?.comment }}</p>
                                        <div style="display: flex; margin-top: 10px">
                                            <div v-for="(img, index) in row.medias" :key="index">
                                                <video
                                                    v-if="img.split('.').pop() === 'mp4' || img.split('.').pop() === 'MOV'"
                                                    :src="img"
                                                    style="width: 55px; height: 55px; margin-right: 12px"
                                                    @click="showVideo(img)"
                                                ></video>
                                                <el-image
                                                    v-else
                                                    :src="img"
                                                    :preview-src-list="row.medias.filter(
                                                    (v:string) => !(v.includes('.mp4') || v.includes('.MOV')),
                                                )"
                                                    :initial-index="index"
                                                    style="width: 55px; height: 55px; margin-right: 12px"
                                                />
                                            </div>
                                        </div>
                                        <el-rate
                                            :model-value="row?.rate"
                                            size="large"
                                            disabled
                                            :low-threshold="3"
                                            :high-threshold="5"
                                            text-color="#ff9900"
                                            :colors="['#FF9F9F', '#FF7979', '#FF2929']"
                                            style="flex-shrink: 0"
                                        />
                                        <div v-if="row?.shopReply" style="width: 380px; margin-top: 5px" class="shopReply">
                                            <p style="overflow: hidden; color: #666">
                                                <span style="color: #000"> 商家回复：</span>{{ row?.shopReply }}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column width="420">
                            <template #default="{ row }">
                                <div class="commodity">
                                    <el-image :src="row?.image" style="width: 68px; height: 68px; flex-shrink: 0" />
                                    <div class="commodity__info">
                                        <p class="commodity__info--name">
                                            <span v-if="row.productType === 'VIRTUAL_PRODUCT'" class="virtual_product">虚拟</span>
                                            {{ row?.name }}
                                        </p>
                                        <el-tooltip
                                            v-if="row?.specs?.join(',').length > 20"
                                            class="box-item"
                                            effect="dark"
                                            :content="row?.specs?.join(',')"
                                            placement="top"
                                        >
                                            <p class="commodity__info--spec">{{ row?.specs?.join(',') }}</p>
                                        </el-tooltip>
                                        <p v-else class="commodity__info--spec">{{ row?.specs?.join(',') }}</p>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column width="170">
                            <template #default="{ row }">
                                <div style="text-align: right">
                                    <span style="display: flex; flex-direction: column; justify-content: center; align-items: center">
                                        <el-image :src="row?.avatar" style="width: 52px; height: 52px; border-radius: 50%; flex-shrink: 0" />
                                        <span style="color: #333">{{ row.nickname || `用户${row.userId.slice(-6)}` }}</span>
                                    </span>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column align="right" width="150">
                            <template #default="scope">
                                <div>
                                    <span class="btn" style="padding-right: 14px" @click="handleChoiceness(scope.row)">{{
                                        scope.row.isExcellent ? '取消精选' : '设为精选'
                                    }}</span>
                                    <span v-if="!scope.row.shopReply" class="btn" style="padding-right: 14px" @click="handlerReply(scope.row)"
                                        >回复
                                    </span>
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                </template>
            </el-table-column>
            <el-table-column width="420">
                <template #header>
                    <span class="label">商品</span>
                </template>
            </el-table-column>
            <el-table-column width="170" align="center">
                <template #header>
                    <span class="label">评价用户</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="right" width="150">
                <template #header>
                    <span class="label">操作</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <!-- tab表格部分e -->
    <div class="pagination">
        <PageManage
            :page-size="PageConfig.size"
            :page-num="PageConfig.current"
            :total="PageConfig.total"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </div>
    <el-dialog v-model="dialogVisible" title="商家回复" width="30%" :align-center="true" @close="shopReply = ''">
        <el-input v-model="shopReply" :rows="2" type="textarea" placeholder="" maxlength="100" min="2" :clearable="true" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="handleClose">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
    <div v-show="showVideoControl" class="video-container">
        <div class="video-container__box">
            <div class="video-container__close" @click="videoEnd">
                <QIcon svg name="icon-icon-close1" size="25px" />
            </div>
            <video ref="myVideo" class="video-container__video" :src="videoSrc" controls @canplay="playVideo"></video>
        </div>
    </div>
</template>
<style lang="scss">
.rowstyle {
    td {
        padding: 10px 0 !important;
    }
    td:first-child {
        border-left: 1px solid #ebeef5 !important;
    }
    td:last-child {
        border-right: 1px solid #ebeef5 !important;
    }
}
.rowstyle:hover > td {
    background-color: transparent !important;
}
.row_tr {
    .el-table__cell {
        z-index: auto;
    }
}
</style>
<style lang="scss" scoped>
:deep(.el-image-viewer__next) {
    right: 15%;
}
:deep(.el-image-viewer__prev) {
    left: 15%;
}
:deep(.el-image-viewer__close) {
    top: 12%;
    right: 13%;
}
:deep(.el-image .el-image-viewer__canvas img) {
    max-width: 62% !important;
    max-height: 70% !important;
}
.el-rate__item {
    transition: all 0.3s;
}

.el-rate__item .el-rate__icon {
    display: inline-block;
    width: 20px;
    height: 20px;
    background: linear-gradient(to right, #ff9f9f, #ff7979, #ff2929);
    background-size: 100% 100%;
    background-repeat: no-repeat;
    transition: all 0.3s;
}
.el-rate__item.is-active .el-rate__icon {
    background: linear-gradient(to right, #ff9f9f, #ff7979, #ff2929);
    background-size: 100% 100%;
    background-repeat: no-repeat;
}
:deep(.el-table .cell) {
    padding: 0;
}
@include b(label) {
    display: block;
    padding: 0 14px;
}
@include b(table) {
    transition: height 0.5s;
}
:deep(.table > .el-table__inner-wrapper > .el-table__body-wrapper) {
    margin-top: 12px;
}
@include b(flex) {
    @include flex;
    min-height: 68px;
}
@include b(shopReply) {
    padding: 14px 10px;
    margin-bottom: 10px;
    background: #f3f3f3;
    border-radius: 5px;
    text-align: LEFT;
    color: #333333;
}
@include b(btns) {
    @include flex;
}
:deep(.el-switch__core) {
    height: 30px;
    border-radius: 15px;
    .el-switch__action {
        width: 25px;
        height: 25px;
    }
}
// title样式e
// form样式s
@include b(form) {
    width: 100%;
    background: #f9f9f9;
    margin-bottom: 10px;
    position: relative;
    :deep(.el-form-item) {
        width: 440px;
    }

    :deep(.el-icon) {
        margin-left: 10px;
    }
    @include b(form-flex) {
        position: relative;
        display: flex;
        flex: 3;
        justify-content: space-between;
        flex-wrap: wrap;
        font-size: 14px;
        color: #797979;
    }
    @include e(form-order) {
        margin-left: 15px;
    }
    @include e(from_btn) {
        margin-left: 60px;
    }
}
@include b(from_btn) {
    margin-left: 70px;
}
// form样式e
:deep(.body--header) {
    font-size: 12px;
    color: #000000;
}
.avatar_text {
    cursor: pointer;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1; /* 可以显示的行数，超出部分用...表示*/
    -webkit-box-orient: vertical;
}
@include b(rate_size) {
    :deep(.el-rate__icon) {
        //评分图标大小
        font-size: 25px;
    }
}
@include b(theHead) {
    line-height: 52px;
    background-color: #f7f8fa;
    padding: 0 14px;
    span:first-child::after {
        content: '|';
        margin: 0 10px;
        color: #999;
    }
}

@include b(commodity) {
    @include flex(flex-start);
    padding: 0 14px;
    @include e(info) {
        height: 68px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        margin-left: 5px;
        @include m(name) {
            font-size: 14px;
            width: 261px;
            @include utils-ellipsis(2);
        }
        @include m(spec) {
            margin-top: 3px;
            color: #999;
            @include utils-ellipsis(1);
        }
        @include m(type) {
            margin-top: 5px;
        }
    }
}
@include b(user) {
    padding: 0 14px;
    @include e(info) {
        @include flex(space-between);
        span {
            flex: 1;
            margin: 0 10px;
            @include utils-ellipsis(1);
        }
        @include e(comment) {
            @include utils-ellipsis(4);
        }
    }
}
@include b(btn) {
    color: #555cfd;
    cursor: pointer;
}
@include b(video-container) {
    z-index: 99;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: hsla(0, 0%, 1%, 0.7);
    @include e(box) {
        position: absolute;
        top: 45%;
        left: 50%;
        transform: translate(-50%, -50%);
    }
    @include e(close) {
        display: flex;
        display: flex;
        justify-content: end;
        color: #fff;
        margin-right: -25px;
    }
    @include e(video) {
        max-width: 750px;
        max-height: calc(100vh - 200px);
        opacity: 1;
    }
}
</style>
