<template>
    <q-table v-model:checked-item="multipleSelection" :data="tableData" class="q-table">
        <template #header="{ row }">
            <div class="header">
                <div v-if="tabRadio !== 'storeReview'" class="header__content">
                    <span class="vertical_bar" style="margin-left: 16px">店铺ID：{{ row.no }}</span>
                    <text v-if="currentTabChoose === ''">
                        <span class="vertical_bar"
                            >店铺评分：<text style="color: red">{{ row.score }}</text></span
                        >
                        <span v-if="row.auditTime" class="vertical_bar">入驻时间：{{ row.auditTime.split(' ')[0] }}</span>
                        <span>管理员账号：{{ row.userMobile }}</span>
                    </text>
                    <span v-if="currentTabChoose !== ''" class="vertical_bar">申请时间：{{ row.createTime }}</span>
                    <span v-if="currentTabChoose === 'REJECT'" class="vertical_bar">审批时间：{{ row.auditTime }}</span>
                </div>
                <div v-else class="header__content">
                    <span class="vertical_bar" style="margin-left: 16px">店铺ID：{{ row.no }}</span>
                    <span class="vertical_bar">申请人：{{ row.userName }}({{ row.userMobile }})</span>
                    <span class="vertical_bar">申请时间：{{ row.createTime && row.createTime!.split(' ')[0] }}</span>
                    <span v-if="currentTabChoose !== 'UNDER_REVIEW' && row.extra" class="vertical_bar">
                        审核员：{{ row.extra.operatorName }}({{ row.extra.operatorPhone }})
                    </span>
                    <span v-if="currentTabChoose !== 'UNDER_REVIEW'" class="vertical_bar"
                        >审批时间：{{ row.auditTime && row.auditTime!.split(' ')[0] }}</span
                    >
                </div>
            </div>
        </template>
        <q-table-column label="店铺信息" width="350" align="left">
            <template #default="{ row }">
                <div class="shop">
                    <el-avatar
                        shape="square"
                        style="vertical-align: middle; margin: 0 10px 0 0; flex-shrink: 0"
                        :size="54"
                        :src="row.logo"
                        :icon="Shop"
                    />
                    <div class="shop__content">
                        <div :class="[oneLineDisplay(row.name) ? 'shop__content--twoLineDisplay' : 'shop__content--name']">
                            <q-tooltip v-if="row.name.length > 10" :content="row.name" maxWidth="210px" width="auto">
                                <span>{{ row.name }}</span>
                            </q-tooltip>
                            <span v-else>{{ row.name }}</span>
                        </div>
                        <div class="shop__content--phone">{{ row.contractNumber }}</div>
                    </div>
                </div>
            </template>
        </q-table-column>
        <q-table-column v-if="tabRadio === 'storeReview'" label="店铺地址" align="left" :width="'320'">
            <template #default="{ row }">
                <el-tooltip :content="row.address" placement="top">
                    <div>
                        <p class="address">{{ row.address.split('~')[0] }}</p>
                        <p>{{ row.address.split('~').slice(1).join('~') }}</p>
                    </div>
                </el-tooltip>
            </template>
        </q-table-column>

        <q-table-column v-if="tabRadio === 'storeReview'" label="状态" align="left" width="90">
            <template #default>
                <div>
                    <span v-if="currentTabChoose === 'UNDER_REVIEW'" style="color: #ff860b">待审核</span>
                    <span v-else-if="currentTabChoose === ''">已通过</span>
                    <span v-else style="color: #666">已拒绝</span>
                </div>
            </template>
        </q-table-column>
        <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'storeReview'" label="店铺余额(元)" align="left" width="150">
            <template #default="{ row }">
                <el-row class="row_balance" @click="handleNavToWithdraw">
                    <div class="col_row_balance" style="color: red">
                        <span style="font-size: 16px">{{ divTenThousand(row.shopBalance.undrawn).toFixed(2).split('.')[0] }}</span>
                        <span v-if="+divTenThousand(row.shopBalance.undrawn).toFixed(2) > 0"
                            >.{{ divTenThousand(row.shopBalance.undrawn).toFixed(2).split('.')[1] }}</span
                        >
                    </div>
                </el-row>
            </template>
        </q-table-column>
        <q-table-column label="经营模式" align="left" :width="100">
            <template #default="{ row }">
                <span> {{ shopModeMap[(row as ShopListVO).mode] }} </span>
            </template>
        </q-table-column>
        <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'storeReview'" label="店铺类型" align="left" width="100">
            <template #default="{ row }">
                <div>
                    <span v-if="row.shopType === 'SELF_OWNED'">自营店铺</span>
                    <span v-else-if="row.shopType === 'PREFERRED'">优选店铺</span>
                    <span v-else-if="row.shopType === 'ORDINARY'">普通店铺</span>
                </div>
            </template>
        </q-table-column>
        <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'storeReview'" label="提佣类型" align="left" width="110">
            <template #default="{ row }">
                <span>
                    {{
                        row.extractionType === 'CATEGORY_EXTRACTION'
                            ? '类目抽佣'
                            : row.extractionType === 'ORDER_SALES_EXTRACTION'
                            ? '订单金额提佣'
                            : ''
                    }}</span
                >
            </template>
        </q-table-column>
        <q-table-column v-if="currentTabChoose === '' && tabRadio !== 'storeReview'" prop="status" label="状态" width="80" align="left">
            <template #default="{ row }">
                <el-switch
                    v-if="row.status === 'FORBIDDEN' || row.status === 'NORMAL'"
                    v-model="row.status"
                    active-value="NORMAL"
                    inactive-value="FORBIDDEN"
                    inline-prompt
                    active-text="启用"
                    inactive-text="禁用"
                    style="width: 52px; height: 20px"
                    @change="changeStatus(row.id, row.status)"
                ></el-switch>
                <div v-show="row.status === 'REJECT'">
                    <span>已拒绝</span>
                </div>
            </template>
        </q-table-column>
        <q-table-column label="操作" align="right" width="180" fixed="right">
            <template #default="{ row }">
                <div class="flex_nowarp">
                    <el-link :underline="false" type="primary" class="mr-10" @click="previewShop(row)">查看</el-link>
                    <div v-show="tabRadio !== 'storeReview' && (row.status === 'FORBIDDEN' || row.status === 'NORMAL')" class="btn">
                        <el-link :underline="false" type="primary" @click="navToEdit(row, 'EDIT')">编辑</el-link>
                    </div>
                    <div v-show="row.status === 'UNDER_REVIEW'" class="flex_nowarp">
                        <el-link :underline="false" type="primary" class="mr-10" @click="shopAudit(row.id, true)">通过</el-link>
                        <el-link :underline="false" type="danger" @click="refuseHandle(row.id)">拒绝</el-link>
                    </div>
                    <div v-show="row.status === 'REJECT'" class="flex_nowarp">
                        <el-link :underline="false" type="primary" class="mr-10" @click="reAudit(row.id)">重新审核</el-link>
                        <el-tooltip :visible="row.rejectReason">
                            <template #content>
                                <div style="max-width: 440px">{{ rejectReason }}</div>
                            </template>
                            <el-button
                                type="primary"
                                link
                                :underline="false"
                                @mouseenter="getRejectReason(row)"
                                @mouseleave="row.rejectReason = false"
                            >
                                拒绝原因
                            </el-button>
                        </el-tooltip>
                    </div>
                </div>
            </template>
        </q-table-column>
    </q-table>
    <el-dialog v-model="dialogFormVisible" title="重新审核" width="500" center>
        <div>请确认是否将【已拒绝】状态，变更为待审核！！！</div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmReAudit"> 确定 </el-button>
            </div>
        </template>
    </el-dialog>
    <el-dialog v-model="refuseVisible" title="店铺审核" width="690">
        <div style="display: flex; align-items: center; padding: 10px 0">
            <div style="color: #666666; width: 85px">拒绝原因</div>
            <el-input v-model="refuse" show-word-limit type="text" maxlength="20" placeholder="请输入拒绝原因" />
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="refuseVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmRejection">确认</el-button>
            </div>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
