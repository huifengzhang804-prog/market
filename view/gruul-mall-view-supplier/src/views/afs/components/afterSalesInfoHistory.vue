<script lang="ts" setup>
import { ref, watch } from 'vue'
import Countdown from './countdown.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import { aRefundWhy, Desc, getAfsOrderStatusCn, getAfsStatusCn, systemInfo } from '@/composables/useAfsStatus'
import { doGetAfssHistory, doGetUserDataByBuyerId, doPutAfsconfirmDeliveryRefunds, doPutAfsRequestAuditByAfsNo } from '@/apis/afs'
import type { AFSSTATUS } from '@/views/afs/types/'
import type { ApiBuyersData, ApiHistory, ApiShopInfo, ProcessingHistory } from '@/views/afs/types/api'

const $emit = defineEmits(['initOrderDetails'])
const { divTenThousand } = useConvert()
const dialogVisible = ref(false)
// 拒绝说明
const refuseToReason = ref('')
const $route = useRoute()
// 买家数据
const buyersData = ref<ApiBuyersData>()
// 卖家数据
const shopInfo = ref<ApiShopInfo>({
    id: '',
    logo: '',
    name: '',
})
// timeout 超时时间 =创建订单时间+3天
const timeout = ref(0)
const processingHistory = ref<ProcessingHistory[]>([])
const currentAfsNo = $route.query.afsNo as string
const currentAfsStatus = ref<keyof typeof AFSSTATUS>()
//售后状态配置对象
const afsConfigObj = ref<Desc>({
    title: '',
    list: '',
    type: '',
    desc: '',
    afs: '',
    msg: '',
    isCountdown: false,
    isShowAgreebtn: false,
    isConsumer: false,
    isSystem: false,
    agreebtnText: '',
})

watch(
    () => currentAfsNo,
    (val) => {
        initBuyersData()
    },
    { immediate: true },
)

function initTimeout(data: ApiHistory) {
    let tim
    switch (data.status) {
        case 'REFUND_REQUEST' || 'RETURN_REFUND_REQUEST':
            tim = data.keyNodeTimeout.requestAgreeTimeout
            break
        // 退货已发出 收货确认倒计时
        case 'RETURNED_REFUND':
            tim = data.keyNodeTimeout.confirmReturnedTimeout
            break
        // 已同意退货退款申请 退货倒计时
        case 'RETURN_REFUND_AGREE':
            tim = data.keyNodeTimeout.returnedTimeout
            break
        default:
            break
    }
    const createOrderTime = new Date(data.updateTime).getTime()
    timeout.value = createOrderTime + Number(tim) * 1000
}

const afsOrderItem = ref<any>({})

/**
 * @description: init买家数据
 * @returns {*}
 */
async function initBuyersData() {
    const data = await initHistory()
    if (data?.[0]) {
        buyersData.value = {
            avatar: data[0].buyerAvatar,
            nickname: data[0].buyerNickname,
            userId: data[0].buyerId,
            phone: data[0].buyerPhone,
        }
    } else {
        buyersData.value = { avatar: '', nickname: '', userId: '', phone: '' }
    }
    initTimeout(data[0])
    afsOrderItem.value = data?.[0]?.afsOrderItem
    afsConfigObj.value = getAfsOrderStatusCn(data[0].status)
    currentAfsStatus.value = data[0].status
    initShopInfo(data[0])
    initRenderHistory(data)
}

/**
 * @description: init商铺店信息
 * @returns {*}
 */
const sellType = ref('')

async function initShopInfo(res: ApiHistory) {
    shopInfo.value.logo = res.shopLogo
    shopInfo.value.id = res.shopId
    shopInfo.value.name = res.shopName
    sellType.value = res?.supplierId ? 'CONSIGNMENT' : ''
    // 获取商品数据
    // const { data } = await doGetSingleCommodity($route.query.productId, res.shopId)
    // sellType.value = data.sellType
    // CONSIGNMENT
}

