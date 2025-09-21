<script setup lang="ts">
import { useRouter } from 'vue-router'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import LabelViewDialog from '@/views/baseVip/components/label-view-dialog.vue'
import BetterPageManage from '@/components/BetterPageManage/BetterPageManage.vue'
import QDropdownBtn from '@/components/q-btn/q-dropdown-btn.vue'
import VipIntegralDropdown from '@/q-plugin/integral/VipIntegralDropdown.vue'
import BalanceTopUpDialog from '@/views/baseVip/components/balance-top-up-dialog.vue'
import { ElMessage } from 'element-plus'
import { doGetBaseVipList } from '@/apis/vip'
import type { ApiBaseVipListItem, ParamsSearchVipBase, TopUpBalanceType } from '@/views/baseVip/types'
import useDisableUserHooks from '../hooks/useDisableUserHooks'

const $props = defineProps({
    searchFromChangeVal: {
        type: Boolean,
        default: true,
    },
    sortType: { type: String, default: '3' },
})
const $router = useRouter()
const { divTenThousand } = useConvert()
const pageConfig = reactive({
    size: 20,
    current: 1,
    total: 0,
})
const tableSelectedArr = ref<ApiBaseVipListItem[]>([])
const vipBaseList = ref<ApiBaseVipListItem[]>([])
// 标签
const labelViewData = reactive({
    labelView: false,
    currentUserIds: [] as string[],
    currentUserTagIds: [] as string[],
})
const changeType = ref<'BALANCE' | 'INTEGRAL' | 'GROWTHVALUE'>('BALANCE')
const { labelView, currentUserIds, currentUserTagIds } = toRefs(labelViewData)
// 余额充值
const topUpBalance = reactive<TopUpBalanceType>({
    isShowTopUpBalance: false,
    currentBalance: {
        balance: '',
        consumeCount: 0,
        createTime: '',
        dealTotalMoney: '',
        distributionCount: 0,
        id: '',
        integralTotal: '',
        remark: '',
        userHeadPortrait: '',
        userPhone: '',
        userNickname: '',
        userTagVOList: [{ tagId: '', tagName: '' }],
        userId: '',
    },
})
const { isShowTopUpBalance, currentBalance } = toRefs(topUpBalance)
const blackDialog = ref(false)
const currentUserId = ref<string[]>([])
const sortTypes = ref('')
const { blackDialogFormModel, handleBlackConfirm } = useDisableUserHooks()

onBeforeMount(() => {
    initBaseVipList()
})

function allChange(val: boolean) {
    vipBaseList.value.forEach((v) => (v.checked = val))
    tableSelectedArr.value = vipBaseList.value.filter((v) => v.checked)
}

async function initBaseVipList(params?: ParamsSearchVipBase) {
    const { data, code } = (await doGetBaseVipList({
        ...params,
        ...pageConfig,
        sortType: sortTypes.value,
    })) as any
    if (code !== 200) return ElMessage.error('获取会员信息失败')
    vipBaseList.value = data.records
    tableSelectedArr.value = []
    pageConfig.total = data.total
}

const handleOperation = (val: string, row: ApiBaseVipListItem) => {
    switch (val) {
        case 'Balance':
            changeType.value = 'BALANCE'
            currentBalance.value = row
            isShowTopUpBalance.value = true
            break
        case 'growthValue':
            changeType.value = 'GROWTHVALUE'
            currentBalance.value = row
            isShowTopUpBalance.value = true
            break
        case 'black':
            blackDialogFormModel.roles = []
            currentUserId.value = [row.userId]
            blackDialog.value = true
            break
        default:
            break
    }
}
// 分页
const handleSizeChange = (value: number) => {
    pageConfig.current = 1
    pageConfig.size = value
    initBaseVipList()
}
const handleCurrentChange = (value: number) => {
    pageConfig.current = value
    initBaseVipList()
}

const openLabelView = () => {
    if (!tableSelectedArr.value.length) {
        return ElMessage.info('请选择操作用户')
    }
    currentUserIds.value = tableSelectedArr.value.map((item) => item.userId)
    labelView.value = true
}
/**
 * 批量赠送
 */