import { Shop } from '@element-plus/icons-vue'
import type { Ref } from 'vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import storage from '@/utils/Storage'
import QTooltip from '@/components/q-tooltip/q-tooltip.vue'
import { doChangeStatus, doDelShop, doShopAudit, doReAudit, doGetShopRejectReason } from '@/apis/shops'
import { ShopMode } from '@/apis/overview'
import { ShopListVO } from '@/apis/shops/types/response'

// 映射
type ShopModeMap = {
    [key in ShopMode]: string
}
const shopModeMap: ShopModeMap = {
    [ShopMode.COMMON]: '线上模式',
    [ShopMode.SUPPLIER]: '供应商',
    [ShopMode.O2O]: 'O2O模式',
}

const parentTabChangeHandle = inject('parentTabChangeHandle') as (v: string) => void
const $emit = defineEmits(['getStoreStatus'])
const parentTabChoose = inject('parentTabChoose') as Ref
const $router = useRouter()
const { divTenThousand } = useConvert()

type Prop = { tableList: ShopListVO[]; currentTabChoose: string; tabRadio: string }
const $props = withDefaults(defineProps<Prop>(), {
    tableList: () => [],
    currentTabChoose: '',
    tabRadio: 'storeList',
})
const tableData = ref<ShopListVO[]>([])
const dialogFormVisible = ref(false)
const refuseVisible = ref(false)
const shopId = ref('')
const refuse = ref('')
const oneLineDisplay = (str: string) => {
    return /^\d+$/.test(str) || /^[a-zA-Z]+$/.test(str) || /^[a-zA-Z0-9]+$/.test(str)
}
watch(
    $props,
    (val) => {
        tableData.value = val.tableList.map((v: any) => {
            return {
                ...v,
                rejectReason: false,
            }
        })
    },
    {
        immediate: true,
        deep: true,
    },
)
const multipleSelection = ref<any[]>([])
/**
 * 店铺审核
 */
