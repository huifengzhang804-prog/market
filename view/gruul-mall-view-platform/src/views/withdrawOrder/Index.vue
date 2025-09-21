<script setup lang="ts">
import SchemaForm from '@/components/SchemaForm.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import { doGetWithdrawList, doGetCheckWithdraw } from '@/apis/finance'
import RemarkPopup from '@/components/remark/remark-popup.vue'
import { ElMessage, FormInstance, FormItemRule } from 'element-plus'
import useClipboard from 'vue-clipboard3'
import { doPostExportWithdrawData } from '@/apis/overview'
import ElTableEmpty from '@/components/element-plus/el-table/ElTableEmpty/index.vue'
import type { Arrayable } from 'element-plus/es/utils'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import DescriptionDialog from './description-dialog.vue'

interface SearchFormType {
    name: string
    id: string
    startDate: string
    endDate: string
    status: 'APPLYING' | string
    type: string
    tempDate: string
    offline: string | boolean
    applyUserPhone: string
    auditUserPhone: string
    tradeDate: string
    tradeStartDate: Date | undefined
    tradeEndDate: Date | undefined
}

const { divTenThousand } = useConvert()
const { toClipboard } = useClipboard()
const tempDate = ref([])
const showDescriptionDialog = ref(false)
const searchForms = reactive<SearchFormType>({
    name: '',
    id: '',
    startDate: '',
    endDate: '',
    status: 'APPLYING',
    type: '',
    tempDate: '',
    applyUserPhone: '',
    auditUserPhone: '',
    offline: '', // 支付方式 true 线下打款 false 线上打款
    tradeDate: '',
    tradeStartDate: undefined, //交易开始时间
    tradeEndDate: undefined, //交易结束时间
})
const pageConfig = reactive({
    current: 1,
    size: 10,
    total: 0,
})
const remarkValue = ref('')
const saveRemarkType = ref('single')
const remarkDialog = ref(false)
const selectOption = [
    {
        label: '全部',
        value: '',
    },
    {
        label: '交易提现(店铺)',
        value: 'SHOP',
    },
    {
        label: '交易提现(供应商)',
        value: 'SUPPLIER',
    },
    {
        label: '佣金提现(分销商)',
        value: 'DISTRIBUTOR',
    },
    {
        label: '返利提现(用户)',
        value: 'REBATE',
    },
]
const currentNo = ref('')
const withdrawList = ref<any[]>([])
const tableSelectedArr = ref<any[]>([])
const ids = ref<string[]>([])
const dialogTableVisible = ref(false)
// 付款凭证图片
const rowPaymentVoucher = ref<string[]>([])
// 审核表单
const checkForm = reactive({
    showDialog: false,
    pass: false,
    offline: false,
    reason: '',
    isAgree: false,
    amount: 0,
    paymentVoucher: [],
})
const sortEnum = ref('')
// 银行卡不支持线上打款
const notSupportOnline = ref(false)
// 表单配置项
const columns = [
    {
        label: '申请单号',
        prop: 'id',
        valueType: 'copy',
        fieldProps: {
            placeholder: '请填写申请编号',
        },
    },
    {
        label: '申请人手机',
        labelWidth: '85',
        prop: 'applyUserPhone',
        valueType: 'input', // 改为input类型
        fieldProps: {
            placeholder: '请填写申请人手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '提现类型',
        prop: 'type',
        valueType: 'select',
        options: selectOption,
        fieldProps: {
            placeholder: '请选择体现类型',
        },
    },
    {
        label: '申请时间',
        prop: 'tempDate',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            valueFormat: 'YYYY-MM-DD',
        },
    },
    {
        label: '审批员手机',
        labelWidth: '85',
        prop: 'auditUserPhone',
        valueType: 'input',
        fieldProps: {
            placeholder: '请输入审批员手机',
            maxlength: 11,
            type: 'text', // 使用text类型配合v-model.number
            'v-model.number': '', // 只允许输入数字
            oninput: "value=value.replace(/[^\\d]/g,'')", // 只允许输入数字
            pattern: '^1[3-9]\\d{9}$', // 手机号码格式验证
        },
    },
    {
        label: '支付方式',
        prop: 'offline',
        valueType: 'select',
        options: [
            { label: '全部', value: '' },
            { value: true, label: '线下打款' },
            { value: false, label: '线上打款' },
        ],
        fieldProps: {
            placeholder: '请选择支付方式',
        },
    },
    {
        label: '打款时间',
        prop: 'tradeDate',
        valueType: 'date-picker',
        fieldProps: {
            type: 'daterange',
            startPlaceholder: '开始时间',
            endPlaceholder: '结束时间',
            valueFormat: 'YYYY-MM-DD',
            onChange: (data: Array<Date>) => {
                searchForms.tradeStartDate = data ? data[0] : undefined
                searchForms.tradeEndDate = data ? data[1] : undefined
            },
        },
    },
]