const batchGiftsCoupons = () => {
    if (!tableSelectedArr.value.length) {
        return ElMessage.info('请选择操作用户')
    }
    const userIds = tableSelectedArr.value.map((item) => item.userId)
    $router.push({
        name: 'memberCouponBaseInfo',
        query: { ids: userIds },
    })
}

/**
 *  批量加入黑名单
 */
function batchBlack() {
    if (!tableSelectedArr.value.length) {
        return ElMessage.info('请选择操作用户')
    }
    blackDialogFormModel.roles = []
    currentUserId.value = tableSelectedArr.value.map((item) => item.userId)
    blackDialog.value = true
}
const handleCloseBlackDialog = () => {
    blackDialogFormModel.explain = ''
    blackDialogFormModel.roles = []
}

// 表格排序
const sortTableList = (label: string) => {
    const values1 = ['BALANCE_ASC', 'BALANCE_DESC', '']
    const values2 = ['REGISTER_TIME_DESC', 'REGISTER_TIME_ASC']
    const values3 = ['RECENT_CONSUME_TIME_DESC', 'RECENT_CONSUME_TIME_ASC']
    switch (label) {
        case '金额(元)':
            // 让sortTypes.value在'BALANCE_ASC','BALANCE_DESC'和''之间循环切换
            sortTypes.value = values1[(values1.indexOf(sortTypes.value) + 1) % values1.length]
            initBaseVipList()
            break
        case '注册时间':
            sortTypes.value = values2[(values2.indexOf(sortTypes.value) + 1) % values2.length]
            initBaseVipList()
            break
        case '最近消费时间':
            sortTypes.value = values3[(values3.indexOf(sortTypes.value) + 1) % values3.length]
            initBaseVipList()
            break
        default:
            break
    }
}

