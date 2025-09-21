<script setup lang="ts">
import { reactive, Ref, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, TabPaneName } from 'element-plus'
import Search, { type SearchType } from './components/Search.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { doGetinvoiceRequestList, doGetinvoiceDetail, doPostrefuseInvoiceRequest, doPostinvoiceAttachment } from '@/apis/invoice'
import PageManage from '@/components/PageManage/index.vue'
import InvoicingDialogMain from './components/InvoicingDialogMain.vue'
import detailDialogMain from './components/detailDialogMain.vue'

const { divTenThousand } = useConvert()
const $shopInfoStore = useShopInfoStore()
const tableList = reactive({
    page: { size: 10, current: 1 },
    list: [],
    total: 0,
})
const searchParams = ref({
    id: '',
    header: '',
    taxIdentNo: '',
    orderNo: '',
    invoiceHeaderType: '',
    date: '',
    applicationStartTime: '',
    applicationEndTime: '',
    invoiceStatus: '' as TabPaneName,
    invoiceOwnerType: 'SHOP',
})
const invoiceHeaderType = {
    PERSONAL: '个人',
    ENTERPRISE: '企业',
}
const invoiceStatus = {
    REQUEST_IN_PROCESS: '开票中',
    SUCCESSFULLY_INVOICED: '开票成功',
    FAILED_INVOICE_REQUEST: '开票失败',
    CLIENT_CANCEL_REQUEST: '用户撤销',
}
const refuseDialog = ref(false) //拒绝弹窗
const form = ref({ RejectReason: '' }) //拒绝原因
const Dialogid = ref() //弹窗所需发票id
const InvoicingDialog = ref(false) //开票弹窗
const InvoicingDialogTypeType = ref(true) //(true开票| false重新上传发票）
const PdfList = ref<string[]>([]) //开票所需pdf
const detailDialog = ref(false) //详情弹窗
const invoiceDetail = ref() //发票详情

async function initList() {
    const { code, data, msg } = await doGetinvoiceRequestList({
        ...searchParams.value,
        ...tableList.page,
    })
    if (code !== 200) {
        ElMessage.error(msg || '获取申请列表失败')
        return
    }
    if (data) {
        tableList.total = data.total
        tableList.list = data.records
    }
}
initList()

const getSearch = (e: SearchType) => {
    tableList.page.current = 1
    if (e.date.length > 0) {
        searchParams.value.applicationStartTime = e.date[0]
        searchParams.value.applicationEndTime = e.date[1]
    }
    const params = { ...e }
    delete (params as { date?: any }).date
    delete (params as { invoiceTime?: any }).invoiceTime
    searchParams.value = { ...searchParams.value, ...params }
    initList()
}
//tab点击事件
const handleTabClick = (status: TabPaneName) => {
    searchParams.value.invoiceStatus = status
    initList()
}
const handledetail = async (id: string) => {
    const { data, code, msg } = await doGetinvoiceDetail(id)
    if (code !== 200) {
        ElMessage.error(msg || '获取发票设置失败')
        return
    }
    invoiceDetail.value = data
    detailDialog.value = true
    Dialogid.value = id
}
/**
 * @description: 打开拒绝弹窗
 * @returns
 */
const openRefuseDialog = async (id: string) => {
    Dialogid.value = id
    refuseDialog.value = true
}
/**
 * @description: 拒绝弹窗确认
 * @returns
 */
const formRef = ref<FormInstance>()
const handleRefuse = async () => {
    try {
        const validate = await formRef.value?.validate()
        if (!validate) return
        const { data, code, msg } = await doPostrefuseInvoiceRequest({
            id: Dialogid.value,
            denialReason: form.value.RejectReason,
        })
        if (code !== 200) {
            ElMessage.error(msg || '拒绝失败')
            return
        }
        refuseDialog.value = false
        initList()
    } catch (error) {
        console.error('error', error)
    }
}
/**
 * @description: 拒绝弹窗关闭
 * @returns
 */