const formRef = ref<FormInstance | null>(null)
const rules: Partial<Record<string, Arrayable<FormItemRule>>> = {
    paymentVoucher: [
        { required: true, message: '请选择支付方式', trigger: 'change' },
        {
            validator: (_, val, callback) => {
                if (checkForm.offline) {
                    if (!val) {
                        callback(new Error('请上传凭证'))
                    }
                }
                callback()
            },
            trigger: 'change',
        },
    ],
}
// 处理选择变化
function handleSelectionChange(selection: any[]) {
    tableSelectedArr.value = selection
}

initList()

const handleChangeCurrent = (e: number) => {
    pageConfig.current = e
    initList()
}
const handleChangeSize = (e: number) => {
    pageConfig.current = 1
    pageConfig.size = e
    initList()
}
const handleChangeTab = () => {
    initList()
}

const handleSearch = () => {
    if (Array.isArray(searchForms.tempDate)) {
        searchForms.startDate = searchForms.tempDate[0]
        searchForms.endDate = searchForms.tempDate[1]
    }
    initList()
}

/**
 * 关闭审核弹窗回调
 */
const handleCloseCheckDialog = () => {
    checkForm.reason = ''
    checkForm.offline = false
}
/**
 * 重置搜索条件
 */
const handleReset = () => {
    searchForms.type = ''
    searchForms.endDate = ''
    searchForms.startDate = ''
    searchForms.id = ''
    searchForms.name = ''
    searchForms.applyUserPhone = ''
    searchForms.auditUserPhone = ''
    searchForms.offline = ''
    tempDate.value = []
    initList()
}

/**
 * @LastEditors: vikingShip
 * @description: 显示备注弹窗记录no
 */
const handleShowRemark = (row: { no: string; remark: string }) => {
    ids.value = [row.no]
    if (row.remark) {
        remarkValue.value = row.remark
    }
    saveRemarkType.value = 'single'
    remarkDialog.value = true
}

/**
 * 批量备注
 */