/**
 * @description:初始化协商历史（协商历史中添加金额和状态） 同意请求超时  确认返回超时   返回超时
 * @param {*} no
 * @returns {*}
 */
async function initHistory() {
    const { code, data } = await doGetAfssHistory<ApiHistory>(currentAfsNo)
    if (code !== 200) {
        ElMessage.error('协商历史获取失败')
        return Promise.reject('协商历史获取失败')
    }
    return Promise.resolve(data)
}

/**
 * @description: 初始化协商历史（协商历史中整合用户头像和商家logo）
 * @returns {*}
 */
const initProcessingHistory = () => {
    if (!processingHistory.value) return
    processingHistory.value = processingHistory.value.map((item) => {
        const afsInfo = getAfsStatusCn(item.afsStatus)
        const isConsumer = afsInfo.isConsumer
        const isSystem = afsInfo.isSystem
        if (isConsumer) {
            return {
                ...item,
                name: buyersData.value?.nickname,
                logo: buyersData.value?.avatar,
                afsStatusCn: afsInfo.desc,
                isConsumer: true,
                type: afsInfo.type,
            }
        } else if (isSystem) {
            return {
                ...item,
                name: systemInfo.name,
                logo: systemInfo.avatar,
                afsStatusCn: afsInfo.desc,
                isConsumer: true,
                type: afsInfo.type,
            }
        }
        return { ...item, name: shopInfo.value.name, logo: shopInfo.value.logo, afsStatusCn: afsInfo.desc, isConsumer: false }
    })
}

function initRenderHistory(data: ApiHistory[]) {
    for (let i = 0; i < data.length; i++) {
        data[i].histories[data[i].histories.length - 1] = Object.assign(data[i].histories[data[i].histories.length - 1], {
            reason: data[i].reason,
            refundAmount: data[i].refundAmount,
        })
    }
    processingHistory.value = data.flatMap((item: ApiHistory) => item.histories)
    initProcessingHistory()
}

/**
 * @description: 同意退款
 * @returns {*}
 */
const handleDetermine = async (type: string) => {
    const msg = currentAfsStatus.value === 'RETURN_REFUND_REQUEST' ? '需等待买家退货' : '将退款给买家'
    try {
        await ElMessageBox.confirm(`${type}后，${msg}`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '关闭',
            type: 'warning',
        })
        AfsRequestAuditByAfsNo(currentAfsStatus.value, true, refuseToReason.value).then(() => {
            initBuyersData()
        })
        $emit('initOrderDetails')
    } catch (error) {
        console.error(error)
    }
}
/**
 * @description: 拒绝
 * @returns {*}
 */
const handleRefused = () => {
    if (!refuseToReason.value) return ElMessage.error({ message: '请输入拒绝理由' })
    AfsRequestAuditByAfsNo(currentAfsStatus.value, false, refuseToReason.value).then(() => {
        initBuyersData()
    })
    $emit('initOrderDetails')
    dialogVisible.value = false
}

/**
 * @description:通过售后单号处理售后请求
 * @param {*} type
 * @param {*} agree
 * @param {*} remark
 * @param {*} receiver
 * @returns {*}
 */
async function AfsRequestAuditByAfsNo(type: keyof typeof AFSSTATUS, agree: boolean, remark: string, receiver?: string) {
    const { code, data } =
        type === 'RETURNED_REFUND'
            ? await doPutAfsconfirmDeliveryRefunds(currentAfsNo, agree, remark, receiver)
            : await doPutAfsRequestAuditByAfsNo(currentAfsNo, agree, remark, receiver)
    if (code !== 200) return ElMessage.error('审核失败')
    ElMessage.success(`${agree ? '已同意申请' : '已拒绝申请'}`)
    $emit('initOrderDetails')
    initHistory()
}
</script>