/**
 * 待审核列表的批量拒绝
 * @param {*} shopId
 * @param {*} isPass
 */
const shopAudit = (shopId: string, isPass: boolean) => {
    ElMessageBox.confirm(`店铺通过审核后将正常进行店铺运营，确定通过审核吗？`, `店铺审核`, {
        type: 'warning',
        center: true,
    }).then(() => {
        const query = { shopId: shopId, passFlag: isPass }
        doShopAudit(query).then((res) => {
            if (res.code !== 200) {
                ElMessage.error('更新')
                return
            }
            ElMessage.success('已更新')
            parentTabChangeHandle('UNDER_REVIEW')
        })
    })
}
const refuseHandle = (id: string) => {
    refuseVisible.value = true
    shopId.value = id
}
/**
 * 店铺审核拒绝
 * @param {*} shopId
 */
const confirmRejection = () => {
    const refuseTest = refuse.value
    if (refuseTest === '') {
        ElMessage.warning('请填写拒绝原因')
        return
    }
    const id = shopId.value
    const query = { shopId: id, passFlag: false, reasonForRejection: refuseTest }
    doShopAudit(query).then((res) => {
        if (res.code !== 200) {
            ElMessage.error('更新')
            return
        }
        ElMessage.success('已更新')
        parentTabChangeHandle('UNDER_REVIEW')
        refuseVisible.value = false
        refuse.value = ''
        $emit('getStoreStatus')
    })
}

/**
 * 店铺重新审核
 * @param {*} shopId
 */
const reAudit = (id: string) => {
    dialogFormVisible.value = true
    shopId.value = id
}
/**
 * 获取拒绝原因
 */
const rejectReason = ref('')
const getRejectReason = async (row: any) => {
    if (row.rejectReason && rejectReason) {
        return
    }
    const { data, code } = await doGetShopRejectReason(row.id)
    if (data && code === 200) {
        rejectReason.value = data
        row.rejectReason = true
    }
}
const confirmReAudit = () => {
    const id = shopId.value
    const query = { shopId: id }
    doReAudit(query).then((res) => {
        if (res.code !== 200) {
            ElMessage.error('更新')
            return
        }
        ElMessage.success('已更新')
        parentTabChangeHandle('REJECT')
        dialogFormVisible.value = false
        $emit('getStoreStatus')
    })
}

/**
 * 单一状态切换
 * @param {string} shopsId
 * @param {string} status
 */
const changeStatus = (shopsId: string, status: string) => {
    const toBoolean = status === 'NORMAL'
    doChangeStatus([shopsId], toBoolean)
}
/**
 * 店铺列表单个删除
 * @param {string} shopsId
 */
const deleteShop = async (
    row: {
        shopBalance: {
            total: string
            uncompleted: string
            undrawn: string
        }
        id: string
    },
    type?: string,
) => {
    ElMessageBox.confirm('确定要删除该店铺吗，此操作不可逆?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(async () => {
            const uncompleted = divTenThousand(row.shopBalance.uncompleted).toNumber()
            const undrawn = divTenThousand(row.shopBalance.undrawn).toNumber()
            if (uncompleted || undrawn) {
                ElMessage.warning({
                    message: '该店铺存在未（提现/结算）金额，禁止删除',
                    duration: 4000,
                })
                return
            }
            const { code, msg } = await doDelShop([row.id])
            if (code === 200) {
                ElMessage({
                    type: 'success',
                    message: '删除成功',
                })
                parentTabChangeHandle(type ? type : '')
                return
            }
            ElMessage({
                type: 'error',
                message: msg,
            })
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '删除失败',
            })
        })
}
/**
 * （店铺列表||已关闭）批量删除
 */