const handleBatchRemark = () => {
    if (tableSelectedArr.value.length) {
        ids.value = tableSelectedArr.value.map((item) => item.no)
        saveRemarkType.value = 'batch'
        remarkDialog.value = true
        return
    }
    ElMessage.error('请先选择订单')
}
const handlePass = (no: string, status: string, amount: number) => {
    // 判断结算是否支持线上打款
    notSupportOnline.value = status === 'BANK_CARD'
    checkForm.offline = status === 'BANK_CARD' ? true : false
    checkForm.amount = amount
    checkForm.showDialog = true
    checkForm.isAgree = true
    checkForm.pass = true
    currentNo.value = no
}
const handleRefuse = (no: string) => {
    checkForm.showDialog = true
    checkForm.isAgree = false
    checkForm.pass = false
    currentNo.value = no
}
const handleCheckConfirm = async () => {
    if (!checkForm.isAgree && !checkForm.reason) return ElMessage.error({ message: '请填写拒绝说明' })
    if (!formRef.value) return
    const isValida = await formRef.value.validate()
    if (!isValida) return
    const { code, msg } = await doGetCheckWithdraw(currentNo.value, {
        pass: checkForm.pass,
        offline: checkForm.offline,
        reason: checkForm.reason,
        paymentVoucher: checkForm.paymentVoucher.join(','),
    })
    if (code === 200) {
        ElMessage.success('操作成功')
        checkForm.showDialog = false
        checkForm.paymentVoucher = []
        initList()
    } else {
        ElMessage.error(msg ? msg : '操作失败')
    }
}
// 金额排序
const sortTableList = (lable: string) => {
    if (lable === '提现金额(元)') {
        sortEnum.value = sortEnum.value === 'WITHDRAW_AMOUNT_ASC' ? 'WITHDRAW_AMOUNT_DESC' : 'WITHDRAW_AMOUNT_ASC'
        initList()
    } else if (lable === '申请时间') {
        sortEnum.value = sortEnum.value === 'APPLY_TIME_ASC' ? 'APPLY_TIME_DESC' : 'APPLY_TIME_ASC'
        initList()
    } else if (lable === '审批时间') {
        sortEnum.value = sortEnum.value === 'AUDIT_TIME_ASC' ? 'AUDIT_TIME_DESC' : 'AUDIT_TIME_ASC'
        initList()
    }
}
async function initList() {
    const { code, data } = await doGetWithdrawList({
        ...searchForms,
        ...pageConfig,
        tempDate: undefined,
        tradeDate: undefined,
        sortEnum: sortEnum.value,
    })
    if (code && code === 200) {
        withdrawList.value = data.records
        pageConfig.total = data.total
    } else {
        ElMessage.error('获取列表失败')
    }
}
function convertType(val: string) {
    const type: { [x: string]: string } = {
        ALIPAY: '支付宝',
        WECHAT: '微信',
        BANK_CARD: '银行卡',
    }
    return type[val]
}

const convertOwnerType = (val: string) => {
    const ownerTypeMap: { [x: string]: string } = {
        CONSUMER: '普通会员',
        DISTRIBUTOR: '佣金提现(分销商)',
        SHOP: '交易提现(店铺)',
        REBATE: '返利提现(用户)',
        SUPPLIER: '交易提现(供应商)',
    }
    return ownerTypeMap[val]
}
const handleRemarkSuccess = () => {
    tableSelectedArr.value = []
    initList()
}
const handleCopy = async (drawType: any) => {
    let str = ''
    if (drawType?.type === 'WECHAT') {
        str = `openid：${drawType?.openid}`
    } else if (drawType?.type === 'ALIPAY') {
        str = `姓名：${drawType?.name}\n支付宝账号：${drawType?.alipayAccount}`
    } else {
        str = `持卡人：${drawType?.name}\n开户行：${drawType?.bank}\n银行卡号：${drawType?.cardNo}`
    }
    try {
        await toClipboard(str)
        ElMessage.success('复制成功')
    } catch (e) {
        ElMessage.error('复制失败')
    }
}
// 导出
const handleExport = async () => {
    let params: any = {}
    if (tableSelectedArr.value.length) {
        params.exportNos = tableSelectedArr.value.map((item) => item.no)
    } else {
        searchForms.startDate = searchForms.tempDate[0]
        searchForms.endDate = searchForms.tempDate[1]
        params = searchForms
    }
    const { code, msg } = await doPostExportWithdrawData(params)
    if (code === 200) {
        ElMessage.success({ message: msg || '导出成功' })
    } else {
        ElMessage.error({ message: msg || '导出失败' })
    }
}

/**
 * 结算线下打款上传凭证
 */
const addNewMainSuccess = (response: string) => {
    if (typeof response === 'string') {
        ;(checkForm.paymentVoucher as string[]).push(response)
    }
}

/**
 * 显示支付凭证
 */
const handleShowPaymentVoucher = (row: any) => {
    dialogTableVisible.value = true
    rowPaymentVoucher.value = row.paymentVoucher.split(',')
}

/**
 * 隐藏支付凭证
 */
const handleClosePaymentVoucher = () => {
    dialogTableVisible.value = false
}
</script>

