<script lang="ts" setup>
import { doGetShopMemberList, doGetUserTags, doPostUserTag } from '@/apis/member'
import Search from './components/search.vue'
import PageManage from '@/components/PageManage.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import { useRouter } from 'vue-router'
import SetLabels from './components/SetLabels.vue'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { compareTags, getInitialLabelsForMember, type TagsInterface } from './helper'
import type { Ref } from 'vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const multiSelect = ref([])
const tableData = ref([])
const searchParams = ref({
    userNickname: '',
    userPhone: '',
    birthdayStartTime: '',
    birthdayEndTime: '',
    memberType: '',
    rankCode: '',
    tagId: '',
})

const pagination = reactive({
    page: { size: 10, current: 1 },
    total: 0,
})

let currentMemberIds: any[] = []
const showSetLabelDialog = ref(false)
const shopTags = ref([])
const initialLabels: Ref<TagsInterface[]> = ref([])

const initPageData = async () => {
    const { data } = await doGetShopMemberList({ ...searchParams.value, ...pagination.page })
    tableData.value = data?.records || []
    pagination.total = data?.total || 0
}
initPageData()

/**
 * 表格排序
 */
let sortEnum = ref('')
const sortTableList = (label: string) => {
    switch (label) {
        case '本店消费(元)':
            sortEnum.value = sortEnum.value === '' ? '' : ''
            initPageData()
            break
        case '最近消费时间':
            sortEnum.value = sortEnum.value === '' ? '' : ''
            initPageData()
            break
        default:
            break
    }
}
const handleSearchParams = (searchOptions: any) => {
    searchParams.value = searchOptions
    initPageData()
}
const handleOperation = (operationName: string, row: any) => {
    const shopStore = useShopInfoStore()
    switch (operationName) {
        case 'setTags':
            currentMemberIds = [row.userId]
            doGetUserTags(shopStore.shopInfo.id).then(({ data, msg }) => {
                initialLabels.value = getInitialLabelsForMember(row?.tags, data || [])
                showSetLabelDialog.value = true
            })
            break
        case 'coupon':
            currentMemberIds = [row.userId]
            router.push({ path: '/member/list/coupon', query: { userIds: JSON.stringify(currentMemberIds) } })
            break
        default:
            break
    }
}
const handlePreviewMemberInfo = (row: any) => {
    router.push({
        path: '/member/list/details',
        query: { id: row.userId },
    })
}
const getShopTags = async () => {
    const shopStore = useShopInfoStore()
    const { success, data } = await doGetUserTags(shopStore.shopInfo.id, true)
    if (success) {
        shopTags.value = data || []
    }
}
const handleCloseSetLabelDialog = () => {
    initialLabels.value = []
    showSetLabelDialog.value = false
}
const setLabelFromGlobal = () => {
    if (multiSelect.value.length === 0) return ElMessage.error({ message: '请先选择用户后设置标签' })
    currentMemberIds = multiSelect.value?.map((item: any) => item.userId)
    console.log(currentMemberIds)
    initialLabels.value = getInitialLabelsForMember([], shopTags.value)
    showSetLabelDialog.value = true
}
const handleConfirmSetLabel = async () => {
    const result = compareTags(shopTags.value, initialLabels.value)
    const { success, msg } = await doPostUserTag({ ...result, userIdList: currentMemberIds })
    if (success) {
        ElMessage.success({ message: msg || '设置成功' })
        handleCloseSetLabelDialog()
        getShopTags()
        initPageData()
    } else {
        ElMessage.error({ message: msg || '设置失败' })
    }
}
const handlePresentCouponFromGlobal = () => {
    if (multiSelect.value.length === 0) return ElMessage.error({ message: '请先选择用户后赠送优惠券' })
    currentMemberIds = multiSelect.value?.map((item: any) => item.userId)
    router.push({ path: '/member/list/coupon', query: { userIds: JSON.stringify(currentMemberIds) } })
}
onMounted(() => {
    currentMemberIds = []
    getShopTags()
})
const handleSizeChange = (value: number) => {
    pagination.page.size = value
    initPageData()
}
const handleCurrentChange = (value: number) => {
    pagination.page.current = value
    initPageData()
}
</script>
<template>
    <el-config-provider :empty-values="[undefined, null]">
        <Search :shopTags="shopTags" @on-search-params="handleSearchParams" />
    </el-config-provider>
    <div class="grey_bar"></div>
    <div class="handle_container" style="padding-top: 16px">
        <el-button style="color: #555cfd; border-color: #555cfd" @click="handlePresentCouponFromGlobal">送优惠券</el-button>
        <el-button @click="setLabelFromGlobal">设置标签</el-button>
    </div>
    <QTable v-model:checkedItem="multiSelect" :data="tableData" :selection="true" no-border class="table" @change-sort="sortTableList">
        <QTableColumn label="客户信息" width="300">
            <template #default="{ row }">
                <div class="customer-Infor">
                    <el-image
                        class="customer-Infor__img"
                        fit="cover"
                        style="width: 52px; height: 52px; margin-right: 10px"
                        :src="row.userHeadPortrait"
                    />
                    <div class="customer-nick-Infor">
                        <div class="ellipsis">
                            <span> {{ row.userNickname }}</span>
                            <br />
                            <span v-if="row.userPhone" class="customer-nick-Infor__phone">{{ row.userPhone }}</span>
                        </div>
                    </div>
                </div>
            </template>
        </QTableColumn>

        <QTableColumn label="会员信息" width="140">
            <template #default="{ row }">
                <span>{{ row.memberType === 'PAID_MEMBER' ? '付费' : '免费' }}</span>
                <span>（{{ row.memberType === 'PAID_MEMBER' ? 'SVIP' : 'VIP' }}{{ row.rankCode }}）</span>
            </template>
        </QTableColumn>
        <QTableColumn label="本店消费(元)" prop="shopConsumption" width="140">
            <template #default="{ row }">
                {{ (row?.shopConsumption / 10000).toFixed(2) }}
            </template>
        </QTableColumn>
        <QTableColumn label="最近消费时间" prop="registerTime" width="200">
            <template #default="{ row }">
                {{ row.registerTime }}
            </template>
        </QTableColumn>
        <QTableColumn label="标签" width="270">
            <template #default="{ row }">
                <div style="display: flex; flex-wrap: wrap">
                    <div v-for="(item, index) in row.tags.slice(0, 3)" :key="index">
                        <div class="tagName">{{ item.tagName }}</div>
                    </div>
                </div>
            </template>
        </QTableColumn>
        <QTableColumn prop="sex" label="操作" align="right" fixed="right" width="100">
            <template #default="{ row }">
                <q-dropdown-btn
                    title="查看"
                    :option="[
                        { label: '送优惠券', name: 'coupon' },
                        { label: '设置标签', name: 'setTags' },
                    ]"
                    @right-click="handleOperation($event, row)"
                    @left-click="handlePreviewMemberInfo(row)"
                />
            </template>
        </QTableColumn>
    </QTable>
    <!-- 好用的分页器 -->
    <PageManage
        :page-size="pagination.page.size"
        :total="pagination.total"
        :page-num="pagination.page.current"
        @handle-size-change="handleSizeChange"
        @handle-current-change="handleCurrentChange"
    />
    <el-dialog v-model="showSetLabelDialog" title="设置标签" destroy-on-close>
        <SetLabels v-model:initial-labels="initialLabels" />
        <template #footer>
            <el-button @click="handleCloseSetLabelDialog">取 消</el-button>
            <el-button type="primary" @click="handleConfirmSetLabel">确 定</el-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
* {
    font-size: 14px;
}
@include b(customer-Infor) {
    width: 300px;
    height: 80px;
    @include flex(flex-start);
    @include e(img) {
        width: 40px;
        height: 40px;
        border-radius: 10px;
    }
}
@include b(table) {
    overflow-y: auto;
}
@include b(customer-nick-Infor) {
    color: #333;
    @include e(phone) {
        margin-top: 10px;
        display: block;
    }
}
.ellipsis {
    width: 149px;
    @include utils-ellipsis;
}
.tagName {
    max-width: 82px;
    @include utils-ellipsis;
    padding: 4px 6px;
    background-color: rgba(85, 92, 253, 0.07);
    margin-right: 5px;
    border-radius: 3px;
    color: #555cfd;
}
</style>