const refuseDialogClose = () => {
    Dialogid.value = ''
    form.value.RejectReason = ''
}
/**
 * @description: 详情弹窗打开
 * @returns
 */
const handleConfirm = (id: string, header: string, invoiceAmount: string, taxIdentNo: string, attachments: string[]) => {
    Dialogid.value = id
    InvoicingDialogTypeType.value = true
    invoiceDetail.value = { header, invoiceAmount, taxIdentNo, attachments }
    InvoicingDialog.value = true
}
/**
 * @description: 重新上传发票打开弹窗
 * @returns
 */
const againAgree = () => {
    InvoicingDialogTypeType.value = false
    detailDialog.value = false
    InvoicingDialog.value = true
}
/**
 * @description: 上传pdf
 * @returns
 */
const PdfListChange = (e: string[]) => {
    PdfList.value = e
}
/**
 * @description: 同意开票
 * @returns
 */
const handleAgree = async () => {
    if (!PdfList.value.length) return ElMessage.error('请上传发票')

    const { data, code, msg } = await doPostinvoiceAttachment({
        invoiceRequestId: Dialogid.value,
        shopId: $shopInfoStore.getterShopInfo.id,
        invoiceAttachmentUrl: PdfList.value,
    })
    if (code !== 200) {
        ElMessage.error(msg || '开票失败')
        return
    }
    InvoicingDialog.value = false
    initList()
}
/**
 * @description: 同意开票弹窗关闭
 * @returns
 */
const InvoicingDialogClose = () => {
    Dialogid.value = ''
    PdfList.value = []
    invoiceDetail.value = {}
}

const rules: any = {
    RejectReason: [{ required: true, message: '拒绝原因为必填项', trigger: 'blur' }],
}

/**
 * @description: 分页器
 * @param {*} value
 * @returns {*}
 */
const handleSizeChange = (value: number) => {
    tableList.page.current = value
    initList()
}
const handleCurrentChange = (value: number) => {
    tableList.page.size = value
    initList()
}
</script>