<template>
    <div class="search_container">
        <el-config-provider :empty-values="[undefined, null]">
            <SchemaForm v-model="searchForms" :columns="columns" label-width="90" @searchHandle="handleSearch" @handleReset="handleReset">
                <template #otherOperations>
                    <el-button round type="primary" @click="handleExport">导出</el-button>
                </template>
            </SchemaForm>
        </el-config-provider>
    </div>
    <div class="grey_bar"></div>
    <div class="tab_container" style="position: relative">
        <el-tabs v-model="searchForms.status" class="demo-tabs" style="margin: 10px 0 0 0" @tab-change="handleChangeTab">
            <el-tab-pane label="待审核" name="APPLYING" />
            <el-tab-pane label="提现中" name="PROCESSING" />
            <el-tab-pane label="已打款" name="SUCCESS" />
            <el-tab-pane label="提现失败" name="CLOSED" />
            <el-tab-pane label="已拒绝" name="FORBIDDEN" />
        </el-tabs>
        <QIcon class="last" name="icon-wenhao" size="18px" @click="showDescriptionDialog = true"></QIcon>
    </div>
    <div class="handle_container">
        <div class="withdraw__tool">
            <div class="withdraw__tool--btns">
                <el-button @click="handleBatchRemark">批量备注</el-button>
            </div>
        </div>
    </div>
    <div class="table_container">
        <el-table
            :data="withdrawList"
            :header-row-style="{ color: '#333' }"
            :header-cell-style="{ background: '#F7F8FA', height: '48px' }"
            :cell-style="{ color: '#333333' }"
            @selection-change="handleSelectionChange"
        >
            <template #empty>
                <ElTableEmpty />
            </template>
            <el-table-column type="selection" width="35" />
            <el-table-column label="申请单号" width="180">
                <template #default="{ row }">
                    <span>{{ row.no }}</span>
                </template>
            </el-table-column>
            <el-table-column label="申请时间" width="200">
                <template #default="{ row }">
                    <span>{{ row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="提现金额(元)" width="110">
                <template #default="{ row }">
                    <div style="color: red">
                        <span style="font-size: 16px">{{ String(divTenThousand(row.drawType.amount)).split('.')[0] }}</span>
                        <span v-if="String(divTenThousand(row.drawType.amount)).split('.')[1]"
                            >.{{ String(divTenThousand(row.drawType.amount)).split('.')[1] }}</span
                        >
                        <QIcon
                            v-if="row.status === 'SUCCESS' && row.offline && row.ownerAvatar"
                            name="icon-pingzhengzhongxin"
                            svg
                            size="24px"
                            style="position: relative; top: 2px; margin-left: 3px; cursor: pointer"
                            @click="handleShowPaymentVoucher(row)"
                        />
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="申请人" width="170">
                <template #default="{ row }">
                    <div class="info">
                        <div class="info__right">
                            <div class="info__right--name ellipsis">{{ row.ownerName }}</div>
                            <div>{{ row.contract }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="提现方式">
                <template #default="{ row }">
                    <el-popover :width="250">
                        <template #reference>
                            <el-link type="primary">{{ convertType(row.drawType.type) }}</el-link>
                        </template>
                        <div class="draw-type">
                            <div class="draw-type-copy" @click="handleCopy(row.drawType)">复制</div>
                            <template v-if="row.drawType.type === 'WECHAT'">
                                <p class="draw-type-line">openid：{{ row?.drawType?.openid }}</p>
                            </template>
                            <template v-else-if="row.drawType.type === 'ALIPAY'">
                                <p class="draw-type-line">姓名：{{ row?.drawType?.name }}</p>
                                <p class="draw-type-line">支付宝账号：{{ row?.drawType?.alipayAccount }}</p>
                            </template>
                            <template v-else>
                                <p class="draw-type-line">持卡人：{{ row?.drawType?.name }}</p>
                                <p class="draw-type-line">开户行：{{ row?.drawType?.bank }}</p>
                                <p class="draw-type-line">银行卡号：{{ row?.drawType?.cardNo }}</p>
                            </template>
                        </div>
                    </el-popover>
                </template>
            </el-table-column>
            <el-table-column label="提现类型" width="150">
                <template #default="{ row }">
                    <div>{{ convertOwnerType(row.ownerType) }}</div>
                </template>
            </el-table-column>
            <el-table-column v-if="!['APPLYING', 'FORBIDDEN', 'PROCESSING'].includes(searchForms.status)" label="支付方式" width="150">
                <template #default="{ row }">
                    <div>{{ row.offline ? '线下打款' : '线上打款' }}</div>
                </template>
            </el-table-column>
            <el-table-column v-if="['SUCCESS', 'FORBIDDEN'].includes(searchForms.status)" label="审核员" width="150">
                <template #default="{ row }">
                    <div>
                        <p>{{ row.auditUserName }}</p>
                        <p>{{ row.auditUserPhone }}</p>
                    </div>
                </template>
            </el-table-column>
            <el-table-column v-if="['SUCCESS', 'FORBIDDEN', 'CLOSED'].includes(searchForms.status)" label="审批时间" width="170">
                <template #default="{ row }">
                    <div>{{ row.auditTime }}</div>
                </template>
            </el-table-column>
            <el-table-column v-if="searchForms.status === 'SUCCESS'" width="170" label="打款时间">
                <template #default="{ row }">
                    <span>{{ row.tradeTime }}</span>
                </template>
            </el-table-column>
            <el-table-column v-if="searchForms.status === 'CLOSED'" width="170" label="失败时间">
                <template #default="{ row }">
                    <span>{{ row.tradeTime }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="right" fixed="right" width="150">
                <template #default="{ row }">
                    <template v-if="searchForms.status === 'APPLYING'">
                        <el-button link type="primary" @click="handlePass(row.no, row.drawType.type, row.drawType.amount)">结算</el-button>
                        <el-button link type="danger" @click="handleRefuse(row.no)">拒绝</el-button>
                    </template>
                    <el-tooltip v-if="searchForms.status === 'FORBIDDEN'" class="box-item" effect="dark" placement="bottom-end">
                        <template #content>
                            <div style="max-width: 440px">{{ row.reason }}</div>
                        </template>
                        <el-button link type="primary">拒绝原因</el-button>
                    </el-tooltip>
                    <el-tooltip class="box-item" effect="dark" :content="row.remark" placement="bottom-end" :disabled="!row.remark">
                        <template #content>
                            <div style="max-width: 440px">{{ row.remark }}</div>
                        </template>
                        <el-button link type="primary" @click="handleShowRemark(row)">备注</el-button>
                    </el-tooltip>
                    <el-tooltip v-if="row.failReason" class="box-item" effect="dark" placement="bottom-end">
                        <template #content>
                            <div style="max-width: 440px">{{ row.failReason }}</div>
                        </template>
                        <el-button v-if="searchForms.status === 'CLOSED'" link type="primary">失败原因</el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
    </div>
    <BetterPageManage
        :page-size="pageConfig.size"
        :page-num="pageConfig.current"
        :total="pageConfig.total"
        @handle-size-change="handleChangeSize"
        @handle-current-change="handleChangeCurrent"
    />
    <remark-popup
        v-model:isShow="remarkDialog"
        v-model:ids="ids"
        v-model:remark="remarkValue"
        remark-type="WITHDRAW"
        @success="handleRemarkSuccess"
    />
    <!-- 审核弹窗 -->
    <el-dialog v-model="checkForm.showDialog" width="500px" :title="checkForm.isAgree ? '提现审核' : '拒绝原因'" @close="handleCloseCheckDialog">
        <el-form ref="formRef" :model="checkForm" :rules="rules" label-width="80px" label-position="right">
            <el-form-item v-if="checkForm.isAgree" label="提现金额"
                ><span style="color: red"
                    >￥<text style="font-size: 18px">{{ (checkForm.amount / 10000).toFixed(2) }}</text></span
                ></el-form-item
            >
            <el-form-item v-if="checkForm.isAgree" label="支付方式">
                <el-radio-group v-model="checkForm.offline">
                    <el-radio :value="false" :disabled="notSupportOnline">线上打款</el-radio>
                    <el-radio :value="true">线下打款</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item v-if="checkForm.isAgree && checkForm.offline" label="付款凭证" prop="paymentVoucher">
                <div>
                    <div style="color: #999; font-size: 12px">至少1张，每张2MB以内，jpg、jpeg、png</div>
                    <div style="display: flex; flex-wrap: wrap">
                        <div v-for="(item, index) in checkForm.paymentVoucher" :key="item">
                            <q-upload
                                v-model:src="checkForm.paymentVoucher[index]"
                                :isCropper="false"
                                style="margin-right: 10px"
                                :format="{ size: 2, width: 10000, height: 10000, types: ['image/png', 'image/jpg', 'image/jpeg'] }"
                                :cropper="false"
                                :height="100"
                                :width="100"
                            />
                        </div>
                        <q-upload
                            v-show="checkForm.paymentVoucher.length < 6"
                            :format="{ size: 2, width: 10000, height: 10000, types: ['image/png', 'image/jpg', 'image/jpeg'] }"
                            :isCropper="false"
                            :height="100"
                            :width="100"
                            @change="addNewMainSuccess"
                        />
                    </div>
                </div>
            </el-form-item>
            <el-form-item v-if="!checkForm.isAgree" label="拒绝原因" prop="explain">
                <el-input v-model="checkForm.reason" placeholder="请输入拒绝原因" :maxlength="20" show-word-limit />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="checkForm.showDialog = false">取消</el-button>
            <el-button type="primary" @click="handleCheckConfirm"> 确定 </el-button>
        </template>
    </el-dialog>
    <!-- 付款凭证 -->
    <el-image-viewer v-if="dialogTableVisible" :url-list="rowPaymentVoucher" @close="handleClosePaymentVoucher" />
    <!-- 提现说明 -->
    <el-dialog v-model="showDescriptionDialog" title="提现工单说明" :width="950" top="6vh" center>
        <description-dialog />
    </el-dialog>
</template>

<style lang="scss" scoped>
* {
    font-size: 14px;
}
@include b(withdraw) {
    @include e(tool) {
        height: 40px;
        @include flex(space-between);
    }
    @include e(table) {
        overflow-y: auto;
        transition: height 0.5s;
        scrollbar-width: none;
        -ms-overflow-style: none;
        &::-webkit-scrollbar {
            display: none;
        }
    }
    @include e(table-up) {
        height: calc(100vh - 450px);
    }
}
@include b(header) {
    width: 100%;
    font-size: 12px;
    color: #000;
    @include flex(space-between);
}
@include b(info) {
    @include e(img) {
        width: 68px;
        height: 68px;
        margin-right: 6px;
        flex-shrink: 0;
    }
    @include e(right) {
        @include m(name) {
            margin-bottom: 4px;
        }
    }
}
.ellipsis {
    @include utils-ellipsis(1);
    width: 130px;
}
.draw-type {
    position: relative;
    &-copy {
        color: #1890ff;
        text-align: right;
        padding-bottom: 8px;
        cursor: pointer;
    }
}
.last {
    position: absolute;
    right: 20px;
    top: 38%;
    z-index: 6;
    cursor: pointer;
    color: #303133;
}
.demo-image__error .block {
    padding: 30px 0;
    text-align: center;
    border-right: solid 1px var(--el-border-color);
    display: inline-block;
    width: 49%;
    box-sizing: border-box;
    vertical-align: top;
}
.demo-image__error .demonstration {
    display: block;
    color: var(--el-text-color-secondary);
    font-size: 14px;
    margin-bottom: 20px;
}
.demo-image__error .el-image {
    padding: 0 5px;
    max-width: 100%;
    max-height: 300px;
    width: 100%;
    height: 300px;
}

.demo-image__error .image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    background: var(--el-fill-color-light);
    color: var(--el-text-color-secondary);
    font-size: 30px;
}
.demo-image__error .image-slot .el-icon {
    font-size: 30px;
}
</style>