const batchDeleteShop = () => {
    if (!multipleSelection.value.length) {
        return ElMessage.error('请勾选列表')
    }
    ElMessageBox.confirm('确定要删除该店铺吗，此操作不可逆?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    })
        .then(async () => {
            const tempArr = multipleSelection.value.map((item) => item.id)
            const { code, success } = await doDelShop(tempArr)
            if (code === 200 && success) {
                parentTabChangeHandle(parentTabChoose.value !== 'REJECT' ? ' ' : 'REJECT')
                ElMessage({
                    type: 'success',
                    message: '删除成功',
                })
            }
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '删除失败',
            })
        })
}
/**
 * 批量启用禁用方法
 * @param {boolean} status NORMAL启用 FORBIDDEN 禁用
 */

const batchChangeStatus = async (status: string) => {
    const toBoolean = status === 'NORMAL'
    if (!checkVaild(status)) {
        ElMessage.error('请勾选列表')
        return false
    }
    const tempArr = multipleSelection.value.map((item) => {
        return item.id
    })
    if (status === 'refusedTo') {
        // 批量拒绝
        return shopAudit(tempArr.join(','), false)
    }
    const { code, success } = await doChangeStatus(tempArr, toBoolean)
    if (code === 200 && success === true) {
        ElMessage.success('操作成功')
    }
    return true
}
/**
 * 跳转至对账单
 */
const handleNavToWithdraw = () => {}
/**
 * 判断选中row是否符合批量操作
 * @param {boolean} status NORMAL启用 FORBIDDEN 禁用
 * @returns {boolean}
 */
function checkVaild(status: string): boolean {
    let flag = true
    if (!multipleSelection.value.length) {
        flag = false
    }
    return flag
}
/**
 * 待审核列表的通过
 * @param {*} rows
 * @param {*} type
 */
const navToEdit = (rows: any, type: 'EDIT' | 'through') => {
    new storage().setItem('SHOPITEM', rows, 60 * 60 * 2)
    $router.push({
        path: '/shopList/editShop',
        query: {
            shopId: rows.id,
            type,
        },
    })
}

const previewShop = (rows: any) => {
    new storage().setItem('SHOPITEM', rows, 60 * 60 * 2)
    $router.push({
        path: '/shopList/previewShop',
        query: { shopId: rows.id, tabRadio: $props.tabRadio },
    })
}

defineExpose({
    changeStatus,
    deleteShop,
    batchChangeStatus,
    batchDeleteShop,
})
</script>
<style lang="scss" scope>
.flex_nowarp {
    display: flex;
    flex-wrap: nowrap;
    .el-link {
        flex-shrink: 0;
    }
}
.el-message-box--center {
    width: 480px;
}
.el-message-box--center .el-message-box__title {
    display: flex;
    justify-content: flex-start;
}
.el-message-box--center .el-message-box__container {
    justify-content: flex-start;
}
.el-message-box--center .el-message-box__btns {
    justify-content: flex-end;
}

.row_balance {
    display: flex;
    width: 100%;
    flex-direction: column;
    align-items: left;
    .col_row_balance + .col_row_balance {
        margin-top: 5px;
    }
}
@include b(address) {
    font-size: 15px;
    font-weight: bold;
}
@include b(header) {
    display: inline-block;
    width: 100%;
    color: #333333;
    height: 46px;
    line-height: 46px;
    border-radius: 10px 10px 0 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
}
@include b(mr-10) {
    margin-right: 10px;
}
.vertical_bar::after {
    position: relative;
    content: '|';
    margin: 0 10px;
}
.vertical_bar:last-child::after {
    position: relative;
    content: '';
}
@include b(shop) {
    height: 68px;
    // display: flex;
    // align-items: center;
    // justify-content: flex-start;
    width: 100%;
    display: flex;
    align-items: center;
    @include e(title) {
        width: 130px;
        margin-left: 10px;
        @include utils-ellipsis;
    }
    @include e(content) {
        height: 60px;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        flex: 1;
        overflow: hidden;
        @include m(name) {
            align-items: center;
            font-weight: bold;
            font-size: 14px;
            color: #333;
            width: 150px;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            overflow: hidden;
        }
        @include m(twoLineDisplay) {
            font-weight: bold;
            font-size: 14px;
            color: #333;
            width: 150px;
        }
        @include m(phone) {
            font-size: 14px;
            color: #999999;
        }
    }
    @include b(tag) {
        @include e(autarky) {
            flex-shrink: 0;
            margin-right: 5px;
            padding: 0 5px;
            background-color: #fd0505;
            color: #fff;
            border-radius: 4px;
            font-size: 0.7em;
        }
        @include e(optimize) {
            flex-shrink: 0;
            margin-right: 5px;
            padding: 0 5px;
            background-color: #7728f5;
            color: #fff;
            border-radius: 4px;
            font-size: 0.8em;
        }
    }
}
@include b(btn) {
    color: #2e99f3;
    cursor: pointer;
}
@include b(q-table) {
    overflow: auto;
    transition: height 0.5s;
}
</style>