<template>
    <div class="search_container">
        <el-config-provider value-on-clear="" :empty-values="[undefined, null]">
            <Search @on-search-params="getSearch" />
        </el-config-provider>
    </div>
    <div class="tab_container">
        <el-tabs v-model="searchParams.invoiceStatus" @tab-change="handleTabClick">
            <el-tab-pane label="全部" name=""></el-tab-pane>
            <el-tab-pane label="开票中" name="REQUEST_IN_PROCESS"></el-tab-pane>
            <el-tab-pane label="开票成功" name="SUCCESSFULLY_INVOICED"></el-tab-pane>
            <el-tab-pane label="开票失败" name="FAILED_INVOICE_REQUEST"></el-tab-pane>
        </el-tabs>
    </div>
    <div class="table_container">
        <el-table
            empty-text="暂无数据~"
            :data="tableList.list"
            style="width: 100%"
            :header-cell-style="{
                backgroundColor: '#F6F8FA',
                color: '#333',
                height: '48px',
            }"
            :cell-style="{ color: '#666', height: '60px' }"
        >
            <el-table-column label="申请单号" width="200">
                <template #default="{ row }">{{ row.id }} </template>
            </el-table-column>
            <el-table-column label="发票类型" width="200">
                <template #default="{ row }">
                    <div>{{ row.invoiceType === 'VAT_GENERAL' ? '增值税普通发票' : '增值税专用发票' }}</div>
                </template>
            </el-table-column>
            <el-table-column label="发票抬头" width="200">
                <template #default="{ row }">
                    <el-tooltip :content="row.header" placement="top">
                        <div class="ellipsis">{{ row.header }}</div>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column label="税号" width="240">
                <template #default="{ row }">
                    <div>{{ row.taxIdentNo }}</div>
                </template>
            </el-table-column>
            <el-table-column label="开票金额" width="150">
                <template #default="{ row }">
                    <div style="color: #fd0505">{{ divTenThousand(row.invoiceAmount).toFixed(2) }}</div>
                </template>
            </el-table-column>
            <el-table-column label="开票状态" width="120">
                <template #default="{ row }">
                    <div
                        :style="{
                            color:
                                row.invoiceStatus === 'REQUEST_IN_PROCESS'
                                    ? '#FD9224'
                                    : row.invoiceStatus === 'SUCCESSFULLY_INVOICED'
                                    ? '#00BB2C'
                                    : '#666',
                        }"
                    >
                        {{ invoiceStatus[row.invoiceStatus as keyof typeof invoiceStatus] }}
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="关联订单" width="220">
                <template #default="{ row }">
                    <div>{{ row.orderNo }}</div>
                </template>
            </el-table-column>
            <el-table-column label="邮箱" width="200">
                <template #default="{ row }">
                    <div>{{ row.email }}</div>
                </template>
            </el-table-column>
            <el-table-column label="申请时间" width="200">
                <template #default="{ row }">
                    <div>{{ row.applicationTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="开票时间" width="200">
                <template #default="{ row }">
                    <div>{{ row.invoiceTime }}</div>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right" align="right">
                <template #default="{ row }">
                    <el-link type="primary" @click="handledetail(row.id)">详情</el-link>
                    <span v-if="row.invoiceStatus === 'REQUEST_IN_PROCESS'">
                        <el-link
                            type="primary"
                            style="margin-left: 10px"
                            @click="handleConfirm(row.id, row.header, row.invoiceAmount, row.taxIdentNo, row.attachments)"
                            >开票</el-link
                        >
                        <el-link type="danger" style="margin-left: 10px" @click="openRefuseDialog(row.id)">拒绝</el-link>
                    </span>
                </template>
            </el-table-column>
        </el-table>
    </div>

    <PageManage
        :page-num="tableList.page.current"
        :page-size="tableList.page.size"
        :total="tableList.total"
        @handle-current-change="handleSizeChange"
        @handle-size-change="handleCurrentChange"
    />

    <!-- 拒绝弹窗 -->
    <el-dialog v-model="refuseDialog" title="拒绝" width="500px" center @close="refuseDialogClose">
        <el-form ref="formRef" :model="form" class="m-t-16" :rules="rules">
            <el-form-item label="拒绝原因" prop="RejectReason" class="m-b-10">
                <el-input v-model="form.RejectReason" maxlength="15" placeholder="请输入拒绝原因" show-word-limit />
            </el-form-item>
        </el-form>

        <template #footer>
            <el-button @click="refuseDialog = false">取消</el-button>
            <el-button type="primary" @click="handleRefuse()"> 确认 </el-button>
        </template>
    </el-dialog>

    <!-- 开票弹窗 -->
    <el-dialog
        v-model="InvoicingDialog"
        :title="InvoicingDialogTypeType ? '开票' : '重新开票'"
        width="800px"
        center
        destroy-on-close
        @close="InvoicingDialogClose"
    >
        <InvoicingDialogMain :invoice-detail="invoiceDetail" @handle-pdf-list-change="PdfListChange" />
        <template #footer>
            <el-button @click="InvoicingDialog = false">取消</el-button>
            <el-button type="primary" @click="handleAgree"> 确认 </el-button>
        </template>
    </el-dialog>
    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialog" title="详情" width="1000px" center>
        <detailDialogMain :invoice-detail="invoiceDetail" />
        <template #footer>
            <el-button v-if="invoiceDetail.invoiceStatus === 'SUCCESSFULLY_INVOICED'" type="primary" @click="againAgree"> 重新开票 </el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@include b(m-b-10) {
    margin-bottom: 10px;
}
@include b(m-t-16) {
    margin-top: 16px;
}
@include b(ellipsis) {
    width: 180px;
    @include utils-ellipsis(1);
}
</style>