defineExpose({
    tableSelectedArr,
    vipBaseList,
    openLabelView,
    initBaseVipList,
    batchGiftsCoupons,
    batchBlack,
    pageConfig,
    allChange,
})
</script>
<template>
    <q-table
        v-model:checked-item="tableSelectedArr"
        :data="vipBaseList"
        class="base-vip-table"
        :selection="true"
        :class="{ 'base-vip-table-Up': !$props.searchFromChangeVal }"
        no-border
        @change-sort="sortTableList"
    >
        <q-table-column label="客户信息" width="270" fixed="left">
            <template #default="{ row }">
                <div class="customer-Infor">
                    <el-image class="customer-Infor__img" fit="cover" :src="row.userHeadPortrait" />
                    <div class="customer-nick-Infor">
                        <div class="ellipsis">
                            <span class="customer-nick-Infor__nickname"> {{ row.userNickname }}</span>
                            <span v-if="row.userPhone" class="customer-nick-Infor__phone">{{ row.userPhone }}</span>
                        </div>
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="会员等级" prop="memberType" align="left" width="170">
            <template #default="{ row }">
                <div style="font-size: 14px">
                    <span>{{ row.memberType === 'PAID_MEMBER' ? '付费会员' : '免费会员' }}</span>
                    <span>({{ row.memberType === 'PAID_MEMBER' ? 'SVIP' : 'VIP' }}{{ row.rankCode }})</span>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="储值余额(元)" prop="balance" align="left" width="120">
            <template #default="{ row }">
                <div style="font-size: 14px">{{ divTenThousand(row.balance).toFixed(2) }}</div>
            </template>
        </q-table-column>
        <q-table-column label="累计消费(元)" prop="dealTotalMoney" align="left" width="120">
            <template #default="{ row }">
                <div style="font-size: 14px">{{ divTenThousand(row.dealTotalMoney).toFixed(2) }}</div>
            </template>
        </q-table-column>
        <q-table-column label="最近消费时间" prop="lastDealTime" align="left" width="180">
            <template #default="{ row }">
                <div style="font-size: 14px">{{ row.lastDealTime }}</div>
            </template>
        </q-table-column>

        <q-table-column label="注册时间" prop="createTime" align="left" width="180">
            <template #default="{ row }">
                <div style="font-size: 14px">{{ row.createTime }}</div>
            </template>
        </q-table-column>
        <q-table-column prop="sex" label="操作" width="150" align="right" fixed="right">
            <template #default="{ row }">
                <q-dropdown-btn
                    title="查看"
                    :option="[
                        { label: '储值调整', name: 'Balance' },
                        { label: '成长值调整', name: 'growthValue' },
                        { label: '加入黑名单', name: 'black' },
                    ]"
                    :slot-index="1"
                    @right-click="handleOperation($event, row)"
                    @left-click="
                        $router.push({
                            name: 'vipDetailsIndex',
                            query: {
                                userId: row.userId,
                            },
                        })
                    "
                >
                    <vip-integral-dropdown :user-id="row.userId" />
                </q-dropdown-btn>
            </template>
        </q-table-column>
    </q-table>
    <el-row justify="space-between" align="middle">
        <slot name="batch">
            <span></span>
        </slot>
        <BetterPageManage
            :page-size="pageConfig.size"
            :page-num="pageConfig.current"
            :total="pageConfig.total"
            background
            @reload="initBaseVipList"
            @handle-size-change="handleSizeChange"
            @handle-current-change="handleCurrentChange"
        />
    </el-row>
    <!-- 黑名单弹窗 -->
    <el-dialog v-model="blackDialog" title="权限设置" @close="handleCloseBlackDialog">
        <el-form>
            <el-form-item label="拉黑原因">
                <el-input v-model="blackDialogFormModel.explain" placeholder="请输入拉黑原因" />
            </el-form-item>
            <el-form-item label="权限">
                <el-checkbox-group v-model="blackDialogFormModel.roles">
                    <el-checkbox label="FORBIDDEN_COMMENT">禁止评论</el-checkbox>
                    <el-checkbox label="FORBIDDEN_ORDER">禁止下单</el-checkbox>
                </el-checkbox-group>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="blackDialog = false">取消</el-button>
            <el-button
                type="primary"
                @click="
                    handleBlackConfirm(currentUserId, () => {
                        blackDialog = false
                        initBaseVipList()
                    })
                "
                >确定</el-button
            >
        </template>
    </el-dialog>
    <label-view-dialog v-model:label-view="labelView" :user-ids="currentUserIds" :current-user-tag-ids="currentUserTagIds" @reset="initBaseVipList" />
    <balance-top-up-dialog
        v-model:top-up-balance="isShowTopUpBalance"
        :base-vip-list-item="currentBalance"
        :change-type="changeType"
        @reset="initBaseVipList"
    />
</template>

<style scoped lang="scss">
@include b(base-vip-table) {
}

@include b(base-vip-table-top) {
    @include flex(flex-start);
    width: 100%;
    @include m(no) {
    }
    @include m(time) {
        padding: 0 20px;
    }
}

@include b(customer-Infor) {
    display: inline-block;
    height: 63px;
    @include flex(flex-start);
    @include e(img) {
        width: 52px;
        height: 52px;
        border-radius: 50%;
        flex-shrink: 0;
    }
}

@include b(customer-nick-Infor) {
    // height: 80px;
    @include flex(space-around, center);
    padding-left: 10px;
    font-size: 14px;
    // flex-direction: column;
    @include e(phone) {
        margin-top: 10px;
        display: block;
    }
    @include e(box) {
        padding: 0 15px;
    }
    @include e(tags) {
        display: inline-block;
        max-width: 100px;
        @include utils-ellipsis(3);
    }
    @include m(label) {
        cursor: pointer;
        &::after {
            content: '';
            display: inline-block;
            margin: 0 0 2px 2px;
            width: 0;
            height: 0;
            vertical-align: middle;
            border-top: 5px solid #000;
            border-left: 5px solid transparent;
            border-right: 5px solid transparent;
        }
    }
}

@include b(money-text) {
    @include e(value) {
        font-size: 1.3em;
        font-weight: 600;
    }
    @include e(red) {
        color: #fd0505;
    }
}
.ellipsis {
    width: 180px;
    @include utils-ellipsis;
}
</style>