<template>
    <div class="afterSalesInfo">
        <div class="afterSalesInfo__status">
            <div class="afterSalesInfo__status--title">
                <span>售后状态：{{ $route.query.statusContent }}</span>
                <countdown v-if="afsConfigObj?.isCountdown" :timeout="timeout" :unmounted="afsConfigObj?.isCountdown" />
            </div>
            <div v-if="afsConfigObj?.msg" class="afterSalesInfo__status--prompt">{{ afsConfigObj?.msg }}</div>
            <el-row v-if="afsConfigObj?.isShowAgreebtn">
                <el-button :disabled="!(currentAfsNo && currentAfsStatus)" round type="primary" @click="handleDetermine(afsConfigObj?.agreebtnText)"
                    >{{ afsConfigObj?.agreebtnText }}
                </el-button>
                <el-button :disabled="!(currentAfsNo && currentAfsStatus)" round @click="dialogVisible = true"
                    >拒绝{{ afsConfigObj?.type }}
                </el-button>
            </el-row>
        </div>
        <el-row :gutter="8">
            <el-col :span="12">
                <div class="afterSalesInfo__history">买家信息</div>
                <el-row :gutter="8" style="line-height: 1.5; margin-left: 10px">
                    <el-col :span="24">用户昵称：{{ buyersData?.nickname }}</el-col>
                    <el-col :span="24">手机号：{{ buyersData?.phone }}</el-col>
                    <el-col :span="24">订单号：{{ currentAfsNo }}</el-col>
                </el-row>
            </el-col>
            <el-col :span="12">
                <div class="afterSalesInfo__history">售后商品信息</div>
                <el-row :gutter="8" style="margin-left: 10px">
                    <el-col :span="5">
                        <el-image :src="afsOrderItem?.image" style="width: 80px; height: 80px" />
                    </el-col>
                    <el-col :span="13">
                        <p style="font-weight: 600; line-height: 1.8">{{ afsOrderItem?.productName }}</p>
                        <p>{{ afsOrderItem?.specs?.join(',') }}</p>
                    </el-col>
                    <el-col :span="6">
                        <p style="color: #f00">￥{{ divTenThousand(afsOrderItem?.salePrice || 0) }}</p>
                        <p style="font-size: 0.9em; margin-left: 8px">×{{ afsOrderItem?.num }}</p>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
        <div class="afterSalesInfo__history">协商历史</div>
        <div class="history">
            <div v-for="(historyItem, index) in processingHistory" :key="index" class="history__item">
                <div class="history__item--user">
                    <el-avatar :src="historyItem?.logo" style="flex-shrink: 0" />
                    <span class="name">{{ historyItem?.name }}</span>
                </div>
                <div class="history__item--list">
                    <template v-if="historyItem?.isConsumer">
                        <el-row :gutter="8">
                            <template v-if="historyItem.name === '系统'">
                                <el-col :span="12">{{ historyItem.afsStatusCn }}</el-col>
                                <el-col :span="12">审批时间：{{ historyItem.createTime }}</el-col>
                            </template>
                            <template v-else>
                                <!-- 门店退货 -->
                                <el-col v-if="historyItem?.historyBuyerReturnedInfo?.goStoreRefund" :span="6"
                                    >退货店员名：{{ historyItem?.historyBuyerReturnedInfo?.goStoreRefund?.shopAssistantName }}</el-col
                                >
                                <el-col v-if="historyItem?.historyBuyerReturnedInfo?.goStoreRefund" :span="6"
                                    >退货店员电话：{{ historyItem?.historyBuyerReturnedInfo?.goStoreRefund?.mobile }}</el-col
                                >
                                <!-- 快递退货 -->
                                <el-col v-if="historyItem?.historyBuyerReturnedInfo?.expressRefund?.expressCompany?.expressCompanyName" :span="6"
                                    >物流公司：{{ historyItem?.historyBuyerReturnedInfo?.expressRefund?.expressCompany?.expressCompanyName }}
                                </el-col>
                                <el-col v-if="historyItem?.historyBuyerReturnedInfo?.expressRefund?.expressCompany?.expressNo" :span="6"
                                    >物流单号：{{ historyItem?.historyBuyerReturnedInfo?.expressRefund?.expressCompany?.expressNo }}
                                </el-col>
                                <el-col v-if="historyItem?.historyBuyerReturnedInfo?.expressRefund?.mobile" :span="6"
                                    >联系电话：{{ historyItem.historyBuyerReturnedInfo.expressRefund.mobile }}</el-col
                                >
                                <el-col v-if="historyItem?.type" :span="6">退款类型：{{ historyItem?.type }}</el-col>
                                <el-col v-if="historyItem.refundAmount" :span="4">退款金额：￥{{ divTenThousand(historyItem.refundAmount) }} </el-col>
                                <el-col v-if="historyItem.reason" :span="8"
                                    >退款原因：{{ historyItem.reason && aRefundWhy[historyItem.reason].title }}
                                </el-col>
                                <el-col v-if="historyItem.createTime" :span="6">申请时间：{{ historyItem.createTime }}</el-col>
                            </template>
                        </el-row>
                        <el-row :gutter="8">
                            <el-col v-if="historyItem.remark" :span="12">补充说明：{{ historyItem.remark }}</el-col>
                            <el-col v-if="historyItem?.evidences?.length">
                                <span>拍照凭证：</span>
                                <div style="display: flex">
                                    <el-image
                                        v-for="evidence in historyItem.evidences"
                                        :key="evidence"
                                        :preview-src-list="[evidence]"
                                        :src="evidence"
                                        :z-index="9999"
                                        class="picture__image-box"
                                        fit="cover"
                                        style="width: 56px; height: 56px; border-radius: 6px"
                                    />
                                </div>
                            </el-col>
                        </el-row>
                    </template>
                    <template v-else>
                        <el-row :gutter="8">
                            <el-col :span="12">{{ historyItem.afsStatusCn }}</el-col>
                            <el-col :span="12">审批时间：{{ historyItem.createTime }}</el-col>
                        </el-row>
                        <el-row
                            v-if="['REFUND_REJECT', 'RETURNED_REFUND_REJECT', 'RETURN_REFUND_REJECT'].includes(historyItem.afsStatus)"
                            :gutter="8"
                        >
                            <el-col :span="24">拒绝原因：{{ historyItem.remark }}</el-col>
                        </el-row>
                    </template>
                </div>
            </div>
        </div>
        <el-dialog v-model="dialogVisible" width="30%" @close="refuseToReason = ''">
            <template #header="{ titleId, titleClass }">
                <div class="my-header">
                    <h4 :id="titleId" :class="titleClass">拒绝原因</h4>
                </div>
            </template>
            <el-input v-model="refuseToReason" :rows="4" height="100px" max="140" maxlength="140" placeholder="请输入拒绝原因" type="textarea">
            </el-input>
            <template #footer>
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleRefused">确 定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<style lang="scss" scoped>
@include b(picture) {
    @include flex(flex-start, flex-start);
    @include e(image-box) {
        @include flex;
        margin-right: 15px;
        margin-top: 15px;
    }
}

@include b(afterSalesInfo) {
    padding: 0 16px;
    overflow-y: scroll;
    @include e(status) {
        padding: 21px 16px;
        background: #f7f8fa;
        font-size: 18px;
        font-weight: bold;
        color: #333333;
        @include m(title) {
            @include flex(space-between);
        }

        @include m(prompt) {
            font-size: 14px;
            font-weight: normal;
            color: #838383;
            line-height: 40px;
        }
    }
    @include e(history) {
        font-size: 14px;
        color: #333333;
        font-weight: bold;
        margin-top: 28px;
        margin-bottom: 10px;
    }
}

@include b(red-bold) {
    color: #f00;
    font-weight: bold;
    font-size: 14px;
}

@include b(history) {
    @include e(item) {
        padding: 15px 0;
        border-bottom: 1px solid #efefef;
        @include m(user) {
            display: flex;
            align-items: center;
            @include b(name) {
                flex: 1;
                margin-left: 20px;
            }
        }
        @include m(list) {
            line-height: 2;
        }
    }
}
</style>
